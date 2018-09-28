package com.hotelpal.service.web;

import com.hotelpal.service.common.mo.HttpParams;
import com.hotelpal.service.common.utils.HttpGetUtils;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;

public class Test {
	public static void main(String[] args) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(Test.class.getResourceAsStream("/poster.png"));
			BufferedImage courseBufferedImg = ImageIO.read(iis);
			int width = courseBufferedImg.getWidth();

			//将用户信息写到海报上
			String userHeadImg = "http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eqjX87Nx8tL7Bn3w06hucg64PV7ZYrzwaX2BjDFK6q73BiapnOwZDprZc1EPK6ib8eM6QyjY3TX5QOA/0";
			String userTitle = "Redx";
			HttpParams params = new HttpParams();
			params.setUrl(userHeadImg);
			InputStream headIS = HttpGetUtils.executeGetStream(params);
			// 可能获取不到
			double rate = width / 750D;
			Graphics baseGraph = courseBufferedImg.getGraphics();
			if (headIS != null) {
				BufferedImage bufferedHead = ImageIO.read(ImageIO.createImageInputStream(headIS));
				/*
				 * 用户信息块 (宽度为750px时)距离上边30，左边60, 72*72, radius=72/2
				 * 需要按比例缩放
				 */
				//生成一个与bufferedHead一样大小的图片，准备剪裁
				BufferedImage copy = new BufferedImage(bufferedHead.getWidth(), bufferedHead.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D copyGraph = copy.createGraphics();
				copyGraph.setClip(new Ellipse2D.Double(0, 0, copy.getWidth(), copy.getHeight()));
				copyGraph.drawImage(bufferedHead, 0, 0, null);
				copyGraph.dispose();


				int posWidth = (int)(30 * rate);
				int posHeight = (int)(30 * rate);
				baseGraph.drawImage(copy, posWidth, posHeight, 72, 72, null);
			}


			int nickMarginLeft = (int)((30 + 72 + 20) * rate);
			int nickMarginTop = (int)((30 + 24) * rate);
			baseGraph.setColor(new Color(0x999999));
			baseGraph.setFont(new Font("微软雅黑", Font.PLAIN, 24));
			baseGraph.drawString(userTitle, nickMarginLeft, nickMarginTop);

			int constantMarginTop = (int)((30 + 24 + 30 + 6) * rate);
			baseGraph.setColor(new Color(0x666666));
			baseGraph.setFont(new Font("微软雅黑", Font.PLAIN, 30));
			baseGraph.drawString("送你一堂免费课", nickMarginLeft, constantMarginTop);

			System.out.println(Test.class.getResource("/"));
			File out =
			new File(URI.create(Test.class.getResource("/").toString() + "result.png"));
			System.out.println(out.exists());
			System.out.println(out.getPath());
			System.out.println(out.toPath());
			System.out.println(out.getAbsolutePath());
			System.out.println(out.toURI());
			ImageIO.write(courseBufferedImg, "png", out);


//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			ImageIO.write(courseBufferedImg, "png", baos);
//			byte[] imageInByte = baos.toByteArray();
//			return new String(imageInByte, "ISO-8859-1");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
