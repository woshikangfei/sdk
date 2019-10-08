package com.analytics.sdk.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.analytics.sdk.common.view.ProgressWebView;
import com.analytics.sdk.helper.BeanUtils;
import com.analytics.sdk.helper.PublicUtils;
import com.analytics.sdk.service.ad.entity.ClickLoction;
import java.io.File;


public class WebviewActivity extends Activity {

    private ProgressWebView webView;
    private DownloadManager downloadManager;
    private long downloadId;
    private BroadcastReceiver broadcastReceiver;
    //public static Listener listener;
    private LinearLayout webViewTitleLayout;
    //广告关闭按钮
    private TextView tvClose;

    private int DOWNLOAD_REQUEST_PERMISSION_CODE = 1001110;

    private String title="";
    private String clickUrl;
    private String[] arrDownloadTrackUrls;
    private static WebViewStateListener webViewStateListener = WebViewStateListener.EMPTY;

    public interface WebViewStateListener {
        void onShow();

        WebViewStateListener EMPTY = new WebViewStateListener() {
            @Override
            public void onShow() {
            }
        };
    }


    public static void startWebActivity(Context context,String title,String url,WebViewStateListener listener){
        webViewStateListener = (listener == null ? WebViewStateListener.EMPTY : listener);
        Intent intent = new Intent(context,WebviewActivity.class);
        intent.putExtra("mClickUrl",url);
        intent.putExtra("title",title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startWebActivity(Context context,String title,String url){
        startWebActivity(context, title, url,null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickUrl=getIntent().getStringExtra("mClickUrl");
        title=getIntent().getStringExtra("title");
        arrDownloadTrackUrls = getIntent().getStringArrayExtra("arrDownloadTrackUrl");

        initView();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},DOWNLOAD_REQUEST_PERMISSION_CODE);
        } else {
            loadUrl();
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0) {
            if (requestCode == DOWNLOAD_REQUEST_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadUrl();
                } else {
                    finish();
                    webViewStateListener.onShow();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void loadUrl(){

        try {
            if (clickUrl.startsWith("http:") || clickUrl.startsWith("https:")) {
                webView.loadUrl(clickUrl);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(clickUrl));
                startActivity(intent);
                finish();
            }

            webViewStateListener.onShow();

        }catch (Exception e){
            e.printStackTrace();
        }

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                downloadApk(url);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView(){
        webViewTitleLayout=new LinearLayout(this);
        webViewTitleLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout relativeLayout=new LinearLayout(this);
        LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(titleLayoutParams);
        relativeLayout.setOrientation(LinearLayout.HORIZONTAL);
        webViewTitleLayout.setBackgroundColor(Color.parseColor("#dddddd"));
        webViewTitleLayout.addView(relativeLayout);

        tvClose = new TextView(this);
        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvClose.setPadding(30,8,30,8);
        tvClose.setLayoutParams(closeParams);
        tvClose.setTextSize(30);
        tvClose.setText("×");
        tvClose.setTextColor(Color.parseColor("#FFFFFF"));
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }else{
                 /*   if (listener!=null){
                        listener.onCancel();
                    }*/
                    finish();
                }
            }
        });

        relativeLayout.addView(tvClose);


        TextView tvTitle = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.gravity=Gravity.CENTER;
        tvTitle.setLayoutParams(titleParams);
        tvTitle.setPadding(0,0,30,0);
        tvTitle.setTextSize(18);
        tvTitle.setText(title);
        tvTitle.setSingleLine(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        tvTitle.setTextColor(Color.parseColor("#FFFFFF"));
        relativeLayout.addView(tvTitle);

        webView=new ProgressWebView(this);

        webView.setTitleView(tvTitle);
        LinearLayout.LayoutParams webParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        webParams.weight=1;
        webView.setLayoutParams(webParams);
        webViewTitleLayout.addView(webView);
        setContentView(webViewTitleLayout);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (webView.canGoBack())) {
            webView.goBack();
            return true;
        }else{
            Intent mIntent = new Intent();
            this.setResult(0, mIntent);
           /* if (listener!=null){
                listener.onCancel();
            }*/
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        if(broadcastReceiver!=null){
            unregisterReceiver(broadcastReceiver);
        }
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(webView.canGoBack()){
                    webView.goBack();
                }else{
                    Intent mIntent = new Intent();
                    this.setResult(0, mIntent);
                 /*   if (listener!=null){
                        listener.onCancel();
                    }*/
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 下载APK
     **/
    private void downloadApk(String apkUrl) {

        if(arrDownloadTrackUrls!=null) {
            BeanUtils.track(arrDownloadTrackUrls, this, new ClickLoction());
        }

        try {
            Uri uri = Uri.parse(apkUrl);
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            request.setAllowedNetworkTypes(request.NETWORK_MOBILE | request.NETWORK_WIFI);
            //设置是否允许漫游
            request.setAllowedOverRoaming(false);
            //设置文件类型
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(apkUrl));
            request.setMimeType(mimeString);
            //在通知栏中显示
            request.setNotificationVisibility(request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle("download...");
            Toast.makeText(this, "正在下载APP", Toast.LENGTH_LONG).show();
            request.setVisibleInDownloadsUi(true);
            //sdcard目录下的download文件夹
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, System.currentTimeMillis() + ".apk");
            // 将下载请求放入队列
            downloadId = downloadManager.enqueue(request);
            listener(downloadId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private void listener(final long Id) {
        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    install();
                }
            }
        };

        registerReceiver(broadcastReceiver, intentFilter);
    }

    private void install() {

        //获取下载文件的Uri
        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
        if (downloadFileUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                startInstallAPK(downloadFileUri);
            }
        }


    }
    private void startInstallAPK(Uri apk){
        // 通过Intent安装APK文件
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DEFAULT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        File apkFile = queryDownloadedApk();
        if(apkFile.exists()){
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
    //获取下载文件
    private File queryDownloadedApk() {
        File targetApkFile = null;
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloadManager.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (!PublicUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }



}
