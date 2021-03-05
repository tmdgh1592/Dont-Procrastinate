package com.app.buna.dontdelay.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
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
import android.widget.Toast;

import com.app.buna.dontdelay.ActionBarNoti.ActionBarNotification;
import com.app.buna.dontdelay.AdapterDataRecycler.NotDoneRecyclerAdapter;
import com.app.buna.dontdelay.Push.AlarmHATT;
import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.CalcCalendar.CalendarDOW;
import com.app.buna.dontdelay.DB.DBHelper;
import com.app.buna.dontdelay.CalcCalendar.DayManager;
import com.app.buna.dontdelay.CalcCalendar.HourType;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class EditActivty extends AppCompatActivity {

    private MainActivity mainActivity;
    private Calendar calendar;
    private SimpleDateFormat sdf;

    private static final int memoRequestCode = 1000;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Spinner howRepeatSpinner;
    private View howRepeatView;
    AlertDialog howRepeatDialog;
    AlertDialog.Builder builder;

    int textLength = 0;
    boolean todOrTmrCheck = false;
    SpannableStringBuilder ssb;

    private EditText howRepeatEditText;
    DayManager dayManager;

    private PopupMenu popupMenu;
    private String todayDayOfWeek, tomorrowDayOfWeek, nextWeekDayOfWeek, calendarDayOfWeek;
    private SpannableStringBuilder dowStringBuilder = new SpannableStringBuilder();
    private ArrayList<String> toDoDayList = new ArrayList<>();
    private CheckBox monCB, tueCB, wedCB, thurCB, friCB, satCB, sunCB;


    /* MainActivity로부터 전달 받은 값이자 전달해야 하는 내용 */

    private Intent getIntent;
    private String toDoContent; // 해야할 일 내용
    private String toDoDay; // format : year-month-date
    private String created;
    private String howRepeat, repeatMonth, repeatDate, repeatDay, repeatDow, repeatText; //repeatDow format : 월요일화요일수요일 || repeatText : addview.사용자지정.text
    private String alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin;
    private String memo = "";
    private int isFavorite, isClear, howRepeatUnit, isFixed;
    private LinearLayout dateLayout, repeatLayout, alarmLayout;
    private RelativeLayout memoLayout;
    private TextView memoTextView;
    private String forSeparatingCreated;

    private int isFixChange;
    private boolean isContentChange = false;

    /* 여기까지 */
    String toDoYear, toDoMonth, toDoDate, toDoDow;
    String dow = null;

    private EditText contentEditText;
    private ImageView favoriteImageView, calendarImageView, repeatImageView, alarmImageView, deleteImageView, backImageView, shareImageView, pinImageView;
    private TextView toDoDayTextView, repeatTextView, alarmTextView, createTextView;

    final Calendar createCal = Calendar.getInstance();

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        setAds();
        initialize();

        deleteImageView.setOnClickListener(clickListener);
        favoriteImageView.setOnClickListener(clickListener);
        favoriteImageView.setOnTouchListener(touchListener);
        dayManager = new DayManager();

    }

    private void setAds(){
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.editAct_adView);
        mAdView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                // 광고가 문제 없이 로드시 출력됩니다.
                Log.d("@@@", "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // 광고 로드에 문제가 있을시 출력됩니다.
                Log.d("@@@", "onAdFailedToLoad " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initialize() {
        intentInit();
        setView();
        createHRDialog();
        loadCheckBox();
    }

    private void intentInit() {

        mainActivity = new MainActivity();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar = Calendar.getInstance();

        getIntent = getIntent();
        toDoContent = getIntent.getStringExtra("content");
        isFavorite = getIntent.getIntExtra("favorite", 0);
        memo = getIntent.getStringExtra("memo");
        toDoDay = getIntent.getStringExtra("toDoDay");
        created = getIntent.getStringExtra("createTime");
        repeatMonth = getIntent.getStringExtra("repeatMonth");
        repeatDate = getIntent.getStringExtra("repeatDate");
        repeatDow = getIntent.getStringExtra("repeatDow");  // format : 월요일화요일수요일 ...
        howRepeat = getIntent.getStringExtra("howRepeat");
        repeatText = getIntent.getStringExtra("repeatText");
        alarmYear = getIntent.getStringExtra("alarmYear");
        alarmMonth = getIntent.getStringExtra("alarmMonth");
        alarmDate = getIntent.getStringExtra("alarmDate");
        alarmHour = getIntent.getStringExtra("alarmHour");
        alarmMin = getIntent.getStringExtra("alarmMin");
        isClear = getIntent.getIntExtra("isClear", 0);
        howRepeatUnit = getIntent.getIntExtra("howRepeatUnit", 1);
        forSeparatingCreated = getIntent.getStringExtra("forSeparatingCreated");
        isFixed = getIntent.getIntExtra("actionBarNoti", 0);
        isFixChange = isFixed;
    }

    private void setView() {
        pinImageView = findViewById(R.id.edit_text_pin);
        shareImageView = findViewById(R.id.edit_text_share);
        deleteImageView = findViewById(R.id.edit_delete_image_view);
        contentEditText = findViewById(R.id.edit_to_do_content_edit_text);
        favoriteImageView = findViewById(R.id.edit_favorite_image_view);
        calendarImageView = findViewById(R.id.edit_calendar_image_view);
        alarmImageView = findViewById(R.id.edit_alarm_image_view);
        repeatImageView = findViewById(R.id.edit_repeat_image_view);
        toDoDayTextView = findViewById(R.id.edit_to_do_day_text_view);
        repeatTextView = findViewById(R.id.edit_repeat_text_view);
        alarmTextView = findViewById(R.id.edit_alarm_text_view);
        dateLayout = findViewById(R.id.edit_date_layout);
        repeatLayout = findViewById(R.id.edit_repeat_layout);
        alarmLayout = findViewById(R.id.edit_alarm_layout);
        createTextView = findViewById(R.id.edit_create_text_view);
        backImageView = findViewById(R.id.back_image_view);
        pinImageView.setOnClickListener(clickListener);
        shareImageView.setOnClickListener(clickListener);
        backImageView.setOnClickListener(clickListener);
        dateLayout.setOnClickListener(clickListener);
        repeatLayout.setOnClickListener(clickListener);
        alarmLayout.setOnClickListener(clickListener);
        memoLayout = findViewById(R.id.memo_layout);
        memoTextView = findViewById(R.id.memo_text_view);
        howRepeatView = getLayoutInflater().inflate(R.layout.custom_how_repeat_dialog, null);
        howRepeatEditText = howRepeatView.findViewById(R.id.how_repeat_edit_text);
        howRepeatSpinner = howRepeatView.findViewById(R.id.how_repeat_spinner);
        howRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 여기
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LinearLayout layout = howRepeatView.findViewById(R.id.day_select_layout);
                switch (position) {
                    case 0:
                        howRepeatUnit = 1;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 1:
                        howRepeatUnit = 7;
                        toDoDayList.clear();
                        layout.setVisibility(View.VISIBLE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_visible));
                        break;
                    case 2:
                        howRepeatUnit = 30;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                    case 3:
                        howRepeatUnit = 365;
                        toDoDayList.clear();
                        layout.setVisibility(View.GONE);
                        layout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.day_select_layout_invisible));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("LogD", "onNothingSelected: nothing selected");
            }
        });

        updatePinImage();

        if (isClear == 1) {
            contentEditText.setPaintFlags(contentEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); }
        if ((isFavorite == 1)) {
            favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
        } else {
            favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
        }

        calendarImageView.setBackgroundResource(R.drawable.enable_edit_calendar_icon);

        try{
            if (!howRepeat.equals("NOREPEAT")) {
                repeatImageView.setBackgroundResource(R.drawable.enable_edit_repeat_icon);
            } else{
                repeatImageView.setBackgroundResource(R.drawable.disabled_edit_repeat_icon);
            }
            if (!alarmYear.equals("")) {
                alarmImageView.setBackgroundResource(R.drawable.enable_edit_alarm_icon);
            } else {
                alarmImageView.setBackgroundResource(R.drawable.disabled_edit_alarm_icon);
            }
        }catch (NullPointerException e){
        }



        contentEditText.setText(toDoContent);

        try {
            toDoYear = toDoDay.split("-")[0];
            toDoMonth = toDoDay.split("-")[1];
            toDoDate = toDoDay.split("-")[2];
            toDoDow = null;
        }catch (NullPointerException e){

        }

        try {
            dow = mainActivity.getDOW((toDoYear), (toDoMonth), (toDoDate), 2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* 기한 날짜 부분 */
        try {
            if (!toDoYear.equals("")) {
                toDoDayTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                if (!toDoYear.equals(Integer.toString(calendar.get(Calendar.YEAR)))) {        // 날짜가 올해가 아닌 경우
                    toDoDayTextView.setText(toDoYear + "년 " + toDoMonth + "월 "
                            + toDoDate + "일 (" + dow + ")");
                } else if (toDoDay.equals(sdf.format(new Date()))) {           // 날짜가 오늘인 경우
                    toDoDayTextView.setText("오늘");
                } else if(toDoYear.equals("")){
                    toDoDayTextView.setText("미정");
                }
                else {     // 날짜가 올해인 경우의 일정
                    toDoDayTextView.setText(toDoMonth + "월 " + toDoDate + "일 (" + dow + ")"); // 오늘 이후의 날짜인 경우
                }
            }
        }catch (NullPointerException e){}




        /*반복 부분*/
        try {
            switch (howRepeat) {
                case "EVERYDAY":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("매일 반복");
                    break;
                case "EVERYWEEK":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("매주 반복");
                    break;
                case "EVERYMONTH":
                    repeatTextView.setTextSize(16);
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                    repeatTextView.setText("매달 반복");
                    break;
                case "CALENDAR":
                    switch (howRepeatUnit) {
                        case 1:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "일마다 반복");
                            break;
                        case 7:
                            repeatTextView.setTextSize(14);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);

                            ssb = new SpannableStringBuilder(repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 "));
                            textLength = repeatText.length() + "주마다\n".length();
                            // textLength ~ 문장 마지막 까지 회색으로 색상 지정
                            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.defaultDisableColor)),
                                    textLength, (repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            ssb.setSpan(new AbsoluteSizeSpan(16, true), textLength, (repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            repeatTextView.setText(ssb);
                            break;
                        case 30:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "개월마다 반복");
                            break;
                        case 365:
                            repeatTextView.setTextSize(16);
                            repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
                            repeatTextView.setText(repeatText + "년마다 반복");
                            break;
                    }
                    break;
                case "NOREPEAT":
                    repeatTextView.setText("반복");
                    repeatTextView.setTextColor(ContextCompat.getColor(this, R.color.defaultDisableColor));
                    break;
            }
        }catch (NullPointerException e){}

//        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin;
        try{
        if (!alarmMin.equals("")) {
            // Toast.makeText(this, alarmYear +"-" + alarmMonth + "-" + alarmDate +"\n" + alarmHour + "시" + alarmMin+"분", Toast.LENGTH_LONG).show();
            alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.enableIconColor));
            String todayYear = sdf.format(new Date()).split("-")[0];
            String alarmTimeText = null;
            String alarmDayText = null;
            String hourType = "오전"; //오전 or 오후
            String expressingHour = null;

            if(!alarmHour.equals("") || !alarmMin.equals("")) {
                HourType ht = new HourType(Integer.parseInt(alarmHour), Integer.parseInt(alarmMin));
                hourType = ht.getHourType();
                expressingHour = ht.getTranslatedHour();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date date = null;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(alarmYear + "-" + alarmMonth + "-" + alarmDate + " " + alarmHour + ":" + alarmMin + ":" + "00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (hourType.equals("자정")) {
                alarmTimeText = "'" + hourType + "'에 알림\n";
            } else {
                alarmTimeText = hourType + " " + expressingHour + "시 " + alarmMin + "분에 알림\n";
            }

            // 알람 날짜가 '오늘', '내일'인 경우
            Calendar todCal = Calendar.getInstance();
            Calendar tmrCal = Calendar.getInstance();
            tmrCal.add(Calendar.DATE, 1);
            if ((new SimpleDateFormat("yyyyMMdd").format(todCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "오늘";
                todOrTmrCheck = true;

            } else if ((new SimpleDateFormat("yyyyMMdd").format(tmrCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "내일";
                todOrTmrCheck = true;
            }

            if (todOrTmrCheck == false) {     // 알람 날짜가 '오늘', '내일'이 아닌 경우
                if (alarmYear.equals(todayYear)) {     // 알람 설정이 "올 해" 인 경우
                    alarmDayText = alarmMonth + "월 " + alarmDate + "일 (" + mainActivity.getTranslatedKoreanDow(sdf.format(date), 2) + ")";
                } else {      // 올해가 아닌 경우
                    alarmDayText = alarmYear + "년 " + alarmMonth + "월 " + alarmDate + "일 (" + mainActivity.getTranslatedKoreanDow(sdf.format(date), 2) + ")";
                }
            }
            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
            textLength = alarmTimeText.length();
            ssb = new SpannableStringBuilder(alarmTimeText + alarmDayText);
            ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.nightBackgroundColor)), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alarmTextView.setText(ssb);
        } else {  // 알림 설정 안돼 있는 경우
            alarmTextView.setText("푸시 알림");
            alarmTextView.setTextColor(ContextCompat.getColor(this, R.color.defaultDisableColor));
            alarmImageView.setImageResource(R.drawable.disabled_edit_alarm_icon);
        }}catch (NullPointerException e){}


        /* 메모 부분 */

        try{
        memoLayout.setOnClickListener(clickListener);
        if(!memo.equals("")){
            memoTextView.setText(memo);
        }}catch (NullPointerException e){}

        /* 생성 일자 부분 */
        String createdString = created;
        StringBuilder stringBuilder = new StringBuilder("");

        try {
            createCal.setTime(sdf.parse(createdString));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e){

        }

        try {
            if (!createdString.split("-")[0].equals(sdf.format(new Date()).split("-")[0])) {      // 생성 날짜가 올해가 아닌 경우 '년도'까지 표시
                stringBuilder.append(createdString.split("-")[0] + "년 ")
                        .append(createdString.split("-")[1] + "월 ")
                        .append(createdString.split("-")[2] + "일 ")
                        .append("(" + new CalendarDOW(createCal.get(Calendar.DAY_OF_WEEK)).getDow().replace("요일", "") + ")")
                        .append("에 생성됨");


            } else {                                                                                          // 생성 날짜가 올해인 경우 '년도' 생략
                stringBuilder.append(createdString.split("-")[1] + "월 ")
                        .append(createdString.split("-")[2] + "일 ")
                        .append("(" + new CalendarDOW(createCal.get(Calendar.DAY_OF_WEEK)).getDow().replace("요일", "") + ")")
                        .append("에 생성됨");
            }

            createTextView.setText(stringBuilder.toString());
            stringBuilder.setLength(0);
        }catch (NullPointerException e){}

    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

            switch (view.getId()) {
                case R.id.edit_text_share:
                    shareContent(contentEditText.getText().toString(), memo);
                    break;
                case R.id.edit_delete_image_view:
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivty.this)
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                                    ActionBarNotification actionBarNotification = new ActionBarNotification(EditActivty.this);

                                    if(actionBarNotification.isIncluded(forSeparatingCreated)){
                                        actionBarNotification.cancel();
                                    }

                                    db.execSQL("DELETE FROM todo_table WHERE forSeparatingCreated=? AND content=?", new String[]{
                                            forSeparatingCreated, toDoContent
                                    });
                                    db.close();
                                    finish();
                                    NotDoneRecyclerAdapter.lastPosition--;

                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setMessage(toDoContent + "을(를) 삭제하시겠습니까?");
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                        }
                    });
                    alertDialog.show();
                    break;
                case R.id.edit_favorite_image_view:
                    if (isFavorite == 1) {
                        favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
                        isFavorite = 0;
                    } else {
                        favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
                        isFavorite = 1;
                    }
                    break;
                case R.id.memo_layout:
                    Intent intent = new Intent(EditActivty.this, MemoActivity.class);
                    intent.putExtra("memo", memo);
                    startActivityForResult(intent, memoRequestCode);
                    break;
                case R.id.edit_date_layout:
                    getMenuInflater().inflate(R.menu.add_first_menu, popupMenu.getMenu());
                    todayDayOfWeek = mainActivity.setDeadlineDOW("오늘");
                    tomorrowDayOfWeek = mainActivity.setDeadlineDOW("내일");
                    nextWeekDayOfWeek = mainActivity.setDeadlineDOW("다음주");
                    popupMenu.getMenu().add(Menu.NONE, 1, 0, "오늘 (" + todayDayOfWeek + ")");  //groupId, itemId, order, title
                    popupMenu.getMenu().add(Menu.NONE, 2, 1, "내일 (" + tomorrowDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 3, 2, "다음 주 (" + nextWeekDayOfWeek + ")");
                    popupMenu.getMenu().add(Menu.NONE, 4, 3, "날짜 선택");
                    popupMenu.show();
                    break;
                case R.id.edit_alarm_layout:
                    getMenuInflater().inflate(R.menu.add_second_menu, popupMenu.getMenu());
                    popupMenu.show();
                    break;
                case R.id.edit_repeat_layout:
                    getMenuInflater().inflate(R.menu.add_third_menu, popupMenu.getMenu());
                    toDoDayList.clear();
                    popupMenu.show();
                    break;
                case R.id.back_image_view:
                    onBackPressed();
                    break;
                case R.id.edit_text_pin:
                    updateFix(false);
                    break;
            }
        }
    };

    private void updateFix(boolean isBack) {

        ActionBarNotification actionBarNotification = new ActionBarNotification(EditActivty.this);

        if(isBack){
            actionBarNotification.showCustomLayoutNotification();
            return;
        }


        DBHelper dbHelper = new DBHelper(EditActivty.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        if(isFixed == 0) {
            /* 상단 탭 고정갯수 확인하기*/

            if(actionBarNotification.isExist()){
                db.execSQL("UPDATE todo_table SET actionBarNoti=? WHERE actionBarNoti=?",
                        new String[]{"0", "1"});
            }

            /* 실제 고정 상태 변경 */
            isFixed = 1;
            db.execSQL("UPDATE todo_table SET actionBarNoti=?, content=? WHERE forSeparatingCreated=? AND content=?",
                    new String[]{"1", contentEditText.getText().toString(), forSeparatingCreated, toDoContent});
            updatePinImage();

            //actionBarNotification.cancel();
            actionBarNotification.showCustomLayoutNotification();

        }else if(isFixed == 1){
            isFixed = 0;
            db.execSQL("UPDATE todo_table SET actionBarNoti=?, content=? WHERE forSeparatingCreated=? AND content=?",
                    new String[]{"0", contentEditText.getText().toString(), forSeparatingCreated, toDoContent});
            updatePinImage();

            actionBarNotification.cancel();
        }
        db.close();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (view.getId()) {
                case R.id.edit_favorite_image_view:
                    if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                        if (isFavorite == 1) {
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
                        } else {
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_uncheck));
                        }
                    } else if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        if (isFavorite == 1) {
                            favoriteImageView.setBackgroundResource(R.drawable.empty_favorite_icon);
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
                            isFavorite = 0;
                        } else {
                            favoriteImageView.setBackgroundResource(R.drawable.favorite_icon);
                            favoriteImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.favorite_image_check));
                            isFavorite = 1;
                        }
                    }
            }
            return true;
        }
    };

    @Override
    protected void onStop() {
        saveData();
        super.onStop();
    }

    public void createHRDialog() {
        builder = new AlertDialog.Builder(EditActivty.this);


        builder.setView(howRepeatView)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });

        howRepeatDialog = builder.create();
        howRepeatDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posBtn = howRepeatDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button negBtn = howRepeatDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                negBtn.setTextColor(Color.GRAY);
                posBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dowStringBuilder = new SpannableStringBuilder();
                        howRepeat = "CALENDAR";
                        addToDoDOWToArrayList();
                        Iterator<String> iterator = toDoDayList.iterator();
                        while (iterator.hasNext()) {
                            dowStringBuilder.append(iterator.next());           // 월요일화요일수요일
                        }
                        repeatDow = dowStringBuilder.toString();
                        if (howRepeatEditText.getText().toString().equals("0")) {
                            Toast.makeText(getApplicationContext(), "0보다 크게 입력해주세요.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        } else if (howRepeatUnit == 7 && toDoDayList.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "요일을 선택해주세요.", Toast.LENGTH_SHORT).show();
                            toDoDayList.clear();
                        } else {
                            repeatText = howRepeatEditText.getText().toString();
                            if (howRepeatUnit == 1) {
                                ssb = new SpannableStringBuilder(repeatText + "일마다");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "개월마다").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            } else if (howRepeatUnit == 7) {
                                repeatTextView.setTextSize(14);
                                ssb = new SpannableStringBuilder(repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 "));

                                // textLength ~ 문장 마지막 까지 회색으로 색상 지정
                                textLength = (repeatText + "주마다\n").length();
                                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)),
                                        textLength, (repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), textLength, (repeatText + "주마다\n" + repeatDow.replaceAll("요일", "요일 ")).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                                repeatTextView.setText(ssb);
                            } else if (howRepeatUnit == 30) {
                                ssb = new SpannableStringBuilder(repeatText + "개월마다");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "개월마다").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            } else if (howRepeatUnit == 365) {
                                ssb = new SpannableStringBuilder(repeatText + "년마다");
                                ssb.setSpan(new AbsoluteSizeSpan(16, true), 0, (repeatText + "개월마다").length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                repeatTextView.setText(ssb);
                                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            }

                            repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                            repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);

                            howRepeatDialog.dismiss();
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
            }
        });
        howRepeatDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case memoRequestCode:
                    Bundle bundle = data.getExtras();
                    memo = bundle.getString("memo");
                    memoTextView.setText(memo);
                    if (memo.equals("")) {
                        memoTextView.setText("메모장");
                    }
            }
        } else {
            Toast.makeText(this, getString(R.string.memoError), Toast.LENGTH_SHORT).show();
        }
    }

    PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {


            if (menuItem.getItemId() == 1) {     // 오늘 선택
                toDoYear = dateFormat.format(new Date()).split("-")[0];
                toDoMonth = dateFormat.format(new Date()).split("-")[1];
                toDoDate = dateFormat.format(new Date()).split("-")[2];
                toDoDayTextView.setText("오늘");
                return true;
            } else if (menuItem.getItemId() == 2) {       // 내일 선택
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 1);
                String dateStr = dateFormat.format(cal.getTime());
                toDoYear = dateStr.split("-")[0];
                toDoMonth = dateStr.split("-")[1];
                toDoDate = dateStr.split("-")[2];
                toDoDayTextView.setText("내일");
                return true;
            } else if (menuItem.getItemId() == 3) {       // 일주일 후 선택
                Calendar cal = Calendar.getInstance();
                cal.add(cal.DATE, 7);
                String dateStr = dateFormat.format(cal.getTime());

                toDoYear = dateStr.split("-")[0];
                toDoMonth = dateStr.split("-")[1];
                toDoDate = dateStr.split("-")[2];
                toDoDayTextView.setText(toDoMonth + "월 " + toDoDate + "일 (" + nextWeekDayOfWeek.replace("요일", "") + ")");
                return true;
            } else if (menuItem.getItemId() == 4) {       // 캘린더로 지정 선택
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditActivty.this, dateSetListener,
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[0]),
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[1]) - 1,
                        Integer.parseInt(dateFormat.format(new Date()).split("-")[2]));
                datePickerDialog.show();

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

                String alarmTimeText = "'자정'에 알림\n";

                alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                textLength = alarmTimeText.length();
                ssb = new SpannableStringBuilder(alarmTimeText + "내일");
                ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)), textLength, (alarmTimeText + "내일").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + "내일").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alarmTextView.setText(ssb);
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_morning) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                String when = dateFormat.format(cal.getTime());
                alarmYear = when.split("-")[0];
                alarmMonth = when.split("-")[1];
                alarmDate = dayManager.getAddNumDay(Integer.parseInt(when.split("-")[2])) + Integer.parseInt(when.split("-")[2]);
                alarmHour = "08";
                alarmMin = "00";

                String alarmTimeText = "오전 8시 00분에 알림\n";

                alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
                textLength = alarmTimeText.length();
                ssb = new SpannableStringBuilder(alarmTimeText + "내일");
                ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor)), textLength, (alarmTimeText + "내일").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + "내일").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                alarmTextView.setText(ssb);
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_no) {
                alarmYear = "";
                alarmMonth = "";
                alarmDate = "";
                alarmHour = "";
                alarmMin = "";
                alarmTextView.setText("푸시 알림");
                alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor));
                alarmImageView.setImageResource(R.drawable.disabled_edit_alarm_icon);

            } else if (menuItem.getItemId() == R.id.alarm_user_control) {         // 사용자 지정
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(slideDateTimeListener)
                        .setMinDate(new Date())
                        .setTheme(SlideDateTimePicker.HOLO_LIGHT)
                        .setInitialDate(new Date())
                        .build()
                        .show();

            } else if (menuItem.getItemId() == R.id.repeat_every_day) {          // howRepeat으로 구분
                howRepeat = "EVERYDAY";
                repeatTextView.setText("매일 반복");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_every_week) {          // 매주 반복은 '요일 DOW' 저장 -> 불러올때 로직 : 오늘 요일 == 요일array에 있는지
                howRepeat = "EVERYWEEK";
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                String DOW = sdf.format(new Date());
                DOW = mainActivity.getTranslatedKoreanDow(DOW, 1);
                repeatDow = DOW;
                repeatTextView.setText("매주 반복");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_every_month) {     // 한달에 한번은 '일' 저장
                howRepeat = "EVERYMONTH";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
                repeatMonth = "0";
                repeatDay = simpleDateFormat.format(new Date());
                repeatTextView.setText("매달 반복");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
                repeatImageView.setImageResource(R.drawable.enable_edit_repeat_icon);
            } else if (menuItem.getItemId() == R.id.repeat_user_control) {
                howRepeat = "CALENDAR";
                howRepeatDialog.show();
            } else if (menuItem.getItemId() == R.id.no_repeat) {
                howRepeat = "NOREPEAT";
                repeatTextView.setText("반복");
                repeatTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.defaultDisableColor));
                repeatImageView.setImageResource(R.drawable.disabled_edit_repeat_icon);
            }
            return false;
        }
    };


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            toDoYear = Integer.toString(year);

            if (month + 1 >= 10) {
                toDoMonth = Integer.toString(month + 1);
            } else if (month + 1 < 10) {
                toDoMonth = "0" + Integer.toString(month + 1);
            }

            if (day >= 10) {
                toDoDate = Integer.toString(day);
            } else if (day < 10) {
                toDoDate = "0" + Integer.toString(day);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            calendarDayOfWeek = simpleDateFormat.format(new Date(year, month, day - 1));
            toDoDayTextView.setText(toDoMonth + "월 " + toDoDate + "일 (" + mainActivity.getTranslatedKoreanDow(calendarDayOfWeek, 2) + ")");
        }
    };


    private SlideDateTimeListener slideDateTimeListener = new SlideDateTimeListener() {     // 알림수정  alarmDayText <-null

        @Override
        public void onDateTimeSet(Date date) {
            SimpleDateFormat tempFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

            String thisDay = tempFormat.format(date);

            alarmYear = thisDay.split("-")[0];
            alarmMonth = thisDay.split("-")[1];
            alarmDate = thisDay.split("-")[2];
            alarmHour = thisDay.split("-")[3];
            alarmMin = thisDay.split("-")[4];
            todOrTmrCheck = false;


            alarmTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.enableIconColor));
            alarmImageView.setImageResource(R.drawable.enable_edit_alarm_icon);
            String todayYear = tempFormat.format(new Date()).split("-")[0];
            String alarmTimeText = null;
            String alarmDayText = null;
            String hourType = "오전"; //오전 or 오후
            String expressingHour = null;

            HourType ht = new HourType(Integer.parseInt(alarmHour), Integer.parseInt(alarmMin));
            hourType = ht.getHourType();
            expressingHour = ht.getTranslatedHour();

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
            Date date2 = null;

            try {
                date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(alarmYear + "-" + alarmMonth + "-" + alarmDate + " " + alarmHour + ":" + alarmMin + ":" + "00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (hourType.equals("자정")) {
                alarmTimeText = "'" + hourType + "'에 알림\n";
            } else {
                alarmTimeText = hourType + " " + expressingHour + "시 " + alarmMin + "분에 알림\n";
            }

            // 알람 날짜가 '오늘', '내일'인 경우
            Calendar todCal = Calendar.getInstance();
            Calendar tmrCal = Calendar.getInstance();
            tmrCal.add(Calendar.DATE, 1);
            if ((new SimpleDateFormat("yyyyMMdd").format(todCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "오늘";
                todOrTmrCheck = true;

            } else if ((new SimpleDateFormat("yyyyMMdd").format(tmrCal.getTime())).equals((alarmYear + alarmMonth + alarmDate))) {
                alarmDayText = "내일";
                todOrTmrCheck = true;
            }

            if (todOrTmrCheck == false) {     // 알람 날짜가 '오늘', '내일'이 아닌 경우
                if (alarmYear.equals(todayYear)) {     // 알람 설정이 "올 해" 인 경우
                    alarmDayText = alarmMonth + "월 " + alarmDate + "일 (" + mainActivity.getTranslatedKoreanDow(sdf.format(date2), 2) + ")";
                } else {      // 올해가 아닌 경우
                    alarmDayText = alarmYear + "년 " + alarmMonth + "월 " + alarmDate + "일 (" + mainActivity.getTranslatedKoreanDow(sdf.format(date2), 2) + ")";
                }
            }
            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);
            textLength = alarmTimeText.length();
            ssb = new SpannableStringBuilder(alarmTimeText + alarmDayText);
            ssb.setSpan(new AbsoluteSizeSpan(15, true), 0, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.nightBackgroundColor)), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new AbsoluteSizeSpan(13, true), textLength, (alarmTimeText + alarmDayText).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            alarmTextView.setText(ssb);

            alarmTextView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6.0f, getResources().getDisplayMetrics()), 1.0f);

        }

        @Override
        public void onDateTimeCancel() {
            // on canceled
        }
    };

    private void shareContent(String toDoMessage, String toDoMemo){
        StringBuilder message = new StringBuilder();
        message.append("• 일정 : " + toDoMessage +"\n");

        if(!toDoMemo.equals("") || toDoMemo != null){ /* 메모가 작성되어 있는 경우 */
            message.append("• 메모 : " + toDoMemo);
        }

        try{
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, message.toString());
        intent.setPackage("com.kakao.talk");
        startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Uri uriMarket = Uri.parse("market://details?id=com.kakao.talk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriMarket);
            startActivity(intent);
        }
    }

    private void addToDoDOWToArrayList() {
        toDoDayList.clear();
        if (monCB.isChecked()) {
            toDoDayList.add("월요일");
        }
        if (tueCB.isChecked()) {
            toDoDayList.add("화요일");
        }
        if (wedCB.isChecked()) {
            toDoDayList.add("수요일");
        }
        if (thurCB.isChecked()) {
            toDoDayList.add("목요일");
        }
        if (friCB.isChecked()) {
            toDoDayList.add("금요일");
        }
        if (satCB.isChecked()) {
            toDoDayList.add("토요일");
        }
        if (sunCB.isChecked()) {
            toDoDayList.add("일요일");
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

    @Override
    public void onBackPressed() {
        if(contentEditText.getText().toString().length() == 0){
            Toast.makeText(EditActivty.this, "일정을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
        saveData();

        if(isContentChange && isFixChange == 1){
            updateFix(true);
        }
    }

    private void updatePinImage(){
        if(isFixed == 0){
            pinImageView.setColorFilter(getResources().getColor(R.color.unfixed_pin_color));
        }else if(isFixed == 1){
            pinImageView.setColorFilter(getResources().getColor(R.color.fixed_pin_color));
        }
    }

    private void saveData(){
        String content = contentEditText.getText().toString();
        if(!content.equals(toDoContent)){
            isContentChange = true;
        }
        toDoDay = toDoYear+"-"+toDoMonth+"-"+toDoDate;

        updateDB(content, isFavorite, toDoDay, repeatDow, howRepeat, isClear, alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, howRepeatUnit, memo, isFixed);
        if(!alarmYear.equals("")){
            Calendar alarmCalendar = Calendar.getInstance();
            int alarmId = Integer.parseInt(forSeparatingCreated.substring(7, forSeparatingCreated.length()-1));
            // 한 달 빼줘야 정확한 알람 시간이 됌
            // calendar는 0 ~ 11
            alarmCalendar.set(Integer.parseInt(alarmYear),
                    Integer.parseInt(alarmMonth) - 1, Integer.parseInt(alarmDate),
                    Integer.parseInt(alarmHour), Integer.parseInt(alarmMin), 00);
            AlarmHATT alarmHATT = new AlarmHATT(getApplicationContext(), alarmId);
            alarmHATT.alarmManagerCancel(alarmId);
            alarmHATT.alarm(alarmCalendar);
        }
    }



    private void updateDB(String content, int isFavorite, String dayToDo, String repeatDOW,
                          String howRepeat, int isClear, String alarmYear, String alarmMonth, String alarmDate
            , String alarmHour, String alarmMin, String repeatText, int howRepeatUnit, String memo, int actionBarNoti) {

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE todo_table SET content=?, dayToDo=?, repeatDOW=?, howRepeat=?, repeatMonth=?, repeatDate=?, isFavorite=?, isclear=?," +
                "alarmYear=?, alarmMonth=?, alarmDate=?, alarmHour=?, alarmMin=?, repeatText=?, howRepeatUnit=?, memo=?, actionBarNoti=?" +
                        "WHERE forSeparatingCreated=? AND content=?",
                new String[] {content, dayToDo, repeatDOW, howRepeat, repeatMonth, repeatDate, Integer.toString(isFavorite), Integer.toString(isClear),
        alarmYear, alarmMonth, alarmDate, alarmHour, alarmMin, repeatText, Integer.toString(howRepeatUnit), memo, String.valueOf(actionBarNoti), forSeparatingCreated, toDoContent});

        db.close();

    }
}


