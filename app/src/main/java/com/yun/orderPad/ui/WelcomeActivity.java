package com.yun.orderPad.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.yun.orderPad.BuildConfig;
import com.yun.orderPad.R;
import com.yun.orderPad.ui.login.LoginActivity;
import com.yun.orderPad.util.MainThreadHandler;

public class WelcomeActivity extends BaseActivity {

  private static final String TAG = "WelcomeActivity";
  private TextView tvVersion;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    tvVersion = findViewById(R.id.version);
    tvVersion.setText(BuildConfig.VERSION_NAME);
    Toast.makeText(this,"请登录账号",Toast.LENGTH_LONG).show();
    MainThreadHandler.postDelayed(TAG, () -> {
      startActivity(new Intent(this, LoginActivity.class));
      this.finish();
    }, 1000);
  }

}