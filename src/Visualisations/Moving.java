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

public class Moving implements VisualListener, SolarListener {
	PGraphics canvas;
	PApplet applet;
	String visual_name = "Visual 5";
	int[] color;
	boolean notification = true;
	
	ArrayList<Electron> electronsGrid;
	ArrayList<Electron> electronsOnSite;
	ArrayList<SunParticle> electronsSun;

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

	float produced, consumed, consumedOnSite;
	private float change_consumption;
	private float max_consumption;
	private float MAX_CONSUMPTION = 2000;
	private float MAX_PRODUCTION = 2000;

	private float consumedSpeed;
	private float consumedOnSiteSpeed;
	private float producedSpeed;
	
	private float imp, exp;

	public Moving(PApplet a, SensorData sensorData, PGraphics c) {
		applet = a;
		this.sensorData = sensorData;
		this.api = SolarAnalyticsAPI.getInstance();
		api.addLiveDataListener(this);
		
		canvas = c;
		electronsGrid = new ArrayList<Electron>();
		electronsOnSite = new ArrayList<Electron>();
		electronsSun = new ArrayList<SunParticle>();

		fields = new ArrayList<Powerfield>();
		charge = 0;
		chargeStep = 0;

		color = new int[]{ applet.color(45, 47, 48),
			    applet.color(244, 99, 97),
			    applet.color(135, 96, 190),
			    applet.color(255,255,255)};
		
		produced = api.getCurrentGen(); //api.getLastSiteDataEntry().energy_generated;
		consumed = api.getCurrentCons();
		
		imp = consumed - produced;
		
		consumedSpeed = consumed/MAX_CONSUMPTION;
		
		change_consumption = api.getChangeCons();
		max_consumption = api.getMaxCons();
		
		sensorData.addVisualListener(this);
		
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
		/*if (timer % 2 == 0 && !electronEmitted) {
			electrons.add(new Electron(applet, canvas));
			electronEmitted = true;
		} else if (timer % 2 != 0 && electronEmitted) {
			electronEmitted = false;
		}*/
		
		/*if (timer % 2 == 0 && !electronEmitted) {
			if(electronsGrid.size()<applet.map(consumedSpeed, 0, 1, 0, 10)){
				electronsGrid.add(new Electron(applet, canvas, consumedSpeed, applet.color(0,0,0)));
			}
			electronEmitted = true;
		} else if (timer % 2 != 0 && electronEmitted) {
			electronEmitted = false;
		}*/
		
		
		//System.out.println("CON: "+consumed+" "+applet.map(consumedSpeed, 0, 1, 0, 5));
		//System.out.println("COS: "+consumedOnSite+" "+applet.map(consumedOnSiteSpeed, 0, 1, 0, 5));
		//System.out.println("PRO: "+produced+" "+applet.map(producedSpeed, 0, 1, 0, 5));

		
		
		if(electronsGrid.size()<applet.map(consumedSpeed, 0, 1, 0, 5)){
			electronsGrid.add(new Electron(applet, canvas, consumedSpeed, color[1]));
		}
		
		if(electronsOnSite.size()<applet.map(consumedOnSiteSpeed, 0, 1, 0, 5)){
			electronsOnSite.add(new Electron(applet, canvas, consumedOnSiteSpeed, color[0]));
		}

		//if(electronsSun.size()<5)
		//electronsSun.add(new SunParticle(applet, canvas, consumedSpeed, color[2]));

		//System.out.println(electronsSun.size()+" "+applet.map(producedSpeed, 0, 1, 0, 5));
		
		if(electronsSun.size()<applet.map(producedSpeed, 0, 1, 0, 5)){
			//System.out.println("new");
			electronsSun.add(new SunParticle(applet, canvas, producedSpeed, color[2]));
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
		
		//if(produced>consumed){
			//smoothCircle(canvas.width/2);
			//smoothCircleConsumed(mapConsumed2);
		//}else{
			smoothCircleConsumed(canvas.width/2);
			//smoothCircle(mapProduce2);
		//}
		
		for (int e = 0; e < electronsGrid.size(); e++) {
			if (electronsGrid.get(e).dead()) {
				electronsGrid.remove(e);
			}
		}

		for (int e = 0; e < electronsGrid.size(); e++) {
			electronsGrid.get(e).display();
		}
		
		for (int e = 0; e < electronsOnSite.size(); e++) {
			if (electronsOnSite.get(e).dead()) {
				electronsOnSite.remove(e);
			}
		}

		for (int e = 0; e < electronsOnSite.size(); e++) {
			electronsOnSite.get(e).display();
		}
		
		for (int e = 0; e < electronsSun.size(); e++) {
			if (electronsSun.get(e).dead()) {
				electronsSun.remove(e);
			}
		}

		for (int e = 0; e < electronsSun.size(); e++) {
			electronsSun.get(e).display();
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

		
		float c1 = color[3] >> 16 & 0xFF;
		float c2 = color[3] >> 8 & 0xFF;;
		float c3 = color[3] & 0xFF;
		
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
		
		float c1 = color[3] >> 16 & 0xFF;
		float c2 = color[3] >> 8 & 0xFF;;
		float c3 = color[3] & 0xFF;
		
		canvas.fill(calcColor(100, applet.color(c1-50, c2-50, c3-50, 88), applet.color(c1-100, c2-100, c3-100, 88), applet.color(c1-100, c2-100, c3-100, 88)));

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
		
		produced = api.getCurrentGen() - api.getCurrentCons(); //api.getLastSiteDataEntry().energy_generated;
		if(produced < 0){
			produced = 0;
		}
		producedSpeed = produced/MAX_CONSUMPTION;
		
		//imp = consumed - produced;

		consumedOnSite = api.getCurrentGen();
		consumedOnSiteSpeed = consumedOnSite/MAX_CONSUMPTION;
		
		consumed = api.getCurrentCons() - api.getCurrentGen();
		if(consumed < 0){
			consumed = 0;
		}
		consumedSpeed = consumed/MAX_CONSUMPTION;
		//System.out.println("Consumed: "+consumed+" Produced: "+produced);
		
		change_consumption = api.getChangeCons();
		max_consumption = api.getMaxCons();
	}
}