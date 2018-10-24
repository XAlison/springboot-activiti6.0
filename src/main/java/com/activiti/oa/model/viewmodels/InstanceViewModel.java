package com.activiti.oa.model.viewmodels;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InstanceViewModel {
    private String id;
    private String name;
    private String started;
    private Date startedTime;
    private Date endTime;
    private String taskId;
    private String titleHtml;
    private String taskName;
    private String taskAssignee;
    private String instanceState;
    private String title;
    private String formId;
    private String icon;
    private String listIcon;

    public String getStatus() {
        if (getEndTime() == null) {
            return "进行中";
        } else if ("0".equals(getInstanceState())) {
            return "已删除";
        } else if ("1".equals(getInstanceState())) {
            return "进行中";
        } else if ("2".equals(getInstanceState())) {
            return "已完成";
        } else if ("3".equals(getInstanceState())) {
            return "已终止";
        } else if ("4".equals(getInstanceState())) {
            return "已作废";
        } else {
            return "已完成";
        }
    }

}
