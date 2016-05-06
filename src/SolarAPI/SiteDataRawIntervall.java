package SolarAPI;

public class SiteDataRawIntervall {
	
	public float apparentPower;
	public float current;
	public float energy;
	public float power;
	public float powerFactor;
	public float reactiveEnergy;
	public float reactivePower;
	public long time;
	public float voltage;
	
	public SiteDataRawIntervall(float apparentPower, float current, float energy, float power, float powerFactor, float reactiveEnergy, float reactivePower, long time, float voltage){
		this.apparentPower = apparentPower;
		this.current = current;
		this.energy = energy;
		this.power = power;
		this.powerFactor = powerFactor;
		this.reactiveEnergy = reactiveEnergy;
		this.reactivePower = reactivePower;
		this.time = time;
		this.voltage = voltage;
	}
	
	public String toString(){
		String str = "SiteDataRawIntervall: "+apparentPower+" "+current+" "+energy+" "+power+" "+powerFactor+" "+reactiveEnergy+" "+reactivePower+" "+time+" "+voltage;
		return str;
	}

}
