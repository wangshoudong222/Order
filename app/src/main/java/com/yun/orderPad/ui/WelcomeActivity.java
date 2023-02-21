package com.yun.orderPad.ui;

import android.os.Bundle;

import com.yun.orderPad.R;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

  /**
   * 主屏8”IPS,800x1280，电
   * 容式多点触控
   * 副屏4.95”IPS,480x960
   * @param savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
  }
}