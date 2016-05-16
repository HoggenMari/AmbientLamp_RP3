package DDPClient;

import java.util.Observable;
import java.util.Observer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import Event.SensorData;

/**
 * Sample observer class that simply prints any responses from the Meteor server to the console
 * @author peterkutrumbos
 *
 */
public class DdpClientObserver implements Observer {

	@Override
	public void update(Observable client, Object msg) {

		SensorData sensorData = SensorData.getInstance();
		
		if (msg instanceof String) {
			//System.out.println("Test: "+msg);
			
			JsonObject jsonObject;
			JsonParser jejpl = new JsonParser();
			JsonElement el = jejpl.parse((String) msg);
			jsonObject = el.getAsJsonObject();
			
			String property = "";
			
			if(jsonObject.has("msg")){
				property = jsonObject.get("msg").getAsString();
				
				if(jsonObject.has("id")){
					String id = jsonObject.get("id").getAsString();

					if(property.equals("changed")){
						JsonObject fields = jsonObject.getAsJsonObject("fields");
					
						if(id.equals("XTzsWbFMpabBcvwjs")){
							//System.out.println(fields.get("score").getAsFloat());
							sensorData.setBrightness((float)(fields.get("score").getAsFloat()/100.0));
						}
					}
				}
			}
			/*try {
			  property = jsonObject.get("collection").getAsString();
			} catch (JsonParseException e) {
				
			}*/
		
			//System.out.println(property);

		}
		
	}

}
