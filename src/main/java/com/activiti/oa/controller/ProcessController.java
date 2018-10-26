package com.activiti.oa.controller;

import com.activiti.oa.model.Definition;
import com.activiti.oa.model.ProcessModel;
import com.activiti.oa.model.viewmodels.InstanceViewModel;
import com.activiti.oa.model.viewmodels.TaskHistoryViewModel;
import com.activiti.oa.model.viewmodels.TaskViewModel;
import com.activiti.oa.service.ProcessService;
import com.activiti.oa.viewmodels.PagedFilterViewModel;
import com.activiti.oa.viewmodels.PagedListViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName: ProcessController
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/10/18 10:54
 * @Version 1.0
 */
@RestController
@RequestMapping("/process")
public class ProcessController {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ProcessService processService;

    @RequestMapping("deployByResource")
    public String deployByResource(String name,String resourceName, String tenantId) {
        return processService.deployByResource( name,resourceName, tenantId);
    }

    @RequestMapping("getAllFlowList")
    public Map<String, List<Definition>> getAllFlowList() throws Exception {
        return processService.getAllFlowList();
    }


    @RequestMapping("getProcessList")
    public List<ProcessModel> getProcessList() {
        List<ProcessDefinition> list = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion().asc()//升序
                .list();
        //定义有序map，相同的key,添加map值后，后面的会覆盖前面的值
        Map<String, ProcessDefinition> map = new LinkedHashMap<>();
        //遍历相同的key，替换最新的值
        for (ProcessDefinition pd : list) {
            map.put(pd.getKey(), pd);
        }
        List<ProcessDefinition> linkedList = new LinkedList<>(map.values());
        List<ProcessModel> processModels = new ArrayList<>();
        for (ProcessDefinition pd : linkedList) {
            ProcessModel model = ProcessModel.builder()
                    .id(pd.getId())
                    .key(pd.getKey())
                    .name(pd.getName())
                    .version(pd.getVersion() + "")
                    .build();
            processModels.add(model);

        }
        return processModels;
    }

    @RequestMapping("startProcess")
    public String startProcess(String processKey, String tenantId, String userId) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("applyUser", userId);
        variables.put("user1", userId);
        Task task = processService.startProcess(processKey, tenantId, variables);
        return task.getId();
    }

    @RequestMapping("completeTask")
    public String completeTask(String taskId, String tenantId) throws Exception {
        String task = processService.completeTask(taskId, tenantId, "", "", null, null);
        return task;
    }

    @RequestMapping("recallTask")
    public void recallTask(String taskId) throws Exception {
        processService.recallTask(taskId);
    }

    @RequestMapping("suspendTask")
    public ResponseEntity suspendTask(String instanceId) throws Exception {
        processService.suspendTask(instanceId);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
    // 待办任务
    @RequestMapping("doingTasks")
    public PagedListViewModel<TaskViewModel> doingTasks(String userId) throws Exception {

        PagedFilterViewModel pagedFilterViewModel = new PagedFilterViewModel();
        pagedFilterViewModel.setStart(0);
        pagedFilterViewModel.setSize((int) Math.pow(2, 31));
        pagedFilterViewModel.setFilters(new HashMap<String, String>(1) {{
            put("currentUserId", userId);
        }});
        // TODO 当前用户
        //filter.put("currentUserId", "待定");
        return processService.getDoingTasks(pagedFilterViewModel);
    }

    // 已办任务
    @RequestMapping("getDoneTasks")
    public PagedListViewModel<InstanceViewModel> getDoneTasks(String userId) throws Exception {

        PagedFilterViewModel pagedFilterViewModel = new PagedFilterViewModel();
        pagedFilterViewModel.setStart(0);
        pagedFilterViewModel.setSize((int) Math.pow(2, 31));
        pagedFilterViewModel.setFilters(new HashMap<String, String>(1) {{
            put("currentUserId", userId);
        }});
        // TODO 当前用户
        //filter.put("currentUserId", "待定");
        return processService.getDoneTasks(pagedFilterViewModel);
    }

    @RequestMapping("startedInstances")
    public PagedListViewModel<InstanceViewModel> startedInstances(String userId) throws Exception {
        PagedFilterViewModel pagedFilterViewModel = new PagedFilterViewModel();
        pagedFilterViewModel.setStart(0);
        pagedFilterViewModel.setSize((int) Math.pow(2, 31));
        pagedFilterViewModel.setFilters(new HashMap<String, String>(1) {{
            put("currentUserId", userId);
        }});
        return processService.getInstances(pagedFilterViewModel);
    }

    @RequestMapping("getInstances")
    private PagedListViewModel<InstanceViewModel> getInstances() throws Exception {
        PagedFilterViewModel pagedFilterViewModel = new PagedFilterViewModel();
        pagedFilterViewModel.setStart(0);
        pagedFilterViewModel.setSize((int) Math.pow(2, 31));
        return processService.getInstances(pagedFilterViewModel);
    }

    @RequestMapping("getTaskHistory")
    private  List<TaskHistoryViewModel> getTaskHistory(String instanceId) throws Exception {
        return processService.getTaskHistory(instanceId);
    }


   /* @RequestMapping("doingTasks")
    public List<TaskViewModel> doingTasks(String userId) throws Exception {
        List<TaskViewModel> taskViewModels = new ArrayList<>();
        List<Task> runTask = processEngine.getTaskService().createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        if (runTask != null && runTask.size() > 0) {
            for (org.activiti.engine.task.Task task : runTask) {
                TaskViewModel model = TaskViewModel.builder()
                        .id(task.getId())
                        .assignee(task.getAssignee())
                        .name(task.getName())
                        .createTime(task.getCreateTime().toString())
                        .processInstanceId(task.getProcessInstanceId() + "")
                        .build();
                taskViewModels.add(model);
                System.out.println("节点任务ID：" + task.getId());
                System.out.println("节点任务的办理人：" + task.getAssignee());
                System.out.println("节点任务名称：" + task.getName());
                System.out.println("节点任务的创建时间：" + task.getCreateTime());
                System.out.println("节点流程实例ID：" + task.getProcessInstanceId());
                System.out.println("#######################################");
            }

        }
        return taskViewModels;
    }*/

    /*@RequestMapping("doneTasks")
    public List<InstanceViewModel> doneTasks(String userId) throws Exception {
        List<InstanceViewModel> instanceViewModels = new ArrayList<>();
        List<HistoricTaskInstance> list = processEngine.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .orderByHistoricTaskInstanceStartTime().desc()
                .finished() // 查询已经完成的任务
                .list();
        for (HistoricTaskInstance hti : list) {
            InstanceViewModel model = InstanceViewModel.builder()
                    .id(hti.getId())
                    .assignee(hti.getAssignee())
                    .name(hti.getName())
                    .createTime(hti.getCreateTime().toString())
                    .processInstanceId(hti.getProcessInstanceId() + "")
                    .endTime(hti.getEndTime().toString())
                    .build();
            System.out.println("任务ID:" + hti.getId());
            System.out.println("流程实例ID:" + hti.getProcessInstanceId());
            System.out.println("办理人：" + hti.getAssignee());
            System.out.println("创建时间：" + hti.getCreateTime());
            System.out.println("结束时间：" + hti.getEndTime());
            System.out.println("===========================");
            instanceViewModels.add(model);
        }
        return instanceViewModels;
    }*/

}
