package com.activiti.oa.hander;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManagerImpl;

/**
 * @ClassName: DeleteTaskCmd
 * @Description: 处理活动节点
 * @Author: xiewl
 * @Date: 2018/10/17 14:11
 * @Version 1.0
 */
public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
    public DeleteTaskCmd(String taskId) {
        super(taskId);
    }

    public String execute(CommandContext commandContext, TaskEntity currentTask) {
        // 获取所需服务
        TaskEntityManagerImpl taskEntityManager = (TaskEntityManagerImpl) commandContext.getTaskEntityManager();
        // 获取当前任务的来源任务及来源节点信息
        ExecutionEntity executionEntity = currentTask.getExecution();
        // 删除当前任务,来源任务
        taskEntityManager.deleteTask(currentTask, "jumpReason", false, false);
        return executionEntity.getId();
    }

    public String getSuspendedTaskException() {
        return "挂起的任务不能跳转";
    }
}
