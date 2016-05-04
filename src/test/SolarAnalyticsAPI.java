package test;

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

public class SolarAnalyticsAPI implements SiteDataDao{

	enum GRAN
	{
		minute,
		hour,
		day,
		month,
		year
	}
	
	String webPage = "https://portal.solaranalytics.com.au/api/v2";
	String name = "demo@solaranalytics.com.au";
	String password = "demo123";
	int site_id = 140;
	String token;
	long tokenTimeStamp;
	long lastUpdate;
	
	ArrayList<SiteData> siteData;
	
	HashMap<String,ArrayList<SiteData>> sData = new HashMap();
	
	public SolarAnalyticsAPI(){
		
		//token = requestSecureToken();
		//requestData("day");
		//siteData = new ArrayList<SiteData>();
		
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
			
			List<String> list = verifyElement(jsonObject, SiteDataRaw.class);

			for(String s : list){
				System.out.println(s);
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
		      System.out.println("unknown Field");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getYear(int gran) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SiteData getMonth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getMonth(int gran) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getMonth(int month, int year) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getMonth(int month, int year, int gran) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SiteData getDay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getDay(GRAN value) {		
		
		Date date = new Date();
		long timeStamp = date.getTime();
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(date);
		//System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
		
		

		String hash = dateString+value;
	    ArrayList<SiteData> siteDataList = (ArrayList<SiteData>)sData.get(hash);
	    
	    long before = System.nanoTime();
	    //System.out.println("BEFORE: "+System.nanoTime());
	    
	    //System.out.println(timeStamp-(timeStamp%60000));
	    
	    if(siteDataList == null) {
	    	
	    	lastUpdate = timeStamp;
	    	siteDataList = new ArrayList<SiteData>();
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+dateString+"&tend="+dateString+"&gran="+value);
			
			System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+dateString+"&tend="+dateString+"&gran="+value);
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			Gson gson = new Gson();
			siteData = new ArrayList<SiteData>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
	        sData.put(hash, siteData);
	        //System.out.println("Creating circle of color : " + hash);
	        
	    }else if(lastUpdate < timeStamp-(timeStamp%305000)) {
	    	
	    	System.out.println("NewUpdate: "+timeStamp+" "+lastUpdate);
	    	
	    	lastUpdate = timeStamp;
	    	sData.remove(hash);
	    	siteDataList = new ArrayList<SiteData>();
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+dateString+"&tend="+dateString+"&gran="+value);
			
			//System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+tStart+"&tend="+tEnd+"&gran="+value);
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			Gson gson = new Gson();
			siteData = new ArrayList<SiteData>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
	        sData.put(hash, siteData);
	        //System.out.println("Creating circle of color : " + hash);
	    	
	    }
	    
	    //System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    //System.out.println(sData.get(hash).toString());

		//for(int i = 0; i < getArray.size(); i++)
		//{
			//siteDataEntries.add(gson.fromJson(getArray.get(i), SiteData.class));
		//}
		
		//System.out.println("SiteDataEntry Size: "+siteData.size());
		
		//for(SiteData e:siteData){
		//	System.out.println(e.toString());
		//}

		return null;
	}

	@Override
	public SiteData getDay(int day, int month, int year) {
		// TODO Auto-generated method stub
		String tStart = Integer.toString(year)+Integer.toString(month)+Integer.toString(day);
		return null;
	}

	@Override
	public List<SiteData> getDay(int day, int month, int year, GRAN value) {

		String dayS;
		if(day<10){
			dayS = "0"+Integer.toString(day);
		}else{
			dayS = Integer.toString(day);	
		}
		String monthS;
		if(month<10){
			monthS = "0"+Integer.toString(month);
		}else{
			monthS = Integer.toString(month);	
		}
		String yearS = Integer.toString(year);
		String tStart = yearS+monthS+dayS;
		String tEnd = yearS+monthS+dayS;

		String hash = dayS+monthS+yearS+value;
	    ArrayList<SiteData> siteDataList = (ArrayList<SiteData>)sData.get(hash);
	    
	    long before = System.nanoTime();
	    //System.out.println("BEFORE: "+System.nanoTime());
	    
	    if(siteDataList == null) {
	    	siteDataList = new ArrayList<SiteData>();
	        
	        JsonObject jsonObject = requestData("/site_data/"+Integer.toString(site_id)+"?tstart="+tStart+"&tend="+tEnd+"&gran="+value);
			
			//System.out.println("/site_data/"+Integer.toString(site_id)+"?tstart="+tStart+"&tend="+tEnd+"&gran="+value);
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			siteData = new ArrayList<SiteData>();
	        
			for(int i=0; i<getArray.size(); i++){
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
	        sData.put(hash, siteData);
	        System.out.println("Creating circle of color : " + hash);
	    }
	    
	    System.out.println("AFTER:  "+(System.nanoTime()-before));
	    
	    System.out.println(sData.get(hash).toString());

		//for(int i = 0; i < getArray.size(); i++)
		//{
			//siteDataEntries.add(gson.fromJson(getArray.get(i), SiteData.class));
		//}
		
		//System.out.println("SiteDataEntry Size: "+siteData.size());
		
		//for(SiteData e:siteData){
		//	System.out.println(e.toString());
		//}

		return null;
	}

	@Override
	public List<SiteData> getIntervall(Date tStart, Date tEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiteData> getIntervall(Date tStart, Date tEnd, int gran) {
		// TODO Auto-generated method stub
		return null;
	}
}
