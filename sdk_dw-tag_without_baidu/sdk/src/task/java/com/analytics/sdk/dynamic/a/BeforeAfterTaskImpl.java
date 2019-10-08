package com.analytics.sdk.dynamic.a;

import android.util.Log;

import com.analytics.sdk.service.AbstractService;
import com.analytics.sdk.service.dynamic.IDynamicContext;
import com.analytics.sdk.service.dynamic.ITaskService;

import java.io.File;

public class BeforeAfterTaskImpl extends AbstractService implements ITaskService {

    static final String TAG = BeforeAfterTaskImpl.class.getSimpleName();

    public BeforeAfterTaskImpl() {
        super(ITaskService.class);
    }

    @Override
    public String[] beforeMethodSignArray() {
        return new String[]{"IClientServcie#loadSplashAd"};
    }

    @Override
    public String[] afterMethodSignArray() {
        return new String[]{"IClientServcie#loadSplashAd"};
    }

    @Override
    public ExecuteResult execute(IDynamicContext dynamicContext, ExecuteArgs executeArgs) {
        Log.i(TAG,"execute enter #1");

        Log.i(TAG,"executeArgs method = " + executeArgs.method);
        Log.i(TAG,"executeArgs methodArgs = " + executeArgs.methodArgs);
        Log.i(TAG,"executeArgs realImpl = " + executeArgs.realImpl);
        Log.i(TAG,"executeArgs executeArgs.isEmpty = " + executeArgs.isEmpty());

        return ExecuteResult.VOID;
    }

}
