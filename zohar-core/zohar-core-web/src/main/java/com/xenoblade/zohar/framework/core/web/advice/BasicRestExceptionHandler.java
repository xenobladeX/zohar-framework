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

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.threadpool.agent.internal.javassist.NotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xenoblade.zohar.framework.core.api.dto.BasicErrorCode;
import com.xenoblade.zohar.framework.core.api.dto.Response;
import com.xenoblade.zohar.framework.core.api.validate.SimpleValidateResults;
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

/**
 * ZoharRestExceptionHandler
 *
 * @author xenoblade
 * @since 0.0.1
 */
@RestControllerAdvice
@Slf4j
public class BasicRestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 其他异常的处理
     *
     * @param request
     * @param ex
     * @param response
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Response> handleException(HttpServletRequest request, final Throwable ex, HttpServletResponse response) {
        log.error("Unhandle error: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.INNER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBody);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Response> handleNotFoundException(HttpServletRequest request, final NotFoundException ex, HttpServletResponse response) {
        log.warn("Api not found: {}", ex.getMessage());
        Response responseBody = Response.error(BasicErrorCode.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<Response> handleJsonProcessingException(HttpServletRequest request, final JsonProcessingException ex, HttpServletResponse response) {
        log.warn("Json process error: {}", ex.getMessage());
        Response responseBody = Response.error(BasicErrorCode.JASON_PROCESS_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response> handleConstraintViolationException(HttpServletRequest request, final ConstraintViolationException ex, HttpServletResponse response) {
        log.warn(ex.getMessage(), ex);

        SimpleValidateResults results = new SimpleValidateResults();

        ex.getConstraintViolations()
                .stream()
                .filter(ConstraintViolation.class::isInstance)
                .map(ConstraintViolation.class::cast)
                .forEach(violation -> results.addResult(violation.getPropertyPath().toString(), violation.getMessage()));
        Response responseBody = Response
                .error(BasicErrorCode.PARAM_NOT_VALID,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage());
        responseBody.setData(results.getResults());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);

    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Method not allowed: {}", ex.getMessage());
        Response responseBody = Response.error(BasicErrorCode.METHOD_NOT_ALLOWED);
        if (StrUtil.isNotEmpty(ex.getMethod())) {
            responseBody.setErrMessage(StrUtil.format("Method {} not allowed", ex.getMethod()));
        }
        responseBody.setData(ex.getSupportedHttpMethods());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Media type not supported: {}", ex.getMessage());

        Response responseBody = Response.error(BasicErrorCode.MEDIA_TYPE_NOT_SUPPORTED);
        if (ex.getContentType() != null) {
            responseBody.setErrMessage(StrUtil.format("Media type {} not supported", ex.getContentType()));
        }
        responseBody.setData(ex.getContentType());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Media type can't acceptable: {}", ex.getMessage());

        Response responseBody = Response.error(BasicErrorCode.MEDIA_TYPE_NOT_ACCEPTABLE);
        String contentType = request.getHeader("Content-Type");
        if (contentType != null) {
            String message = StrUtil.format("Media type can't acceptable：{}", contentType);
            responseBody.setErrMessage(message);
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Path variable is missed: {}", ex.getMessage());

        Response responseBody = Response.error(BasicErrorCode.PARAM_MISS);
        if (StrUtil.isNotEmpty(ex.getVariableName())) {
            String message = StrUtil.format("Path variable '{}' is missed", ex.getVariableName());
            responseBody.setErrMessage(message);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Servlet request param is missed: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.PARAM_MISS);
        if (StrUtil.isNotEmpty(ex.getParameterName())) {
            String message = StrUtil.format("Servlet request param '{}' is missed", ex.getParameterName());
            responseBody.setErrMessage(message);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Servlet request bind error: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Conversion not supported: {}", ex.getMessage());

        return super.handleConversionNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        log.warn(ex.getMessage(), ex);

        String messsage = StrUtil.format("Can't convert type '{}' to type '{}'", ClassUtils.getDescriptiveType(ex.getValue()), ex.getValue(), ex.getRequiredType());
        Response responseMessage = Response
                .error(BasicErrorCode.TYPE_MISMATCH, messsage);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn(ex.getMessage(), ex);

        if (ex.getCause() instanceof JsonParseException) {
            Response responseMessage = Response
                    .error(BasicErrorCode.JASON_PROCESS_ERROR);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseMessage);
        } else {
            Response responseMessage = Response
                    .error(BasicErrorCode.BAD_REQUEST);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(responseMessage);
        }
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Http message not writable: {}", ex.getMessage());

        return super.handleHttpMessageNotWritable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Method param no valid: {}", ex.getMessage());

        SimpleValidateResults results = new SimpleValidateResults();
        ex.getBindingResult().getAllErrors()
                .stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .forEach(fieldError -> results.addResult(fieldError.getField(), fieldError.getDefaultMessage()));
        Response responseBody = Response
                .error(BasicErrorCode.PARAM_NOT_VALID,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage());
        responseBody.setData(results.getResults());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("Servlet request miss part: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.PARAM_MISS, StrUtil.format("miss request part {}", ex.getRequestPartName()));
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex,
                                                         HttpHeaders headers,
                                                         HttpStatus status,
                                                         WebRequest request) {
        log.warn("Bind error: {}", ex.getMessage());

        SimpleValidateResults results = new SimpleValidateResults();
        ex.getBindingResult().getAllErrors()
                .stream()
                .filter(FieldError.class::isInstance)
                .map(FieldError.class::cast)
                .forEach(fieldError -> results.addResult(fieldError.getField(), fieldError.getDefaultMessage()));
        Response responseBody = Response
                .error(BasicErrorCode.BAD_REQUEST,
                        results.getResults().isEmpty() ?
                                ex.getMessage() :
                                results.getResults().get(0).getMessage());
        responseBody.setData(results.getResults());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        log.warn("No handler Found: {}", ex.getMessage());

        String message = StrUtil.format("Can't find handler for uri {} with method {}", ex.getRequestURL(), ex.getHttpMethod());
        Response responseBody = Response
                .error(BasicErrorCode.NOT_FOUND, message);
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status,
            WebRequest webRequest) {
        log.warn("Process async request timeout: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.SERVICE_UNAVAILABLE);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(responseBody);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        log.error("Unhandle error: {}", ex.getMessage());

        Response responseBody = Response
                .error(BasicErrorCode.INNER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseBody);
    }

}