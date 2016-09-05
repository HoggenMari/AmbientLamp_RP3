package Sketch;

import java.awt.Color;
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
	int brightness, saturation;
	int currentBrightness, currentSaturation;
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

	//GENIUS
	boolean first = false;
	boolean geniusMode = false;
	boolean geniusFirst = true;
	private boolean geniusPaused = false;
	private boolean geniusPausedActive = false;
	private boolean settingActive = false;
	private boolean settingPausedActive = false;
	int geniusModeTimer = 0;
	boolean geniusModeTimerCalled = false;
	int GENIUS_TIME = 10000;
	int geniusActiveVisual = 0;
	int geniusCounter = 0;
	
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
		
		for(int ix=0; ix<pSend.width; ix++){
			for(int iy=0; iy<pSend.height; iy++){
				int c = pSend.get(ix, iy);
				float[] hsv = new float[3];
				int r = c >> 16 & 0xFF;
				int g = c >> 8 & 0xFF;
				int b = c & 0xFF;
				Color.RGBtoHSB(r,g,b,hsv);
					//pSend.fill(saturation(c));
					if(ix==5 && iy==5){
						//System.out.println("Color: "+hsv[0]+" "+hsv[1]+" "+hsv[2]);
					}
					pSend.colorMode(HSB,255,255,255);
					pSend.fill(hsv[0]*255, (hsv[1]*(float)(currentSaturation/255.0))*255, hsv[2]*255);
					pSend.rect(ix, iy, 1, 1);
			}
		}
		pSend.endDraw();
		
		if(currentBrightness<brightness){
			currentBrightness++;
		}else if(currentBrightness>brightness){
			currentBrightness--;
		}
		
		if(currentSaturation<saturation){
			currentSaturation++;
		}else if(currentSaturation>saturation){
			currentSaturation--;
		}
		
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			screen.drawOnGui();
		}
		screen.send(9,8,0,0,0);
		
		if(frameCount % 1000 == 0){			
			//{"msg":"changed","collection":"visuals","id":"RH8TD6zpG3p4ZgdcQ","fields":{"active":true}}
			
			//System.out.println("Genius: "+geniusPaused+" "+geniusPausedActive+" "+settingActive);
			
			if(geniusMode && !geniusPaused && !settingActive){
			//if(geniusMode && !geniusPaused & !geniusPausedActive){
				for (Visual value : sensorData.getVisualList().values()) {
					String id = value.getId();
					if(value.getIndex()==geniusCounter){
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
						client.call(s);
					}else{
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
						client.call(s);
					}
				}
			
			//String s = new String("{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\"RH8TD6zpG3p4ZgdcQ\", \"fields\":{\"geniusActive\":\"false\"}}");
			//String s = new String("{\"id\":\"RH8TD6zpG3p4ZgdcQ\", \"fields\":{\"geniusActive\":\"false\"}}");
			//client.call(s);
				activeVisual = geniusCounter;
				next = geniusCounter;
				fade = 0;
				
				if(geniusCounter<3){
					geniusCounter++;
				}else{
					geniusCounter = 0;
				}

			}
		}
		
		
		
		/*if(millis()-geniusModeTimer>GENIUS_TIME && !geniusModeTimerCalled){
    		System.out.println("TIME_Over");
    		geniusModeTimerCalled = true;
    		String id = sensorData.getGeniusID();
    		String s = "{\"msg\":\"changed\", \"collection\":\"settings\", \"id\":\""+id+"\", \"fields\":{\"geniusPaused\":false}}";
			client.call(s);
		}*/
		
		if(frameCount%100==0){
			System.out.println(frameRate);
			//System.out.println(saturation);
		}
		
	}

	
	

	
	
	// -------FADE
	public PGraphics drawMode(int mode) {
		switch (mode) {
		case 0:
			return downscale(voltage.draw(), 1);
		case 1:
			return text.draw();
		case 2:
			return downscale(bargraph.draw(), 0);
		case 3:
			return downscale(bargraph_gencons.draw(), 1);
		default:
			return voltage.draw();
		}
	}

		// -------FADE

		// -------FADE
		public PGraphics fade(PGraphics a, PGraphics b, float fade) { // 0 < fade <
																		// 1
			//System.out.println("Genius: "+geniusPaused+" "+geniusPausedActive);
			if(geniusPaused || geniusPausedActive || settingActive || settingPausedActive){
				PGraphics c = createGraphics(17, 12, P2D);
				c.beginDraw();
				c.background(0);
				c.noStroke();
				//c.fill(0, 255 * fade);
				c.image(a, 0, 0);
				//c.rect(0, 0, 17, 12);
				//c.fill(0, 255 - 255 * fade);
				//c.tint(255, 255 * fade);
				//c.image(b, 0, 0);
				//c.noStroke();
				for(int ix=0; ix<c.width; ix++){
					for(int iy=0; iy<c.height; iy++){
						int color = b.get(ix, iy);
						int red = color >> 16 & 0xFF;
						int green = color >> 8 & 0xFF;
						int blue = color & 0xFF;
						c.fill(red, green, blue, 255*fade*5);
						c.rect(ix, iy, 1, 1);
					}
				}
				//c.rect(0, 0, 17, 12);
				c.endDraw();
				if(fade>0.95){
					geniusPausedActive = false;
				}
				return c;
			}else{
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
		if(e.getID()==VisualEvent.VISUAL_ACTIVE){
			//System.out.println("VISUAL ACTIVE");
			for (Visual value : vList.values()) {
				//System.out.println("ACTIVE: "+value);
				if(value.isActive()){
					//System.out.println(value.getName());
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
		}else if(e.getID()==VisualEvent.VISUAL_PAUSEDACTIVE){
			//System.out.println("VISUAL PAUSED");
			for (Visual value : vList.values()) {
				//System.out.println("ACTIVE: "+value);
				if(value.isPausedActive()){
					//System.out.println(value.getName());
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
		}else if(e.getID()==VisualEvent.VISUAL_SETTINGACTIVE){
			//System.out.println("VISUAL SETTING");
			settingActive = false;
			for (Visual value : vList.values()) {
				//System.out.println("ACTIVE: "+value);
				if(value.isSettingActive()){
					//System.out.println(value.getName());
					settingActive = true;
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
			if(!settingActive){
				//System.out.println("called genius event: "+geniusFirst+" "+geniusPausedActive);
				for (Visual value : sensorData.getVisualList().values()) {
					String id = value.getId();
					if(value.getIndex()==geniusCounter){
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
						client.call(s);
					}else{
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
						client.call(s);
					}
				}
				settingPausedActive = true;
				activeVisual = geniusCounter;
				next = geniusCounter;
				fade = 0;
			};
		}
		//System.out.print(activeVisual);
	}

	@Override
	public void geniusModeChanged(GeniusEvent e) {
		// TODO Auto-generated method stub
		geniusMode = e.getGenius();
		geniusPaused = e.getGeniusPaused();
		if(e.getID()==GeniusEvent.GENIUS_MODE_CHANGED){
			if(geniusMode){
				geniusPausedActive = true;
				//System.out.println("called genius event: "+geniusFirst+" "+geniusPausedActive);
				for (Visual value : sensorData.getVisualList().values()) {
					String id = value.getId();
					if(value.getIndex()==geniusCounter){
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
						client.call(s);
					}else{
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
						client.call(s);
					}
				}
				activeVisual = geniusCounter;
				next = geniusCounter;
				fade = 0;
			}
		}else if(e.getID()==GeniusEvent.GENIUS_PAUSED_CHANGED){
			//System.out.println("called genius event: "+geniusFirst);
			if(geniusPaused==false && !geniusFirst){
				geniusPausedActive = true;
				//System.out.println("called genius event: "+geniusFirst+" "+geniusPausedActive);
				for (Visual value : sensorData.getVisualList().values()) {
					String id = value.getId();
					if(value.getIndex()==geniusCounter){
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
						client.call(s);
					}else{
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
						client.call(s);
					}
				}
				activeVisual = geniusCounter;
				next = geniusCounter;
				fade = 0;
			}
			geniusFirst = false;
		}
		
		/*if(geniusPaused==false || first){
			geniusPausedActive = true;
			for (Visual value : sensorData.getVisualList().values()) {
				String id = value.getId();
				if(value.getIndex()==geniusCounter){
					String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
					client.call(s);
				}else{
					String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
					client.call(s);
				}
			}
			activeVisual = geniusCounter;
			next = geniusCounter;
			fade = 0;
		}
		if(geniusMode==false){
			geniusPausedActive = false;
		}
		first = true;*/
		//System.out.println("Genius"+e.getGenius()+"GeniusPaused"+e.getGeniusPaused());
	}

	@Override
	public void saturationChanged(SensorEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("Saturation Changed");
		saturation = (int)(e.getSaturation()*255);
	}
}
