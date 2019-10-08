package com.analytics.sdk.common.runtime.permission;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.analytics.sdk.common.log.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 运行时权限访问操作 可以对某一权限进行鉴别，也可以对指定的混合权限进行鉴别
 * 注意：需要升级support-v4包到23版本以上才可以使用
 * @author
 * {@link /github.com/shkschneider/android_RuntimePermissionsCompat}
 *
 * 使用示例代码：
 *
//执行前判断权限
//构造需要的权限，可以支持混合类、某一个、某一类中一个容许，根据需求确定
final Permission permission = new Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PermissionInfo.PROTECTION_DANGEROUS);
if (PermissionsHelper.isGranted(this, permission.name)) {
Toast.makeText(this, "容许执行", Toast.LENGTH_SHORT).show();
//1、若要返回可以在这里执行想要执行的业务逻辑
//2、若不返回则可以在回调中执行业务逻辑，但是多了一层判断

//return ;
}
else if (PermissionsHelper.shouldPrompt(this, permission.name)) {
// Explain WHY you need this permission
Toast.makeText(this, "给用户明确的解释为什么要使用对应的权限", Toast.LENGTH_LONG).show();
}

//执行权限请求
PermissionsHelper.requestPermission(this, permission.name);
 *
//Activity的请求权限后的回调函数
 @Override
 public void onRequestPermissionsResult(final int requestCode,  final String[] permissions,  final int[] grantResults) {
 final Map<String, Boolean> runtimePermissionsResults = PermissionsHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
 if (runtimePermissionsResults != null) {
 final boolean granted = runtimePermissionsResults.get(permissions[0]);
 Toast.makeText(this, (granted ? "Permission GRANTED" : "Permission DENIED"), Toast.LENGTH_SHORT).show();

 if (granted) {
 //若是容许，可以执行相关的业务逻辑
 }

 return ;
 }
 }
 *
 */
public class PermissionsHelper {

    private static final String TAG = PermissionsHelper.class.getSimpleName();
    private static final int MARSHMALLOW = Build.VERSION_CODES.M;
    private static final int REQUEST_CODE = MARSHMALLOW;

    protected PermissionsHelper() {
        // Empty
    }

    /**
     * 用于判断是否需要进行权限判断
     *
     * @return
     *     false：手机非M版本以上，不用对权限进行判断
     *     true：需要对权限进行判断
     * @throws PackageManager.NameNotFoundException
     */
    public static boolean isNeedCheckPermission(Context pContext) throws PackageManager.NameNotFoundException {

        String strPackageName = pContext.getPackageName();
        int iTargetSDKVersion = getTargetSDKVersoin(pContext, strPackageName);

        return ((iTargetSDKVersion >= MARSHMALLOW)
                && (Build.VERSION.SDK_INT >= MARSHMALLOW )) ?  true : false;
    }


    public static int getTargetSDKVersoin(Context pContext, String pPackageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = pContext.getPackageManager();
        PackageInfo aPackageInfo = pm.getPackageInfo(pPackageName, 0);

        return (aPackageInfo == null) ? 0 :  aPackageInfo.applicationInfo.targetSdkVersion;
    }

    /**
     * 对某个权限进行控制
     *
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean isGranted(final Context context, final String permission) {
        return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 对某类权限分组的权限进行判断，即只要有一个容许了，那么其他的肯定都会是容许
     * 故不需要对全部权限进行判断
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isAnyGranted(final Context context, final String[] permissions) {
        for (final String permission : permissions) {
            if (isGranted(context, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对申请的权限进行全部判断，该接口用户混合模式下，包含不同的权限分组时候使用
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean areAllGranted(final Context context, final String[] permissions) {
        for (final String permission : permissions) {
            if (! isGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据用户申请的权限判断是否要提示用户
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean shouldPrompt(final Activity activity, final String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 对申请的某一系列权限判断是否要提示用户
     *
     * @param activity
     * @param permissions
     * @return
     */
    public static boolean shouldPrompt(final Activity activity, final String[] permissions) {
        for (final String permission : permissions) {
            if (shouldPrompt(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 申请某一权限检测
     *
     * @param activity
     * @param permission
     */
    public static void requestPermission(final Activity activity, final String permission, int pReqCode) {
        requestPermissions(activity, new String[] { permission }, pReqCode);
    }

    /**
     * 通过反射机制通知上层判断结果，上层根据结果做相应的业务调用
     *
     * @param activity
     * @param permissions
     */
    @SuppressLint("NewApi")
    public static void requestPermissions(final Activity activity, final String[] permissions, int pReqCode) {
        if (Build.VERSION.SDK_INT < MARSHMALLOW) {
            try {
                Logger.i(TAG,"No Runtime Permissions -- bridging to Activity.onRequestPermissionsResult()");
                final Method method = activity.getClass().getMethod("onRequestPermissionsResult", int.class, String[].class, int[].class);
                method.setAccessible(true);
                final int[] grantResults = new int[permissions.length];
                for (int i = 0; i < permissions.length; i++) {
                    grantResults[i] = (isGranted(activity, permissions[i]) ? PackageManager.PERMISSION_GRANTED : PackageManager.PERMISSION_DENIED);
                }
                method.invoke(activity, pReqCode, permissions, grantResults);
            }
            catch (final Exception e) {
               e.printStackTrace();
            }
            return ;
        }

        //若是M版本以上，则直接调用接口
        int alreadyGranted = 0;
        for (final String permission : permissions) {
            alreadyGranted += (isGranted(activity, permission) ? 1 : 0);
        }

        if (alreadyGranted == permissions.length) {
            final int[] grantResults = new int[alreadyGranted];
            for (int i = 0; i < alreadyGranted; i++) {
                grantResults[i] = PackageManager.PERMISSION_GRANTED;
            }
            activity.onRequestPermissionsResult(pReqCode, permissions, grantResults);
            return ;
        }

        ActivityCompat.requestPermissions(activity, permissions, pReqCode);
    }

    /**
     * 判断指定的权限是否需要申请验证，对于normal级别权限不做处理
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean isRevocable(final Context context, final String permission) {
        if (Build.VERSION.SDK_INT < MARSHMALLOW) {
            return false;
        }

        final PackageManager packageManager = context.getPackageManager();
        if (packageManager == null) {
            return false;
        }

        try {
            final PermissionInfo permissionInfo = packageManager.getPermissionInfo(permission, 0);
            @SuppressLint("InlinedApi") // API-16+
            final int protectionLevel = (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE);
            return (protectionLevel != PermissionInfo.PROTECTION_NORMAL);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断指定一组权限是否需要申请验证，对于normal级别权限不做处理，必须属于一个分组，否则判断无效
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean isAnyRevocable(final Context context, final String[] permissions) {
        for (final String permission : permissions) {
            if (isRevocable(context, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断指定一组权限是否需要申请验证，对于normal级别权限不做处理，支持混合模式
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean areAllRevocable(final Context context, final String[] permissions) {
        for (final String permission : permissions) {
            if (! isRevocable(context, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对给出的权限进行组装是否已经允许、不允许
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static Map<String, Boolean> onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        final Map<String, Boolean> results = new HashMap<String, Boolean>();
        for (int i = 0; i < permissions.length; i++) {
            results.put(permissions[i], ((grantResults[i] == PackageManager.PERMISSION_GRANTED) ? Boolean.TRUE : Boolean.FALSE));
        }
        return results;
    }


    /**
     * 获取储存权限
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if((activity.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                    ( activity.checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                Logger.i(TAG,"Permission android.permission is required in AndroidManifest.xml");
                activity.requestPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
                return false;
            }
        }
        return true;
    }

    public static boolean isGrantReadPhoneStatePermission(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                if((context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)){
                    return false;
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    public static boolean isGrantWriteExternalStoragePermission(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                if((context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                    return false;
                }
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

}
