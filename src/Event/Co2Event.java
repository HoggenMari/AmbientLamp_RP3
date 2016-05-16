package Event;

import java.util.EventObject;

public class Co2Event extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int CO2_CHANGED = 10001;

	protected int id;
	int co2Value;

	public Co2Event(Object source, int id, int co2Value) {
		super(source);
		this.id = id;
		this.co2Value = co2Value;
	}

	public int getID() {
		return id;
	}

	public int getCo2Value() {
		return co2Value;
	}

}