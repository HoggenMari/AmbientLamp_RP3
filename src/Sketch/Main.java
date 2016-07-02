package Sketch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import DDPClient.DDPClient;
import Event.SensorData;
import Event.SensorEvent;
import Event.SensorListener;
import FenoDMX.Screen;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import Visualisations.BarGraph;
import Visualisations.Voltage;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Main extends PApplet implements SensorListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Screen screen;
	PGraphics pSend;
	DDPClient client;
	int brightness;
	int currentBrightness;
	SolarAnalyticsAPI api;
	
	private Voltage voltage;
	private SensorData sensorData;
	private BarGraph bargraph;
	
	public static void main(final String... args){
    	
		PApplet.main(new String[] { "--present", "Sketch.Main" });
    	    	
    	/*while(1==1){
    		api.getDay();
    	}*/
    }
	
	public void setup() {
		
		sensorData = SensorData.getInstance();
		
		client = new DDPClient("193.168.0.100", 3000);
    	client.connect();
    	
    	SensorData.getInstance().addSensorListener(this);
    	
		size(100,100);
		screen = new Screen(this, 17, 12);
		pSend = createGraphics(17,12,P2D);
		
    	api = new SolarAnalyticsAPI();
		voltage = new Voltage(this, sensorData, api, createGraphics(170, 120, P2D));
		bargraph = new BarGraph(this, sensorData, api, createGraphics(170, 120, P2D));

    	delay(2000);
	}
	
	public void draw() {
		
		//frameRate(1);
		//background(255,0,0);
		
		//System.out.println(currentBrightness+" "+brightness);
		
		//PGraphics pg = voltage.draw();
		//PGraphics pg_small = downscale(pg,3);
		
		PGraphics pg = bargraph.draw();
		PGraphics pg_small = downscale(pg,3);
		
		sensorData.setCar(100);
		
		
		pSend.beginDraw();
		pSend.noStroke();
		pSend.fill(200,200,200);
		pSend.rect(0, 0, pSend.width, pSend.height);
		pSend.fill(0,currentBrightness);
		pSend.rect(0, 0, pSend.width, pSend.height);
		pSend.image(pg_small,0,0);
		pSend.endDraw();
		
		if(currentBrightness<brightness){
			currentBrightness++;
		}else if(currentBrightness>brightness){
			currentBrightness--;
		}
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			//System.out.println(frameRate);
			screen.drawOnGui();
		}
		screen.send(9, 8, 8, 8, 8);
		
		image(pg,0,0);
		
		if(frameCount%1000==0){
			System.out.println(frameRate);
		}
		
		//System.out.println(api.getDay().energy_generated);
		
		//api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.day, true);
		
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(0).monitors);
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(1).monitors);
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(2).monitors);

		//api.getLastEntry(new GregorianCalendar(2016, 6, 30, 19, 5, 0), true);
		
		if(frameCount%1000==0){
			
			System.out.println("CHANGE: "+api.getCurrentChangeConsumption());
			System.out.println("MAX: "+api.getMaxConsumedLive());
			System.out.println("MEAN: "+api.getMeanProducedWeekly(GRAN.day));
			System.out.println("MAX PRODUCED: "+api.getMaxProducedWeekly(GRAN.minute));
			System.out.println("MAX CONSUMED: "+api.getMaxConsumedWeekly(GRAN.minute));
			

			//System.out.println(api.getLastSiteDataEntry());
			//System.out.println(api.getMaxConsumedWeekly(GRAN.minute));
			//System.out.println(api.getMaxProducedWeekly(GRAN.minute));

			//System.out.println(api.getMaxProducedWeekly(GRAN.minute));
			
			//System.out.println("LiveData: "+api.getLiveDataEntry(MONITORS.ac_load_net));
			//System.out.println("AC: "+api.getLastEntry(MONITORS.ac_load_net).power);
			//System.out.println("HOT_WATER: "+api.getLastEntry(MONITORS.load_hot_water).power);
			//System.out.println("PV: "+api.getLastEntry(MONITORS.pv_site_net).power);
		}
		
	}

	PGraphics downscale(PGraphics pg, int intensity) {
		PImage in = pg.get();
		in.filter(BLUR, intensity);
		in.resize(17, 12);
		PGraphics out = createGraphics(17, 12, P2D);
		out.image(in, 0, 0);
		return out;
	}

	
	@Override
	public void brightnessChanged(SensorEvent e) {
		//System.out.println("Brightness Changed");
		brightness = (int)(255.0-e.getBrightness()*255);
	}

	@Override
	public void particlesChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void speedChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		
	}

}
