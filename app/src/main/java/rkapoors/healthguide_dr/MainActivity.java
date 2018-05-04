//Source : https://www.androidhive.info/2015/09/android-material-design-working-with-tabs/

package rkapoors.healthguide_dr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    final Context context = this;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    boolean doubleBackToExitPressedOnce = false;
    private TextView maildesc;
    TextView naam;
    String useruid="";

    FirebaseDatabase database;
    DatabaseReference dbref;

    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        database=FirebaseDatabase.getInstance();
        dbref=database.getReference();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
                public void onDrawerClosed(View view){
                    supportInvalidateOptionsMenu();
                }
                public void onDrawerOpened(View drawerView){
                    supportInvalidateOptionsMenu();
                }
            };
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
        }

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(!isConnected)
        {
            Snackbar snackbar=Snackbar.make(drawerLayout, "Check Internet Connection", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }

        auth = FirebaseAuth.getInstance();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, login.class));
                    finish();
                }
            }
        };

        String mailaddr="";
        if(user!=null) {mailaddr=user.getEmail();useruid=user.getUid();}
        maildesc = (TextView)findViewById(R.id.useremail);
        maildesc.setText(mailaddr);

        naam = (TextView)findViewById(R.id.userName);
        dbref.child("doctors").child(useruid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                naam.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        String[] data={"Schedule","Suggest","Records","Emergency","________________________________","Settings & Info","About us","Log out"};
        Integer[] images={R.drawable.schedicon,R.drawable.notificon,R.drawable.recordicon,R.drawable.emergicon,0,
                R.drawable.settings,R.drawable.information,R.drawable.logicon,};

        Draweradapter adapter = new Draweradapter(MainActivity.this,data,images);

        final ListView navList = (ListView) findViewById(R.id.navList);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos,long id){

                switch(pos){
                    case 0:
                        drawerLayout.closeDrawers();
                        viewPager.setCurrentItem(0);
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,notification.class));
                        break;
                    case 2:
                        drawerLayout.closeDrawers();
                        viewPager.setCurrentItem(1);
                        break;
                    case 3:
                        drawerLayout.closeDrawers();
                        viewPager.setCurrentItem(2);
                        break;
                    case 5:
                        Intent settingsintent = new Intent(MainActivity.this,settings.class);
                        settingsintent.putExtra("mailid",maildesc.getText().toString());
                        settingsintent.putExtra("naam",naam.getText().toString());
                        startActivity(settingsintent);
                        break;
                    case 6:
                        Intent abtact = new Intent(MainActivity.this,aboutus.class);
                        startActivity(abtact);
                        break;
                    case 7:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        // set dialog message
                        alertDialogBuilder
                                .setTitle("Log out")
                                .setMessage("Sure to Log out ?")
                                .setCancelable(true)
                                .setPositiveButton("Log out",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                signOut();
                                            }
                                        })
                                .setNegativeButton("Cancel",
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
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScheduleFragment(), "Schedule");
        adapter.addFragment(new RecordFragment(), "Records");
        adapter.addFragment(new EmergencyFragment(), "Emergency");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            Snackbar snackbar = Snackbar.make(drawerLayout, "Tap back again to exit.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    //sign out method
    public void signOut() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Signing out...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
                auth.signOut();
                pd.dismiss();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume(){
        super.onResume();

        dbref.child("doctors").child(useruid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.getValue(String.class);
                naam.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}