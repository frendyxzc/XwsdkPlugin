package cn.myxianwen;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.morgoo.droidplugin.R;
import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by iiMedia on 2016/11/10.
 */

public class XwsdkManager implements ServiceConnection {
    private static String DEFAULT_APK = "xw.apk";
    private static String DEFAULT_PATH;

    private Context mContext;
    private ApkItem xwApk;
    final Handler handler = new Handler();

    public XwsdkManager(Application app) {
        init(app);
    }

    public void init(Context context) {
        mContext = context;

        if (PluginManager.getInstance().isConnected()) {
            startLoad();
        } else {
            PluginManager.getInstance().addServiceConnection(this);
        }
    }

    public void destroy() {
        PluginManager.getInstance().removeServiceConnection(this);
    }

    private void startLoad() {
        new Thread() {
            @Override
            public void run() {
                String storagePath = mContext.getFilesDir().getAbsolutePath();
                DEFAULT_PATH = storagePath + "/" + DEFAULT_APK;
                loadRawFile(R.raw.xw, new File(DEFAULT_PATH));

                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(DEFAULT_PATH, 0);
                xwApk = new ApkItem(mContext, info, DEFAULT_PATH);
                xwApk.hasPrepared = true;
            }
        }.start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        startLoad();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    public void install() {
        if (!PluginManager.getInstance().isConnected()) {
            Toast.makeText(mContext, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
            return;
        }
        if (xwApk == null || xwApk.installing) {
            Toast.makeText(mContext, "正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            if (PluginManager.getInstance().getPackageInfo(xwApk.packageInfo.packageName, 0) != null) {
                Toast.makeText(mContext, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        doInstall(xwApk);
                    }
                }.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                PluginManager.getInstance().installPackage(xwApk.apkfile, 0);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }

    private synchronized void doInstall(ApkItem item) {
        item.installing = true;

        try {
            final int re = PluginManager.getInstance().installPackage(item.apkfile, 0);
            item.installing = false;

            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, re == PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION ?
                            "安装失败，文件请求的权限太多或者您已安装该应用" : "安装完成", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void openXw() {
        PackageManager pm = mContext.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(xwApk.packageInfo.packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void loadRawFile(int rawId, File file) {
        InputStream dbInputStream = mContext.getResources().openRawResource(rawId);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = dbInputStream.read(bytes)) > 0) {
                fos.write(bytes, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                dbInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
