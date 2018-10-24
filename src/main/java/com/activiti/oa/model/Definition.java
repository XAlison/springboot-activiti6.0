package com.activiti.oa.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Definition {
    private String id;
    private String name;
    private String category;
    private String currentVersion;
    private String creator;
    private Integer state;
    private boolean isTest;
    private String currentTestVersion;
    private String updateUser;
    private Date updateTime;
    private String icon;
    private String listIcon;
}
