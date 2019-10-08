package com.adsdk.demo.pointdrawer;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PointReader {

    static final String TAG = PointReader.class.getSimpleName();

    public interface Callback {
        void onRealy(List<Point> pointList);
    }

    public static void startRead(final Context context,final Callback callback){

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Point> pointList = new ArrayList<>();

                String lineTxt = null;
                try {
                    InputStreamReader read = new InputStreamReader(context.getAssets().open("point_list.csv"));
                    BufferedReader bufferedReader = new BufferedReader(read);

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

                    int width = wm.getDefaultDisplay().getWidth();
                    int height = wm.getDefaultDisplay().getHeight();

                    bufferedReader.readLine();
                    //从第二行开始读
                    lineTxt = bufferedReader.readLine();
                    while (lineTxt != null) {

                        String[] feedTypes = lineTxt.split(",");

                        String xString = feedTypes[0];
                        String yString = feedTypes[1];

                        xString = xString.substring(1,xString.length()-1);
                        yString = yString.substring(1,yString.length()-1);

                        float x1 = width  * Float.valueOf(xString);
                        float y1 =  height * Float.valueOf(yString);

                        int fx = (int) x1;
                        int fy = (int) y1;

                        Log.i(TAG,"x = " + xString + " , y = " + yString + " , fx = " + fx + " , fy = " + fy);

                        pointList.add(new Point(fx,fy));

                        lineTxt = bufferedReader.readLine();

                    }

                    read.close();

                    Log.i(TAG,"pointListSize = " + pointList.size());

                    callback.onRealy(pointList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
