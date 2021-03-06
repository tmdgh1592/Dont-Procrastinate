package com.app.buna.dontdelay.activity;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_ITEM;
import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_TITLE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.adapter.recycler.data.NotDoneRecyclerAdapter;
import com.app.buna.dontdelay.adapter.recycler.data.ToDoData;
import com.app.buna.dontdelay.adapter.recycler.data.theme.ThemeData;
import com.app.buna.dontdelay.adapter.recycler.theme.ThemeRecyclerAdapter;
import com.app.buna.dontdelay.calendar.calc.AbsoluteTime;
import com.app.buna.dontdelay.calendar.calc.DOWManger;
import com.app.buna.dontdelay.calendar.calc.DayManager;
import com.app.buna.dontdelay.callback.SwipeToDeleteCallback;
import com.app.buna.dontdelay.customView.BackPressEditText;
import com.app.buna.dontdelay.customView.CircleImageView;
import com.app.buna.dontdelay.db.DBHelper;
import com.app.buna.dontdelay.network.NetworkStatus;
import com.app.buna.dontdelay.noti.actionbar.ActionBarNotification;
import com.app.buna.dontdelay.push.AlarmHATT;
import com.app.buna.dontdelay.push.AlarmVO;
import com.app.buna.dontdelay.utils.MyTextWatcher;
import com.app.buna.dontdelay.widget.MAppWidget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private int SHOW_STATE = 0;
    private int SHOW_DONE = 0;
    private int SORT_STATE = 1000;
    private int SHOW_IMPO = 0;
    private boolean HOW_SORT = true;
    private int THEME_STYLE = 0;

    SharedPreferences setting;
    SharedPreferences.Editor editor;


    /* activity_drawer */
    private View drawerView;
    private ImageView profileImageView;
    private ImageView option1Check, option3Check, option4Check, option5Check;
    private TextView loginText;

    private DrawerLayout drawerLayout;
    private ImageView drawerConfig;

    private DialogPlus widgetMakingDialog;
    private ImageView closeWidgetImageView;
    private View makingWidgetLayout;

    private RelativeLayout optionlayout1, optionlayout2, optionlayout3, optionlayout4, optionlayout5, optionlayout6, optionlayout7, optionlayout8;

    private Snackbar snackbar;


    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Context mContext;
    private TabLayout mTabLayout;
    private View todayScheduleView;
    private LinearLayout todoLinearLayout, addLinearLayout, writeOkLayout, firstMenuLayout, secondMenuLayout, thirdMenuLayout, topLayout, notDoneView;
    private RelativeLayout backgroundLayout;

    private DOWManger dowManger;
    StringBuilder dowStringBuilder = new StringBuilder();

    public RecyclerView notDoneRecyclerView;
    private RecyclerView.Adapter notDoneAdapter;

    private Animation addViewAnimation;

    private AppBarLayout appBarLayout;

    private RecyclerView.LayoutManager layoutManager;
    public ArrayList<ToDoData> notDoneDataset = new ArrayList<>();
    public ArrayList<ToDoData> doneDataset = new ArrayList<>();
    private ArrayList<String> toDoDayList = new ArrayList<>();

    private ItemTouchHelper itemTouchHelper;

    private TextView firstMenuTextView, secondMenuTextView, thirdMenuTextView;

    private FloatingActionButton fab;
    private InputMethodManager imm;
    private BackPressEditText enterToDoEditText;
    private ImageView writeOkImageView, favoriteImageView, backgroundImageView;

    private InterstitialAd mInterstitialAd;

    private TabLayout tabLayout;
    private View dialogView;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    DayManager dayManager;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String todayYear, todayMonth, todayDay, toDoContent = "";

    private CheckBox monCB, tueCB, wedCB, thurCB, friCB, satCB, sunCB;

    private Spinner howRepeatSpinner;
    private EditText howRepeatEditText;

    String[] weekDay = {"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????"};

    private boolean isKeyBoardView = false;
    private boolean isFavorite = false;
    private PopupMenu popupMenu;
    public final int REQUEST_TO_EDIT_ACTIVITY_CODE = 3300;
    public final int REQUEST_TO_PREF_ACTIVITY_CODE = 4000;
    private Toolbar toolbar;

    AlertDialog.Builder exitBuilder;
    AlertDialog exitDialog;

    private View howRepeatView;

    AlertDialog.Builder builder;
    AlertDialog howRepeatDialog;

    private View exitView;

    private int editPosition, howRepeatUnit = 1, howRepeatDayInt = 0;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private String todayDayOfWeek, tomorrowDayOfWeek, nextWeekDayOfWeek, calendarDayOfWeek;     // ?????? deadline
    private String selectedYear = "", selectedMonth = "", selectedDay = "";                                    // ?????? ??????
    private String howRepeat = "NOREPEAT", howRepeatDayString = "";
    private String alarmHour = "", alarmMin = "";                                                         // ?????? ??????
    private String alarmYear = "", alarmMonth = "", alarmDate = "";
    private String repeatMonth = "", repeatDay = "", repeatDow = "", repeatText = "";                                      // ?????? ???, ???, ??????
    private String memo = "";
    private String forSeparatingCreated = "";

    private boolean isDaySet = false;
    private boolean isRepeatSet = false;
    private boolean isAlarmSet = false;

    private RecyclerView themeRecyclerView;
    private RecyclerView.Adapter themeAdapter;
    private RecyclerView.LayoutManager themeLayoutManager;
    private ArrayList<ThemeData> themeDataArrayList = new ArrayList<>();

    private DialogPlus loginDialog;

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private Bitmap bitmap;
    private View loginView;
    private SignInButton googleBtn;     //gogle login button;

    private CallbackManager mCallbackManager;
    /*private LoginButton kakaoLoginButton;
    SessionCallback callback;
    //???????????????
    String token = "";
    String name = "";*/
    private AdView mAdView;
    private AdLoader adLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        loginView = getLayoutInflater().inflate(R.layout.layout_login, null);
        exitView = LayoutInflater.from(this).inflate(R.layout.custom_toast_back_press, null);

        settingGso(); // google login setting
        settingFb(); // facebook login setting
        //settingkakao(); // kakao login setting

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        firstStartCheck();

        /*drawer setting*/

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        drawerLayout.openDrawer(drawerView);
        drawerConfig = findViewById(R.id.drawer_config);
        optionlayout1 = findViewById(R.id.option1);
        optionlayout2 = findViewById(R.id.option2);
        optionlayout3 = findViewById(R.id.option3);
        optionlayout4 = findViewById(R.id.option4);
        optionlayout5 = findViewById(R.id.option5);
        optionlayout6 = findViewById(R.id.option6);
        optionlayout7 = findViewById(R.id.option7);
        optionlayout8 = findViewById(R.id.option8);

        option1Check = findViewById(R.id.option1_check_image_view);
        option3Check = findViewById(R.id.option3_check_image_view);
        option4Check = findViewById(R.id.option4_check_image_view);
        option5Check = findViewById(R.id.option5_check_image_view);

        setfullwidth();

        loginText = findViewById(R.id.drawer_login);
        loginText.setOnClickListener(clickListener);
        drawerLayout.setDrawerListener(drawerListener);
        drawerView.setOnTouchListener(touchListener);
        drawerConfig.setOnClickListener(clickListener);

        optionlayout1.setOnClickListener(clickListener);
        optionlayout2.setOnClickListener(clickListener);
        optionlayout3.setOnClickListener(clickListener);
        optionlayout4.setOnClickListener(clickListener);
        optionlayout5.setOnClickListener(clickListener);
        optionlayout6.setOnClickListener(clickListener);
        optionlayout7.setOnClickListener(clickListener);
        optionlayout8.setOnClickListener(clickListener);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        snackbar = Snackbar
                .make(drawerLayout, "?????? ?????????", Snackbar.LENGTH_INDEFINITE);

        topLayout = findViewById(R.id.top_layout);
        topLayout.setOnTouchListener(touchListener);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout01);

        howRepeatView = getLayoutInflater().inflate(R.layout.custom_how_repeat_dialog, null);

        loadCheckBox();
        loadAndSetListenerMenuLayout();


        favoriteImageView = findViewById(R.id.favorite_image_view);
        favoriteImageView.setOnTouchListener(touchListener);

        dayManager = new DayManager();


        firstMenuTextView = findViewById(R.id.first_menu_text_view);
        secondMenuTextView = findViewById(R.id.second_menu_text_view);
        thirdMenuTextView = findViewById(R.id.third_menu_text_view);

        howRepeatEditText = howRepeatView.findViewById(R.id.how_repeat_edit_text);
        howRepeatSpinner = howRepeatView.findViewById(R.id.how_repeat_spinner);
        howRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // ??????
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LinearLayout layout = howRepeatView.findViewById(R.id.day_select_layout);
                switch (position) {
                    case 0:
                        howRepeatUnit = 1;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 1:
                        howRepeatUnit = 7;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(VISIBLE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_visible));
                        break;
                    case 2:
                        howRepeatUnit = 30;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 3:
                        howRepeatUnit = 365;
                        initCheckBox();
                        toDoDayList.clear();
                        layout.setVisibility(GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("LogD", "onNothingSelected: nothing selected");
            }
        });


        writeOkLayout = findViewById(R.id.write_ok_layout);
        writeOkImageView = findViewById(R.id.write_ok_image_view);

        enterToDoEditText = findViewById(R.id.add_edit_text);
        enterToDoEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        enterToDoEditText.setOnBackPressListener(onBackPressListener);
        enterToDoEditText.setOnEditorActionListener(editorActionListener);
        MyTextWatcher editTextWatcher = new MyTextWatcher(enterToDoEditText);
        enterToDoEditText.addTextChangedListener(editTextWatcher);
        editTextWatcher.setResource(getResources(), writeOkImageView, writeOkLayout);

        exitBuilder = new AlertDialog.Builder(MainActivity.this);
        exitBuilder.setView(exitView);
        exitBuilder.setCancelable(false);
        exitBuilder.setPositiveButton("??????", (dialogInterface, i) -> finishAffinity());
        exitBuilder.setNegativeButton("??????", (dialogInterface, i) -> {

        });

        exitDialog = exitBuilder.create();
        exitDialog.setContentView(R.layout.custom_toast_back_press);
        exitDialog.setCanceledOnTouchOutside(false);

        mContext = getApplicationContext();


        /*mTabLayout = (TabLayout) findViewById(R.id.layout_tab);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("?????? ??? ???".toString(),0)));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("D - Day".toString(), 1)));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("??????&??????".toString(), 2)));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(createTabView("?????????", 3)));

        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mPagerAdapter = new com.app.buna.dontdelay.Adapter.PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(tabListener);*/

        todayScheduleView = LayoutInflater.from(this).inflate(R.layout.dday_schedule, null);
        todoLinearLayout = todayScheduleView.findViewById(R.id.to_do_linearlayout);
        todoLinearLayout.setClickable(true);
        todoLinearLayout.setOnClickListener(clickListener);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        addLinearLayout = findViewById(R.id.add_linear_layout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickListener);
        writeOkImageView.setOnClickListener(clickListener);

        todayYear = sdf.format(new Date()).split("-")[0];
        todayMonth = sdf.format(new Date()).split("-")[1];
        todayDay = sdf.format(new Date()).split("-")[2];


        /* ????????? recyclerview */


        notDoneRecyclerView = (RecyclerView) findViewById(R.id.not_done_recycler_view);
        notDoneRecyclerView.setHasFixedSize(true);

        enableSwipeToDeleteAndUndo();

        /*itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(notDoneRecyclerView);*/

        builder = new AlertDialog.Builder(MainActivity.this);


        builder.setView(howRepeatView)
                .setPositiveButton("??????", (dialogInterface, which) -> {

                })
                .setNegativeButton("??????", (dialogInterface, which) -> initCheckBox());

        howRepeatDialog = builder.create();
        howRepeatDialog.setOnShowListener(dialogInterface -> {
            Button posBtn = howRepeatDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negBtn = howRepeatDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negBtn.setTextColor(Color.GRAY);
            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    howRepeatDayString = Integer.toString(howRepeatDayInt = (Integer.parseInt(howRepeatEditText.getText().toString()) * howRepeatUnit));
                    howRepeat = "CALENDAR";
                    thirdMenuTextView.setTextSize(12);
                    addToDoDOWToArrayList();
                    Iterator<String> iterator = toDoDayList.iterator();
                    while (iterator.hasNext()) {
                        dowStringBuilder.append(iterator.next());      // ????????? split("??????")??? ????????????
                    }
                    repeatDow = dowStringBuilder.toString();
                    if (howRepeatEditText.getText().toString().equals("0")) {
                        Toast.makeText(mContext, "0?????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        toDoDayList.clear();
                    } else if (howRepeatUnit == 7 && toDoDayList.isEmpty()) {
                        Toast.makeText(mContext, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                        toDoDayList.clear();
                    } else {
                        setThirdTextView();
                        initCheckBox();
                        repeatText = howRepeatEditText.getText().toString();
                        thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                        howRepeatDialog.dismiss();
                        isRepeatSet = true;
                    }
                }
            });
            negBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toDoDayList.clear();
                    howRepeatDialog.cancel();
                }
            });
        });
        howRepeatDialog.setCanceledOnTouchOutside(false);


        notDoneRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                notDoneRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {          /* item ??? ????????? ??? */

            }
        }));


        layoutManager = new LinearLayoutManager(this);
        notDoneRecyclerView.setLayoutManager(layoutManager);

        notDoneAdapter = new NotDoneRecyclerAdapter(notDoneDataset, this, getResources(), this);
        notDoneRecyclerView.setAdapter(notDoneAdapter);
        //setViewAsTime();


    }




    /*private void settingkakao() {

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        kakaoLoginButton = loginView.findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // kakao button
                kakaoLoginButton.performClick();

                *//* custom kakao button
                Session session = Session.getCurrentSession();
                session.addCallback(new SessionCallback());
                session.open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
                * *//*
            }
        });
    }*/

    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                    if (enterToDoEditText.getText().length() == 0) {
                        return true;
                    } else if (enterToDoEditText.getText().length() > 0) { // ??? ??? ???????????? ???????????? ?????? ??????
                        /*if(isAlarmSet == false || isDaySet == false || isRepeatSet == false) {  // ??????, ??????, ?????? ?????? ???????????? ???
                            Toast.makeText(mContext, getString(R.string.alertAboutUnSet), Toast.LENGTH_SHORT).show();
                            return;
                        }else{*/  // ??????, ????????????, ??????????????? ?????? ??? ?????? ??????
                        toDoContent = enterToDoEditText.getText().toString();
                        forSeparatingCreated = Long.toString(System.currentTimeMillis());

                        dbInsert(toDoContent, isFavorite, selectedYear + "-" + selectedMonth + "-" + selectedDay,
                                dowStringBuilder.toString(),
                                sdf.format(new Date()), howRepeat, false,
                                alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, forSeparatingCreated,
                                calculateDDay(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth), Integer.parseInt(selectedDay)), 0);

                        if (!alarmYear.equals("")) {
                            Calendar alarmCalendar = Calendar.getInstance();
                            int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length() - 1));
                            // ??? ??? ????????? ????????? ?????? ????????? ???
                            // calendar??? 0 ~ 11

                            alarmCalendar.set(Integer.parseInt(alarmYear),
                                    Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                                    Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
                            new AlarmHATT(getApplicationContext(), alarmId).alarm(alarmCalendar);
                        }

                        /* ????????? */
                        updateWidget();
                        initSettings();
                        hideAddLayout();
                        onResume();
                        //}
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public boolean isSoftKeyboardShown() {
        if ((drawerLayout) == null) return false;

        int height = drawerLayout.getMeasuredHeight();
        Activity activity = (Activity) MainActivity.this;
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        int screenHeight;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point p = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(p);
            screenHeight = p.y;
        } else {
            screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight();
        }

        int diff = (screenHeight - statusBarHeight) - height;

        return (diff > 200);// assume all soft keyboards are at least 128 pixels high
    }


    private void setAds() {

        setNativeAds();
        MobileAds.initialize(this, initializationStatus -> {

        });
        ((AdView) findViewById(R.id.banner)).loadAd(new AdRequest.Builder().build());
        loadFullpageAds();

        mAdView = loginView.findViewById(R.id.mainAct_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadFullpageAds() {
        InterstitialAd.load(this, getString(R.string.full_ad_unit_id), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        //super.onAdDismissedFullScreenContent();
                        loadFullpageAds();
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });
    }


    private void setNativeAds() {
        AdLoader adLoader = new AdLoader.Builder(this, getString(R.string.native_ad_unit_id))
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().build();

                    TemplateView template = exitView.findViewById(R.id.my_template);
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }



    /* google Firebase Methods */

    private void settingGso() {

        setAds();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1023891221826-jfri5ajr7lfgjqq066ldojot2ceudt80.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginDialog = DialogPlus.newDialog(MainActivity.this)
                .setContentHolder(new ViewHolder(loginView))
                .setGravity(Gravity.CENTER)
                .setCancelable(true)
                .create();


        googleBtn = loginView.findViewById(R.id.google_login_button);
        googleBtn.setOnClickListener(view -> signIn());
    }

    private void signIn() { // ????????? ?????? ???????????? ?????????
        if (NetworkStatus.isConnectedInternet(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(mContext, "????????? ?????? ????????? ?????? ????????????.\nWifi ?????? ????????? ?????? ????????? ?????????????????? ????????????.", Toast.LENGTH_LONG).show();
        }
    }


    private void updateUI(final FirebaseUser user) {

        final TextView emailTextView = (TextView) findViewById(R.id.email_text_view);
        final TextView nameTextView = (TextView) findViewById(R.id.name_text_view);
        final CircleImageView profile = findViewById(R.id.profile_image_view);

        if (user != null) {

            Thread mThread = new Thread() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(user.getPhotoUrl().toString());
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    } catch (MalformedURLException ee) {
                        ee.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();
            try {
                mThread.join();
                profile.setImageBitmap(bitmap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            findViewById(R.id.login_alert).setVisibility(GONE);
            findViewById(R.id.name_text).setVisibility(VISIBLE);
            emailTextView.setVisibility(VISIBLE);
            nameTextView.setVisibility(VISIBLE);
            findViewById(R.id.login_text).setVisibility(VISIBLE);
            emailTextView.setText(user.getEmail());
            nameTextView.setText(user.getDisplayName());
        } else {
            findViewById(R.id.login_alert).setVisibility(VISIBLE);
            findViewById(R.id.name_text).setVisibility(GONE);
            emailTextView.setVisibility(GONE);
            emailTextView.setText("???????????? ????????? ??????????????????.");
            nameTextView.setVisibility(GONE);
            profile.setImageResource(R.drawable.profile);
            findViewById(R.id.login_text).setVisibility(GONE);
        }


    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("MainActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("MainActivity", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.d("MainActivity", "signInWithCredential:failed");
                        updateUI(null);
                    }
                    // ...
                });
    }







    /* facebook firebase methods*/

    private void settingFb() {
        mCallbackManager = CallbackManager.Factory.create();
        com.facebook.login.widget.LoginButton googleButton = loginView.findViewById(R.id.facebook_login_button);
        googleButton.setReadPermissions("email", "public_profile");
        googleButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
    }

    private void handleFacebookAccessToken(com.facebook.AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                    }
                });
    }




    /*                     */
    /*   listener methods  */
    /*                     */


    private BackPressEditText.OnBackPressListener onBackPressListener = () -> {
        initSettings();
        hideAddLayout();
        initCheckBox();
    };

    private void firstStartCheck() {
        int checkFirstConn = setting.getInt("firstStart", 0);

        if (checkFirstConn == 0) {       //???????????? ??????

            Log.d(TAG, "firstStartCheck: this is first.");

            /* ?????? 7????????? ???????????? START */
            editor.putInt("firstStart", 1);
            editor.commit();

            int[] time = {7, 0};

            Calendar calendar = Calendar.getInstance();
            int am_pm = calendar.get(Calendar.AM_PM);

            AbsoluteTime absoluteTime = new AbsoluteTime(calendar.get(Calendar.HOUR), am_pm);
            int nowHour = absoluteTime.getAbsTime();

            if ((nowHour >= time[0]) && (calendar.get(Calendar.MINUTE) >= time[1]) && (calendar.get(Calendar.SECOND) >= 0)) {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
                calendar.add(Calendar.DATE, 1);
                Log.d("TAG", time[0] + "??? " + time[1] + "??? ??????");
            } else {
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), time[0], time[1], 00);
            }

            (new AlarmHATT(MainActivity.this, AlarmVO.Id.MORNINGID)).alarm(calendar);

            /* ?????? 7????????? ???????????? END */
        }
    }

    // ????????? ?????? ?????? ????????? ??????

    /*private void setViewAsTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        int nowHour = Integer.parseInt(sdf.format(new Date()));
        Toast.makeText(mContext, Integer.toString(nowHour), Toast.LENGTH_SHORT).show();

        backgroundImageView = findViewById(R.id.background_image_view);
        backgroundLayout = findViewById(R.id.background_layout);

        Toast.makeText(mContext, Integer.toString(nowHour), Toast.LENGTH_SHORT).show();

        if(7 <= nowHour && nowHour <= 16) {  // ?????? ?????? ??????
            backgroundImageView.setBackgroundResource(R.drawable.morning_background);
            backgroundLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
            getWindow().setStatusBarColor(Color.RED);
            collapsingToolbarLayout.setContentScrimColor(Color.GREEN);
        } else if((17 <= nowHour && nowHour <= 20) || (5 <= nowHour && nowHour <= 6)){ // ?????????, ?????? ?????? ??????
            backgroundImageView.setBackgroundResource(R.drawable.evening_background);
            backgroundLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.nightBackgroundColor2));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.nightBackgroundColor));
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.nightBackgroundColor));
        } else { // ??? ?????? ??????
            backgroundImageView.setBackgroundResource(R.drawable.night_background);
            backgroundLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.nightBackgroundColor));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.nightBackgroundColor));
            collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.nightBackgroundColor));
        }


    }*/

    private void setfullwidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();
        params.width = displayMetrics.widthPixels;
        drawerView.setLayoutParams(params);
    }

    public TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @SuppressLint("RestrictedApi")
        @Override
        public void onClick(View view) {

            popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);


            switch (view.getId()) {
                /*case R.id.to_do_linearlayout:
                    TextView textView = view.findViewById(R.id.to_do_text);
                    break;*/
                case R.id.first_menu_layout:
                    getMenuInflater().inflate(R.menu.add_first_menu, popupMenu.getMenu());
                    todayDayOfWeek = setDeadlineDOW("??????");
                    tomorrowDayOfWeek = setDeadlineDOW("??????");
                    nextWeekDayOfWeek = setDeadlineDOW("?????????");
                    popupMenu.getMenu().add(Menu.NONE, 1, 0, "?????? (" + todayDayOfWeek + ")");  //groupId, itemId, order, title
                    popupMenu.getMenu().add(Menu.NONE, 2, 1, "?????? (" + tomorrowDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 3, 2, "?????? ??? (" + nextWeekDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 4, 3, "?????? ??????");
                    popupMenu.show();
                    break;
                case R.id.second_menu_layout:
                    getMenuInflater().inflate(R.menu.add_second_menu, popupMenu.getMenu());
                    popupMenu.show();
                    break;
                case R.id.third_menu_layout:
                    getMenuInflater().inflate(R.menu.add_third_menu, popupMenu.getMenu());
                    toDoDayList.clear();
                    popupMenu.show();
                    break;
                case R.id.fab:
                    if (snackbar.isShown()) {
                        snackbar.dismiss();
                    }
                    initAddView();
                    enterToDoEditText.requestFocus();
                    isKeyBoardView = true;
                    initSettings();

                    fab.setVisibility(GONE);
                    fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_invisible));

                    appBarLayout = findViewById(R.id.app_bar_layout);
                    appBarLayout.setExpanded(false);
                    if (addLinearLayout.getVisibility() == GONE) {
                        addLinearLayout.setVisibility(VISIBLE);
                    }

                    Thread visibleThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            imm.showSoftInput(enterToDoEditText, 0);
                        }
                    });
                    visibleThread.start();
/*                    addViewAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_visible);
                    addLinearLayout.startAnimation(addViewAnimation);*/

                    selectedYear = todayYear;
                    selectedMonth = todayMonth;
                    selectedDay = todayDay;

                    firstMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
                    secondMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);
                    thirdMenuLayout.setBackgroundResource(R.drawable.custom_unsetting_layout);

                    break;
                case R.id.write_ok_image_view:
                    if (enterToDoEditText.getText().length() != 0) { // ??? ??? ???????????? ???????????? ?????? ??????
                        /*if(isAlarmSet == false || isDaySet == false || isRepeatSet == false) {  // ??????, ??????, ?????? ?????? ???????????? ???
                            Toast.makeText(mContext, getString(R.string.alertAboutUnSet), Toast.LENGTH_SHORT).show();
                            return;
                        }else{*/  // ??????, ????????????, ??????????????? ?????? ??? ?????? ??????
                        toDoContent = enterToDoEditText.getText().toString();
                        forSeparatingCreated = Long.toString(System.currentTimeMillis());

                        dbInsert(toDoContent, isFavorite, selectedYear + "-" + selectedMonth + "-" + selectedDay,
                                dowStringBuilder.toString(),
                                sdf.format(new Date()), howRepeat, false,
                                alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, forSeparatingCreated,
                                calculateDDay(Integer.parseInt(selectedYear), Integer.parseInt(selectedMonth), Integer.parseInt(selectedDay)), 0);

                        if (!alarmYear.equals("")) {
                            Calendar alarmCalendar = Calendar.getInstance();
                            int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length() - 1));
                            // ??? ??? ????????? ????????? ?????? ????????? ???
                            // calendar??? 0 ~ 11

                            alarmCalendar.set(Integer.parseInt(alarmYear),
                                    Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                                    Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
                            new AlarmHATT(getApplicationContext(), alarmId).alarm(alarmCalendar);
                        }

                        /* ????????? */
                        updateWidget();
                        initSettings();
                        hideAddLayout();
                        onResume();

                        if(new Random().nextInt(10)<2) {
                            showAd();
                        }
                        //}
                    }
                    break;
                case R.id.drawer_config:
                    Intent prefIntent = new Intent(MainActivity.this, PreferenceActivity.class);
                    startActivityForResult(prefIntent, REQUEST_TO_PREF_ACTIVITY_CODE);

                    overridePendingTransition(R.anim.fade_in_center, R.anim.fade_out_center);
                    break;
                case R.id.drawer_login:
                    //drawerLayout.closeDrawer(drawerView);

                    if (mAuth.getCurrentUser() == null) {
                        googleBtn.setSize(SignInButton.SIZE_STANDARD);
                        loginDialog.show();
                    } else {
                        Toast.makeText(mContext, "?????? ???????????? ????????? ????????????.\n??????????????? ????????? ?????? \'??????\'??? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.option1:
                    drawerLayout.closeDrawer(drawerView);
                    editor.putInt("SHOW_STATE", 0);
                    editor.putInt("SHOW_IMPO", 0);
                    editor.putInt("SHOW_DONE", 0);
                    editor.commit();
                    invalidateOptionsMenu();
                    onResume();
                    break;
                case R.id.option2:
                    startActivity(new Intent(MainActivity.this, CalendarActivity.class));
                    break;
                case R.id.option3:
                    drawerLayout.closeDrawer(drawerView);
                    //editor.putInt("SHOW_IMPO", 0);
                    if (setting.getInt("SHOW_STATE", 0) == 0) {
                        editor.putInt("SHOW_STATE", 1);
                    } else {
                        editor.putInt("SHOW_STATE", 0);
                    }
                    //editor.putInt("SHOW_DONE", 0);
                    editor.commit();
                    invalidateOptionsMenu();
                    onResume();
                    break;
                case R.id.option4:
                    drawerLayout.closeDrawer(drawerView);
                    //editor.putInt("SHOW_IMPO", 0);
                    //editor.putInt("SHOW_STATE", 0);
                    if (setting.getInt("SHOW_DONE", 0) == 0) {
                        editor.putInt("SHOW_DONE", 1);
                    } else {
                        editor.putInt("SHOW_DONE", 0);
                    }

                    editor.commit();
                    invalidateOptionsMenu();
                    onResume();
                    break;
                case R.id.option5:
                    drawerLayout.closeDrawer(drawerView);
                    if (setting.getInt("SHOW_IMPO", 0) == 0) {
                        editor.putInt("SHOW_IMPO", 1);
                    } else {
                        editor.putInt("SHOW_IMPO", 0);
                    }
                    editor.commit();
                    invalidateOptionsMenu();
                    onResume();
                    break;
                case R.id.option6:
                    drawerLayout.closeDrawer(drawerView);
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    Intent appIntent = new Intent(Intent.ACTION_VIEW);
                    try {
                        appIntent.setData(Uri.parse("market://details?id=" + appPackageName));
                        startActivity(appIntent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        appIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                        startActivity(appIntent);
                    }
                    break;
                case R.id.option7:  // ?????? ????????????
                    ActionBarNotification actionBarNotification = new ActionBarNotification(MainActivity.this);
                    if (actionBarNotification.isExist()) {
                        actionBarNotification.showCustomLayoutNotification();
                        Toast.makeText(MainActivity.this, "????????? ?????? ?????? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "??????, ?????? ??????????????? ????????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.option8:  // ?????? ?????????
                    makingWidgetLayout = getLayoutInflater().inflate(R.layout.widget_making_layout, null);
                    closeWidgetImageView = makingWidgetLayout.findViewById(R.id.widget_close_image_view);

                    widgetMakingDialog = DialogPlus.newDialog(MainActivity.this)
                            .setContentHolder(new ViewHolder(makingWidgetLayout))
                            .setGravity(Gravity.CENTER)
                            .create();

                    closeWidgetImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (widgetMakingDialog.isShowing()) {
                                widgetMakingDialog.dismiss();
                            }
                        }
                    });

                    widgetMakingDialog.show();
                    break;
                case R.id.widget_close_image_view:

                    break;
                default:
                    return;
            }
        }
    };


    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            if (menuItem.getItemId() == 1) {     // ?????? ??????
                selectedYear = todayYear;
                selectedMonth = todayMonth;
                selectedDay = todayDay;
                firstMenuTextView.setText("??????");
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isDaySet = true;
                return true;
            } else if (menuItem.getItemId() == 2) {       // ?????? ??????
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 1);
                String dateStr = dateFormat.format(cal.getTime());
                selectedYear = dateStr.split("-")[0];
                selectedMonth = dateStr.split("-")[1];
                selectedDay = dateStr.split("-")[2];
                firstMenuTextView.setText("??????");
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isDaySet = true;
                return true;
            } else if (menuItem.getItemId() == 3) {       // ????????? ??? ??????
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 7);
                String dateStr = dateFormat.format(cal.getTime());

                selectedYear = dateStr.split("-")[0];
                selectedMonth = dateStr.split("-")[1];
                selectedDay = dateStr.split("-")[2];

                firstMenuTextView.setText(selectedMonth + "??? " + selectedDay + "??? " + nextWeekDayOfWeek);
                firstMenuTextView.setTextSize(12);
                firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);

                isDaySet = true;
                return true;
            } else if (menuItem.getItemId() == 4) {       // ???????????? ?????? ??????
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, dateSetListener,
                        Integer.parseInt(todayYear), Integer.parseInt(todayMonth) - 1, Integer.parseInt(todayDay));
                datePickerDialog.show();
                firstMenuTextView.setTextSize(12);

                return true;
            } else if (menuItem.getItemId() == R.id.alarm_midnight) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "00";
                alarmMin = "00";
                alarmTimeSet("24", alarmMin);
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
                Toast.makeText(mContext, "?????? ??? \'??????\' ????????????", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.alarm_morning) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "08";
                alarmMin = "00";
                alarmTimeSet(alarmHour, alarmMin);
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
                Toast.makeText(mContext, "?????? ??? " + alarmHour + "??? " + alarmMin + "??? ????????????", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.alarm_no) {
                alarmHour = "";
                alarmMin = "";
                secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                secondMenuTextView.setText("?????? ??????");
                secondMenuTextView.setTextSize(12);
                isAlarmSet = true;
            } else if (menuItem.getItemId() == R.id.alarm_user_control) {
                /*TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, timeSetListener, 12, 00, false);
                timePickerDialog.show();*/
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(slideDateTimeListener)
                        .setInitialDate(new Date())
                        .setMinDate(new Date())
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .build()
                        .show();
            } else if (menuItem.getItemId() == R.id.repeat_every_day) {          // howRepeat?????? ??????
                howRepeat = "EVERYDAY";
                thirdMenuTextView.setText("?????? ??????");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            } else if (menuItem.getItemId() == R.id.repeat_every_week) {          // ?????? ????????? '?????? DOW' ?????? -> ???????????? ?????? : ?????? ?????? == ??????array??? ?????????
                howRepeat = "EVERYWEEK";
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String DOW = sdf.format(new Date());
                DOW = getTranslatedKoreanDow(DOW, 1);
                repeatDow = DOW;
                thirdMenuTextView.setText(repeatDow + " ?????? ??????");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            } else if (menuItem.getItemId() == R.id.repeat_every_month) {     // ????????? ????????? '???' ??????
                howRepeat = "EVERYMONTH";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                repeatMonth = "0";
                repeatDay = simpleDateFormat.format(new Date());
                thirdMenuTextView.setText("?????? ??????");
                thirdMenuTextView.setTextSize(12);
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            } else if (menuItem.getItemId() == R.id.repeat_user_control) {
                howRepeat = "CALENDAR";
                howRepeatDialog.show();
            } else if (menuItem.getItemId() == R.id.no_repeat) {
                howRepeat = "NOREPEAT";
                thirdMenuTextView.setText("?????? ??????");
                thirdMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
                isRepeatSet = true;
            }
            return false;
        }
    };


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            selectedYear = Integer.toString(year);

            if (month + 1 >= 10) {
                selectedMonth = Integer.toString(month + 1);
            } else if (month + 1 < 10) {
                selectedMonth = "0" + Integer.toString(month + 1);
            }

            if (day >= 10) {
                selectedDay = Integer.toString(day);
            } else if (day < 10) {
                selectedDay = "0" + Integer.toString(day);
            }


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            calendarDayOfWeek = simpleDateFormat.format(new Date(year, month, day - 1));
            firstMenuTextView.setText(selectedMonth + "??? " + selectedDay + "??? " + getTranslatedKoreanDow(calendarDayOfWeek, 1));
            firstMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            isDaySet = true;
        }
    };


    private SlideDateTimeListener slideDateTimeListener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date) {
            SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            SimpleDateFormat tempFormat2 = new SimpleDateFormat("EEEE");

            Calendar curCalendar = Calendar.getInstance();

            String thisDay = tempFormat.format(date);

            alarmYear = thisDay.split("-")[0];
            alarmMonth = thisDay.split("-")[1];
            alarmDate = thisDay.split("-")[2];
            alarmHour = thisDay.split("-")[3];
            alarmMin = thisDay.split("-")[4];
            Log.d("MainActivity", "onDateTimeSet: " + alarmYear + alarmMonth + alarmDate + alarmHour + alarmMin);

            secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            if (Integer.parseInt(alarmYear) == curCalendar.get(Calendar.YEAR)) {
                secondMenuTextView.setTextSize(11);
                secondMenuTextView.setText(alarmMonth + "??? " + alarmDate + "??? (" + getTranslatedKoreanDow(tempFormat2.format(date), 2) + ")" + "\n" + alarmHour + "??? " + alarmMin + "??? ????????????");
            } else {
                secondMenuTextView.setTextSize(11);
                secondMenuTextView.setText(alarmYear + "??? " + alarmMonth + "??? " + alarmDate + "???" + "\n" + alarmHour + "??? " + alarmMin + "??? ????????????");
            }
            secondMenuTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.0f, getResources().getDisplayMetrics()), 1.0f);
            isAlarmSet = true;
        }

        @Override
        public void onDateTimeCancel() {
            // on canceled
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

            alarmHour = Integer.toString(hourOfDay);
            alarmMin = Integer.toString(minute);

            if (hourOfDay < 10) {
                alarmHour = "0" + alarmHour;
            }
            if (minute < 10) {
                alarmMin = "0" + alarmMin;
            }
            secondMenuTextView.setText(alarmHour + "??? " + alarmMin + "?????? ????????????");
            secondMenuTextView.setTextSize(12);
            secondMenuLayout.setBackgroundResource(R.drawable.custom_setting_layout);
            isAlarmSet = true;
        }
    };


    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (view.getId()) {
                case R.id.favorite_image_view:
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!isFavorite) {       // ???????????? ?????? ???????????? ??????
                            setClickedStar();
                        } else if (isFavorite) {     // ???????????? ?????? ????????? ??????
                            setUnclikedStar();
                        }

                    }
                    break;
                case R.id.first_menu_layout:
                    firstMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                    break;
                case R.id.second_menu_layout:
                    secondMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                    break;
                case R.id.third_menu_layout:
                    thirdMenuLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.menus_layout_click));
                    break;
                case R.id.top_layout:
                    return true;
                case R.id.drawer:
                    return true;
            }
            return false;
        }
    };

    public static String getTranslatedKoreanDow(String DOW, int MODE) {

        switch (MODE) {
            case 1:
                if (DOW.equals("Sunday") || DOW.equals("1") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Monday") || DOW.equals("2") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Tuesday") || DOW.equals("3") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Wednesday") || DOW.equals("4") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Thursday") || DOW.equals("5") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Friday") || DOW.equals("6") || DOW.equals("?????????")) {
                    return "?????????";
                } else if (DOW.equals("Saturday") || DOW.equals("7") || DOW.equals("?????????")) {
                    return "?????????";
                } else {
                    return "NULL";
                }
            case 2:
                if (DOW.equals("Sunday") || DOW.equals("1") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Monday") || DOW.equals("2") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Tuesday") || DOW.equals("3") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Wednesday") || DOW.equals("4") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Thursday") || DOW.equals("5") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Friday") || DOW.equals("6") || DOW.equals("?????????")) {
                    return "???";
                } else if (DOW.equals("Saturday") || DOW.equals("7") || DOW.equals("?????????")) {
                    return "???";
                } else {
                    return "NULL";
                }
            default:
                return "NULL";
        }

    }

    private void showAd(){
        if(mInterstitialAd != null){
            mInterstitialAd.show(this);
        }else{
            Log.d("ERROR", "The interstitialAd wasn't loaded yet..");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {         // ?????? ??????
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {        // ?????? ????????? ??????????????? ??????

        if (setting.getInt("SHOW_STATE", 0) == 1) {       // ????????? ?????? ?????? ????????? ??????
            //menu.findItem(R.id.item_menu1).setTitle("?????? ?????? ??????");
            menu.findItem(R.id.item_menu1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_calendar_icon));
        } else {
            menu.findItem(R.id.item_menu1).setTitle("????????? ??????");
            menu.findItem(R.id.item_menu1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_unselected_calendar_icon));
        }

        if (setting.getInt("SHOW_DONE", 0) == 1) {    // ????????? ????????? ?????? ??????
            //menu.findItem(R.id.item_menu3).setTitle("????????? ?????? ??????");
            menu.findItem(R.id.item_menu3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_done_icon));
        } else {
            menu.findItem(R.id.item_menu3).setTitle("?????? ?????? ??????");
            menu.findItem(R.id.item_menu3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_unselected_done_icon));
        }

        /* ?????? ?????? ?????? */
        if (setting.getBoolean("HOW_SORT", true)) { // ??????????????? ??????
            switch (setting.getInt("SORT_STATE", 4)) {
                case 0:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_up_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 1:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_up_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 2:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_up_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 3:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_up_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                default:    // ??????????????? ?????? ???????????? ?????????????????? ???????????? '????????????'??? ??????
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_unselected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
            }
        } else {  // ??????????????? ??????
            switch (setting.getInt("SORT_STATE", 4)) {
                case 0:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_down_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 1:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_down_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 2:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_down_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                case 3:
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_selected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_down_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
                default:    // ??????????????? ?????? ???????????? ?????????????????? ???????????? '????????????'??? ??????
                    menu.findItem(R.id.item_menu4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_unselected_sort_icon));
                    menu.findItem(R.id.sort0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort1).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort3).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    menu.findItem(R.id.sort4).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.menu_transparent_arrow));
                    break;
            }
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {       // ????????? ???????????? ???
        AlertDialog.Builder builder;
        AlertDialog deleteDialog;

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(drawerView);
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
                break;
            case R.id.item_menu1:   // ????????? ??????
                editor.remove("SHOW_STATE");
                if (setting.getInt("SHOW_STATE", 0) == 1) {
                    editor.putInt("SHOW_STATE", 0);
                } else {
                    item.setChecked(true);
                    editor.putInt("SHOW_STATE", 1);
                }
                editor.commit();
                invalidateOptionsMenu();    // ?????? ?????? ????????????
                onResume();
                break;
            case R.id.item_menu2:   // ?????? ??????
                if (snackbar.isShown()) {
                    snackbar.dismiss();
                }
                View themeLayout = getLayoutInflater().inflate(R.layout.select_theme_layout, null);
                setThemeData();
                themeRecyclerView = (RecyclerView) themeLayout.findViewById(R.id.theme_recycler_view);
                themeRecyclerView.setHasFixedSize(true);
                themeLayoutManager = new LinearLayoutManager(this);
                ((LinearLayoutManager) themeLayoutManager).setOrientation(RecyclerView.HORIZONTAL);
                themeRecyclerView.setLayoutManager(themeLayoutManager);
                themeAdapter = new ThemeRecyclerAdapter(themeDataArrayList, MainActivity.this, MainActivity.this);
                themeRecyclerView.setAdapter(themeAdapter);

                DialogPlus dialogPlus = DialogPlus.newDialog(this)
                        .setContentHolder(new ViewHolder(themeLayout))
                        .setGravity(Gravity.BOTTOM)
                        .setCancelable(true)
                        .create();
                dialogPlus.show();
                break;
            case R.id.item_menu3:   // ?????? ?????? ??????
                editor.remove("SHOW_DONE");
                if (setting.getInt("SHOW_DONE", 0) == 1) {
                    editor.putInt("SHOW_DONE", 0);
                } else {
                    item.setChecked(true);
                    editor.putInt("SHOW_DONE", 1);
                }
                editor.commit();
                invalidateOptionsMenu();    // ?????? ?????? ????????????
                onResume();
                break;
            case R.id.sort0:   // ?????? ?????? : ?????????
                if (setting.getInt("SORT_STATE", 0) == 0) {   // ?????? ?????? ?????? ????????? ???????????? ??????????????? ???????????? ??????
                    editor.putBoolean("HOW_SORT", !setting.getBoolean("HOW_SORT", false));
                } else {
                    editor.remove("SORT_STATE");
                    editor.putInt("SORT_STATE", 0);
                    editor.putBoolean("HOW_SORT", true);
                }
                editor.commit();
                invalidateOptionsMenu();
                onResume();
                break;
            case R.id.sort1:    // ?????? ?????? : ??????
                if (setting.getInt("SORT_STATE", 0) == 1) {   // ?????? ?????? ?????? ????????? ???????????? ??????????????? ???????????? ??????
                    editor.putBoolean("HOW_SORT", !setting.getBoolean("HOW_SORT", false));
                } else {
                    editor.remove("SORT_STATE");
                    editor.putInt("SORT_STATE", 1);
                    editor.putBoolean("HOW_SORT", true);
                }
                editor.commit();
                invalidateOptionsMenu();
                onResume();
                break;
            case R.id.sort2:    // ?????? ?????? : ?????? ??????
                if (setting.getInt("SORT_STATE", 0) == 2) {   // ?????? ?????? ?????? ????????? ???????????? ??????????????? ???????????? ??????
                    editor.putBoolean("HOW_SORT", !setting.getBoolean("HOW_SORT", false));
                } else {
                    editor.remove("SORT_STATE");
                    editor.putInt("SORT_STATE", 2);
                    editor.putBoolean("HOW_SORT", true);
                }
                editor.commit();
                invalidateOptionsMenu();
                onResume();
                break;
            case R.id.sort3:    // ?????? ?????? : ?????? ??????
                if (setting.getInt("SORT_STATE", 0) == 3) {   // ?????? ?????? ?????? ????????? ???????????? ??????????????? ???????????? ??????
                    editor.putBoolean("HOW_SORT", !setting.getBoolean("HOW_SORT", false));
                } else {
                    editor.remove("SORT_STATE");
                    editor.putInt("SORT_STATE", 3);
                    editor.putBoolean("HOW_SORT", true);
                }
                editor.commit();
                invalidateOptionsMenu();
                onResume();
                break;
            case R.id.sort4:    // ?????? ??????
                editor.remove("SORT_STATE");
                editor.putInt("SORT_STATE", 4);
                editor.putBoolean("HOW_SORT", true);
                editor.commit();
                invalidateOptionsMenu();
                onResume();
                break;
            case R.id.delete2:   // ?????? ??????
                builder = new AlertDialog.Builder(MainActivity.this)
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                ActionBarNotification actionBarNotification = new ActionBarNotification(MainActivity.this);
                                Cursor cursor = db.rawQuery("SELECT forSeparatingCreated FROM todo_table WHERE isclear=1", null);

                                while (cursor.moveToNext()) {
                                    if (actionBarNotification.isIncluded(cursor.getString(0))) {
                                        actionBarNotification.cancel();
                                    }
                                }

                                db.execSQL("DELETE FROM todo_table WHERE isclear=1");
                                db.close();
                                onResume();
                            }
                        }).setMessage("'??????' ?????? ????????? ?????? ?????????????????????????");
                deleteDialog = builder.create();
                deleteDialog.show();
                break;
            case R.id.delete3:   // ?????? ??????
                builder = new AlertDialog.Builder(MainActivity.this)
                        .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DBHelper dbHelper = new DBHelper(getApplicationContext());
                                SQLiteDatabase db = dbHelper.getWritableDatabase();

                                ActionBarNotification actionBarNotification = new ActionBarNotification(MainActivity.this);
                                actionBarNotification.cancel();

                                db.execSQL("DELETE FROM todo_table");
                                db.close();
                                onResume();
                            }
                        }).setMessage("'??????' ????????? ?????? ?????????????????????????");
                deleteDialog = builder.create();
                deleteDialog.show();
                break;
            default:
                break;
        }

        return true;
    }

    private void setThemeData() {
        themeDataArrayList.clear();
        TypedArray arrResId = getResources().obtainTypedArray(R.array.themeResId);
        String[] titles = getResources().getStringArray(R.array.themeTitle);

        for (int i = 0; i < arrResId.length(); i++) {
            ThemeData themeData = new ThemeData();
            themeData.setThemeImageResId(arrResId.getResourceId(i, 0));
            themeData.setThemeTitle(titles[i]);
            themeDataArrayList.add(themeData);
        }
    }


    private void loadCheckBox() {
        monCB = howRepeatView.findViewById(R.id.monday_check_box);
        tueCB = howRepeatView.findViewById(R.id.tuesday_check_box);
        wedCB = howRepeatView.findViewById(R.id.wednesday_check_box);
        thurCB = howRepeatView.findViewById(R.id.thursday_check_box);
        friCB = howRepeatView.findViewById(R.id.friday_check_box);
        satCB = howRepeatView.findViewById(R.id.saturday_check_box);
        sunCB = howRepeatView.findViewById(R.id.sunday_check_box);
    }

    private void loadAndSetListenerMenuLayout() {
        firstMenuLayout = findViewById(R.id.first_menu_layout);
        secondMenuLayout = findViewById(R.id.second_menu_layout);
        thirdMenuLayout = findViewById(R.id.third_menu_layout);
        firstMenuLayout.setOnClickListener(clickListener);
        firstMenuLayout.setOnTouchListener(touchListener);
        secondMenuLayout.setOnClickListener(clickListener);
        secondMenuLayout.setOnTouchListener(touchListener);
        thirdMenuLayout.setOnClickListener(clickListener);
        thirdMenuLayout.setOnTouchListener(touchListener);
    }

    private void initSettings() {
        textViewInit();
        initStrings();
        toDoContentInit();
        initCheckBox();
        setUnclikedStar();
        initBoolean();
        initAlarmTime();
    }

    private void initAlarmTime() {
        alarmYear = "";
        alarmMonth = "";
        alarmDate = "";
        alarmHour = "";
        alarmMin = "";
        secondMenuTextView.setTextSize(13);
    }

    private void initCheckBox() {
        monCB.setChecked(false);
        tueCB.setChecked(false);
        wedCB.setChecked(false);
        thurCB.setChecked(false);
        friCB.setChecked(false);
        satCB.setChecked(false);
        sunCB.setChecked(false);
        repeatDow = "";
        toDoDayList.clear();
    }

    public String getDOW(String year, String month, String day, int mode) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = sdf.parse(year + month + day);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String DOW = getTranslatedKoreanDow(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)), mode);

        return DOW;
    }

    private void setThirdTextView() {
        if (howRepeatUnit == 1) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "?????????");
        } else if (howRepeatUnit == 7) {
            if (toDoDayList.isEmpty() || howRepeatEditText.getText().toString().equals("0")) {
                toDoDayList.clear();
                thirdMenuTextView.setText("?????? ??????");
            } else {
                StringBuilder textContent = new StringBuilder(Integer.parseInt(howRepeatEditText.getText().toString()) + "????????? ");
                Iterator<String> iterator = toDoDayList.iterator();

                while (iterator.hasNext()) {
                    textContent.append(iterator.next() + ", ");
                }
                textContent.delete(textContent.length() - 2, textContent.length() - 1);
                thirdMenuTextView.setText(textContent);
            }
        } else if (howRepeatUnit == 30) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "????????????");
        } else if (howRepeatUnit == 365) {
            thirdMenuTextView.setText(Integer.parseInt(howRepeatEditText.getText().toString()) + "?????????");
        }

    }



    /*                   */
    /*   others methods  */
    /*                   */

/*    private View createTabView(String tabName, int idx) {

        String mTabName = tabName;

        View tabView = tabView = LayoutInflater.from(mContext).inflate(R.layout.my_custom_tab, null);;
        ImageView imageView = (ImageView) tabView.findViewById(R.id.tab_image_view);
        TextView textView = (TextView) tabView.findViewById(R.id.tab_name_text_view);

        switch (idx){
            case 0:
                textView.setText(tabName);
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // first tab image
                break;
            case 1:
                textView.setText(tabName);
                imageView.setImageResource(R.drawable.ic_launcher_foreground); // second tab image
                break;
            case 2:
                textView.setText(tabName);
                imageView.setImageResource(R.drawable.calender_icon); // third tab image
                break;
            case 3:
                textView.setText(tabName);
                imageView.setImageResource(R.drawable.calender_icon); // third tab image
                break;
        }
        return tabView;
    }*/

    private void setClickedStar() {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
        favoriteImageView.setImageResource(R.drawable.favorite_icon);
        isFavorite = true;
    }

    private void setUnclikedStar() {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
        favoriteImageView.setImageResource(R.drawable.empty_favorite_icon);
        isFavorite = false;
    }


    private void dbInsert(String toDoContent, boolean isFavorite, String dayToDo, String repeatDOW,
                          String created, String howRepeat, boolean isClear, String alarmYear, String alarmMonth, String alarmDate
            , String alarmHour, String alarmMin, String repeatText, String howRepeatUnit, String memo, String forSeparatingCreated, int dDay, int actionBarNoti) {

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String favoriteChk;
        String clearChk;


        if (isFavorite == true) {
            favoriteChk = "1";
        } else {
            favoriteChk = "0";
        }

        if (isClear == true) {
            clearChk = "1";
        } else {
            clearChk = "0";
        }

        db.execSQL("INSERT INTO todo_table (content, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDate, isFavorite, isclear," +
                        " alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, dDay, actionBarNoti) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new String[]{toDoContent, dayToDo, repeatDOW, created, howRepeat, repeatMonth, repeatDay, favoriteChk, clearChk,
                        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, forSeparatingCreated, Integer.toString(dDay), Integer.toString(actionBarNoti)});

        db.close();

    }

    public String setDeadlineDOW(String when) {
        String dow = "";
        Calendar cal = Calendar.getInstance();
        int dayIndex = cal.get(Calendar.DAY_OF_WEEK) - 1;       // cal.get = ????????? : 1  ~   ????????? : 7

        // dayIndex = ????????? : 0 ~   ????????? : 6
        // weekDay = {"?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????"}
        /*               0         1        2        3         4        5        6         */

        if (when.equals("??????")) {
            dow = weekDay[dayIndex];
        } else if (when.equals("??????")) {
            dow = weekDay[(dayIndex + 1) % 7];
        } else if (when.equals("?????????")) {
            dow = weekDay[dayIndex];
        }
        return dow;
    }

    private void initBoolean() {
        isDaySet = false;
        isAlarmSet = false;
        isRepeatSet = false;
    }

    private void addToDoDOWToArrayList() {
        if (monCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (tueCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (wedCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (thurCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (friCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (satCB.isChecked()) {
            toDoDayList.add("?????????");
        }
        if (sunCB.isChecked()) {
            toDoDayList.add("?????????");
        }
    }

    private void alarmTimeSet(String hour, String minute) {
        if (hour.equals("24") && minute.equals("00")) {
            secondMenuTextView.setText("?????? ?????? ??????");
        }
        secondMenuTextView.setText(hour + "??? " + minute + "??? ????????????");
        secondMenuTextView.setTextSize(12);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(drawerView)) {
            drawerLayout.closeDrawer(drawerView);
            return;
        }

        if (addLinearLayout.getVisibility() == GONE && isKeyBoardView == false) {
            if (!exitDialog.isShowing()) {                    // dialog??? ???????????? ?????? : ?????? ???????????????
                try {
                    exitDialog.show();
                } catch (IllegalStateException e) {

                }
            }
        }
        if (isKeyBoardView) {
            isKeyBoardView = false;
        }
        //super.onBackPressed();
    }


    @Override
    public void onResume() { //onStart??? onCreate ????????? ?????? (Activity??? ???????????? ????????? ?????? ???)
        super.onResume();
        updateWidget();

        todayYear = sdf.format(new Date()).split("-")[0];
        todayMonth = sdf.format(new Date()).split("-")[1];
        todayDay = sdf.format(new Date()).split("-")[2];

        doneDataset.clear();
        notDoneDataset.clear();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ToDoData header = new ToDoData();
        header.setToDoContent("??????");
        header.setViewType(VIEW_TYPE_TITLE);
        notDoneDataset.add(header);

        SHOW_STATE = setting.getInt("SHOW_STATE", 0);
        SORT_STATE = setting.getInt("SORT_STATE", 4);    // default??? : 4 -> ?????? ?????? ???
        SHOW_DONE = setting.getInt("SHOW_DONE", 0);
        SHOW_IMPO = setting.getInt("SHOW_IMPO", 0);
        HOW_SORT = setting.getBoolean("HOW_SORT", true);
        THEME_STYLE = setting.getInt("THEME_STYLE", 0);

        setOptionCheck();


        // ????????? ?????? ??????

        Cursor cursor = null;

        if (SHOW_STATE == 0) {    //????????? ????????? ????????????
            if (SHOW_IMPO == 1) {    // ?????? ?????? ??????????????? ??????
                if (HOW_SORT == true) {   // ??????????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by CAST(isFavorite AS int) asc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by dDay asc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by forSeparatingCreated asc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by forSeparatingCreated asc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table where isclear=? AND isFavorite=?", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                    }
                } else {
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by CAST(isFavorite AS int) desc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by dDay desc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by forSeparatingCreated desc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by alarmYear desc, alarmMonth desc, alarmDate desc, alarmHour desc, alarmMin desc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? AND isFavorite=? order by forSeparatingCreated desc", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table where isclear=? AND isFavorite=?", new String[]{Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                    }
                }
            } else if (SHOW_IMPO == 0) {   // ????????? ???????????????
                if (HOW_SORT == true) {   // ??????????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by CAST(isFavorite AS int) asc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by dDay asc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by forSeparatingCreated asc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by forSeparatingCreated asc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table where isclear=?", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                    }
                } else {
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by CAST(isFavorite AS int) desc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by dDay desc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by forSeparatingCreated desc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by alarmYear desc, alarmMonth desc, alarmDate desc, alarmHour desc, alarmMin desc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE isclear=? order by forSeparatingCreated desc", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table where isclear=?", new String[]{Integer.toString(SHOW_DONE)});
                            break;
                    }
                }
            }

        } else if (SHOW_STATE == 1) {  // '????????? ??????' ????????? ??????
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            if (SHOW_IMPO == 1) {
                if (HOW_SORT == true) {   // ???????????? ????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(isFavorite AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by dDay asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(created AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(created AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=?",
                                    new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                    }
                } else {  // ???????????? ????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(isFavorite AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by dDay desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(created AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by alarmYear desc, alarmMonth desc, alarmDate desc, alarmHour desc, alarmMin desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=? order by CAST(created AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? AND isFavorite=?",
                                    new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE), Integer.toString(SHOW_IMPO)});
                    }
                }
            } else if (SHOW_IMPO == 0) {
                if (HOW_SORT == true) {   // ???????????? ????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(isFavorite AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by dDay asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(created AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(created AS int) asc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=?",
                                    new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                    }
                } else {  // ???????????? ????????? ??????
                    switch (SORT_STATE) {
                        case 0: // ?????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(isFavorite AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 1: // ??????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by dDay desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 2: // ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(created AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 3: // ?????? ????????????
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by alarmYear desc, alarmMonth desc, alarmDate desc, alarmHour desc, alarmMin desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        case 4:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=? order by CAST(created AS int) desc", new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                            break;
                        default:
                            cursor = db.rawQuery("select * from todo_table WHERE (dayToDo=? OR howRepeat=?) AND isclear=?",
                                    new String[]{today, "EVERYDAY", Integer.toString(SHOW_DONE)});
                    }
                }
            }
        }


        while (cursor.moveToNext()) {
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
            data.setActionBarNoti(Integer.parseInt(cursor.getString(20)));
            data.setdDay(calculateDDay(Integer.parseInt(cursor.getString(2).split("-")[0]), Integer.parseInt(cursor.getString(2).split("-")[1]), Integer.parseInt(cursor.getString(2).split("-")[2])));

            notDoneDataset.add(data);
        }

        notDoneAdapter.notifyDataSetChanged();
        db.close();

    }

    /*drawer layout?????? ???????????? ?????? '????????????'??? ?????? ????????? ???????????? ??????*/
    private void setOptionCheck() {

        if (SHOW_STATE == 0 && SHOW_DONE == 0 && SHOW_IMPO == 0) {
            option1Check.setVisibility(VISIBLE);
            option3Check.setVisibility(GONE);
            option4Check.setVisibility(GONE);
            option5Check.setVisibility(GONE);
            return;
        } else {
            option1Check.setVisibility(GONE);
        }

        // ?????? ??? ???
        if (SHOW_STATE == 1) {
            option3Check.setVisibility(VISIBLE);
        } else {
            option3Check.setVisibility(GONE);
        }

        // ??????
        if (SHOW_DONE == 1) {
            option4Check.setVisibility(VISIBLE);
        } else {
            option4Check.setVisibility(GONE);
        }

        // ??????
        if (SHOW_IMPO == 1) {
            option5Check.setVisibility(VISIBLE);
        } else {
            option5Check.setVisibility(GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateWidget();
        Log.d("MainActivity", "onPause: updateWidget()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateWidget();
        Log.d("MainActivity", "onStop: updateWidget()");
    }


    protected void updateWidget() {
        Intent updateIntent = new Intent(getApplicationContext(), MAppWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        MainActivity.this.sendBroadcast(updateIntent);
    }


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this, getResources()) {

            boolean isNoti = false;
            ActionBarNotification actionBarNotification = new ActionBarNotification(MainActivity.this);

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final ToDoData item = notDoneDataset.get(position);

                DBHelper dbHelper = new DBHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                if (actionBarNotification.isIncluded(item.getForSeparatingCreated())) {
                    actionBarNotification.cancel();
                    isNoti = true;
                }

                db.execSQL("DELETE FROM todo_table WHERE forSeparatingCreated=?", new String[]{
                        notDoneDataset.get(position).getForSeparatingCreated()
                });
                notDoneDataset.remove(position);
                notDoneAdapter.notifyDataSetChanged();


                db.close();

                snackbar.setAction("?????? ??????", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        notDoneDataset.add(item);
                        notDoneRecyclerView.scrollToPosition(position);
                        dbInsert(item.getToDoContent(), item.getIsFavroite() == 1, item.getToDoDay(),
                                item.getRepeatDow(), item.getCreated(), item.getHowRepeat(), item.getIsClear() == 1,
                                item.getAlarmYear(), item.getAlarmMonth(), item.getAlarmDate(), item.getAlarmHour(), item.getAlarmMin(), item.getRepeatText(),
                                Integer.toString(item.getHowRepeatUnit()), item.getMemo(), item.getForSeparatingCreated(), item.getdDay(), item.getActionBarNoti());

                        if (isNoti) {
                            actionBarNotification.showCustomLayoutNotification();
                        }

                        onResume();
                    }
                });

                View snackView = snackbar.getView();
                TextView snackTextView = snackView.findViewById(R.id.snackbar_text);

                snackTextView.setTextColor(getResources().getColor(R.color.white));
                snackbar.setActionTextColor(getResources().getColor(R.color.enableIconColor));
                snackView.setBackgroundColor(getResources().getColor(R.color.nightBackgroundColor2));
                snackbar.show();
                updateWidget();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(notDoneRecyclerView);
    }


    @SuppressLint("RestrictedApi")
    public void hideAddLayout() {

        imm.hideSoftInputFromWindow(enterToDoEditText.getWindowToken(), 0);

        Thread hideThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //addLinearLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_invisible));
                fab.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_visible));
                addLinearLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.add_layout_invisible));
            }
        });
        hideThread.start();
        addLinearLayout.setVisibility(GONE);
        fab.setVisibility(VISIBLE);
    }

    private void initStrings() {
        selectedYear = "";
        selectedMonth = "";
        selectedDay = "";                                    // ?????? ??????
        howRepeat = "NOREPEAT";
        howRepeatDayString = "";
        alarmHour = "";
        alarmMin = "";
        repeatMonth = "";
        repeatDay = "";
        repeatDow = "";
        repeatText = "";
        isAlarmSet = false;
        isDaySet = false;
        isRepeatSet = false;
        dowStringBuilder.setLength(0);
        forSeparatingCreated = "";
    }

    private void initAddView() {
        textViewInit();
        favoriteImageView.setImageResource(R.drawable.empty_favorite_icon);
        isFavorite = false;

        firstMenuTextView.setText("?????? ??????");
        secondMenuTextView.setText("?????? ??????");
        thirdMenuTextView.setText("?????? ??????");

        firstMenuTextView.setTextSize(13);
        secondMenuTextView.setTextSize(13);
        thirdMenuTextView.setTextSize(13);

        howRepeatSpinner.setSelection(0);
        howRepeatUnit = 1;
        howRepeatDayInt = 0;
        selectedYear = "";
        selectedMonth = "";
        selectedDay = "";
        alarmHour = "";
        alarmMin = "";
        howRepeat = "";
        howRepeatDayString = "";
    }

    private void textViewInit() {
        enterToDoEditText.setText("");
    }

    private void toDoContentInit() {
        toDoContent = "";
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }


        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }*/
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //EditActivity??? ????????? ??? ?????? CODE
                case REQUEST_TO_EDIT_ACTIVITY_CODE:
                    int newYear, newMonth, newDay;

                    Bundle editBundle = data.getExtras();

                    ToDoData newData = new ToDoData();

                    newYear = editBundle.getInt("year");
                    newMonth = editBundle.getInt("month");
                    newDay = editBundle.getInt("day");
                    String newSec = editBundle.getString("sec");

                    newData.setToDoContent(editBundle.getString("content"));
                    newData.setToDoDay(editBundle.getString("dayString"));
                    //newData.setPriority(editBundle.getInt("priority"));
                    newData.setdDay(calculateDDay(newYear, newMonth, newDay));
                    newData.setCreated(newSec);

                    notDoneDataset.set(editPosition, newData);
                    notDoneAdapter.notifyItemChanged(editPosition);
                    break;
                case RC_SIGN_IN:
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                        if (loginDialog.isShowing()) {
                            loginDialog.dismiss();
                        }
                        Log.d("MainActivity", "Google sign in success");
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w("MainActivity", "Google sign in failed", e);
                        updateUI(null);
                        // ...
                    }
                    break;
                case REQUEST_TO_PREF_ACTIVITY_CODE:
                    onResume();
                    break;
            }
        }
    }


    DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };


    public int calculateDDay(int _year, int _month, int _day) {

        Calendar today = Calendar.getInstance(); // ?????? ??????
        Calendar dDay = Calendar.getInstance(); // d - Day ??????

        dDay.set(_year, _month - 1, _day); // d-day ?????? ??????

        long _dDay = dDay.getTimeInMillis() / 86400000; // 8640000 = 24 * 60 * 60 * 1000 milliseconds
        long _today = today.getTimeInMillis() / 86400000;
        int count = (int) (_dDay - _today); // d - Day

        return count;
    }

    @Nullable
    public static String getHashKey(Context context) {
        final String TAG = "KeyHash";
        String keyHash = null;
        try {
            PackageInfo info =
                    context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = new String(Base64.encode(md.digest(), 0));
                Log.d(TAG, keyHash);
            }
        } catch (Exception e) {
            Log.e("name not found", e.toString());
        }

        if (keyHash != null) {
            return keyHash;
        } else {
            return null;
        }
    }
}

