package com.activiti.oa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: InstanceViewModel
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/10/18 14:53
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstanceViewModel {
    private  String id;
    private  String assignee;
    private  String createTime;
    private  String name;
    private  String processInstanceId;
    private  String endTime;
}
