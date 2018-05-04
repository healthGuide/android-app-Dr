package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class notification extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference, patientchkref;

    AutoCompleteTextView mail;
    private SharedPreferences patients;
    private Set<String> history;

    String patientmail="",uidofuser="",patientuid="", suggestmsg="", dt="", readid="";

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    private SimpleDateFormat dateFormatter;

    int flg=0;

    Button sbt;
    EditText msg;
    RelativeLayout notifact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        setTitle("suggest");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {uidofuser = user.getUid();}

        patientchkref = databaseReference.child("doctors").child(uidofuser).child("patients");

        notifact = (RelativeLayout)findViewById(R.id.layout);
        sbt = (Button)findViewById(R.id.sendbt);
        msg = (EditText)findViewById(R.id.editText);

        mail=(AutoCompleteTextView)findViewById(R.id.email);
        String tp = getIntent().getStringExtra("mailid");
        if(tp != null) mail.setText(tp);
        patients=getSharedPreferences("patientpref",0);
        history = new HashSet<String>(patients.getStringSet("patientkey", new HashSet<String>()));     //key, default value
        setautocompletesource();

        Calendar c=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);
        dt=dateFormatter.format(c.getTime());

        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(notifact.getWindowToken(), 0);

                flg = 0;

                patientmail = mail.getText().toString().trim();
                if(TextUtils.isEmpty(patientmail)) {
                    Snackbar.make(notifact,"Enter patient's email.",Snackbar.LENGTH_LONG).show();
                    return ;
                }

                suggestmsg = msg.getText().toString().trim();
                if(TextUtils.isEmpty(suggestmsg)){
                    Snackbar.make(notifact,"Enter message to be sent.",Snackbar.LENGTH_LONG).show();
                    return ;
                }

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(!isConnected)
                {
                    Snackbar snackbar=Snackbar.make(notifact, "Check Internet Connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                else {
                    flg = 0;
                    fetchrecord task = new fetchrecord(notification.this);
                    task.execute();
                }
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void>{
        private ProgressDialog pd;

        public fetchrecord(notification activity){
            pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute(){
            pd.setMessage("Please wait a moment...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                patientchkref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.child("email").getValue(String.class).equals(patientmail)){
                                patientuid = ds.child("uid").getValue(String.class);
                                flg = 1;
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Snackbar.make(notifact,"Connection Lost. Try Again.",Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(flg==1){
                        DatabaseReference temp = databaseReference.child("users").child(patientuid).child("notifs").child(dt);
                        readid=temp.push().getKey();
                        temp.child(readid).child("msg").setValue(suggestmsg);
                        sbt.setEnabled(false);
                        sbt.setTextColor(Color.parseColor("#A9A9A9"));
                        Snackbar.make(notifact,"Message sent successfully.",Snackbar.LENGTH_LONG).show();
                    }
                    else{
                        Snackbar.make(notifact,"Patient has not authorized you.",Snackbar.LENGTH_LONG).show();
                    }
                    pd.dismiss();
                }
            },5000);
        }
    }

    private void setautocompletesource()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(notification.this,android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
        mail.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
