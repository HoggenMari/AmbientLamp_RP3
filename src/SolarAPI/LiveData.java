package SolarAPI;

import java.util.ArrayList;

public class LiveData {
	
    public String circuit;
    public String monitors;
	public ArrayList<LiveDataEntry> data = new ArrayList<LiveDataEntry>();
    
    public LiveData(String circuit, String monitors, ArrayList<LiveDataEntry> data) {
    	
    	this.circuit = circuit;
    	this.monitors = monitors;
    	this.data  = data;
    	
    	
    }
    
    public String toString(){
    	String str = "LiveDataEntry: "+circuit+" "+monitors+" "+data.toString();
    	return str;
    }

}
