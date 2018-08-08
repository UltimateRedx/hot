package com.hotelpal.service.common.enums;

public class EnumHelper {
	
	public static boolean isIn(String str, Enum[] es) {
		for(Enum e : es) {
			if(e.toString().equalsIgnoreCase(str)){
				return true;
			}
		}
		return false;
	}
}
