package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class people extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference databaseReference,temp;

    String uidofuser="";

    ArrayList<String> patientlist;
    ArrayAdapter<String> adapter;

    final Context context = this;
    RelativeLayout relativeLayout;
    Button rldbutton;
    ListView ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        relativeLayout = (RelativeLayout)findViewById(R.id.pplview);
        rldbutton = (Button)findViewById(R.id.rfbt);
        ls=(ListView)findViewById(R.id.ppllist);

        setTitle("People");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {uidofuser = user.getUid();}

        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();

        temp=databaseReference.child("doctors").child(uidofuser).child("patients");

        patientlist = new ArrayList<>();
        adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,patientlist);
        ls.setAdapter(adapter);

        rldbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientlist = new ArrayList<>();
                fetchrecord task = new fetchrecord(people.this);
                task.execute();
            }
        });

        fetchrecord task = new fetchrecord(people.this);
        task.execute();

    }

    private class fetchrecord extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public fetchrecord(people activity){
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
                    adapter.notifyDataSetChanged();
                    pd.dismiss();
                }
            },5000);    //show for atlest 500 msec
        }

        @Override
        protected Void doInBackground(Void... params){
            try{
                temp.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            patientlist.add(ds.child("name").getValue(String.class)+"\n"+ds.child("email").getValue(String.class));
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
