package org.sheedon.advlibrary.data;

import org.sheedon.advlibrary.model.AdvModel;
import org.sheedon.advlibrary.model.SourceModel;

/**
 * 数据中心的基本定义
 *
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2020/7/21 9:05 AM
 */
public interface SourceCenter {

    void dispatch(SourceModel model);

    void updateAdv(AdvModel model);

    void addListener(HandleListener listener);


    void removeListener(HandleListener listener);
}
