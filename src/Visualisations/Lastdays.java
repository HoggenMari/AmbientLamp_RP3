package Visualisations;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import Event.SensorData;
import Event.Visual;
import Event.VisualEvent;
import Event.VisualListener;
import SolarAPI.LiveSiteDataEntry;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarListener;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Lastdays implements VisualListener, SolarListener {

	private PApplet applet;
	private SensorData sensorData;
	private PGraphics canvas;
	String visual_name = "Visual 4";
	int[] color;
	
	private SolarAnalyticsAPI api;
	List<LiveSiteDataEntry> live_site_data;
	float highestValue;
	
	float energy[] = new float[3];
	private int oldTimer;
	private int highest;
	
	public Lastdays(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		sensorData.addVisualListener(this);
		this.api = SolarAnalyticsAPI.getInstance();
		api.addLiveDataListener(this);
		canvas = c;
		
		color = new int[]{ applet.color(45, 47, 48),
			    applet.color(244, 99, 97),
			    applet.color(244, 99, 97) };
		
		live_site_data  = api.getLiveSiteData();
		highestValue = api.getMaxCons(120);
		
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		System.out.println(today.get(Calendar.DAY_OF_MONTH));
		
		System.out.println(api.getDay().energy_consumed);
		System.out.println(api.getDay(today.get(Calendar.DAY_OF_MONTH)-1, 10, 2016));
		
		highest = 0;
		for(int i=0; i<energy.length; i++){
			energy[i] = api.getDay(today.get(Calendar.DAY_OF_MONTH)-i, 10, 2016).energy_consumed;
			if(energy[i]>highest){
				highest = (int) energy[i];
			}
		}
		
		System.out.println("HIGHEST: "+highest);
	}
	
	public PGraphics draw() {
		
		//System.out.println(applet.millis());
		
		if(applet.millis() - oldTimer > 300000){
		GregorianCalendar today = (GregorianCalendar) GregorianCalendar.getInstance();
		//System.out.println(today.get(Calendar.DAY_OF_MONTH));
		
		//System.out.println(api.getDay().energy_consumed);
		//System.out.println(api.getDay(today.get(Calendar.DAY_OF_MONTH)-1, 10, 2016));
		int highest = 0;
		for(int i=0; i<energy.length; i++){
			energy[i] = api.getDay(today.get(Calendar.DAY_OF_MONTH)-i, 10, 2016).energy_consumed;
			System.out.println(energy[i]);
			if(energy[i]>highest){
				highest = (int) energy[i];
			}
		}		
		oldTimer = applet.millis();
		
		
		}
		
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

		
		
	
		
		//System.out.println(highestValue);
		//System.out.println(data.size());

		
		canvas.beginDraw();
		canvas.background(0);
		canvas.fill(color[1]);
		canvas.noStroke();
		canvas.rect(0, 0, canvas.width, canvas.height);
		canvas.fill(color[0]);
		/*int start = 0;
		if(live_site_data.size()>17){
			start = live_site_data.size()-17;
		}
		for(int i=start; i<live_site_data.size(); i++){
			float power = live_site_data.get(i).getCons();
			float val = applet.map(power, 0, highestValue, 0, 120);
			//System.out.println(17-(data.size()-i)+" "+val);
			//canvas.rect((17-(data.size()-i))*10,0,(17-(data.size()-i+1))*10,val);
			canvas.rect((17-(live_site_data.size()-i))*10, 120-val, 10, 120);
		}*/
		
		
		for(int i=0; i<energy.length; i++){
			int val = (int) applet.map(energy[i], 0, highest, 0, 50);
			int val2 = (int) applet.map(energy[i], 0, highest, 0, 255);

			//System.out.println("VAL: "+i+" "+val);
			
			float c1 = color[0] >> 16 & 0xFF;
			float c2 = color[0] >> 8 & 0xFF;;
			float c3 = color[0] & 0xFF;
			
			canvas.fill(c1,c2,c3,val2);
			canvas.rect((2-i)*60+((50-val)/2),60-val/2,val,val);
		}
		
		canvas.endDraw();
		
		return canvas;	
		
	}

	@Override
	public void liveSiteDataChanged() {
		// TODO Auto-generated method stub
		
		live_site_data = api.getLiveSiteData();
		highestValue = api.getMaxCons(120);
		
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
				}
			}
		}
	}

}

