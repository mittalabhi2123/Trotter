package com.trotter.common;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class Utility {

	public static Properties systVariables = new Properties();
	
	public static enum MongoQueryHandles {
		$set, $push, $in, $gte;
	}

	public static void initSystVariables() {
        String filePath = System.getenv(Const.SYST_VAR_PROP.SYST_VAR_NAME);
        if(isNullEmpty(filePath))
            throw new RuntimeException("System Variable not set!!!");
        try{
            FileInputStream fis = new FileInputStream(filePath);
            systVariables.clear();
            systVariables.load(fis);
            fis.close();
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
	
	 public static boolean isNullEmpty(String data){
	      return data == null || data.equalsIgnoreCase("");
	 }
	 
    public static boolean isNumeric(String text) {
        for(int i = 0 ; i < text.length() ; i ++){
            if(!Character.isDigit(text.charAt(i)))
                return false;
        }
        return true;
    }
    
    public static class DateUtils{
    	public static int date2Int(Date date){
    		if(date == null)
    			return 0;
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		String dd = cal.get(Calendar.DAY_OF_MONTH) > 9 ? cal.get(Calendar.DAY_OF_MONTH) + "" : "0" + cal.get(Calendar.DAY_OF_MONTH);
    		String mm = (cal.get(Calendar.MONTH) + 1) > 9 ? (cal.get(Calendar.MONTH) + 1) + "" : "0" + (cal.get(Calendar.MONTH + 1));
    		String yyyy = cal.get(Calendar.YEAR) + "";
    		return Integer.parseInt(dd+mm+yyyy);
    	}
    	
    	public static int time2Int(Date date){
    		if(date == null)
    			return 0;
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		String hh = cal.get(Calendar.HOUR_OF_DAY) > 9 ? cal.get(Calendar.HOUR_OF_DAY) + "" : "0" + cal.get(Calendar.HOUR_OF_DAY);
    		String mm = cal.get(Calendar.MINUTE) > 9 ? cal.get(Calendar.MINUTE) + "" : "0" + cal.get(Calendar.MINUTE);
    		return Integer.parseInt(hh+mm);
    	}
    	
    	public static Date Int2DateTime(int dateVal, int timeVal){
    		if(dateVal <= 0)
    			return null;
    		String data = dateVal + "";
    		if(data.length()<7 || data.length() > 8)
    			throw new RuntimeException("Invalid date supplied (" + data + ")!!!");
    		if(data.length() == 7)
    			data = "0" + data;
    		Calendar cal = Calendar.getInstance();
    		String dd = data.substring(0,2);
    		String mm = data.substring(2,4);
    		String yyyy = data.substring(4);
    		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
    		cal.set(Calendar.MONTH, Integer.parseInt(mm) - 1);
    		cal.set(Calendar.YEAR, Integer.parseInt(yyyy));
    		if(timeVal <= 0)
    			return cal.getTime();
    		String timeData = timeVal + "";
    		if(timeData.length() < 3 || timeData.length() > 4)
    			throw new RuntimeException("Invalid time supplied (" + timeData + ")!!!");
    		if(timeData.length() == 3)
    			timeData = "0" + timeData;
    		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeData.substring(0,2)));
    		cal.set(Calendar.MINUTE, Integer.parseInt(timeData.substring(2)));
    		return cal.getTime();
    	}
    	
    	public static Date int2Time(Date date, int value){
    		if(value <= 0)
    			return null;
    		String data = value + "";
    		if(data.length()<3 || data.length() > 4)
    			throw new RuntimeException("Invalid time supplied (" + data + ")!!!");
    		if(data.length() == 3)
    			data = "0" + data;
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		String hh = data.substring(0,2);
    		String mm = data.substring(2,4);
    		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hh));
    		cal.set(Calendar.MINUTE, Integer.parseInt(mm));
    		return cal.getTime();
    	}
    	
    	public static int hhmm2Time(int hh, int mm){
    		return (hh*100) + mm;
    	}
    }
}
