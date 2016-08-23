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

}
