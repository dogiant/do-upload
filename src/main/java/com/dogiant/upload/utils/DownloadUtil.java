package com.dogiant.upload.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dogiant.upload.graphics.ImageTools;

public class DownloadUtil {

	public static String downloadFromUrl(String url, String dir) {

		// URL resourceUrl = new URL(urlStr);
		// InputStream content = (InputStream) resourceUrl.getContent();

		try {
			URL httpurl = new URL(url);
			String fileName = getFileNameFromUrl(url);
			File f = new File(dir + fileName);
			FileUtils.copyURLToFile(httpurl, f);
			return f.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFileNameFromUrl(String url) {
		String name = System.currentTimeMillis() + "_X";
		int index = url.lastIndexOf("/");
		if (index > 0) {
			name = url.substring(index + 1);
			if (name.trim().length() > 0) {
				return name;
			}
		}
		return name;
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				System.out.println("line " + line + ": " + tempString);

				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void main(String[] args) {
		// String b = downloadFromUrl(
		// "http://images.17173.com/2010/www/roll/201003/0301sohu01.jpg",
		// "d:/");
		// System.out.println(b);

		String fileName = "C:\\backgroudImage.txt";

		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			int i = 0;
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				// System.out.println("line " + line + ": " + tempString);

				String s = downloadFromUrl(tempString, "c:\\themePic\\");

				// 获取图片宽高
				String sizeJson = ImageTools.getImageSize(s);
				JSONObject jsonObject = null;
				try {
					jsonObject = JSON.parseObject(sizeJson);
					int width = jsonObject.getIntValue("width");
					int height = jsonObject.getIntValue("height");

					if (width > height) {
						i++;
						System.out.println("width:" + width);
						System.out.println("height:" + height);
						System.out.println("line " + line + ": " + tempString);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				line++;
			}
			System.out.println(i);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}
}
