/*
 * Copyright [2022] [xenoblade]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.core.boot.extension;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.xenoblade.zohar.framework.core.extension.ExtensionFinder;
import com.xenoblade.zohar.framework.core.extension.ExtensionWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * SpringExtensionLoader
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Slf4j
public class SpringExtensionLoader implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ExtensionFinder extensionFinder;

    public SpringExtensionLoader(ExtensionFinder extensionFinder) {
        this.extensionFinder = extensionFinder;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void registerExtensions() {
        List<ExtensionWrapper> extensionWrappers= extensionFinder.find();
        AbstractAutowireCapableBeanFactory beanFactory = (AbstractAutowireCapableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        for (ExtensionWrapper extensionWrapper : extensionWrappers) {
            Class<?> extensionClass = extensionWrapper.getDescriptor().extensionClass;
            if (AnnotationUtil.hasAnnotation(extensionClass, ExtensionComponent.class)) {
                log.debug("Register extension '{}' as bean", extensionClass);
                Map<String, ?> extensionBeanMap = applicationContext.getBeansOfType(extensionClass);
                if (extensionBeanMap.isEmpty()) {
                    Object extension = extensionWrapper.getExtension();
                    String annotationBeanName = AnnotationUtil.getAnnotation(extensionClass, ExtensionComponent.class).name();
                    String beanName = StrUtil.isEmpty(annotationBeanName) ? StrUtil.lowerFirst(ClassUtil.getClassName(extensionClass, true)) : annotationBeanName;
                    beanFactory.registerSingleton(beanName, extension);
                } else {
                    log.debug("Bean registeration aborted! Extension '{}' already existed as bean!", extensionClass.getName());
                }
            }
        }
    }

}