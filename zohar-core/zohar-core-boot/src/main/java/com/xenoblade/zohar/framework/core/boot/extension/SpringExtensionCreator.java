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
import com.xenoblade.zohar.framework.core.extension.ExtensionCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;


/**
 * SpringExtensionCreator
 *
 * @author xenoblade
 * @since 0.0.1
 */
@Slf4j
public class SpringExtensionCreator implements ExtensionCreator {

    private ApplicationContext applicationContext;

    public SpringExtensionCreator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Boolean match(Class<?> extensionClass) {
        return AnnotationUtil.hasAnnotation(extensionClass, ExtensionComponent.class);
    }

    @Override
    public <T> T create(Class<T> extensionClass) {
        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        log.debug("Instantiate extension class '" + ClassUtil.getClassName(extensionClass, false) + "' by using constructor autowiring.");
        // Autowire by constructor. This does not include the other types of injection (setters and/or fields).
        final Object autowiredExtension = beanFactory.autowire(extensionClass, AUTOWIRE_CONSTRUCTOR,
                // The value of the 'dependencyCheck' parameter is actually irrelevant as the using constructor of 'RootBeanDefinition'
                // skips action when the autowire mode is set to 'AUTOWIRE_CONSTRUCTOR'. Although the default value in
                // 'AbstractBeanDefinition' is 'DEPENDENCY_CHECK_NONE', so it is set to false here as well.
                false);
        log.trace("Created extension instance by constructor injection: " + autowiredExtension);

        log.debug("Completing autowiring of extension: " + autowiredExtension);
        // Autowire by using remaining kinds of injection (e. g. setters and/or fields).
        beanFactory.autowireBean(autowiredExtension);
        log.trace("Autowiring has been completed for extension: " + autowiredExtension);

        return (T) autowiredExtension;
    }

    @Override
    public Integer order() {
        return 1;
    }

}