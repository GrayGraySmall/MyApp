package com.example.myapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.myapp.R;

/**
 * Created by asus on 2016/5/1.
 */
public class AddnewActivity extends Activity implements View.OnClickListener{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);         //!!这里还没有改动
    }

    @Override
    public void onClick(View v) {

    }
}
