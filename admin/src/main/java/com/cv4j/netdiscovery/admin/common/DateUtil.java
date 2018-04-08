package com.cv4j.netdiscovery.admin.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String getCurrentDateForTag(){
		return getCurrentDate("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 得到当前时间(yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String getCurrentDate(){
		return formatDate(new Date());
	}
	
	/**
	 * 得到当前时间
	 * @param formate 格式
	 * @return
	 */
	public static String getCurrentDate(String formate){
		return formatDate(new Date(),formate);
	}
	
	/**
	 * 得到当前年份字符串
	 */
	public static String getCurrentYear() {
		return formatDate(new Date(), "yyyy");
	}

	/**
	 * 得到当前月份字符串
	 */
	public static String getCurrentMonth() {
		return formatDate(new Date(), "MM");
	}

	/**
	 * 得到当天字符串
	 */
	public static String getCurrentDay() {
		return formatDate(new Date(), "dd");
	}

	/**
	 * 得到当前星期字符串(星期几)
	 */
	public static String getCurrentWeek() {
		return formatDate(new Date(), "E");
	}
	
	/**
	 * Date转化为String
	 * @param date
	 * @param formate 格式
	 * @return
	 */
	public static String formatDate(Date date, String formate){
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		return sdf.format(date);
	}
	
	/**
	 * Date转化为String(yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date){
		return formatDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * String转化为Date
	 * @param date
	 * @param formate
	 * @return
	 */
	public static Date parseDate(String date, String formate){
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * String转化为Date
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date){
		return parseDate(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 比较时间大小
	 * @param first
	 * @param second
	 * @return 返回0 first等于second, 返回-1 first小于second,, 返回1 first大于second
	 */
	public static int compareToDate(String first, String second, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		try {
			cal1.setTime(df.parse(first));
			cal2.setTime(df.parse(second));
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("比较时间错误");
		}
		int result = cal1.compareTo(cal2);
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * 比较时间大小
	 * @param first
	 * @param second
	 * @return 返回0 first等于second, 返回-1 first小于second,, 返回1 first大于second
	 */
	public static int compareToDate(Date first, Date second) {
		int result = first.compareTo(second);
		if (result < 0) {
			return -1;
		} else if (result > 0) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * 得到给定时间的给定天数后的日期
	 * @return
	 */
	public static Date getAppointDate(Date date, int day){
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_YEAR, day);
		return calendar.getTime();
	}
	
	/**
	 * 获取两个日期之间的天数
	 * @param before
	 * @param after
	 * @return
	 */
	public static double getDistanceOfTwoDate(Date before, Date after) {
		long beforeTime = before.getTime();
		long afterTime = after.getTime();
		return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
	}
	
	/**
	 * 获取过去的天数
	 * @param date
	 * @return
	 */
	public static long pastDays(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (24 * 60 * 60 * 1000);
	}

	/**
	 * 获取过去的小时
	 * @param date
	 * @return
	 */
	public static long pastHour(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 60 * 1000);
	}

	/**
	 * 获取过去的分钟
	 * @param date
	 * @return
	 */
	public static long pastMinutes(Date date) {
		long t = new Date().getTime() - date.getTime();
		return t / (60 * 1000);
	}
	
	/**
	 * 得到本周的第一天
	 * @return
	 */
	public static Date getFirstDayOfWeek(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}
	
	/**
	 * 得到当月第一天
	 * @return
	 */
	public static Date getFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		int firstDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		return cal.getTime();
	}
	
	/**
	 * 得到下月的第一天
	 * @return
	 */
	public static Date getFirstDayOfNextMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, +1);
		int firstDay = cal.getMinimum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		return cal.getTime();
	}
	
	/**
	 * 根据生日获取年龄
	 * @param birtnDay
	 * @return
	 */
	public static int getAgeByBirthDate(Date birtnDay) {
		Calendar cal = Calendar.getInstance();
		if (cal.before(birtnDay)) {
			return 0;
		}
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTime(birtnDay);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		int age = yearNow - yearBirth;
		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				age--;
			}
		}
		return age;
	}

	public static Timestamp getCurrentTimestamp() {
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		return timeStamp;
	}
}
