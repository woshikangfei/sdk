package com.adsdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class DevMainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GlobalConfig.RConfig.MAIN_ACTIVITY_DEV_LAYOUT_ID);

        this.findViewById(GlobalConfig.RConfig.MAIN_ACTIVITY_BTN_ID).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "xx", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


//        Logger.i("DevMainActivity","DevMainActivity#dispatchTouchEvent enter , action = " + SdkHelper.getMotionEventActionString(event));

//        IAdStrategyService.CallResult callResult = adStrategyService.dispatchTouchEvent(adStragegyWorkArgs);
//        if(IAdStrategyService.CallResult.CALL_RECURSION == callResult) {
//            return dispatchTouchEvent(adStragegyWorkArgs.event);
//        } else if(IAdStrategyService.CallResult.CALL_SUPER == callResult) {
//            return super.dispatchTouchEvent(adStragegyWorkArgs.event);
//        } else {
//            return super.dispatchTouchEvent(adStragegyWorkArgs.event);
//        }

        return super.dispatchTouchEvent(event);

    }
}
