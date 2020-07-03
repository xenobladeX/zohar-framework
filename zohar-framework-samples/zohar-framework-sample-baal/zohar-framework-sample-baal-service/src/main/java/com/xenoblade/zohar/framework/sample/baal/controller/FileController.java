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
package com.xenoblade.zohar.framework.sample.baal.controller;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * FileController
 * @author xenoblade
 * @since 1.0.0
 */
@RestController
@RequestMapping("/file")
@Validated
@Slf4j
public class FileController {

//    @PostMapping("test")
//    public void test(@RequestParam("file") MultipartFile file) {
//        StopWatch stopWatch = new StopWatch("test upload");
//        try {
//            stopWatch.start("read bytes");
//            byte[] data = StreamUtils.copyToByteArray(file.getInputStream());
//            stopWatch.stop();
//        } catch (Exception ex) {
//            log.warn("test upload failed: ", ex);
//        } finally {
//            if (stopWatch.isRunning()) {
//                stopWatch.stop();
//            }
//            log.info("test upload: {}", stopWatch.prettyPrint());
//        }
//
//    }

    @PostMapping("test")
    public void test(HttpServletRequest request) {
        StopWatch stopWatch = new StopWatch("test upload");
        try {

            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                InputStream stream = item.openStream();
                stopWatch.start(StrUtil.format("read {} value", name));
                if (!item.isFormField()) {
                    byte[] data = StreamUtils.copyToByteArray(stream);
                    log.info("data length: {}",data.length);
                } else {
                    String formFieldValue = Streams.asString(stream);
                }
                stopWatch.stop();
            }


//            FileItemIterator iter = upload.getItemIterator(request);
//            FileItemStream fileItemStream = null;
//            while (iter.hasNext()) {
//                FileItemStream item = iter.next();
//                String name = item.getFieldName();
//                stopWatch.start(StrUtil.format("read {} value", name));
//                InputStream stream = item.openStream();
//                if (!item.isFormField()) {
//                    byte[] data = StreamUtils.copyToByteArray(stream);
//                } else {
//                    String formFieldValue = Streams.asString(stream);
//                }
//                stopWatch.stop();
//            }
        } catch (Exception ex) {
            log.warn("test upload failed: ", ex);
        } finally {
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            log.info("test upload: {}", stopWatch.prettyPrint());
        }

    }

}
