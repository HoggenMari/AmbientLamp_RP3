package test;

import java.util.Date;
import java.util.List;

import test.SolarAnalyticsAPI.GRAN;

public interface SiteDataDao {
	
	public List<SiteData> getAllSiteData();
	public SiteData getSiteData(int entryNo);
	public SiteData getYear();
	public List<SiteData> getYear(int gran);
	public SiteData getMonth();
	public List<SiteData> getMonth(int gran);
	public List<SiteData> getMonth(int month, int year);
	public List<SiteData> getMonth(int month, int year, int gran);
	public SiteData getDay();
	public List<SiteData> getDay(GRAN value);
	public SiteData getDay(int day, int month, int year);
	public List<SiteData> getDay(int day, int month, int year, GRAN value);
	public List<SiteData> getIntervall(Date tStart, Date tEnd);
	public List<SiteData> getIntervall(Date tStart, Date tEnd, int gran);
	

}
