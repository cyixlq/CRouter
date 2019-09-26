package top.cyixlq.myapplication;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import top.cyixlq.crouter.CRouter;
import top.cyixlq.crouter.ICRouterCallback;

@top.cyixlq.annotion.Modules({"app", "login"})
public class MyApplication extends Application {

    private static MyApplication myApplication;
    public boolean isLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        CRouter.get().init(this);
        CRouter.get().setGlobalCallback(new ICRouterCallback() {
            @Override
            public boolean onBeforeOpen(Context context, String path) {
                // 打开之前，如果返回true代表拦截，false表示不拦截
                if (!isLogin) {
                    Toast.makeText(MyApplication.this, "请先登录！", Toast.LENGTH_SHORT).show();
                }
                return !isLogin;
            }

            @Override
            public void onAfterOpen(Context context, String path) {
                // 打开之后
                Toast.makeText(MyApplication.this, path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotFound(Context context, String path) {
                // 跳转路径未找到
                Toast.makeText(MyApplication.this, "Not Found" + path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Context context, String path, Exception e) {
                // 跳转路径出错
            }
        });
    }

    public static MyApplication getMyApplication() {
        return myApplication;
    }
}
