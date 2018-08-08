package com.hotelpal.service.common.utils;

import java.text.DecimalFormat;
import java.util.UUID;
import java.util.stream.IntStream;

public class RandomUtils {
	
	public static String getRandomDigitalString(Integer n, Integer bit) {
		StringBuilder sb = new StringBuilder();
		IntStream.range(0, bit).forEach(e -> sb.append("0"));
		return new DecimalFormat(sb.toString()).format(org.apache.commons.lang3.RandomUtils.nextInt() % n);
	}
	public static String createUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
