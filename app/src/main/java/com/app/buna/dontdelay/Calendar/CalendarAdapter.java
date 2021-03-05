package com.app.buna.dontdelay.Calendar;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.dontdelay.Activity.CalendarActivity;
import com.app.buna.dontdelay.AdapterDataRecycler.ToDoData;
import com.app.buna.dontdelay.Etc.GoEditActivity;
import com.app.buna.dontdelay.R;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder>{

    ArrayList<ToDoData> mDataset;
    Context context;
    CalendarActivity calendarActivity;
    public final int REQUEST_TO_EDIT_ACTIVITY_CODE = 3300;
    private DialogPlus dialogPlus;

    public CalendarAdapter(ArrayList<ToDoData> mDataset, Context context, CalendarActivity calendarActivity, DialogPlus dialogPlus){
        this.mDataset = mDataset;
        this.context = context;
        this.calendarActivity = calendarActivity;
        this.dialogPlus = dialogPlus;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout calendarItem;
        private ImageView calendarBar;
        private TextView contentText;

        public MyViewHolder(@NonNull View view) {
            super(view);
            calendarItem = view.findViewById(R.id.calendar_item);
            calendarBar = view.findViewById(R.id.color_bar);
            contentText = view.findViewById(R.id.calendar_content);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_item, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.contentText.setText(mDataset.get(position).getToDoContent());
        holder.calendarBar.setImageDrawable(context.getResources().getDrawable(R.drawable.calendar_bar));
        holder.calendarItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoEditActivity goEditActivity = new GoEditActivity(calendarActivity, mDataset, position, REQUEST_TO_EDIT_ACTIVITY_CODE, context);
                goEditActivity.goToEditActivity();
                if(dialogPlus.isShowing()){
                    dialogPlus.dismiss();
                }
            }
        });

        int size = holder.contentText.getText().toString().length();

        if(size < 5){
            holder.calendarBar.setColorFilter(context.getResources().getColor(R.color.calendarColor1), PorterDuff.Mode.SRC_IN);
        }else if(5 <= size && size <= 10){
            holder.calendarBar.setColorFilter(context.getResources().getColor(R.color.calendarColor2), PorterDuff.Mode.SRC_IN);
        }else if(10< size && size <= 15){
            holder.calendarBar.setColorFilter(context.getResources().getColor(R.color.calendarColor3), PorterDuff.Mode.SRC_IN);
        }else{
            holder.calendarBar.setColorFilter(context.getResources().getColor(R.color.calendarColor4), PorterDuff.Mode.SRC_IN);
        }
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
