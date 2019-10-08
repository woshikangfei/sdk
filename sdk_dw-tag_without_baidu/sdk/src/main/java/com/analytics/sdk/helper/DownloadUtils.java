package com.analytics.sdk.helper;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.client.AdRequest;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.service.AdEventActions;
import com.analytics.sdk.service.ad.entity.AdResponse;
import com.analytics.sdk.service.report.entity.ReportData;

import java.io.File;
import java.util.ArrayList;

public class DownloadUtils {

    static final String TAG = DownloadUtils.class.getSimpleName();

    //下载器
    private DownloadManager downloadManager;
    //上下文
    private Context mContext;
    //下载的ID
    private long downloadId;
    public  DownloadUtils(Context context){
        this.mContext = context;
    }
    private int DOWNLOAD_REQUEST_PERMISSION_CODE = 1001110;

    public void downloadApk(AdResponse adResponse,String url, String apkName) throws Throwable {

//        AdRequest adRequest = adResponse.getClientRequest();
//        Activity activity = adRequest.getActivity();
//
//        if (ContextCompat.checkSelfPermission(AdClientContext.getClientContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},DOWNLOAD_REQUEST_PERMISSION_CODE);
//
//        } else {
            downloadApkInner(url,apkName);
//        }
    }

    //下载apk
    public void downloadApkInner(String url, String apkName) throws Throwable{
        try {
            //创建下载任务
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            // 设置允许使用的网络类型，这里是移动网络和wifi都可以
            request.setAllowedNetworkTypes(request.NETWORK_MOBILE | request.NETWORK_WIFI);
            //移动网络情况下是否允许漫游
            request.setAllowedOverRoaming(false);

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setMimeType(mimeString);

            //在通知栏中显示，默认就是显示的
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setTitle(apkName);
            request.setVisibleInDownloadsUi(true);

            //设置下载的路径
            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() , apkName+".apk");
            //获取DownloadManager
            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
            downloadId = downloadManager.enqueue(request);
            Logger.i(TAG,"DownloadUtils:注册下载广播");
            //注册广播接收者，监听下载状态
            mContext.registerReceiver(receiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Throwable e){
            e.printStackTrace();
            throw e;
        }

    }

    //广播监听下载的各个状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStatus();
        }
    };


    //检查下载状态
    private void checkStatus() {
        try {
            DownloadManager.Query query = new DownloadManager.Query();
            //通过下载的id查找
            query.setFilterById(downloadId);
            Cursor c = downloadManager.query(query);
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    //下载暂停
                    case DownloadManager.STATUS_PAUSED:
                        break;
                    //下载延迟
                    case DownloadManager.STATUS_PENDING:
                        break;
                    //正在下载
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    //下载完成
                    case DownloadManager.STATUS_SUCCESSFUL:
                        ReportData.obtain(AdEventActions.ACTION_AD_DOWNLOAD_COMPLETED).startReport();
                        //下载完成安装APK
                        installAPK();
                        break;
                    //下载失败
                    case DownloadManager.STATUS_FAILED:
                        // Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            c.close();
        } catch (Throwable t){
            t.printStackTrace();
            throw t;
        }

    }

    //下载到本地后执行安装
    private void installAPK() {
        //获取下载文件的Uri
        Uri downloadFileUri = downloadManager.getUriForDownloadedFile(downloadId);
        if (downloadFileUri != null) {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
                Intent intent= new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                mContext.unregisterReceiver(receiver);
            }else{
                startInstallAPK(downloadFileUri);
                mContext.unregisterReceiver(receiver);
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
            mContext.startActivity(intent);
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
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;
    }
}
