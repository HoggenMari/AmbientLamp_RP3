package FenoDMX;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import artnet4j.ArtNet;
import artnet4j.ArtNetException;
import artnet4j.packets.ArtDmxPacket;
import hypermedia.net.UDP;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;


public class Screen extends Thread {

	static byte[] KEY = { 0x6f, 0x6e, 0x65, 0x66 };
	static byte[] CMD_DATA = { 0x00, 0x02, 0x00, (byte) 0xff };
	static byte[] CMD_SYNC = { (byte) 0xff, (byte) 0xff, 0x02, 0x00 };
	static int CONT_ID = 0;
	
	int dataIn;
	int controller;
	
	static byte[] data = new byte[5000];
	static int data_counter = 0;

	static byte[] sync_data = new byte[16];

	static final String HOST = "193.168.0.98";
	static int PORT = 53280;
	private static InetAddress INET_ADDR;
	private static DatagramSocket SOCKET;
	ArtNet artnet;
	private int sequenceID = 0;
	
	//iPad prototyping
	static final String HOST_IPAD = "192.168.0.100";
	static int PORT_IPAD = 53281;
	UDP udp;
	
	private int resX;
	private int resY;
	private PApplet p;
	private PGraphics pgMain;

	public Screen(PApplet p, int resX, int resY, int controller) {
		this.p = p;
		this.resX = resX;
		this.resY = resY;
		this.pgMain = p.createGraphics(resX, resY,PConstants.P2D);
		this.controller = controller;
		
		if(controller == 0){
			try {
				SOCKET = new DatagramSocket();
			} catch (SocketException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		}else if(controller == 1){
			artnet = new ArtNet();
			try {
			  artnet.start();
			} catch (SocketException e) {
			  throw new AssertionError(e);
			} catch (ArtNetException e) {
			  throw new AssertionError(e);
			}
		}
		
		udp = new UDP( this, 53282 );
	}

	public void addLayer(PGraphics pg) {
		//pgMain.clear();
		PImage img = pg.get(0, 0, pg.width, pg.height);
		pgMain.beginDraw();
		pgMain.image(img, 0, 0);
		pgMain.endDraw();
		// p.image(pgMain, 0, 100);
	}

	public void drawOnGui() {
		for (int x = 0; x < resX; x++) {
			for (int y = 0; y < resY; y = y + 1) {
				p.stroke(0);
				p.strokeWeight(1);
				int rgb = pgMain.get(x, y);
				// System.out.println("RGB: "+rgb);
				p.fill(rgb);
				p.rect(x * 10 + 10, y * 10 + 10, 10, 10);
			}
		}
	}

	public void start() {
		super.start();
	}

	public void run() {
		while (true) {
			//send();
			/*
			 * try { sleep((long)(100)); } catch (Exception e) { }
			 */
		}
	}

	public void send(int...nRow ){
		if(controller == 0){
			//sendFeno(nRow);
		}else if(controller == 1){
			sendArtNet(nRow);
			//sendIpad(nRow);
		}
	}
	
	public void sendIpad(int... nRow) {
		
		String data = "";
		
		for (int ix = 0; ix < 17; ix++) {
			for (int iy = 0; iy < 12; iy++) {
				int rgb = pgMain.get(ix, iy);
				data = data+"#";
				data = data+PApplet.hex(rgb, 6);
			}
		}
		
		
		String message  = data;	// the message to send
	    int port        = 53282;		// the destination port
	    
	    // formats the message for Pd
	    message = message+";\n";
	    
	    //System.out.println(message);
	    // send the message
	    udp.send( message, HOST_IPAD, port );
		
	}
	
	public void sendFeno(int... nRow) {

		// key
		data[0] = (byte) 0x66;
		data[1] = (byte) 0x65;
		data[2] = (byte) 0x6e;
		data[3] = (byte) 0x6f;

		// cmd_data
		data[4] = (byte) 0x00;
		data[5] = (byte) 0x02;
		data[6] = (byte) 0x00;
		data[7] = (byte) 0xff;

		// controller_id
		data[8] = (byte) 0x00;
		data[9] = (byte) 0x00;
		data[10] = (byte) 0x00;
		data[11] = (byte) 0x00;

		// frameCounter
		data[12] = (byte) 0x00;
		data[13] = (byte) 0x00;
		data[14] = (byte) 0x00;
		data[15] = (byte) 0x00;

		// n_universe
		data[16] = (byte) 0x00;
		data[17] = (byte) 0x05;

		data_counter = 18;

		for (int i_universe = 0; i_universe < 5; i_universe++) {
			// i_universe
			data[data_counter++] = (byte) 0x00;
			data[data_counter++] = (byte) i_universe;

			data[data_counter++] = (byte) 0x01;
			data[data_counter++] = (byte) 0xfe;

			data[data_counter++] = (byte) 0x00;

			int sum = 0;
			for (int i = 0; i < i_universe; i++) {
				sum += nRow[i];
			}

			// System.out.println("Sum: "+sum);

			for (int ix = sum; ix < sum + nRow[i_universe]; ix++) {
				for (int iy = 0; iy < 12; iy++) {
					int rgb = pgMain.get(ix, iy);
					data[data_counter + 2] = (byte) (rgb & 0xff);
					data[data_counter + 1] = (byte) (rgb >> 8 & 0xff);
					data[data_counter] = (byte) (rgb >> 16 & 0xff);
					data_counter += 3;
					// System.out.println("DATACOUNT: "+data_counter);
				}
			}

			for (int j = 0; j < 510 - (nRow[i_universe] * 12 * 3); j++) {
				data[data_counter++] = (byte) 0;
			}

			// System.out.println("STOP: "+data_counter);

			/*
			 * for(int j=0; j<170; j++){ int rgb = pg.get(0, 0);
			 * data[data_counter+2] = (byte) (rgb & 0xff); data[data_counter+1]
			 * = (byte) ((rgb >> 8) & 0xff); data[data_counter] = (byte) ((rgb
			 * >> 16) & 0xff); data_counter +=3; }
			 */

			// data_counter += (170*3);
			data[data_counter++] = (byte) 0;

		}

		// System.out.println(data_counter);
		// for(int dc=0; dc<data.length; dc++){
		// System.out.println(dc+" "+data[dc]);
		// }

		try {
			INET_ADDR = InetAddress.getByName(HOST);
			DatagramPacket dp = new DatagramPacket(data, data_counter,
					INET_ADDR, PORT);
			SOCKET.send(dp);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		try {
			Thread.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// key
		sync_data[0] = (byte) 0x66;
		sync_data[1] = (byte) 0x65;
		sync_data[2] = (byte) 0x6e;
		sync_data[3] = (byte) 0x6f;

		// cmd_data
		sync_data[4] = (byte) 0x00;
		sync_data[5] = (byte) 0x02;
		sync_data[6] = (byte) 0xff;
		sync_data[7] = (byte) 0xff;

		// controller_id
		sync_data[8] = (byte) 0x00;
		sync_data[9] = (byte) 0x00;
		sync_data[10] = (byte) 0x00;
		sync_data[11] = (byte) 0x00;

		// framecounter
		sync_data[12] = (byte) 0x00;
		sync_data[13] = (byte) 0x00;
		sync_data[14] = (byte) 0x00;
		sync_data[15] = (byte) 0x00;

		try {
			INET_ADDR = InetAddress.getByName(HOST);
			DatagramPacket dp2 = new DatagramPacket(sync_data,
					sync_data.length, INET_ADDR, PORT);
			SOCKET.send(dp2);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		/*
		 * try { Thread.sleep(50); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}
	
	public void sendArtNet(int... nRow) {
		
		for (int i_universe = 0; i_universe < nRow.length; i_universe++) {

		ArtDmxPacket dmx = new ArtDmxPacket();
	    dmx.setUniverse(0, i_universe);
	    dmx.setSequenceID(sequenceID % 255);
	    byte[] data = new byte[(nRow[i_universe] * 12 * 3)];
	    
	    int sum = 0;
		for (int i = 0; i < i_universe; i++) {
			sum += nRow[i];
		}
		
		int data_counter = 0;
	    for (int ix = sum; ix < sum + nRow[i_universe]; ix++) {
			for (int iy = 0; iy < 12; iy++) {
				int rgb = pgMain.get(ix, iy);
				data[data_counter + 2] = (byte) (rgb & 0xff);
				data[data_counter + 1] = (byte) (rgb >> 8 & 0xff);
				data[data_counter] = (byte) (rgb >> 16 & 0xff);
				data_counter += 3;
				// System.out.println("DATACOUNT: "+data_counter);
			}
		}

		//for (int j = 0; j < 510 - (nRow[i_universe] * 12 * 3); j++) {
		//	data[data_counter++] = (byte) 0;
		//}
		
		//for(int i=0; i<data.length; i++){
		//	System.out.println(data[i]);
		//}
	    
	    dmx.setDMX(data, data.length);
	    artnet.unicastPacket(dmx, HOST);
	    sequenceID++;
	    
	    p.delay(7);
	    
		}

	}
	
}
