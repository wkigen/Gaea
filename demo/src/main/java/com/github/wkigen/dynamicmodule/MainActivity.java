package com.github.wkigen.dynamicmodule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.wkigen.gaea.RefInvoke;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView)findViewById(R.id.tv_main);

        try {
            String holle = (String) RefInvoke.invokeStaticMethod("com.github.wkigen.helloandroid.Hello","hello",null,null);
            textView.setText(holle);
        }catch (Exception e){

        }

    }



}
