package com.hotelpal.service.common.utils.wx;

import com.hotelpal.service.common.exception.ServiceException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Arrays;

public class WXEncodeUtils {
	private static Charset CHARSET = Charset.forName("utf-8");
	public static final String OPEN_SECRET = "884c2eb0974d7615905e95d594cc53c9";
	private static final String KEY = "hotelpal66666666hotelpal66666666hotelpal666";
	private static final byte[]  OPEN_AES_KEY = Base64.decodeBase64(KEY + "=");

	/**
	 * 对密文进行解密.
	 *
	 * @param text 需要解密的密文
	 * @return 解密得到的明文
	 * @throws Exception aes解密失败
	 */
	public static String decrypt(String text) {
		byte[] original;
		try {
			// 设置解密模式为AES的CBC模式
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec key_spec = new SecretKeySpec(OPEN_AES_KEY, "AES");
			IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(OPEN_AES_KEY, 0, 16));
			cipher.init(Cipher.DECRYPT_MODE, key_spec, iv);

			// 使用BASE64对密文进行解码
			byte[] encrypted = Base64.decodeBase64(text);

			// 解密
			original = cipher.doFinal(encrypted);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		String xmlContent;
		try {
			// 去除补位字符
			byte[] bytes = PKCS7Encoder.decode(original);

			// 分离16位随机字符串,网络字节序和AppId
			byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);

			int xmlLength = recoverNetworkBytesOrder(networkOrder);

			xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), CHARSET);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return xmlContent;
	}

	private static int recoverNetworkBytesOrder(byte[] orderBytes) {
		int sourceNumber = 0;
		for (int i = 0; i < 4; i++) {
			sourceNumber <<= 8;
			sourceNumber |= orderBytes[i] & 0xff;
		}
		return sourceNumber;
	}
}
