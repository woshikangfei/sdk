package com.analytics.sdk.common.network;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.common.runtime.permission.PermissionsHelper;
import com.qq.e.comm.managers.GDTADManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkHelper {

    static final String TAG = NetworkHelper.class.getSimpleName();

    //没有网络连接
    private static final int NETWORN_NONE = 0;
    //wifi连接
    private static final int NETWORN_WIFI = 100;
    //手机网络数据连接类型
    private static final int NETWORN_2G = 2;
    private static final int NETWORN_3G = 3;
    private static final int NETWORN_4G = 4;
    private static final int NETWORN_UNKNOWN = 1;

    private NetworkHelper() {}

    /**
     * Wifi网络是否可用
     * @param context 上下文
     * @return boolean
     */
    public static boolean isWifiAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            Logger.i(TAG,"isWifiAvailable UnAvailable");
            return false;
        }

        GDTADManager.getInstance().getDeviceStatus();

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 网络是否可用
     * @param context　上下文
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        // Context context = context.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            Logger.i(TAG,"isNetworkAvailable 网络不可用");
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (NetworkInfo aNetworkInfo : networkInfo) {
                    // 判断当前网络状态是否为连接状态
                    if (aNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        Logger.i(TAG,"isNetworkAvailable 网络不可用");
        return false;
    }

    public static int getWifiLevel(Context context) {

        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //获得信号强度值
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                int level = wifiInfo.getRssi();
                //根据获得信号的强度发送信息
                if (level <= 0 && level >= -50) {//最强
                    return 1;
                } else if (level < -50 && level >= -70) {//较强
                    return 2;
                } else if (level < -70 && level >= -80) {//较弱
                    return 3;
                } else if (level < -80 && level >= -100) {//微弱
                    return 4;
                } else {
                    return 5;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取本机IPv4地址
     * @param context 上下文
     * @return 本机IPv4地址；null：无网络连接
     */
    public static String getIpAddress(Context context) {
        // 获取WiFi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断WiFi是否开启
        if (wifiManager.isWifiEnabled()) {
            // 已经开启了WiFi
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        } else {
            // 未开启WiFi
            try {
                NetworkInterface networkInterface;
                InetAddress inetAddress;
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                    networkInterface = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
                return null;
            } catch (SocketException ex) {
                Logger.i(TAG,"本机IPv4地址获取失败");
                ex.printStackTrace();
                return null;
            }
        }
    }

    private static String intToIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF);
    }


    public static String getNetworkTypeString(int type){
        if(type == NETWORN_NONE) {
            return "none";
        } else if(type == NETWORN_WIFI) {
            return "wifi";
        } else if(type == NETWORN_2G) {
            return "2G";
        } else if(type == NETWORN_3G) {
            return "3G";
        } else if(type == NETWORN_4G) {
            return "4G";
        } else {
            return "none";
        }
    }

    /**
     * 获取当前网络连接类型
     * @param context 上下文
     * @return 网络连接类型
     */
    public static int getNetworkState(Context context) {
        //获取系统的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //如果当前没有网络
        if (null == connManager)
            return NETWORN_NONE;

        //获取当前网络类型，如果为空，返回无网络
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NETWORN_NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NETWORN_WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORN_2G;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORN_3G;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORN_4G;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return NETWORN_UNKNOWN;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA") || strSubTypeName.equalsIgnoreCase("WCDMA") || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NETWORN_3G;
                            } else {
                                return NETWORN_UNKNOWN;
                            }
                    }
                }
        }
        return NETWORN_NONE;
    }

    public static int getPhoneNet(Context context) {
        try {

            GDTADManager.getInstance().getDeviceStatus();

            TelephonyManager localTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return localTelephonyManager.getNetworkType();
        } catch (Exception localException) {
            return 0;
        }
    }

    public static String getDataNet(Context context) {
        String dataNetType = "unknow";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            switch (networkInfo.getType()) {
                case 0:
                    dataNetType = "ed";
                    break;
                case 1:
                    dataNetType = "wi";
                    break;
            }
            return dataNetType;
        } catch (Exception localException) {
            return dataNetType;
        }
    }

    public static String getOperator(Context context) {
        try {
            TelephonyManager var1 = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            return var1.getNetworkOperator();
        } catch (Exception var2) {
            ;
        }

        return "";
    }

    public static Carrier getCarrier(Context context) {
        String var1;
        if ((var1 = getOperator(context)) != null) {
            if (var1.equals("46000") || var1.equals("46002") || var1.equals("46007") || var1.equals("46020")) {
                return Carrier.CMCC;
            }

            if (var1.equals("46001") || var1.equals("46006")) {
                return Carrier.UNICOM;
            }

            if (var1.equals("46003") || var1.equals("46005")) {
                return Carrier.TELECOM;
            }
        }

        return Carrier.UNKNOWN;
    }

    /**
     * 返回运营商 需要加入权限 <uses-permission android:name="android.permission.READ_PHONE_STATE">
     * @return 1, 代表中国移动，2，代表中国电信，3，代表中国联通，0，代表未知,99代表其他运营商
     */
    public static int getOperators(Context context) {
        // 移动设备网络代码（英语：Mobile Network Code，MNC）是与移动设备国家代码（Mobile Country Code，MCC）相结合, 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
        Carrier carrier = getCarrier(context);
        return carrier.getValue();
    }

    /**
     * 基站id
     * @param context 上下文
     * @return 　基站ID
     */
    public static String getCid(Context context) {
        int cellId = 0;
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 中国移动和中国联通获取LAC、CID的方式
            GsmCellLocation location = null;

            if (PermissionsHelper.isNeedCheckPermission(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return String.valueOf(cellId);
                }
                location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            } else {
                location = (GsmCellLocation) mTelephonyManager.getCellLocation();
            }
            if (location == null) {
                return String.valueOf(cellId);
            }
            cellId = location.getCid();
        } catch (Exception e) {
            Logger.i(TAG,"基站ID获取失败");
        }
        return String.valueOf(cellId);
    }


    /**
     * 获取经纬度
     * @param context　上下文
     * @return 经度和纬度
     */
    public static double[] getLongitudeAndLatitude(Context context) {
        // L.i("定位","开始获取位置");
        double[] data = {0.0, 0.0};
        try {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager == null) {
                // L.i("定位","locationManage=null 定位失败");
                return data;
            }
            // L.i("定位","locationManage!=null 继续");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return data;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Logger.i(TAG,"getLongitudeAndLatitude 使用上次GPS定位信息");
                    data[0] = location.getLatitude();
                    data[1] = location.getLongitude();
                } /*else {
                    LocationListener locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                        }

                        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        // Provider被enable时触发此函数，比如GPS被打开
                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        // Provider被disable时触发此函数，比如GPS被关闭
                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                    };

                    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,
                                locationListener);
                        Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location2 != null) {
                            data[0] = location2.getLatitude(); // 纬度
                            data[1] = location2.getLongitude(); // 经度
                        }
                    } else {
                        data[0] = 0.0; // 纬度
                        data[1] = 0.0; // 经度
                    }
                }*/
            }
        } catch (Exception e) {
            //LogUtils.log("经纬度获取失败");
        }
        return data;
    }
}
