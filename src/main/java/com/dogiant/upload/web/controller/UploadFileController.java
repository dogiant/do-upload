package com.dogiant.upload.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dogiant.upload.config.ImageConfig;
import com.dogiant.upload.domain.UploadFile;
import com.dogiant.upload.graphics.ImageTools;
import com.dogiant.upload.service.UploadFileService;
import com.dogiant.upload.utils.DownloadUtil;
import com.dogiant.upload.utils.FileUtil;

@Controller
public class UploadFileController implements HandlerExceptionResolver {

	protected final Log LOG = LogFactory.getLog(getClass());
	
	@Autowired
	private UploadFileService uploadFileService;

	@RequestMapping(value = "/input", method = RequestMethod.GET)
	public String input(Map<String, Object> model) {
		LOG.info("/input");
		model.put("fileHost", ImageConfig.FILE_HOST);
		return "upload";
	}

	@ResponseBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String upload(@RequestParam MultipartFile[] uploads, HttpServletRequest request,
			HttpServletResponse response) {

		String callback = request.getParameter("callback");

		String channel = request.getParameter("channel");
		if (channel == null || channel.length() == 0) {
			channel = "default";
		}

		// 返回地址 ?fileName=/201405/16/hp/hp_14002197707471.jpg
		String returnUrl = request.getParameter("returnUrl");
		LOG.info(returnUrl);

		String size = request.getParameter("size");
		LOG.info("图片宽高值:" + size);

		Date date = new Date();
		StringBuffer url = new StringBuffer();
		url.append("/").append(new SimpleDateFormat("yyyyMM").format(date)).append("/")
				.append(new SimpleDateFormat("dd").format(date)).append("/").append(channel);

		// 图片存储路径
		String uploadpath = ImageConfig.IMAGE_LOCAL_PATH_PREFIX + url.toString();

		FileUtil.mkdir(uploadpath);

		Map<String, Object> obj = new HashMap<String, Object>();

		// 上传的文件列表
		List<UploadFile> list = new ArrayList<UploadFile>();

		// <input type="file" name="uploads"/>
		for (MultipartFile upload : uploads) {
			if (upload.isEmpty()) {
				LOG.info("文件未上传");
				continue;
			} else {
				LOG.info("文件长度: " + upload.getSize());
				LOG.info("文件类型: " + upload.getContentType());
				LOG.info("文件字段名称: " + upload.getName());
				LOG.info("文件名: " + upload.getOriginalFilename());
				LOG.info("========================================");

				// 判断size是否超出限制 1048576
				if (upload.getSize() > 1048576) {
					LOG.info("文件长度1M超限>1048576: " + upload.getSize());
					if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf(ImageConfig.FILE_HOST) != -1) {
						String msg = "上传文件大小超过1M限制";
						try {
							response.sendRedirect(returnUrl + "?code=403&msg=" + URLEncoder.encode(msg, "utf8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					} else {
						obj.put("success", false);
						obj.put("code", 403);
						obj.put("msg", "上传文件大小超过限制");
						JSONObject o = new JSONObject(obj);
						String jsonString = o.toJSONString();
						if (callback == null || callback.length() == 0) {
							return jsonString;
						}
						return "try{" + callback + "(" + jsonString + ");}catch(e){}";
					}
				}

				Date createtime = new Date();
				String contentType = upload.getContentType();
				String uploadFileName = upload.getOriginalFilename();

				LOG.info("uploadFile" + ":" + uploadFileName + ":" + contentType);

				String format = uploadFileName.substring(uploadFileName.lastIndexOf('.') + 1);

				// 图片名
				String name = channel + "_" + createtime.getTime() + "_" + upload.getSize() + "." + format;

				try {
					FileUtils.copyInputStreamToFile(upload.getInputStream(), new File(uploadpath, name));
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// 相对路径
				String pathUrl = url.toString() + "/" + name;

				int width = 0;
				int height = 0;
				// 判断图片宽高大小是否符合要求
				try {
					String sizeJson = ImageTools.getImageSize(ImageConfig.IMAGE_LOCAL_PATH_PREFIX + pathUrl);
					JSONObject jsonObject = JSON.parseObject(sizeJson);
					width = jsonObject.getIntValue("width");
					height = jsonObject.getIntValue("height");
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (width != 0 && height != 0) {
					String msg = "";
					if ("console_theme_type".equals(channel)) {
						if (StringUtils.isNotEmpty(size)) {
							try {
								String[] s = size.split("\\*");
								if (s != null && s.length == 2
										&& (width != Integer.valueOf(s[0]) || height != Integer.valueOf(s[1]))) {
									msg = "上传图片宽高必须为" + s[0] + "x" + s[1];
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if (StringUtils.isNotBlank(msg)) {
						if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf(".jd") != -1) {
							try {
								response.sendRedirect(returnUrl + "?code=402&msg=" + URLEncoder.encode(msg, "utf8"));
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return null;
						} else {
							obj.put("success", false);
							obj.put("code", 402);
							obj.put("msg", "上传图片比例不正确," + msg);
							JSONObject o = new JSONObject(obj);
							String jsonString = o.toJSONString();
							if (callback == null || callback.length() == 0) {
								return jsonString;
							}
							return "try{" + callback + "(" + jsonString + ");}catch(e){}";
						}
					}
				}

				UploadFile file = new UploadFile();
				file.setChannel(channel);
				file.setCreator(channel);
				file.setUserId("");
				file.setNickname("");

				file.setCtime(createtime);
				file.setMtime(createtime);
				file.setContentType(contentType);
				file.setExtension(format);
				file.setName(name);
				file.setUrl(pathUrl);

				file.setAudit(false);
				file.setStatus(1);
				list.add(file);

			}
		}

		JSONArray imgUrls = new JSONArray();
		// uploadFileDao 插入数据表UploadFile
		if (CollectionUtils.isNotEmpty(list)) {
			for (UploadFile uploadFile : list) {
				LOG.info("uploadFile path: " + ImageConfig.IMAGE_LOCAL_PATH_PREFIX + uploadFile.getUrl());
				File file = null;
				try {
					file = new File(ImageConfig.IMAGE_LOCAL_PATH_PREFIX + uploadFile.getUrl());
					LOG.info("file.exists()==" + file.exists());
				} catch (Exception e) {
					e.printStackTrace();
				}

				// 此处为本地地址返回

				uploadFileService.save(uploadFile);

				imgUrls.add(uploadFile.getUrl());
			}

		}

		if (StringUtils.isNotBlank(returnUrl) && returnUrl.indexOf(".jd") != -1) {
			try {
				response.sendRedirect(returnUrl + "?imgUrls=" + imgUrls.toJSONString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (imgUrls.size() > 0) {
			obj.put("success", true);
			obj.put("result", imgUrls);
		} else {
			obj.put("success", false);
			obj.put("code", -1);
			obj.put("msg", "上传文件失败");
		}

		JSONObject o = new JSONObject(obj);
		String jsonString = o.toJSONString();

		if (callback == null || callback.length() == 0) {
			return jsonString;
		}
		return "try{" + callback + "(" + jsonString + ");}catch(e){}";

	}

	@ResponseBody
	@RequestMapping(value = "/crop", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String crop(HttpServletRequest request) {

		String callback = request.getParameter("callback");
		
		String channel = request.getParameter("channel");
		if (channel == null || channel.length() == 0) {
			channel = "default";
		}

		// 选择图像区域二次剪裁
		Boolean imgAreaSelect = false;

		String imgAreaSelectStr = request.getParameter("imgAreaSelect");
		if (imgAreaSelectStr != null && "true".equalsIgnoreCase(imgAreaSelectStr)) {
			imgAreaSelect = true;
		}
		String imgPath = null;
		Integer x1 = null;
		Integer y1 = null;
		Integer x2 = null;
		Integer y2 = null;

		Date date = new Date();
		List<File> fileList = new ArrayList<File>();
		// 二次剪裁
		if (imgAreaSelect) {
			imgPath = request.getParameter("imgPath");
			x1 = Integer.valueOf(request.getParameter("x1"));
			y1 = Integer.valueOf(request.getParameter("y1"));
			x2 = Integer.valueOf(request.getParameter("x2"));
			y2 = Integer.valueOf(request.getParameter("y2"));

			// 区域选择裁剪
			// savePath = savePath.substring(0, savePath.lastIndexOf("."))
			if (StringUtils.isNotEmpty(imgPath)) {
				if (imgPath.indexOf(".") != -1) {

					StringBuffer path = new StringBuffer();
					path.append("/").append(new SimpleDateFormat("yyyyMM").format(date)).append("/")
							.append(new SimpleDateFormat("dd").format(date)).append("/").append("crop").append("/");

					String uploadpath = ImageConfig.IMAGE_LOCAL_PATH_PREFIX + path.toString();
					FileUtil.mkdir(uploadpath);

					String filePath = ImageConfig.IMAGE_LOCAL_PATH_PREFIX + imgPath;
					if (imgPath.indexOf("http") != -1) {
						filePath = DownloadUtil.downloadFromUrl(imgPath,
								ImageConfig.IMAGE_LOCAL_PATH_PREFIX + path.toString());
					}

					LOG.info("filePath=========" + filePath);

					if (filePath != null) {
						String newName = filePath.substring(0, filePath.lastIndexOf(".")) + "_crop"
								+ filePath.substring(filePath.lastIndexOf("."));
						try {
							ImageTools.cutImage(filePath, newName, x1, y1, x2, y2);
							File file = null;
							try {
								file = new File(newName);
								LOG.info("file.exists()==" + file.exists());
							} catch (Exception e) {
								e.printStackTrace();
							}
							fileList.add(file);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						 if ("avatar".equals(channel)) {
							 zoomAvartar(ImageConfig.IMAGE_LOCAL_PATH_PREFIX + newName, false); }
						 
					}

				}
			}
		}

		// 此处为本地地址返回
		String desFileName = null;

		JSONArray imgUrls = new JSONArray();

		imgUrls.add(desFileName);

		Map<String, Object> obj = new HashMap<String, Object>();

		if (imgUrls.size() > 0) {
			obj.put("success", true);
			obj.put("result", imgUrls);
		} else {
			obj.put("success", false);
		}

		JSONObject o = new JSONObject(obj);
		String jsonString = o.toJSONString();

		if (callback == null || callback.length() == 0) {
			return jsonString;
		}
		return "try{" + callback + "(" + jsonString + ");}catch(e){}";
	}

	/**
	 * 根据源图路径生成头像各尺寸缩略图
	 * 
	 * @param srcPath
	 * @param addWhite
	 */
	private void zoomAvartar(String srcPath, boolean addWhite) {
		String formats = ImageConfig.AVATAR_FORMATS;
		for (String format : formats.split(";")) {
			String[] adapters = format.split(",");
			Integer width = null;
			Integer height = null;
			try {
				width = Integer.parseInt(adapters[0]);
				height = Integer.parseInt(adapters[1]);
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			String newPath = getNewFileName(srcPath, adapters[2]);
			try {
				ImageTools.zoomImage(width, height, addWhite, srcPath, newPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 图像重命名
	 * 
	 * @param fileName
	 * @param token
	 * @return
	 */
	public String getNewFileName(String fileName, String token) {
		String pendTarget = fileName.substring(0, fileName.lastIndexOf(".")) + token + "."
				+ fileName.substring(fileName.lastIndexOf(".") + 1);
		return pendTarget;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) {
		
		Map<String, Object> model = new HashMap<String, Object>();

		//此处发现tomcat的异常会事先抛出
		if(exception instanceof MultipartException){
			LOG.info("FileSizeLimitExceededException ... ");
			model.put("errors", ((MultipartException) exception).getMessage());
		}else if (exception instanceof MaxUploadSizeExceededException) {
			LOG.info("MaxUploadSizeExceededException ... ");
			model.put("errors", "File size should be less then "
					+ ((MaxUploadSizeExceededException) exception).getMaxUploadSize() + " byte.");
		}else {
			model.put("errors", "Unexpected error: " + exception.getMessage());
		}
		
		String type = request.getParameter("returnType");
		if (!"json".equals(type)) {
			return new ModelAndView("upload", (Map<String, Object>) model);
		} else {
			ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
			mav.addObject("success", false);
			mav.addObject((Map<String, Object>) model);
			return mav;
		}

	}

}
