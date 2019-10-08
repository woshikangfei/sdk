package com.analytics.sdk.view.strategy.crack;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toolbar;

import com.analytics.sdk.common.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

public class ProxyActivity extends Activity {

    //百度测试时的包名
//    public static String PROXY_PACKAGE_NAME = "com.mengxiang.mymusic";

    public static String PROXY_PACKAGE_NAME = "";

    Activity context;

    public ProxyActivity(Activity context) {
        this.context = context;
    }

    @Override
    public String getPackageName() {
//        new Exception("getPackageName " + PROXY_PACKAGE_NAME).printStackTrace();
        return PROXY_PACKAGE_NAME;
    }

    public String getBasePackageName() {
        return PROXY_PACKAGE_NAME;
    }

    @Override
    public Context getBaseContext() {
        return new ProxyContext(context.getBaseContext(),PROXY_PACKAGE_NAME);
    }

    @Override
    public Window getWindow() {
        return context.getWindow();
    }

    @Override
    public WindowManager getWindowManager() {
        return context.getWindowManager();
    }

    @Override
    public AssetManager getAssets() {
        return context.getAssets();
    }

    @Override
    public Resources getResources() {
        return context.getResources();
    }

    @Override
    public PackageManager getPackageManager() {
        return context.getPackageManager();
    }

    @Override
    public ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

    @Override
    public Looper getMainLooper() {
        return context.getMainLooper();
    }

    @Override
    public Context getApplicationContext() {
        return new ProxyContext(context, PROXY_PACKAGE_NAME);
    }

    @Override
    public void setTheme(int resid) {
        context.setTheme(resid);
    }

    @Override
    public Resources.Theme getTheme() {
        return context.getTheme();
    }

    @Override
    public ClassLoader getClassLoader() {
        return context.getClassLoader();
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        ApplicationInfo applicationInfo =  context.getApplicationInfo();
        applicationInfo.packageName = PROXY_PACKAGE_NAME;
        applicationInfo.processName = ProxyActivity.PROXY_PACKAGE_NAME;
        return applicationInfo;
    }

    @Override
    public String getPackageResourcePath() {
        return context.getPackageResourcePath();
    }

    @Override
    public String getPackageCodePath() {
        return context.getPackageCodePath();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return context.getSharedPreferences(name, mode);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean moveSharedPreferencesFrom(Context sourceContext, String name) {
        return context.moveSharedPreferencesFrom(sourceContext, name);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean deleteSharedPreferences(String name) {
        return context.deleteSharedPreferences(name);
    }

    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return context.openFileInput(name);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return context.openFileOutput(name, mode);
    }

    @Override
    public boolean deleteFile(String name) {
        return context.deleteFile(name);
    }

    @Override
    public File getFileStreamPath(String name) {
        return context.getFileStreamPath(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public File getDataDir() {
        return context.getDataDir();
    }

    @Override
    public File getFilesDir() {
        return context.getFilesDir();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getNoBackupFilesDir() {
        return context.getNoBackupFilesDir();
    }

    @Nullable
    @Override
    public File getExternalFilesDir(@Nullable String type) {
        return context.getExternalFilesDir(type);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalFilesDirs(String type) {
        return context.getExternalFilesDirs(type);
    }

    @Override
    public File getObbDir() {
        return context.getObbDir();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getObbDirs() {
        return context.getObbDirs();
    }

    @Override
    public File getCacheDir() {
        return context.getCacheDir();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File getCodeCacheDir() {
        return context.getCodeCacheDir();
    }

    @Nullable
    @Override
    public File getExternalCacheDir() {
        return context.getExternalCacheDir();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public File[] getExternalCacheDirs() {
        return context.getExternalCacheDirs();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public File[] getExternalMediaDirs() {
        return context.getExternalMediaDirs();
    }

    @Override
    public String[] fileList() {
        return context.fileList();
    }

    @Override
    public File getDir(String name, int mode) {
        return context.getDir(name, mode);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return context.openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, @Nullable DatabaseErrorHandler errorHandler) {
        return context.openOrCreateDatabase(name, mode, factory, errorHandler);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        return context.moveDatabaseFrom(sourceContext, name);
    }

    @Override
    public boolean deleteDatabase(String name) {
        return context.deleteDatabase(name);
    }

    @Override
    public File getDatabasePath(String name) {
        return context.getDatabasePath(name);
    }

    @Override
    public String[] databaseList() {
        return context.databaseList();
    }

    @Override
    public Drawable getWallpaper() {
        return context.getWallpaper();
    }

    @Override
    public Drawable peekWallpaper() {
        return context.peekWallpaper();
    }

    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return context.getWallpaperDesiredMinimumWidth();
    }

    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return context.getWallpaperDesiredMinimumHeight();
    }

    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        context.setWallpaper(bitmap);
    }

    @Override
    public void setWallpaper(InputStream data) throws IOException {
        context.setWallpaper(data);
    }

    @Override
    public void clearWallpaper() throws IOException {
        context.clearWallpaper();
    }

    @Override
    public void startActivity(Intent intent) {
        context.startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        context.startActivity(intent, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        context.startActivities(intents);
    }

    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        context.startActivities(intents, options);
    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        context.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
    }

    @Override
    public void startIntentSender(IntentSender intent, @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, @Nullable Bundle options) throws IntentSender.SendIntentException {
        context.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        context.sendBroadcast(intent);
    }

    @Override
    public void sendBroadcast(Intent intent, @Nullable String receiverPermission) {
        context.sendBroadcast(intent, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(Intent intent, @Nullable String receiverPermission) {
        context.sendBroadcast(intent,receiverPermission);
    }

    @Override
    public void sendOrderedBroadcast(@NonNull Intent intent, @Nullable String receiverPermission, @Nullable BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        context.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        context.sendBroadcastAsUser(intent, user);
    }

    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission) {
        context.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, @Nullable String receiverPermission, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        context.sendBroadcastAsUser(intent, user, receiverPermission);
    }

    @Override
    public void sendStickyBroadcast(Intent intent) {
        context.sendStickyBroadcast(intent);
    }

    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        context.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void removeStickyBroadcast(Intent intent) {
        context.removeStickyBroadcast(intent);
    }

    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        context.sendStickyBroadcastAsUser(intent, user);
    }

    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver, @Nullable Handler scheduler, int initialCode, @Nullable String initialData, @Nullable Bundle initialExtras) {
        context.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData, initialExtras);
    }

    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        context.removeStickyBroadcastAsUser(intent, user);
    }

    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter) {
        return context.registerReceiver(receiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Intent registerReceiver(@Nullable BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return context.registerReceiver(receiver, filter, flags);
    }

    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler) {
        return context.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, @Nullable String broadcastPermission, @Nullable Handler scheduler, int flags) {
        return context.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public ComponentName startService(Intent service) {
        return context.startService(service);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        return context.startForegroundService(service);
    }

    @Override
    public boolean stopService(Intent service) {
        return context.stopService(service);
    }

    @Override
    public boolean bindService(Intent service, @NonNull ServiceConnection conn, int flags) {
        return context.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(@NonNull ServiceConnection conn) {
        context.unbindService(conn);
    }

    @Override
    public boolean startInstrumentation(@NonNull ComponentName className, @Nullable String profileFile, @Nullable Bundle arguments) {
        return context.startInstrumentation(className, profileFile, arguments);
    }

    @Nullable
    @Override
    public Object getSystemService(@NonNull String name) {
        return context.getSystemService(name);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public String getSystemServiceName(@NonNull Class<?> serviceClass) {
        return context.getSystemServiceName(serviceClass);
    }

    @Override
    public int checkPermission(@NonNull String permission, int pid, int uid) {
        return context.checkPermission(permission, pid, uid);
    }

    @Override
    public int checkCallingPermission(@NonNull String permission) {
        return context.checkCallingPermission(permission);
    }

    @Override
    public int checkCallingOrSelfPermission(@NonNull String permission) {
        return context.checkCallingOrSelfPermission(permission);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int checkSelfPermission(@NonNull String permission) {
        return context.checkSelfPermission(permission);
    }

    @Override
    public void enforcePermission(@NonNull String permission, int pid, int uid, @Nullable String message) {
        context.enforcePermission(permission, pid, uid, message);
    }

    @Override
    public void enforceCallingPermission(@NonNull String permission, @Nullable String message) {
        context.enforceCallingPermission(permission, message);
    }

    @Override
    public void enforceCallingOrSelfPermission(@NonNull String permission, @Nullable String message) {
        context.enforceCallingOrSelfPermission(permission, message);
    }

    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        context.grantUriPermission(toPackage, uri, modeFlags);
    }

    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        context.revokeUriPermission(uri,modeFlags);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void revokeUriPermission(String toPackage, Uri uri, int modeFlags) {
        context.revokeUriPermission(toPackage, uri, modeFlags);
    }

    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return context.checkUriPermission(uri, pid, uid, modeFlags);
    }

    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return context.checkCallingUriPermission(uri, modeFlags);
    }

    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return context.checkCallingOrSelfUriPermission(uri, modeFlags);
    }

    @Override
    public int checkUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags) {
        return context.checkUriPermission(uri, pid, uid, modeFlags);
    }

    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        context.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        context.enforceCallingUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        context.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }

    @Override
    public void enforceUriPermission(@Nullable Uri uri, @Nullable String readPermission, @Nullable String writePermission, int pid, int uid, int modeFlags, @Nullable String message) {
        context.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }

    @Override
    public Context createPackageContext(String packageName, int flags) throws PackageManager.NameNotFoundException {
        return context.createPackageContext(packageName, flags);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Context createContextForSplit(String splitName) throws PackageManager.NameNotFoundException {
        return context.createContextForSplit(splitName);
    }

    @Override
    public Context createConfigurationContext(@NonNull Configuration overrideConfiguration) {
        return context.createConfigurationContext(overrideConfiguration);
    }

    @Override
    public Context createDisplayContext(@NonNull Display display) {
        return context.createDisplayContext(display);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Context createDeviceProtectedStorageContext() {
        return context.createDeviceProtectedStorageContext();
    }

    @Override
    public Intent getIntent() {
        return context.getIntent();
    }

    @Override
    public View getCurrentFocus() {
        return context.getCurrentFocus();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }

    @Override
    public void setVisible(boolean visible) {
        context.setVisible(visible);
    }

    @Override
    public boolean isFinishing() {
        return context.isFinishing();
    }

    @Override
    public boolean isDestroyed() {
        return context.isDestroyed();
    }

    @Override
    public void finish() {
        context.finish();
        super.finish();
    }

    @Override
    public int getTaskId() {
        return context.getTaskId();
    }

    @Override
    public ComponentName getComponentName() {
        return context.getComponentName();
    }

    @Override
    public void setTitle(CharSequence title) {
        context.setTitle(title);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        context.addContentView(view,params);
    }

    @Override
    public void setTitle(int titleId) {
        context.setTitle(titleId);
    }

    @Override
    public ActionBar getActionBar() {
        return context.getActionBar();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setActionBar(Toolbar toolbar) {
        context.setActionBar(toolbar);
    }

    @Override
    public void setContentView(int layoutResID) {
        context.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        context.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        context.setContentView(view, params);
    }

    @Override
    public boolean hasWindowFocus() {
        return context.hasWindowFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    @Override
    public void setTurnScreenOn(boolean turnScreenOn) {
        context.setTurnScreenOn(turnScreenOn);
    }

    @Override
    public void openOptionsMenu() {
        context.openOptionsMenu();
    }

    @Override
    public void registerForContextMenu(View view) {
        context.registerForContextMenu(view);
    }

    @Override
    public void openContextMenu(View view) {
        context.openContextMenu(view);
    }

    @Override
    public void closeContextMenu() {
        context.closeContextMenu();
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return context.getLayoutInflater();
    }

    @Override
    public Intent getParentActivityIntent() {
        return context.getParentActivityIntent();
    }

    @Override
    public View onCreateView(String name, Context c, AttributeSet attrs) {
        return context.onCreateView(name, c, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context c, AttributeSet attrs) {
        return context.onCreateView(parent, name, c, attrs);
    }

    public Activity getContext() {
        return context;
    }

    @Override
    public FragmentManager getFragmentManager() {
        return context.getFragmentManager();
    }

    @Override
    public int getChangingConfigurations() {
        return context.getChangingConfigurations();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Scene getContentScene() {
        return context.getContentScene();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public TransitionManager getContentTransitionManager() {
        return context.getContentTransitionManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public Uri getReferrer() {
        return context.getReferrer();
    }

    @Override
    public int getRequestedOrientation() {
        return context.getRequestedOrientation();
    }

    @Override
    public String getLocalClassName() {
        return context.getLocalClassName();
    }

    @Override
    public ComponentName getCallingActivity() {
        return context.getCallingActivity();
    }

    @Override
    public String getCallingPackage() {
        return context.getCallingPackage();
    }

    @Override
    public SharedPreferences getPreferences(int mode) {
        return context.getPreferences(mode);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return context.getMenuInflater();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean isDeviceProtectedStorage() {
        return context.isDeviceProtectedStorage();
    }

}
