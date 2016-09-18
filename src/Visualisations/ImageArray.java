package Visualisations;

import java.io.File;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class ImageArray {

	ArrayList<PImage> imgList;
	private int index;
	private int smoothIndex;
	private PApplet p;

	public ImageArray(PApplet p, File dir) {
		this.p = p;
		imgList = new ArrayList<PImage>();
		// File dir = new
		// File("/Volumes/Externe Festplatte 1/cloudvideos/cloud2");
		String[] list = dir.list();

		if (list == null) {
			System.out.println("Folder does not exist or cannot be accessed.");
		} else {
			int i = 0;
			for (String s : list) {
				if (s.contains("cloud")) {

					// System.out.println(s);
					PImage img = p.loadImage(dir + "/" + s);
					img.filter(p.BLUR, 1);
					img.resize(17, 12);
					imgList.add(img);
				}
			}
		}
	}

	PImage nextFrame() {
		PImage img = imgList.get(index);
		// img.filter(p.BLUR, 1);
		// img.resize(17, 12);
		if (index < imgList.size() - 1) {
			index++;
			smoothIndex = 0;
		} else if (smoothIndex < 40) {
			PGraphics pg = p.createGraphics(img.width, img.height, PConstants.P2D);
			PImage img1 = imgList.get(imgList.size() - 1);
			// img.filter(p.BLUR, 1);
			// img1.resize(17, 12);
			PImage img2 = imgList.get(0);
			// img2.resize(17, 12);
			pg.beginDraw();
			for (int x = 0; x < img.width; x++) {
				for (int y = 0; y < img.height; y++) {
					int c = p.lerpColor(img1.get(x, y), img2.get(x, y),
							(float) (smoothIndex / 40.0));
					// color c = img2.get(x,y);
					pg.noStroke();
					pg.fill(c);
					pg.rect(x, y, 1, 1);
				}
			}
			pg.endDraw();
			img = pg;
			smoothIndex++;
		} else {
			index = 1;
			smoothIndex = 0;
			img = imgList.get(0);
		}
		// img.resize(17, 12);
		return img;

	}

}
