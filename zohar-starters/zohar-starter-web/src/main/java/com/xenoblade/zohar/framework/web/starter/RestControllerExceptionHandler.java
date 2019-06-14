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
package com.xenoblade.zohar.framework.web.starter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoblade.zohar.framework.commons.api.enums.ZoharErrorCode;
import com.xenoblade.zohar.framework.commons.api.exception.NotFoundException;
import com.xenoblade.zohar.framework.commons.api.exception.UnauthorizedException;
import com.xenoblade.zohar.framework.commons.api.exception.ZoharException;
import com.xenoblade.zohar.framework.commons.api.validate.SimpleValidateResults;
import com.xenoblade.zohar.framework.commons.api.validate.ValidateResults;
import com.xenoblade.zohar.framework.commons.web.msg.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.ClassUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * RestControllerExceptionHandler
 * @author xenoblade
 * @since 1.0.0
 */
@RestControllerAdvice
@Slf4j
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {


//    Custom Exception

    @ExceptionHandler(ZoharException.class)
    public ResponseEntity<ResponseMessage> handleZoharException(HttpServletRequest request, final ZoharException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);
        ResponseMessage responseMessage = ResponseMessage.error(ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseMessage> handleNotFoundException(HttpServletRequest request, final NotFoundException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);
        ResponseMessage responseMessage = ResponseMessage.error(ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(responseMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseMessage> handleNotFoundException(HttpServletRequest request, final UnauthorizedException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);
        ResponseMessage responseMessage = ResponseMessage.error(ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(responseMessage);
    }

//    Third Exception

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Object> handleJsonProcessingException(HttpServletRequest request, final JsonProcessingException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.JSON_FORMAT_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(HttpServletRequest request, final ConstraintViolationException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);

        SimpleValidateResults results = new SimpleValidateResults();

        ex.getConstraintViolations()
                .stream()
                .filter(ConstraintViolation.class::isInstance)
                .map(ConstraintViolation.class::cast)
                .forEach(violation -> results.addResult(violation.getPropertyPath().toString(), violation.getMessage()));
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.METHOD_ARGUMENT_NOT_VALID,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage()).result(results.getResults());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);

    }

    /**
     * 其他异常的处理
     * @param request
     * @param ex
     * @param response
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(HttpServletRequest request, final Exception ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.INNER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }


    @Override protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        ResponseMessage responseMessage = ResponseMessage.error(ZoharErrorCode.METHOD_NOT_ALLOWED)
                .result(ex.getSupportedHttpMethods());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage.error( ZoharErrorCode.UNSUPPORTED_MEDIA_TYPE)
                .result(ex.getContentType());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage.error(ZoharErrorCode.NOT_ACCEPTABLE);
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            String message = StrUtil.format("无法接收的媒体类型：{}", contentType);
            responseMessage.message(message);
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        String message = StrUtil.format("路径字段 {} 校验不通过", ex.getVariableName());
        ResponseMessage responseMessage = ResponseMessage.error( ZoharErrorCode.UNSUPPORTED_MEDIA_TYPE, message);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        String message = StrUtil.format("参数[{}]不能为空", ex.getParameterName());
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.BAD_REQUEST, message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        return super.handleConversionNotSupported(ex, headers, status, request);
    }

    @Override protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        log.warn(ex.getMessage(), ex);

        String messsage = StrUtil.format("无法将{}类型的值{}转为类型{}", ClassUtils.getDescriptiveType(ex.getValue()), ex.getValue(), ex.getRequiredType());
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.TYPE_MISMATCH, messsage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        if (ex.getCause() instanceof JsonParseException) {
            ResponseMessage responseMessage = ResponseMessage
                    .error(ZoharErrorCode.JSON_FORMAT_ERROR);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseMessage);
        } else {
            ResponseMessage responseMessage = ResponseMessage
                    .error(ZoharErrorCode.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseMessage);
        }
    }

    @Override protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        return super.handleHttpMessageNotWritable(ex, headers, status, request);
    }

    @Override protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        SimpleValidateResults results = new SimpleValidateResults();
        ex.getBindingResult().getAllErrors()
                .stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .forEach(fieldError -> results.addResult(fieldError.getField(), fieldError.getDefaultMessage()));
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.METHOD_ARGUMENT_NOT_VALID,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage()).result(results.getResults());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleBindException(BindException ex,
                                                                   HttpHeaders headers,
                                                                   HttpStatus status,
                                                                   WebRequest request) {
        log.warn(ex.getMessage(), ex);

        SimpleValidateResults results = new SimpleValidateResults();
        ex.getBindingResult().getAllErrors()
                .stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .forEach(fieldError -> results.addResult(fieldError.getField(), fieldError.getDefaultMessage()));
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.BAD_REQUEST,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage()).result(results.getResults());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        String message = StrUtil.format("{}地址{}不存在", ex.getRequestURL(), ex.getHttpMethod());
        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.NOT_FOUND, message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status,
            WebRequest webRequest) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(responseMessage);
    }

    @Override protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                                       HttpHeaders headers,
                                                                       HttpStatus status,
                                                                       WebRequest request) {
        log.warn(ex.getMessage(), ex);

        ResponseMessage responseMessage = ResponseMessage
                .error(ZoharErrorCode.INNER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseMessage);
    }
}
