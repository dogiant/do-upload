package com.dogiant.upload.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class ImageConfig {

	// 站点域名
	public static String DOMAIN;

	// 文件服务器主机
	public static String FILE_HOST;

	// 图片URL地址前缀
	public static String IMAGE_URL_PREFIX;

	// 图片本机路径前缀
	public static String IMAGE_LOCAL_PATH_PREFIX;

	// 默认通道
	public static String CHANNEL = "common";

	// 头像大小格式
	public static String AVATAR_FORMATS = "180,180,_x;73,73,_m;33,33,_s;";

	// 水印路径
	public static String WATERMARK_LOCAL_PATH;

	// 默认补白
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

	@Value("${image.watermark.local_path}")
	public void setWartermarkLocalPath(String watermarkLocalPath) {
		ImageConfig.WATERMARK_LOCAL_PATH = watermarkLocalPath;
	}

}
