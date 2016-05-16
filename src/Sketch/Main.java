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
import processing.core.PApplet;
import processing.core.PGraphics;

public class Main extends PApplet implements SensorListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Screen screen;
	PGraphics pSend;
	DDPClient client;
	float brightness;
	
	public static void main(final String... args){
    	
		PApplet.main(new String[] { "--present", "Sketch.Main" });
    	
    	SolarAnalyticsAPI api = new SolarAnalyticsAPI();
    	
    	/*while(1==1){
    		api.getDay();
    	}*/
    }
	
	public void setup() {
    	
		client = new DDPClient("193.168.0.100", 3000);
    	client.connect();
    	
    	SensorData.getInstance().addSensorListener(this);
    	
		size(100,100);
		screen = new Screen(this, 17, 12);
		pSend = createGraphics(17,12,P2D);
		
	}
	
	public void draw() {
		
		//background(255,0,0);
		
		System.out.println(brightness);
		pSend.beginDraw();
		pSend.fill((int)(brightness*255.0),(int)(brightness*255.0),(int)(brightness*255.0));
		pSend.rect(0, 0, pSend.width, pSend.height);
		pSend.endDraw();
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			//System.out.println(frameRate);
			screen.drawOnGui();
		}
		screen.send(9, 8, 8, 8, 8);
		
	}

	@Override
	public void brightnessChanged(SensorEvent e) {
		//System.out.println("Brightness Changed");
		brightness = e.getBrightness();
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
