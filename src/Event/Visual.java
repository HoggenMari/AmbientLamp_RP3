package Event;

public class Visual {
	
	String id;
	String name;
	String[] colors;
	boolean checked;
	boolean active;

	public Visual(String id, String name, String[] colors, boolean checked, boolean active ){
		this.id = id;
		this.name = name;
		this.colors = colors;
		this.checked = checked;
		this.active = active;
	}

	public Visual(String name, String[] colors, boolean checked, boolean active ){
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
}
