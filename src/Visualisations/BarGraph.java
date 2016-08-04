package Visualisations;

import java.util.ArrayList;
import java.util.List;

import Event.SensorData;
import SolarAPI.*;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import processing.core.PApplet;
import processing.core.PGraphics;

public class BarGraph {

	private PApplet applet;
	private SensorData sensorData;
	private PGraphics canvas;
	private SolarAnalyticsAPI api;
	
	public BarGraph(PApplet a, SensorData sensorData, SolarAnalyticsAPI api, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = api;
		canvas = c;
	}
	
	public PGraphics draw() {
		
		int[] c = { applet.color(24, 34, 43),
				    applet.color(41, 46, 49),
				    applet.color(68, 58, 46),
				    applet.color(91, 71, 60),
				    applet.color(135, 93, 62),
				    applet.color(142, 105, 53),
				    applet.color(120, 111, 94),
				    applet.color(116, 122, 136),
				    applet.color(123, 140, 162),
				    applet.color(167, 199, 222),
				    applet.color(165, 198, 221),
				    applet.color(168, 203, 226),
				    applet.color(172, 204, 226),
				    applet.color(158, 192, 217),
				    applet.color(130, 168, 196),
				    applet.color(123, 140, 163),
				    applet.color(119, 123, 135),
				    applet.color(127, 114, 94),
				    applet.color(145, 108, 56),
				    applet.color(141, 98, 63),
				    applet.color(100, 76, 58),
				    applet.color(76, 61, 46),
				    applet.color(45, 47, 48),
				    applet.color(25, 36, 42)

				    };

		
		List<LiveDataEntry> data = api.getLiveBuffer(MONITORS.ac_load_net);
		
		float highestValue = 0;
		for(LiveDataEntry entry : data){
			if(entry.power>highestValue){
				highestValue = entry.power;
			}
		}
		
		//System.out.println(highestValue);
		//System.out.println(data.size());

		
		canvas.beginDraw();
		canvas.background(0);
		canvas.fill(c[8]);
		canvas.noStroke();
		canvas.rect(0, 0, canvas.width, canvas.height);
		canvas.fill(120,50,20);
		int start = 0;
		if(data.size()>17){
			start = data.size()-17;
		}
		for(int i=start; i<data.size(); i++){
			float power = data.get(i).power;
			float val = applet.map(power, 0, highestValue, 0, 120);
			//System.out.println(17-(data.size()-i)+" "+val);
			//canvas.rect((17-(data.size()-i))*10,0,(17-(data.size()-i+1))*10,val);
			canvas.rect((17-(data.size()-i))*10, 120-val, 10, 120);
		}
		canvas.endDraw();
		
		return canvas;	
		
	}

}
