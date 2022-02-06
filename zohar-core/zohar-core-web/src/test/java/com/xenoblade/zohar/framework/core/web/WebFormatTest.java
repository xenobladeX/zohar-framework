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
package com.xenoblade.zohar.framework.core.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * WebTest
 *
 * @author xenoblade
 * @since 0.0.1
 */
@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
@Slf4j
public class WebFormatTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testResponseString() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/getString"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"errCode\":100000,\"errMessage\":\"OK\",\"data\":\"Hello world\"}"));
    }

    @Test
    public void testResponseBean() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/getBean"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"errCode\":100000,\"errMessage\":\"OK\",\"data\":{\"along\":\"3257342073940842385\",\"alocalDateTime\":\"2022-02-03 16:57:00\"}}"));
    }

    @Test
    public void testResponseResponse() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/test/getResponse"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("{\"errCode\":100000,\"errMessage\":\"OK\"}"));
    }

}