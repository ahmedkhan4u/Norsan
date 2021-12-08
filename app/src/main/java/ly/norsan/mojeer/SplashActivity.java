package ly.norsan.mojeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import ly.norsan.mojeer.R;
import com.onesignal.OneSignal;

public class SplashActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "9a5b658c-0182-432d-931e-4fb2a8b2e03a";
    boolean checkGpsOnResume = false;
    private ProgressBar progressBar;
    String user_id = "";
    public static String urlString;
    private VideoView videoHolder;
    public static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        videoHolder = findViewById(R.id.splashVideo);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        //OneSignal
        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        user_id = OneSignal.getDeviceState().getUserId();


        //grantLocationPermission();

        progressBar = findViewById(R.id.spinKitView);

        ThreeBounce db = new ThreeBounce();

        progressBar.setIndeterminateDrawable(db);
        //startVideo();
        countDownTimer();
//..
    }

//    void startVideo() {
//        videoHolder.setZOrderOnTop(true);
//        try {
//            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
//                    + R.raw.splash_video);
//            videoHolder.setVideoURI(video);
//            //videoHolder.requestFocus();
//
//            videoHolder.setOnCompletionListener(mp -> countDownTimer());
//            videoHolder.start();
//        } catch (Exception ex) {
//            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }


    private void countDownTimer() {

        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                //This thing was missing so I need to take care of this
                user_id = OneSignal.getDeviceState().getUserId();
                Log.d(TAG, "onFinish: " + user_id);
                if (user_id == null) {
                    countDownTimer();
                } else {
                        urlString = "https://norsan.zmzm.x10.ltd/mobile.php?device-id=" + user_id + "&os=android";
                        Log.d(TAG, "Targer Url: " + urlString);
                        //Toast.makeText(SplashActivity.this, "Targer Url: " + urlString, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }

        }.start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        countDownTimer();
                    }
                } else {
                    countDownTimer();
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}