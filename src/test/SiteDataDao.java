package test;

import java.util.GregorianCalendar;
import java.util.List;

import test.SolarAnalyticsAPI.GRAN;

public interface SiteDataDao {
	
	public List<SiteData> getAllSiteData();
	public SiteData getSiteData(int entryNo);
	public SiteData getYear();
	public List<SiteData> getYear(GRAN value);
	public SiteData getMonth();
	public List<SiteData> getMonth(GRAN value);
	public List<SiteData> getMonth(int month, int year);
	public List<SiteData> getMonth(int month, int year, GRAN value);
	public SiteData getDay();
	public List<SiteData> getDay(GRAN value);
	public SiteData getDay(int day, int month, int year);
	public SiteData getDay(GregorianCalendar day);
	public List<SiteData> getDay(int day, int month, int year, GRAN value);
	public List<SiteData> getDay(GregorianCalendar day, GRAN value);
	public List<SiteData> getIntervall(GregorianCalendar tStart, GregorianCalendar tEnd);
	public List<SiteData> getIntervall(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear);
	public List<SiteData> getIntervall(GregorianCalendar tStart, GregorianCalendar tEnd, GRAN value);
	public List<SiteData> getIntervall(int startDay, int startMonth, int startYear, int endDay, int endMonth, int endYear, GRAN value);
	

}
