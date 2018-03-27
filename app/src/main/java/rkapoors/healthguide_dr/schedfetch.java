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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Set;

public class schedfetch extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference, patientchkref;

    AutoCompleteTextView mail;
    private SharedPreferences patients;
    private Set<String> history;

    String patientmail="",uidofuser="",patientuid="";

    int flg=0;

    Button sbt;

    TextView date, fast, rec, mx, ms, as, ex, ns, bchk;

    RelativeLayout fact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedfetch);

        setTitle("schedule");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {uidofuser = user.getUid();}

        patientchkref = databaseReference.child("doctors").child(uidofuser).child("patients");

        fact = (RelativeLayout)findViewById(R.id.shedfetchact);

        date = (TextView)findViewById(R.id.dateresp);
        fast = (TextView)findViewById(R.id.fastingresp);
        rec = (TextView)findViewById(R.id.recordresp);
        mx = (TextView)findViewById(R.id.morningexcresp);
        ms = (TextView)findViewById(R.id.morninginsresp);
        as = (TextView)findViewById(R.id.afterinsresp);
        ex = (TextView)findViewById(R.id.eveningexcresp);
        ns = (TextView)findViewById(R.id.nightinsresp);
        bchk = (TextView)findViewById(R.id.bedchkresp);

        mail=(AutoCompleteTextView)findViewById(R.id.email);
        String tp = getIntent().getStringExtra("mailid");
        if(tp != null) mail.setText(tp);

        patients=getSharedPreferences("patientpref",0);
        history = new HashSet<String>(patients.getStringSet("patientkey", new HashSet<String>()));     //key, default value
        setautocompletesource();

        sbt = (Button)findViewById(R.id.chkbt);
        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fact.getWindowToken(), 0);

                flg = 0;

                patientmail = mail.getText().toString().trim();
                if(TextUtils.isEmpty(patientmail)) {
                    Snackbar.make(fact,"Enter patient's email.",Snackbar.LENGTH_LONG).show();
                    return ;
                }

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if(!isConnected)
                {
                    Snackbar snackbar=Snackbar.make(fact, "Check Internet Connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
                else{
                    flg=0;
                    fetchrecord task = new fetchrecord(schedfetch.this);
                    task.execute();
                }
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(schedfetch activity){
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

                        databaseReference.child("users").child(patientuid).child("schedule").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String chkstr = dataSnapshot.child("date").getValue(String.class);
                                if(chkstr!=null){
                                    date.setText(chkstr);
                                    fast.setText(dataSnapshot.child("fastingcheck").getValue(String.class));
                                    rec.setText(dataSnapshot.child("record").getValue(String.class));
                                    mx.setText(dataSnapshot.child("morningexc").getValue(String.class));
                                    ms.setText(dataSnapshot.child("morninginsulin").getValue(String.class));
                                    as.setText(dataSnapshot.child("nooninsulin").getValue(String.class));
                                    ex.setText(dataSnapshot.child("eveningexc").getValue(String.class));
                                    ns.setText(dataSnapshot.child("nightinsulin").getValue(String.class));
                                    bchk.setText(dataSnapshot.child("bedtimecheck").getValue(String.class));
                                }
                                else {
                                    date.setText("Schedule not recorded !");
                                    fast.setText("-");
                                    rec.setText("-");
                                    mx.setText("-");
                                    ms.setText("-");
                                    as.setText("-");
                                    ex.setText("-");
                                    ns.setText("-");
                                    bchk.setText("-");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                    else
                        Snackbar.make(fact,"Patient has not authorized you.",Snackbar.LENGTH_LONG).show();
                }
            },8000);    //show for atlest 500 msec
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
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(schedfetch.this,android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
        mail.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
