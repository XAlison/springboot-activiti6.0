package com.activiti.oa.hander;

import java.util.List;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

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

    public TaskNodeCmd(FlowNode flowElement, String executionId) {
        this.flowElement = flowElement;
        this.executionId = executionId;
    }

    public Void execute(CommandContext commandContext) {
        // 获取目标节点的来源连线
        List<SequenceFlow> flows = flowElement.getIncomingFlows();
        if (flows == null || flows.size() < 1) {
            throw new ActivitiException("回退错误，目标节点没有来源连线");
        }
        // 选择指定连线来执行
        ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findById(executionId);
        executionEntity.setCurrentFlowElement(flows.get(0));
        commandContext.getAgenda().planTakeOutgoingSequenceFlowsOperation(executionEntity, true);
        return null;
    }
}
