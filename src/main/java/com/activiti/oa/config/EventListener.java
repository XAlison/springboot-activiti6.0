package com.activiti.oa.config;


import java.util.Map;
import java.util.Set;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.springframework.stereotype.Component;

/**
 * @Description: 流程事件监听中心
 * @Author: xiewl
 * @Date: 2018/10/15 17:13
 * @Version 1.0
 */
@Component
public class EventListener implements ActivitiEventListener, ExecutionListener, TaskListener {
    @Override
    public void onEvent(ActivitiEvent event) {
        System.out.println("Excecution id" + event.getExecutionId() + "ProcessDefinition id" + event.getProcessDefinitionId());
        System.out.println("ProcessInstance Id" + event.getProcessInstanceId() + "Type" + event.getType());
        switch (event.getType()) {
            /***/
            case ACTIVITY_STARTED:
                System.out.println("Activi started ");
                break;
            /**流程完成*/
            case ACTIVITY_COMPLETED:
                System.out.println("Activi completed ");
                break;
            /**流程任务创建*/
            case TASK_CREATED:
                System.out.println("流程任务创建");
                break;
            /**流程完成*/
            case TASK_COMPLETED:
                System.out.println("流程任务完成");
                break;
            /**流程任务分配*/
            case TASK_ASSIGNED:
                System.out.println("A job well done!");
                break;
            /**流程完成*/
            case JOB_EXECUTION_FAILURE:
                System.out.println("A job has failed...");
                break;
            /**流程完成*/
            default:
                System.out.println("Event received: " + event.getType());
        }
    }


    private void createNotice(ActivitiEvent event) {
        ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
        TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
        System.out.println("Event getOwner: " + taskEntity.getOwner());
        System.out.println("Event getAssignee: " + taskEntity.getAssignee());
        System.out.println("Event getName: " + taskEntity.getName());
        System.out.println("Event getType: " + event.getType());
    }

    private boolean isNode(String activityType) {
        return "userTask".equals(activityType) || "inclusiveGateway".equals(activityType) || "endEvent".equals(activityType);
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public void notify(DelegateExecution delegateExecution) {
        // TODO Auto-generated method stub
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        // TODO Auto-generated method stub
        System.out.println(delegateTask.getExecutionId());
        Map<String, Object> variables = delegateTask.getVariables();
        Set<String> variableNames = delegateTask.getVariableNames();
        for (String key : variableNames) {
            System.out.println(key + ":" + delegateTask.getVariable(key));
        }
        System.out.println("一个任务由" + delegateTask.getName() + "创建了,下一步由" + delegateTask.getAssignee() + "负责办理");

    }
}