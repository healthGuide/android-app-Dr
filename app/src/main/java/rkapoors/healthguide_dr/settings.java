package rkapoors.healthguide_dr;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class settings extends AppCompatActivity {

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    RelativeLayout relativeLayout;

    String mailofuser="";
    String uidofuser="";

    TextView mailtv,nametv;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        relativeLayout = (RelativeLayout)findViewById(R.id.settingsview);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {mailofuser=user.getEmail();uidofuser=user.getUid();}

        setTitle("Settings");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mailtv = (TextView)findViewById(R.id.usermail);
        nametv = (TextView)findViewById(R.id.usernaam);

        mailtv.setText(mailofuser);
        nametv.setText(getIntent().getStringExtra("naam"));

        String[] data2={"Update Name","Update Password"};

        Integer[] images2={R.drawable.contacts,R.drawable.pass};

        Draweradapter adapter2 = new Draweradapter(settings.this,data2,images2);

        final ListView navList2 = (ListView) findViewById(R.id.list);
        navList2.setAdapter(adapter2);
        navList2.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id){

                switch(pos){
                    case 0:
                        LayoutInflater li = LayoutInflater.from(context);
                        View promptsView = li.inflate(R.layout.prompts, null);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(promptsView);

                        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                        // set dialog message
                        alertDialogBuilder
                                .setCancelable(true)
                                .setPositiveButton("SAVE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // get user input and set it to result
                                                // edit text
                                                if(TextUtils.isEmpty(userInput.getText().toString().trim())){
                                                    Snackbar.make(relativeLayout,"Name can't be empty",Snackbar.LENGTH_LONG).show();
                                                }
                                                else {
                                                    nametv.setText(userInput.getText().toString().trim());
                                                    mFirebaseDatabase.child("doctors").child(uidofuser).child("name")
                                                            .setValue(userInput.getText().toString().trim());
                                                }
                                            }
                                        })
                                .setNegativeButton("CANCEL",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                dialog.cancel();
                                            }
                                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        break;
                    case 1:
                        startActivity(new Intent(settings.this,changepassword.class));
                        break;
                }
            }
        });

        String[] data={"Your people"};

        Integer[] images={R.drawable.docrec};

        Draweradapter adapter = new Draweradapter(settings.this,data,images);

        final ListView navList = (ListView) findViewById(R.id.list1);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id){

                switch(pos){
                    case 0:
                        Intent pplact = new Intent(settings.this,people.class);
                        startActivity(pplact);
                       break;
                }
            }
        });

        String[] data3={"Update Members"};
        Integer[] images3={R.drawable.family};

        Draweradapter adapter3 = new Draweradapter(settings.this,data3,images3);

        final ListView navList3 = (ListView) findViewById(R.id.list2);
        navList3.setAdapter(adapter3);
        navList3.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id){

                switch(pos){
                    case 0:
                        startActivity(new Intent(settings.this,contacts.class));
                        break;
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
