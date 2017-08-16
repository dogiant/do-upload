package com.dogiant.upload.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.dogiant.upload.dao")
@EntityScan(basePackages = "com.dogiant.upload.domain")
public class JPAConfig {

}
