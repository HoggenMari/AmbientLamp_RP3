package Visualisations;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import Event.CarEvent;
import Event.CarListener;
import Event.SensorData;
import SolarAPI.SiteData;
import SolarAPI.SolarAnalyticsAPI;
import SolarAPI.SolarAnalyticsAPI.GRAN;
import SolarAPI.SolarAnalyticsAPI.MONITORS;

public class Voltage implements CarListener {
	PGraphics canvas;
	PApplet applet;
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
	public boolean fake = true;

	float produced, consumed;


	public Voltage(PApplet a, SensorData sensorData, SolarAnalyticsAPI api, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = api;
		canvas = c;
		electrons = new ArrayList<Electron>();
		fields = new ArrayList<Powerfield>();
		charge = 0;
		chargeStep = 0;

		sensorData.addCarListener(this);
		
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

		step += 0.02f;

		timer = (int) (step * 3);
		if (timer % 2 == 0 && !electronEmitted) {
			//electrons.add(new Electron(applet, canvas));
			electronEmitted = true;
		} else if (timer % 2 != 0 && electronEmitted) {
			electronEmitted = false;
		}

		float change_consumption = api.getCurrentChangeInConsumption();
		float max_consumption = api.getMaxInConsumption();
		
		float change_water = api.getCurrentChangeInHotWater();
		float max_water = api.getMaxInHotWater();
		
		
		if(change_consumption>0.1*max_consumption || fake){
			System.out.println(fake);
		if (timer % 30 == 0)
			fields.add(new Powerfield(applet, canvas, applet.color(255,50)));
		}
		
	
		//if(change_consumption>0.1*max_consumption){
		//	change = true;
		//}else{
		//	change = false;
		//}
		
		//change = true;
		
		canvas.beginDraw();
		int bc = applet.color(22 + 22 * applet.sin(0),
				22 * applet.sin(0), 55);
		bc = c[22];
		
		canvas.background(bc);
		canvas.fill(255);

		

		canvas.noStroke();
		//smoothCircle3(charge);
		//smoothCircle2(charge);
		
		produced = api.getCurrentProduction(); //api.getLastSiteDataEntry().energy_generated;
		consumed = api.getCurrentConsumption();
		
		//produced = 100;
		//consumed = 50;
		
		//if(applet.frameCount%1800==0){
		//	produced = applet.random(0, 100);
		//	consumed = applet.random(0, 100);
		//}
		
		if(producedOld != produced || consumedOld != consumed){
			valueChanged = true;
		}
		
		
	
		/*if(producedCur<produced){
			producedCur+=0.5;
		}else if(producedCur>produced){
			producedCur-=0.5;
		}
		if(consumedCur<consumed){
			consumedCur+=0.5;
		}else if(consumedCur>consumed){
			consumedCur-=0.5;
		}*/
			
		
		//System.out.println("PRODUCED: "+produced+" "+producedCur);
		//System.out.println("CONSUMED: "+consumed+" "+consumedCur);
		//System.out.println(max);
		//System.out.println(change);
		//System.out.println("LIVEBUFFER: "+api.getLiveBuffer(MONITORS.ac_load_net));
		//System.out.println("MAX: "+api.getMaxInConsumption());

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
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		canvas.fill(calcColor(60, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 150), applet.color(255, 250, 170, 200)));
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
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 150)));
		canvas.fill(calcColor(100, applet.color(135, 96, 190, 88), applet.color(153, 109, 214, 88), applet.color(180, 136, 242, 88)));

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
	public void carChanged(CarEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("BLLLAAAA");
		setCharge((float) (e.getCarValue() / 100.0));

	}
}
