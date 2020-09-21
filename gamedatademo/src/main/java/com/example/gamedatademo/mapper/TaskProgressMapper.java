package com.example.gamedatademo.mapper;

import com.example.gamedatademo.bean.Bag;
import com.example.gamedatademo.bean.TaskProgress;
import javafx.concurrent.Task;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hang hang
 * @Date: 2020/09/21/18:09
 * @Description:
 */
public interface TaskProgressMapper {
    /**
     * 按照id查询
     * @param taskProgressId
     * @return
     */
    TaskProgress selectByTaskProgressId(Long taskProgressId);

    /**
     * 插入新的进度
     * @param taskProgress
     * @return
     */
    Integer insert(TaskProgress taskProgress);

    /**
     * 按照id更新
     * @param taskProgress
     * @return
     */
    Integer updateByTaskProgressId(TaskProgress taskProgress);
}
