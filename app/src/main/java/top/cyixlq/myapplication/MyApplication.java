package top.cyixlq.myapplication;

import android.app.Application;

import top.cyixlq.crouter.CRouter;

@top.cyixlq.annotion.Modules({"app", "login"})
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CRouter.get().init(this);
    }
}
