package test;

import java.util.List;

public interface SiteDataDao {
	
	public List<SiteData> getAllSiteData();
	public SiteData getSiteData(int entryNo);
	

}
