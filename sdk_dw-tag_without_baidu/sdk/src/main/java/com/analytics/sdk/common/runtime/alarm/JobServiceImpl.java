package com.analytics.sdk.common.runtime.alarm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobServiceImpl implements AlarmTask{

    static final String TAG = JobServiceImpl.class.getSimpleName();

    private Context mContext;
    private int jobId = 1000086;

    public JobServiceImpl(Context context){
        try {
            this.mContext = context;
        } catch (Throwable localThrowable) {
            localThrowable.printStackTrace();
            LogControl.info(TAG,"getSystemService JOB_SCHEDULER_SERVICE  = " + localThrowable);
        }
    }

    @Override
    public boolean run(Alarm alarm) {

        start(mContext,alarm);

        return true;
    }

    static void start(Context context,Alarm alarm){

        LogControl.info(TAG,"start enter");

        JobScheduler jobScheduler = ((JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE));

        if(jobScheduler == null){
            LogControl.info(TAG,"run mJobScheduler is null , return");
            return;
        }

        int senconds = alarm.getAlarmDelaySeconds();
        int alarmId = alarm.getAlarmId();

        ComponentName componentName = new ComponentName(context.getPackageName(), AlarmJobService.class.getName());

        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putInt(AlarmJobService.EXTRA_ALARM_ID,alarmId);

        jobScheduler.schedule(new JobInfo.Builder(alarmId, componentName).setMinimumLatency(senconds * 1000).setExtras(persistableBundle).setOverrideDeadline(senconds * 1000).setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE).build());

        LogControl.info(TAG,"run completed , senconds = " + senconds);
    }

    @Override
    public boolean cancel(Alarm alarm) {
        JobScheduler jobScheduler = ((JobScheduler)mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE));
        if(jobScheduler == null){
            LogControl.info(TAG,"cancel mJobScheduler is null , return");
            return false;
        }

        AlarmService.remove(alarm.getAlarmId());

        jobScheduler.cancel(alarm.getAlarmId());

        return true;
    }

    public static class AlarmJobService extends JobService {
        static final String TAG = AlarmJobService.class.getSimpleName();

        public static final String ACTION_ALARM_RECEIVER = "com.devy.action.ALARM_RECEIVER";
        public static final String EXTRA_ALARM_ID = "com.devy.extra.ALARM_ID";

        public boolean onStartJob(JobParameters paramJobParameters) {
            LogControl.info(TAG,"onStartJob enter");

            PersistableBundle persistableBundle = paramJobParameters.getExtras();
            if(persistableBundle!=null){
                int alarmId = persistableBundle.getInt(EXTRA_ALARM_ID);
                LogControl.info(TAG,"onStartJob alarmId = " + alarmId);

                Alarm alarm = AlarmService.handle(alarmId);

                if(alarm != null && alarm.isLooping()) {
                    start(getApplicationContext(),alarm);
                }

            }

            jobFinished(paramJobParameters,false);
            return true;
        }

        public boolean onStopJob(JobParameters paramJobParameters) {
            LogControl.info(TAG,"onStopJob enter");
            return false;
        }
    }

}
