package com.app.buna.dontdelay.Calendar;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class OnDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Resources res;
    public OnDayDecorator(Resources res)
    {
        date = CalendarDay.today();
        this.res = res;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.1f));
//        view.addSpan(new ForegroundColorSpan(res.getColor(R.color.bg_row_background)));
    }

    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}