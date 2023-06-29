package com.kwaishou.ad.riaid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kuaishou.riaid.Riaid;
import com.kwaishou.riaid_adapter.glide.GlideImageService;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.open_riaid).setOnClickListener(
        v -> startActivity(new Intent(MainActivity.this, RiaidActivity.class)));

    findViewById(R.id.open_create_bitmap).setOnClickListener(
        v -> startActivity(new Intent(MainActivity.this, CreateBitmapActivity.class)));
  }
}