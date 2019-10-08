/*
 *  Copyright (c) 2015, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.analytics.sdk.common.network.sample;

import android.util.Log;

/**
 * Moving average calculation for ConnectionClass.
 */
class ExponentialGeometricAverage {

  private final double mDecayConstant;
  private final int mCutover;

  private volatile double mValue = -1;
  private int mCount;

  public ExponentialGeometricAverage(double decayConstant) {
    mDecayConstant = decayConstant;
    mCutover = decayConstant == 0.0
        ? Integer.MAX_VALUE
        : (int) Math.ceil(1 / decayConstant);
  }

  /**
   * Adds a new measurement to the moving average.
   * @param measurement - Bandwidth measurement in bits/ms to add to the moving average.
   */
  public void addMeasurement(double measurement) {
    double keepConstant = 1 - mDecayConstant;
    if (mCount > mCutover) {
      mValue = Math.exp(keepConstant * Math.log(mValue) + mDecayConstant * Math.log(measurement));
      Log.i("networkTrace","addMeasurement#1 mValue = " + mValue);
    } else if (mCount > 0) {
      double retained = keepConstant * mCount / (mCount + 1.0);
      double newcomer = 1.0 - retained;
      mValue = Math.exp(retained * Math.log(mValue) + newcomer * Math.log(measurement));
      Log.i("networkTrace","addMeasurement#2 mValue = " + mValue);
    } else {
      mValue = measurement;
      Log.i("networkTrace","addMeasurement#3 mValue = " + mValue);
    }
    mCount++;
  }

  public double getAverage() {
    Log.i("networkTrace","getAverage mValue = " + mValue);
    return mValue;
  }

  /**
   * Reset the moving average.
   */
  public void reset() {
    mValue = -1.0;
    mCount = 0;
  }
}
