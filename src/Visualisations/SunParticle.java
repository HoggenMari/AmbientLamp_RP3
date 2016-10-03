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
	private int counter;
	
	public SunParticle(PApplet a, PGraphics c, float speed, int color) {
		applet = a;
		canvas = c;
		float x, y;
		x = 0;
		y = 0;
		switch ((int) applet.noise(0, 3.9f)) {
		case 0:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			if((int) applet.random(0, 1.5f)>0){
			sub = true;
			}else{
			sub = false;
			}
			break;
		case 1:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			if((int) applet.random(0, 1.5f)>0){
			sub = true;
			}else{
			sub = false;
			}
			break;
		case 2:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			if((int) applet.random(0, 1.5f)>0){
			sub = true;
			}else{
			sub = false;
			}
			break;
		case 3:
			x = canvas.width / 2;
			y = canvas.height / 2;
			direction = new PVector(a.random(0, canvas.width), a.random(0, canvas.height));
			if((int) applet.random(0, 1.5f)>0){
			sub = true;
			}else{
			sub = false;
			}
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

		if(counter<255){
			  counter++;
		}
		
		float fader = (PVector.dist(position, new PVector(canvas.width / 2,
				canvas.height / 2)) / PVector.dist(new PVector(0, 0), new PVector(canvas.width / 2,
						canvas.height / 2)))*255;
		
		canvas.fill(c1+(255-255), c2+(255-255), c3+(255-255), (PVector.dist(position, new PVector(canvas.width / 2,
				canvas.height / 2)) / PVector.dist(new PVector(0, 0), new PVector(canvas.width / 2,
						canvas.height / 2)))*255);
		
		float size = applet.map(counter, 0, 255, 5, 12);
			canvas.ellipse(position.x, position.y, size, size);
		
		canvas.endDraw();
	}

	boolean dead() {
		float d = PVector.dist(position, new PVector(canvas.width / 2,
				canvas.height / 2));
		if (position.x < applet.random(-10f, 0f) || position.x > applet.random(canvas.width, canvas.width+10f) || position.y < applet.random(-10f, 0f) || position.y > applet.random(canvas.height, canvas.height+10f)) {
			return true;
		} else
			return false;
	}

}