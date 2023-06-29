package com.kwaishou.ad.riaid;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.kuaishou.riaid.adbrowser.ADBrowser;
import com.kuaishou.riaid.proto.nano.RiaidModel;
import com.kwaishou.ad.demo.DemoRiaidFactory;
import com.kwaishou.ad.riaid.service.DemoBrowserService;
import com.kwaishou.ad.riaid.service.DemoOutputEventListener;

public class RiaidActivity extends AppCompatActivity {

  /**
   * 给riaid用的画布
   */
  private ADBrowserCanvas canvas;
  private ADBrowser adBrowser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_riaid);
    canvas = findViewById(R.id.canvas);
    initRiaid();
  }

  /**
   * 注意，RiaidModel有两个包名，客户端应该用这个com.kuaishou.riaid.proto.nano包名下的。
   */
  private RiaidModel getRiaidModel() {
    // 这个其实应该是打成json，交给服务做变量的databinding的，这里demo示例就简单来吧
    byte[] bytes = new DemoRiaidFactory().create().toByteArray();
    RiaidModel riaidModel = null;
    try {
      riaidModel = RiaidModel.parseFrom(bytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return riaidModel;
  }

  private void initRiaid() {
    adBrowser =
        new ADBrowser(this, getRiaidModel(), canvas, new DemoBrowserService());
    adBrowser.addBrowserMetricsEventListener(new DemoOutputEventListener(this));
    adBrowser.onDidLoad();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (adBrowser!=null){
      adBrowser.onDidAppear();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (adBrowser!=null){
      adBrowser.onDidDisappear();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (adBrowser!=null){
      adBrowser.onDidUnload();
      adBrowser.onDestroy();
    }
  }
}