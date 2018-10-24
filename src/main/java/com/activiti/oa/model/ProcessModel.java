package com.activiti.oa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: Process
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/10/18 11:19
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessModel {
    private String id;
    private String name;
    private String key;
    private String version;
}
