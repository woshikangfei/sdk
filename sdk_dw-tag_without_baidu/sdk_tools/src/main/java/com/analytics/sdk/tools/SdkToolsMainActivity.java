package com.analytics.sdk.tools;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SdkToolsMainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = SdkToolsMainActivity.class.getName();

    /**
     * 打开LOG
     */
    private Button mCmdOpenLog;
    public static String PACKAGE;
    /**
     * 打印信息
     */
    private Button mCmdPrintInfo;
    /**
     * 检查混淆
     */
    private Button mCmdProguard;
    /**
     * 开启绘制热力图
     */
    private Button mClickMapOpen;
    /**
     * 开启绘制单元格数值
     */
    private Button mClickMapOpenCellValue;
    /**
     * 输入个数
     */
    private EditText mEditText;
    /**
     * 开启热力图模拟点击
     */
    private Button mClickMapOpenTestPoints;
    /**
     * 读取服务器热力图配置
     */
    private Button mClickMapPrint;
    /**
     * 开启坐标漂移
     */
    private Button mClickStrategyForceOpen;
    /**
     * 查看热修复和任务信息
     */
    private Button mDynamicPrint;
    /**
     * 查看hack状态
     */
    private Button mHackQuery;
    /**
     * 输出日志到文件
     */
    private Button mLog2file;
    /**
     * 清除缓存
     */
    private Button mCacheClear;
    /**
     * 切换环境
     */
    private Button mChangeEnv;

    private int SWITCH = 1;
    private int VIEWLOG = 2;
    private int OTHER = 3;
    private boolean b;
    private View inflate;
    /**
     * 修改调试路径
     */
    private Button mOpenDebugPluginPath;
    /**
     * 执行dex
     */
    private Button mExecuteDex;
    /**
     * 打印缓存地址
     */
    private Button mPrintCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = LayoutInflater.from(this).inflate(R.layout.activity_sdk_tools_main, null);
        setContentView(inflate);
        initView();
    }

    private void initView() {
        mCmdOpenLog = (Button) findViewById(R.id.cmd_open_log);
        mCmdOpenLog.setOnClickListener(this);
        mCmdPrintInfo = (Button) findViewById(R.id.com_print_info);
        mCmdPrintInfo.setOnClickListener(this);
        mCmdProguard = (Button) findViewById(R.id.cmd_proguard);
        mCmdProguard.setOnClickListener(this);
        mClickMapOpen = (Button) findViewById(R.id.click_map_open);
        mClickMapOpen.setOnClickListener(this);
        mClickMapOpenCellValue = (Button) findViewById(R.id.click_map_open_cell_value);
        mClickMapOpenCellValue.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.setOnClickListener(this);
        mClickMapOpenTestPoints = (Button) findViewById(R.id.click_map_open_test_points);
        mClickMapOpenTestPoints.setOnClickListener(this);
        mClickMapPrint = (Button) findViewById(R.id.click_map_print);
        mClickMapPrint.setOnClickListener(this);
        mClickStrategyForceOpen = (Button) findViewById(R.id.click_strategy_force_open);
        mClickStrategyForceOpen.setOnClickListener(this);
        mDynamicPrint = (Button) findViewById(R.id.dynamic_print);
        mDynamicPrint.setOnClickListener(this);
        mHackQuery = (Button) findViewById(R.id.hack_query);
        mHackQuery.setOnClickListener(this);
        mLog2file = (Button) findViewById(R.id.log2file);
        mLog2file.setOnClickListener(this);
        mCacheClear = (Button) findViewById(R.id.cache_clear);
        mCacheClear.setOnClickListener(this);
        mChangeEnv = (Button) findViewById(R.id.change_env);
        mChangeEnv.setOnClickListener(this);
        mOpenDebugPluginPath = (Button) findViewById(R.id.open_debug_plugin_path);
        mOpenDebugPluginPath.setOnClickListener(this);
        mExecuteDex = (Button) findViewById(R.id.execute_dex);
        mExecuteDex.setOnClickListener(this);
        mPrintCache = (Button) findViewById(R.id.print_cache);
        mPrintCache.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.cmd_open_log:
                //打开LOG
                cmd_open_log(mCmdOpenLog, CmdIntent.ACTION_LOG);
                break;
            case R.id.com_print_info:
                //打印信息
                dispose(VIEWLOG, CmdIntent.ACTION_PRINT_CONFIG);
                break;
            case R.id.cmd_proguard:
                //检查混淆
                dispose(OTHER, CmdIntent.ACTION_PROGUARD);
                break;
            case R.id.click_map_open:
                //开启绘制热力图
                click_map_open(mClickMapOpen, CmdIntent.ACTION_DRAW_CLICK_MAP);
                break;
            case R.id.click_map_open_cell_value:
                //开启绘制单元格数值
                click_map_open_cell_value(mClickMapOpenCellValue, CmdIntent.ACTION_PRINT_CLICK_MAP_CELL_VALUE);
                break;
            case R.id.click_map_open_test_points:
                //开启热力图模拟点击
                click_map_open_test_points(mClickMapOpenTestPoints,mEditText, CmdIntent.ACTION_DRAW_CLICK_MAP_TEST_POINTS);
                break;
            case R.id.click_map_print:
                //读取服务器热力图配置
                dispose(VIEWLOG, CmdIntent.ACTION_PRINT_CLICK_MAP);
                break;
            case R.id.click_strategy_force_open:
                //开启坐标漂移
                click_strategy_force_open(mClickStrategyForceOpen, CmdIntent.ACTION_CLICK_STRATEGY);
                break;
            case R.id.dynamic_print:
                //查看热修复和任务信息
                dispose(VIEWLOG, CmdIntent.ACTION_PRINT_DYNAMIC);
                break;
            case R.id.hack_query:
                //查看hack状态
                dispose(VIEWLOG, CmdIntent.ACTION_HACK);
                break;
            case R.id.log2file:
                //输出日志到文件
                dispose(OTHER, CmdIntent.ACTION_LOG2FILE);
                break;
            case R.id.cache_clear:
                //清除缓存
                dispose(OTHER, CmdIntent.ACTION_CLEAR_CACHE);
                break;
            case R.id.change_env:
                //切换环境
                dispose(OTHER, CmdIntent.ACTION_CHANGE_ENV);
                break;
            case R.id.open_debug_plugin_path:
                //修改存储路径
                open_debug_plugin_path(mOpenDebugPluginPath,CmdIntent.ACTION_OPEN_DEBUG_PLUING_PATH);
                break;
            case R.id.execute_dex:
                //执行dex
                dispose(OTHER,CmdIntent.ACTION_EXECUTE_DEX);
                break;
            case R.id.print_cache:
                //打印缓存
                dispose(VIEWLOG,CmdIntent.ACTION_PRINT_CACHE);
                break;
        }
    }

    private void open_debug_plugin_path(final Button mOpenDebugPluginPath, String actionOpenDebugPluingPath) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionOpenDebugPluingPath);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mOpenDebugPluginPath.getText().equals("修改热修复任务路径为外部")) {
                            mOpenDebugPluginPath.setText("修改热修复任务路径为内部");
                        } else {
                            mOpenDebugPluginPath.setText("修改热修复任务路径为外部");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void click_strategy_force_open(final Button mClickStrategyForceOpen, String actionClickStrategy) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionClickStrategy);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mClickStrategyForceOpen.getText().equals("开启坐标漂移")) {
                            mClickStrategyForceOpen.setText("关闭坐标漂移");
                        } else {
                            mClickStrategyForceOpen.setText("开启坐标漂移");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void click_map_open_test_points(final Button mClickMapOpenTestPoints,EditText mEditText ,String actionDrawClickMapTestPoints) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionDrawClickMapTestPoints);
        intent.putExtra("numder",mEditText.getText().toString());
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mClickMapOpenTestPoints.getText().equals("开启热力图模拟点击")) {
                            mClickMapOpenTestPoints.setText("关闭热力图模拟点击");
                        } else {
                            mClickMapOpenTestPoints.setText("开启热力图模拟点击");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void click_map_open_cell_value(final Button mClickMapOpenCellValue, String actionPrintClickMapCellValue) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionPrintClickMapCellValue);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mClickMapOpenCellValue.getText().equals("开启绘制单元格数值")) {
                            mClickMapOpenCellValue.setText("关闭绘制单元格数值");
                        } else {
                            mClickMapOpenCellValue.setText("开启绘制单元格数值");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void click_map_open(final Button mClickMapOpen, String actionDrawClickMap) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionDrawClickMap);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mClickMapOpen.getText().equals("开启绘制热力图")) {
                            mClickMapOpen.setText("关闭绘制热力图");
                        } else {
                            mClickMapOpen.setText("开启绘制热力图");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void cmd_open_log(final Button mCmdOpenLog, String actionLog) {
        final Handler handler = new Handler();
        Intent intent = new Intent(actionLog);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && resultData.contains("operate success")) {
                        if (mCmdOpenLog.getText().equals("开启LOG")) {
                            mCmdOpenLog.setText("关闭LOG");
                        } else {
                            mCmdOpenLog.setText("开启LOG");
                        }
                    }else {
                        Toast.makeText(SdkToolsMainActivity.this, "请安装或打开需要调试的AdSdk", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, resultData != null ? resultData : "resulData = null");
                    }
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private void dispose(final int operation, final String str) {
        final Handler handler = new Handler();
        Intent intent = new Intent(str);
        intent.putExtra("Package", PACKAGE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SdkToolsMainActivity.this, 10012, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            pendingIntent.send(10014, new PendingIntent.OnFinished() {

                @Override
                public void onSendFinished(PendingIntent pendingIntent, Intent intent, int resultCode, String resultData, Bundle resultExtras) {
                    if (resultData != null && operation == VIEWLOG) {
                        Intent intent1 = new Intent(SdkToolsMainActivity.this, MainActivity.class);
                        intent1.putExtra("txt", resultData);
                        startActivity(intent1);
                    }
                    if (resultData != null && operation == OTHER) {
                        Toast.makeText(SdkToolsMainActivity.this, resultData, Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, resultData != null ? resultData : "resulData = null");
                }
            }, handler);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
