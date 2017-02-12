package com.kii.sample.balance.title;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.kii.sample.balance.MainActivity;
import com.kii.sample.balance.R;
import com.kii.sample.balance.samples.SampleMainActivity;

/**
 * Created by Atsuto5 on 2016/08/06.
 */
public class GreetingActivity extends Activity {

    ImageView imgview;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_image);
        imgview = (ImageView) findViewById(R.id.splash);

        //ちなみに(1,0)を(0,1)に変更するとフェードインになるよ！
        AlphaAnimation feedout = new AlphaAnimation( 1, 0 );
        //フェードアウトするまでの時間。単位はmsec。
        feedout.setDuration( 1500 );

        //imv2にフェードアウトアニメーションを適用する
        imgview.startAnimation( feedout );

            Handler hdl = new Handler();
            // 500ms遅延させてsplashHandlerを実行します。
            hdl.postDelayed(new splashHandler(), 1500);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class splashHandler implements Runnable {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public void run() {
            imgview = (ImageView) findViewById(R.id.splash);
            imgview.setVisibility(View.INVISIBLE);
            // スプラッシュ完了後に実行するActivityを指定します。
            Intent intent = new Intent(getApplication(), MainActivity.class);
            //Intent intent = new Intent(getApplication(), SampleMainActivity.class);
            startActivity(intent);
            // SplashActivityを終了させます。
            GreetingActivity.this.finish();
        }
    }


}
