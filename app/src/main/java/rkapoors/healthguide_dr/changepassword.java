package rkapoors.healthguide_dr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changepassword extends AppCompatActivity {

    private Button changepassbtn;
    private EditText newpass,oldpass;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        setTitle("Security");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        changepassbtn = (Button)findViewById(R.id.btn_change_password);
        newpass = (EditText)findViewById(R.id.newpassword);
        oldpass = (EditText)findViewById(R.id.oldpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected) Snackbar.make(coordinatorLayout,"Check Internet Connection",Snackbar.LENGTH_LONG).show();

        //auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        changepassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newpass.getText().toString().trim().equals("") && !oldpass.getText().toString().trim().equals(""))
                {
                    if (newpass.getText().toString().trim().length() < 6) {
                        Snackbar.make(coordinatorLayout,"New Password too short. Minimum 6 characters",Snackbar.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        final String email = user.getEmail();
                        AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass.getText().toString().trim());

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newpass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()){
                                                Snackbar snackbar_fail = Snackbar.make(coordinatorLayout, "Something went wrong. Please try again", Snackbar.LENGTH_LONG);
                                                snackbar_fail.show();
                                                progressBar.setVisibility(View.GONE);
                                            }else {
                                                Snackbar snackbar_su = Snackbar.make(coordinatorLayout, "Password Successfully UPDATED", Snackbar.LENGTH_LONG);
                                                snackbar_su.show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                                else {
                                    Snackbar snackbar_su = Snackbar.make(coordinatorLayout, "Authentication FAILED", Snackbar.LENGTH_LONG);
                                    snackbar_su.show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
                else if (oldpass.getText().toString().trim().equals(""))
                {
                    Snackbar.make(coordinatorLayout,"Enter current Password",Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else if (newpass.getText().toString().trim().equals(""))
                {
                    Snackbar.make(coordinatorLayout,"Enter new Password",Snackbar.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
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
