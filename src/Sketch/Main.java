package Sketch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import FenoDMX.Screen;
import SolarAPI.SolarAnalyticsAPI;
import processing.core.PApplet;
import processing.core.PGraphics;

public class Main extends PApplet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Screen screen;
	PGraphics pSend;

	public static void main(final String... args){
    	
		PApplet.main(new String[] { "--present", "Sketch.Main" });
		
    	SolarAnalyticsAPI api = new SolarAnalyticsAPI();
    	
    	/*while(1==1){
    		api.getDay();
    	}*/
    }
	
	public void setup() {
		
		size(100,100);
		screen = new Screen(this, 17, 12);
		pSend = createGraphics(17,12,P2D);
		
	}
	
	public void draw() {
		
		background(255,0,0);
		
		pSend.beginDraw();
		pSend.background(255,220,220);
		pSend.endDraw();
		
		screen.addLayer(pSend);
		if (frameCount % 1 == 0) {
			// System.out.println(frameRate);
			screen.drawOnGui();
		}
		screen.send(9, 8, 8, 8, 8);
		
	}

}
