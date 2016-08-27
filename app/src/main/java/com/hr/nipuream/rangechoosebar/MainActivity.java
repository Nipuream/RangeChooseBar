package com.hr.nipuream.rangechoosebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements RangeChooseBar.RangeChooseListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RangeChooseBar bar = (RangeChooseBar) findViewById(R.id.rangechoosebar);
        bar.setOnRangeChooseListener(this);
    }

    @Override
    public void chooseResult(int min, int max) {
        Toast.makeText(this,"Min-->"+min + "　"+"Max-->"+max,Toast.LENGTH_SHORT).show();
    }
}
