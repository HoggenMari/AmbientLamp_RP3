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

import javax.swing.event.EventListenerList;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SolarAnalyticsAPI extends Thread implements SiteDataDao{

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
		ac_load_net,
		load_hot_water,
		pv_site_net
	}
	
	private static SolarAnalyticsAPI instance;
	
	String webPage = "https://portal.solaranalytics.com.au/api/v2";
	String name = "demo@solaranalytics.com.au";
	//String name = "m.hoggenmueller@googlemail.com";
	String password = "demo123";//"fr21muc08";
	//String password = "fr21muc08";
	int site_id = 8072;
	//int site_id = 8753;
	String token;
	int duration;
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
	ArrayList<LiveDataEntry> consumptionArray = new ArrayList<LiveDataEntry>();
	ArrayList<LiveDataEntry>[] liveBuffer = new ArrayList[3];
	HashMap<String,ArrayList<LiveData>> sLiveData = new HashMap<String, ArrayList<LiveData>>();
	long lastUpdateLiveData;
	int MAX_LIVE_DATA_SIZE = 200;
	
	//LiveSiteData
	ArrayList<LiveSiteDataEntry> live_site_data;
	long lastUpdateLiveSiteData;
	EventListenerList listenerList = new EventListenerList();
	
	private SolarAnalyticsAPI () {
		token = requestSecureToken();
		System.out.println(token);
		//requestData("day");
		siteData = new ArrayList<SiteData>();
		siteDataRaw = new ArrayList<SiteDataRaw>();
		liveData = new ArrayList<LiveData>();
		
		for(int i=0; i<liveBuffer.length; i++){
			liveBuffer[i] = new ArrayList<LiveDataEntry>();
		}
		
	}
	
	public void start() {
		super.start();
	}

	public void run() {
		while (true) {
			
			getLiveSiteData();
			//getLiveSiteData();
		}
	}

	public static synchronized SolarAnalyticsAPI getInstance() {
		if (SolarAnalyticsAPI.instance == null) {
			SolarAnalyticsAPI.instance = new SolarAnalyticsAPI ();
		}
		return SolarAnalyticsAPI.instance;
    }

	public void addLiveDataListener(SolarListener l) {
		listenerList.add(SolarListener.class, l);
	}

	public void removeSensorListener(SolarListener l) {
		listenerList.remove(SolarListener.class, l);
	}
	
	public synchronized JsonObject requestData(String gran){
		
		Date date = new Date();
		long currentTimeStamp = date.getTime();
		
		
		if(currentTimeStamp-tokenTimeStamp>=duration*10){
			System.out.println("Request Secure Token");
			token = requestSecureToken();
		}
		String authString = token + ":x";
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		
		URL url;
		try {
			
			//url = new URL(webPage+"/site_data/140?tstart=20160504&tend=20160504&gran="+gran+"&raw=false&trunc=false");
			url = new URL(webPage+gran);
			System.out.println("URL: "+url);
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
						
			//System.out.println("sb"+jsonObject);

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
			duration = jsonObject.get("duration").getAsInt();
						
			Date date = new Date();
			tokenTimeStamp = date.getTime();
			
			
			return property;
			
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Excpetion: "+e.getMessage());
			//e.printStackTrace();
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
			
	        if(jsonObject != null){
	        	System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value);
			
	        	JsonArray getArray = jsonObject.getAsJsonArray("data");
			
	        	Gson gson = new Gson();
	        	siteData = new ArrayList<SiteData>();
	        
	        	for(int i=0; i<getArray.size(); i++){
	        		siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
	        	}
	        	sData.put(hash, siteData);
	        
		    //System.out.println("AFTER:  "+(System.nanoTime()-before));

		    //System.out.println(sData.get(hash).toString());

	        return sData.get(hash);
	        
	        } else {
	        	//System.out.println("null");

	        	return new ArrayList<SiteData>();
	        }
	        //System.out.println("Creating circle of color : " + hash);
	    }else if(lastUpdateSiteData < timeStamp-(timeStamp%30000) && endCalendar.get(Calendar.YEAR)==GregorianCalendar.getInstance().get(Calendar.YEAR) && endCalendar.get(Calendar.MONTH)==GregorianCalendar.getInstance().get(Calendar.MONTH) && endCalendar.get(Calendar.DAY_OF_MONTH)==GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
	    	
	    	lastUpdateSiteData = timeStamp;
	    	sData.remove(hash);
	    	siteDataList = new ArrayList<SiteData>();
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStartS+"&tend="+tEndS+"&gran="+value);
			
	        if(jsonObject != null){
	        	JsonArray getArray = jsonObject.getAsJsonArray("data");

				Gson gson = new Gson();
				siteData = new ArrayList<SiteData>();
	        
				for(int i=0; i<getArray.size(); i++){
					siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
				}
	        	sData.put(hash, siteData);
	        
		    	//System.out.println("AFTER M:  "+(System.nanoTime()-before));

		    	//System.out.println(sData.get(hash).toString());
		    
	        	return sData.get(hash);
	        } else {
	        	//System.out.println("null");

	        	return new ArrayList<SiteData>();
	        }
	    }
	    
	    //System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    //System.out.println(sData.get(hash).toString());
	    
	    //System.out.println(sData.size());

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
			
			//if(jsonObject.has("bla")){
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			siteDataRaw = new ArrayList<SiteDataRaw>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteDataRaw.add(gson.fromJson(getArray.get(i), SiteDataRaw.class));
			}
	        sDataRaw.put(hash, siteDataRaw);
			//}
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
		
		String watt_device_id = getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(0).watt_device_id;
		
		Date date = new Date();
		long timeStamp = date.getTime();
		
		if(liveData == null || lastUpdateLiveData < timeStamp-(timeStamp%30000)){
			
			liveData = new ArrayList<LiveData>();
			JsonObject jsonObject = requestData("/live_data?device="+watt_device_id+"&all="+all);
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			liveData = new ArrayList<LiveData>();
	        
			for(int i=0; i<getArray.size(); i++){
				liveData.add(gson.fromJson(getArray.get(i), LiveData.class));
				//System.out.println(getArray.get(i));
			}
			
			lastUpdateLiveData = timeStamp;
			
		}
		
		return liveData;
		
	}
	
	/*public List<LiveData> getIntervallLive(GregorianCalendar startCalendar, boolean all) {
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
		
	}*/

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

	@Override
	public SiteData getLastSiteDataEntry() {
		// TODO Auto-generated method stub
		List<SiteData> liveData = getDay(GRAN.minute);
		
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		int hours = today.get(Calendar.HOUR_OF_DAY);
		int minutes = today.get(Calendar.MINUTE);
	
		int counter = 12*hours+minutes/5;
		//System.out.println(counter);
		
		return liveData.get(counter);
	}
	
	public float getMaxProducedWeekly(GRAN gran){
		
		GregorianCalendar lastWeek = (GregorianCalendar) GregorianCalendar.getInstance();
		lastWeek.add(GregorianCalendar.DAY_OF_MONTH, -7);

		List<SiteData> data = getIntervall(lastWeek, new GregorianCalendar(), gran);
		
		float max_produced = 0;
		for(SiteData entry : data){
			if(entry.energy_generated > max_produced){
				max_produced = entry.energy_generated;
			}
		}
		
		//System.out.println(max_produced);
		
		//System.out.println(data.size());
		
		return max_produced;
	}
	
	public float getMeanProducedWeekly(GRAN gran){
		
		GregorianCalendar lastWeek = (GregorianCalendar) GregorianCalendar.getInstance();
		lastWeek.add(GregorianCalendar.DAY_OF_MONTH, -7);

		List<SiteData> data = getIntervall(lastWeek, new GregorianCalendar(), gran);
		
		float max_produced = 0;
		for(SiteData entry : data){
			max_produced += entry.energy_consumed;
		}
		
		System.out.println(max_produced/data.size());
		
		return max_produced/data.size();
		
	}
	
	public float getMaxConsumedWeekly(GRAN gran){
		
		GregorianCalendar lastWeek = (GregorianCalendar) GregorianCalendar.getInstance();
		lastWeek.add(GregorianCalendar.DAY_OF_MONTH, -7);

		List<SiteData> data = getIntervall(lastWeek, new GregorianCalendar(), gran);
		
		float max_produced = 0;
		for(SiteData entry : data){
			if(entry.energy_consumed > max_produced){
				max_produced = entry.energy_consumed;
			}
		}
		
		//System.out.println(data);
		
		return max_produced;
	}
	
	public ArrayList<LiveDataEntry> getLiveBuffer(MONITORS monitor){
		
		List<LiveData> liveData = getIntervallLive(new GregorianCalendar(), true);
				
		if(liveBuffer[monitor.ordinal()].isEmpty()){
			for(LiveDataEntry entry : liveData.get(monitor.ordinal()).live_data){
				liveBuffer[monitor.ordinal()].add(entry);
			}
		}else{
			
			int buf1 = liveData.get(monitor.ordinal()).live_data.size();
			int buf2 = liveBuffer[monitor.ordinal()].size();
			
			//System.out.println(buf1+" "+buf2);
			
			int num = 0;
			
			for(int i=0; i<buf1; i++){
				boolean comp = false;
				for(int j=0; j<buf2; j++){
					if(liveData.get(monitor.ordinal()).live_data.get(i).time.compareTo(liveBuffer[monitor.ordinal()].get(j).time)==0){
						comp = true;
					};
				}
				if(comp==true){
					num = i;
				}
			}
			
			if(num!=buf1-1){
				for(int i=num+1; i<buf1; i++){
					liveBuffer[monitor.ordinal()].add(liveData.get(monitor.ordinal()).live_data.get(i));
				}
			}
			
			//System.out.println(num);
			
			if(liveBuffer[monitor.ordinal()].size()>MAX_LIVE_DATA_SIZE){
				liveBuffer[monitor.ordinal()].remove(0);
			}
		}
		
		//System.out.println(monitor.ordinal());
		
		return liveBuffer[monitor.ordinal()];
	}
	
	public ArrayList<LiveDataEntry> getLiveConsumptionBuffer() {
		List<LiveData> liveData = getIntervallLive(new GregorianCalendar(), true);

		if(consumptionArray.isEmpty()){
			//System.out.println("Empty");
			for(LiveDataEntry entry : liveData.get(0).live_data){
				consumptionArray.add(entry);
			}
		}else{
			
			int buf1 = liveData.get(0).live_data.size();
			int buf2 = consumptionArray.size();
			
			//System.out.println(buf1+" "+buf2);
			
			int num = 0;
			
			for(int i=0; i<buf1; i++){
				boolean comp = false;
				for(int j=0; j<buf2; j++){
					if(liveData.get(0).live_data.get(i).time.compareTo(consumptionArray.get(j).time)==0){
						comp = true;
					};
				}
				if(comp==true){
					num = i;
				}
			}
			
			if(num!=buf1-1){
				for(int i=num+1; i<buf1; i++){
					consumptionArray.add(liveData.get(0).live_data.get(i));
				}
			}
			
			//System.out.println(num);
			
			if(consumptionArray.size()>MAX_LIVE_DATA_SIZE){
				consumptionArray.remove(0);
			}
			
			//System.out.println("Counter: "+consumptionArray.size()+" "+liveData.get(0).live_data.size());
			
			
		}
		
	
		//System.out.println(consumptionArray);
		
		return consumptionArray;
		
	}
	
	public float getCurrentConsumption() {
		
		ArrayList<LiveDataEntry> data_ac = getLiveBuffer(MONITORS.ac_load_net);
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
		
		if(data_ac.size()>0){
			return data_pv.get(data_pv.size()-1).power+data_ac.get(data_ac.size()-1).power;
		}
		return 0;
	}
	
	public float getCurrentProduction() {
		
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
				
		if(data_pv.size()>0){
			return data_pv.get(data_pv.size()-1).power;
		}
		return 0;

	}
	
	public float getCurrentHotWater() {
		
		ArrayList<LiveDataEntry> data_hw = getLiveBuffer(MONITORS.load_hot_water);
		
		if(data_hw.size()>0){
			return data_hw.get(data_hw.size()-1).power;
		}
		return 0;
	}
	
	public float getCurrentChangeInConsumption() {
		
		ArrayList<LiveDataEntry> data_ac = getLiveBuffer(MONITORS.ac_load_net);
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
		
		//System.out.println(data_pv.get(data_pv.size()-1).power);
		//System.out.println(data_ac.get(data_ac.size()-1).power);
		//System.out.println(data_pv.get(data_pv.size()-2).power);
		//System.out.println(data_ac.get(data_ac.size()-2).power);
		
		if(data_ac.size()>=2){
			return (data_pv.get(data_pv.size()-1).power+data_ac.get(data_ac.size()-1).power)-(data_pv.get(data_pv.size()-2).power+data_ac.get(data_ac.size()-2).power);
		}
		return 0;
		
	}
	
	public float getCurrentChangeInProduction(){
		
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
		
		if(data_pv.size()>=2){
			return data_pv.get(data_pv.size()-1).power-data_pv.get(data_pv.size()-2).power;
		}
		return 0;

	}
	
	public float getCurrentChangeInHotWater(){
		
		ArrayList<LiveDataEntry> data_hw = getLiveBuffer(MONITORS.load_hot_water);
		
		if(data_hw.size()>=2){
			return data_hw.get(data_hw.size()-1).power-data_hw.get(data_hw.size()-2).power;
		}
		return 0;
		
	}
	
	public float getMaxInConsumption(){
		
		ArrayList<LiveDataEntry> data_ac = getLiveBuffer(MONITORS.ac_load_net);
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
		
		float max_consumed = 0;
		if(data_ac.size() == data_pv.size()){
			
			for(int i=0; i<data_ac.size(); i++){
				if((data_pv.get(i).power+data_ac.get(i).power)>max_consumed){
					max_consumed = data_pv.get(i).power+data_ac.get(i).power;
					//System.out.println(i);
				}
			}
			
		}
		
		return max_consumed;
	}
	
	public float getMaxInProduction(){
		
		ArrayList<LiveDataEntry> data_pv = getLiveBuffer(MONITORS.pv_site_net);
		
		float max_produced = 0;
		for(LiveDataEntry entry : data_pv){
			if(entry.power > max_produced){
				max_produced = entry.power;
			}
		}
		
		return max_produced;
	}
	
	public float getMaxInHotWater(){
		
		ArrayList<LiveDataEntry> data_hw = getLiveBuffer(MONITORS.load_hot_water);
		
		float max_hw = 0;
		for(LiveDataEntry entry : data_hw){
			if(entry.power > max_hw){
				max_hw = entry.power;
			}
		}
		
		return max_hw;
	}
	
	/*public float getMaxConsumedLive() {
		
		ArrayList<LiveDataEntry> data = getLiveConsumptionBuffer();
		
		float max_consumed = 0;
		for(LiveDataEntry entry : data){
			if(entry.power > max_consumed){
				max_consumed = entry.power;
			}
		}
		
		return max_consumed;
		
	}*/
	
	public ArrayList<LiveSiteDataEntry> getLiveSiteData() {
        
		//if(1==1){
		Date date = new Date();
		long timeStamp = date.getTime();
		
		if(live_site_data == null){
		
			JsonObject jsonObject = requestData("/live_site_data/"+Integer.toString(site_id)+"?last_hour=true&last_sync=false");
			JsonArray getArray = jsonObject.getAsJsonArray("data");
		
			Gson gson = new Gson();
			live_site_data = new ArrayList<LiveSiteDataEntry>();
        
			for(int i=0; i<getArray.size(); i++){
				live_site_data.add(gson.fromJson(getArray.get(i), LiveSiteDataEntry.class));
			}
			
			//update listeners
			System.out.println("update listeners");
			Object[] listeners = listenerList.getListenerList();

			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] == SolarListener.class) {
					((SolarListener) listeners[i + 1]).liveSiteDataChanged();
				}
			}
			
		}else if(lastUpdateLiveSiteData < timeStamp-(timeStamp%30000)) {
			
			lastUpdateLiveSiteData = timeStamp;
			
			JsonObject jsonObject = requestData("/live_site_data/"+Integer.toString(site_id)+"?last_six=true"+"&last_sync=false");
			JsonArray getArray = jsonObject.getAsJsonArray("data");
		
			Gson gson = new Gson();
			//live_site_data = new ArrayList<LiveSiteDataEntry>();
        
			for(int i=0; i<getArray.size(); i++){
				LiveSiteDataEntry entry = gson.fromJson(getArray.get(i), LiveSiteDataEntry.class);
				//System.out.println(entry.t_stamp);
				boolean contains = false;
				for(int j=live_site_data.size()-1; j>0; j--){
					if(live_site_data.get(j).t_stamp.equals(entry.t_stamp)){
						//System.out.println("true");
						contains = true;
					}	
				}
				if(!contains){
					live_site_data.add(entry);
				}
				
			}
			
			//update listeners
			System.out.println("update listeners");
			Object[] listeners = listenerList.getListenerList();

			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] == SolarListener.class) {
					((SolarListener) listeners[i + 1]).liveSiteDataChanged();
				}
			}
		}
		
		return live_site_data;
	}
	
	public float getCurrentCons(){
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		if(live_site_data_list.size()>0){
			return live_site_data_list.get(live_site_data_list.size()-1).cons;
		}
		return 0;
	}
	
	public float getCurrentGen(){
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		if(live_site_data_list.size()>0){
			return live_site_data_list.get(live_site_data_list.size()-1).gen;
		}
		return 0;
	}
	
	public float getMaxCons(){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		float max_consumed = 0;
		for(LiveSiteDataEntry entry : live_site_data_list){
			if(entry.cons > max_consumed){
				max_consumed = entry.cons;
			}
		}
		return max_consumed;
	}
	
	public float getMaxGen(){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		float max_produced = 0;
		for(LiveSiteDataEntry entry : live_site_data_list){
			if(entry.gen > max_produced){
				max_produced = entry.gen;
			}
		}
		return max_produced;
	}
	
	public float getMaxGen(int sample_size){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		int start = 0;
		if(live_site_data_list.size()-sample_size>0){
			start = live_site_data_list.size()-sample_size;
		}
		
		float max_produced = 0;
		for(int i=start; i<live_site_data_list.size(); i++){
			if(live_site_data_list.get(i).getGen() > max_produced){
				max_produced = live_site_data_list.get(i).getGen();
			}
		}
		return max_produced;
	}
	
	public float getMaxCons(int sample_size){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		int start = 0;
		if(live_site_data_list.size()-sample_size>0){
			start = live_site_data_list.size()-sample_size;
		}
		
		float max_consumed = 0;
		for(int i=start; i<live_site_data_list.size(); i++){
			if(live_site_data_list.get(i).getCons() > max_consumed){
				max_consumed = live_site_data_list.get(i).getCons();
			}
		}
		return max_consumed;
	}
	
	public float getMaxGenCons(int sample_size){
		
		float maxCons = getMaxCons(sample_size);
		float maxGen = getMaxGen(sample_size);
		
		if(maxCons>=maxGen){
			return maxCons;
		}else{
			return maxGen;
		}
	}
	
	public float getChangeCons(){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		if(live_site_data_list.size()>=2){
			return live_site_data_list.get(live_site_data_list.size()-1).cons-live_site_data_list.get(live_site_data_list.size()-2).cons;
		}
		return 0;
	}
	
	public float getChangeGen(){
		
		ArrayList<LiveSiteDataEntry> live_site_data_list = getLiveSiteData();
		
		if(live_site_data_list.size()>=2){
			return live_site_data_list.get(live_site_data_list.size()-1).gen-live_site_data_list.get(live_site_data_list.size()-2).gen;
		}
		return 0;
	}
}
