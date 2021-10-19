package com.app.buna.dontdelay.adapter.recycler.data;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.app.buna.dontdelay.activity.MainActivity;
import com.app.buna.dontdelay.R;
import com.app.buna.dontdelay.db.DBHelper;
import com.app.buna.dontdelay.utils.GoEditActivity;
import com.google.android.material.snackbar.Snackbar;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_ITEM;
import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_ITEM2;
import static com.app.buna.dontdelay.adapter.recycler.data.ViewTypeVO.VIEW_TYPE_TITLE;

public class NotDoneRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ToDoData> mDataset;
    private Context context;
    private String clear;
    private MainActivity mainActivity;
    private Calendar calendar;
    private Calendar tomCal;
    private SimpleDateFormat sdf1;
    private Resources res;
    public static int lastPosition = 0;


    public NotDoneRecyclerAdapter(ArrayList<ToDoData> myDataset, Context context, Resources res, MainActivity mainActivity) {
        mDataset = myDataset;
        this.context = context;
        this.mainActivity = mainActivity;
        calendar = Calendar.getInstance();
        tomCal = Calendar.getInstance();
        tomCal.add(Calendar.DATE , 1);
        sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        this.res = res;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        if(viewType == VIEW_TYPE_ITEM){
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.today_not_done_schedule, parent, false);
            return new ItemViewHolder(v);
        }else if(viewType == VIEW_TYPE_TITLE) {
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_title_layout, parent, false);
            return new TitleViewHolder(v);
        }else if(viewType == VIEW_TYPE_ITEM2) {
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.today_done_schedule, parent, false);
            return new ItemViewHolder2(v);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if(holder instanceof ItemViewHolder) {  // 진행중

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            setAnimation(itemViewHolder.itemView, position);

            itemViewHolder.checkBox.setOnCheckedChangeListener(null);

            boolean isClear = false;
            if(mDataset.get(position).getIsClear() == 1){
                isClear = true;
            }

            // repeat 아이콘 보이게할지 여부
            if(!mDataset.get(position).getHowRepeat().equals("NOREPEAT")){
                itemViewHolder.repeatImageView.setVisibility(View.VISIBLE);
            }else{
                itemViewHolder.repeatImageView.setVisibility(View.GONE);
            }


            // alarm 아이콘 보이게할지 여부
            if(!mDataset.get(position).getAlarmYear().equals("") || !mDataset.get(position).getAlarmMonth().equals("")
            || !mDataset.get(position).getAlarmDate().equals("") || !mDataset.get(position).getAlarmHour().equals("")
            || !mDataset.get(position).getAlarmMin().equals("")) {
                itemViewHolder.alarmImageView.setVisibility(View.VISIBLE);
            }else{
                itemViewHolder.alarmImageView.setVisibility(View.GONE);
            }

            OnCheckedListener onCheckedListener = new OnCheckedListener(position);
            OnClickListener onClickListener = new OnClickListener(position, itemViewHolder.favoriteImageView);

            itemViewHolder.contentTextView.setText(mDataset.get(position).getToDoContent());
            itemViewHolder.checkBox.setChecked(isClear);
            itemViewHolder.checkBox.setOnCheckedChangeListener(onCheckedListener.checkedChangeListener);
            String toDoYear = mDataset.get(position).getToDoDay().split("-")[0];
            String toDoMonth = mDataset.get(position).getToDoDay().split("-")[1];
            String toDoDate = mDataset.get(position).getToDoDay().split("-")[2];
            String dow = null;
            try {
                dow = mainActivity.getDOW((toDoYear), (toDoMonth),(toDoDate), 2);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if(isClear == true){
                itemViewHolder.contentTextView.setPaintFlags(itemViewHolder.contentTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                itemViewHolder.contentTextView.setPaintFlags(Paint.HINTING_OFF);
            }
            if(mDataset.get(position).getIsFavroite() == 1) {
                itemViewHolder.favoriteImageView.setImageResource(R.drawable.item_favorite_icon);
            }else{
                itemViewHolder.favoriteImageView.setImageResource(R.drawable.item_empty_favorite_icon);
            }
            itemViewHolder.favoriteImageView.setOnClickListener(onClickListener.clickListener);

            if(!toDoYear.equals(Integer.toString(calendar.get(Calendar.YEAR)))) {        // 날짜가 올해가 아닌 경우
                itemViewHolder.whenTextView.setText(toDoYear + "년 " + toDoMonth +"월 "
                        + toDoDate + "일 (" + dow + ")");
                itemViewHolder.whenTextView.setTextColor(res.getColor(R.color.white));
            }else if(mDataset.get(position).getToDoDay().equals(sdf1.format(new Date()))){           // 날짜가 오늘인 경우
                itemViewHolder.whenTextView.setText("오늘");
                itemViewHolder.whenTextView.setTextColor(res.getColor(R.color.colorAccent));
            }else if(mDataset.get(position).getToDoDay().equals(sdf1.format(tomCal.getTimeInMillis()))) {
                itemViewHolder.whenTextView.setText("내일");
                itemViewHolder.whenTextView.setTextColor(res.getColor(R.color.white));
            }else {     // 날짜가 올해인 경우의 일정
                itemViewHolder.whenTextView.setText(toDoMonth +"월 " + toDoDate +"일 (" + dow + ")"); // 오늘 이후의 날짜인 경우
                itemViewHolder.whenTextView.setTextColor(res.getColor(R.color.white));
            }

        }else if(holder instanceof TitleViewHolder) {   // 타이틀
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.titleTextView.setText(mDataset.get(position).getToDoContent());

        }else if(holder instanceof ItemViewHolder2) {   // 완료

            ItemViewHolder2 itemViewHolder2 = (ItemViewHolder2) holder;

            itemViewHolder2.contentTextView.setText(mDataset.get(position).getToDoContent());
            itemViewHolder2.doneDayTextView.setText(mDataset.get(position).getToDoDay());
            itemViewHolder2.contentTextView.setPaintFlags(itemViewHolder2.contentTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        }
    }



    @Override
    public int getItemViewType(int position) {
        if (mDataset.get(position).getViewType() == VIEW_TYPE_TITLE){
            return VIEW_TYPE_TITLE;
        }else if (mDataset.get(position).getViewType() == VIEW_TYPE_ITEM){
            return VIEW_TYPE_ITEM;
        }else if (mDataset.get(position).getViewType() == VIEW_TYPE_ITEM2){
            return VIEW_TYPE_ITEM2;
        }
        return 0;
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }



    /* my methods */


    public class ItemViewHolder extends ViewHolder {

        TextView contentTextView;
        TextView whenTextView;
        ImageView alarmImageView, repeatImageView, favoriteImageView;
        CheckBox checkBox;
        LinearLayout notDoneView;

        private ItemViewHolder(View v) {
            super(v);
            View view = v;

            contentTextView = view.findViewById(R.id.to_do_text);
            whenTextView = view.findViewById(R.id.when_text_view);
            alarmImageView = view.findViewById(R.id.alarm_image_view);
            repeatImageView = view.findViewById(R.id.repeat_image_view);
            checkBox = view.findViewById(R.id.not_done_check_box);
            favoriteImageView= view.findViewById(R.id.item_favorite_image_view);
            notDoneView = view.findViewById(R.id.not_done_view);

            notDoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // editActivity로 넘어가는 부분
                    GoEditActivity goEditActivity = new GoEditActivity(mainActivity, mDataset, getAdapterPosition(), mainActivity.REQUEST_TO_EDIT_ACTIVITY_CODE, context);
                    goEditActivity.goToEditActivity();
                }
            });


        }
    }

    public class ItemViewHolder2 extends ViewHolder {

        TextView contentTextView;
        TextView whenTextView;
        TextView doneDayTextView;
        CardView cardView;

        private ItemViewHolder2(View v) {
            super(v);
            View view = v;

            contentTextView = view.findViewById(R.id.done_text);
            doneDayTextView = view.findViewById(R.id.done_day_text_view);
            whenTextView = view.findViewById(R.id.done_place_text_view);
            cardView = view.findViewById(R.id.card_view);

        }
    }

    public class TitleViewHolder extends ViewHolder {

        TextView titleTextView;
        LinearLayout titleLayout;


        private TitleViewHolder(View v) {
            super(v);
            View view = v;

            titleTextView = view.findViewById(R.id.title_text_view);
            titleLayout = view.findViewById(R.id.title_layout);
        }

    }

    private void setAnimation(final View viewToAnimate, final int position) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(position > lastPosition) {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.fab_visible);
                    animation.setInterpolator(new DecelerateInterpolator());
                    if(position <= 5)
                        animation.setStartOffset(position * 30);
                    viewToAnimate.startAnimation(animation);
                    lastPosition = position;
                }
            }
        });
    }


    private class OnClickListener {

        private int position;
        private ImageView favoriteImageView;

        public OnClickListener(int position, ImageView favoriteImageView) {
            this.position = position;
            this.favoriteImageView = favoriteImageView;
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.item_favorite_image_view:
                        Log.d("TAG", "onClick: adapter");
                        int isFavorite = mDataset.get(position).getIsFavroite();
                        if (isFavorite == 0) {    // 즐겨찾기 안돼있는 경우 -> 설정시켜주기
                            setClickedStar(favoriteImageView, position);
                        } else if (isFavorite == 1) {   // 즐겨찾기 돼있는 경우 -> 해제시켜주기
                            setUnclikedStar(favoriteImageView, position);
                        }
                        break;
                }
            }
        };
    }




    private class OnCheckedListener {

        private int position;

        public OnCheckedListener(int position) {
            this.position = position;
        }


        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                final boolean isClear = isChecked;
                final CheckBox checkBox = (CheckBox) compoundButton;

                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setMessage("일정을 완료하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeCheckState(isClear, checkBox);
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mainActivity.onResume();
                            }
                        });

                if(isClear == false){
                    changeCheckState(isClear, checkBox);
                }else if(isClear == true){
                    AlertDialog checkDialog = builder.create();
                    checkDialog.show();
                }
            }
        };



        private void changeCheckState(final boolean isClear, final CheckBox checkBox){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    checkBox.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_visible));

                }
            }).start();

            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            clear = (isClear) ? "1" : "0";

            db.execSQL("UPDATE todo_table SET isclear=? WHERE content=? AND dayToDo=? AND forSeparatingCreated=?", new String[] {
                    clear,
                    mDataset.get(position).getToDoContent(),
                    mDataset.get(position).getToDoDay(),
                    mDataset.get(position).getForSeparatingCreated()
            });
            db.close();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainActivity.onResume();
                }
            }, 200);

            DrawerLayout layout = mainActivity.findViewById(R.id.drawer_layout);

            if(isClear){
                Snackbar.make(layout, "일정 완료!  우측 상단의 '완료된 작업 보기'에서 확인하세요.", Snackbar.LENGTH_LONG).show();
            }
        }

    }



    private void setClickedStar(ImageView favoriteImageView, int thisPosition) {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.favorite_image_uncheck));
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.favorite_image_check));
        favoriteImageView.setImageResource(R.drawable.item_favorite_icon);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE todo_table SET isFavorite=? WHERE content=? AND dayToDo=? AND forSeparatingCreated=?", new String[]{
                "1",
                mDataset.get(thisPosition).getToDoContent(),
                mDataset.get(thisPosition).getToDoDay(),
                mDataset.get(thisPosition).getForSeparatingCreated()
        });
        db.close();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainActivity.onResume();
            }
        }, 200);
    }

    private void setUnclikedStar(ImageView favoriteImageView, int thisPosition) {
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.favorite_image_uncheck));
        favoriteImageView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.favorite_image_check));
        favoriteImageView.setImageResource(R.drawable.item_empty_favorite_icon);

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE todo_table SET isFavorite=? WHERE content=? AND dayToDo=? AND forSeparatingCreated=?", new String[]{
                "0",
                mDataset.get(thisPosition).getToDoContent(),
                mDataset.get(thisPosition).getToDoDay(),
                mDataset.get(thisPosition).getForSeparatingCreated()
        });
        db.close();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainActivity.onResume();
            }
        }, 200);
    }





}