package test;

public class LiveDataEntry {
	
	public float current;
    public float energy;
    public float reactivePower;
    public float reactive_eng;
    public float voltage;
    public String time;

    public LiveDataEntry(float current, float energy, float reactivePower, float reactive_eng, float voltage, String time) {
    	this.current = current;
    	this.energy = energy;
    	this.reactivePower = reactivePower;
    	this.reactive_eng = reactive_eng;
    	this.voltage = voltage;
    	this.time = time;
    }
    
    public String toString(){
    	String str = "LiveDataEntry: "+current+" "+" "+energy+" "+reactivePower+" "+reactive_eng+" "+voltage+" "+time;
    	return str;
    }

}
