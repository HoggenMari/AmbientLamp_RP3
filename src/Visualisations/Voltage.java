package Visualisations;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import Event.CarEvent;
import Event.CarListener;
import Event.SensorData;

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

	public Voltage(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
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

		if (timer % 30 == 0)
			fields.add(new Powerfield(applet, canvas));

		canvas.beginDraw();
		int bc = applet.color(22 + 22 * applet.sin(step),
				22 * applet.sin(step), 55);
		canvas.background(bc);
		canvas.fill(255);

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

		canvas.noStroke();
		smoothCircle(charge);
		canvas.endDraw();
		return canvas;
	}

	void smoothCircle(float r) {
		int x = canvas.width / 2;
		int y = canvas.height / 2;
		float rad = applet.map(r, 0f, 100f, 10f, 200f);
		rad = ((applet.sin(step) + 1f) / 2f) * rad * 0.25f + rad * 0.75f;
		canvas.fill(calcColor(r, applet.color(244, 57, 67, 88),
				applet.color(227, 229, 229, 88),
				applet.color(100, 194, 255, 88)));
		canvas.fill(calcColor(r, applet.color(244, 99, 97, 88), applet.color(222, 212, 111, 88), applet.color(255, 250, 127, 88)));
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
