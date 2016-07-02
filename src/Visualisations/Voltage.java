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

		step += 0.02f;

		timer = (int) (step * 3);
		if (timer % 2 == 0 && !electronEmitted) {
			//electrons.add(new Electron(applet, canvas));
			electronEmitted = true;
		} else if (timer % 2 != 0 && electronEmitted) {
			electronEmitted = false;
		}

		float change = api.getCurrentChangeConsumption();
		float max = api.getMaxConsumedLive();
		float mean = api.getMeanProducedWeekly(GRAN.minute);
		
		if(change>0.05*max){
		if (timer % 30 == 0)
			fields.add(new Powerfield(applet, canvas));
		}
		
		canvas.beginDraw();
		int bc = applet.color(22 + 22 * applet.sin(0),
				22 * applet.sin(0), 55);
		canvas.background(bc);
		canvas.fill(255);

		for (int e = 0; e < electrons.size(); e++) {
			if (electrons.get(e).dead()) {
				electrons.remove(e);
			}
		}

		for (int e = 0; e < electrons.size(); e++) {
			//electrons.get(e).display();
		}

		for (int f = 0; f < fields.size(); f++) {
			if (fields.get(f).dead()) {
				fields.remove(f);
			}
		}

		for (int f = 0; f < fields.size(); f++) {
			fields.get(f).display();
		}

		canvas.noStroke();
		//smoothCircle3(charge);
		//smoothCircle2(charge);
		
		float produced = api.getLastSiteDataEntry().energy_generated;
		float consumed = api.getLastSiteDataEntry().energy_consumed;

		float maxProduced = api.getMaxProducedWeekly(GRAN.minute);
		float maxConsumed = api.getMaxConsumedWeekly(GRAN.minute);

		float mapProduce = applet.map(produced, 0, maxProduced, 0, 100);
		float mapConsumed = applet.map(consumed, 0, maxConsumed, 0, 100);

		smoothCircleConsumed(mapConsumed);
		//smoothCircle(20);
		
		canvas.endDraw();
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
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 150)));
		//canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(200, 31, 255, 150)));

		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.66f, n),
					rad * applet.pow(0.66f, n));
		}
	}
	
	void smoothCircleConsumed(float r) {
		int x = canvas.width/2;
		int y = canvas.height/2;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(0) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 150)));
		canvas.fill(calcColor(r, applet.color(135, 96, 190, 88), applet.color(153, 109, 214, 88), applet.color(180, 136, 242, 88)));

		for (int n = 0; n < 5; n++) {
			canvas.ellipse(x, y, rad * applet.pow(0.66f, n),
					rad * applet.pow(0.66f, n));
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
