package top.cyixlq.crouter;

import android.content.Context;

public interface ICRouterCallback {
    boolean onBeforeOpen(Context context, String path);
    void onAfterOpen(Context context, String path);
    void onNotFound(Context context, String path);
    void onError(Context context, String path, Exception e);
}
