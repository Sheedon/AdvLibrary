package org.sheedon.advlibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.sheedon.advlibrary.AdvFactory;
import org.sheedon.advlibrary.R;
import org.sheedon.advlibrary.model.AdvModel;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频广告
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 10:09
 */
public class AdvVideoView extends FrameLayout implements AdvViewCenter {

    private int currentPosition = 0;

    private ImageView advImage;

    private CustomVideoView advVideo;

    // 视频路径
    private final List<String> paths = new ArrayList<>();

    private final Map<String, BitmapDrawable> drawableMap = new LinkedHashMap<>();

    public AdvVideoView(Context context) {
        this(context, null);
    }

    public AdvVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AdvVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        advVideo = new CustomVideoView(context);
        addView(advVideo);
        advVideo.setVisibility(GONE);

        advImage = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        advImage.setLayoutParams(params);
        advImage.setScaleType(ImageView.ScaleType.FIT_XY);
        advImage.setImageResource(R.mipmap.bg_adv_vertical);

        addView(advImage);

    }


    @Override
    public void initConfig(AdvModel model) {
        initFiles(model.getVideoPath());
        setListener();
    }

    /**
     * 设置播放监听器
     */
    private void setListener() {
        advVideo.setOnCompletionListener(mediaPlayer -> {
            if (++currentPosition >= paths.size()) {
                currentPosition = 0;
            }
            startVideoPosition();
        });
        advVideo.setOnErrorListener((mp, what, extra) -> {
            try {
                if (advVideo != null)
                    advVideo.stopPlayback();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        });
        advVideo.setOnPreparedListener(mp -> mp.setOnInfoListener((mp1, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                advVideo.setBackgroundColor(Color.TRANSPARENT);
                return true;
            }
            return false;
        }));
    }

    /**
     * 获取图片文件
     */
    private void initFiles(String path) {
        paths.clear();
        if (path == null || path.isEmpty()) {
            paths.add("");
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
                AdvFactory.runOnAsync(new LoadingRes(file1.getAbsolutePath()));
            }
        }

        this.paths.addAll(paths);
    }

    /**
     * 核实后缀
     */
    private boolean checkEndSuffix(String suffix) {
        return suffix.endsWith(".mp4")
                || suffix.endsWith(".3gp")
                || suffix.endsWith(".wmv")
                || suffix.endsWith(".rmvb")
                || suffix.endsWith(".mov")
                || suffix.endsWith(".avi")
                || suffix.endsWith(".m3u8")
                || suffix.endsWith(".mkv")
                || suffix.endsWith(".flv");
    }

    @Override
    public void onResume() {
        if (paths.size() == 0) {
            advImage.setVisibility(VISIBLE);
            advVideo.setVisibility(GONE);
        } else {
            advImage.setVisibility(GONE);
            advVideo.setVisibility(VISIBLE);
            startVideoPosition();
        }
    }

    @Override
    public void onPause() {
        try {
            if (advVideo != null)
                advVideo.stopPlayback();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        onPause();
        removeAllViews();
        paths.clear();
        advImage = null;
        advVideo = null;

        for (BitmapDrawable value : drawableMap.values()) {
            if (value == null)
                continue;

            Bitmap bitmap = value.getBitmap();
            if (bitmap == null) {
                continue;
            }

            bitmap.recycle();
        }

        drawableMap.clear();
    }


    private void startVideoPosition() {
        if (paths.size() > 0 && currentPosition < paths.size() && currentPosition >= 0) {
            String path = paths.get(currentPosition);
            startVideo(path);
        }
    }

    /**
     * 播放视频
     *
     * @param path 视频路径
     */
    private void startVideo(String path) {
        if (path == null || path.isEmpty())
            return;

        Drawable drawable = drawableMap.get(path);
        if (drawable != null) {
            advVideo.setBackground(drawable);
        } else {
            advVideo.setBackgroundColor(Color.WHITE);
        }

        if (advVideo != null) {
            advVideo.setVideoPath(path);
            advVideo.start();
        }
    }

    /**
     * 获取本地视频的第一帧
     *
     * @param localPath
     * @return
     */
    public static Bitmap getLocalVideoBitmap(String localPath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据文件路径获取缩略图
            retriever.setDataSource(localPath);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    private class LoadingRes implements Runnable {

        private String path;

        LoadingRes(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            if (path == null || path.equals(""))
                return;

            Bitmap bitmap = getLocalVideoBitmap(path);

            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            drawableMap.put(path, drawable);
        }
    }
}
