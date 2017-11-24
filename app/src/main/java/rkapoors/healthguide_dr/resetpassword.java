package rkapoors.healthguide_dr;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class resetpassword extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnReset;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);

        setTitle("Security");
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.btn_reset_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        inputEmail.setText(getIntent().getStringExtra("usermail"));

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected) Snackbar.make(coordinatorLayout,"Check Internet Connection",Snackbar.LENGTH_LONG).show();

        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(coordinatorLayout, "Enter registered email address.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(coordinatorLayout, "Passwords reset instructions sent.", Snackbar.LENGTH_LONG).show();
                                } else {
                                    Snackbar.make(coordinatorLayout, "FAILED to send password reset instructions.", Snackbar.LENGTH_LONG).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
