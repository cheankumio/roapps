package com.klapper.roapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import es.dmoral.toasty.Toasty;

/**
 * Created by c1103304 on 2017/10/31.
 */

public class FloatingWebView extends MainActivity{
    WebView webView;
    ProgressDialog pgd;
    boolean firstactive = true;
    @Override
    public void setUpWindow() {
        pgd = new ProgressDialog(FloatingWebView.this);
        pgd.setTitle("載入網頁中");
        pgd.setMessage("請稍候....");
        pgd.setCancelable(false);

       // MobileAds.initialize(this, "ca-app-pub-7003556787929258~1331181288");

        // Creates the layout for the window and the look of it
//        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Params for the window.
        // You can easily set the alpha and the dim behind the window from here
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;    // lower than one makes it more transparent
        params.dimAmount = 0f;  // set it higher if you want to dim behind the window
        getWindow().setAttributes(params);

        // Gets the display size so that you can set the window to a percent of that
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // You could also easily used an integer value from the shared preferences to set the percent
        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .97));
        } else {
            getWindow().setLayout((int) (width * .9), (int) (height * .97));
        }


    }

    @Override
    public void CheckPermission() {
        webView = (WebView)findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();

        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDisplayZoomControls(true);


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String lastsite = request.toString();
                //Log.d("MYLOG","URL: "+lastsite);
                FloatingViewService.last_view_site = lastsite;
                view.loadUrl(lastsite);
                return true;
            }

        });
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if ( view.getProgress()>=100) {


                    // I save Y w/in Bundle so orientation changes [in addition to
                    // initial loads] will reposition to last location
                    if(firstactive) {
                        jumpToY(FloatingViewService.last_Y);
                        if(FloatingViewService.last_view_site.length()>10){
                            Toasty.success(FloatingWebView.this,"已返回上次瀏覽處",Toast.LENGTH_SHORT).show();
                        }else{
                            Toasty.success(FloatingWebView.this,"載入完成",Toast.LENGTH_SHORT).show();
                        }
                    }
                    firstactive = false;
                    pgd.dismiss();
                }
            }
        });
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        Log.d("MYLOG","URL: "+FloatingViewService.last_view_site);
        if(FloatingViewService.last_view_site.length()>10){
            webView.loadUrl(FloatingViewService.last_view_site);
        }else {
            webView.loadUrl("https://m.gamer.com.tw/forum/B.php?ltype=&bsn=28924");
        }
        pgd.show();

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: "+webView.getScrollY()+", "+webView.getScrollX());
        FloatingViewService.last_view_site = webView.getUrl();
        FloatingViewService.last_Y = webView.getScrollY();
        super.onDestroy();

    }

    private void jumpToY (final int yLocation ) {
        webView.postDelayed( new Runnable () {
            @Override
            public void run() {
                webView.scrollTo(0, yLocation);
            }
        }, 300);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_03:
                finish();
                break;
            case R.id.button_04:
                webView.loadUrl("https://m.gamer.com.tw/forum/B.php?ltype=&bsn=28924");
                break;
        }

        super.onClick(view);
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.close:
                finish();
                break;
            case R.id.home:
                webView.loadUrl("https://m.gamer.com.tw/forum/B.php?ltype=&bsn=28924");
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
