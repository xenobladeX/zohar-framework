/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.starter.spring.boot.dao;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * MyBatisAutoConfiguration
 * @author xenoblade
 * @since 1.0.0
 */
@EnableTransactionManagement
@Configuration
public class MyBatisAutoConfiguration {

    /**
     * 逻辑删除插件
     * @return
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    /**
     * 分页插件
     * @return 分页拦截器
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 性能分析插件
     * @return 性能拦截器
     */
    @ConditionalOnProperty(prefix = "zohar.mybatis", name = "performance", havingValue = "true")
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }

    /**
     * 乐观锁插件
     * @return 乐观锁拦截器
     */
    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    // TODO: sqlSessionFactory
//    @Bean(name = "sqlSessionFactory")
//    @Primary
//    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        MybatisProperties mybatisProperties = this.mybatisProperties();
//        if (null != entityFactory) {
//            factory.setObjectFactory(new MybatisEntityFactory(entityFactory));
//        }
//        factory.setVfs(SpringBootVFS.class);
//        if (mybatisProperties().isDynamicDatasource()) {
//            factory.setSqlSessionFactoryBuilder(new DynamicDataSourceSqlSessionFactoryBuilder());
//            factory.setTransactionFactory(new SpringManagedTransactionFactory() {
//                @Override
//                public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
//                    return new DynamicSpringManagedTransaction();
//                }
//            });
//        }
//        factory.setDataSource(dataSource);
//        if (StringUtils.hasText(mybatisProperties.getConfigLocation())) {
//            factory.setConfigLocation(this.resourceLoader.getResource(mybatisProperties
//                    .getConfigLocation()));
//        }
//        if (mybatisProperties.getConfiguration() != null) {
//            factory.setConfiguration(mybatisProperties.getConfiguration());
//        }
//        if (this.interceptors != null && this.interceptors.length > 0) {
//            factory.setPlugins(this.interceptors);
//        }
//        if (this.databaseIdProvider != null) {
//            factory.setDatabaseIdProvider(this.databaseIdProvider);
//        }
//        factory.setTypeAliasesPackage(mybatisProperties.getTypeAliasesPackage());
//        String typeHandlers = "com.cmcc.videopp.mainas.vacon.commons.dao.mybatis.handler";
//        if (mybatisProperties.getTypeHandlersPackage() != null) {
//            typeHandlers = typeHandlers + ";" + mybatisProperties.getTypeHandlersPackage();
//        }
//        factory.setTypeHandlersPackage(typeHandlers);
//        factory.setMapperLocations(mybatisProperties.resolveMapperLocations());
//
//        SqlSessionFactory sqlSessionFactory = factory.getObject();
//        MybatisUtils.sqlSession = sqlSessionFactory;
//
//        EnumDictHandlerRegister.typeHandlerRegistry = sqlSessionFactory.getConfiguration().getTypeHandlerRegistry();
//        EnumDictHandlerRegister.register("com.cmcc.videopp.mainas.vacon.commons;" + mybatisProperties.getTypeHandlersPackage());
//
//        try {
//            Class.forName("javax.persistence.Table");
//            EasyOrmSqlBuilder.getInstance().useJpa = mybatisProperties.isUseJpa();
//        } catch (@SuppressWarnings("all") Exception ignore) {
//        }
//        EasyOrmSqlBuilder.getInstance().entityFactory = entityFactory;
//
//        return sqlSessionFactory;
//    }

}
