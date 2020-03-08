package com.zyc.file_system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="file.base")
@Data
public class FileBaseConfiguration {

    private String path;

    private String regex;

    private String folder;

}
