package com.hotelpal.service.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.hotelpal.service.common.exception.ServiceException;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImgUtils {
	public static final String DEFAULT_FORMAT = "PNG";
	public static final Integer DEFAULT_HEIGHT = 106;
	public static final Integer DEFAULT_WIDTH = 106;
	
	public static BufferedImage generateQRCode(String content) {
		try {
			QRCodeWriter writer = new QRCodeWriter();
			Map<EncodeHintType, Object> hints = new HashMap<>();
			hints.put(EncodeHintType.MARGIN, 0);
			BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, DEFAULT_WIDTH, DEFAULT_HEIGHT, hints);
			return MatrixToImageWriter.toBufferedImage(matrix);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}
