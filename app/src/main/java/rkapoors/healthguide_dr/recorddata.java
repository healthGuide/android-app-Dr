package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class recorddata extends AppCompatActivity {

    List<checkrecorddata> list;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    RecyclerView recycle;
    Button ftbt;
    TextView frdt,todt,mailtv;
    RelativeLayout relativeLayout;

    String uidofpatient="", prevdt="";

    Date fromtithi, totithi, firebasetithi, prevdate;
    DateFormat df;
    SimpleDateFormat dateFormatter;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorddata);

        setTitle("Records");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recycle = (RecyclerView) findViewById(R.id.cardView);
        ftbt = (Button)findViewById(R.id.fetchbt);
        frdt = (TextView)findViewById(R.id.fttv);
        todt = (TextView)findViewById(R.id.tttv);
        mailtv = (TextView)findViewById(R.id.mail);
        relativeLayout = (RelativeLayout)findViewById(R.id.rellayout);

        frdt.setText(getIntent().getStringExtra("fromdate"));
        todt.setText(getIntent().getStringExtra("todate"));
        mailtv.setText(getIntent().getStringExtra("patient"));
        uidofpatient = getIntent().getStringExtra("patientuid");

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        cal.add(Calendar.DATE,-61);              //go back 61 days
        prevdt = dateFormatter.format(cal.getTime());

        df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            fromtithi = df.parse(frdt.getText().toString());
            totithi = df.parse(todt.getText().toString());
            prevdate = df.parse(prevdt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        fetchrecord task = new fetchrecord(recorddata.this);
        task.execute();

        ftbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=0;
                fetchrecord task = new fetchrecord(recorddata.this);
                task.execute();
            }
        });

        FloatingActionButton grbt = (FloatingActionButton)findViewById(R.id.grbt);
        grbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gract = new Intent(recorddata.this,grview.class);
                gract.putExtra("patientuid",uidofpatient);
                startActivity(gract);
            }
        });
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(recorddata activity){
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

                    if(flag==1){
                        recycleadapter recycadp = new recycleadapter(list,recorddata.this);
                        recycadp.notifyDataSetChanged();
                        RecyclerView.LayoutManager recyclayout = new LinearLayoutManager(recorddata.this);
                        recycle.setLayoutManager(recyclayout);
                        recycle.setItemAnimator( new DefaultItemAnimator());
                        recycle.setAdapter(recycadp);
                    }
                    else{
                        Snackbar.make(relativeLayout,"Check Connection or Constraints.",Snackbar.LENGTH_LONG).show();
                    }
                }
            },5000);    //show for atlest 500 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                databaseReference.child("users").child(uidofpatient).child("records").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        list = new ArrayList<checkrecorddata>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String tithi = ds.getKey();
                            try {
                                firebasetithi = df.parse(tithi);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(prevdate.compareTo(firebasetithi)>=0){
                                ds.getRef().setValue(null);
                            }
                            else if(firebasetithi.compareTo(fromtithi)>=0 && firebasetithi.compareTo(totithi)<=0) {
                                for (DataSnapshot dts : ds.getChildren()) {
                                    checkrecorddata value = dts.getValue(checkrecorddata.class);
                                    checkrecorddata temp = new checkrecorddata();
                                    String samay = value.gettime();
                                    String kab = value.getcomment();
                                    String kitnamed = value.getdosage();
                                    String kitnival = value.getvalue();
                                    temp.setdt(tithi);
                                    temp.settime(samay);
                                    temp.setcomment(kab);
                                    temp.setdosage(kitnamed);
                                    temp.setvalue(kitnival);
                                    list.add(temp);
                                    flag = 1;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Failed to read value
                        Snackbar.make(relativeLayout,"Connection Lost. Try Again.",Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
