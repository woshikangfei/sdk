package com.analytics.sdk.common.network;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Uses {@link ConnectivityManager} to identify connectivity changes.
 */
public final class ConnectivityMonitor {
  private static final String TAG = "ConnectivityListener";
  private final Context context;
  private ConnectivityListener listener;

  boolean isConnected;
  private boolean isRegistered;

  private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
      boolean wasConnected = isConnected;
      isConnected = isConnected(context);
      if (wasConnected != isConnected) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
          Log.d(TAG, "connectivity changed, isConnected: " + isConnected);
        }

        listener.onConnectivityChanged(isConnected);
      }
    }
  };

  public ConnectivityMonitor(@NonNull Context context, @NonNull ConnectivityListener listener) {
    this.context = context.getApplicationContext();
    this.listener = (listener == null ? ConnectivityListener.EMPTY : listener);
  }

  public static ConnectivityMonitor startNewMonitor(Context context, ConnectivityListener listener){
    ConnectivityMonitor connectivityMonitor = new ConnectivityMonitor(context,listener);
    connectivityMonitor.start();
    return connectivityMonitor;
  }

  private void register() {
    if (isRegistered) {
      return;
    }

    // Initialize isConnected.
    isConnected = isConnected(context);
    try {
      // See #1405
      context.registerReceiver(connectivityReceiver,
              new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
      isRegistered = true;
    } catch (SecurityException e) {
      // See #1417, registering the receiver can throw SecurityException.
      if (Log.isLoggable(TAG, Log.WARN)) {
        Log.w(TAG, "Failed to register", e);
      }
    }
  }

  private void unregister() {
    if (!isRegistered) {
      return;
    }

    context.unregisterReceiver(connectivityReceiver);
    isRegistered = false;
  }

  @SuppressWarnings("WeakerAccess")
  // Permissions are checked in the factory instead.
  @SuppressLint("MissingPermission")
  boolean isConnected(@NonNull Context context) {
    NetworkInfo networkInfo;
    try {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      networkInfo = connectivityManager.getActiveNetworkInfo();
    } catch (RuntimeException e) {
      // #1405 shows that this throws a SecurityException.
      // b/70869360 shows that this throws NullPointerException on APIs 22, 23, and 24.
      // b/70869360 also shows that this throws RuntimeException on API 24 and 25.
      if (Log.isLoggable(TAG, Log.WARN)) {
        Log.w(TAG, "Failed to determine connectivity status when connectivity changed", e);
      }
      // Default to true;
      return true;
    }
    return networkInfo != null && networkInfo.isConnected();
  }

  public void start() {
    register();
  }

  public void stop() {
    unregister();
  }

}