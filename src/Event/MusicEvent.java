package Event;

import java.util.EventObject;

public class MusicEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int AVERAGEPOWERVALUE_CHANGED = 10001;

	protected int id;
	float averagePowerValue;

	public MusicEvent(Object source, int id, float averagePowerValue) {
		super(source);
		this.id = id;
		this.averagePowerValue = averagePowerValue;
	}

	public int getID() {
		return id;
	}

	public float getAveragePowerValue() {
		return averagePowerValue;
	}

}