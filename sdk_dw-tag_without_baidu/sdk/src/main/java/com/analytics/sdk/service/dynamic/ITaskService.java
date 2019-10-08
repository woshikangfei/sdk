package com.analytics.sdk.service.dynamic;

import com.analytics.sdk.service.IService;

import java.lang.reflect.Method;

public interface ITaskService extends IService {

    ExecuteArgs EMPTY_EXECUTE_ARGS = new ExecuteArgs();

    enum ExecuteResult {
        ABORT,VOID
    }

    String[] beforeMethodSignArray();
    String[] afterMethodSignArray();
    ExecuteResult execute(IDynamicContext dynamicContext,ExecuteArgs taskArgs);

    class ExecuteArgs {

        public Object realImpl;
        public Object[] methodArgs;
        public Method method;
        public String action;

        public static ExecuteArgs obtain(Object realImpl,Object[] methodArgs,Method method,String action){
            ExecuteArgs executeArgs = new ExecuteArgs();
            executeArgs.realImpl = realImpl;
            executeArgs.methodArgs = methodArgs;
            executeArgs.method = method;
            return executeArgs;
        }

        public boolean isEmpty(){
            return (EMPTY_EXECUTE_ARGS == this);
        }

    }
}
