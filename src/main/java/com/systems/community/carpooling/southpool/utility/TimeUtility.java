package com.systems.community.carpooling.southpool.utility;

public class TimeUtility {

	public TimeUtility(){}

	public static String convertToStandardTime(String time, String dayNight) {

		String hour = time.split(":")[0];
		String minutes = time.split(":")[1];
		String am_pm = dayNight;
		
		switch (hour) {
		case "13":
			return "1:"+minutes+" "+am_pm;
			
		case "14":
			return "2:"+minutes+" "+am_pm;
			
		case "15":
			return "3:"+minutes+" "+am_pm;
			
		case "16":
			return "4:"+minutes+" "+am_pm;
			
		case "17":
			return "5:"+minutes+" "+am_pm;
			
		case "18":
			return "6:"+minutes+" "+am_pm;
			
		case "19":
			return "7:"+minutes+" "+am_pm;
			
		case "20":
			return "8:"+minutes+" "+am_pm;
			
		case "21":
			return "9:"+minutes+" "+am_pm;
			
		case "22":
			return "10:"+minutes+" "+am_pm;
			
		case "23":
			return "11:"+minutes+" "+am_pm;
			
		case "00":
			return "12:"+minutes+" "+am_pm;
		}
		return hour+":"+minutes+" "+am_pm;
	}
	
	public static String convertMilitaryToStandardTime(String time) {
		
		switch (time) {
		case "13":
			return "1";
			
		case "14":
			return "2";
			
		case "15":
			return "3";
			
		case "16":
			return "4";
			
		case "17":
			return "5";
			
		case "18":
			return "6";
			
		case "19":
			return "7";
			
		case "20":
			return "8";
			
		case "21":
			return "9";
			
		case "22":
			return "10";
			
		case "23":
			return "11";
			
		case "00":
			return "12";
		}
		return time;
	}
	
	public static String convertStandardTimeToMilitaryTime(String time) {
		
		String[] hourtime = time.split(" ");
		String hour = hourtime[0].split(":")[0];
		String minutes = hourtime[0].split(":")[1];
		String am_pm = hourtime[1];
		
		switch (hour) {
		case "1":
			return "PM".equals(am_pm) ? "13:"+minutes+" "+ am_pm : time;
		case "2":
			return "PM".equals(am_pm) ? "14:"+minutes+" "+ am_pm : time;
		case "3":
			return "PM".equals(am_pm) ? "15:"+minutes+" "+ am_pm : time;
		case "4":
			return "PM".equals(am_pm) ? "16:"+minutes+" "+ am_pm : time;
		case "5":
			return "PM".equals(am_pm) ? "17:"+minutes+" "+ am_pm : time;
		case "6":
			return "PM".equals(am_pm) ? "18:"+minutes+" "+ am_pm : time;
		case "7":
			return "PM".equals(am_pm) ? "19:"+minutes+" "+ am_pm : time;
		case "8":
			return "PM".equals(am_pm) ? "20:"+minutes+" "+ am_pm : time;
		case "9":
			return "PM".equals(am_pm) ? "21:"+minutes+" "+ am_pm : time;
		case "10":
			return "PM".equals(am_pm) ? "22:"+minutes+" "+ am_pm : time;
		case "11":
			return "PM".equals(am_pm) ? "23:"+minutes+" "+ am_pm : time;
		case "12":
			return "AM".equals(am_pm) ? "00:"+minutes+" "+ am_pm : time;
		}
		return time;
	}

}