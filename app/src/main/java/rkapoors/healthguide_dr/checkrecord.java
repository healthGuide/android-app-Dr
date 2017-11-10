package rkapoors.healthguide_dr;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class checkrecord extends AppCompatActivity {

    int selectedYear;
    int selectedMonth;
    int selectedDayOfMonth;
    public TextView fdt;
    public TextView tdt;

    AutoCompleteTextView mail;
    private SharedPreferences patients;
    private Set<String> history;

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
        Calendar c = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        selectedYear=c.get(Calendar.YEAR);
        selectedMonth=c.get(Calendar.MONTH);
        selectedDayOfMonth=c.get(Calendar.DAY_OF_MONTH);
        c.set(selectedYear,selectedMonth,selectedDayOfMonth);

        String datetoshow=dateFormatter.format(c.getTime());

        relativeLayout=(RelativeLayout)findViewById(R.id.content);

        mail=(AutoCompleteTextView)findViewById(R.id.email);
        patients=getSharedPreferences("patientpref",0);
        history = new HashSet<String>(patients.getStringSet("patientkey", new HashSet<String>()));     //key, default value
        setautocompletesource();

        mail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER))
                {
                    addsearchinput(mail.getText().toString());
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
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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
                dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
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

                if (TextUtils.isEmpty(mail.getText().toString())) {
                    Snackbar.make(relativeLayout, "Enter patient's email", Snackbar.LENGTH_LONG).show();
                    return;
                }

                ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    Intent chka = new Intent(getApplicationContext(), recorddata.class);
                    history.add(mail.getText().toString());
                    chka.putExtra("fromdate", fdt.getText().toString());
                    chka.putExtra("todate", tdt.getText().toString());
                    startActivity(chka);
                }
            }
        });
    }

    private void setautocompletesource()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
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
