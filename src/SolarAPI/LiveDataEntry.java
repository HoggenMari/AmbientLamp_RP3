package SolarAPI;

public class LiveDataEntry {
	
	public float current;
    public float energy;
    public float frequency;
    public float power;
    public float reactivePower;
    public float reactive_eng;
    public float voltage;
    public String time;

    public LiveDataEntry(float current, float energy, float frequency, float power, float reactivePower, float reactive_eng, float voltage, String time) {
    	this.current = current;
    	this.energy = energy;
    	this.frequency = frequency;
    	this.power = power;
    	this.reactivePower = reactivePower;
    	this.reactive_eng = reactive_eng;
    	this.time = time;
    	this.voltage = voltage;
    }
    
    public String toString(){
    	String str = "LiveDataEntry: "+current+" "+" "+energy+" "+frequency+" "+power+" "+reactivePower+" "+reactive_eng+" "+voltage+" "+time;
    	return str;
    }

}
