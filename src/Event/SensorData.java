package Event;

import java.util.HashMap;

import javax.swing.event.EventListenerList;

public class SensorData {

	private static SensorData instance;

	// private SensorEventMulticaster multicaster;
	private float brightness, saturation, particles, speed;
	private String brightnessID, saturationID;
	private boolean genius, geniusPaused;
	private String geniusID;
	private int speedZ;
	private float accX, accY, accZ;
	private float gyroX, gyroY, gyroZ;
	private float cursorX, cursorY;
	EventListenerList listenerList = new EventListenerList();
	SensorEvent sensorEvent = null;
	private int modeIndex;
	private int solarValue;
	private int carValue;
	private int co2Value;
	private float averagePowerValue;
	HashMap<String,Visual> visuals = new HashMap<String,Visual>();

	private SensorData () {}

	public static synchronized SensorData getInstance() {
		if (SensorData.instance == null) {
			SensorData.instance = new SensorData ();
		}
		return SensorData.instance;
    }

	public void addGeniusListener(GeniusListener l) {
		listenerList.add(GeniusListener.class, l);
	}

	public void removeGeniusListener(GeniusListener l) {
		listenerList.remove(GeniusListener.class, l);
	}
	
	public void addSensorListener(SensorListener l) {
		listenerList.add(SensorListener.class, l);
	}

	public void removeSensorListener(SensorListener l) {
		listenerList.remove(SensorListener.class, l);
	}

	public void addAccListener(AccListener l) {
		listenerList.add(AccListener.class, l);
	}

	public void removeAccListener(AccListener l) {
		listenerList.remove(AccListener.class, l);
	}

	public void addCursorListener(CursorListener l) {
		listenerList.add(CursorListener.class, l);
	}

	public void removeCursorListener(CursorListener l) {
		listenerList.remove(CursorListener.class, l);
	}

	public void addModeListener(ModeListener l) {
		listenerList.add(ModeListener.class, l);
	}

	public void removeModeListener(ModeListener l) {
		listenerList.remove(ModeListener.class, l);
	}

	public void addSolarListener(SolarListener l) {
		listenerList.add(SolarListener.class, l);
	}

	public void removeSolarListener(SolarListener l) {
		listenerList.remove(SolarListener.class, l);
	}

	public void addCarListener(CarListener l) {
		listenerList.add(CarListener.class, l);
	}

	public void removeCarListener(CarListener l) {
		listenerList.remove(CarListener.class, l);
	}

	public void addCo2Listener(Co2Listener l) {
		listenerList.add(Co2Listener.class, l);
	}

	public void removeCo2Listener(Co2Listener l) {
		listenerList.remove(Co2Listener.class, l);
	}

	public void addMusicListener(MusicListener l) {
		listenerList.add(MusicListener.class, l);
	}

	public void removeMusicListener(MusicListener l) {
		listenerList.remove(MusicListener.class, l);
	}

	public void addVisualListener(VisualListener l) {
		listenerList.add(VisualListener.class, l);
	}

	public void removeVisualListener(VisualListener l) {
		listenerList.remove(VisualListener.class, l);
	}
	
	public void setBrightness(float brightness) {
		this.brightness = brightness;
		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Brightness"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == SensorListener.class) {
				((SensorListener) listeners[i + 1])
						.brightnessChanged(new SensorEvent(this,
								SensorEvent.BRIGHTNESS_CHANGED, brightness, saturation,
								particles, speed));
			}
		}
	}

	public void setParticles(float particles) {
		this.particles = particles;
		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == SensorListener.class) {
				((SensorListener) listeners[i + 1])
						.particlesChanged(new SensorEvent(this,
								SensorEvent.PARTICLES_CHANGED, brightness, saturation,
								particles, speed));
			}
		}
	}

	public void setSpeed(float speed) {
		this.speed = speed;
		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == SensorListener.class) {
				((SensorListener) listeners[i + 1])
						.speedChanged(new SensorEvent(this,
								SensorEvent.SPEED_CHANGED, brightness, saturation,
								particles, speed));
			}
		}
	}

	public void setAccelerometer(float accX, float accY, float accZ) {
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;

		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Accelerometer-Event: "+accY);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == AccListener.class) {
				((AccListener) listeners[i + 1])
						.accelerometerEvent(new AccEvent(this,
								AccEvent.PARALLAX_CHANGED, accX, accY, accZ,
								gyroX, gyroY, gyroZ));
			}
		}
	}

	public void setThrowEvent(int speedZ, float accZ) {
		this.speedZ = speedZ;
		this.accZ = accZ;

		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Throw-Event"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == AccListener.class) {
				((AccListener) listeners[i + 1]).throwEvent(new AccEvent(this,
						AccEvent.THROW_EVENT, speedZ, accZ));
			}
		}
	}

	public void setCursorEvent(float cursorX, float cursorY) {
		this.cursorX = cursorX;
		this.cursorY = cursorY;

		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Cursor"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			System.out.println(listeners[i].getClass());
			if (listeners[i] == CursorListener.class) {
				((CursorListener) listeners[i + 1])
						.cursorChanged(new CursorEvent(this,
								CursorEvent.CURSOR_CHANGED, cursorX, cursorY));
			}
		}
	}

	public void processActionEvent() {

	}

	public void setGyroscope(float gyroX, float gyroY, float gyroZ) {
		this.gyroX = gyroX;
		this.gyroY = gyroY;
		this.gyroZ = gyroZ;

		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Accelerometer-Event: "+accY);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == AccListener.class) {
				((AccListener) listeners[i + 1])
						.accelerometerEvent(new AccEvent(this,
								AccEvent.PARALLAX_CHANGED, accX, accY, accZ,
								gyroX, gyroY, gyroZ));
			}
		}
	}

	public void setMode(int modeIndex) {
		this.modeIndex = modeIndex;

		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == ModeListener.class) {
				((ModeListener) listeners[i + 1]).modeChanged(new ModeEvent(
						this, ModeEvent.MODE_CHANGED, modeIndex));
			}
		}
	}

	public void setSolar(int solarValue) {
		this.solarValue = solarValue;

		//System.out.println(solarValue);

		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == SolarListener.class) {
				((SolarListener) listeners[i + 1]).solarChanged(new SolarEvent(
						this, SolarEvent.SOLAR_CHANGED, solarValue));
			}
		}

	}

	public void setCar(int carValue) {
		this.carValue = carValue;

		//System.out.println(carValue);

		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == CarListener.class) {
				((CarListener) listeners[i + 1]).carChanged(new CarEvent(this,
						CarEvent.CAR_CHANGED, carValue));
			}
		}
	}

	public void setCo2(int co2Value) {
		this.co2Value = co2Value;

		System.out.println(co2Value);

		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == Co2Listener.class) {
				((Co2Listener) listeners[i + 1]).co2Changed(new Co2Event(this,
						Co2Event.CO2_CHANGED, co2Value));
			}
		}

	}

	public void setAveragePowerValue(float averagePowerValue) {
		this.averagePowerValue = averagePowerValue;

		System.out.println(averagePowerValue);

		Object[] listeners = listenerList.getListenerList();

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == MusicListener.class) {
				((MusicListener) listeners[i + 1])
						.averagePowerValueChanged(new MusicEvent(this,
								MusicEvent.AVERAGEPOWERVALUE_CHANGED,
								averagePowerValue));
			}
		}

	}

	public String getBrightnessID() {
		return brightnessID;
	}

	public void setBrightnessID(String brightnessID) {
		this.brightnessID = brightnessID;
	}
	
	public HashMap<String, Visual> getVisualList() {
		return visuals;
	}
	
	public void setVisual(int id) {
		Object[] listeners = listenerList.getListenerList();
		System.out.println("Set Visual");
		
		// System.out.println("Brightness"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == VisualListener.class) {
				((VisualListener) listeners[i + 1])
						.visualsChanged(new VisualEvent(this,
								id, visuals));
			}
		}
	}

	public boolean getGenius() {
		return genius;
	}

	public void setGenius(boolean genius) {
		this.genius = genius;
		
		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Brightness"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == GeniusListener.class) {
				((GeniusListener) listeners[i + 1])
						.geniusModeChanged(new GeniusEvent(this,
								genius, geniusPaused));
			}
		}
	}

	public String getGeniusID() {
		return geniusID;
	}

	public void setGeniusID(String geniusID) {
		this.geniusID = geniusID;
	}

	public String getSaturationID() {
		return saturationID;
	}

	public void setSaturationID(String saturationID) {
		this.saturationID = saturationID;
	}

	public float getSaturation() {
		return saturation;
	}

	public void setSaturation(float saturation) {
		this.saturation = saturation;
		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Brightness"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == SensorListener.class) {
				((SensorListener) listeners[i + 1])
						.saturationChanged(new SensorEvent(this,
								SensorEvent.SATURATION_CHANGED, brightness, saturation,
								particles, speed));
			}
		}
	}

	public boolean isGeniusPaused() {
		return geniusPaused;
	}

	public void setGeniusPaused(boolean geniusPaused) {
		this.geniusPaused = geniusPaused;
		
		Object[] listeners = listenerList.getListenerList();

		// System.out.println("Brightness"+listeners.length);

		for (int i = 0; i < listeners.length; i++) {
			if (listeners[i] == GeniusListener.class) {
				((GeniusListener) listeners[i + 1])
						.geniusModeChanged(new GeniusEvent(this,
								genius, geniusPaused));
			}
		}
	}
	
}
