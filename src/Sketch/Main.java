package Sketch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import DDPClient.DDPClient;
import Event.GeniusEvent;
import Event.GeniusListener;
import Event.SensorData;
import Event.SensorEvent;
import Event.SensorListener;
import Event.Visual;
import Event.VisualEvent;
import Event.VisualListener;
import FenoDMX.Screen;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;
import Visualisations.BarGraph;
import Visualisations.BarGraphGenCons;
import Visualisations.Circle;
import Visualisations.Text;
import Visualisations.Voltage;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Main extends PApplet implements SensorListener, VisualListener, GeniusListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Screen screen;
	PGraphics pSend, canvasFade;
	DDPClient client;
	int brightness;
	int currentBrightness;
	SolarAnalyticsAPI api;
	
	private Voltage voltage;
	private Circle circle;
	private SensorData sensorData;
	private BarGraph bargraph;
	private BarGraphGenCons bargraph_gencons;

	private Text text;
	boolean textBol = true;
	
	ArrayList<Visual> visualList;
	int activeVisual = 0;
	
	// -------FADE
	float fade = 1;
	int next = 0;
	int active = 0;
	// -------FADE
	
	public static void main(final String... args){
    	
		PApplet.main(new String[] { "--present", "Sketch.Main" });
    	    	
    	/*while(1==1){
    		api.getDay();
    	}*/
    }
	
	public void setup() {
		
		sensorData = SensorData.getInstance();
    	
    	sensorData.addSensorListener(this);
    	sensorData.addVisualListener(this);
    	sensorData.addGeniusListener(this);
    	
		size(100,100);
		screen = new Screen(this, 17, 12, 1);
		pSend = createGraphics(17,12,P2D);
		canvasFade = createGraphics(17,12,P2D);

    	api = SolarAnalyticsAPI.getInstance();
    	api.start();
    	
		voltage = new Voltage(this, sensorData, createGraphics(85, 60, P2D));
		//circle = new Circle(this, sensorData, api, createGraphics(85, 60, P2D));

		bargraph = new BarGraph(this, sensorData, createGraphics(170, 120, P2D));
		bargraph_gencons = new BarGraphGenCons(this, sensorData, createGraphics(170, 120, P2D));
		text = new Text(this, sensorData, createGraphics(17, 12, P2D));

		visualList = new ArrayList<Visual>();
		
		//visualList.add(new Visual("Circle", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));
		//visualList.add(new Visual("Powerfield", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));

		client = new DDPClient("localhost", 3000);
    	client.connect();
    	
    	delay(2000);
	}
	
	public void draw() {
		
		// -------FADE
		if (fade < 1 && next != active) {
			fade += 0.01f;
			canvasFade = fade(drawMode(active), drawMode(next), fade);
			// System.out.println("Fade: " + fade);
		} else {
			active = next;
			canvasFade = drawMode(active);
			//System.out.println("NoFade: " + fade);

		}

		//pSend = downscale(canvasFade, 0);
		
			
		//System.out.println("test");
		pSend.beginDraw();
		pSend.noStroke();
	    pSend.image(downscale(canvasFade, 0),0,0);
		pSend.fill(0,currentBrightness);
		pSend.rect(0,0,pSend.width,pSend.height);
		
		pSend.endDraw();
		
		if(currentBrightness<brightness){
			currentBrightness++;
		}else if(currentBrightness>brightness){
			currentBrightness--;
		}
		
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			screen.drawOnGui();
		}
		screen.send(9,8,0,0,0);
		
	}

	// -------FADE
	public PGraphics drawMode(int mode) {
		switch (mode) {
		case 0:
			return downscale(voltage.draw(), 3);
		case 1:
			return text.draw();
		case 2:
			return downscale(bargraph.draw(), 0);
		case 3:
			return downscale(bargraph_gencons.draw(), 3);
		default:
			return voltage.draw();
		}
	}

		// -------FADE

		// -------FADE
		public PGraphics fade(PGraphics a, PGraphics b, float fade) { // 0 < fade <
																		// 1
			PGraphics c = createGraphics(17, 12, P2D);
			c.beginDraw();
			c.background(0);
			c.noStroke();
			c.fill(0, 255 * fade);
			c.image(a, 0 + (int) (17 * fade), 0);
			c.rect(0 + (int) (17 * fade),0, 17, 12);
		    c.fill(0, 255 - 255 * fade);
			c.image(b, -17 + (int) (17 * fade), 0);
			c.rect(-17 + (int) (17 * fade),0, 17, 12);
			c.endDraw();
			return c;
		}

		// -------FADE

	
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

	@Override
	public void visualsChanged(VisualEvent e) {
		// TODO Auto-generated method stub
		HashMap<String,Visual> vList = e.getVisualList();
		int i=0;
		for (Visual value : vList.values()) {
			//System.out.println(value);
			if(value.isActive()){
				System.out.println(value.getName());
				activeVisual = value.getIndex();
				next = value.getIndex();
				fade = 0;
				break;
			}
			i++;
		}
		System.out.print(activeVisual);
	}

	@Override
	public void geniusModeChanged(GeniusEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e.getGenius());
	}
}
