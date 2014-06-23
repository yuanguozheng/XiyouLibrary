package com.JustYY.xiyoulibrary;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BookDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.book_detail, menu);
        return true;
    }
    
}
