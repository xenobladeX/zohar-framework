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
package com.xenoblade.zohar.framework.core.web.advice;

import com.xenoblade.zohar.framework.core.api.dto.Response;
import com.xenoblade.zohar.framework.core.api.exception.BizException;
import com.xenoblade.zohar.framework.core.api.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ZoharRestExceptionHandler
 *
 * @author xenoblade
 * @since 0.0.1
 */
@RestControllerAdvice
@Slf4j
public class ZoharRestExceptionHandler {

    @ExceptionHandler(SysException.class)
    public ResponseEntity<Response> handleSysException(HttpServletRequest request, final SysException ex, HttpServletResponse response) {
        log.error("System exception: {}", ex.getMessage());
        Response responseBody = Response.error(ex.getErrCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Response> handleBizException(HttpServletRequest request, final BizException ex, HttpServletResponse response) {
        log.error("Biz exception: {}", ex.getMessage());
        Response responseBody = Response.error(ex.getErrCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

}