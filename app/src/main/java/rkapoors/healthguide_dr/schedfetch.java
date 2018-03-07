package rkapoors.healthguide_dr;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.HashSet;
import java.util.Set;

public class schedfetch extends AppCompatActivity {

    AutoCompleteTextView mail;
    private SharedPreferences patients;
    private Set<String> history;

    Button sbt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedfetch);

        setTitle("schedule");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mail=(AutoCompleteTextView)findViewById(R.id.email);
        patients=getSharedPreferences("patientpref",0);
        history = new HashSet<String>(patients.getStringSet("patientkey", new HashSet<String>()));     //key, default value
        setautocompletesource();

        sbt = (Button)findViewById(R.id.chkbt);
        sbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
