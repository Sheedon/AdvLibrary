package org.sheedon.advlibrary;

import android.content.Context;
import android.webkit.JavascriptInterface;


/**
 * js调用映射
 */
public class AndroidInterface {

    private Context context;

    private jsListener listener;

    public AndroidInterface(Context context, jsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setListener(jsListener listener) {
        this.listener = listener;
    }

    /**
     * 获取token信息
     */
    @JavascriptInterface
    public void getAccountInfo() {
        if (listener != null) {
            listener.getAccountInfo();
        }
    }


    public interface jsListener {
        void getAccountInfo();
    }


}
