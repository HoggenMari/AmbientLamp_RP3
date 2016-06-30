package SolarAPI;

import java.util.ArrayList;

public class LiveData {
	
    public String circuit;
    public String circuit_name;
    public String monitors;
    public String watt_device_id;
	public ArrayList<LiveDataEntry> live_data = new ArrayList<LiveDataEntry>();
    
    public LiveData(String circuit, String circuit_name, ArrayList<LiveDataEntry> live_data, String monitors, String watt_device_id) {
    	
    	this.circuit = circuit;
    	this.circuit_name = circuit_name;
    	this.live_data  = live_data;
    	this.monitors = monitors;
    	this.watt_device_id = watt_device_id;
    	
    	
    }
    
    public String toString(){
    	String str = "LiveDataEntry: "+circuit+" "+circuit_name+" "+live_data.toString()+" "+monitors+" "+watt_device_id;
    	return str;
    }

}
