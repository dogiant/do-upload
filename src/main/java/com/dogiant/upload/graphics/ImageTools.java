package com.dogiant.upload.graphics;

import java.io.IOException;
import java.util.ArrayList;

import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;

public class ImageTools {

	/**
	 * ImageMagick的路径
	 */
	public static String imageMagickPath = null;
	public static boolean setSearchPath = false;

	static {
		/**
		 * 获取ImageMagick的路径
		 */
		java.util.ResourceBundle rs = java.util.ResourceBundle
				.getBundle("image");
		if ("windows".equals(rs.getString("OS"))) {
			setSearchPath = true;
			// linux下不要设置此值，不然会报错
			imageMagickPath = rs.getString("GraphicsMagickPath");
		}
	}

	/**
	 * 根据坐标裁剪图片
	 * 
	 * @param srcPath
	 *            要裁剪图片的路径
	 * @param newPath
	 *            裁剪图片后的路径
	 * @param x
	 *            起始横坐标
	 * @param y
	 *            起始纵坐标
	 * @param x1
	 *            结束横坐标
	 * @param y1
	 *            结束纵坐标
	 */
	public static void cutImage(String srcPath, String newPath, int x, int y, int x1, int y1) throws Exception {

		int width = x1 - x;
		int height = y1 - y;
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		/** width：裁剪的宽度 * height：裁剪的高度 * x：裁剪的横坐标 * y：裁剪纵坐标 */
		op.crop(width, height, x, y);
		op.addImage(newPath);
		ConvertCmd convert = new ConvertCmd(true);
		if (setSearchPath) {
			convert.setSearchPath(imageMagickPath);
		}
		convert.run(op);
	}

	/**
	 * 根据尺寸缩放图片
	 * 
	 * @param width
	 *            缩放后的图片宽度
	 * @param height
	 *            缩放后的图片高度
	 * @param addWhite
	 *            是否补白
	 * @param srcPath
	 *            源图片路径
	 * @param newPath
	 *            缩放后图片的路径
	 */
	public static void zoomImage(Integer width, Integer height,
			boolean addWhite, String srcPath, String newPath) throws Exception {
		IMOperation op = new IMOperation();
		op.addImage(srcPath);
		if (width == null) {// 根据高度缩放图片
			op.resize(null, height);
		} else if (height == null) {// 根据宽度缩放图片
			op.resize(width, null);
		} else {
			op.resize(width, height);
		}
		if (addWhite) {
			op.background("white");
			op.gravity("center");
			op.extent(width, height);
		}
		op.addImage(newPath);
		ConvertCmd convert = new ConvertCmd(true);
		if (setSearchPath) {
			convert.setSearchPath(imageMagickPath);
		}
		convert.run(op);
	}

	/**
	 * 给图片加水印
	 * 
	 * @param srcPath
	 *            源图片路径
	 */
	public static void addImgText(String srcPath, String content)
			throws Exception {
		IMOperation op = new IMOperation();
		op.font("微软雅黑");
		op.gravity("southeast");
		op.pointsize(18).fill("#BCBFC8").draw("text 0,0 " + content);
		// ("x1 x2 x3 x4") x1 格式，x2 x轴距离 x3 y轴距离 x4名称
		op.addImage();
		op.addImage();
		ConvertCmd convert = new ConvertCmd(true);
		if (setSearchPath) {
			convert.setSearchPath(imageMagickPath);
		}
		try {
			convert.run(op, srcPath, srcPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片水印
	 * 
	 * @param srcImagePath
	 *            源图片
	 * @param waterImagePath
	 *            水印
	 * @param destImagePath
	 *            生成图片
	 * @param gravity
	 *            图片位置
	 * @param dissolve
	 *            水印透明度
	 */
	public static void waterMark(String waterImagePath, String srcImagePath,
			String destImagePath, String gravity, int dissolve) {
		IMOperation op = new IMOperation();
		op.gravity(gravity);
		op.dissolve(dissolve);
		op.addImage(waterImagePath);
		op.addImage(srcImagePath);
		op.addImage(destImagePath);
		CompositeCmd cmd = new CompositeCmd(true);
		if (setSearchPath) {
			cmd.setSearchPath(imageMagickPath);
		}
		try {
			cmd.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片旋转
	 * 
	 * @param srcImagePath
	 * @param destImagePath
	 * @param angle
	 */
	public static void rotate(String srcImagePath, String destImagePath,
			double angle) {
		try {
			IMOperation op = new IMOperation();
			op.rotate(angle);
			op.addImage(srcImagePath);
			op.addImage(destImagePath);
			ConvertCmd cmd = new ConvertCmd(true);
			if (setSearchPath) {
				cmd.setSearchPath(imageMagickPath);
			}
			cmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片信息
	 * 
	 * @param imagePath
	 * @return
	 */
	public static String showImageInfo(String imagePath) {
		String line = null;
		try {
			IMOperation op = new IMOperation();
			op.format("width:%w,height:%h,path:%d%f,size:%b%[EXIF:DateTimeOriginal]");
			op.addImage(1);
			IdentifyCmd identifyCmd = new IdentifyCmd(true);
			if (setSearchPath) {
				identifyCmd.setSearchPath(imageMagickPath);
			}
			ArrayListOutputConsumer output = new ArrayListOutputConsumer();
			identifyCmd.setOutputConsumer(output);
			identifyCmd.run(op, imagePath);
			ArrayList<String> cmdOutput = output.getOutput();
			assert cmdOutput.size() == 1;
			line = cmdOutput.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * 图片宽高信息
	 * 
	 * @param imagePath
	 * @return
	 */
	public static String getImageSize(String imagePath) {
		String line = null;
		try {
			IMOperation op = new IMOperation();
			op.format("{width:%w,height:%h}");
			op.addImage(1);
			IdentifyCmd identifyCmd = new IdentifyCmd(true);
			if (setSearchPath) {
				identifyCmd.setSearchPath(imageMagickPath);
			}
			ArrayListOutputConsumer output = new ArrayListOutputConsumer();
			identifyCmd.setOutputConsumer(output);
			identifyCmd.run(op, imagePath);
			ArrayList<String> cmdOutput = output.getOutput();
			assert cmdOutput.size() == 1;
			line = cmdOutput.get(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	
	/**
	 * 图片合成
	 * 
	 * @param args
	 * @param maxWidth
	 * @param maxHeight
	 * @param newpath
	 * @param mrg
	 * @param type
	 *            1:横,2:竖
	 */
	public static void montage(String[] args, Integer maxWidth,
			Integer maxHeight, String newpath, Integer mrg, String type) {
		IMOperation op = new IMOperation();
		ConvertCmd cmd = new ConvertCmd(true);
		if (setSearchPath) {
			cmd.setSearchPath(imageMagickPath);
		}
		String thumb_size = maxWidth + "x" + maxHeight + "^";
		String extent = maxWidth + "x" + maxHeight;
		if ("1".equals(type)) {
			op.addRawArgs("+append");
		} else if ("2".equals(type)) {
			op.addRawArgs("-append");
		}

		op.addRawArgs("-thumbnail", thumb_size);
		op.addRawArgs("-gravity", "center");
		op.addRawArgs("-extent", extent);

		Integer border_w = maxWidth / 40;
		op.addRawArgs("-border", border_w + "x" + border_w);
		op.addRawArgs("-bordercolor", "#ccc");

		op.addRawArgs("-border", 1 + "x" + 1);
		op.addRawArgs("-bordercolor", "#fff");

		for (String img : args) {
			op.addImage(img);
		}
		if ("1".equals(type)) {
			Integer whole_width = ((mrg / 2) + 1 + border_w + maxWidth
					+ border_w + (mrg / 2) + 1)
					* args.length - mrg;
			Integer whole_height = maxHeight + border_w + 1;
			op.addRawArgs("-extent", whole_width + "x" + whole_height);
		} else if ("2".equals(type)) {
			Integer whole_width = maxWidth + border_w + 1;
			Integer whole_height = ((mrg / 2) + 1 + border_w + maxHeight
					+ border_w + (mrg / 2) + 1)
					* args.length - mrg;
			op.addRawArgs("-extent", whole_width + "x" + whole_height);
		}
		op.addImage(newpath);
		try {
			cmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 边框处理
	 * @param srcImagePath
	 * @param newImagePath
	 * @param borderWidth
	 */
	public static void borderProcessing(String srcImagePath,String newImagePath, Integer borderWidth) {
		IMOperation op = new IMOperation();
		ConvertCmd cmd = new ConvertCmd(true);
		if (setSearchPath) {
			cmd.setSearchPath(imageMagickPath);
		}
		op.addRawArgs("-border", borderWidth + "x" + borderWidth);
		op.addRawArgs("-bordercolor", "#ccc");

		op.addRawArgs("-border", 1 + "x" + 1);
		op.addRawArgs("-bordercolor", "#fff");

		op.addImage(srcImagePath);
		
		op.addImage(newImagePath);
		try {
			cmd.run(op);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片叠加合成
	 * 
	 * @param srcImagePath
	 *            源图片
	 * @param layerImagePath
	 *            图层
	 * @param destImagePath
	 *            生成图片
	 * @param gravity
	 *            图片位置
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param leftOrRight
	 *            左右边距
	 * @param topOrBottom
	 *            上下边距
	 * @param dissolve
	 *            透明度 100不透明
	 */
	public static void superimposing(String layerImagePath,
			String srcImagePath, String destImagePath, String gravity,
			Integer width, Integer height, int leftOrRight, int topOrBottom,
			int dissolve) {
		IMOperation op = new IMOperation();
		op.gravity(gravity);
		op.geometry(width, height, leftOrRight, topOrBottom);
		op.dissolve(dissolve);
		op.addImage(layerImagePath);
		op.addImage(srcImagePath);
		op.addImage(destImagePath);
		CompositeCmd cmd = new CompositeCmd(true);
		if (setSearchPath) {
			cmd.setSearchPath(imageMagickPath);
		}
		try {
			cmd.run(op);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		// addImgText("e://a2.jpg");
		// zoomImage(300, 150, "e://a.jpg", "e://a1.jpg");
		// zoomImage(300, 150, "e://b.jpg", "e://b1.jpg");
		// zoomImage(300, 150, "e://c.jpg", "e://c1.jpg");
		// zoomImage(300, 150, "e://d.jpg", "e://d1.jpg");
		// zoomImage(300, 150, "e://e.jpg", "e://e1.jpg");
		// zoomImage(100, 100, false, "C:\\graphics_test\\avatar-test.jpg",
		// "C:\\graphics_test\\avatar.jpg");
		// waterMark("C:\\graphics_test\\watermark1.png",
		// "C:\\graphics_test\\dogiant1.jpg",
		// "C:\\graphics_test\\dogiantwm.jpg", "southeast", 40);
		// rotate("e://aa.jpg", "e://ee.jpg", 90);
//		JSONObject jo = JSON.parseObject(getImageSize("e://aa.jpg"));
//		System.out.println(jo.get("width"));
		//System.out.println(getImageSize("e://aa.jpg"));
		// String[] files = new String[5];
		// files[0] = "e://a1.jpg";
		// files[1] = "e://b1.jpg";
		// files[2] = "e://c1.jpg";
		// files[3] = "e://d1.jpg";
		// files[4] = "e://e1.jpg";
		// montage(files, 280, 200, "e://liboy1.jpg", 0,"2");
		// cropImage("e://a.jpg", "e://liboy22.jpg", 1024, 727, 500, 350);
		// cutImage("e://a.jpg", "e://liboy222.jpg", 5, 10, 100, 120);

		//borderProcessing("C:\\graphics_test\\layer1.jpg","C:\\graphics_test\\layer1_border.jpg",10);
		
		
//		 superimposing("C:\\graphics_test\\layer1.jpg",
//		 "C:\\graphics_test\\bgimage.jpg", "C:\\graphics_test\\image1.jpg",
//		 "southeast", 30, 500, 10, 30, 100);
	}

}
