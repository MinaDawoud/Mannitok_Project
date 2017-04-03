package com.example.pascal.Mannitok;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button user, marchand, entreprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        user = (Button) findViewById(R.id.user);
        marchand = (Button) findViewById(R.id.marchand);
        entreprise = (Button) findViewById(R.id.entreprise);


        user.setOnClickListener(this);
        marchand.setOnClickListener(this);
        entreprise.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        Intent i = null;

        switch (view.getId()) {

            case R.id.user:
                i = new Intent(MainActivity.this, UserActivity.class);
                break;

            case R.id.marchand:
                i = new Intent(MainActivity.this, MarchandActivity.class);
                break;

            case R.id.entreprise:
                i = new Intent(MainActivity.this, EntrepriseActivity.class);
                break;
        }

        startActivity(i);
    }
}
