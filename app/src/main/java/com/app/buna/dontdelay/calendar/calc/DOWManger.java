package com.app.buna.dontdelay.calendar.calc;

public class DOWManger {

    public DOWManger() {

    }

    public String getTranslatedKoreanDow(String DOW) {

        if(DOW.equals("Sunday") || DOW.equals("1")) {
            return "일요일";
        }else if(DOW.equals("Monday") || DOW.equals("2")) {
            return "월요일";
        }else if(DOW.equals("Tuesday") || DOW.equals("3")) {
            return "화요일";
        }else if(DOW.equals("Wednesday") || DOW.equals("4")) {
            return "수요일";
        }else if(DOW.equals("Thursday") || DOW.equals("5")) {
            return "목요일";
        }else if(DOW.equals("Friday") || DOW.equals("6")) {
            return "금요일";
        }else if(DOW.equals("Saturday") || DOW.equals("7")) {
            return "토요일";
        }else {
            return "NULL";
        }
    }

}
