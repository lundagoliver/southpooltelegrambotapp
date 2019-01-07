package com.systems.community.carpooling.southpool.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateUtility
 * @author Oliver D. Lundag
 *
 */
public class DateUtility {
	
	public static final String TIME =  " 23:59:59";
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final DateTimeFormatter FORMAT_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	public static final DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern(DATE_FORMAT);
	public static final DateTimeFormatter FORMAT_DATETIME_INFO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
	public static final long TICKS_AT_EPOCH = 621355968000000000L;
    public static final long TICKS_PER_MILLISECOND = 10000000;
    
    private DateUtility(){}


	/**
	 * Method used to get the gatherer request end date.
	 * @param requestStartDate
	 * @return the default 24 hour difference if the date is not todate
	 * else it will return the current date and time todate
	 */
	public static String getRequestEndDate(String requestStartDate) {
		LocalDateTime timePoint = LocalDateTime.now(); 
		String requestedstartDate = requestStartDate.substring(0,10); 
		String toDay = timePoint.format(FORMAT_YMD);
		return toDay.equals(requestedstartDate) ? timePoint.format(FORMAT_DATETIME) : requestedstartDate + TIME; 
	}
	
	/**
	 * Method used to get the gatherer request end date.
	 * @param requestStartDate
	 * @return the default 24 hour difference if the date is not todate
	 * else it will return the current date and time todate
	 */
	public static String getRequestEndDate(Date requestStartDate) {
		LocalDateTime timePoint = LocalDateTime.now();
		String requestedstartDate = LocalDateTime.ofInstant(requestStartDate.toInstant(), ZoneId.systemDefault()).format(FORMAT_DATETIME).substring(0,10);
		String toDay = timePoint.format(FORMAT_YMD);
		return toDay.equals(requestedstartDate) ? timePoint.format(FORMAT_DATETIME) : requestedstartDate + TIME; 
	}
	
	/**
	 * Method used to get the gatherer request end date
	 * @param requestStartDate
	 * @return the LocalDateTime default 24 hour difference if the date is not todate
	 * else it will return the current date and time todate
	 */
	public static LocalDateTime getLocalDateTimeRequestEndDate(Date requestStartDate) {
		LocalDateTime timePoint = LocalDateTime.now();
		String requestedstartDate = LocalDateTime.ofInstant(requestStartDate.toInstant(), ZoneId.systemDefault()).format(FORMAT_DATETIME).substring(0,10);
		String toDay = timePoint.format(FORMAT_YMD);
		String endDate = toDay.equals(requestedstartDate) ? timePoint.format(FORMAT_DATETIME) : requestedstartDate + TIME;
		return toLocaDateTime(endDate);
	}

	/**
	 * Get the duration of the specified date and time. 
	 * The required date format for parameters is yyyy-MM-dd HH:mm:ss
	 * @param jobStartRun
	 * @param jobEndRun
	 * @return duration of the specified date and time
	 */
	public static String getJobRunDuration(String jobStartRun, String jobEndRun) {
		LocalDateTime fromTime = LocalDateTime.parse(jobStartRun, FORMAT_DATETIME);
		LocalDateTime toDateTime = LocalDateTime.parse(jobEndRun, FORMAT_DATETIME);
		LocalDateTime tempDateTime = LocalDateTime.from(fromTime);
		long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays( days );
		long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours( hours );
		long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes( minutes );
		long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS);
		return new StringBuilder().append(days+" day(s), ").append(hours+" hour(s), ").append(minutes+" minute(s), ").append(seconds+" second(s).").toString();
	}	
	
	/**
	 * Get the duration of the specified date and time. 
	 * The required date format for parameters is yyyy-MM-dd HH:mm:ss
	 * @param jobStartRun
	 * @param jobEndRun
	 * @return duration of the specified date and time
	 */
	public static String getJobRunDuration(Date jobStartRun, Date jobEndRun) {
		LocalDateTime fromTime = LocalDateTime.ofInstant(jobStartRun.toInstant(), ZoneId.systemDefault());
		LocalDateTime toDateTime = LocalDateTime.ofInstant(jobEndRun.toInstant(), ZoneId.systemDefault());
		LocalDateTime tempDateTime = LocalDateTime.from(fromTime);
		long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays( days );
		long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours( hours );
		long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes( minutes );
		long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS);
		return new StringBuilder().append(days+" day(s), ").append(hours+" hour(s), ").append(minutes+" minute(s), ").append(seconds+" second(s).").toString();
	}	
	
	/**
	 * Covert to UTC date format
	 * Asia/Hong_Kong
	 * America/Anguilla
	 * America/Araguaina
	 * @param betTime
	 * @param timezone
	 * @return
	 */
	public static String getUTCDate(String betTime, int timezone) {
		ZoneId zoneId = null;
		switch (timezone) {
		case 8: //Asia/Hong_Kong
			zoneId = ZoneId.of("Asia/Hong_Kong");
			break;
		case -4: //America/Anguilla
			zoneId = ZoneId.of("America/Anguilla");
			break;
		case -3://America/Araguaina
			zoneId = ZoneId.of("America/Araguaina");
			break;
		case 9://Asia/Seoul
			zoneId = ZoneId.of("Asia/Seoul");
			break;

		default:
			break;
		}
	    LocalDateTime theBetTime = LocalDateTime.parse(betTime, FORMAT_DATETIME);
	    ZonedDateTime zonedDateTime = ZonedDateTime.of(theBetTime, zoneId);
	    ZonedDateTime utcDate = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
	    return utcDate.format(FORMAT_DATETIME);
	}
	
	/**
	 * Covert Date to LocalDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime toLocaDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}
	
	/**
	 * Covert date strng to LocalDateTime
	 * @param date
	 * @return
	 */
	public static LocalDateTime toLocaDateTime(String date) {
		return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
	}
	
	/**
	 * Method use to check if the date is still behind the requested end date
	 * @param dateToCheck
	 * @param requestEndDate
	 * @return
	 */
	public static boolean isDateBefore(LocalDateTime dateToCheck, LocalDateTime requestEndDate) {
		return dateToCheck.isBefore(requestEndDate);
	}
	
	/**
	 * Method to covert LocalDateTime to Date
	 * @param localDateTime
	 * @return
	 */
	public static Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	/**
	 * Covert date strng to LocalDateTime then to Date
	 * @param date
	 * @return
	 */
	public static Date covertLocaDateTimeToDate(String date) {
		return toDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT)));
	}
	
	/**
	 * Method used to get the simpledate format  yyyy-MM-dd HH:mm:ss
	 * @param date 
	 * @return yyyy-MM-dd HH:mm:ss format
	 */
	public static String getSimpleDateFormat(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(date); 
	}
	
	/**
	 * Get current date with the specified format
	 * @param format
	 * @return
	 */
	public static String getCurrentDateString(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}
	
	/**
	 * Method used to get the simpledate format  yyyy-MM-dd HH:mm:ss and covert to Date
	 * @param date  - yyyy-MM-dd HH:mm:ss
	 * @return Date
	 * @throws ParseException 
	 */
	public static Date getDateFormat(String date) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.parse(date);
	}
	
	/**
	 * millisToStringDate
	 * @param dateFormat
	 * @param milTime
	 * @return
	 */
	public static String millisToStringDate(String dateFormat, long milTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milTime);
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(cal.getTime());
	}
	
	/**
	 * Method used to get the simpledate format yyyy/MM/dd HH:mm:ss and covert to Date
	 * @param date - yyyy/MM/dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static Date getSimpleDateFormat(String date) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return dateFormat.parse(date);
	}

	/**
	 * Method used to get the simpledate format yyyy-MM-dd HH:mm:ss.SSS and covert to Date
	 * @param date - yyyy/MM/dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateFormatWithNanoSeconds(String date) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return dateFormat.parse(date);
	}


	/**
	 * convertTicksToDate
	 * @param ticks
	 * @return
	 */
    public static Date convertTicksToDate(long ticks) {
    	long time = (ticks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND;
    	
    	Date date = new Date(time * 1000L);
    	
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) - 9);
  	  
  	  
    	return cal.getTime();
    }
    
    /**
     * gmt 시간 기준으로 + 시간 가져오기.
     * @param hour
     * @param pattren
     * @param per 3분마다 30분마다. 등등. 
     * @return
     */
    public static String getStartGmtPlus(int hour , String pattren , int per){
    	Calendar gmt = Calendar.getInstance();
    	gmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    	gmt.setTimeInMillis(System.currentTimeMillis());
    	gmt.set(Calendar.HOUR, gmt.get(Calendar.HOUR) + hour);
    	gmt.set(Calendar.MINUTE, gmt.get(Calendar.MINUTE) - per);
    	SimpleDateFormat df = new SimpleDateFormat(pattren);
    	df.setTimeZone(TimeZone.getTimeZone("GMT"));
    	return df.format(gmt.getTime());	
    }
    
    /**
     * getUTCString
     * @return
     */
    public static String getUTCString() {
    	ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
    	ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
    	return utcDateTime.format(FORMAT_DATETIME);
    }
    
    /**
     * Get the date by prefered timezone
     * @param zone
     * @return
     */
	public static Date convertDateToGMT(int zone) {
		LocalDateTime curDate = LocalDateTime.now();
		switch (zone) {
		case 8: //Asia/Hong_Kong
			curDate = LocalDateTime.now(ZoneId.of("Asia/Hong_Kong"));
			break;
		case -4: //America/Anguilla
			curDate = LocalDateTime.now(ZoneId.of("America/Anguilla"));
			break;
		case -3://America/Araguaina
			curDate = LocalDateTime.now(ZoneId.of("America/Araguaina"));
			break;
		case 9://Asia/Seoul
			curDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
			break;
		case 7:
			curDate = LocalDateTime.now(ZoneId.of("Asia/Bangkok"));
			break;
		case 0:
			curDate = LocalDateTime.now(ZoneId.of("UTC"));
			break;
		default:
			break;
		}
		
		return covertLocaDateTimeToDate(curDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}	
}
