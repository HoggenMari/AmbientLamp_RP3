package SolarAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAPI {

	private static WeatherAPI instance;
	
	String key = "8764b5448236b671";
	String location = "Penrith";

	long timeStamp;
	String forecast_array[];
	
	
	String weather_list[] = {
	"chanceflurries",
	"chancerain",
	"chancesleet",
	"chancesnow",
	"chancetstorms",
	"clear",
	"cloudy",
	"flurries",
	"fog",
	"hazy",
	"mostlycloudy",
	"mostlysunny",
	"partlycloudy",
	"partlysunny",
	"rain",
	"sleet",
	"snow",
	"sunny",	
	"tstorms",
	"unknown",
	"nt_chanceflurries",
	"nt_chancerain",
	"nt_chancesleet",
	"nt_chancesnow",
	"nt_chancetstorms",
	"nt_clear",
	"nt_cloudy",
	"nt_flurries",
	"nt_fog",
	"nt_hazy",
	"nt_mostlycloudy",
	"nt_mostlysunny",
	"nt_partlycloudy",
	"nt_partlysunny",
	"nt_rain",
	"nt_sleet",
	"nt_snow",
	"nt_sunny",	
	"nt_tstorms",
	"nt_unknown" };
	
	private WeatherAPI () {
		
		requestData();
		
	}
	
	public static synchronized WeatherAPI getInstance() {
		if (WeatherAPI.instance == null) {
			WeatherAPI.instance = new WeatherAPI ();
		}
		return WeatherAPI.instance;
    }
	
	public void requestData(){
		
		Date date = new Date();
		long currentTimeStamp = date.getTime();
		
		if(currentTimeStamp-timeStamp>=3600000){
			
			timeStamp = currentTimeStamp;
			
			URL url;
			try {
				
				//url = new URL(webPage+"/site_data/140?tstart=20160504&tend=20160504&gran="+gran+"&raw=false&trunc=false");
				url = new URL("http://api.wunderground.com/api/"+key+"/forecast/q/AU/"+location+".json");
				System.out.println("URL: "+url);
				URLConnection urlConnection = url.openConnection();
				//urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
				InputStream is = urlConnection.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				
				int numCharsRead;
				char[] charArray = new char[1024];
				StringBuffer sb = new StringBuffer();
				while ((numCharsRead = isr.read(charArray)) > 0) {
					sb.append(charArray, 0, numCharsRead);
				}
				String result = sb.toString();

				//System.out.println(result);
				
				JsonObject jsonObject;
				JsonParser jejpl = new JsonParser();
				
				JsonElement el = jejpl.parse(result);
				jsonObject = el.getAsJsonObject();
				
				//Verify Json with Class
				//List<String> list = verifyElement(jsonObject, LiveData.class);
							
				//JsonArray getArray = jsonObject.getAsJsonArray("response");
				
				JsonArray forecast = jsonObject.get("forecast").getAsJsonObject().get("txt_forecast").getAsJsonObject().get("forecastday").getAsJsonArray();
				//JsonObject forecastJsonObject = forecast.getAsJsonObject();
				
				forecast_array = new String[forecast.size()];
				for(int i=0; i<forecast.size(); i++){
					//siteDataRaw.add(gson.fromJson(getArray.get(i), SiteDataRaw.class));
					//System.out.println("Array: "+i+" "+forecast.get(i).getAsJsonObject().get("icon"));
					forecast_array[i] = forecast.get(i).getAsJsonObject().get("icon").getAsString();
				}
				
				//System.out.println("sb"+forecast);

				//return jsonObject;
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} /*catch (NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		}
	}
	
	public String[] getForecastArray(){
		requestData();
		return forecast_array;	
	}
}
