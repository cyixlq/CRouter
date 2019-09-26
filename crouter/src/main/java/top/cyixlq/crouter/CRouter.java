package top.cyixlq.crouter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public final class CRouter {

    private Map<String, Class<?>> classMapMap;
    private Application application;
    private ICRouterCallback globalCallback;

    private CRouter() {
        classMapMap = new HashMap<>();
    }

    private static volatile CRouter instance;

    public static CRouter get() {
        if (instance == null) {
            synchronized (CRouter.class) {
                if (instance == null) {
                    instance = new CRouter();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        this.application = application;
    }

    public void addPath(String path, Class<?> clazz) {
        if (classMapMap.containsKey(path)) {
            throw new RuntimeException(path + "Already registered");
        }
        classMapMap.put(path, clazz);
    }

    public Object open(String path) {
        return open(null, path);
    }

    public Object open(String path, ICRouterCallback callback) {
        return open(null, path, callback);
    }

    public Object open(Context context, String path) {
        return open(context, path, null, null);
    }

    public Object open(Context context, String path, ICRouterCallback callback) {
        return open(context, path, null, callback);
    }

    public Object open(Context context, String path, Bundle bundle) {
        return open(context, path, bundle, null);
    }

    public Object open(Context context, String path, Bundle bundle, ICRouterCallback callback) {
        return doOpen(context, path, bundle, -1, callback);
    }

    public void openForResult(Context context, String path, int requestCode) {
        openForResult(context, path, null, requestCode);
    }

    public void openForResult(Context context, String path, Bundle bundle, int requestCode) {
        openForResult(context, path, bundle, requestCode, null);
    }

    public void openForResult(Context context, String path, Bundle bundle, int requestCode, ICRouterCallback callback) {
        doOpen(context, path, bundle, requestCode, callback);
    }

    private Object doOpen(Context context, String path, Bundle bundle, int requestCode, ICRouterCallback callback) {
        injectIfNeed();
        Class<?> clazz = classMapMap.get(path);
        if (clazz == null) {
            if (callback != null) {  // 如果传入了局部回调，优先使用局部回调
                callback.onNotFound(context, path);
                return null;
            } else if (this.globalCallback != null) {
                this.globalCallback.onNotFound(context, path);
                return null;
            } else {
                throw new RuntimeException(path + "===>>> path not found!");
            }
        }
        if(Fragment.class.isAssignableFrom(clazz) || android.app.Fragment.class.isAssignableFrom(clazz)) {
            try {
                Object instance = clazz.getConstructor().newInstance();
                if(bundle != null) {
                    if (instance instanceof Fragment) {
                        ((Fragment) instance).setArguments(bundle);
                    } else if (instance instanceof android.app.Fragment) {
                        ((android.app.Fragment) instance).setArguments(bundle);
                    }
                }
                return instance;
            } catch (Exception e) {
                e.printStackTrace();
                onError(context, path, callback, e);
            }
        } else {
            if (callback != null) {
                if (callback.onBeforeOpen(context, path)) { // 如果Activity跳转被拦截
                    return null;
                }
            } else if (this.globalCallback != null) {
                if (this.globalCallback.onBeforeOpen(context, path)) { // 如果Activity跳转被拦截
                    return null;
                }
            }
            Intent intent = getIntent(context, path, bundle);
            if (requestCode >= 0) {
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                    onAfterOpen(context, path, callback);
                } else {
                    onError(context, path, callback, new RuntimeException("When using the openForResult method, the context parameter must be an Activity"));
                }
            } else {
                context.startActivity(intent);
                onAfterOpen(context, path, callback);
            }
        }
        return null;
    }

    public Intent resolve(String path) {
        return resolve(this.application, path);
    }

    public Intent resolve(Context context, String path) {
        return getIntent(context, path, null);
    }

    public void setGlobalCallback(ICRouterCallback callback) {
        this.globalCallback = callback;
    }

    private Intent getIntent(Context context, String path, Bundle bundle) {
        injectIfNeed();
        Class<?> clazz = classMapMap.get(path);
        if (clazz == null) {
            throw new RuntimeException(path + "===>>> path not found!");
        }
        if (context == null) {
            if (application == null) {
                throw new RuntimeException("If you don't pass in the context, then you must execute CRouter's init method.");
            }
            context = application.getApplicationContext();
        }
        final Intent intent = new Intent(context, clazz);
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        return intent;
    }

    private void injectIfNeed() {
        if (classMapMap.isEmpty()) {
            RouterInjector.init();
        }
    }

    private void onAfterOpen(Context context, String path, ICRouterCallback callback) {
        if (callback != null) {
            callback.onAfterOpen(context, path);
        } else if (this.globalCallback != null) {
            this.globalCallback.onAfterOpen(context, path);
        }
    }

    private void onError(Context context, String path, ICRouterCallback callback, Exception e) {
        if (callback != null) {
            callback.onError(context, path, e);
        } else if (this.globalCallback != null) {
            this.globalCallback.onError(context, path, e);
        } else {
            throw new RuntimeException("No callbacks handle error events：" + e.getLocalizedMessage());
        }
    }

}
