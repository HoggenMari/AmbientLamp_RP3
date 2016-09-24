package Event;

import java.util.ArrayList;

public class Visual {
	
	String id;
	String name;
	int index;
	String[] colors;
	boolean checked;
	boolean active;
	boolean geniusActive;
	boolean pausedActive;
	boolean settingActive;
	boolean notification;

	public Visual(String id, String name, int index, String[] colors, boolean checked, boolean active, boolean geniusActive, boolean pausedActive, boolean settingActive, boolean notification ){
		this.id = id;
		this.name = name;
		this.index = index;
		this.colors = colors;
		this.checked = checked;
		this.active = active;
		this.geniusActive = geniusActive;
		this.pausedActive = pausedActive;
		this.settingActive = settingActive;
		this.notification = notification;
	}
	
	public Visual(String id, String name, int index, String[] colors, boolean checked, boolean active, boolean geniusActive, boolean pausedActive, boolean settingActive){
		this.id = id;
		this.name = name;
		this.index = index;
		this.colors = colors;
		this.checked = checked;
		this.active = active;
	}

	public Visual(String name, String[] colors, boolean checked, boolean active, boolean geniusActive ){
		this.name = name;
		this.colors = colors;
		this.checked = checked;
		this.active = active;
	}
	
	public String toString(){
		return name+" "+id+" "+" "+colors.toString()+" "+checked+" "+active;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String[] getColors() {
		return colors;
	}

	public void setColors(String[] colors) {
		this.colors = colors;
	}
	
	public ArrayList<int[]> getColorsAsRGB() {
		ArrayList<int[]> colorList = new ArrayList<int[]>();
		for(int i=0; i<colors.length; i++){
			colorList.add(new int[] {
					Integer.valueOf(colors[i].substring(1, 3), 16), 
					Integer.valueOf(colors[i].substring(3, 5), 16),
					Integer.valueOf(colors[i].substring(5, 7), 16)	
			});
		}
		
		return colorList;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isPausedActive() {
		return pausedActive;
	}

	public void setPausedActive(boolean pausedActive) {
		this.pausedActive = pausedActive;
	}
	
	public boolean isSettingActive() {
		return settingActive;
	}

	public void setSettingActive(boolean settingActive) {
		this.settingActive = settingActive;
	}
	
	public boolean isGeniusActive() {
		return geniusActive;
	}

	public void setGeniusActive(boolean geniusActive) {
		this.geniusActive = geniusActive;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}
}