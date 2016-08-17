package DDPClient;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import Event.SensorData;
import Event.Visual;

/**
 * Sample observer class that simply prints any responses from the Meteor server to the console
 * @author peterkutrumbos
 *
 */
public class DdpClientObserver implements Observer {

	HashMap<String,Visual> visuals = new HashMap<String,Visual>();

	@Override
	public void update(Observable client, Object msg) {

		SensorData sensorData = SensorData.getInstance();
		
		if (msg instanceof String) {
			System.out.println("Test: "+msg);
			
			JsonObject jsonObject;
			JsonParser jejpl = new JsonParser();
			JsonElement el = jejpl.parse((String) msg);
			jsonObject = el.getAsJsonObject();
			
			String property = "";
			
			if(jsonObject.has("msg")){
				
				String collection = jsonObject.get("collection").getAsString();
						
				if(collection.equals("visuals")){
					
					String message = jsonObject.get("msg").getAsString();
					
					String id = jsonObject.get("id").getAsString();
					
					if(message.equals("added")){
						System.out.println("added");
						
						JsonObject fields = jsonObject.getAsJsonObject("fields");
						System.out.println(fields);
						
						String name = fields.get("name").getAsString();
						System.out.println(name);
						
						boolean checked = fields.get("checked").getAsBoolean();
						System.out.println(checked);
						
						boolean active = fields.get("active").getAsBoolean();
						System.out.println(active);
						
						JsonArray colors = fields.get("colors").getAsJsonArray();
						System.out.println(colors.size());
						
						String[] colorsAsString = new String[colors.size()];
						for(int i=0; i<colors.size(); i++){
							JsonObject color = colors.get(i).getAsJsonObject();
							colorsAsString[i] = color.get("color").getAsString();
						}
						
						Visual visual = new Visual(id, name, colorsAsString, checked, active);
						visuals.put(id, visual);
						
						System.out.println("VISUAL"+visuals.size());
						
					}else if(message.equals("changed")){
						System.out.println("changed");
						
						JsonObject fields = jsonObject.getAsJsonObject("fields");
						System.out.println(fields);
												
						if(fields.has("colors")){
							JsonArray colors = fields.get("colors").getAsJsonArray();
							System.out.println(colors.size());
							
							String[] colorsAsString = new String[colors.size()];
							for(int i=0; i<colors.size(); i++){
								JsonObject color = colors.get(i).getAsJsonObject();
								colorsAsString[i] = color.get("color").getAsString();
							}
						
							//Visual visual = new Visual("test", "Visual 5", colorsAsString, true, true);
							visuals.get(id).setColors(colorsAsString);
						
						}
						
						
						System.out.println("VISUAL"+visuals.size());

					}
					
					
					
				}
				
				
				
				property = jsonObject.get("msg").getAsString();
				
				if(jsonObject.has("id")){
					String id = jsonObject.get("id").getAsString();

					if(property.equals("changed")){
						JsonObject fields = jsonObject.getAsJsonObject("fields");
					
						if(id.equals(sensorData.getBrightnessID())){
							System.out.println(fields.get("score").getAsFloat());
							sensorData.setBrightness((float)(fields.get("score").getAsFloat()/100.0));
						}
					}else if(property.equals("added")){
						JsonObject fields = jsonObject.getAsJsonObject("fields");
						
						String name = fields.get("name").getAsString();
						
						if(name.equals("Brightness")){
							System.out.println("Brightness: "+id);
							sensorData.setBrightnessID(id);
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
