package com.activiti.oa.model;

/**
 * @Description: 流程自定义执行状态
 * @Author: xiewl
 * @Date: 2018/10/18 9:28
 * @Version 1.0
 */
public enum ProcessStateEnum {

    已删除("0"),

    进行中("1"),

    已完成("2"),

    已终止("3"),

    已作废("4");

    private final String value;

    ProcessStateEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
