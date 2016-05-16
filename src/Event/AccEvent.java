package Event;

import java.util.EventObject;

public class AccEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int THROW_EVENT = 10001;
	public final static int SHAKE_EVENT = 10002;
	public final static int PARALLAX_CHANGED = 10003;

	protected int id;
	protected int speedX, speedY, speedZ;
	protected float accX, accY, accZ;
	protected String password;
	private float gyroX;
	private float gyroY;
	private float gyroZ;

	public AccEvent(Object source, int id, int speedZ, float accZ) {
		super(source);
		this.id = id;
		this.speedZ = speedZ;
		this.accZ = accZ;
	}

	public AccEvent(Object source, int id, float accX, float accY, float accZ,
			float gyroX, float gyroY, float gyroZ) {
		super(source);
		this.id = id;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
		this.gyroX = gyroX;
		this.gyroY = gyroY;
		this.gyroZ = gyroZ;
	}

	int getID() {
		return id;
	}

	public float getSpeedZ() {
		return speedZ;
	}

	public float getAccX() {
		return accX;
	}

	public float getAccY() {
		return accY;
	}

	public float getAccZ() {
		return accZ;
	}

	public float getGyroX() {
		return gyroX;
	}

	public float getGyroY() {
		return gyroY;
	}

	public float getGyroZ() {
		return gyroZ;
	}

}