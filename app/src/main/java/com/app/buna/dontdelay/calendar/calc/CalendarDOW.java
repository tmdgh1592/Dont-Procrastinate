package com.app.buna.dontdelay.calendar.calc;

public class CalendarDOW {

    private int dowCalNumber = 0;
    private String dow = null;
    private String[] dows = new String[] {
            "일요일",
            "월요일",
            "화요일",
            "수요일",
            "목요일",
            "금요일",
            "토요일",


    };

    public CalendarDOW(int dowCalNumber){
        this.dowCalNumber = dowCalNumber;
        dow = dows[dowCalNumber - 1];
    }

    public String getDow(){
        return dow;
    }

}
