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
package com.xenoblade.zohar.framework.core.api;

import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.dto.IZoharErrorCode;
import com.xenoblade.zohar.framework.core.api.extension.IExtension;
import com.xenoblade.zohar.framework.core.api.extension.TestErrorCode;
import com.xenoblade.zohar.framework.core.extension.*;
import com.xenoblade.zohar.framework.core.extension.enumeration.ZoharEnumFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ExtensionTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
public class ExtensionTest {

    private Logger logger = LoggerFactory.getLogger(ExtensionTest.class);

    private ExtensionFinder extensionFinder;

    @Before
    public void init() {
        ExtensionFactory extensionFactory = new ZoharExtensionFactory();
        ZoharExtensionFinder zoharExtensionFinder = new ZoharExtensionFinder(extensionFactory);
        // add enum extension finder filter
        zoharExtensionFinder.addFilter(new ZoharEnumExtensionFinderFilter());
        MultipleExtensionFinder multipleExtensionFinder = new MultipleExtensionFinder();
        multipleExtensionFinder.add(zoharExtensionFinder);
        extensionFinder = zoharExtensionFinder;
    }

    @Test
    public void testFindExtension() {
        List<ExtensionWrapper<IExtension>> extensions = extensionFinder.find(IExtension.class);
        Assert.assertEquals( 2, extensions.size());
        for (ExtensionWrapper<IExtension> extensionWrapper : extensions) {
            IExtension extensionImpl = extensionWrapper.getExtension();
            extensionImpl.hello();
        }
    }

    @Test
    public void testZoharEnum() {
        extensionFinder.find();
        IZoharErrorCode errorCode = ZoharEnumFactory.INSTANCE.valueOf(100000, IZoharErrorCode.class);
        Assert.assertEquals(BasicErrorCode.OK, errorCode);
        errorCode = ZoharEnumFactory.INSTANCE.valueOf(666, IZoharErrorCode.class);
        Assert.assertEquals(TestErrorCode.TEST_ERROR, errorCode);
    }

}