package DDPClient;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Event.SensorData;
import Event.Visual;
import Event.VisualEvent;
import me.kutrumbos.DdpClient;
import me.kutrumbos.examples.SimpleDdpClientObserver;

/**
 * Simple example of DDP client use-case that just involves 
 * 		making a connection to a locally hosted Meteor server
 * @author peterkutrumbos
 *
 */
public class DDPClient implements Observer {
	
	private String meteorIp;
	private int meteorPort;
	DdpClient ddp = null;

	public DDPClient(String meteorIp, int meteorPort){
		
		//System.out.println("Test");
		
		this.meteorIp = meteorIp;
		this.meteorPort = meteorPort;
				
	}

	public boolean connect() {
		
		//System.out.println("Test");

		ddp = null;
				
		try {
			
			// create DDP client instance
			ddp = new DdpClient(meteorIp, meteorPort);
			
			// create DDP client observer
			Observer obs = new DdpClientObserver();
			
			// add observer
			ddp.addObserver(this);
						
		}catch (URISyntaxException e) {
			e.printStackTrace();
		}
			// make connection to Meteor server
		
		do {
		ddp.connect();
		} while(ddp.getReadyState()==3);
		return false;
			
		    /*while(true) {
				try {
					Thread.sleep(5000);

					System.out.println("calling remote method...");
					
					Object[] methodArgs = new Object[1];
					methodArgs[0] = new String("{\"name\":\"peter andersson\", \"phone\":\"12345678\"}");
					ddp.call("update", methodArgs);

				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
		    }*/
	}

	public void call(String s) {
		Object[] methodArgs = new Object[1];
		methodArgs[0] = s;
		ddp.call("update", methodArgs);
	}
	

	@Override
	public void update(Observable client, Object msg) {

		//System.out.println("TEST: "+msg);
		
		SensorData sensorData = SensorData.getInstance();
		
		if (msg instanceof String) {
					
			//System.out.println("Test: "+msg);
			
			JsonObject jsonObject;
			JsonParser jejpl = new JsonParser();
			JsonElement el = jejpl.parse((String) msg);
			jsonObject = el.getAsJsonObject();
			
			String property = "";
			
			if(jsonObject.has("code")){
				//System.out.println("code: "+jsonObject.get("code").getAsString());
				connect();
			}
			
			if(jsonObject.has("msg") && jsonObject.has("collection")){
				
				String collection = jsonObject.get("collection").getAsString();
						
				if(collection.equals("visuals")){
					
					String message = jsonObject.get("msg").getAsString();
					
					String id = jsonObject.get("id").getAsString();
					
					if(message.equals("added")){
						//System.out.println("added");
						
						JsonObject fields = jsonObject.getAsJsonObject("fields");
						//System.out.println(fields);
						
						String name = fields.get("name").getAsString();
						//System.out.println(name);
						
						int index = fields.get("index").getAsInt();
						
						boolean checked = fields.get("checked").getAsBoolean();
						//System.out.println(checked);
						
						boolean active = fields.get("active").getAsBoolean();
						//System.out.println(active);
						
						boolean geniusActive = fields.get("geniusActive").getAsBoolean();
						//System.out.println(geniusActive);
						
						boolean pausedActive = fields.get("pausedActive").getAsBoolean();
						//System.out.println(pausedActive);
						
						boolean settingActive = fields.get("settingActive").getAsBoolean();
						//System.out.println(settingActive);
						
						JsonArray colors = fields.get("colors").getAsJsonArray();
						//System.out.println(colors.size());
						
						String[] colorsAsString = new String[colors.size()];
						for(int i=0; i<colors.size(); i++){
							JsonObject color = colors.get(i).getAsJsonObject();
							colorsAsString[i] = color.get("color").getAsString();
						}
						
						Visual visual;
						if(fields.has("notification")){
							boolean notification = fields.get("notification").getAsBoolean();
							visual = new Visual(id, name, index, colorsAsString, checked, active, geniusActive, pausedActive, settingActive, notification);
						}else{
							visual = new Visual(id, name, index, colorsAsString, checked, active, geniusActive, pausedActive, settingActive);
						}
						
						sensorData.getVisualList().put(id, visual);

						sensorData.setVisual(VisualEvent.VISUAL_CHANGED);
						//System.out.println("VISUAL"+sensorData.getVisualList().size());
						
					}else if(message.equals("changed")){
						//System.out.println("changed");
						
						JsonObject fields = jsonObject.getAsJsonObject("fields");
						//System.out.println(fields);
												
						if(fields.has("colors")){
							JsonArray colors = fields.get("colors").getAsJsonArray();
							//System.out.println(colors.size());
							
							String[] colorsAsString = new String[colors.size()];
							for(int i=0; i<colors.size(); i++){
								JsonObject color = colors.get(i).getAsJsonObject();
								colorsAsString[i] = color.get("color").getAsString();
							}
						
							//Visual visual = new Visual("test", "Visual 5", colorsAsString, true, true);
							sensorData.getVisualList().get(id).setColors(colorsAsString);
							sensorData.setVisual(VisualEvent.VISUAL_COLORS);
						}else if(fields.has("checked")){
							boolean checked = fields.get("checked").getAsBoolean();
							//System.out.println(checked);
							
							sensorData.getVisualList().get(id).setChecked(checked);
							sensorData.setVisual(VisualEvent.VISUAL_CHECKED);
						}else if(fields.has("active")){
							boolean active = fields.get("active").getAsBoolean();
							//System.out.println(active);
							
							sensorData.getVisualList().get(id).setActive(active);
							sensorData.setVisual(VisualEvent.VISUAL_ACTIVE);

						}else if(fields.has("geniusActive")){
							boolean geniusActive = fields.get("geniusActive").getAsBoolean();
							//System.out.println(active);
							
							sensorData.getVisualList().get(id).setGeniusActive(geniusActive);
							sensorData.setVisual(VisualEvent.VISUAL_GENIUSACTIVE);

						}else if(fields.has("pausedActive")){
							boolean pausedActive = fields.get("pausedActive").getAsBoolean();
							//System.out.println("pausedActive: "+pausedActive);
							
							sensorData.getVisualList().get(id).setPausedActive(pausedActive);
							sensorData.setVisual(VisualEvent.VISUAL_PAUSEDACTIVE);

						}else if(fields.has("settingActive")){
							boolean settingActive = fields.get("settingActive").getAsBoolean();
							//System.out.println("Setting Active: "+settingActive);
							
							sensorData.getVisualList().get(id).setSettingActive(settingActive);
							sensorData.setVisual(VisualEvent.VISUAL_SETTINGACTIVE);

						}else if(fields.has("notification")){
							boolean notification = fields.get("notification").getAsBoolean();
							sensorData.getVisualList().get(id).setNotification(notification);
							sensorData.setVisual(VisualEvent.VISUAL_NOTIFICATION);
						}
						
						//System.out.println("VISUAL"+sensorData.getVisualList().size());

					}
										
				}else if(collection.equals("settings") && jsonObject.has("collection")){
				
					property = jsonObject.get("msg").getAsString();
				
					if(jsonObject.has("id")){
						String id = jsonObject.get("id").getAsString();

						if(property.equals("changed")){
							JsonObject fields = jsonObject.getAsJsonObject("fields");
					
							if(id.equals(sensorData.getBrightnessID())){
								//System.out.println(fields.get("score").getAsFloat());
								sensorData.setBrightness((float)(fields.get("score").getAsFloat()/100.0));
							}else if(id.equals(sensorData.getSaturationID())){
								//System.out.println(fields.get("score").getAsFloat());
								sensorData.setSaturation((float)(fields.get("score").getAsFloat()/100.0));
							}else if(id.equals(sensorData.getGeniusID())){
								if(fields.has("geniusActive")){
								//System.out.println(fields.get("geniusActive").getAsBoolean());
								sensorData.setGenius(fields.get("geniusActive").getAsBoolean());
								}else if(fields.has("geniusPaused")){
								//System.out.println(fields.get("geniusPaused").getAsBoolean());
								sensorData.setGeniusPaused(fields.get("geniusPaused").getAsBoolean());	
								}else if(fields.has("settingActive")){
								//System.out.println(fields.get("settingActive").getAsBoolean());
								}
							}
							
						}else if(property.equals("added")){
							JsonObject fields = jsonObject.getAsJsonObject("fields");
						
							String name = fields.get("name").getAsString();
						
							if(name.equals("Brightness")){
								//System.out.println("Brightness: "+id);
								sensorData.setBrightnessID(id);
								sensorData.setBrightness((float)(fields.get("score").getAsFloat()/100.0));
							}else if(name.equals("Saturation")){
								//System.out.println("Brightness: "+id);
								sensorData.setSaturationID(id);
								sensorData.setSaturation((float)(fields.get("score").getAsFloat()/100.0));
							}else if(name.equals("Genius")){
								sensorData.setGeniusID(id);
								sensorData.setGenius(fields.get("geniusActive").getAsBoolean());
								sensorData.setGeniusPaused(fields.get("geniusPaused").getAsBoolean());
							}
						}
					}
				}
			}
		}	
	}
}
