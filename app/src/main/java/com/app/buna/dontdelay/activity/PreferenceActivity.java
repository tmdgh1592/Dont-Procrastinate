package com.app.buna.dontdelay.activity;


import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_ITEM;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.adapter.recycler.data.ToDoData;
import com.app.buna.dontdelay.calendar.calc.AbsoluteTime;
import com.app.buna.dontdelay.db.DBHelper;
import com.app.buna.dontdelay.noti.actionbar.ActionBarNotification;
import com.app.buna.dontdelay.push.AlarmHATT;
import com.app.buna.dontdelay.push.AlarmVO;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class PreferenceActivity extends android.preference.PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    public static final String MORNING_ALARM_CHECK_KEY = "alarmcheck";
    public static final String MORNING_PUSH_LIST_KEY = "morning_push_list";
    public static final String MORNING_MENT_EDIT_KEY = "morning_ment";
    public static final String BACKUP_TO_FIREBASE = "backup_to_firebase";
    public static final String LOAD_FROM_FIREBASE = "load_from_firebase";
    public static final String VERSION_CHECK_KEY = "version_check";
    public static final String LOGOUT_KEY = "logout";
    public static final String NOTI_ON_KEY = "noti_on";
    public static final String NOTI_OFF_KEY = "noti_off";
    //public static final String CAM_P_SWITCH = "camera_permission";

    private static final int MY_PERMISSIONS_REQUEST_CAMERA_CODE = 300;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 400;

    private GoogleSignInClient mGoogleSignInClient;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth = null;
    private FirebaseUser user = null;
    private String uid = null;

    private CheckBoxPreference alarmCheckPref;
    private ListPreference morningPushList;
    private Preference backupPref, loadPref, versionPref, logoutPref, notiOn, notiOff;
    private EditTextPreference mentEditText;

    //private SwitchPreference camPermissionSwitch;

    private SharedPreferences setting;

    private RewardedAd rewardedAd;


    //private SharedPreferences mainSetting;
    //private SharedPreferences.Editor mainEditor;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setAds();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(this);

        setting = PreferenceManager.getDefaultSharedPreferences(this);

        notiOn = (Preference)getPreferenceScreen().findPreference(NOTI_ON_KEY);
        notiOff = (Preference)getPreferenceScreen().findPreference(NOTI_OFF_KEY);
        alarmCheckPref = (CheckBoxPreference) getPreferenceScreen().findPreference(MORNING_ALARM_CHECK_KEY);
        morningPushList = (ListPreference) getPreferenceScreen().findPreference(MORNING_PUSH_LIST_KEY);
        mentEditText = (EditTextPreference) getPreferenceScreen().findPreference(MORNING_MENT_EDIT_KEY);
        backupPref = (Preference) getPreferenceScreen().findPreference(BACKUP_TO_FIREBASE);
        loadPref = (Preference) getPreferenceScreen().findPreference(LOAD_FROM_FIREBASE);
        versionPref = (Preference) getPreferenceScreen().findPreference(VERSION_CHECK_KEY);
        logoutPref = (Preference) getPreferenceScreen().findPreference(LOGOUT_KEY);
        //camPermissionSwitch = (SwitchPreference) getPreferenceScreen().findPreference(CAM_P_SWITCH);
        notiOn.setOnPreferenceClickListener(this);
        notiOff.setOnPreferenceClickListener(this);
        backupPref.setOnPreferenceClickListener(this);
        loadPref.setOnPreferenceClickListener(this);
        logoutPref.setOnPreferenceClickListener(this);
        versionPref.setOnPreferenceClickListener(this);
        alarmCheckPref.setOnPreferenceChangeListener(this);
        //camPermissionSwitch.setOnPreferenceChangeListener(this);
        //camPerCheck();

        versionPref.setSummary(getVersionInfo(PreferenceActivity.this));
    }


    private void setAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        InterstitialAd.load(this, getString(R.string.full_ad_unit_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });
    }


    /*private void camPerCheck(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            // ?????? ??????
            camPermissionSwitch.setChecked(false);
        }else{
            // ?????? ??????
            camPermissionSwitch.setChecked(true);
            camPermissionSwitch.setEnabled(false);
        }
    }*/


    private void showAd(){
        if(mInterstitialAd != null){
            mInterstitialAd.show(this);
        }else{
            Log.d("ERROR", "The interstitialAd wasn't loaded yet..");
        }
    }


    public String getVersionInfo(Context context) {
        String version = context.getString(R.string.app_version);
        PackageInfo packageInfo;

        if (context == null) {
            return version;
        }
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("Error", "getVersionInfo :" + e.getMessage());
        }
        return version;
    }




    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_PERMISSIONS_REQUEST_CODE){
            camPerCheck();
        }
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ?????? ??????
                    camPermissionSwitch.setChecked(true);
                    camPermissionSwitch.setEnabled(false);
                }else {
                    //?????? ??????
                    camPermissionSwitch.setChecked(false);
                }
                break;
            default:

                break;
        }

        return;
    }*/

/*    private void saveEditData(){
        String mentString = mentEditText.getText();
        Toast.makeText(this, mentString, Toast.LENGTH_SHORT).show();
        mainEditor.putString("MORNING_MENT", mentString);
        mainEditor.commit();
    }

    private void saveTimeDate(){
        String time = morningPushList.getEntry().toString();

        morningPushList.setSummary("?????? ?????? " + morningPushList.getEntry().toString() + "??? ??????????????? ???????????????!");
        mainEditor.putInt("ALARM_HOUR", Integer.parseInt(time.substring(0,1)));

        if(time.length() < 3){
            mainEditor.putInt("ALARM_MIN", 0);
        }else{
            mainEditor.putInt("ALARM_MIN", Integer.parseInt(time.substring(3, 5)));
        }

        mainEditor.commit();
    }*/

    private void updateSummary(){
        String time = morningPushList.getEntry().toString();
        String ment = mentEditText.getText();

        morningPushList.setSummary("?????? ?????? " + time + "??? ??????????????? ???????????????!");

        if((setting.getString(MORNING_MENT_EDIT_KEY, "").equals(""))){
            mentEditText.setSummary("???????????? ??????????????? ????????? ?????? ?????? ???????????????.");
        }else {
            mentEditText.setSummary(ment);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        InitializeFirebase();

        ActionBarNotification actionBarNotification = new ActionBarNotification(PreferenceActivity.this);

        switch (key){
            case BACKUP_TO_FIREBASE:
                writeData(uid);
                break;
            case LOAD_FROM_FIREBASE:
                loadData(uid);
                break;
            case LOGOUT_KEY:
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(this, "????????? ????????? ??? ????????????.\n??????, ?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this)
                            .setTitle("????????????")
                            .setMessage("?????? ??????????????? ?????? ????????? ???????????? ???????????????????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    signOut();
                                    finish();
                                    Toast.makeText(PreferenceActivity.this, "??????????????? ???????????? ???????????????!", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case NOTI_ON_KEY:
                actionBarNotification.showCustomLayoutNotification();
                if(actionBarNotification.isExist()){
                    Toast.makeText(this, "????????? ?????? ?????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "??????, ?????? ??????????????? ????????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
                break;
            case NOTI_OFF_KEY:
                if(actionBarNotification.isExist()){
                    actionBarNotification.cancel();
                    Toast.makeText(this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
                break;
            case VERSION_CHECK_KEY:
                break;
        }
        return false;
    }


    //??????????????? ???????????? ????????? ?????? ?????????????????? ??????
    /*public void CALLDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("??? ??????");
        alertDialog.setMessage("?????? ?????? ????????? ????????? ?????????????????? ?????????????????? ??????>??????> ?????? ?????? ????????? ????????? ????????????");

        // ???????????? ????????? ????????? ??????
        alertDialog.setPositiveButton("????????????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_CODE);
                        dialog.cancel();
                    }
                });
        //??????
        alertDialog.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        camPermissionSwitch.setChecked(false);
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }*/

    private void signOut() { // ?????????????????? ?????????
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1023891221826-jfri5ajr7lfgjqq066ldojot2ceudt80.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setResult(1);
                    }
                });
    }

    private void InitializeFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if(user != null){
            uid = user.getUid();
        }else{
            Toast.makeText(this, "????????? ????????? ??? ????????????.\n??????, ?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
            return;
        }
    }



    private void writeData(final String uid) {
        if(uid == null){
            Toast.makeText(this, "????????? ????????? ??? ????????????.\n??????, ?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this)
                .setMessage("???????????? ?????? ???????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAd();

                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.rawQuery("SELECT * FROM todo_table", null);

                        HashMap<String, Object> datas = new HashMap<>();

                        mDatabase.child("user-data").child(uid).child("todo-data").removeValue();

                        while(cursor.moveToNext()) {
                            ToDoData data = new ToDoData();

                            data.setToDoContent(cursor.getString(1));
                            data.setToDoDay((cursor.getString(2))); //format : mYear-mMonth-mDay
                            data.setRepeatDow(cursor.getString(3)); // format : x??????y??????z?????? or x??????y??????
                            data.setCreated(cursor.getString(4));
                            data.setHowRepeat(cursor.getString(5));
                            data.setRepeatMonth(cursor.getString(6));
                            data.setRepeatDate(cursor.getString(7));
                            data.setIsFavroite(Integer.parseInt(cursor.getString(8)));
                            data.setIsClear(Integer.parseInt(cursor.getString(9)));
                            data.setAlarmYear(cursor.getString(10));
                            data.setAlarmMonth(cursor.getString(11));
                            data.setAlarmDate(cursor.getString(12));
                            data.setAlarmHour(cursor.getString(13));
                            data.setAlarmMin(cursor.getString(14));
                            data.setRepeatText(cursor.getString(15));
                            data.setHowRepeatUnit(Integer.parseInt(cursor.getString(16)));
                            data.setMemo((cursor.getString(17)));
                            data.setForSeparatingCreated((cursor.getString(18)));
                            data.setViewType(VIEW_TYPE_ITEM);
                            data.setActionBarNoti(0);
                            //int dDay = calculateDDay(Integer.parseInt(cursor.getString(2).split("-")[0]), Integer.parseInt(cursor.getString(2).split("-")[1]), Integer.parseInt(cursor.getString(2).split("-")[2]));
                            data.setdDay(0);

                            datas.put(cursor.getString(0), data);

                        }

                        mDatabase.child("user-data").child(uid).child("todo-data").updateChildren(datas).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Firebase Database", "Success for saving");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Firebase Database", " Failed to saving");
                            }
                        });

                        setResult(RESULT_OK);
                        finish();

                        Toast.makeText(PreferenceActivity.this, "???????????? ??????????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        builder.create().show();
        return;
    }



    private void loadData(final String uid) {
        if(uid == null){
            Toast.makeText(this, "????????? ????????? ??? ????????????.\n??????, ?????? ????????? ????????? ????????????.", Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(PreferenceActivity.this)
                .setMessage("???????????? ???????????? ?????? ???????????? ???????????? ????????? ???????????? ????????? ???????????? ???????????????.\n????????? ???????????? ?????????????????????????")
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showAd();

                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                final SQLiteDatabase db = dbHelper.getWritableDatabase();

                                db.execSQL("DELETE FROM todo_table");
                                mDatabase.child("user-data").child(uid).child("todo-data").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                            ToDoData data = snapshot.getValue(ToDoData.class);
                                            dbInsert(data.getToDoContent(), data.getIsFavroite()==1, data.getToDoDay(),
                                                    data.getRepeatDow(), data.getCreated(), data.getHowRepeat(), data.getRepeatMonth(), data.getRepeatDate(), data.getIsClear()==1,
                                                    data.getAlarmYear(), data.getAlarmMonth(), data.getAlarmDate(), data.getAlarmHour(), data.getAlarmMin(), data.getRepeatText(),
                                                    Integer.toString(data.getHowRepeatUnit()), data.getMemo(), data.getForSeparatingCreated(), data.getdDay(), data.getActionBarNoti());

                                        }
                                        Toast.makeText(PreferenceActivity.this, "???????????? ??????????????? ??????????????????.", Toast.LENGTH_LONG).show();

                                        ActionBarNotification actionBarNotification = new ActionBarNotification(PreferenceActivity.this);
                                        actionBarNotification.cancel();

                                        setResult(RESULT_OK);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(PreferenceActivity.this, "????????? ???????????? ???????????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
                                        Log.d("Firebase DB Error", databaseError.getMessage());
                                    }
                                });
                            }
                        }

                        )
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

        builder.create().show();

    }

    private void dbInsert(String toDoContent, boolean isFavorite, String dayToDo, String repeatDOW,
                          String created, String howRepeat, String repeatMonth, String repeatDay, boolean isClear, String alarmYear, String alarmMonth, String alarmDate
            , String alarmHour, String alarmMin, String repeatText, String howRepeatUnit, String memo, String forSeparatingCreated, int dDay, int actionBarNoti) {

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String favoriteChk;
        String clearChk;

        if(isFavorite == true) {
            favoriteChk = "1";
        }else{
            favoriteChk = "0";
        }

        if(isClear == true) {
            clearChk = "1";
        }else {
            clearChk = "0";
        }

        db.execSQL("INSERT INTO todo_table (content, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDate, isFavorite, isclear," +
                        " alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, dDay, actionBarNoti) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new String[] {toDoContent, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDay, favoriteChk, clearChk,
                        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, Integer.toString(dDay), Integer.toString(actionBarNoti)});

        db.close();

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if(preference.getKey().equals(MORNING_ALARM_CHECK_KEY)){
            if(newValue.toString().equals("true")){
                reloadMorningAlarm();
            }else{
                new AlarmHATT(getApplicationContext()).alarmManagerCancel(AlarmVO.Id.MORNINGID);
            }
            return true;
        }/*else if(preference.getKey().equals(CAM_P_SWITCH)){
            if(newValue.toString().equals("true")){
                // ?????? ????????????
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)) {

                        // ??????????????????????????? ????????? ??????????????? ?????? ????????? ????????? ????????? ?????? ???????????????
                        // ?????? ????????? ????????? requestPermissions()????????? ???????????? ??????????????? ???????????? ?????????
                        CALLDialog();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA_CODE);
                    }

                }
            }else if(newValue.toString().equals("false")){
                //?????? ????????????

            }
            return true;
        }*/
        return false;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    // sharedPreference??? ????????? ?????? ????????? ????????? listener
        updateSummary();    // preference update

        if(key.equals(MORNING_PUSH_LIST_KEY)){    // ?????? preference??? ?????????????????? ???????????? ?????? ??????.
            /* ?????? ?????? reload */
            reloadMorningAlarm();
        }
    }


    private void reloadMorningAlarm(){

        new AlarmHATT(getApplicationContext()).alarmManagerCancel(AlarmVO.Id.MORNINGID);

        int[] time = new AlarmHATT(PreferenceActivity.this).getTime(setting.getString("morning_push_list", ""));
        Calendar calendar = Calendar.getInstance();
        int am_pm = calendar.get(Calendar.AM_PM);

        AbsoluteTime absoluteTime = new AbsoluteTime(calendar.get(Calendar.HOUR), am_pm);
        int nowHour = absoluteTime.getAbsTime();

        if((nowHour >= time[0]) && (calendar.get(Calendar.MINUTE) >= time[1]) && (calendar.get(Calendar.SECOND) >= 0)){
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
            calendar.add(Calendar.DATE, 1);
            Log.d("TAG", time[0]+"??? " + time[1]+"??? ??????");
        }else{
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
        }

        (new AlarmHATT(getApplicationContext(), AlarmVO.Id.MORNINGID)).alarm(calendar);
    }
}
