package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.Iterator;

public class grview extends AppCompatActivity {

    ValueLineChart mCubicValueLineChart;
    ValueLineSeries series;

    FirebaseDatabase database;
    DatabaseReference databaseReference,recref;
    Query recquery;

    ArrayList<String> tithi,val;

    int flg=0;
    String patientuid="";

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grview);

        setTitle("Records");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        linearLayout = (LinearLayout)findViewById(R.id.graphview);
        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        patientuid = getIntent().getStringExtra("patientuid");

        tithi = new ArrayList<String>();
        val = new ArrayList<String>();

        database= FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        recref = databaseReference.child("users").child(patientuid).child("records");
        recquery = recref.limitToLast(8);              //last 8 days

        series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        fetchrecord task = new fetchrecord(grview.this);
        task.execute();
    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(grview activity){
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
                    if(flg==1)
                    {
                        Iterator tithiitr = tithi.iterator();
                        Iterator valitr = val.iterator();

                        while(tithiitr.hasNext() && valitr.hasNext())
                        {
                            Float tp = Float.parseFloat(valitr.next()+"");
                            series.addPoint(new ValueLinePoint(tithiitr.next()+"",tp));
                        }

                    mCubicValueLineChart.addSeries(series);
                    mCubicValueLineChart.startAnimation();
                    }
                    else Snackbar.make(linearLayout,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();
                    pd.dismiss();
                }
            },5000);    //show for atlest 5000 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                recquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            for(DataSnapshot dts : ds.getChildren())
                            {
                                tithi.add(ds.getKey());
                                val.add(dts.child("value").getValue(String.class));
                            }
                        }

                        flg=1;
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
