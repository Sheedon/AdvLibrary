package org.sheedon.advlibrary.view;

import android.util.DisplayMetrics;

import org.sheedon.advlibrary.model.AdvModel;

/**
 * 广告中心控制器
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/3/28 14:05
 */
public interface AdvViewCenter {

    void initConfig(AdvModel model);

    void onResume();

    void onPause();

    void onDestroy();
}
