package Visualisations;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Powerfield {

	PApplet applet;
	PGraphics canvas;
	float rad;

	public Powerfield(PApplet a, PGraphics c) {
		applet = a;
		canvas = c;
		rad = 0;
	}

	void display() {
		rad += 1f;
		canvas.beginDraw();
		canvas.noFill();
		canvas.stroke(255, 200);
		canvas.strokeWeight(10);
		canvas.ellipse(canvas.width / 2, canvas.height / 2, rad, rad);
		canvas.endDraw();
	}

	boolean dead() {
		if (rad > 200) {
			return true;
		} else
			return false;
	}

}
