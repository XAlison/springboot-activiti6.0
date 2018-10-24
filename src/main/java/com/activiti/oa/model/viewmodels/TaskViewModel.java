package com.activiti.oa.model.viewmodels;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskViewModel {
    private String id;
    private String name;
    private String assignee;
    private Date createTime;
    private String processName;
    private String started;
    private Date startedTime;
    private Date startTime;
    private Date endTime;
    private String titleHtml;
    private String title;
    private String processId;
    private String formId;
    private String icon;
    private String listIcon;
}
