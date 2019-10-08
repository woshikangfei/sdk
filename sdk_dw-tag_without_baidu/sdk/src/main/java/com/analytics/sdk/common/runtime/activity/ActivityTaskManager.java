package com.analytics.sdk.common.runtime.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContextWrapper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityTaskManager {

    private List<WeakReference<Activity>> activityList = new ArrayList<WeakReference<Activity>>();
    public  static Dialog sReminderDialog;

    private static ActivityTaskManager sInstance = null;


    private ActivityTaskManager(){
    }

    /**
     * Get the instance of this.
     * 
     * @return instance
     */
    public static synchronized ActivityTaskManager getInstance() {
        if (sInstance == null) {
            sInstance = new ActivityTaskManager();
        }
        return sInstance;
    }

    public List<WeakReference<Activity>> getActivityList(){
        return activityList;
    }

    public Activity peekTopActivity(){
        if(activityList!=null && activityList.size()>0){
            WeakReference<Activity> ref = activityList.get(activityList.size()-1);
            if(ref!=null && ref.get()!=null){
                return ref.get();
            }
        }
        return null;
    }

    public void pushToActivityStack(Activity activity) {
        activityList.add(new WeakReference<Activity>(activity));
    }

    public void popFromActivityStack(Activity activity) {
        if(sReminderDialog!=null &&
                (sReminderDialog.getContext()==activity ||
                        (sReminderDialog.getContext() instanceof ContextWrapper && ((ContextWrapper)sReminderDialog.getContext()).getBaseContext()==activity))){
            try{
                sReminderDialog.dismiss();
            }catch (Throwable e){}finally {
                sReminderDialog = null;
            }
        }
        for(int x=0; x<activityList.size(); x++){
            WeakReference<Activity> ref = activityList.get(x);
            if(ref!=null && ref.get()!=null && ref.get()==activity){
                activityList.remove(ref);
            }
        }
    }

    public void clearActivityStack(){
        try {
            for (WeakReference<Activity> ref : activityList) {
                if (ref != null && ref.get() != null && !ref.get().isFinishing()) {
                    ref.get().finish();
                }
            }
        }catch (Throwable e){

        }finally {
            activityList.clear();
        }
    }

    public boolean isActivityStackEmpty() {
        return activityList.size() == 0;
    }
    
	public int sizeOfActivityStack() {
		return activityList.size();
	}

}
