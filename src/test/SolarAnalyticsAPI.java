package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
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

	String webPage = "https://portal.solaranalytics.com.au/api/v2";
	String name = "demo@solaranalytics.com.au";
	String password = "demo123";
	String token;
	long tokenTimeStamp;
	
	ArrayList<SiteData> siteData;
	
	public SolarAnalyticsAPI(){
		
		//token = requestSecureToken();
		requestData();
		//siteData = new ArrayList<SiteData>();
		
	}
		
	private JsonObject requestData(){
		
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
			
			url = new URL(webPage+"/site_data/140?tstart=20160501&tend=20160503&gran=day&raw=false&trunc=false");
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
			
			JsonArray getArray = jsonObject.getAsJsonArray("data");
			
			Gson gson = new Gson();
			//ArrayList<SiteData> siteDataEntries = new ArrayList<SiteData>();
			//ArrayList<SiteData> siteDataEntries = new ArrayList<SiteData>();
			siteData = new ArrayList<SiteData>();

			for(int i = 0; i < getArray.size(); i++)
			{
				//siteDataEntries.add(gson.fromJson(getArray.get(i), SiteData.class));
				siteData.add(gson.fromJson(getArray.get(i), SiteData.class));
			}
			
			System.out.println("SiteDataEntry Size: "+siteData.size());
			
			for(SiteData e:siteData){
				System.out.println(e.toString());
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
		  
		  System.out.println(classFields);
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
}
