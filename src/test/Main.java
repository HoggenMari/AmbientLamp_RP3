package test;

import java.util.ArrayList;

import test.SolarAnalyticsAPI.GRAN;

public class Main {

    public static void main(final String... args){
    	
    	SolarAnalyticsAPI api = new SolarAnalyticsAPI();
    	    
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(21,10,2015,GRAN.day);
    	api.getDay(22,10,2015,GRAN.day);
    	api.getDay(22,10,2015,GRAN.day);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);
    	api.getDay(4,5,2016,GRAN.minute);
    	
    	while(1==1){
    		api.getDay(GRAN.minute);
    	}
    	
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
