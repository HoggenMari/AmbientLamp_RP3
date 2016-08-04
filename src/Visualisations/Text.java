package Visualisations;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import Event.CarEvent;
import Event.CarListener;
import Event.SensorData;
import SolarAPI.SiteData;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;

public class Text implements CarListener {
	PGraphics canvas;
	PApplet applet;
	private SensorData sensorData;
	private SolarAnalyticsAPI api;

	float produced, consumed;
	ArrayList<PImage> imgList = new ArrayList<PImage>();

	public Text(PApplet a, SensorData sensorData, SolarAnalyticsAPI api, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = api;
		canvas = c;

		sensorData.addCarListener(this);
		
		for(int i=0; i<10; i++){
			imgList.add(applet.loadImage("char_"+i+".png"));
		}
		
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

		

		float change_consumption = api.getCurrentChangeInConsumption();
		float max_consumption = api.getMaxInConsumption();
		
		float change_water = api.getCurrentChangeInHotWater();
		float max_water = api.getMaxInHotWater();
		
		
	
		//if(change_consumption>0.1*max_consumption){
		//	change = true;
		//}else{
		//	change = false;
		//}
		
		//change = true;
		
		NumberFormat numberFormat = new DecimalFormat("0");
	    numberFormat.setRoundingMode(RoundingMode.DOWN);
		
		produced = api.getCurrentProduction()/10; 
		consumed = api.getCurrentConsumption()/10;
		
		String consumedStr = numberFormat.format(consumed);
		String producedStr = numberFormat.format(produced);
		
		canvas.beginDraw();
		canvas.noStroke();
		canvas.fill(0);
		canvas.rect(0, 0, canvas.width, canvas.height);
		//consumedStr = "20";
		
		//System.out.println(consumedStr);
		//System.out.println(producedStr);

		//canvas.image(imgList.get(0), 0, 0);
		
		for(int i=0; i<consumedStr.length(); i++){
			String a_char = ""+consumedStr.charAt(i);
			int num = Integer.parseInt(a_char);
			canvas.image(imgList.get(num), 13-5*((consumedStr.length()-1)-i), 0);
		}
		
		if(consumedStr.length()>2){
			canvas.fill(122,78,183,255);
			canvas.rect(7, 4, 1, 1);
		}else{
			canvas.fill(122,78,183,255);
			canvas.rect(17-consumedStr.length()*5, 4, 1, 1);
		}

		for(int i=0; i<producedStr.length(); i++){
			String a_char = ""+producedStr.charAt(i);
			int num = Integer.parseInt(a_char);
			canvas.image(imgList.get(num), 13-5*((producedStr.length()-1)-i), 7);
		} 
	
		if(producedStr.length()>2){
			canvas.fill(254,197,51,200);
			canvas.rect(7, 11, 1, 1);
		}else{
			canvas.fill(254,197,51,200);
			canvas.rect(17-producedStr.length()*5, 11, 1, 1);
		}
		
		//produced = api.getCurrentProduction(); //api.getLastSiteDataEntry().energy_generated;
		//consumed = api.getCurrentConsumption();
		
		//produced = 100;
		//consumed = 50;
		
		if(applet.frameCount%1800==0){
			produced = applet.random(0, 100);
			consumed = applet.random(0, 100);
		}
		
		
		
	
		

		canvas.endDraw();
		
		return canvas;
	}

	

	void setCharge(float c) {
		//System.out.println(c);
	}

	@Override
	public void carChanged(CarEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("BLLLAAAA");
		setCharge((float) (e.getCarValue() / 100.0));

	}
}
