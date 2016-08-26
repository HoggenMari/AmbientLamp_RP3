package SolarAPI;

public class LiveSiteDataEntry {
	
	int cnt;
	float cons;
	float gen;
	String t_stamp;
	
	public LiveSiteDataEntry(int cnt, float cons, float gen, String t_stamp){
		this.cnt = cnt;
		this.cons = cons;
		this.gen = gen;
		this.t_stamp = t_stamp;
	}
	
	public String toString(){
    	String str = "LiveSiteDataEntry: "+cnt+" "+" "+cons+" "+gen+" "+t_stamp;
    	return str;
    }
	
	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public float getCons() {
		return cons;
	}

	public void setCons(float cons) {
		this.cons = cons;
	}

	public float getGen() {
		return gen;
	}

	public void setGen(float gen) {
		this.gen = gen;
	}

	public String getT_stamp() {
		return t_stamp;
	}

	public void setT_stamp(String t_stamp) {
		this.t_stamp = t_stamp;
	}
}
