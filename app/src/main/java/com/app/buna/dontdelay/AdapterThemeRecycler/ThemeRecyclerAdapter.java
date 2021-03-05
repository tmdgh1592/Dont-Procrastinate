package com.app.buna.dontdelay.AdapterThemeRecycler;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.buna.dontdelay.Activity.MainActivity;
import com.app.buna.dontdelay.CustomView.CircleImageView;
import com.app.buna.dontdelay.R;

import java.util.ArrayList;

public class ThemeRecyclerAdapter extends RecyclerView.Adapter<ThemeRecyclerAdapter.MyViewHolder>{

    ArrayList<ThemeData> mDataset;
    Context context;
    MainActivity mainActivity;
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    public ThemeRecyclerAdapter(ArrayList<ThemeData> mDataset, Context context, MainActivity mainActivity){
        this.mDataset = mDataset;
        this.context = context;
        this.mainActivity = mainActivity;
        setting = context.getSharedPreferences("setting", 0);
        editor = setting.edit();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView themeImageView;
        private TextView themeTextView;
        private RelativeLayout themeItemView;
        private CheckBox themeCheckbox;

        public MyViewHolder(@NonNull View view) {
            super(view);
            themeImageView = view.findViewById(R.id.theme_item_image_view);
            themeTextView = view.findViewById(R.id.theme_item_name);
            themeItemView = view.findViewById(R.id.theme_item_view);
            themeCheckbox = view.findViewById(R.id.theme_check_box);
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_list_item, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ThemeData data = mDataset.get(position);
        holder.themeImageView.setImageResource(data.getThemeImageResId());
        holder.themeTextView.setText(data.getThemeTitle());
        holder.themeItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.themeCheckbox.isChecked() && setting.getInt("THEME_STYLE", 0) != position){
                    holder.themeCheckbox.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_visible));
                }else{
                    holder.themeCheckbox.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_invisible));
                }
                editor.putInt("THEME_STYLE", position);
                editor.commit();
                mainActivity.onResume();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
