package com.activiti.oa.service;

import com.activiti.oa.hander.DeleteTaskCmd;
import com.activiti.oa.hander.TaskNodeCmd;
import com.activiti.oa.model.ProcessStateEnum;
import com.activiti.oa.exceptions.ApplicationException;
import com.activiti.oa.mapper.ProcessMapper;
import com.activiti.oa.model.Definition;
import com.activiti.oa.model.viewmodels.InstanceViewModel;
import com.activiti.oa.model.viewmodels.TaskHistoryViewModel;
import com.activiti.oa.model.viewmodels.TaskViewModel;
import com.activiti.oa.viewmodels.PagedFilterViewModel;
import com.activiti.oa.viewmodels.PagedListViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @ClassName: ProcessServiceImpl
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/10/16 16:08
 * @Version 1.0
 */
@Service
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ProcessMapper processMapper;

    @Override
    public String deploy(String flowId, String descriptionXml, String tenantId, String... args) {
        // 获取仓库服务
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 创建部署对象
        Deployment deploy = repositoryService.createDeployment()
                .tenantId(tenantId)
                .addString(flowId.concat(".bpmn"), descriptionXml)
                .deploy();
        return deploy.getId();
    }

    @Override
    public String deployByResource(String resourceName, String tenantId) {
        Deployment deploy = processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource(resourceName)
                .tenantId(tenantId)
                .deploy();
        return deploy.getId();
    }


    @Override
    public Task startProcess(String definitionId, String tenantId, Map<String, Object> variables) {
        // 启动流程设置发起人以及节点处理人
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey(definitionId, variables);
        // 第一个任务节点
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        task.setTenantId(tenantId);
        task.setDescription("first Task");
        return task;
    }

    @Override
    public String completeTask(String taskId, String tenantId, String btnName, String actionId, Map<String, Object> variables, List<Map<String, Object>> nodeExecutor) throws Exception {
        // 查询当前任务
        Task currentTask = processEngine.getTaskService().createTaskQuery()
                .taskId(taskId)
                .singleResult();
        if (currentTask == null) {
            throw new Exception("当前任务已被处理或不存在，请刷新待办列表重试。");
        }

        // 处理表单参数
        Map<String, Object> variablesTmp = new HashMap<>();
        if (variables != null) {
            variables.forEach((key, value) -> {
                if (!key.startsWith("fix_")) {
                    variablesTmp.put("fix_" + key.replace("-", "_"), value);
                }
            });
            variables.put("action", actionId);
            variables.put("btn_" + taskId, btnName);
            variables.putAll(variablesTmp);
            // 保存变量参数
            processEngine.getRuntimeService().setVariables(currentTask.getProcessInstanceId(), variables);
        }
        // 设置租户id
        currentTask.setTenantId(tenantId);
        // 保存当前任务
        processEngine.getTaskService().saveTask(currentTask);

        Map<String, Object> variables1 = new HashMap<>();
        variables1.put("user2", "谢文林");
        // 执行流转
        processEngine.getTaskService().complete(taskId, variables1);
        // 获取下一节点并行或者分发任务
        List<Task> nextTasks = processEngine.getTaskService().createTaskQuery()
                .processInstanceId(currentTask.getProcessInstanceId())
                .active()
                .list();
        // 判断流程是否结束
        if (nextTasks.size() == 0) {
            return "End";
        }
        for (Task nextTask : nextTasks) {
            // 如果两个节点之间的连接线包含退回, 用于处理退回逻辑
          /*  List<Connection> connections = processMapper.selectConnectionsByNode(
            currentTask.getProcessDefinitionId().split(":")[0], currentTask.getTaskDefinitionKey(), nextTask.getTaskDefinitionKey())

            if (connections.stream().anyMatch(m -> Connection.OPERATION_TYPE_BACK.equals(m.getOperationType()))) {
                // 获取相同节点定义的历史任务
                HistoricTaskInstance historicTask = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(currentTask.getProcessInstanceId())
                        .taskDefinitionKey(nextTask.getTaskDefinitionKey())
                        .finished()
                        .orderByTaskId().desc()
                        .list().stream().findFirst().orElseThrow(() -> new ApplicationException("历史节点处理人不存在."));


                // 分配历史任务的处理人为当前任务实例的处理人
                nextTask.setAssignee(historicTask.getAssignee());
            } else {
                // 寻找下一个节点是否预分配处理人;
                nodeExecutor.stream()
                        .filter(m -> nextTask.getTaskDefinitionKey().equals(m.get("nodeId")))
                        .findFirst()
                        .ifPresent(m -> nextTask.setAssignee(m.get("userId").toString()));
            }*/
            nextTask.setAssignee("谢文林");
            processEngine.getTaskService().saveTask(nextTask);
        }
        return StringUtils.join(nextTasks.stream().map(TaskInfo::getId).collect(Collectors.toList()), ",");
    }

    @Override
    public void recallTask(String taskId) throws ApplicationException {
        // 当前任务
        Task currentTask = processEngine.getTaskService().createTaskQuery().taskId(taskId).singleResult();
        if (currentTask == null) {
            throw new ApplicationException("流程未启动或已执行完成，无法撤回");
        }
        //获取流程定义
        Process process = processEngine.getRepositoryService().getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        // 取得流程定义
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) processEngine.getRepositoryService())
                .getDeployedProcessDefinition(currentTask
                        .getProcessDefinitionId());
        if (definition == null) {
            throw new ApplicationException("流程定义未找到");

        }
        // 查询历史中上一任务节点
        List<HistoricActivityInstance> activityInstances = processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(currentTask.getProcessInstanceId()).orderByExecutionId().desc().finished().list();
        Optional<HistoricActivityInstance> nextTask = activityInstances.stream()
                .filter(activityInstance -> "userTask".equals(activityInstance.getActivityType()))
                .findFirst();
        if (!nextTask.isPresent()) {
            throw new ApplicationException("没有可撤回的任务节点");
        }
        // 获取目标节点定义
        FlowNode targetNode = (FlowNode) process.getFlowElement(nextTask.get().getActivityId());
        if (targetNode != null) {
            //删除当前运行任务
            String executionEntityId = processEngine.getManagementService().executeCommand(new DeleteTaskCmd(currentTask.getId()));
            // 流程执行到来源节点
            processEngine.getManagementService().executeCommand(new TaskNodeCmd(targetNode, executionEntityId));
        }

    }

    @Override
    public void suspendTask(String instanceId) throws Exception {
        //当前任务
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(instanceId).singleResult();
        //获取流程定义
        Process process = processEngine.getRepositoryService().getBpmnModel(task.getProcessDefinitionId()).getMainProcess();
        Execution execution = processEngine.getRuntimeService().createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        // 当前活动节点
        FlowNode sourceFlowNode = (FlowNode) process.getFlowElement(activityId);
        FlowNode targetFlowNode = (FlowNode) process.getFlowElement("endpoint");
        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
        oriSequenceFlows.addAll(sourceFlowNode.getOutgoingFlows());
        // 清理活动方向
        sourceFlowNode.getOutgoingFlows().clear();
        // 建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("cancelled");
        newSequenceFlow.setSourceFlowElement(sourceFlowNode);
        newSequenceFlow.setTargetFlowElement(targetFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        sourceFlowNode.setOutgoingFlows(newSequenceFlowList);
        // 完成任务
        processEngine.getTaskService().complete(task.getId());
        // 恢复原方向
        sourceFlowNode.setOutgoingFlows(oriSequenceFlows);
        // 修改流程执行状态
        this.modifyProcessState(instanceId, ProcessStateEnum.已终止.getValue());

    }

    @Override
    public void cancelProcess(String instanceId) throws Exception {
        // 获取正在运行实例任务
        HistoricProcessInstance instance = processEngine.getHistoryService()
                .createHistoricProcessInstanceQuery()
                .processInstanceId(instanceId)
                .singleResult();
        if (instance == null) {
            throw new ApplicationException("未查询到流程实例任务");
        }
        // 判断当前实例任务是否已经结束
        if (instance.getEndTime() == null) {
            throw new ApplicationException("进行中的流程不能进行作废操作");
        }
        // 流程执行状态
        this.modifyProcessState(instance.getId(), ProcessStateEnum.已作废.getValue());
    }

    @Override
    public int modifyProcessState(String instanceId, String state) throws Exception {
        Map<String, String> instanceMap = new HashMap<String, String>() {{
            put("instanceId", instanceId);
            put("state", state);
        }};
        return processMapper.modifyProcessState(instanceMap);
    }

    @Override
    public List<TaskHistoryViewModel> getTaskHistory(String instanceId) throws ApplicationException {
        // 开始时间 结束时间 操作按钮 操作人
        List<TaskHistoryViewModel> histories = new ArrayList<>();
        long task = processEngine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceId(instanceId).count();
        if (task == 0) {
            throw new ApplicationException("流程不存在");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HistoricActivityInstance> history = processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(instanceId).activityType("userTask").list();
        for (HistoricActivityInstance item : history) {
            HistoricVariableInstance variable = processEngine.getHistoryService()
                    .createHistoricVariableInstanceQuery()
                    .variableName("btn_" + item.getTaskId()).singleResult();
            TaskHistoryViewModel taskHistory = new TaskHistoryViewModel();
            taskHistory.setId(histories.size() + 1);
            taskHistory.setName(item.getActivityName());
            taskHistory.setStartTime(format.format(item.getStartTime()));
            if (item.getEndTime() != null) {
                taskHistory.setEndTime(format.format(item.getEndTime()));
            }
            taskHistory.setOperator(item.getAssignee());

            if (variable != null && !"".equals(variable.getValue())) {
                taskHistory.setButtonName(variable.getValue().toString());
            }
            histories.add(taskHistory);
        }
        return histories;
    }

    @Override
    public PagedListViewModel<TaskViewModel> getDoingTasks(PagedFilterViewModel filter) {
        List<TaskViewModel> tasks = processMapper.getDoingTasks(filter);
        Long totalCount = processMapper.getDoingTasksCount(filter);
        return new PagedListViewModel<>(tasks, totalCount);
    }

    @Override
    public PagedListViewModel<InstanceViewModel> getDoneTasks(PagedFilterViewModel filter) {
        List<InstanceViewModel> tasks = processMapper.getDoneTasks(filter);
        Long totalCount = processMapper.getDoneTasksCount(filter);
        return new PagedListViewModel<>(tasks, totalCount);
    }

    @Override
    public PagedListViewModel<InstanceViewModel> startedInstances(PagedFilterViewModel filter) {
        List<InstanceViewModel> tasks = processMapper.getInstances(filter);
        Long totalCount = processMapper.getInstancesCount(filter);
        return new PagedListViewModel<>(tasks, totalCount);
    }

    @Override
    public PagedListViewModel<InstanceViewModel> getInstances(PagedFilterViewModel filter) {
        List<InstanceViewModel> tasks = processMapper.getInstances(filter);
        Long totalCount = processMapper.getInstancesCount(filter);
        return new PagedListViewModel<>(tasks, totalCount);
    }

    @Override
    public Map<String, List<Definition>> getAllFlowList() throws Exception {
        List<Definition> definitions = processMapper.getAllFlowList();
        Map<String, List<Definition>> flowLists = new LinkedHashMap<>();
        if (definitions != null && definitions.size() > 0) {
            definitions.forEach(definition -> {
                if (definition != null && definition.getCategory() != null) {
                    if (flowLists.containsKey(definition.getCategory())) {
                        flowLists.get(definition.getCategory()).add(definition);
                    } else {
                        flowLists.put(definition.getCategory(), new ArrayList<>());
                    }
                }
            });
        }
        return flowLists;
    }

}
