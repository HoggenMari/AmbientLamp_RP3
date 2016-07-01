package SolarAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import SolarAPI.SolarAnalyticsAPI.GRAN;

public class SolarAnalyticsAPI implements SiteDataDao{

	public enum GRAN
	{
		minute,
		hour,
		day,
		month,
		year
	}
	
	public enum MONITORS
	{
		load_hot_water,
		ac_load_net,
		pv_site_net,
	}
	
	String webPage = "https://portal.solaranalytics.com.au/api/v2";
	String name = "demo@solaranalytics.com.au";
	String password = "demo123";
	int site_id = 8072;
	String token;
	long tokenTimeStamp;
	
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	DateFormat dateFormatLive = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	//SiteData with raw=false
	ArrayList<SiteData> siteData;
	HashMap<String,ArrayList<SiteData>> sData = new HashMap<String, ArrayList<SiteData>>();
	long lastUpdateSiteData;
	
	//SiteData with raw=true
	ArrayList<SiteDataRaw> siteDataRaw;
	HashMap<String,ArrayList<SiteDataRaw>> sDataRaw = new HashMap<String, ArrayList<SiteDataRaw>>();
	long lastUpdateSiteDataRaw;

	//LiveData
	ArrayList<LiveData> liveData;
	HashMap<String,ArrayList<LiveData>> sLiveData = new HashMap<String, ArrayList<LiveData>>();
	long lastUpdateSiteLiveData;
	
		
	public SolarAnalyticsAPI(){
		
		token = requestSecureToken();
		System.out.println(token);
		//requestData("day");
		siteData = new ArrayList<SiteData>();
		siteDataRaw = new ArrayList<SiteDataRaw>();
		liveData = new ArrayList<LiveData>();

	}
		
	public JsonObject requestData(String gran){
		
		Date date = new Date();
		long currentTimeStamp = date.getTime();getClass();
		
		
		if(currentTimeStamp-tokenTimeStamp>=6000){
			token = requestSecureToken();
		}
		String authString = token + ":x";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		
		URL url;
		try {
			
			//url = new URL(webPage+"/site_data/140?tstart=20160504&tend=20160504&gran="+gran+"&raw=false&trunc=false");
			url = new URL(webPage+gran);
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();
			
			JsonObject jsonObject;
			JsonParser jejpl = new JsonParser();
			
			JsonElement el = jejpl.parse(result);
			jsonObject = el.getAsJsonObject();
			
			//Verify Json with Class
			List<String> list = verifyElement(jsonObject, LiveData.class);

			for(String s : list){
			//	System.out.println(s);
			}
						
			return jsonObject;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return null;
	}
	
	private String requestSecureToken(){
		String authString = name + ":" + password;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		
		URL url;
		try {
			
			url = new URL(webPage+"/token");
			URLConnection urlConnection = url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);

			int numCharsRead;
			char[] charArray = new char[1024];
			StringBuffer sb = new StringBuffer();
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
			String result = sb.toString();
			
			JsonObject jsonObject;
			JsonParser jejpl = new JsonParser();

			JsonElement el = jejpl.parse(result);
			jsonObject = el.getAsJsonObject();

			String property = jsonObject.get("token").getAsString();
			
			Date date = new Date();
			tokenTimeStamp = date.getTime();
			
			
			return property;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static List<String> verifyElement(JsonObject element, Class klass) throws NoSuchFieldException, IllegalAccessException {
		  List<String> unknownFields = new ArrayList<>();
		  Set<String> classFields = new HashSet<>();

		  for (Field field : klass.getDeclaredFields()) {
		    if (!Modifier.isPublic(field.getModifiers())) {
		      throw new IllegalArgumentException("All fields must be public. Please correct this field :" + field);
		    }
		  }

		  for (Field field : klass.getFields()) {
		    classFields.add(field.getName());
		  }
		  
		  //System.out.println(classFields);
		  //System.out.println(element.entrySet());

		  // Verify recursively that the class contains every
		  for (Map.Entry<String, JsonElement> entry : element.entrySet()) {
			//System.out.println(entry.toString());
		    if (!classFields.contains(entry.getKey())) {
		      //System.out.println("unknown Field");
		      unknownFields.add(klass.getCanonicalName() + "::" + entry.getKey() + "\n");
		    } else {
		      Field field = klass.getField(entry.getKey());
		      Class fieldClass = field.getType();
		      if (!fieldClass.isPrimitive() && entry.getValue().isJsonObject()) {
		        List<String> elementErrors = verifyElement(entry.getValue().getAsJsonObject(), fieldClass);
		        unknownFields.addAll(elementErrors);
		      }
		    }
		  }
		  return unknownFields;

		}

	@Override
	public List<SiteData> getAllSiteData() {
		// TODO Auto-generated method stub
		return siteData;
	}

	@Override
	public SiteData getSiteData(int entryNo) {
		// TODO Auto-generated method stub
		return siteData.get(entryNo);
	}

	@Override
	public SiteData getYear() {
		return getYear(GRAN.year).get(0);
	}

	@Override
	public List<SiteData> getYear(GRAN value) {
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		return getIntervall(new GregorianCalendar(today.get(Calendar.YEAR),0,1), today, value);
	}

	@Override
	public SiteData getMonth() {
		return getMonth(GRAN.month).get(0);
	}

	@Override
	public List<SiteData> getMonth(GRAN value) {
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		return getIntervall(new GregorianCalendar(today.get(Calendar.YEAR),today.get(Calendar.MONTH),1), new GregorianCalendar(today.get(Calendar.YEAR),today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH)),value);
	}

	@Override
	public List<SiteData> getMonth(int month, int year) {
		return getMonth(month, year, GRAN.month);
	}

	@Override
	public List<SiteData> getMonth(int month, int year, GRAN value) {
		GregorianCalendar startCalendar = new GregorianCalendar(year, month-1, 1);
		return getMonth(startCalendar, value);
	}

	@Override
	public SiteData getDay() {
		return getDay(GRAN.day).get(0);
	}

	@Override
	public List<SiteData> getDay(GRAN value) {		
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		return getIntervall(today,today,value);
	}

	@Override
	public SiteData getDay(int day, int month, int year) {
		return getDay(day, month, year, GRAN.day).get(0);
	}

	@Override
	public List<SiteData> getDay(int day, int month, int year, GRAN value) {
		return getIntervall(day, month, year, day, month, year, value);
	}

	@Override
	public List<SiteData> getIntervall(GregorianCalendar startCalendar, GregorianCalendar endCalendar, GRAN value) {
		// TODO Auto-generated method stub
	
		Date date = new Date();
		long timeStamp = date.getTime();
		
		GregorianCalendar copyS = (GregorianCalendar) startCalendar.clone();
		GregorianCalendar copyE = (GregorianCalendar) endCalendar.clone();
		
		//System.out.println(copyE.get(Calendar.DAY_OF_MONTH));
		
		copyS.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
		copyE.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
		
		String tStartS = dateFormat.format(copyS.getTime());
		String tEndS = dateFormat.format(copyE.getTime());
		
		String hash = tStartS+tEndS+value;
	    ArrayList<SiteData> siteDataList = (ArrayList<SiteData>)sData.get(hash);
	    
	    long before = System.nanoTime();
	    //System.out.println("BEFORE: "+System.nanoTime());
	    
	    if(siteDataList == null) {
	    	
	    	lastUpdateSiteData = timeStamp;
	    	
	    	siteDataList = new ArrayList<SiteData>();
	        
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value);
			
			System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value);
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			siteData = new ArrayList<SiteData>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
	        sData.put(hash, siteData);
	        
		    System.out.println("AFTER:  "+(System.nanoTime()-before));

		    System.out.println(sData.get(hash).toString());

	        return sData.get(hash);
	        //System.out.println("Creating circle of color : " + hash);
	    }else if(lastUpdateSiteData < timeStamp-(timeStamp%300000) && endCalendar.get(Calendar.YEAR)==GregorianCalendar.getInstance().get(Calendar.YEAR) && endCalendar.get(Calendar.MONTH)==GregorianCalendar.getInstance().get(Calendar.MONTH) && endCalendar.get(Calendar.DAY_OF_MONTH)==GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
	    	
	    	lastUpdateSiteData = timeStamp;
	    	sData.remove(hash);
	    	siteDataList = new ArrayList<SiteData>();
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value);
			JsonArray getArray = jsonObject.getAsJsonArray("data");

			Gson gson = new Gson();
			siteData = new ArrayList<SiteData>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
	        sData.put(hash, siteData);
	        
		    System.out.println("AFTER M:  "+(System.nanoTime()-before));

		    System.out.println(sData.get(hash).toString());
		    
	        return sData.get(hash);
	    }
	    
	    //System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    //System.out.println(sData.get(hash).toString());

		return siteDataList;
	}

	@Override
	public List<SiteData> getIntervall(GregorianCalendar tStart, GregorianCalendar tEnd) {
		return getIntervall(tStart, tEnd, GRAN.day);
	}

	@Override
	public List<SiteData> getIntervall(int startDay, int startMonth, int startYear, int endDay, int endMonth,
			int endYear) {
		return getIntervall(startDay, startMonth, startYear, endDay, endMonth, endYear, GRAN.day);
	}

	@Override
	public List<SiteData> getIntervall(int startDay, int startMonth, int startYear, int endDay, int endMonth,
			int endYear, GRAN value) {
		GregorianCalendar startCalendar = new GregorianCalendar(startYear, startMonth-1, startDay);
		GregorianCalendar endCalendar = new GregorianCalendar(endYear, endMonth-1, endDay);
		return getIntervall(startCalendar, endCalendar, value);
	}

	@Override
	public SiteData getDay(GregorianCalendar day) {
		return getIntervall(day, day, GRAN.day).get(0);
	}

	@Override
	public List<SiteData> getDay(GregorianCalendar day, GRAN value) {
		return getIntervall(day, day, value);
	}

	@Override
	public List<SiteData> getMonth(GregorianCalendar date) {
		return getMonth(date, GRAN.day);
	}

	@Override
	public List<SiteData> getMonth(GregorianCalendar date, GRAN value) {
		GregorianCalendar copyMonthStart = (GregorianCalendar) date.clone();
		GregorianCalendar copyMonthEnd = (GregorianCalendar) date.clone();
		
		copyMonthStart.set(Calendar.DAY_OF_MONTH, 1);
		int month = date.get(Calendar.MONTH);
		//System.out.println(month);
		if(month==0 || month==2 || month==4 || month==6 || month==7 || month==9 || month==11 ) {
			copyMonthEnd.set(Calendar.DAY_OF_MONTH, 31);
		}else if(month==1){
			if(date.isLeapYear(date.get(Calendar.YEAR))){
				copyMonthEnd.set(Calendar.DAY_OF_MONTH, 29);
			}else{
				copyMonthEnd.set(Calendar.DAY_OF_MONTH, 28);
			}
		}else{
			copyMonthEnd.set(Calendar.DAY_OF_MONTH, 30);			
		}
		if(date.get(Calendar.YEAR)==GregorianCalendar.getInstance().get(Calendar.YEAR) & date.get(Calendar.MONTH)==GregorianCalendar.getInstance().get(Calendar.MONTH)){
			//System.out.println("go");
			return getMonth(value);
		}else{
			return getIntervall(copyMonthStart, copyMonthEnd, value);
		}
	}

	@Override
	public SiteData getYear(int year) {
		return getYear(year, GRAN.year).get(0);
	}

	@Override
	public List<SiteData> getYear(int year, GRAN value) {
		GregorianCalendar startCalendar = new GregorianCalendar(year, 0, 1);
		return getYear(startCalendar, value);
	}

	@Override
	public SiteData getYear(GregorianCalendar date) {
		return getYear(date, GRAN.year).get(0);
	}

	@Override
	public List<SiteData> getYear(GregorianCalendar date, GRAN value) {
		GregorianCalendar copyDate = (GregorianCalendar) date.clone();
		
		if(date.get(Calendar.YEAR)==GregorianCalendar.getInstance().get(Calendar.YEAR)){
			return getYear(value);
		}else{
			return getIntervall(new GregorianCalendar(copyDate.get(Calendar.YEAR), 0, 1),new GregorianCalendar(copyDate.get(Calendar.YEAR), 11, 31), value);
		}
	}

	@Override
	public List<SiteDataRaw> getIntervall(GregorianCalendar startCalendar, GregorianCalendar endCalendar, GRAN value, boolean raw) {
		// TODO Auto-generated method stub

		Date date = new Date();
		long timeStamp = date.getTime();
		
		GregorianCalendar copyS = (GregorianCalendar) startCalendar.clone();
		GregorianCalendar copyE = (GregorianCalendar) endCalendar.clone();
		
		//System.out.println(copyE.get(Calendar.DAY_OF_MONTH));
		
		copyS.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
		copyE.set(Calendar.MONTH, endCalendar.get(Calendar.MONTH));
		
		String tStartS = dateFormat.format(copyS.getTime());
		String tEndS = dateFormat.format(copyE.getTime());
		
		String hash = tStartS+tEndS+value+raw;
	    ArrayList<SiteDataRaw> siteDataList = (ArrayList<SiteDataRaw>)sDataRaw.get(hash);
	    
	    long before = System.nanoTime();
	    //System.out.println("BEFORE: "+System.nanoTime());
	    
	    if(siteDataList == null) {
	    	
	    	lastUpdateSiteDataRaw = timeStamp;
	    	
	    	siteDataList = new ArrayList<SiteDataRaw>();
	        
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value+"&raw=true");
			
			System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value+"&raw=true");
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			siteDataRaw = new ArrayList<SiteDataRaw>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteDataRaw.add(gson.fromJson(getArray.get(i), SiteDataRaw.class));
			}
	        sDataRaw.put(hash, siteDataRaw);
	        
		    //System.out.println("AFTER:  "+(System.nanoTime()-before));

		    //System.out.println(sDataRaw.get(hash).toString());

	        return sDataRaw.get(hash);
	        //System.out.println("Creating circle of color : " + hash);
	    }else if(lastUpdateSiteDataRaw < timeStamp-(timeStamp%300000) && endCalendar.get(Calendar.YEAR)==GregorianCalendar.getInstance().get(Calendar.YEAR) && endCalendar.get(Calendar.MONTH)==GregorianCalendar.getInstance().get(Calendar.MONTH) && endCalendar.get(Calendar.DAY_OF_MONTH)==GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
	    	
	    	lastUpdateSiteDataRaw = timeStamp;
	    	sDataRaw.remove(hash);
	    	siteDataList = new ArrayList<SiteDataRaw>();
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value+"&raw=true");
			JsonArray getArray = jsonObject.getAsJsonArray("data");

			Gson gson = new Gson();
			siteDataRaw= new ArrayList<SiteDataRaw>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteDataRaw.add(gson.fromJson(getArray.get(i), SiteDataRaw.class));
			}
	        sDataRaw.put(hash, siteDataRaw);
	        
		    //System.out.println("AFTER M:  "+(System.nanoTime()-before));

		    //System.out.println(sDataRaw.get(hash).toString());
		    
	        return sDataRaw.get(hash);
	    }
	    
	    //System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    //System.out.println(sData.get(hash).toString());

		return siteDataList;
	}

	@Override
	public List<LiveData> getIntervallLive(GregorianCalendar startCalendar, boolean all) {
		// TODO Auto-generated method stub
		
		String watt_device_id = getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(0).watt_device_id;
		
		
		Date date = new Date();
		long timeStamp = date.getTime();
		
		GregorianCalendar copyS = (GregorianCalendar) startCalendar.clone();
		
		//System.out.println(copyE.get(Calendar.DAY_OF_MONTH));
		
		copyS.set(Calendar.MONTH, startCalendar.get(Calendar.MONTH));
		
		String tStartS = dateFormatLive.format(copyS.getTime());
		
		String hash = lastUpdateSiteLiveData+" "+all;
	    ArrayList<LiveData> liveDataList = (ArrayList<LiveData>)sLiveData.get(hash);
	    
	    long before = System.nanoTime();
	    //System.out.println("BEFORE: "+System.nanoTime());
	    
	    if(liveDataList == null) {
	    	
	    	lastUpdateSiteLiveData = timeStamp;
	    	
	    	hash = lastUpdateSiteLiveData+" "+all;
	    	
	    	liveDataList = new ArrayList<LiveData>();
	        
	        //JsonObject jsonObject = requestData("/live_data?device="+watt_device_id+"&tstamp="+tStartS+"&all="+all);
			
	    	JsonObject jsonObject = requestData("/live_data?device="+watt_device_id+"&all="+all);
			
			//System.out.println("/live_data?device="+watt_device_id+"&tstamp="+tStartS+"&all="+all);
			
			//System.out.println("/live_data?device="+watt_device_id+"&all="+all);

			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			liveData = new ArrayList<LiveData>();
	        
			for(int i=0; i<getArray.size(); i++){
				liveData.add(gson.fromJson(getArray.get(i), LiveData.class));
			}
	        sLiveData.put(hash, liveData);
	        
	        System.out.println("null");
		    //System.out.println("AFTER:  "+(System.nanoTime()-before));

		    //System.out.println(sLiveData.get(hash));

	        return sLiveData.get(hash);
	        //System.out.println("Creating circle of color : " + hash);
	    }else if(lastUpdateSiteLiveData < timeStamp-(timeStamp%30000)) {
	    	
	        System.out.println("changed");

	    	lastUpdateSiteLiveData = timeStamp;
	    	sData.remove(hash);
	    	liveDataList = new ArrayList<LiveData>();
	        //JsonObject jsonObject = requestData("/live_data?device="+watt_device_id+"&tstamp="+tStartS+"&all="+all);
	        JsonObject jsonObject = requestData("/live_data?device="+watt_device_id+"&all="+all);
			
	        JsonArray getArray = jsonObject.getAsJsonArray("data");

			
			Gson gson = new Gson();
			liveData = new ArrayList<LiveData>();
	        
			for(int i=0; i<getArray.size(); i++){
				liveData.add(gson.fromJson(getArray.get(i), LiveData.class));
			}
	        sLiveData.put(hash, liveData);
	        
		    //System.out.println("AFTER M:  "+(System.nanoTime()-before));

		    //System.out.println(sLiveData.get(hash));
		    
	        return sLiveData.get(hash);
	    }
	    
	    //System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    //System.out.println(sData.get(hash).toString());

		return liveDataList;
		
	}

	@Override
	public LiveDataEntry getLastEntry(MONITORS monitor) {
		// TODO Auto-generated method stub
		
		List<LiveData> liveData = getIntervallLive(new GregorianCalendar(), true);
		
		for(LiveData entry : liveData){
			//System.out.println(entry.monitors);
			if(entry.monitors.contains(monitor.toString())){
				if(entry.live_data.size()>0){
				return entry.live_data.get(entry.live_data.size()-1);
				};
			};
		}
		
		return null;
	}

	@Override
	public List<LiveDataEntry> getLiveDataEntry(MONITORS monitor) {
		// TODO Auto-generated method stub
		List<LiveData> liveData = getIntervallLive(new GregorianCalendar(), true);

		for(LiveData entry : liveData){
			//System.out.println(entry.monitors);
			if(entry.monitors.contains(monitor.toString())){
				return entry.live_data;
			};
		}
		
		return null;
	}
}