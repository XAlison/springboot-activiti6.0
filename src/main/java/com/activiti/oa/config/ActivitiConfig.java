package com.activiti.oa.config;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Description: 流程引擎配置服务中心
 * @Author: xiewl
 * @Date: 2018/10/15 15:56
 * @Version 1.0
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.activiti.oa.mapper")
public class ActivitiConfig  {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private EventListener eventListener;

    /**
     * 初始化配置，创建流程引擎表
     *
     * @return
     */
    @Bean
    public StandaloneProcessEngineConfiguration processEngineConfiguration() {
        StandaloneProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        // 设置是否自动更新
        configuration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        configuration.setAsyncExecutorActivate(false);

        //configuration.setCustomMybatisMappers(Collections.singleton(ProcessMapper.class));
        //configuration.setActiviti5CustomMybatisXMLMappers(Collections.singleton("classpath:mapper/ProcessMapper.xml"));
        // 注册全局监听器
        List<ActivitiEventListener>  activitiEventListener=new ArrayList<>();
        activitiEventListener.add(eventListener );
        configuration.setEventListeners(activitiEventListener);
        return configuration;
    }

    /**
     * 初始化配置，创建流程引擎表
     *
     * @return
     */
    @Bean
    public ProcessEngine getProcessEngine() {
        return processEngineConfiguration().buildProcessEngine();
    }

    /**
     * 仓库服务
     *
     * @return
     */
    @Bean
    public RepositoryService getRepositoryService() {
        return getProcessEngine().getRepositoryService();
    }

    /**
     * 运行时服务
     *
     * @return
     */
    @Bean
    public RuntimeService getRuntimeService() {
        return getProcessEngine().getRuntimeService();
    }

    /**
     * 表单服务
     *
     * @return
     */
    @Bean
    public FormService getFormService() {
        return getProcessEngine().getFormService();
    }

    /**
     * 任务服务
     *
     * @return
     */
    @Bean
    public TaskService getTaskService() {
        return getProcessEngine().getTaskService();
    }

    /**
     * 历史服务
     *
     * @return
     */
    @Bean
    public HistoryService getHistoryService() {
        return getProcessEngine().getHistoryService();
    }

    /**
     * 认证服务
     *
     * @return
     */
    @Bean
    public IdentityService getIdentityService() {
        return getProcessEngine().getIdentityService();
    }

    /**
     * 管理服务
     *
     * @return
     */
    @Bean
    public ManagementService getManagementService() {
        return getProcessEngine().getManagementService();
    }

}
