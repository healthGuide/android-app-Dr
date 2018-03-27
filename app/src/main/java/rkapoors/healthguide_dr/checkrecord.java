package rkapoors.healthguide_dr;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
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

public class checkrecord extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    public TextView fdt;
    public TextView tdt;

    int flg;

    AutoCompleteTextView mail;
    private SharedPreferences patients;
    private Set<String> history;

    String patientuid="",uidofuser="",mailofuser="", patientmail="";

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkrecord);

        setTitle("Records");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {uidofuser = user.getUid();mailofuser=user.getEmail();}

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        Calendar c = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);

        String datetoshow=dateFormatter.format(c.getTime());

        relativeLayout=(RelativeLayout)findViewById(R.id.content);

        mail=(AutoCompleteTextView)findViewById(R.id.email);
        String tp = getIntent().getStringExtra("mailid");
        if(tp != null) mail.setText(tp);

        patients=getSharedPreferences("patientpref",0);
        history = new HashSet<String>(patients.getStringSet("patientkey", new HashSet<String>()));     //key, default value
        setautocompletesource();

        mail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER))
                {
                    addsearchinput(mail.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        fdt=(TextView)findViewById(R.id.fromdt);
        fdt.setText(datetoshow);

        tdt=(TextView)findViewById(R.id.todt);
        tdt.setText(datetoshow);

        TextView fbt=(TextView)findViewById(R.id.frombt);
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                datePickerDialog = new DatePickerDialog(checkrecord.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        fdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        TextView tbt=(TextView)findViewById(R.id.tobt);
        tbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar newCalendar = Calendar.getInstance();
                dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                datePickerDialog = new DatePickerDialog(checkrecord.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        tdt.setText(dateFormatter.format(newDate.getTime()));
                    }
                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });
        Button chk=(Button)findViewById(R.id.chk);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flg=0;
                patientmail = mail.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relativeLayout.getWindowToken(), 0);

                if (TextUtils.isEmpty(mail.getText().toString())) {
                    Snackbar.make(relativeLayout, "Enter patient's email", Snackbar.LENGTH_LONG).show();
                    return;
                }

                ConnectivityManager cm = (ConnectivityManager) checkrecord.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(!isConnected)
                {
                    Snackbar snackbar=Snackbar.make(relativeLayout, "Check Internet Connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                else{
                    fetchrecord task = new fetchrecord(checkrecord.this);
                    task.execute();
                }
            }
        });
    }


    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(checkrecord activity){
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
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();

                    if(flg==1)
                    {
                    Intent chka = new Intent(checkrecord.this, recorddata.class);
                    history.add(patientmail);
                    chka.putExtra("fromdate", fdt.getText().toString());
                    chka.putExtra("todate", tdt.getText().toString());
                    chka.putExtra("patient",patientmail);
                    chka.putExtra("patientuid",patientuid);
                    startActivity(chka);
                    }
                    else
                        Snackbar.make(relativeLayout,"Patient has not authorized you.",Snackbar.LENGTH_LONG).show();
                }
            },5000);    //show for atlest 500 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                databaseReference.child("doctors").child(uidofuser).child("patients").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if(ds.child("email").getValue(String.class).equals(patientmail)){
                                flg=1;
                                patientuid = ds.child("uid").getValue(String.class);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setautocompletesource()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(checkrecord.this,android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
        mail.setAdapter(adapter);
    }
    private void addsearchinput(String input)
    {
        if(!history.contains(input))
        {
            history.add(input);
            setautocompletesource();
        }
    }
    private void saveprefs()
    {
        patients=getSharedPreferences("patientpref",0);                 //name of sharedPreference object, mode 0 : accessible by app
        SharedPreferences.Editor editor=patients.edit();
        editor.putStringSet("patientkey",history);
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        saveprefs();
    }
}
