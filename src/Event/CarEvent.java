package Event;

import java.util.EventObject;

public class CarEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int CAR_CHANGED = 10001;

	protected int id;
	int carValue;

	public CarEvent(Object source, int id, int carValue) {
		super(source);
		this.id = id;
		this.carValue = carValue;
	}

	public int getID() {
		return id;
	}

	public int getCarValue() {
		return carValue;
	}

}