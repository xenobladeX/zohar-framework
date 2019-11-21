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
package com.xenoblade.zohar.framework.commons.web.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * AbstractMessageConverterProcessor
 * @author xenoblade
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractMessageConverterProcessor implements MessageConverterProcessor, MethodReturnValueHandler{

    private static final Type RESOURCE_REGION_LIST_TYPE =
            new ParameterizedTypeReference<List<ResourceRegion>>() { }.getType();

    private static final MediaType MEDIA_TYPE_APPLICATION = new MediaType("application");

    private static final Set<String> WHITELISTED_MEDIA_BASE_TYPES = new HashSet<>(
            Arrays.asList("audio", "image", "video"));

    /* Extensions associated with the built-in message converters */
    private static final Set<String> WHITELISTED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "text", "yml", "properties", "csv",
            "json", "xml", "atom", "rss",
            "png", "jpe", "jpeg", "jpg", "gif", "wbmp", "bmp"));

    private static final Object NO_VALUE = new Object();

    private static final Set<HttpMethod> SUPPORTED_METHODS =
            EnumSet.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH);



    protected final List<HttpMessageConverter<?>> messageConverters;

    private final ContentNegotiationManager contentNegotiationManager;

    protected final List<MediaType> allSupportedMediaTypes;

    private static final UrlPathHelper decodingUrlPathHelper = new UrlPathHelper();

    private static final UrlPathHelper rawUrlPathHelper = new UrlPathHelper();

    private final Set<String> safeExtensions = new HashSet<>();

    private final PathExtensionContentNegotiationStrategy pathStrategy;

    static {
        rawUrlPathHelper.setRemoveSemicolonContent(false);
        rawUrlPathHelper.setUrlDecode(false);
    }



    /**
     * Constructor with list of converters only.
     */
    protected AbstractMessageConverterProcessor(List<HttpMessageConverter<?>> converters) {
        this(converters, null);
    }

    /**
     * Constructor with list of converters and ContentNegotiationManager.
     */
    protected AbstractMessageConverterProcessor(List<HttpMessageConverter<?>> converters,
                                                      @Nullable ContentNegotiationManager contentNegotiationManager) {

        this.messageConverters = converters;
        this.allSupportedMediaTypes = getAllSupportedMediaTypes(converters);

        this.contentNegotiationManager = (contentNegotiationManager != null ? contentNegotiationManager : new ContentNegotiationManager());
        this.pathStrategy = initPathStrategy(this.contentNegotiationManager);
        //        this.safeExtensions.addAll(this.contentNegotiationManager.getAllFileExtensions());
        //        this.safeExtensions.addAll(WHITELISTED_EXTENSIONS);
    }



    /**
     * Return the media types supported by all provided message converters sorted
     * by specificity via {@link MediaType#sortBySpecificity(List)}.
     */
    private static List<MediaType> getAllSupportedMediaTypes(List<HttpMessageConverter<?>> messageConverters) {
        Set<MediaType> allSupportedMediaTypes = new LinkedHashSet<>();
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            allSupportedMediaTypes.addAll(messageConverter.getSupportedMediaTypes());
        }
        List<MediaType> result = new ArrayList<>(allSupportedMediaTypes);
        MediaType.sortBySpecificity(result);
        return Collections.unmodifiableList(result);
    }

    private static PathExtensionContentNegotiationStrategy initPathStrategy(ContentNegotiationManager manager) {
        Class<PathExtensionContentNegotiationStrategy> clazz = PathExtensionContentNegotiationStrategy.class;
        PathExtensionContentNegotiationStrategy strategy = manager.getStrategy(clazz);
        return (strategy != null ? strategy : new PathExtensionContentNegotiationStrategy());
    }

    protected  <T> Object readWithMessageConverters(HttpServletRequest request,
                                                    MethodParameter parameter, Type targetType)
            throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {

        HttpInputMessage inputMessage = new ServletServerHttpRequest(request);
        return readWithMessageConverters(inputMessage, parameter, targetType);
    }


    protected  <T> Object readWithMessageConverters(HttpInputMessage inputMessage,
                                                          MethodParameter parameter, Type targetType)
            throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        MediaType contentType;
        boolean noContentType = false;
        try {
            contentType = inputMessage.getHeaders().getContentType();
        }
        catch (InvalidMediaTypeException ex) {
            throw new HttpMediaTypeNotSupportedException(ex.getMessage());
        }
        if (contentType == null) {
            noContentType = true;
            contentType = MediaType.APPLICATION_OCTET_STREAM;
        }

        Class<?> contextClass = parameter.getContainingClass();
        Class<T> targetClass = (targetType instanceof Class ? (Class<T>) targetType : null);
        if (targetClass == null) {
            ResolvableType resolvableType = ResolvableType.forMethodParameter(parameter);
            targetClass = (Class<T>) resolvableType.resolve();
        }

        HttpMethod httpMethod = (inputMessage instanceof HttpRequest ? ((HttpRequest) inputMessage).getMethod() : null);
        Object body = NO_VALUE;

        EmptyBodyCheckingHttpInputMessage message;
        try {
            message = new EmptyBodyCheckingHttpInputMessage(inputMessage);

            for (HttpMessageConverter<?> converter : this.messageConverters) {
                Class<HttpMessageConverter<?>> converterType = (Class<HttpMessageConverter<?>>) converter.getClass();
                GenericHttpMessageConverter<?> genericConverter =
                        (converter instanceof GenericHttpMessageConverter ? (GenericHttpMessageConverter<?>) converter : null);
                if (genericConverter != null ? genericConverter.canRead(targetType, contextClass, contentType) :
                        (targetClass != null && converter.canRead(targetClass, contentType))) {
                    if (message.hasBody()) {
//                        HttpInputMessage msgToUse =
//                                getAdvice().beforeBodyRead(message, parameter, targetType, converterType);
                        body = (genericConverter != null ? genericConverter.read(targetType, contextClass, message) :
                                ((HttpMessageConverter<T>) converter).read(targetClass, message));
//                        body = getAdvice().afterBodyRead(body, msgToUse, parameter, targetType, converterType);
                    }
                    else {
//                        body = getAdvice().handleEmptyBody(null, message, parameter, targetType, converterType);
                        body = null;
                    }
                    break;
                }
            }
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("I/O error while reading input message", ex, inputMessage);
        }

        if (body == NO_VALUE) {
            if (httpMethod == null || !SUPPORTED_METHODS.contains(httpMethod) ||
                    (noContentType && !message.hasBody())) {
                return null;
            }
            throw new HttpMediaTypeNotSupportedException(contentType, this.allSupportedMediaTypes);
        }

        MediaType selectedContentType = contentType;
        Object theBody = body;
        if(log.isTraceEnabled()) {
            String formatted = LogFormatUtils.formatValue(theBody, false);
            log.trace("Read \"" + selectedContentType + "\" to [" + formatted + "]");
        }

        return body;
    }

    protected <T> void writeWithMessageConverters(@Nullable T value, MethodParameter returnType, ServletServerHttpRequest inputMessage, ServletServerHttpResponse outputMessage) throws
            IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {

        Object body;
        Class<?> valueType;
        Type targetType;

        if (value instanceof CharSequence) {
            body = value.toString();
            valueType = String.class;
            targetType = String.class;
        }
        else {
            body = value;
            valueType =  getReturnValueType(body, returnType);
            targetType = GenericTypeResolver.resolveType(getGenericType(returnType), returnType.getContainingClass());
        }

        if (isResourceType(value, returnType)) {
            outputMessage.getHeaders().set(HttpHeaders.ACCEPT_RANGES, "bytes");
            if (value != null && inputMessage.getHeaders().getFirst(HttpHeaders.RANGE) != null &&
                    outputMessage.getServletResponse().getStatus() == 200) {
                Resource resource = (Resource) value;
                try {
                    List<HttpRange> httpRanges = inputMessage.getHeaders().getRange();
                    outputMessage.getServletResponse().setStatus(HttpStatus.PARTIAL_CONTENT.value());
                    body = HttpRange.toResourceRegions(httpRanges, resource);
                    valueType = body.getClass();
                    targetType = RESOURCE_REGION_LIST_TYPE;
                }
                catch (IllegalArgumentException ex) {
                    outputMessage.getHeaders().set(HttpHeaders.CONTENT_RANGE, "bytes */" + resource.contentLength());
                    outputMessage.getServletResponse().setStatus(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value());
                }
            }
        }

        MediaType selectedMediaType = null;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType != null && contentType.isConcrete()) {
            if (log.isDebugEnabled()) {
                log.debug("Found 'Content-Type:" + contentType + "' in response");
            }
            selectedMediaType = contentType;
        }
        else {
            HttpServletRequest request = inputMessage.getServletRequest();
            List<MediaType> acceptableTypes = getAcceptableMediaTypes(request);
            List<MediaType> producibleTypes = getProducibleMediaTypes(request, valueType, targetType);

            if (body != null && producibleTypes.isEmpty()) {
                throw new HttpMessageNotWritableException(
                        "No converter found for return value of type: " + valueType);
            }
            List<MediaType> mediaTypesToUse = new ArrayList<>();
            for (MediaType requestedType : acceptableTypes) {
                for (MediaType producibleType : producibleTypes) {
                    if (requestedType.isCompatibleWith(producibleType)) {
                        mediaTypesToUse.add(getMostSpecificMediaType(requestedType, producibleType));
                    }
                }
            }
            if (mediaTypesToUse.isEmpty()) {
                if (body != null) {
                    throw new HttpMediaTypeNotAcceptableException(producibleTypes);
                }
                if (log.isDebugEnabled()) {
                    log.debug("No match for " + acceptableTypes + ", supported: " + producibleTypes);
                }
                return;
            }

            MediaType.sortBySpecificityAndQuality(mediaTypesToUse);

            for (MediaType mediaType : mediaTypesToUse) {
                if (mediaType.isConcrete()) {
                    selectedMediaType = mediaType;
                    break;
                }
                else if (mediaType.equals(MediaType.ALL) || mediaType.equals(MEDIA_TYPE_APPLICATION)) {
                    selectedMediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Using '" + selectedMediaType + "', given " +
                        acceptableTypes + " and supported " + producibleTypes);
            }
        }

        if (selectedMediaType != null) {
            selectedMediaType = selectedMediaType.removeQualityValue();
            for (HttpMessageConverter<?> converter : this.messageConverters) {
                GenericHttpMessageConverter genericConverter = (converter instanceof GenericHttpMessageConverter ?
                        (GenericHttpMessageConverter<?>) converter : null);
                if (genericConverter != null ?
                        ((GenericHttpMessageConverter) converter).canWrite(targetType, valueType, selectedMediaType) :
                        converter.canWrite(valueType, selectedMediaType)) {
//                    body = getAdvice().beforeBodyWrite(body, returnType, selectedMediaType,
//                            (Class<? extends HttpMessageConverter<?>>) converter.getClass(),
//                            inputMessage, outputMessage);
                    if (body != null) {
                        Object theBody = body;
                        if (log.isTraceEnabled()) {
                            log.trace("Writing [" + LogFormatUtils.formatValue(theBody, true) + "]");
                        }
                        addContentDispositionHeader(inputMessage, outputMessage);
                        if (genericConverter != null) {
                            genericConverter.write(body, targetType, selectedMediaType, outputMessage);
                        }
                        else {
                            ((HttpMessageConverter) converter).write(body, selectedMediaType, outputMessage);
                        }
                    }
                    else {
                        if (log.isDebugEnabled()) {
                            log.debug("Nothing to write: null body");
                        }
                    }
                    return;
                }
            }
        }

        if (body != null) {
            throw new HttpMediaTypeNotAcceptableException(this.allSupportedMediaTypes);
        }

    }

    /**
     * Adapt the given argument against the method parameter, if necessary.
     * @param arg the resolved argument
     * @param parameter the method parameter descriptor
     * @return the adapted argument, or the original resolved argument as-is
     * @since 4.3.5
     */
    @Nullable
    protected Object adaptArgumentIfNecessary(@Nullable Object arg, MethodParameter parameter) {
        if (parameter.getParameterType() == Optional.class) {
            if (arg == null || (arg instanceof Collection && ((Collection<?>) arg).isEmpty()) ||
                    (arg instanceof Object[] && ((Object[]) arg).length == 0)) {
                return Optional.empty();
            }
            else {
                return Optional.of(arg);
            }
        }
        return arg;
    }

    /**
     * Return the type of the value to be written to the response. Typically this is
     * a simple check via getClass on the value but if the value is null, then the
     * return type needs to be examined possibly including generic type determination
     * (e.g. {@code ResponseEntity<T>}).
     */
    protected Class<?> getReturnValueType(@org.springframework.lang.Nullable Object value, MethodParameter returnType) {
        return (value != null ? value.getClass() : returnType.getParameterType());
    }

    /**
     * Return whether the returned value or the declared return type extends {@link Resource}.
     */
    protected boolean isResourceType(@org.springframework.lang.Nullable Object value, MethodParameter returnType) {
        Class<?> clazz = getReturnValueType(value, returnType);
        return clazz != InputStreamResource.class && Resource.class.isAssignableFrom(clazz);
    }

    /**
     * Return the generic type of the {@code returnType} (or of the nested type
     * if it is an {@link HttpEntity}).
     */
    private Type getGenericType(MethodParameter returnType) {
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric().getType();
        }
        else {
            return returnType.getGenericParameterType();
        }
    }

    private List<MediaType> getAcceptableMediaTypes(HttpServletRequest request)
            throws HttpMediaTypeNotAcceptableException {

        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    /**
     * Return the more specific of the acceptable and the producible media types
     * with the q-value of the former.
     */
    private MediaType getMostSpecificMediaType(MediaType acceptType, MediaType produceType) {
        MediaType produceTypeToUse = produceType.copyQualityValue(acceptType);
        return (MediaType.SPECIFICITY_COMPARATOR.compare(acceptType, produceTypeToUse) <= 0 ? acceptType : produceTypeToUse);
    }


    /**
     * Returns the media types that can be produced.
     * @see #getProducibleMediaTypes(HttpServletRequest, Class, Type)
     */
    @SuppressWarnings("unused")
    protected List<MediaType> getProducibleMediaTypes(HttpServletRequest request, Class<?> valueClass) {
        return getProducibleMediaTypes(request, valueClass, null);
    }

    /**
     * Returns the media types that can be produced. The resulting media types are:
     * <ul>
     * <li>The producible media types specified in the request mappings, or
     * <li>Media types of configured converters that can write the specific return value, or
     * <li>{@link MediaType#ALL}
     * </ul>
     * @since 4.2
     */
    @SuppressWarnings("unchecked")
    protected List<MediaType> getProducibleMediaTypes(
            HttpServletRequest request, Class<?> valueClass, @Nullable Type targetType) {

        Set<MediaType> mediaTypes =
                (Set<MediaType>) request.getAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            return new ArrayList<>(mediaTypes);
        }
        else if (!this.allSupportedMediaTypes.isEmpty()) {
            List<MediaType> result = new ArrayList<>();
            for (HttpMessageConverter<?> converter : this.messageConverters) {
                if (converter instanceof GenericHttpMessageConverter && targetType != null) {
                    if (((GenericHttpMessageConverter<?>) converter).canWrite(targetType, valueClass, null)) {
                        result.addAll(converter.getSupportedMediaTypes());
                    }
                }
                else if (converter.canWrite(valueClass, null)) {
                    result.addAll(converter.getSupportedMediaTypes());
                }
            }
            return result;
        }
        else {
            return Collections.singletonList(MediaType.ALL);
        }
    }

    /**
     * Check if the path has a file extension and whether the extension is
     * either {@link #WHITELISTED_EXTENSIONS whitelisted} or explicitly
     * {@link ContentNegotiationManager#getAllFileExtensions() registered}.
     * If not, and the status is in the 2xx range, a 'Content-Disposition'
     * header with a safe attachment file name ("f.txt") is added to prevent
     * RFD exploits.
     */
    private void addContentDispositionHeader(ServletServerHttpRequest request, ServletServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        if (headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            return;
        }

        try {
            int status = response.getServletResponse().getStatus();
            if (status < 200 || status > 299) {
                return;
            }
        }
        catch (Throwable ex) {
            // ignore
        }

        HttpServletRequest servletRequest = request.getServletRequest();
        String requestUri = rawUrlPathHelper.getOriginatingRequestUri(servletRequest);

        int index = requestUri.lastIndexOf('/') + 1;
        String filename = requestUri.substring(index);
        String pathParams = "";

        index = filename.indexOf(';');
        if (index != -1) {
            pathParams = filename.substring(index);
            filename = filename.substring(0, index);
        }

        filename = decodingUrlPathHelper.decodeRequestString(servletRequest, filename);
        String ext = StringUtils.getFilenameExtension(filename);

        pathParams = decodingUrlPathHelper.decodeRequestString(servletRequest, pathParams);
        String extInPathParams = StringUtils.getFilenameExtension(pathParams);

        if (!safeExtension(servletRequest, ext) || !safeExtension(servletRequest, extInPathParams)) {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=f.txt");
        }
    }

    @SuppressWarnings("unchecked")
    private boolean safeExtension(HttpServletRequest request, @Nullable String extension) {
        if (!StringUtils.hasText(extension)) {
            return true;
        }
        extension = extension.toLowerCase(Locale.ENGLISH);
        if (this.safeExtensions.contains(extension)) {
            return true;
        }
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null && pattern.endsWith("." + extension)) {
            return true;
        }
        if (extension.equals("html")) {
            String name = HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
            Set<MediaType> mediaTypes = (Set<MediaType>) request.getAttribute(name);
            if (!CollectionUtils.isEmpty(mediaTypes) && mediaTypes.contains(MediaType.TEXT_HTML)) {
                return true;
            }
        }
        return safeMediaTypesForExtension(new ServletWebRequest(request), extension);
    }

    private boolean safeMediaTypesForExtension(NativeWebRequest request, String extension) {
        List<MediaType> mediaTypes = null;
        try {
            mediaTypes = this.pathStrategy.resolveMediaTypeKey(request, extension);
        }
        catch (HttpMediaTypeNotAcceptableException ex) {
            // Ignore
        }
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return false;
        }
        for (MediaType mediaType : mediaTypes) {
            if (!safeMediaType(mediaType)) {
                return false;
            }
        }
        return true;
    }

    private boolean safeMediaType(MediaType mediaType) {
        return (WHITELISTED_MEDIA_BASE_TYPES.contains(mediaType.getType()) ||
                mediaType.getSubtype().endsWith("+xml"));
    }











    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;

        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = (inputStream.read() != -1 ? inputStream : null);
                inputStream.reset();
            }
            else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                }
                else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public InputStream getBody() {
            return (this.body != null ? this.body : StreamUtils.emptyInput());
        }

        public boolean hasBody() {
            return (this.body != null);
        }
    }

}
