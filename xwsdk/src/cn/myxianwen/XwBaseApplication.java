package cn.myxianwen;

import com.morgoo.droidplugin.PluginApplication;

/**
 * Created by iiMedia on 2016/11/10.
 */

public class XwBaseApplication extends PluginApplication {
    private static XwsdkManager xwsdkManager;

    @Override
    public void onCreate() {
        super.onCreate();

        xwsdkManager = new XwsdkManager(this);
    }

    public static XwsdkManager getXwsdkManager() {
        return xwsdkManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if(xwsdkManager != null)
            xwsdkManager.destroy();
    }
}
