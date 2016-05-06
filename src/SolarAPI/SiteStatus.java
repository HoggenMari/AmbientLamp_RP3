package SolarAPI;


public class SiteStatus {

	public enum STATUS {good, under_investigation, action_owner, action_installer, action_sa};
	
	public String status;
	
	public SiteStatus(String status) {
		this.status = status;
	}

}
