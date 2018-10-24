package com.activiti.oa.model.viewmodels;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskHistoryViewModel {
    private int id;
    private String startTime;
    private String endTime;
    private String operator;
    private String buttonName;
    private String name;
}
