package com.activiti.oa;

import com.activiti.oa.mapper.ProcessMapper;
import com.activiti.oa.model.Definition;
import com.activiti.oa.service.ProcessService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

/**
 * @ClassName: singleAssigneeTest
 * @Description: TODO
 * @Author: Administrator
 * @Date: 2018/10/17 10:01
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SingleAssigneeTest {
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private ProcessService processService;
    @Autowired
    private ProcessMapper processMapper;


}
