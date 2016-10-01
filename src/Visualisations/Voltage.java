package Visualisations;

import java.util.ArrayList;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PGraphics;
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

public class Voltage implements VisualListener, SolarListener {
	PGraphics canvas;
	PApplet applet;
	String visual_name = "Visual 1";
	int[] color;
	boolean notification = true;
	
	ArrayList<Electron> electrons;
	ArrayList<Powerfield> fields;
	float charge, step;
	int chargeStep, lastChargeStep;
	private SensorData sensorData;
	int timer;
	boolean electronEmitted;
	private SolarAnalyticsAPI api;
	boolean change = false;
	float producedOld;
	float consumedOld;
	float producedCur;
	float consumedCur;
	public boolean valueChanged = false;
	public boolean fake = false;

	float produced, consumed;
	private float change_consumption;
	private float max_consumption;


	public Voltage(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = SolarAnalyticsAPI.getInstance();
		api.addLiveDataListener(this);
		
		canvas = c;
		electrons = new ArrayList<Electron>();
		fields = new ArrayList<Powerfield>();
		charge = 0;
		chargeStep = 0;

		color = new int[]{ applet.color(45, 47, 48),
			    applet.color(244, 99, 97),
			    applet.color(135, 96, 190),
			    applet.color(255,255,255)};
		
		produced = api.getCurrentGen(); //api.getLastSiteDataEntry().energy_generated;
		consumed = api.getCurrentCons();
		change_consumption = api.getChangeCons();
		max_consumption = api.getMaxCons();
		
		sensorData.addVisualListener(this);
		
	}

	public PGraphics draw() {

		// if (charge + 0.03f < 100.0f)
		// setCharge(charge += 0.03f);
				
		step += 0.02f;

		timer = (int) (step * 3);
		if (timer % 2 == 0 && !electronEmitted) {
			//electrons.add(new Electron(applet, canvas));
			//electronEmitted = true;
		} else if (timer % 2 != 0 && electronEmitted) {
			//electronEmitted = false;
		}	

		if(notification){
			if(change_consumption>0.1*max_consumption || fake){
				if (timer % 20 == 0){
					float c1 = color[2] >> 16 & 0xFF;
					float c2 = color[2] >> 8 & 0xFF;;
					float c3 = color[2] & 0xFF;
					fields.add(new Powerfield(applet, canvas, applet.color(c1,c2,c3,50)));
				}
			}
		}
		
		canvas.beginDraw();
		int bc = applet.color(22 + 22 * applet.sin(0),
				22 * applet.sin(0), 55);
		
		bc = color[3];
		
		canvas.background(bc);
		canvas.fill(255);

		

		canvas.noStroke();
		
		

		
		if(producedOld != produced || consumedOld != consumed){
			valueChanged = true;
		}
		

		float mapProduce2 = applet.map(produced, 0, consumed, 0, canvas.width/2);
		float mapConsumed2 = applet.map(consumed, 0, produced, 0, canvas.width/2);		
		
		if(produced>consumed){
			smoothCircle(canvas.width/2);
			smoothCircleConsumed(mapConsumed2);
		}else{
			smoothCircleConsumed(canvas.width/2);
			smoothCircle(mapProduce2);
		}
		
		for (int e = 0; e < electrons.size(); e++) {
			if (electrons.get(e).dead()) {
				electrons.remove(e);
			}
		}

		for (int e = 0; e < electrons.size(); e++) {
			electrons.get(e).display();
		}

		for (int f = 0; f < fields.size(); f++) {
			if (fields.get(f).dead()) {
				fields.remove(f);
			}
		}

		for (int f = 0; f < fields.size(); f++) {
			fields.get(f).display();
		}

		canvas.endDraw();
		
		producedOld = produced;
		consumedOld = consumed;
		
		return canvas;
	}
	

	void smoothCircle(float r) {
		int x = canvas.width/2;
		int y = canvas.height/2;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(0) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;

		
		float c1 = color[0] >> 16 & 0xFF;
		float c2 = color[0] >> 8 & 0xFF;;
		float c3 = color[0] & 0xFF;
		
		canvas.fill(calcColor(60, applet.color(c1, c2, c3, 88), applet.color(c1, c2, c3, 150), applet.color(c1, c2, c3, 200)));
		//canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(200, 31, 255, 150)));

		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.8f, n),
					rad * applet.pow(0.8f, n));
		}
	}
	
	void smoothCircleConsumed(float r) {
		int x = canvas.width/2;
		int y = canvas.height/2;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(0) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;
		if(change){
			rad = ((applet.sin(step) + 1f) / 2f) * rad * 0.3f + rad * 0.8f;
		}
		
		float c1 = color[1] >> 16 & 0xFF;
		float c2 = color[1] >> 8 & 0xFF;;
		float c3 = color[1] & 0xFF;
		
		canvas.fill(calcColor(100, applet.color(c1, c2, c3, 88), applet.color(c1, c2, c3, 88), applet.color(c1, c2, c3, 88)));

		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.9f, n),
					rad * applet.pow(0.9f, n));
		}
	}
	
	void smoothCircle2(float r) {
		int x = 0;
		int y = 0;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(step) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		//canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 88)));
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(200, 31, 255, 150)));
		
		canvas.fill(calcColor(r, applet.color(255, 255, 255, 88), applet.color(255, 255, 255, 88), applet.color(255, 255, 255, 150)));


		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.66f, n),
					rad * applet.pow(0.66f, n));
		}
	}
	
	void smoothCircle3(float r) {
		int x = canvas.width/2;
		int y = -canvas.height/4;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(step) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		//canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 88)));
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(200, 31, 255, 150)));

		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.66f, n),
					rad * applet.pow(0.66f, n));
		}
	}

	int calcColor(float i, int color1, int color2, int color3) {
		i = i / 100;
		int c = 0;
		if (i < 0.5f)
			c = applet.lerpColor(color1, color2, i * 2);
		else
			c = applet.lerpColor(color2, color3, (i - 0.5f) * 2);
		return c;
	}

	void setCharge(float c) {
		//System.out.println(c);
		charge = c * 100;
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
				notification = value.isNotification();
			}
		}
	}

	@Override
	public void liveSiteDataChanged() {
		// TODO Auto-generated method stub
		//System.out.println("DataChanged");
		
		produced = api.getCurrentGen(); //api.getLastSiteDataEntry().energy_generated;
		consumed = api.getCurrentCons();
		change_consumption = api.getChangeCons();
		max_consumption = api.getMaxCons();
	}
}
