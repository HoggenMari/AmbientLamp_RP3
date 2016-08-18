package Sketch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import DDPClient.DDPClient;
import Event.SensorData;
import Event.SensorEvent;
import Event.SensorListener;
import Event.Visual;
import FenoDMX.Screen;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import Visualisations.BarGraph;
import Visualisations.Circle;
import Visualisations.Text;
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
	private Circle circle;
	private SensorData sensorData;
	private BarGraph bargraph;
	private Text text;
	boolean textBol = true;
	
	ArrayList<Visual> visualList;
	
	public static void main(final String... args){
    	
		PApplet.main(new String[] { "--present", "Sketch.Main" });
    	    	
    	/*while(1==1){
    		api.getDay();
    	}*/
    }
	
	public void setup() {
		
		sensorData = SensorData.getInstance();
    	
    	sensorData.addSensorListener(this);
    	//SensorData.getInstance().addSensorListener(this);
    	
		size(100,100);
		screen = new Screen(this, 17, 12, 1);
		pSend = createGraphics(17,12,P2D);
		
    	api = new SolarAnalyticsAPI();
		voltage = new Voltage(this, sensorData, api, createGraphics(85, 60, P2D));
		circle = new Circle(this, sensorData, api, createGraphics(85, 60, P2D));

		bargraph = new BarGraph(this, sensorData, api, createGraphics(170, 120, P2D));
		text = new Text(this, sensorData, api, createGraphics(17, 12, P2D));

		visualList = new ArrayList<Visual>();
		
		visualList.add(new Visual("Circle", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));
		visualList.add(new Visual("Powerfield", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));

		client = new DDPClient("localhost", 3000);
    	client.connect();
    	
		/*try {
			HttpResponse<JsonNode> response = Unirest.get("https://portal.solaranalytics.com.au/api/v2/token").
					basicAuth("demo@solaranalytics.com.au","demo123").
					asJson();
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
    	delay(2000);
	}
	
	public void draw() {
		
		//frameRate(1);
		//background(255,0,0);
		
		//System.out.println(currentBrightness+" "+brightness);
		
		//PGraphics pg = voltage.draw();
		//PGraphics pg_small = downscale(pg,3);
		
		//PGraphics pg = bargraph.draw();
		//PGraphics pg_small = downscale(pg,3);
		
		voltage.fake = true;
		//sensorData.setCar(100);
		//System.out.println(frameRate);
		
		pSend.beginDraw();
		pSend.noStroke();
		pSend.fill(255,255,255);
		pSend.rect(0, 0, pSend.width, pSend.height);
			//pSend.fill(0,0,0);
			//pSend.rect(0, 0, pSend.width, pSend.height);
	    pSend.image(downscale(circle.draw(), 3),0,0);
			//pSend.image(text.draw(),0,0);
		
		//pSend.rect(0, 0, pSend.width, pSend.height);
		if(!textBol){
			pSend.image(text.draw(),0,0);
		}
		//image(text.draw(),0,0);
	    //}
		
		pSend.fill(0,currentBrightness);

		pSend.rect(0, 0, pSend.width, pSend.height);

		//image(text.draw(),0,0);
		
		//pSend.image(pg_small, 0, 0);
		
		//pSend.background(frameCount%255);
		//pSend.fill(255,255,255);
		//pSend.rect(1, 1, 3, 1);
		//pSend.fill(0,0,0);
		//pSend.rect(1, 2, 1, 1);
		//pSend.fill(0,0,0);
		//pSend.rect(1, 1, 1, 1);
		//pSend.fill(255,255,255);
		//pSend.rect(1, 3, 1, 1);
		//pSend.fill(255,255,255);
		//pSend.rect(2, 2, 1, 2);
		//pSend.fill(0,0,0);
		//pSend.rect(10, 5, 1, 2);
		pSend.endDraw();
		
		if(currentBrightness<brightness){
			currentBrightness++;
		}else if(currentBrightness>brightness){
			currentBrightness--;
		}
		
		//System.out.println("BRIGHTNESS: "+currentBrightness);
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			//System.out.println(frameRate);
			screen.drawOnGui();
		}
		screen.send(9, 8, 0, 0, 0);
		
		//image(pg,0,0);
		
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(0).watt_device_id);
		//System.out.println("AC: "+api.getLastEntry(MONITORS.ac_load_net).power);
		//System.out.println("PC: "+api.getLastEntry(MONITORS.pv_site_net).power);
		//System.out.println("HW: "+api.getLastEntry(MONITORS.load_hot_water).power);
		
		
		//System.out.println(api.getDay().energy_generated);
		
		//api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.day, true);
		
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(0).monitors);
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(1).monitors);
		//System.out.println(api.getIntervall(new GregorianCalendar(), new GregorianCalendar(), GRAN.month, true).get(2).monitors);

		//api.getLastEntry(new GregorianCalendar(2016, 6, 30, 19, 5, 0), true);
		
		if(frameCount%1==0){
			
			//System.out.println("CHANGE: "+api.getCurrentChangeInConsumption());
			//System.out.println("MEAN: "+api.getMeanProducedWeekly(GRAN.day));
			//System.out.println("MAX PRODUCED: "+api.getMaxProducedWeekly(GRAN.minute));
			//System.out.println("MAX CONSUMED: "+api.getMaxConsumedWeekly(GRAN.minute));
			
			//api.getMaxConsumedWeekly(GRAN.minute);
			
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
		System.out.println("Brightness Changed");
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
	
	public void keyPressed() {
		if (key == '1') {
			voltage.fake = !voltage.fake;
		}else if(key == '2') {
			textBol = !textBol;
		}
	}

}
