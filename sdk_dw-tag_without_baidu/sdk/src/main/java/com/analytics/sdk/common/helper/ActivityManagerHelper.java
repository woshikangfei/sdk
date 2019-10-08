package com.analytics.sdk.common.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class ActivityManagerHelper {


    public static Collection<String> getAllPacakgename(Context context) {
        return getAllPacakgename(context, true);
    }

    public static Collection<String> getAllPacakgename(Context context, boolean paramBoolean) {
        return getAllPacakgename(context, paramBoolean, -1);
    }

    public static Collection<String> getAllPacakgename(Context context, boolean paramBoolean, int paramInt) {
        LinkedList linkList = new LinkedList();
        Iterator<ApplicationInfo> iter = context.getPackageManager().getInstalledApplications(0).iterator();
        do {
            ApplicationInfo localApplicationInfo = null;
            do {
                if (!iter.hasNext()) {
                    break;
                }
                localApplicationInfo = iter.next();
            } while ((isinstallSystem(localApplicationInfo)) && (!paramBoolean));
            linkList.add(localApplicationInfo.packageName);
        } while ((paramInt <= 0) || (linkList.size() <= paramInt));
        return linkList;
    }


    /**
     * application里的所有组件是否禁用
     * @param info
     * @return
     */
    public static boolean isenable(ApplicationInfo info) {
        return info.enabled;
    }

    /**
     * 判断某个包的组件是否可用
     * @param context
     * @param packages
     * @return
     */
    public static boolean isenable(Context context, String packages) {
        try {
            boolean bool = isenable(context.getPackageManager().getApplicationInfo(packages, 0));
            return bool;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return false;
    }

    /**
     * 判断APK的安装位置是不是在/system目录下面
     * @param info
     * @return
     */
    public static boolean isinstallSystem(ApplicationInfo info) {
        //获得apk的目录或者jar的目录
        String dir = info.sourceDir;
        if (dir == null) {
            return false;
        }
        return dir.contains("/system/");
    }



    public static ApplicationInfo getApplicationInfo(Context context, String paramString) {
        try {
            return context.getPackageManager().getApplicationInfo(paramString, 0);
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return null;
    }

    /**
     * 判断当前的应用包是否存活
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isLive(Context context, String packageName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Iterator<ActivityManager.RunningServiceInfo> localObject = manager.getRunningServices(Integer.MAX_VALUE).iterator();
        while (localObject.hasNext()) {
            if (packageName.equals((localObject.next()).service.getPackageName())) {
                return true;
            }
        }
        Iterator<ActivityManager.RunningAppProcessInfo> iter = manager.getRunningAppProcesses().iterator();
        while (iter.hasNext()) {
            String[] arrayOfString = (iter.next()).pkgList;
            int j = arrayOfString.length;
            int i = 0;
            while (i < j) {
                if (packageName.equals(arrayOfString[i])) {
                    return true;
                }
                i += 1;
            }
        }
        try {
            Iterator<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(Integer.MAX_VALUE).iterator();
            while (task.hasNext()) {
                ActivityManager.RunningTaskInfo info = task.next();
                if (packageName.equals(info.baseActivity.getPackageName())) {
                    return true;
                }
                return packageName.equals(info.topActivity.getPackageName());
            }
        } catch (Exception ex) {
        }
        return false;
    }

    /**
     * 判断某个APP是否存在
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isExists(Context context, String packageName) {
        if (packageName == null) {
        }
        if (getApplicationInfo(context, packageName) == null) {
            return false;
        }
        return true;
    }



    /**
     * 检测某个权限是否可也使用
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkPermission(Context context, String packageName) {
        return context.getPackageManager().checkPermission(packageName, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }
}
