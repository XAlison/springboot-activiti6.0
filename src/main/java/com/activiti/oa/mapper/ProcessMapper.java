package com.activiti.oa.mapper;


import com.activiti.oa.model.Definition;
import com.activiti.oa.model.viewmodels.InstanceViewModel;
import com.activiti.oa.model.viewmodels.TaskViewModel;
import com.activiti.oa.viewmodels.PagedFilterViewModel;
import java.util.List;
import java.util.Map;


/**
 * @Description: 流程任务列表
 * @Author: xiewl
 * @Date: 2018/10/19 15:13
 * @Version 1.0
 */
public interface ProcessMapper {

    /**
     * 流程列表
     */
    List<Definition> getAllFlowList() throws Exception;

    /**
     * 待办列表
     */
    List<TaskViewModel> getDoingTasks(PagedFilterViewModel filter);

    /**
     * 已办列表
     */
    List<InstanceViewModel> getDoneTasks(PagedFilterViewModel filter);

    /**
     * 实例列表
     */
    List<InstanceViewModel> getInstances(PagedFilterViewModel filter);

    /**
     * 执行修改流程实例执行状态
     */
    int modifyProcessState(Map<String, String> params) throws Exception;

    Long getDoingTasksCount(PagedFilterViewModel filter);

    Long getDoneTasksCount(PagedFilterViewModel filter);

    Long getInstancesCount(PagedFilterViewModel filter);
    /**
     * 动态添加流程任务
     */
    Long addRunTask(Map map);

    /**
     * 动态修改流程任务
     */
    Long updateRunTask(Map map);

}
