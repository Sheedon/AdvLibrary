package org.sheedon.advlibrary;

import androidx.annotation.IntRange;

import org.sheedon.advlibrary.data.SourceCenter;
import org.sheedon.advlibrary.data.SourceDispatcher;
import org.sheedon.advlibrary.model.AdvModel;
import org.sheedon.advlibrary.model.SourceModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 广告基本工厂
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/6/10 9:59
 */
public class AdvFactory {

    // 单例模式
    private static final AdvFactory instance;

    // 全局的线程池
    private final Executor executor;

    private int advType = Constant.TYPE_ADV_IMAGE;

    static {
        instance = new AdvFactory();
    }

    private AdvFactory() {
        // 新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(2);
    }

    /**
     * initialize adv type
     *
     * @param advType advertising type
     */
    public static void init(@IntRange(from = Constant.TYPE_ADV_IMAGE, to = Constant.TYPE_ADV_WEB)
                                    int advType) {
        instance.advType = advType;
    }


    /**
     * download source model (include images、video、webPath)
     *
     * @param model source model
     */
    public static void download(SourceModel model) {
        SourceDispatcher.getInstance().dispatch(model);
    }


    public static int getAdvType() {
        return instance.advType;
    }

    /**
     * 异步运行的方法
     *
     * @param runnable Runnable
     */
    public static void runOnAsync(Runnable runnable) {
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

    public static void updateAdvModel(AdvModel model) {
        SourceDispatcher.getInstance().updateAdv(model);
    }

    public static SourceCenter getSourceCenter() {
        return SourceDispatcher.getInstance();
    }
}
