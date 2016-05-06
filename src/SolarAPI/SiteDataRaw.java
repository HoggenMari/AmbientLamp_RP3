package SolarAPI;

import java.util.ArrayList;

public class SiteDataRaw {
	
	public int circuit;
	public String circuit_name;
	public String monitors;
	public int polarity;
	public String watt_device_id;
	public ArrayList<SiteDataRawIntervall> data = new ArrayList<SiteDataRawIntervall>();
	
	public SiteDataRaw(int circuit, String circuit_name, String monitors, int polarity, String watt_device_id, ArrayList<SiteDataRawIntervall> data){
		this.circuit = circuit;
		this.circuit_name = circuit_name;
		this.polarity = polarity;
		this.watt_device_id = watt_device_id;
		this.data = data;
		
		
	}
	
	public String toString(){
		String str = "SiteDataRaw: "+circuit+" "+circuit_name+" "+polarity+" "+watt_device_id+" "+data.toString();
		return str;
	}

}
