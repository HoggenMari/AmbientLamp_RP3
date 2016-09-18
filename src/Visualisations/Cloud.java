package Visualisations;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class Cloud {

	private PApplet p;
	private int cloudIndex;
	// private ImageArray imgArray1;
	// private ImageArray imgArray2;
	private boolean change;
	private int smoothIndex;
	private File dir;
	private ArrayList<ImageArray> imgArrayList;
	private String[] list;
	private int index = 0;
	private int cloudType;
	private boolean init;

	public Cloud(PApplet p) {
		this.p = p;
		this.imgArrayList = new ArrayList<ImageArray>();

		dir = new File("/Users/hoggenmueller/Documents/MasterArbeit/Software/Eclipse/SolarAnalytics/src/cloud");
		list = dir.list();

		for (String s : list) {
			if (s.contains("cloud")) {
				imgArrayList.add(new ImageArray(p, new File(dir + "/" + s)));
				// System.out.println(s);
			}
		}

		init = true;
		System.out.println("Finished");
	}

	public boolean isInit() {
		return init;
	}

	public void changeCloud(int cloudType) {
		if (!change) {
			this.cloudType = cloudType;
		}
	}

	public PImage getCloudImage() {
		// System.out.println("GO");
		PImage img = imgArrayList.get(index).nextFrame();
		p.image(img, 0, 200);
		PImage img2 = imgArrayList.get(cloudType).nextFrame();
		p.image(img2, 0, 300);
		// img.resize(17, 12);

		if (change == false && cloudType != index) {
			change = true;
			smoothIndex = 0;
			System.out.println("Change1");
		}

		if (change && smoothIndex <= 50) {
			System.out.println("Change2");
			// PImage img2 = imgArrayList.get(1).nextFrame();
			p.image(img2, 0, 300);
			// img2.resize(17, 12);
			PGraphics pg = p.createGraphics(img.width, img.height, PConstants.P2D);
			pg.beginDraw();
			for (int x = 0; x < img.width; x++) {
				for (int y = 0; y < img.height; y++) {
					int c = p.lerpColor(img.get(x, y), img2.get(x, y),
							(float) (smoothIndex / 50.0)); // color
															// c =
															// img2.get(x,y);
					pg.noStroke();
					pg.fill(c);
					pg.rect(x, y, 1, 1);
				}
			}
			pg.endDraw();
			p.image(pg, 0, 400);
			img = pg;
			smoothIndex++;

		} else if (change) {
			System.out.println("Change3");
			smoothIndex = 0;
			change = false;
			index = cloudType;
			img = imgArrayList.get(index).nextFrame();

		}

		return img;

	}

	public PGraphics draw() {
		PImage sky = getCloudImage();
		PGraphics sky_copy = p.createGraphics(sky.width, sky.height, PConstants.P2D);
		sky_copy.beginDraw();
		sky_copy.image(sky, 0, 0);
		sky_copy.endDraw();

		sky_copy.resize(17, 12);
		// image(sky, 200, 0);
		sky_copy.filter(PConstants.BLUR, 1);

		p.image(sky_copy, 500, 50);

		// p.image(downscale(sky_copy, 0), 200, 50);

		PImage img = downscale(sky_copy, 0);
		return (PGraphics) img;
	}

	PGraphics downscale(PGraphics pg, int intensity) {
		PImage in = pg.get();
		in.filter(PConstants.BLUR, intensity);
		// in.resize(17, 120);
		PGraphics out = p.createGraphics(170, 120, PConstants.P2D);
		out.beginDraw();
		for (int ix = 0; ix < in.width; ix++) {
			for (int iy = 0; iy < in.height; iy++) {
				out.fill(in.get(ix, iy));
				out.noStroke();
				out.rect(ix * 10, iy * 10, ix * 10 + 10, iy * 10 + 10);
			}
		}
		// out.image(in, 0, 0);
		out.endDraw();
		return out;
	}
}
