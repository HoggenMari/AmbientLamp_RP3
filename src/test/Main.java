package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import test.SolarAnalyticsAPI.GRAN;

public class Main {

    public static void main(final String... args){
    	
    	SolarAnalyticsAPI api = new SolarAnalyticsAPI();
    	    
    	/*api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(21,10,2015,GRAN.day);
    	api.getDay(22,10,2015,GRAN.day);
    	api.getDay(22,10,2015,GRAN.day);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);*/
    	
    	while(1==1){
    		System.out.println(api.getDay(GRAN.day));
    	}
    		
    	
    	
    	
    	
    	/*api.getIntervall(3, 10, 2015, 3, 10, 2015, GRAN.minute);
    	api.getIntervall(3, 10, 2015, 3, 10, 2015);
    	
    	api.getIntervall(new GregorianCalendar(2015,10,4), new GregorianCalendar(2015,11,4));
    	api.getIntervall(new GregorianCalendar(2015,10,4), new GregorianCalendar(2015,11,4), GRAN.day);
    	
    	api.getDay(new GregorianCalendar(2015,10,4));
    	api.getDay(new GregorianCalendar(2015,10,4), GRAN.day);
    	
    	api.getDay(4, 10, 2015);
    	api.getDay(4, 10, 2015, GRAN.day);
    	
    	api.getDay();
    	api.getDay(GRAN.minute);*/
    	
    	
    	
    	
    	
    	//System.out.println(api.getIntervall(3, 10, 2015, 3, 10, 2015, GRAN.minute));
    	//System.out.println(api.getIntervall(3, 10, 2015, 3, 10, 2015));
    	
    	/*System.out.println(api.getIntervall(new GregorianCalendar(2015,10,4), new GregorianCalendar(2015,11,4)));
    	System.out.println(api.getIntervall(new GregorianCalendar(2015,10,4), new GregorianCalendar(2015,11,4), GRAN.day));
    	
    	System.out.println(api.getDay(new GregorianCalendar(2015,10,4)));
    	System.out.println(api.getDay(new GregorianCalendar(2015,10,4), GRAN.day));*/
    	
    	//System.out.println(api.getDay(4, 10, 2015));
    	//System.out.println(api.getDay(4, 10, 2015, GRAN.day));
    	
    	//System.out.println(api.getDay());
    	//System.out.println(api.getDay(GRAN.minute));
    	
    	
    	
    	//System.out.println(api.getYear());
    	//System.out.println(api.getYear(GRAN.day).get(2));
    	//System.out.println(api.getYear(new GregorianCalendar(2015,01,01)));
    	//System.out.println(api.getYear(new GregorianCalendar(2015,01,01), GRAN.day));
    	//System.out.println(api.getYear(2015));
    	//System.out.println(api.getYear(2015,GRAN.day));
    	
    	
    	//System.out.println(api.getDay(GRAN.minute));
    	//System.out.println(api.getDay(05, 05, 2016, GRAN.minute));

    	//System.out.println(api.getMonth(11, 2015, GRAN.month));
    	
    	
    	//System.out.println(api.getMonth(new GregorianCalendar(2015,Calendar.JUNE,4), GRAN.day));
    	//System.out.println(api.getMonth(new GregorianCalendar(2015,8,4)));

    	//System.out.println(api.getMonth(GRAN.day));

    	
    	//System.out.println(api.getDay(new GregorianCalendar(2015,10,4)));
    	//System.out.println(api.getDay(new GregorianCalendar(2015,10,4), GRAN.day));
    	
    	//System.out.println(api.getMonth(10,2015));
    	//System.out.println(api.getMonth(10,2015, GRAN.hour));

    	//System.out.println(api.getDay(GRAN.minute));
    	
    	//System.out.println(api.getMonth());
    	//System.out.println(api.getMonth(9,2015,GRAN.day));

    	
    	
    	
    	//api.getDay(GRAN.minute);

    	//api.getIntervall(new GregorianCalendar(2015,9,10), new GregorianCalendar(2015,9,15), GRAN.day);
    	
    	/*ArrayList<SiteData> daily = new ArrayList<SiteData>();
    	ArrayList<SiteData> minute = new ArrayList<SiteData>();

    	daily = (ArrayList<SiteData>) api.getAllSiteData();
    	
    	api.requestData("minute");
    	minute = (ArrayList<SiteData>) api.getAllSiteData();

    	System.out.println("BEGIN:");
    	for(SiteData d : daily){
    		System.out.println(d);
    	};
    	System.out.println("END:");
    	
    	System.out.println("BEGIN:");
    	for(SiteData d : minute){
    		System.out.println(d);
    	};
    	System.out.println("END:");*/

    }

}
