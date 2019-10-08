package com.analytics.sdk.tools;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * TextView
     */
    private TextView mTextView;
    private String txt;
    private int firClick;
    private int count = 0;
    private int secClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        initView();
    }


    private void initView() {
        mTextView = (TextView) findViewById(R.id.textView);
        String txt = getIntent().getStringExtra("txt");
        mTextView.setText(txt);
        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.textView:
                count++;
                if (count == 1) {
                    firClick = (int) System.currentTimeMillis();
                } else if (count == 2) {
                    count = 0;
                    secClick = (int) System.currentTimeMillis();
                    if (secClick - firClick < 1000){
                        finish();
                    }
                }
                break;
        }
    }
}
