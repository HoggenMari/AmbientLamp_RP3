package Visualisations;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class SunParticle {

	PVector position;
	PApplet applet;
	PGraphics canvas;
	float speed;
	int color;
	float c1, c2, c3;
	PVector direction;
	boolean sub = false;
	
	public SunParticle(PApplet a, PGraphics c, float speed, int color) {
		applet = a;
		canvas = c;
		float x, y;
		x = 0;
		y = 0;
		switch ((int) applet.random(0, 3.9f)) {
		case 0:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			sub = true;
			break;
		case 1:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			sub = false;
			break;
		case 2:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			sub = true;
			break;
		case 3:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			sub = false;
			break;
		}
		position = new PVector(x, y);
		this.speed = speed;
		this.color = color;
		c1 = color >> 16 & 0xFF;
		c2 = color >> 8 & 0xFF;;
		c3 = color & 0xFF;
	}

	void move() {
		//PVector direction = new PVector(0, 0);
		//direction.sub(position);
		direction.normalize();
		direction.mult(speed*0.5f+applet.random(0f, 0.1f));
		if(sub){
		position.sub(direction);
		}else{
		position.add(direction);	
		}
		//((Object) direction).random2D();
		//direction.mult(0.3f);
		//position.add(direction);
	}

	void display() {
		move();
		canvas.beginDraw();
		//System.out.println(PVector.dist(position, new PVector(canvas.width / 2, canvas.height / 2)));
		
		canvas.fill(c1, c2, c3, (PVector.dist(position, new PVector(canvas.width / 2,
				canvas.height / 2)) / PVector.dist(new PVector(0, 0), new PVector(canvas.width / 2,
						canvas.height / 2)))*255);
		
		
			canvas.ellipse(position.x, position.y, 12, 12);
		
		canvas.endDraw();
	}

	boolean dead() {
		float d = PVector.dist(position, new PVector(canvas.width / 2,
				canvas.height / 2));
		if (position.x < applet.random(-10, 0) || position.x > applet.random(canvas.width, canvas.width+10) || position.y < 0 || position.y > canvas.height) {
			return true;
		} else
			return false;
	}

}