package rkapoors.healthguide_dr;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class recorddata extends AppCompatActivity {

    RecyclerView recycle;
    Button ftbt;
    TextView frdt,todt,mailtv;
    RelativeLayout relativeLayout;

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

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
