package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

public class login extends AppCompatActivity {

    private EditText inputPassword;
    private AutoCompleteTextView inputEmail;
    private FirebaseAuth auth;
    private Button btnSignup, btnLogin, btnReset;
    private CoordinatorLayout coordinatorLayout;

    String email, password;
    public static final String USERNAME="username";
    public static final String SEARCHHISTORY="searchhistory";
    private SharedPreferences settings;
    private Set<String> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(login.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        setTitle("Login");

        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.signup_button);
        btnLogin = (Button) findViewById(R.id.login_button);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected) Snackbar.make(coordinatorLayout,"Check Internet Connection",Snackbar.LENGTH_LONG).show();

        settings=getSharedPreferences(USERNAME,0);
        history = new HashSet<String>(settings.getStringSet(SEARCHHISTORY, new HashSet<String>()));     //key, default value
        setautocompletesource();

        inputEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER))
                {
                    addsearchinput(inputEmail.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, signup.class));
                finish();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resetint = new Intent(login.this, resetpassword.class);
                resetint.putExtra("usermail",inputEmail.getText().toString().trim());
                startActivity(resetint);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = inputEmail.getText().toString().trim();
                password = inputPassword.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(coordinatorLayout, "Enter email address", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(coordinatorLayout, "Enter password", Snackbar.LENGTH_LONG).show();
                    return;
                }

                logintask task = new logintask(login.this);
                task.execute();
            }
        });
    }

    private class logintask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        public logintask(login activity){
            pd = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute(){
            pd.setMessage("Signing in...");
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    pd.dismiss();
                                    Snackbar.make(coordinatorLayout, "AUTHENTICATION failed", Snackbar.LENGTH_LONG).show();
                                } else {
                                    pd.dismiss();
                                    history.add(email);
                                    Intent intent = new Intent(login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setautocompletesource()
    {
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,history.toArray(new String[history.size()]));
        inputEmail.setAdapter(adapter);
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
        settings=getSharedPreferences(USERNAME,0);                 //name of sharedPreference object, mode 0 : accessible by app
        SharedPreferences.Editor editor=settings.edit();
        editor.putStringSet(SEARCHHISTORY,history);
        editor.apply();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        saveprefs();
    }
}
