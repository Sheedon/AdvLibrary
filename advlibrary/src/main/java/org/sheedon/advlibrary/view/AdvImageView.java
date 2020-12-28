package org.sheedon.advlibrary.view;

import android.content.Context;
import android.util.AttributeSet;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import org.sheedon.advlibrary.R;
import org.sheedon.advlibrary.model.AdvModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;


/**
 * 图片广告
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 10:08
 */
public class AdvImageView extends AppCompatImageView implements AdvViewCenter {

    // 图片Observable
    private Observable<Long> imageObservable;

    // 图片消费者
    private Consumer<Long> imageConsumer;

    private Disposable subscribe;

    // 当前指定坐标
    private int currentPosition = 0;

    private int savePosition = 0;

    // 图片路径
    private final List<String> paths = new ArrayList<>();

    private boolean playing = false;

    private DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder(500).setCrossFadeEnabled(true).build();

    public AdvImageView(Context context) {
        this(context, null);
    }

    public AdvImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.FIT_XY);
    }


    @Override
    public void initConfig(@NonNull AdvModel model) {
        createObservable();
        initFiles(model.getImagePath());
    }

    /**
     * 创建轮播监听器
     */
    private void createObservable() {
        if (imageObservable == null)
            imageObservable = Observable.interval(0, 5, TimeUnit.SECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread());

        imageObservable.map(aLong -> aLong + savePosition);
    }

    /**
     * 创建消费者
     */
    private void createConsumer() {
        if (imageConsumer == null) {
            imageConsumer = aLong -> {
                if (!playing)
                    return;

                if (paths.size() == 0)
                    return;

                currentPosition = (int) (aLong % paths.size());

                showImage(paths.get(currentPosition));
            };
        }
    }

    /**
     * 获取图片文件
     */
    private void initFiles(String path) {
        paths.clear();
        if (path == null || path.isEmpty()) {
            return;
        }

        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        File[] files = file.listFiles();

        if (files == null || files.length == 0) {
            return;
        }

        List<String> paths = new ArrayList<>();
        for (File file1 : files) {
            if (file1 != null && file1.isFile() && checkEndSuffix(file1.getName())) {
                paths.add(file1.getAbsolutePath());
            }
        }

        this.paths.addAll(paths);
    }

    /**
     * 核实后缀
     */
    private boolean checkEndSuffix(String suffix) {
        return suffix.endsWith(".jpg") || suffix.endsWith(".jpeg") || suffix.endsWith(".png");
    }

    @Override
    public void onResume() {
        playing = true;
        if (paths.size() > 1) {
            startSubscribe();
        } else {
            String path = paths.size() > 0 ? paths.get(0) : "";
            showImage(path);
        }
    }

    @Override
    public void onPause() {
        playing = false;
        stopSubscribe();
    }

    @Override
    public void onDestroy() {
        stopSubscribe();

        subscribe = null;
        imageObservable = null;
        imageConsumer = null;
        paths.clear();
    }

    /**
     * 启动绑定
     */
    private void startSubscribe() {
        createObservable();
        createConsumer();
        subscribe = imageObservable.subscribe(imageConsumer);
    }

    /**
     * 停止绑定
     */
    private void stopSubscribe() {
        savePosition = currentPosition;
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
    }


    /**
     * 显示图片
     *
     * @param path 图片地址
     */
    private void showImage(String path) {
        Glide.with(this)
                .load(path)
                .apply(new RequestOptions()
                        .error(R.mipmap.bg_adv_vertical))
//                .placeholder(R.mipmap.bg_adv_vertical)
                .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                .into(this);
    }
}
