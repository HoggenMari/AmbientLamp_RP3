package test;

public class SiteData {
	
	public float energy_generated;
	public float energy_consumed;
	public float energy_expected;
	public String t_stamp;
	
	public SiteData(){
		
	}

	public SiteData(float energy_generated, float energy_consumed, float energy_expected, String t_stamp) {
		this.energy_generated = energy_generated;
		this.energy_consumed = energy_consumed;
		this.energy_expected = energy_expected;
		this.t_stamp = t_stamp;
	}
	
	public SiteData(float energy_generated, float energy_consumed, String t_stamp) {
		this.energy_generated = energy_generated;
		this.energy_consumed = energy_consumed;
		this.t_stamp = t_stamp;
	}
	
	public String toString(){
		String str = "SiteDataEntry: "+energy_generated+" "+energy_consumed+" "+energy_expected+" "+t_stamp;
		return str;
	}

}
