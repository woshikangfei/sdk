package com.analytics.sdk.common.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.helper.SdkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Log统一管理类
 */
public class Logger {

    /**
     * 方便通过adb logcat对log进行grep
     */
    private static final String ALL_LOG_MESSAGE_PREFIX = " [AdSdk] ";
    static final String ALL_LOG_TAG = "AdSdk";

    private Logger() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    // 是否需要打印bug，可以在application的onCreate函数里面初始化

    // 下面是传入自定义tag的函数
    public static void forcePrint(String tag, String msg) {
        Log.i(tag, ALL_LOG_MESSAGE_PREFIX +msg);
    }


    static final String LINE_SEPARATOR = System.getProperty("line.separator");

    static void printLine(String tag, boolean isTop) {
        if (isTop) {
            i(tag, "***********************************************");
        } else {
            i(tag, "***********************************************");
        }
    }

    public static void printJson(String msg, String headString) {

        if (!AdConfig.getDefault().isPrintLog()){
            return;
        }

        String message;

        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(4);//最重要的方法，就一行，返回格式化的json字符串，其中的数字4是缩进字符数
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(4);
            } else {
                message = msg;
            }
        } catch (JSONException e) {
            message = msg;
        }

        printLine(ALL_LOG_TAG, true);
        message = headString + LINE_SEPARATOR + message;
        String[] lines = message.split(LINE_SEPARATOR);
        for (String line : lines) {
            i(ALL_LOG_TAG, "* " + line);
        }
        printLine(ALL_LOG_TAG, false);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg,int br_count) {
        if (AdConfig.getDefault().isPrintLog()){
            Log.i(ALL_LOG_TAG,"\n");
            for(int i = 0;i < br_count;i++){
//				Log.i(ALL_LOG_TAG,"☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆☆");
//				Log.i(ALL_LOG_TAG,"\n");
                Log.i(ALL_LOG_TAG,"〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓〓");
            }
            Log.i(ALL_LOG_TAG,"\n");
            i(tag,msg);
        }
    }

    public static void printMotionEvent(String tag, MotionEvent event){
        if (AdConfig.getDefault().isPrintLog()){
            String action = SdkHelper.getMotionEventActionString(event);
            int dx = (int)event.getX();
            int dy = (int)event.getY();
            i(tag,action + " x = " + dx + " , y = " + dy);
        }
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {  //信息太长,分段打印
        if (AdConfig.getDefault().isPrintLog()){
            Log.i(tag, ALL_LOG_MESSAGE_PREFIX + msg);
            if(AdConfig.getDefault().isWriteLog2File()){
                writeLog2File(msg);
            }
        }

    }

    public static void d(String tag, String msg) {
        if (AdConfig.getDefault().isPrintLog()) {
            Log.d(tag, ALL_LOG_MESSAGE_PREFIX +msg);
            if(AdConfig.getDefault().isWriteLog2File()){
                writeLog2File(msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (AdConfig.getDefault().isPrintLog()) {
            Log.e(tag, ALL_LOG_MESSAGE_PREFIX +msg);
            if(AdConfig.getDefault().isWriteLog2File()){
                writeLog2File(msg);
            }
        }
    }

    public static void v(String tag, String msg) {
        if (AdConfig.getDefault().isPrintLog()) {
            //剩余部分
            Log.v(tag, ALL_LOG_MESSAGE_PREFIX +msg);
            if(AdConfig.getDefault().isWriteLog2File()){
                writeLog2File(msg);
            }
        }
    }

    private static final String LOG_PREFIX = "SDK_LOG==>";
    private static final String LOG_FILE_NAME = "sdk.log";
    private static FileWriter fileWriter;
    private static FileWriter sipTraceFileWriter;
    private static String deviceInfoStr;
    private static StringBuffer sb;

    public static void deleteLogFile(){

        if(fileWriter!=null){
            try {
                fileWriter.close();
                fileWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String dirName = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator;
        File dir = new File(dirName);
        File file = new File(dir, LOG_FILE_NAME);
        if(file.exists()){
            file.delete();
        }
    }

    private static synchronized void writeLog2File(String message) {
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                sb = new StringBuffer();
//				sb.append("\r\n"+LOG_PREFIX);
                String time = " TIME:" + getTimeString();
//				sb.append(getDeviceInfo());
                sb.append(time);
                sb.append(message + "\n");
//				sb.append(" Thread:"+Thread.currentThread().getName());
//				sb.append("\r\n===============================\r\n");
                String log = sb.toString();

                if (fileWriter == null) {
                    String dirName = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + File.separator;
                    File dir = new File(dirName);
                    if (!dir.exists())
                        dir.mkdir();
                    File file = new File(dir, LOG_FILE_NAME);
                    Log.i("file","file path = " + file.getAbsolutePath());
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    if (file.length()>1024*1024*50) {
                        file.delete();
                        file = new File(dir, LOG_FILE_NAME);
                    }
                    fileWriter = new FileWriter(file, true);
                }
                fileWriter.write(log);
                fileWriter.flush();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static void writeLog2File2(String fileName,String mMessageStr) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
//		StringWriter wr = new StringWriter();
//		PrintWriter pw = new PrintWriter(wr);
        try {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {

                sb = new StringBuffer();
                sb.append("\r\n"+LOG_PREFIX);
                sb.append("\r\n"+mMessageStr);
                String time = " TIME:" + getTimeString();
//				sb.append(getDeviceInfo());
                sb.append(time);
                sb.append(" Thread:"+Thread.currentThread().getName());
                sb.append("\r\n===============================\r\n");
                String log = sb.toString();

                if (sipTraceFileWriter == null) {
                    String dirName = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + File.separator;
                    File dir = new File(dirName);
                    if (!dir.exists())
                        dir.mkdir();
//					File file = new File(Environment.getExternalStorageDirectory(),
//						"com.zed3.sipua" +
//						".log");
                    File file = new File(dir, fileName);
                    //add by oumogang 2013-09-11
                    if (!file.exists()){
                        file.createNewFile();
                    }
                    if (file.length()>1024*1024*5) {
                        file.delete();
                        file = new File(dir, fileName);
                    }
                    sipTraceFileWriter = new FileWriter(file, true);
                }
                sipTraceFileWriter.write(log);
                sipTraceFileWriter.flush();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static SimpleDateFormat formatter;
    private static String getTimeString() {
        // TODO Auto-generated method stub
        if (formatter == null) {
            formatter = new SimpleDateFormat(
                    " yyyy-MM-dd hh:mm:ss SSS ");
        }
        long systemTime = System.currentTimeMillis();
        Date curDate = new Date(systemTime);// 获取当前时
        return formatter.format(curDate);
    }

    private static String getDeviceInfo(Context context) {
        // TODO Auto-generated method stub
        if (deviceInfoStr == null) {
            // 获取当前手机操作系统的信息.
            String version = null;
            try {

                PackageManager pm = context.getPackageManager();

                PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
                version = packageInfo.versionName;
                deviceInfoStr = ("\r\n"+packageInfo.versionName);
            } catch (PackageManager.NameNotFoundException e1) {
                e1.printStackTrace();
            }
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);// 暴力反射,可以获取私有成员变量的信息
                String name = field.getName();
                String value = "";
                try {
                    value = field.get(null).toString();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (/*name.equalsIgnoreCase("PRODUCT")||*/name.equalsIgnoreCase("MODEL")) {
                    deviceInfoStr+=("\r\n"+name + "=" + value);
                    break;
                }
            }
            deviceInfoStr+=("\r\nSDK:"+Build.VERSION.SDK_INT+"  RELEASE:"+Build.VERSION.RELEASE+" VERSION:"+version);
        }
        return deviceInfoStr;
    }

}