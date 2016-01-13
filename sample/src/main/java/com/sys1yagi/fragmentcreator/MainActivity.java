package com.sys1yagi.fragmentcreator;

import com.sys1yagi.fragmentcreator.fragment.MainFragmentCreator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            MainFragmentCreator.Builder
                                    .newInstance("Hello FragmentCreator!")
                                    .setUserId("mike")
                                    .build())
                    .commit();
        }
    }
}
