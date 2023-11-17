package com.azhar.fadingtextview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    FadingTextView fadingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fadingTextView = findViewById(R.id.fadingTextView);
        fadingTextView.setTimeout(2, TimeUnit.SECONDS);
        fadingTextView.setTexts(R.array.examples);
    }

}
