package com.example.alpha_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.parceler.Parcels;

public class AdminLogin extends AppCompatActivity {
    String school;
    String phone;

    Admin admin;
    Button man;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Toast.makeText(getApplicationContext(),"Success is coming, Admin",Toast.LENGTH_SHORT).show();

        man=findViewById(R.id.manage_account);

        Parcelable parcelable=getIntent().getParcelableExtra("admin");
        admin= Parcels.unwrap(parcelable);


    }

    public void ManageAccounts(View view) {
        Intent i=new Intent(this,ManageAcc.class);
        Parcelable parcelable= Parcels.wrap(admin);
        i.putExtra("adminM", parcelable);
        startActivity(i);
    }

    public void WatchData(View view) {
        Intent i=new Intent(this,DataView.class);
        Parcelable parcelable= Parcels.wrap(admin);
        i.putExtra("adminW", parcelable);
        startActivity(i);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void Profile(MenuItem item) {
        Intent i = new Intent(this, ChangeProfile.class);
        i.putExtra("type",3);
        Parcelable parcelable= Parcels.wrap(admin);
        i.putExtra("admin", parcelable);
        startActivity(i);
    }

    public void MonitorView(View view) {
        Intent i = new Intent(this, MonitorScreen.class);
        i.putExtra("school",admin.getSchool());
        startActivity(i);
    }
}
