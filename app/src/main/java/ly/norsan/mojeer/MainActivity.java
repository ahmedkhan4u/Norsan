package ly.norsan.mojeer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.widget.VideoView;

import ly.norsan.mojeer.R;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private WebView mWebView;
    private SwipeRefreshLayout swipeRefreshLayout;

    GPSTracker gps;
    double latitude = 0, longitude = 0;

    public static final String TAG = MainActivity.class.getSimpleName();
    private VideoView videoHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mWebView = findViewById(R.id.myWebView);
        videoHolder = findViewById(R.id.splashVideo);
        //swipe refresh
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);



        //startVideo();

        gps = new GPSTracker(MainActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();



            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            //statusCheck();
        }


        //Web View

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //improve WebView performance
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // For API level below 18 (This method was deprecated in API level 18)
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //mWebView.clearCache(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setUserAgentString("zmzmAgent");
        CookieManager.getInstance().acceptCookie();
        webSettings.setEnableSmoothTransition(true);
        //webSettings.setEnableSmoothTransition(true);


        //force links open in webview only
        mWebView.setWebViewClient(new MyWebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mWebView.setVisibility(View.VISIBLE);
                videoHolder.setVisibility(View.GONE);
            }
        });
        mWebView.loadUrl(SplashActivity.urlString);



    }

    void startVideo() {

        try{
            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.splash_video);
            videoHolder.setVideoURI(video);
            //videoHolder.requestFocus();

            videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    videoHolder.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }

            });
            videoHolder.start();
        } catch(Exception ex) {
            videoHolder.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            grantLocationPermission();
        }
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRefresh() {
        mWebView.reload();
        swipeRefreshLayout.setRefreshing(false);
        gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            // \n is for new line
            //mWebView.evaluateJavascript("javascript: document.getElementsByClassName('geo')[0].textContent='"+latitude+", "+longitude+"';",null);
            mWebView.evaluateJavascript("getLocation('"+latitude+"','"+longitude+"');", null);

            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }
    }

    private class MyWebViewClient extends WebViewClient {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {



            Log.d(TAG, "shouldOverrideUrlLoading: " + url);

            if (url.contains("get-location")) {
                statusCheck();
                return true;
            } else if (url.contains("external=1")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(intent);
                //view.reload();
                return true;
            } else if (url.startsWith("mailto:")) {
                //Handle mail Urls
                startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                return true;
            } else if (url.contains("maps")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } else {
                view.loadUrl(url);
            }

            return false;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            /*progressDialog = new ProgressDialog(Home.this);
            progressDialog.setTitle("Please Wait...");
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);*/
            //isConnected(Home.this);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            /*if (progressDialog != null)
            {
                progressDialog.dismiss();
            }*/
            //mWebView.evaluateJavascript("javascript: document.getElementsByClassName('geo')[0].textContent='"+latitude+", "+longitude+"';",null);
            mWebView.evaluateJavascript("getLocation('"+latitude+"','"+longitude+"');", null);

            super.onPageFinished(view, url);
        }
    }

    private void grantLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else {
            gps = new GPSTracker(MainActivity.this);
            if(gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                mWebView.evaluateJavascript("getLocation('"+latitude+"','"+longitude+"');", null);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        gps = new GPSTracker(MainActivity.this);
                        if(gps.canGetLocation()) {
                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            mWebView.evaluateJavascript("getLocation('"+latitude+"','"+longitude+"');", null);
                        }

                    }
                } else {
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction()== KeyEvent.ACTION_DOWN)
//        {
//            switch (keyCode)
//            {
//                case KeyEvent.KEYCODE_BACK:
//                    if (mWebView!=null)
//                    {
//                        if (mWebView.canGoBack())
//                        {
//                            mWebView.goBack();
//                        }
//                        else
//                        {
//                            finish();
//                        }
//                        return true;
//                    }
//
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (mWebView.getUrl().contains("#home")) {
                super.onBackPressed();
            } else {
                mWebView.evaluateJavascript("goBack();", null);
            }
        } else {
            super.onBackPressed();
        }
    }
}