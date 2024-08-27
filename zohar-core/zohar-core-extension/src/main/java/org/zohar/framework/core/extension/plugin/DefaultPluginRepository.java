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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zohar.framework.core.extension.util.file.AndFileFilter;
import org.zohar.framework.core.extension.util.file.DirectoryFileFilter;
import org.zohar.framework.core.extension.util.file.FileUtils;
import org.zohar.framework.core.extension.util.file.HiddenFileFilter;
import org.zohar.framework.core.extension.util.file.NotFileFilter;
import org.zohar.framework.core.extension.util.file.OrFileFilter;
import org.zohar.framework.core.extension.util.file.ZipFileFilter;

/**
 * @author Decebal Suiu
 * @Date 2024/8/27
 * @since 0.0.1-SNAPSHOT
 */
public class DefaultPluginRepository extends BasePluginRepository {

    private static final Logger log = LoggerFactory.getLogger(DefaultPluginRepository.class);

    public DefaultPluginRepository(Path... pluginsRoots) {
        this(Arrays.asList(pluginsRoots));
    }

    public DefaultPluginRepository(List<Path> pluginsRoots) {
        super(pluginsRoots);

        AndFileFilter pluginsFilter = new AndFileFilter(new DirectoryFileFilter());
        pluginsFilter.addFileFilter(new NotFileFilter(createHiddenPluginFilter()));
        setFilter(pluginsFilter);
    }

    @Override
    public List<Path> getPluginPaths() {
        extractZipFiles();
        return super.getPluginPaths();
    }

    @Override
    public boolean deletePluginPath(Path pluginPath) {
        FileUtils.optimisticDelete(FileUtils.findWithEnding(pluginPath, ".zip", ".ZIP", ".Zip"));
        return super.deletePluginPath(pluginPath);
    }

    protected FileFilter createHiddenPluginFilter() {
        return new OrFileFilter(new HiddenFileFilter());
    }

    private void extractZipFiles() {
        // expand plugins zip files
        pluginsRoots.stream()
                .flatMap(path -> streamFiles(path, new ZipFileFilter()))
                .map(File::toPath)
                .forEach(this::expandIfZip);
    }

    private void expandIfZip(Path filePath) {
        try {
            FileUtils.expandIfZip(filePath);
        } catch (IOException e) {
            log.error("Cannot expand plugin zip '{}'", filePath);
            log.error(e.getMessage(), e);
        }
    }

}