package com.app.buna.dontdelay.widget;

public class WidgetItem {

    int _id;
    int isFavorite;
    String content;

    public WidgetItem(){
    }

    public WidgetItem(int _id, String content, int isFavorite){
        this._id = _id;
        this.content = content;
        this.isFavorite = isFavorite;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
