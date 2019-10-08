package com.analytics.sdk.common.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.analytics.sdk.client.AdClientContext;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.network.NetworkHelper;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by kf on 2018/9/13.
 */

public class DeviceHelper {

    static final String TAG = DeviceHelper.class.getSimpleName();

    // 获取uuid
    public static String getUUID(Activity activity) {
        String uniqueId = null;
        try {
            final TelephonyManager tm = (TelephonyManager) activity.getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            final String tmDevice, tmSerial,androidId;

            if (PermissionsHelper.isNeedCheckPermission(activity)) {

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    tmDevice = "" + tm.getDeviceId();
                    tmSerial = "" + tm.getSimSerialNumber();
                    androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(),
                            android.provider.Settings.Secure.ANDROID_ID);
                    UUID deviceUuid = new UUID(androidId.hashCode(),
                            ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                    uniqueId = deviceUuid.toString();
                }

            } else {
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
                UUID deviceUuid = new UUID(androidId.hashCode(),
                        ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                uniqueId = deviceUuid.toString();
            }

        } catch (PackageManager.NameNotFoundException e) {
            uniqueId = "000";
            Logger.i(TAG,"手机唯一标识获取失败");
        }
        return uniqueId;
    }
    public static boolean isConnectivity(Context paramContext) {
        try {
            ConnectivityManager manager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            return ((info != null) && (info.isAvailable()));
        } catch (Exception localException) {
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context paramContext) {
        try {
            return ((TelephonyManager) paramContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public static String getUuid(Context paramContext) {
        UUID uuid = null;
        String str = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
        try {
            if ((!"9774d56d682e549c".equals(str)) && (str != null)) {
                uuid = UUID.nameUUIDFromBytes(str.getBytes("utf8"));
            } else {
                String getDeviceId = getDeviceId(paramContext);
                uuid = getDeviceId != null ? UUID.nameUUIDFromBytes(getDeviceId.getBytes("utf8")) : UUID.randomUUID();
            }
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            localUnsupportedEncodingException.printStackTrace();
            uuid = UUID.randomUUID();
        }
        return uuid.toString();
    }


    /**
     * 区分手机平板 手机:false 平板true
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 获取imei
     */
    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "000000000000000";
        try {
            if (PermissionsHelper.isNeedCheckPermission(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    imei = mTelephonyMgr.getDeviceId();
                }
            } else {
                imei = mTelephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            Logger.i(TAG,"imei获取失败");
            e.printStackTrace();
        }
        if (imei == null || "".equals(imei)) {
            imei = "000000000000000";
        }
        return imei;
    }


    /**
     * 获取Imsi
     */
    public static String getPhoneImsi(final Context context) {
        String strIMSI = "000000000000000";

        TelephonyManager phone = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (phone == null) {
            return strIMSI;
        }
        try {
            if (PermissionsHelper.isNeedCheckPermission(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    strIMSI = phone.getSubscriberId();
                }
            } else {
                strIMSI = phone.getSubscriberId();
            }

        } catch (Exception e) {
            Logger.i(TAG,"Imsi获取失败");
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(strIMSI)) {
            strIMSI = "000000000000000";
        }
        return strIMSI;
    }


    // 获取浏览器信息
    public static String getUA(Context context) {
        try {
            Method localMethod = WebSettings.class.getMethod("getDefaultUserAgent", new Class[]{Context.class});
            if (localMethod != null) {
                String str = (String) localMethod.invoke(null, new Object[]{context});
                return str;
            }
        } catch (Exception ex) {
        }
        return System.getProperty("http.agent");
    }

    /**
     * 获取MAC地址
     * @param context 上下文
     * @return  localMac
     */
    public static String getMacAddress(Context context) {
        String localMac = "";
        if (NetworkHelper.isWifiAvailable(context)) {
            localMac = getWifiMacAddress(context);
        }
        return localMac;
    }

    public static String getWifiMacAddress(Context context) {
        String localMac = null;
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (wifi.isWifiEnabled()) {
                localMac = info.getMacAddress();
            }
        } catch (Exception e) {
            Logger.i(TAG,"MAC地址获取失败");
            e.printStackTrace();
        }
        return localMac;
    }

    static String currentBattery = "-1";

    public static String getCurrentBattery(){
        return currentBattery;
    }

    public static void startCollectBattery(final Context context){

        try {
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {

                        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                            //获取当前电量
                            int level = intent.getIntExtra("level", 0);
                            //电量的总刻度
                            int scale = intent.getIntExtra("scale", 100);
                            currentBattery = ((level * 100) / scale) + "%";

                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
            },new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static List<String> getAllUserApps(){
        Context context = AdClientContext.getClientContext();
        LinkedList list = new LinkedList();
        Iterator localIterator = context.getPackageManager().getInstalledPackages(0).iterator();
        while (localIterator.hasNext()) {
            PackageInfo localPackageInfo = (PackageInfo) localIterator.next();
            if ((!ActivityManagerHelper.isinstallSystem(localPackageInfo.applicationInfo))
                    && (ActivityManagerHelper.isenable(localPackageInfo.applicationInfo))) {
                list.add(localPackageInfo.packageName);
            }
        }
        return list;
    }

    /**
     * 读取所有的可用的APP信息
     * @return
     */
    public static String getAllUseApps(Context context) {
        String str = null;
        try {
            LinkedList list = new LinkedList();
            Iterator localIterator = context.getPackageManager().getInstalledPackages(0).iterator();
            while (localIterator.hasNext()) {
                PackageInfo localPackageInfo = (PackageInfo) localIterator.next();
                if ((!ActivityManagerHelper.isinstallSystem(localPackageInfo.applicationInfo))
                        && (ActivityManagerHelper.isenable(localPackageInfo.applicationInfo))) {
                    HashMap mp = new HashMap();
                    mp.put("pkgName", localPackageInfo.packageName);
                    mp.put("appName",  context.getPackageManager().getApplicationLabel(localPackageInfo.applicationInfo).toString());
                    mp.put("version", Integer.valueOf(localPackageInfo.versionCode));
                    list.add(mp);
                }
            }
            JSONArray ja = new JSONArray(list);
            str = ja.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return str;
    }

    public static boolean isRootedDevice() {
        try {
            return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        } catch (Exception e){
            return false;
        }
    }

    private static boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        BufferedReader in = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
            closeStream(in);
        }
    }

    public static void closeStream(Closeable closeable){
        if(closeable!=null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getOsVersion() {
        // 设置操作系统版本
        String osVersion=Build.VERSION.RELEASE;
        if (osVersion.length()==1){
            osVersion=osVersion+".0.0";
        }
        if (osVersion.length()==3){
            osVersion=osVersion+".0";
        }
        return osVersion;
    }
}
