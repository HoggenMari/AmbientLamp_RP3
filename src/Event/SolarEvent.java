package Event;

import java.util.EventObject;

public class SolarEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int SOLAR_CHANGED = 10001;

	protected int id;
	int solarValue;

	public SolarEvent(Object source, int id, int solarValue) {
		super(source);
		this.id = id;
		this.solarValue = solarValue;
	}

	public int getID() {
		return id;
	}

	public int getSolarValue() {
		return solarValue;
	}

}