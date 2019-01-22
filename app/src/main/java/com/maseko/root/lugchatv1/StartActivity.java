package com.maseko.root.lugchatv1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLogin)
    public void openFormLogin(){
        startActivity(new Intent(StartActivity.this, LoginActivity.class));

    }

    @OnClick(R.id.btnRegister)
    public void openFormRegister(){
        startActivity(new Intent(StartActivity.this, RegisterActivity.class));
    }
}
