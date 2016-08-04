package Visualisations;

import processing.core.PApplet;
import processing.core.PGraphics;

public class Powerfield {

	PApplet applet;
	PGraphics canvas;
	float rad;
	int color;

	public Powerfield(PApplet applet, PGraphics canvas) {
		this.applet = applet;
		this.canvas = canvas;
		rad = 0;
		color = applet.color(255, 200);
	}
	
	public Powerfield(PApplet applet, PGraphics canvas, int color) {
		this.applet = applet;
		this.canvas = canvas;
		rad = 0;
		this.color = color;
	}

	void display() {
		rad += 1f;
		canvas.beginDraw();
		canvas.noFill();
		canvas.stroke(color);
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
