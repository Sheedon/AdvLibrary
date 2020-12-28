package org.sheedon.advlibrary.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.lzyzsd.jsbridge.BridgeWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sheedon.advlibrary.AndroidInterface;
import org.sheedon.advlibrary.R;
import org.sheedon.advlibrary.model.AdvModel;

/**
 * H5网页广告
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 10:09
 */
public class AdvWebView extends FrameLayout implements AdvViewCenter, AndroidInterface.jsListener {

    private BridgeWebView webView;
    private ImageView advImage;

    private String webUrl;

    private AdvModel model;

    public AdvWebView(Context context) {
        super(context);
        webView = new BridgeWebView(context);
        init(context);
    }

    public AdvWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        webView = new BridgeWebView(context, attrs);
        init(context);
    }

    public AdvWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        webView = new BridgeWebView(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {


        addView(webView);
        webView.setVisibility(GONE);

        advImage = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        advImage.setLayoutParams(params);
        advImage.setScaleType(ImageView.ScaleType.FIT_XY);
        advImage.setImageResource(R.mipmap.bg_adv_vertical);

        addView(advImage);
    }

    @Override
    public void initConfig(AdvModel model) {
        this.model = model;
        webUrl = String.format("%s?account=%s&password=%s", model.getWebUrl(), model.getAccount(), model.getPassword());
        webView.setDefaultHandler((data, function) -> {
        });

        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(mWebClient);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setUseWideViewPort(true);
        settings.setSupportMultipleWindows(true);
        settings.setBlockNetworkImage(false);//同步请求图片
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.addJavascriptInterface(new AndroidInterface(getContext(), this), "app");

        webView.loadUrl(webUrl);
    }

    @Override
    public void onResume() {
        webView.onResume();
    }

    @Override
    public void onPause() {
        webView.onPause();
    }

    @Override
    public void onDestroy() {
        webView.clearCache(true);
        model = null;
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (model != null)
                    quickCallJs("setAccountInfo", model.getAccount(), model.getPassword());
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("WebChromeClient", consoleMessage.message() + " -- From line "
                    + consoleMessage.lineNumber() + " of "
                    + consoleMessage.sourceId() );
            return true;
        }
    };

    private WebViewClient mWebClient = new WebViewClient() {

        private boolean isReceivedError = false;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isReceivedError) {
                webView.setVisibility(VISIBLE);
                advImage.setVisibility(GONE);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            webView.setVisibility(GONE);
            advImage.setVisibility(VISIBLE);
            isReceivedError = true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            webView.setVisibility(GONE);
            advImage.setVisibility(VISIBLE);
            isReceivedError = true;
        }

    };

    @Override
    public void getAccountInfo() {
        if (model != null)
            quickCallJs("setAccountInfo", model.getAccount(), model.getPassword());
    }

    public void quickCallJs(String method, String... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:" + method);
        if (params == null || params.length == 0) {
            sb.append("()");
        } else {
            sb.append("(").append(concat(params)).append(")");
        }
        callJs(sb.toString());
    }

    public void callJs(String js) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.evaluateJs(js);
        } else {
            this.loadJs(js);
        }

    }

    private void loadJs(String js) {
        webView.loadUrl(js);

    }

    private void evaluateJs(String js) {
        webView.evaluateJavascript(js, value -> {
        });
    }

    /**
     * 连接内容
     *
     * @param params
     * @return
     */
    public static String concat(String... params) {

        StringBuilder mStringBuilder = new StringBuilder();

        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (!isJson(param)) {

                mStringBuilder.append("\"").append(param).append("\"");
            } else {
                mStringBuilder.append(param);
            }

            if (i != params.length - 1) {
                mStringBuilder.append(" , ");
            }

        }

        return mStringBuilder.toString();
    }

    static boolean isJson(String target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        }

        boolean tag = false;
        try {
            if (target.startsWith("[")) {
                new JSONArray(target);
            } else {
                new JSONObject(target);
            }
            tag = true;
        } catch (JSONException ignore) {
//            ignore.printStackTrace();
            tag = false;
        }

        return tag;

    }
}
