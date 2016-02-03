package com.mepsan.callcenter.casa;

import android.graphics.drawable.Drawable;

public class ListViewItem {
    public final Drawable icon;       // the drawable for the ListView item ImageView
    public final Drawable icon2;
    public  String title;        // the text for the ListView item title
    public final String description;  // the text for the ListView item description
    public  String id;

    public ListViewItem(String id,Drawable icon, String title, String description,Drawable icon2) {
        this.icon = icon;
        this.icon2 = icon2;
        this.title = title;
        this.description = description;
        this.id = id;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
         this.title=t;
    }

    public void setId(String t) {
        this.id=t;
    }


    public String getDescription() {
        return description;
    }
}