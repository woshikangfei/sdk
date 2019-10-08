package com.adsdk.demo.pointdrawer;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.adsdk.demo.R;

import java.util.List;

public class PointDrawerActivity extends Activity {

    private static final String TAG = "PointDrawerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_point_drawer);

        final PointDrawer pointDrawer = this.findViewById(R.id.point_drawer);
        final Handler mainHandler = new Handler();

        PointReader.startRead(this, new PointReader.Callback() {
            @Override
            public void onRealy(final List<Point> pointList) {

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pointDrawer.setPointList(pointList);
                    }
                });

            }
        });

    }

}
