package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    int flg=0;

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

        rldbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flg=0;
                fetchrecord task = new fetchrecord(people.this);
                task.execute();
            }
        });

        fetchrecord task = new fetchrecord(people.this);
        task.execute();

        ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String dummystr = ls.getItemAtPosition(pos).toString();
                String[] words=dummystr.split("\\n");    //splits the string based on string
                final String patientkimail = words[1];

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.promptspatient, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final RadioGroup rg = (RadioGroup)promptsView.findViewById(R.id.chkrg);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                        RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                        if (null != rb && checkedId > -1) {
                            if(rb.getText().toString().equals("Records")) {
                                Intent chkact = new Intent(people.this, checkrecord.class);
                                chkact.putExtra("mailid",patientkimail);
                                startActivity(chkact);
                            }
                            else if(rb.getText().toString().equals("Schedule")){
                                Intent schedact = new Intent(people.this, schedfetch.class);
                                schedact.putExtra("mailid",patientkimail);
                                startActivity(schedact);
                            }
                            else{
                                Intent sugact = new Intent(people.this, notification.class);
                                sugact.putExtra("mailid",patientkimail);
                                startActivity(sugact);
                            }
                        }
                    }
                });

                // set dialog message
                alertDialogBuilder
                        .setCancelable(true)
                        .setPositiveButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        });
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
                    if(flg==1) {
                        adapter=new ArrayAdapter<>(people.this,android.R.layout.simple_list_item_1,patientlist);
                        ls.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else Snackbar.make(relativeLayout,"Something went wrong. Try again.",Snackbar.LENGTH_LONG).show();
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
                        patientlist = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            patientlist.add(ds.child("name").getValue(String.class)+"\n"+ds.child("email").getValue(String.class));
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
