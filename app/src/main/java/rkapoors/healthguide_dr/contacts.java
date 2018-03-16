package rkapoors.healthguide_dr;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class contacts extends AppCompatActivity {

    Button addbutton, choosebutton;
    EditText phoneedtv, nameedtv;
    ListView lv;
    RelativeLayout relativeLayout;

    public  static final int RequestPermissionCode  = 1 ;

    String newcontact;

    final Context context = this;

    public static final String USERNAME="contacts";
    public static final String SEARCHHISTORY="contacthistory";
    private SharedPreferences contacts;
    private Set<String> history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        addbutton = (Button)findViewById(R.id.addbt);
        choosebutton = (Button)findViewById(R.id.contactbt);
        phoneedtv = (EditText)findViewById(R.id.phone);
        nameedtv = (EditText)findViewById(R.id.name);
        lv = (ListView)findViewById(R.id.list);
        relativeLayout = (RelativeLayout)findViewById(R.id.contactview);

        setTitle("Members");
        ActionBar actionBar =getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        contacts=getSharedPreferences(USERNAME,0);                                         //get SharedPrefernces with USERNAME key and 0 mode
        history = new HashSet<String>(contacts.getStringSet(SEARCHHISTORY, new HashSet<String>()));     //key, default value
        final ArrayList<String> contlist = new ArrayList<>();

        Iterator iterator = history.iterator();
        while (iterator.hasNext()){
            contlist.add(iterator.next()+"");
        }

        final ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,contlist);
        lv.setAdapter(adapter);

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                // set dialog message
                alertDialogBuilder
                        .setTitle("Sure to delete ?")
                        .setMessage(""+lv.getItemAtPosition(pos))
                        .setCancelable(true)
                        .setPositiveButton("DELETE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        history.remove(lv.getItemAtPosition(pos));
                                        contlist.remove(pos);
                                        contacts=getSharedPreferences(USERNAME,0);                 //name of sharedPreference object, mode 0 : accessible by app
                                        SharedPreferences.Editor editor=contacts.edit();
                                        editor.putStringSet(SEARCHHISTORY,history);
                                        editor.apply();
                                        adapter.notifyDataSetChanged();
                                        Snackbar.make(relativeLayout,"Deleted.",Snackbar.LENGTH_LONG).show();
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

                return true;
            }
        });

        EnableRuntimePermission();

        choosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 7);
            }
        });

        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(relativeLayout.getWindowToken(), 0);

                if(!TextUtils.isEmpty(phoneedtv.getText().toString().trim()) && !TextUtils.isEmpty(nameedtv.getText().toString().trim())) {
                    newcontact = nameedtv.getText().toString().trim() + "\n" + phoneedtv.getText().toString().trim();
                    history.add(newcontact);
                    contlist.add(newcontact);
                    contacts=getSharedPreferences(USERNAME,0);                 //name of sharedPreference object, mode 0 : accessible by app
                    SharedPreferences.Editor editor=contacts.edit();
                    editor.putStringSet(SEARCHHISTORY,history);
                    editor.apply();
                    adapter.notifyDataSetChanged();
                    /*
                    notifyDataSetChanged ()
                        Notifies the attached observers that the underlying
                        data has been changed and any View reflecting the
                        data set should refresh itself.
                 */
                    nameedtv.setText("");phoneedtv.setText("");nameedtv.requestFocus();
                    Snackbar.make(relativeLayout,"Added.",Snackbar.LENGTH_LONG).show();
                }
                else
                    Snackbar.make(relativeLayout,"Please fill in both fields.",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(contacts.this, android.Manifest.permission.READ_CONTACTS))
        {
            //Toast.makeText(contacts.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(contacts.this,new String[]{android.Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(contacts.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                    startActivityForResult(intent, 7);
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    // set dialog message
                    alertDialogBuilder
                            .setTitle("Permission denied")
                            .setMessage("Go to settings.\nCheck healthGuide under Contacts permission.")
                            .setCancelable(false)
                            .setPositiveButton("OK",
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
                break;
        }
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {

        super.onActivityResult(RequestCode, ResultCode, ResultIntent);

        switch (RequestCode) {

            case (7):
                if (ResultCode == Activity.RESULT_OK) {

                    Uri uri;
                    Cursor cursor1, cursor2;
                    String TempNameHolder, TempNumberHolder, TempContactID, IDresult = "" ;
                    int IDresultHolder ;

                    uri = ResultIntent.getData();

                    cursor1 = getContentResolver().query(uri, null, null, null, null);

                    if (cursor1.moveToFirst()) {

                        TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));

                        IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        IDresultHolder = Integer.valueOf(IDresult) ;

                        if (IDresultHolder == 1) {

                            cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);

                            while (cursor2.moveToNext()) {

                                TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                nameedtv.setText(TempNameHolder);
                                phoneedtv.setText(TempNumberHolder);

                            }
                        }

                    }
                }
                break;
        }
    }
}
