package org.sheedon.advlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.sheedon.advlibrary.model.AdvModel;
import org.sheedon.advlibrary.view.AdvVideoView;
import org.sheedon.advlibrary.view.AdvViewCenter;
import org.sheedon.advlibrary.view.AdvImageView;
import org.sheedon.advlibrary.view.AdvWebView;


/**
 * 广告父布局
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 9:51
 */
public class AdvParentView extends FrameLayout {

    private AdvViewCenter viewCenter;
    private Context context;

    public AdvParentView(@NonNull Context context) {
        this(context, null);
    }

    public AdvParentView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvParentView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        switch (AdvFactory.getAdvType()) {
            case Constant.TYPE_ADV_IMAGE:
                addView((View) (viewCenter = new AdvImageView(context)));
                break;
            case Constant.TYPE_ADV_VIDEO:
                addView((View) (viewCenter = new AdvVideoView(context)));
                break;
            case Constant.TYPE_ADV_WEB:
                addView((View) (viewCenter = new AdvWebView(context)));
                break;
        }
    }

    public void resetView(){
        onPause();
        onDestroy();
        switch (AdvFactory.getAdvType()) {
            case Constant.TYPE_ADV_IMAGE:
                addView((View) (viewCenter = new AdvImageView(context)));
                break;
            case Constant.TYPE_ADV_VIDEO:
                addView((View) (viewCenter = new AdvVideoView(context)));
                break;
            case Constant.TYPE_ADV_WEB:
                addView((View) (viewCenter = new AdvWebView(context)));
                break;
        }
    }

    public void initConfig(AdvModel model) {
        if (viewCenter != null)
            viewCenter.initConfig(model);
    }


    public void onResume() {
        if (viewCenter != null) {
            viewCenter.onResume();
        }
    }

    public void onPause() {
        if (viewCenter != null) {
            viewCenter.onPause();
        }
    }

    public void onDestroy() {
        if (viewCenter != null) {
            viewCenter.onDestroy();
        }

        this.removeAllViews();
        viewCenter = null;
    }
}
