package com.analytics.sdk.common.network;

import com.analytics.sdk.common.network.sample.ConnectionClassManager;
import com.analytics.sdk.common.network.sample.ConnectionQuality;
import com.analytics.sdk.common.network.sample.DeviceBandwidthSampler;

public class TrafficTracker {

    public static void beginTrace(){
        DeviceBandwidthSampler.getInstance().startSampling();
    }

    public static TrackResult endTrace(){
        TrackResult trackResult = new TrackResult();

        trackResult.currentBandwidthQuality = ConnectionClassManager.getInstance().getCurrentBandwidthQuality();
        trackResult.downloadKBitsPerSecond = (int)ConnectionClassManager.getInstance().getDownloadKBitsPerSecond();

        ConnectionClassManager.getInstance().reset();
        DeviceBandwidthSampler.getInstance().stopSampling();

        return trackResult;
    }

    public static class TrackResult {
        public ConnectionQuality currentBandwidthQuality;
        public int downloadKBitsPerSecond;

        @Override
        public String toString() {
            return "TrackResult{" +
                    "currentBandwidthQuality=" + currentBandwidthQuality +
                    ", downloadKBitsPerSecond=" + downloadKBitsPerSecond +
                    '}';
        }
    }

}
