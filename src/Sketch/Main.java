package Sketch;

import java.awt.Color;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
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
import SolarAPI.WeatherAPI;
import Visualisations.BarGraph;
import Visualisations.BarGraphGenCons;
import Visualisations.Circle;
import Visualisations.Cloud;
import Visualisations.Lava;
import Visualisations.Moving;
import Visualisations.Text;
import Visualisations.Voltage;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import jssc.*;

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
	WeatherAPI weather;
	
	private Voltage voltage;
	private Moving moving;
	private Circle circle;
	private SensorData sensorData;
	private BarGraph bargraph;
	private BarGraphGenCons bargraph_gencons;

	private Text text;
	boolean textBol = true;
	
	ArrayList<Visual> visualList;
	int activeVisual = 0;
	
	//Cloud
	private Cloud cloud;
	private int clIndex;

	//GENIUS
	boolean first = false;
	boolean firstChanged = true;
	boolean geniusMode = false;
	boolean geniusFirst = true;
	private boolean geniusPaused = false;
	private boolean geniusPausedActive = false;
	private boolean settingActive = false;
	private String settingActiveName = "";
	private int settingActiveRemain = 0;
	private int settingActiveWait = 300000;
	private boolean settingPausedActive = false;
	int geniusModeTimer = 0;
	boolean geniusModeTimerCalled = false;
	int GENIUS_TIME = 10000;
	int geniusActiveVisual = 0;
	private int geniusCtr = 0;
	
	// -------FADE
	float fade = 1;
	int next = 0;
	int active = 0;
	private int activeManual;
	// -------FADE
	
	int load = 0;
	
	SerialPort serialPort;
	StringBuilder message = new StringBuilder();
	String receivedString = "";
	Boolean receivingMessage = false;
	
	PGraphics pg = createGraphics(85,60,P2D);
	Lava lava = new Lava();
	
	boolean firstStepper = true;
	
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
    	
    	weather = WeatherAPI.getInstance();
    	
		voltage = new Voltage(this, sensorData, createGraphics(85, 60, P2D));
		moving = new Moving(this, sensorData, createGraphics(85, 60, P2D));
		//circle = new Circle(this, sensorData, api, createGraphics(85, 60, P2D));

		bargraph = new BarGraph(this, sensorData, createGraphics(170, 120, P2D));
		bargraph_gencons = new BarGraphGenCons(this, sensorData, createGraphics(170, 120, P2D));
		text = new Text(this, sensorData, createGraphics(17, 12, P2D));
		cloud = new Cloud(this);
		
		visualList = new ArrayList<Visual>();
		
		//visualList.add(new Visual("Circle", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));
		//visualList.add(new Visual("Powerfield", new String[] { "#FFFFFF", "#FFFFFF", "#FFFFFF" }, false, false));

		client = new DDPClient("localhost", 3000);
    	client.connect();

    	
    	
    	
    	serialPort = new SerialPort(os.arduino);
        try {
            System.out.println("Port opened: " + serialPort.openPort());
            System.out.println("Params setted: " + serialPort.setParams(115200, 8, 1, 0));
    		serialPort.addEventListener(new SerialPortEventListener()
    		{
    			@Override
    			public void serialEvent(SerialPortEvent serialPortEvent)
    			{
    				 if(serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
    			            try {
    			                String receivedData = serialPort.readString(serialPortEvent.getEventValue());
    			                receivedString += receivedData;
    			                //System.out.println(receivedString);
    			                
    			                if(receivedString.contains("\n")){
    			                	//System.out.print("Received: "+receivedString);
    			                	processArduino(receivedString);
    			                	receivedString = "";
    			                }
    			            }
    			            catch (SerialPortException | ParseException ex) {
    			                System.out.println("Error in receiving string from COM-port: " + ex);
    			            }
    			        }    			}
    		});
            
            //System.out.println("\"Hello World!!!\" successfully writen to port: " + serialPort.writeBytes("Hello World!!!".getBytes()));
            //System.out.println("Port closed: " + serialPort.closePort());
        }
        catch (SerialPortException ex){
            System.out.println(ex);
        }
        
        
    	lava.setup(this);
    	delay(2000);

	}
	
	public void draw() {
		
		//frameRate(5);
		//System.out.println("GENIUS: "+geniusMode);
		if(frameCount%200==0){
			//System.out.println("active:"+active+" next:"+next+" geniusCtr:"+geniusCtr);
			/*try {
				serialPort.writeString("Motors on\n");
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		
		// -------FADE
		
		if(next != active && firstChanged) {
			System.out.println("changed");
			if (next == 0) {
				toFront();
			} else if(next == 1) {
				toFront();
			} else if(next == 2) {
				toBack();
			} else if(next == 3) {
				toBack();
			}
			firstChanged = false;
		}
		
		if (fade < 1 && next != active) {
			fade += 0.02f;
			canvasFade = fade(drawMode(active), drawMode(next), fade);
			// System.out.println("Fade: " + fade);
		} else {
			active = next;
			canvasFade = drawMode(active);
			firstChanged = true;
			//System.out.println("NoFade: " + fade);

		}
		
		if(fade>0.97){
			settingPausedActive = false;
		}

		//pSend = downscale(canvasFade, 0);
		
		/*if(frameCount%1000==0)
		if (clIndex <= 8) {
			clIndex++;
		}else{
			clIndex = 0;
		}
		cloud.changeCloud(clIndex);*/
		
	    //canvasFade = drawMode(4);
		
		/*PImage lv = lava.draw(this, pg);
		PGraphics pg1 = createGraphics(lv.width, lv.height, P2D);
		pg1.beginDraw();
		pg1.image(lv,0,0);
		pg1.endDraw();
		canvasFade = downscale(pg1, 1);
		image(lv, 0, 0);*/
		
		//PImage lv = lava.draw(this, pg);
		//lv.resize(17, 12);
		//canvasFade.beginDraw();
		//canvasFade.image(lv, 0, 0);
		//canvasFade.endDraw();
		
		//canvasFade.beginDraw();
		//canvasFade.fill(frameCount&255);
		//canvasFade.rect(0, 0, 17, 12);
		//canvasFade.endDraw();
			
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
		//LED TEST
		/*pSend.beginDraw();
		pSend.colorMode(RGB);
		pSend.background(255);
		pSend.fill(0,0,255);
		pSend.rect(0, 0, 17, 1);
		pSend.fill(255);
		for(int i=0; i<12; i++){
			pSend.rect(i, i, 1, 1);
		}
		pSend.endDraw();*/
		
		if(currentBrightness<brightness){
			currentBrightness+=2;
		}else if(currentBrightness>brightness){
			currentBrightness-=2;
		}
		
		if(currentSaturation<saturation){
			currentSaturation+=2;
		}else if(currentSaturation>saturation){
			currentSaturation-=2;
		}
		
		if(!client.isAdded()){
			pSend.beginDraw();
			pSend.background(0);
			pSend.fill(0,255,255);
			pSend.noStroke();
			pSend.rect(load, 11, 1, 1);
			pSend.endDraw();
			if(frameCount%10==0){
				if(load<17){
					load++;
				}else{
					load=0;
				}
			}	
		}

		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0 && os.gui) {
			screen.drawOnGui();
		}
		screen.send(9,8);
		
		if(frameCount % 500 == 0){	
			
			//System.out.println("FRAMECOUNT: "+geniusCtr);
			//{"msg":"changed","collection":"visuals","id":"RH8TD6zpG3p4ZgdcQ","fields":{"active":true}}
			
			//System.out.println("Genius: "+geniusPaused+" "+geniusPausedActive+" "+settingActive);
			
			if(geniusMode && !geniusPaused && !settingActive){
				
				if(geniusCtr<4){
					geniusCtr++;
				}else{
					geniusCtr = 0;
				}
				
				geniusCtr = 4;
				
				activeVisual = geniusCtr;
				next = geniusCtr;
				fade = 0;
				
			//if(geniusMode && !geniusPaused & !geniusPausedActive){
				for (Visual value : sensorData.getVisualList().values()) {
					String id = value.getId();
					if(value.getIndex()==geniusCtr){
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
				

			}
		}
		
		
		
		/*if(millis()-geniusModeTimer>GENIUS_TIME && !geniusModeTimerCalled){
    		System.out.println("TIME_Over");
    		geniusModeTimerCalled = true;
    		String id = sensorData.getGeniusID();
    		String s = "{\"msg\":\"changed\", \"collection\":\"settings\", \"id\":\""+id+"\", \"fields\":{\"geniusPaused\":false}}";
			client.call(s);
		}*/
		
		if(frameCount%1000==0){
			System.out.println(frameRate);
			//System.out.println(saturation);
		}
		
		if(settingActive){
			//System.out.println(millis() - settingActiveRemain);
			if(millis() - settingActiveRemain >= settingActiveWait){
			    //println("tick");//if it is, do something
			    if(geniusMode){
					settingPausedActive = true;
					activeVisual = geniusCtr;
					next = geniusCtr;
					fade = 0;
				}else{
					settingPausedActive = true;
					activeVisual = activeManual;
					next = activeManual;
					fade = 0;	
				}
			    settingActive = false;

			    //time = millis();//also update the stored time
			  }
		}
		
		
		
		//Weather API Test
		//System.out.println("WEATHER");
		for(String s : weather.getForecastArray()){
			//System.out.println(s);
		}
		
		//if(firstStepper){
			//toBack();
		//	firstStepper = false;
		//}
		
		//PImage lv = lava.draw(this, pg);
		//image(lv, 0, 0);
	}

	
	

	
	
	// -------FADE
	public PGraphics drawMode(int mode) {
		switch (mode) {
		case 0:
			//toFront();
			//frameRate(60);
			return downscale(voltage.draw(), 3);
		case 1:
			//frameRate(1);
			//toFront();
			return text.draw();
		case 2:
			//frameRate(1);
			//toBack();
			return downscale(bargraph.draw(), 0);
		case 3:
			//frameRate(1);
			//toBack();
			return downscale(bargraph_gencons.draw(), 1);
		case 4:
			//frameRate(60);
			return downscale(moving.draw(), 3);
		default:
			return voltage.draw();
		}
	}

		// -------FADE

		// -------FADE
		public PGraphics fade(PGraphics a, PGraphics b, float fade) { // 0 < fade <
																		// 1
			//System.out.println("Genius: "+geniusPaused+" "+geniusPausedActive);
			//if(geniusPaused || settingActive || settingPausedActive){ //|| geniusPausedActive || settingActive || settingPausedActive){
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
						c.fill(red, green, blue, 255*fade);
						c.rect(ix, iy, 1, 1);
					}
				}
				//c.rect(0, 0, 17, 12);
				c.endDraw();
				//if(fade>0.95){
				//	geniusPausedActive = false;
				//}
				return c;
			/*}else{
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
			}*/
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
	
	public void toFront(){
		try {
			serialPort.writeString("toFront\n");
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void toBack(){
		try {
			serialPort.writeString("toBack\n");
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void keyPressed() {
		if (key == '1') {
			voltage.fake = !voltage.fake;
		}else if(key == '2') {
			textBol = !textBol;
		}else if(key == '3') {
			toFront();
		}else if(key == '4') {
			toBack();
		}
	}

	@Override
	public void visualsChanged(VisualEvent e) {
		// TODO Auto-generated method stub
		HashMap<String,Visual> vList = e.getVisualList();
		if(e.getID()==VisualEvent.VISUAL_ACTIVE || e.getID()==VisualEvent.VISUAL_CHANGED){
			//System.out.println("VISUAL ACTIVE");
			for (Visual value : vList.values()) {
				//System.out.println("ACTIVE: "+value);
				if(value.isActive()){
					//System.out.println(value.getName());
					activeManual = value.getIndex();
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
		}/*else if(e.getID()==VisualEvent.VISUAL_PAUSEDACTIVE){
			System.out.println("GeniusCtr: "+geniusCtr);
			System.out.println("VISUAL PAUSED");
			geniusPaused = false;
			for (Visual value : vList.values()) {
				System.out.println("PAUSED: "+value);
				if(value.isPausedActive()){
					System.out.println(value.getName());
					geniusPaused = true;
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
			if(!geniusPaused){
				activeVisual = geniusCtr;
				next = geniusCtr;
				fade = 0;	
				System.out.println("GeniusCtr: "+geniusCtr);
			}
		}*/else if(e.getID()==VisualEvent.VISUAL_SETTINGACTIVE){
			//System.out.println("VISUAL SETTING");
			settingActive = false;
			for (Visual value : vList.values()) {
				//System.out.println("SETTINGACTIVE: "+value);
				if(value.isSettingActive()){
					//System.out.println(value.getName());
					settingActive = true;
					settingActiveName = value.getName();
					activeVisual = value.getIndex();
					next = value.getIndex();
					fade = 0;
					break;
				}
			}
			if(!settingActive){
				if(geniusMode){
					settingPausedActive = true;
					activeVisual = geniusCtr;
					next = geniusCtr;
					fade = 0;
				}else{
					settingPausedActive = true;
					activeVisual = activeManual;
					next = activeManual;
					fade = 0;	
				}
			}else{
				settingActiveRemain = millis();
			};
		}else if(e.getID()==VisualEvent.VISUAL_COLORS){
			for (Visual value : vList.values()) {
				if(value.getName().equals(settingActiveName)){
					//System.out.println("Value = " + value.getColorsAsRGB().get(0));
					settingActiveRemain = millis();
				}
			}
		}
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
					if(value.getIndex()==geniusCtr){
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":true}}";
						client.call(s);
					}else{
						String s = "{\"msg\":\"changed\", \"collection\":\"visuals\", \"id\":\""+id+"\", \"fields\":{\"geniusActive\":false}}";
						client.call(s);
					}
				}
				activeVisual = geniusCtr;
				next = geniusCtr;
				fade = 0;
			}
		}else if(e.getID()==GeniusEvent.GENIUS_PAUSED_CHANGED){
			//System.out.println("called genius event: "+geniusFirst);
			/*if(geniusPaused==false && !geniusFirst){
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
				System.out.println("Genius-Counter: "+geniusCounter);
				activeVisual = geniusCounter;
				next = geniusCounter;
				fade = 0;
			}
			geniusFirst = false;*/
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
	
	public void processArduino(String message) throws ParseException{
		
		//message = message.replace("\n", "");
		System.out.println(message);
		
		if(message.contains("Start")){
			toFront();
		}

		JsonObject jsonObject;
		JsonParser jejpl = new JsonParser();
		JsonElement el;
		try {
			el = jejpl.parse((String) message);
			if(el.isJsonObject()){
				jsonObject = el.getAsJsonObject();
				//System.out.println(jsonObject);
				if(jsonObject.has("sensor")){
					String sensor = jsonObject.get("sensor").getAsString();
					if(sensor.equals("brightness")){
						//System.out.println(jsonObject.get("data"));
						String s = "{\"msg\":\"changed\", \"collection\":\"settings\", \"id\":\""+sensorData.getBrightnessID()+"\", \"fields\":{\"score\":"+jsonObject.get("data")+"}}";
						if(client.isAdded()){
							client.call(s);
						}
					}
				}
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	


	
}
