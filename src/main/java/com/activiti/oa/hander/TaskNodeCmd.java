package com.activiti.oa.hander;

import com.activiti.oa.service.ProcessService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: TaskNodeCmd
 * @Description: 节点拦截处理
 * @Author: xiewl
 * @Date: 2018/10/17 14:13
 * @Version 1.0
 */
public class TaskNodeCmd implements Command<Void> {
    private FlowNode flowElement;
    private String executionId;
    private HistoricActivityInstance activityInstance;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ProcessService processService;

    public TaskNodeCmd(FlowNode flowElement, String executionId,HistoricActivityInstance activityInstance) {
        this.flowElement = flowElement;
        this.executionId = executionId;
        this.activityInstance=activityInstance;
    }

    public Void execute(CommandContext commandContext) {
        // 获取目标节点的来源连线
        UserTask userTask= (UserTask) flowElement;
        userTask.setAssignee(activityInstance.getAssignee());
        userTask.setId(activityInstance.getTaskId());
        List<SequenceFlow> flows = flowElement.getIncomingFlows();
        if (flows == null || flows.size() < 1) {
            throw new ActivitiException("回退错误，目标节点没有来源连线");
        }

        // 选择指定连线来执行
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        executionEntity.setCurrentFlowElement(flows.get(0));
        executionEntity.setStartTime(activityInstance.getStartTime());
        commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
        return null;
    }
}
