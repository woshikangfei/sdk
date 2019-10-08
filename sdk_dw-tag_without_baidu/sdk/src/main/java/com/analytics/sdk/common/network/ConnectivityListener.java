package com.analytics.sdk.common.network;

import com.analytics.sdk.common.log.Logger;

/**
 * 网络状态监听器
 */
public interface ConnectivityListener {

  ConnectivityListener EMPTY = new ConnectivityListener() {
    @Override
    public void onConnectivityChanged(boolean isConnected) {
      Logger.i("ConnectivityListener","onConnectivityChanged isConnected = " + isConnected);
    }
  };

  void onConnectivityChanged(boolean isConnected);
}
