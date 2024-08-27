/*
 * Copyright [2024] [xenoblade]
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
package org.zohar.framework.core.extension.plugin;

import java.io.FileFilter;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.zohar.framework.core.extension.util.file.AndFileFilter;
import org.zohar.framework.core.extension.util.file.DirectoryFileFilter;
import org.zohar.framework.core.extension.util.file.HiddenFileFilter;
import org.zohar.framework.core.extension.util.file.NameFileFilter;
import org.zohar.framework.core.extension.util.file.NotFileFilter;
import org.zohar.framework.core.extension.util.file.OrFileFilter;

/**
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class DevelopmentPluginRepository extends BasePluginRepository {

    public static final String MAVEN_BUILD_DIR = "target";
    public static final String GRADLE_BUILD_DIR = "build";

    public DevelopmentPluginRepository(Path... pluginsRoots) {
        this(Arrays.asList(pluginsRoots));
    }

    public DevelopmentPluginRepository(List<Path> pluginsRoots) {
        super(pluginsRoots);

        AndFileFilter pluginsFilter = new AndFileFilter(new DirectoryFileFilter());
        pluginsFilter.addFileFilter(new NotFileFilter(createHiddenPluginFilter()));
        setFilter(pluginsFilter);
    }

    protected FileFilter createHiddenPluginFilter() {
        OrFileFilter hiddenPluginFilter = new OrFileFilter(new HiddenFileFilter());

        // skip default build output folders since these will cause errors in the logs
        hiddenPluginFilter
                .addFileFilter(new NameFileFilter(MAVEN_BUILD_DIR))
                .addFileFilter(new NameFileFilter(GRADLE_BUILD_DIR));

        return hiddenPluginFilter;
    }

}