package Visualisations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Event.SensorData;
import Event.Visual;
import Event.VisualEvent;
import Event.VisualListener;
import SolarAPI.*;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import processing.core.PApplet;
import processing.core.PGraphics;

public class BarGraphGenCons implements VisualListener, SolarListener {

	private PApplet applet;
	private SensorData sensorData;
	private PGraphics canvas;
	String visual_name = "Visual 4";
	int[] color;
	int lerp_color;
	
	private SolarAnalyticsAPI api;
	List<LiveSiteDataEntry> live_site_data;
	float highestValue;
	
	public BarGraphGenCons(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		sensorData.addVisualListener(this);
		this.api = SolarAnalyticsAPI.getInstance();
		api.addLiveDataListener(this);
		canvas = c;
		
		color = new int[]{ applet.color(45, 47, 48),
			    applet.color(244, 99, 97),
			    applet.color(244, 200, 97)};
		lerp_color = applet.lerpColor(color[1], color[2], 0.5f);
		
		live_site_data  = api.getLiveSiteData();
		highestValue = api.getMaxGenCons(120);
	}
	
	public PGraphics draw() {
				
		//System.out.println(highestValue);
		//System.out.println(data.size());

		
		canvas.beginDraw();
		canvas.background(0);
		canvas.fill(color[0]);
		canvas.noStroke();
		canvas.rect(0, 0, canvas.width, canvas.height);
		canvas.fill(color[1]);
		int start = 0;
		if(live_site_data.size()>17){
			start = live_site_data.size()-17;
		}
		for(int i=start; i<live_site_data.size(); i++){
			float cons = live_site_data.get(i).getCons();
			float gen = live_site_data.get(i).getGen();

			float val_cons = PApplet.map(cons, 0, highestValue, 0, 120);
			float val_gen = PApplet.map(gen, 0, highestValue, 0, 120);

			if(val_gen>=val_cons){
				canvas.fill(color[1]);
				canvas.rect((17-(live_site_data.size()-i))*10, 120-val_gen, 10, 120);
				canvas.fill(lerp_color);
				canvas.rect((17-(live_site_data.size()-i))*10, 120-val_cons, 10, 120);
			}else{
				canvas.fill(color[2]);
				canvas.rect((17-(live_site_data.size()-i))*10, 120-val_cons, 10, 120);
				canvas.fill(lerp_color);
				canvas.rect((17-(live_site_data.size()-i))*10, 120-val_gen, 10, 120);
			}
			//System.out.println(17-(data.size()-i)+" "+val);
			//canvas.rect((17-(data.size()-i))*10,0,(17-(data.size()-i+1))*10,val);
			//canvas.rect((17-(live_site_data.size()-i))*10, 120-val, 10, 120);
		}
		
		/*canvas.fill(color[2], 100);
		for(int i=start; i<live_site_data.size(); i++){
			float power = live_site_data.get(i).getGen();
			float val = applet.map(power, 0, highestValue, 0, 120);
			//System.out.println(17-(data.size()-i)+" "+val);
			//canvas.rect((17-(data.size()-i))*10,0,(17-(data.size()-i+1))*10,val);
			canvas.rect((17-(live_site_data.size()-i))*10, 120-val, 10, 120);
		}*/
		
		canvas.endDraw();
		
		return canvas;	
		
	}

	@Override
	public void liveSiteDataChanged() {
		// TODO Auto-generated method stub
		
		//live_site_data = api.getLiveSiteData();
		//highestValue = api.getMaxGenCons(120);
		//System.out.println(highestValue);
	}

	@Override
	public void visualsChanged(VisualEvent e) {
		// TODO Auto-generated method stub
		HashMap<String,Visual> vList = e.getVisualList();
		for (Visual value : vList.values()) {
			if(value.getName().equals(visual_name)){
				//System.out.println("Value = " + value.getColorsAsRGB().get(0));
				for(int i=0; i<value.getColorsAsRGB().size(); i++){
					int[] col = value.getColorsAsRGB().get(i);
					color[i] = applet.color(col[0], col[1], col[2]);
					lerp_color = applet.lerpColor(color[1], color[2], 0.5f);
				}
			}
		}
	}

}
