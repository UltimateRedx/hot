package com.hotelpal.service.common.utils;

import com.hotelpal.service.common.exception.ServiceException;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public static final String DATE_DELIMITER = "-";
	public static final String TIME_DELIMITER = ":";
	public static final DecimalFormat decimalformat_00 = new DecimalFormat("00");

	public static String getDateTimeString(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		return getDateTimeString(cal);
	}
	
	public static String getDateTimeString(Calendar cal) {
		if (cal == null) return null;
		return getDateString(cal) + " " +
				decimalformat_00.format(cal.get(Calendar.HOUR_OF_DAY)) + TIME_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.MINUTE)) + TIME_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.SECOND));
	}

	public static String getMMDDString(Calendar cal) {
		if (cal == null) return null;
		return decimalformat_00.format(cal.get(Calendar.MONTH) + 1) + DATE_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.DATE));
	}
	
	public static String getDateString(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND, 0);
		return getDateString(cal);
	}
	
	public static String getDateString(Calendar cal) {
		if (cal == null) return null;
		return cal.get(Calendar.YEAR) + DATE_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.MONTH) + 1) + DATE_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.DATE));
	}

	public static String getHHMMString(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getHHMMString(cal);
	}
	public static String getHHMMString(Calendar cal) {
		if (cal == null) return null;
		return decimalformat_00.format(cal.get(Calendar.HOUR_OF_DAY)) + TIME_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.MINUTE));
	}

	public static String getTimeString(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return getTimeString(cal);
	}

	public static String getTimeString(Calendar cal) {
		if (cal == null) return null;
		return decimalformat_00.format(cal.get(Calendar.HOUR_OF_DAY)) + TIME_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.MINUTE)) + TIME_DELIMITER +
				decimalformat_00.format(cal.get(Calendar.SECOND));
	}
	
	public static Calendar toCalendar(String dateTimeStr, boolean includeTime) {
		if (ValidationUtils.isNullEmpty(dateTimeStr)) {
			return null;
		}
		
		//Split to {"2017-01-01", "12-12-12"}
		String[] str = dateTimeStr.replaceAll("\\s+", " ").replaceAll("\\s+\\D", "-").split(" ");
		
		if (str.length < 1) {
			throw new ServiceException(ServiceException.COMMON_DATA_PARSE_ERROR);
		}
		String[] date = str[0].split("\\D");
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		
		if (date.length == 2) {
			Calendar now = Calendar.getInstance();
			cal.set(now.get(Calendar.YEAR), Integer.parseInt(date[0]) - 1, Integer.parseInt(date[1]));
		} else if (date.length == 3) {
			cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
		} else {
			throw new ServiceException(ServiceException.COMMON_DATA_PARSE_ERROR);
		}
		
		if (includeTime) {
			if (str.length < 2) {
				throw new ServiceException(ServiceException.COMMON_DATA_PARSE_ERROR);
			}
			String[] time = str[1].split("\\D");
			if (time.length < 3) {
				throw new ServiceException(ServiceException.COMMON_DATA_PARSE_ERROR);
			}
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
			cal.set(Calendar.SECOND, Integer.parseInt(time[2]));
		}
		return cal;
	}
	
	public static Date toDate(String dateTimeStr, boolean includeTime) {
		Calendar cal = toCalendar(dateTimeStr, includeTime);
		if (cal != null) return cal.getTime();
		return null;
	}
	
	public static Integer daysBetween(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return 0;
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return daysBetween(cal1, cal2);
	}
	
	public static Integer daysBetween(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return 0;
		}
		cal1.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
		cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
		cal1.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
		cal1.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
		Long days = Math.abs(cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000);
		return days.intValue() + 1;
	}
	
	public static Integer daysDiffer(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			return 0;
		}
		cal1.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
		cal1.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
		cal1.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
		cal1.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
		Long db = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (24 * 60 * 60 * 1000);
		return db.intValue();
	}
	
	public static Calendar clearTime(Date date) {
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return clearTime(cal);
		}
		return null;
	}
	
	public static Calendar clearTime(Calendar cal) {
		if (cal != null) {
			cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
		}
		return cal;
	}
	
	public static Calendar setMaxTime(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		setMaxTime(cal);
		return cal;
	}
	
	public static Calendar setMaxTime(Calendar cal) {
		if (cal == null) return null;
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
		return cal;
	}
	public static Date getDayStart() {
		Calendar cal = Calendar.getInstance();
		clearTime(cal);
		return cal.getTime();
	}
	
	public static Date getDayEnd() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		clearTime(cal);
		cal.add(Calendar.MILLISECOND, -1);
		return cal.getTime();
	}
	
	public static Date increaseAndGet(Date date) {
		if (date == null) return null;
		date.setTime(date.getTime() + 24 * 60 * 60 * 1000);
		return date;
	}

	public static Date addAndGet(Date date, int days) {
		if (date == null) return null;
		date.setTime(date.getTime() + ((long) days) * 24 * 60 * 60 * 1000);
		return date;
	}

	public static Date getIfAbsence(Date get, Date defaultDate) {
		return get == null ? defaultDate : get;
	}
	
}

