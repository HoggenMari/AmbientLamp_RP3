package Visualisations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import Event.CarEvent;
import Event.CarListener;
import Event.SensorData;
import Event.Visual;
import Event.VisualEvent;
import Event.VisualListener;
import SolarAPI.SiteData;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import SolarAPI.SolarListener;

public class Text implements VisualListener, SolarListener {
	PGraphics canvas;
	PApplet applet;
	String visual_name = "Visual 2";
	int[] color;

	private SensorData sensorData;
	private SolarAnalyticsAPI api;

	float produced, consumed;
	ArrayList<PImage> imgList = new ArrayList<PImage>();

	public Text(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = SolarAnalyticsAPI.getInstance();
		canvas = c;
		
		color = new int[]{ applet.color(45, 47, 48),
			    applet.color(244, 99, 97),
			    applet.color(135, 96, 190),
			    applet.color(255,255,255),
			    applet.color(0,0,0) };
		
		for(int i=0; i<10; i++){
			imgList.add(applet.loadImage("char_"+i+".png"));
		}
		
		sensorData.addVisualListener(this);
		api.addLiveDataListener(this);

		produced = Math.abs(api.getCurrentGen()/10); 
		consumed = Math.abs(api.getCurrentCons()/10);
	}

	public PGraphics draw() {

		// if (charge + 0.03f < 100.0f)
		// setCharge(charge += 0.03f);
		
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
		
	
		//if(change_consumption>0.1*max_consumption){
		//	change = true;
		//}else{
		//	change = false;
		//}
		
		//change = true;
		
		NumberFormat numberFormat = new DecimalFormat("0");
	    numberFormat.setRoundingMode(RoundingMode.DOWN);
		
	    
		String consumedStr = numberFormat.format(Math.round(consumed*100)/100.0);
		String producedStr = numberFormat.format(Math.round(produced*100)/100.0);
		
		canvas.beginDraw();
		canvas.noStroke();
		canvas.fill(color[4]);
		canvas.rect(0, 0, canvas.width, canvas.height);
		//consumedStr = "20";
		
		//System.out.println(consumedStr);
		//System.out.println(producedStr);

		//canvas.image(imgList.get(0), 0, 0);
		
		for(int i=0; i<consumedStr.length(); i++){
			String a_char = ""+consumedStr.charAt(i);
			int num = Integer.parseInt(a_char);
			
			PGraphics pg = applet.createGraphics(4,5,applet.P2D);
			pg.beginDraw();
			pg.noStroke();
			pg.fill(color[1]);
			for(int x=0; x<imgList.get(num).width; x++){
				for(int y=0; y<imgList.get(num).height; y++){
					if(imgList.get(num).get(x, y) == -1){
						pg.rect(x,y,1,1);
					};
				}
			}
			pg.endDraw();
			canvas.image(pg, 13-5*((consumedStr.length()-1)-i), 0);
		}
		
		if(consumedStr.length()>2){
			canvas.fill(color[0]);//canvas.fill(122,78,183,255);
			canvas.rect(7, 4, 1, 1);
		}else{
			canvas.fill(color[0]);//canvas.fill(122,78,183,255);
			canvas.rect(17-consumedStr.length()*5, 4, 1, 1);
		}

		for(int i=0; i<producedStr.length(); i++){
			String a_char = ""+producedStr.charAt(i);
			int num = Integer.parseInt(a_char);
			//canvas.tint(255,0,0);
			
			PGraphics pg = applet.createGraphics(4,5,applet.P2D);
			pg.beginDraw();
			pg.noStroke();
			pg.fill(color[3]);
			for(int x=0; x<imgList.get(num).width; x++){
				for(int y=0; y<imgList.get(num).height; y++){
					if(imgList.get(num).get(x, y) == -1){
						pg.rect(x,y,1,1);
					};
				}
			}
			pg.endDraw();
			canvas.image(pg, 13-5*((producedStr.length()-1)-i), 7);
		} 
	
		if(producedStr.length()>2){
			canvas.fill(color[2]);//canvas.fill(254,197,51,200);
			canvas.rect(7, 11, 1, 1);
		}else{
			canvas.fill(color[2]);//canvas.fill(254,197,51,200);
			canvas.rect(17-producedStr.length()*5, 11, 1, 1);
		}
		
		//produced = api.getCurrentProduction(); //api.getLastSiteDataEntry().energy_generated;
		//consumed = api.getCurrentConsumption();
		
		//produced = 100;
		//consumed = 50;
		
		
		
	
		

		canvas.endDraw();
		
		return canvas;
	}

	

	void setCharge(float c) {
		//System.out.println(c);
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

	@Override
	public void liveSiteDataChanged() {
		// TODO Auto-generated method stub
		//System.out.println("Update For Text");
		
		produced = Math.abs(api.getCurrentGen()/10); 
		consumed = Math.abs(api.getCurrentCons()/10);
		//System.out.println("Changed: "+api.getChangeGen());
	}
}
