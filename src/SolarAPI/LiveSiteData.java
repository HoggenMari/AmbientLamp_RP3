package SolarAPI;

import java.util.ArrayList;

public class LiveSiteData {
	
	ArrayList<LiveSiteDataEntry> data= new ArrayList<LiveSiteDataEntry>();
	int tz_offset;
	String tz_zone;
	
	public LiveSiteData(ArrayList<LiveSiteDataEntry> data, int tz_offset, String tz_zone){
		this.data = data;
		this.tz_offset = tz_offset;
		this.tz_zone = tz_zone;
	}

	public void addLiveSiteDataEntry(LiveSiteDataEntry liveSiteDataEntry){
		
		
	}
}
