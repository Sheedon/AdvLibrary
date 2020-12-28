package org.sheedon.advlibrary.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.leon.lfilepickerlibrary.utils.StringUtils;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener2;

import org.sheedon.advlibrary.model.AdvModel;
import org.sheedon.advlibrary.model.SourceModel;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 数据处理
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/7/21 9:13 AM
 */
public class SourceDispatcher implements SourceCenter {

    private static SourceDispatcher instance;

    private Set<HandleListener> listeners = new LinkedHashSet<>();

    // 是否开始移动资源
    private boolean isStartMoveSource = false;

    private static String imageParentPath = "/storage/emulated/0/DCIM";
    private static String videoParentPath = "/storage/emulated/0/Movies";

    // 单线程池；处理卡片一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static SourceDispatcher getInstance() {
        if (instance == null) {
            synchronized (SourceDispatcher.class) {
                instance = new SourceDispatcher();
            }
        }
        return instance;
    }

    private SourceDispatcher() {

    }


    @Override
    public void dispatch(SourceModel model) {
        if (model == null) {
            notifyResult();
            return;
        }

        String[] imagePaths = model.getImagePaths();
        String[] videoPaths = model.getVideoPaths();

//        if (imagePaths.length == 0 && videoPaths.length == 0) {
//            notifyResult();
//            return;
//        }

        imageParentPath = StringUtils.isEmpty(model.getImageParents()) ? imageParentPath : model.getImageParents();
        videoParentPath = StringUtils.isEmpty(model.getVideoParents()) ? videoParentPath : model.getVideoParents();

        executor.execute(new SourceHandler(imagePaths, videoPaths));

    }

    @Override
    public void updateAdv(AdvModel model) {
        isStartMoveSource = true;
    }

    @Override
    public void addListener(HandleListener listener) {
        if (listener == null)
            return;

        listeners.add(listener);
    }

    @Override
    public void removeListener(HandleListener listener) {
        if (listener == null)
            return;

        listeners.remove(listener);
    }

    // 移动资源
    private void moveSource() {

    }

    /**
     * 线程调度的时候会触发run方法
     */
    private class SourceHandler implements Runnable {
        private final String[] imagePaths;
        private final String[] videoPaths;

        private Set<File> imageFiles = new HashSet<>();
        private Set<File> videoFiles = new HashSet<>();

        SourceHandler(String[] imagePaths, String[] videoPaths) {
            this.imagePaths = imagePaths;
            this.videoPaths = videoPaths;
        }

        @Override
        public void run() {
            imageFiles = getFiles(imageParentPath);
            videoFiles = getFiles(videoParentPath);

            List<DownloadTask> tasks = new ArrayList<>(imagePaths.length + videoPaths.length);

            loadTasks(tasks, imagePaths, imageFiles, imageParentPath);
            loadTasks(tasks, videoPaths, videoFiles, videoParentPath);

            if (tasks.size() == 0) {
                notifyResult();
                return;
            }

            DownloadTask.enqueue(tasks.toArray(new DownloadTask[0]), new DownloadListener2() {
                @Override
                public void taskStart(@NonNull DownloadTask task) {
                    // 任务开始
                }

                @Override
                public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                    // 任务完成
                    imageFiles.clear();
                    videoFiles.clear();
                    notifyResult();
                }
            });

        }

        /**
         * 加载任务
         *
         * @param tasks      任务集合
         * @param paths      下载内容路径
         * @param files      本地文件
         * @param parentPath 父目录
         */
        private void loadTasks(List<DownloadTask> tasks, String[] paths, Set<File> files, String parentPath) {
            Map<String, String> names = new HashMap<>();

            for (String path : paths) {
                String fileName = getFileName(path);

                if (fileName == null)
                    continue;

                names.put(fileName, path);
            }

            checkDownloadFile(files, names);

            for (Map.Entry<String, String> entry : names.entrySet()) {
                tasks.add(new DownloadTask.Builder(entry.getValue(), parentPath, entry.getKey()).build());
            }
        }

        private Set<File> getFiles(String parentPath) {
            File file = new File(parentPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            Set<File> files = new HashSet<>();
            for (File f : file.listFiles()) {
                if (f != null && f.isFile()) {
                    files.add(f);
                }
            }

            return files;
        }

        /**
         * 核实并且删除重复数据
         * 本地无效和下载重复
         *
         * @param files         文件
         * @param downloadNames 下载文件
         */
        private void checkDownloadFile(Set<File> files, Map<String, String> downloadNames) {
            Set<String> fileNames = new HashSet<>(files.size());
            for (File file : files) {
                fileNames.add(file.getName());
            }

            Set<String> copy = new HashSet<>(fileNames);

            copy.removeAll(downloadNames.keySet());

            for (File file : files) {
                if (file != null && copy.contains(file.getName())) {
                    file.delete();
                }
            }

            copy.clear();

            for (String fileName : fileNames) {
                downloadNames.remove(fileName);
            }
        }

        private String getFileName(String url) {
            if (url == null || url.isEmpty())
                return null;

            String[] split = url.split("/");
            if (split.length < 0)
                return null;

            return split[split.length - 1];
        }


    }


    private void notifyResult(){
        if (listeners == null) {
            return;
        }

        for (HandleListener listener : listeners) {
            if(listener != null){
                listener.onComplete();
            }
        }
    }
}
