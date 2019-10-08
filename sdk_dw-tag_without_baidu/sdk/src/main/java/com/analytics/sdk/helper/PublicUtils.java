package com.analytics.sdk.helper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.analytics.sdk.common.image.ImageDecoder;
import com.analytics.sdk.common.image.MarkEnforcingInputStream;
import com.analytics.sdk.common.image.RecyclableBufferedInputStream;
import com.analytics.sdk.common.log.Logger;
import com.analytics.sdk.config.AdConfig;
import com.analytics.sdk.view.handler.csj.TTAdManagerHolder;

import java.io.InputStream;
import java.util.List;

/**
 * Created by yangminghui on 2018/8/17.
 */

public class PublicUtils {
    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "null".equals(str) || str.length() == 0;
    }

    /**

     * 检测该包名所对应的应用是否存在

     * @param packageName

     * @return

     */
    public static boolean checkPackage(Activity activity, String packageName) {
        if (isEmpty(packageName))
            return false;
        try {
          activity.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 显示图片
     * @param imageBitmap  图片
     */
    public static void showImage(Activity activity, final ImageView iv, final Bitmap imageBitmap) {
        if (imageBitmap==null||activity==null)
            return;
        try {
            iv.setImageBitmap(imageBitmap);
            iv.setVisibility(View.VISIBLE);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }


    private static Toast mToast;
    /**
     * Toast显示信息
     *
     * @param context
     * @param msg     文字信息,如果是空则不显示@{link PublicUtils#isEmpty}
     */
    public static void showToast(final Context context, final String msg) {
        if(isEmpty(msg)) {
            return;
        }
        if (context==null){
            return;
        }
        try{
            if (mToast == null) {
                mToast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
                mToast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * 判断是否安装了应用
     * @return true 为已经安装
     */
    public static boolean hasApplication(Activity activity, String url) {
        try {
            PackageManager manager = activity.getPackageManager();
            Intent action = new Intent(Intent.ACTION_VIEW);
            action.setData(Uri.parse(url));
            List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
            return list != null && list.size() > 0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 穿山甲
     * */
    public static void initCSJAppId(final Context context,String appId,String appName){
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常穿山甲
        if(!AdConfig.getDefault().isHookCsj()){
            TTAdManagerHolder.init(context,appId, appName);
        } else {
            Logger.i("proxyHook","initCSJAppId hook enter");
//            TTAdManagerHolder.init(new ProxyContext(context,0),appId, appName);

//            try {
//                Class classH =  Class.forName("com.ss.android.downloadlib.c");
//                Class [] params = {Context.class};
//                Method method = classH.getMethod("a",params);
//                Object[] objects = {context};
//                method.invoke(null,objects);
//                Logger.i("proxyHook","initCSJAppId hook a.i ok");
//            }catch (Exception e){
//                Logger.i("proxyHook","initCSJAppId hook a.i exception");
//            }

//            ThreadExecutor.runOnAndroidHandlerThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Class classH =  Class.forName("com.ss.android.downloadlib.a.i");
//                        Class [] params = {Context.class};
//                        Method method = classH.getMethod("a",params);
//                        Object[] objects = {context};
//                        method.invoke(null,objects);
//                        Logger.i("proxyHook","initCSJAppId hook a.i ok");
//                    }catch (Exception e){
//                        Logger.i("proxyHook","initCSJAppId hook a.i exception");
//                    }
//                }
//            },3*1000);


            //低版本
//            try {
//                Class classH =  Class.forName("com.ss.android.downloadlib.a.h");
//                Class [] params = {Context.class};
//                Method method = classH.getMethod("a",params);
//                Object[] objects = {context};
//                method.invoke(null,objects);
//            }catch (Exception e){
//                Log.e("jiaoyb","----->>> ",e);
//            }
        }

    }

    public static Bitmap compressImage(InputStream  stream) {
        return compressImage(stream,-1,-1);
    }

    public static Bitmap compressImage(InputStream  stream,int requestWith,int requestHeight) {
        try {

            stream = new MarkEnforcingInputStream(new RecyclableBufferedInputStream(stream));

            Logger.i(PublicUtils.class.getSimpleName(),"compressImage requestWith = " + requestWith + " , requestHeigth = " + requestHeight);

            BitmapFactory.Options options=new BitmapFactory.Options();
            // TODO: 2019/5/9 后面整个交给ImageDecoder处理，inSampleSize应该要根据Client View的实际大小进行计算, 需要检测在onPreDraw之前获取view的高度和宽度
            ImageDecoder.calculateScaling(stream,requestWith,requestHeight,options);
//            options.inSampleSize = 4;
            ImageDecoder.calculateConfig(stream,options);

//            options.inPreferredConfig = false ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
//
//            options.inDither = true;

            Bitmap bitmap = BitmapFactory.decodeStream(stream,new Rect(),options);
            Logger.i(PublicUtils.class.getSimpleName(),"compressImage bitmap.isMutable = " + (bitmap == null ? bitmap.isMutable() : "null"));
            return bitmap;
           /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while ( baos.toByteArray().length / 1024>45) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;//每次都减少10
                Logger.e("information","options"+options+"      "+baos.toByteArray().length/1024);
                if (options<0){
                    break;
                }
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片*/
        }catch (Exception e){
            e.printStackTrace();
        }
        return  BitmapFactory.decodeStream(stream);

    }

}
