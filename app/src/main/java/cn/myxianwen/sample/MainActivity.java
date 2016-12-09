package cn.myxianwen.sample;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cn.myxianwen.XwBaseApplication;
import cn.myxianwen.XwsdkManager;
import me.frendy.xbutton.CircularProgressButton;

public class MainActivity extends AppCompatActivity {
    private Context mContext = MainActivity.this;
    private int mManagerState = 0;
    private CircularProgressButton circularButton1;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    mManagerState = XwBaseApplication.getXwsdkManager().hasInstalled();
                    if(mManagerState == 0) {
                        circularButton1.setProgress(0);
                    } else if(mManagerState == 1) {
                        circularButton1.setProgress(100);
                    } else if(mManagerState == -1 || mManagerState == -2) {
                        handler.sendEmptyMessageDelayed(0, 500);
                    } else {
                        circularButton1.setProgress(-1);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setProgress(50);
        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mManagerState == 1) {
                    Toast.makeText(mContext, "应用启动中...", Toast.LENGTH_LONG).show();
                    XwBaseApplication.getXwsdkManager().openXw();
                } else {
                    circularButton1.setProgress(50);
                    XwBaseApplication.getXwsdkManager().install();
                }
            }
        });
        XwBaseApplication.getXwsdkManager().setInstallListener(new XwsdkManager.IOnInstallListener() {
            @Override
            public void onComplete() {
                handler.sendEmptyMessage(0);
            }
        });
        //检查安装状态
        handler.sendEmptyMessage(0);
    }
}
