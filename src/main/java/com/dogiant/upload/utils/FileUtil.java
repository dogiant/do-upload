package com.dogiant.upload.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import sun.misc.BASE64Decoder;

/**
 * 文件工具类
 *
 */
@SuppressWarnings("restriction")
public class FileUtil {

	/**
	 * 创建目录
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean mkdir(String dir) {
		File file = new File(dir);
		if (file.exists() && file.isDirectory())
			return true;
		return file.mkdirs();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean exists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 移动文件
	 * 
	 * @param srcPath
	 * @param destPath
	 */
	public static void mv(String srcPath, String destPath) {
		File src = new File(srcPath);
		File dest = new File(destPath);
		if (!src.exists())
			return;

		@SuppressWarnings("unused")
		boolean b = src.renameTo(dest);
	}

	/**
	 * 获得文件对象
	 * 
	 * @param path
	 * @return
	 */
	public static File getFile(String path) {
		return new File(path);
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean delete(String filePath) {
		File file = new File(filePath);
		if (file.exists() && file.isFile())
			return file.delete();
		else
			return true;
	}

	/**
	 * copy 文件基本操作
	 * 
	 * @param fins
	 * @param destine
	 */
	private static boolean cp(InputStream fins, File destine) {
		if (fins == null)
			return false;
		try {
			destine.getParentFile().mkdirs();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(destine);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		byte[] buf = new byte[1024];
		int readLen;
		try {
			while ((readLen = fins.read(buf, 0, buf.length)) > 0) {
				fos.write(buf, 0, readLen);
			}
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fos.close();
				fins.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * copy 文件
	 * 
	 * @param src
	 * @param destine
	 */
	public static boolean cp(File src, File destine) {
		try {
			if (!src.exists()
					|| src.getCanonicalPath()
							.equals(destine.getCanonicalPath()))
				return false;
			FileInputStream fins = new FileInputStream(src);
			boolean b = cp(fins, destine);
			if (b) {
				@SuppressWarnings("unused")
				boolean op = destine.setLastModified(src.lastModified());
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 将srcPath文件copy到desPath上
	 * 
	 * @param srcPath
	 * @param desPath
	 */
	public static boolean cp(String srcPath, String desPath) {
		File src = new File(srcPath);
		File destine = new File(desPath);
		return cp(src, destine);
	}

	/**
	 * base64字串转成图片
	 * 
	 * @param imgStr
	 *            base64字串
	 * @param imgFilePath
	 *            图片保存路径
	 * @return
	 */
	public static String[] generateBase64Image(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return new String[] { "500", null };
		String param = "jpg";
		if (!imgStr.startsWith("data:image")) {
			return new String[] { "500", null };
		}
		if ("gif".equalsIgnoreCase(imgStr.substring(11, imgStr.indexOf(";")))) {
			param = "gif";
			imgFilePath = imgFilePath
					.substring(0, imgFilePath.lastIndexOf(".")) + ".gif";
		}
		imgStr = imgStr.substring(imgStr.indexOf(",") + 1);
		BASE64Decoder decoder = new BASE64Decoder();
		// try {
		// Base64解码
		byte[] bytes = null;
		try {
			bytes = decoder.decodeBuffer(imgStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bytes != null) {
			for (int i = 0; i < bytes.length; ++i) {
				if (bytes[i] < 0) {// 调整异常数据
					bytes[i] += 256;
				}
			}
		}else{
			return new String[] { "500", null };
		}

		// 生成jpeg图片
		OutputStream out = null;
		try {
			out = new FileOutputStream(imgFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[] { "500", null };
		}
		
		try {
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return new String[] { "500", null };
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return new String[] { "200", param };
	}

	/**
	 * 获取远程图片
	 * 
	 * @param fileUrl
	 *            远程图片地址
	 * @param savePath
	 *            图片保存路径
	 * @return 第一个返回状态码，第二个返回格式
	 */
	public static String[] generateRemoteImage(String fileUrl, String savePath) {
		try {
			String param = "jpg";
			URL url = new URL(fileUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			int code = connection.getResponseCode();
			System.out.println("远程图片 fileUrl = " + fileUrl + ";code = " + code);
			System.out.println("远程图片 contentType = "
					+ connection.getContentType() + ";contentencoding = "
					+ connection.getContentEncoding());
			if (code != 200) {// 没有取到图片
				return new String[] { code + "", null };
			}
			if (!connection.getContentType().startsWith("image")) {// 不是图片文件
				return new String[] { "", null };
			}
			if (connection.getContentType().endsWith("gif")) {// 是gif图片
				savePath = savePath.substring(0, savePath.lastIndexOf("."))
						+ ".gif";
				param = "gif";
			}
			InputStream in = null;
			if ("gzip".equalsIgnoreCase(connection.getContentEncoding())) {
				in = new GZIPInputStream(connection.getInputStream());
			} else {
				in = new DataInputStream(connection.getInputStream());
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					savePath));
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			connection.disconnect();
			return new String[] { "200", param };
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e + fileUrl + savePath);
			return new String[] { "500", null };
		}
	}

	/**
	 * 在本文件夹下查找
	 * 
	 * @param s
	 *            String 文件名
	 * @return File[] 找到的文件
	 */
	public static File[] getFiles(String s) {
		return getFiles("./", s);
	}

	/**
	 * 获取文件 可以根据正则表达式查找
	 * 
	 * @param dir
	 *            String 文件夹名称
	 * @param s
	 *            String 查找文件名，可带*.?进行模糊查询
	 * @return File[] 找到的文件
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static File[] getFiles(String dir, String s) {
		// 开始的文件夹
		File file = new File(dir);

		s = s.replace('.', '#');
		s = s.replaceAll("#", "////.");
		s = s.replace('*', '#');
		s = s.replaceAll("#", ".*");
		s = s.replace('?', '#');
		s = s.replaceAll("#", ".?");
		s = "^" + s + "$";

		System.out.println(s);
		Pattern p = Pattern.compile(s);
		ArrayList list = filePattern(file, p);

		File[] rtn = new File[list.size()];
		list.toArray(rtn);
		return rtn;
	}

	/**
	 * @param file
	 *            File 起始文件夹
	 * @param p
	 *            Pattern 匹配类型
	 * @return ArrayList 其文件夹下的文件夹
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static ArrayList filePattern(File file, Pattern p) {
		if (file == null) {
			return null;
		} else if (file.isFile()) {
			Matcher fMatcher = p.matcher(file.getName());
			if (fMatcher.matches()) {
				ArrayList list = new ArrayList();
				list.add(file);
				return list;
			}
		} else if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				ArrayList list = new ArrayList();
				for (int i = 0; i < files.length; i++) {
					ArrayList rlist = filePattern(files[i], p);
					if (rlist != null) {
						list.addAll(rlist);
					}
				}
				return list;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		// 字符串匹配例子
		String s = "*txt.*";
		s = s.replace('.', '#');
		s = s.replaceAll("#", "\\\\.");
		s = s.replace('*', '#');
		s = s.replaceAll("#", ".*");
		s = s.replace('?', '#');
		s = s.replaceAll("#", ".?");
		s = "^" + s + "$";

		System.out.println(s);
		Pattern p = Pattern.compile(s);

		ArrayList<String> list = new ArrayList<String>();
		list.add("aabc.txt");
		list.add("sdfsdfaabc.txt.asdasd");
		list.add("aabcd.txt");
		list.add("aabcdtxt.sadf");
		list.add("abc.txt");

		Matcher fMatcher = null;
		String s1 = null;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			s1 = (String) list.get(i);
			fMatcher = p.matcher(s1);
			if (fMatcher.matches()) {
				System.out.println(s1);
			}
		}
	}
}
