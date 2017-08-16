package com.dogiant.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ImageConfig {

	public static String DOMAIN;
	
	public static String FILE_HOST;

	public static String IMAGE_URL_PREFIX;

	public static String IMAGE_LOCAL_PATH_PREFIX;
	
	//默认通道
	public static String CHANNEL = "common";
	
	//头像大小格式
	public static String AVATAR_FORMATS = "180,180,_x;73,73,_m;33,33,_s;";
	
	//默认补白
	public static boolean PADDING_WHITE = false;
	
	@Value("${image.url.domain}")
	public void setDomain(String domain) {
		ImageConfig.DOMAIN = domain;
	}

	@Value("${image.url.file_host}")
	public void setFileHost(String fileHost) {
		ImageConfig.FILE_HOST = fileHost;
	}

	@Value("${image.url.prefix}")
	public void setImageUrlPrefix(String imageUrlPrefix) {
		ImageConfig.IMAGE_URL_PREFIX = imageUrlPrefix;
	}

	@Value("${image.local_path.prefix}")
	public void setImageLocalPathPrefix(String imageLocalPathPrefix) {
		ImageConfig.IMAGE_LOCAL_PATH_PREFIX = imageLocalPathPrefix;
	}

	/**
	 * 文件上传配置
	 * 
	 * @return
	 */
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		// 单个文件最大
//		factory.setMaxFileSize("10240KB"); // KB,MB
//		// 设置总上传数据总大小
//		factory.setMaxRequestSize("102400KB");
//		return factory.createMultipartConfig();
//	}
}
