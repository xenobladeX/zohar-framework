package com.xenoblade.zohar.sample.baal.logger;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xenoblade.zohar.framework.commons.spring.log.api.AccessLoggerInfo;
import com.xenoblade.zohar.framework.commons.spring.log.api.event.AccessLoggerAfterEvent;
import com.xenoblade.zohar.framework.commons.utils.JacksonUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 使用sfl4j 打印访问日志
 *
 * @author xenoblade
 */
@Component public class Slf4jAccessLoggerListener {
    private Logger logger = LoggerFactory.getLogger("access-logger");

    private static ObjectMapper objectMapper;

    @PostConstruct public void init() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(ServletRequest.class, LogMixInForIgnoreType.class);
        mapper.addMixIn(ServletResponse.class, LogMixInForIgnoreType.class);
        mapper.addMixIn(InputStream.class, LogMixInForIgnoreType.class);
        mapper.addMixIn(OutputStream.class, LogMixInForIgnoreType.class);
        mapper.addMixIn(MultipartFile.class, LogMixInForIgnoreType.class);

        objectMapper = JacksonUtils.initWrapperObjectMapper(mapper);
    }

    @JsonIgnoreType public class LogMixInForIgnoreType {
    }

    @EventListener public void onLogger(AccessLoggerAfterEvent event) {
        AccessLoggerInfo info = event.getLogger();

        try {
            if (info.getException() != null) {
                logger.error(objectMapper.writeValueAsString(info));
            } else if (logger.isInfoEnabled()) {
                logger.info(objectMapper.writeValueAsString(info));
            }
        } catch (JsonProcessingException ex) {
            logger.error("Json Processing failed for AccessLoggerInfo: ", ex);
        }
    }
}
