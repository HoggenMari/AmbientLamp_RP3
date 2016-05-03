package test;


public class Main {

    public static void main(final String... args){
    	
    	SolarAnalyticsAPI api = new SolarAnalyticsAPI();
    	
    	for(SiteData d : api.getAllSiteData()){
    		System.out.println(d);
    	};
    }

}
