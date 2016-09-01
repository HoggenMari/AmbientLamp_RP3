package Event;
import java.util.EventObject;


public class SensorEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int BRIGHTNESS_CHANGED = 10001;
    public final static int PARTICLES_CHANGED = 10002;
    public final static int SPEED_CHANGED = 10003;
    public final static int GENIUS_MODE_CHANGED = 1004;
	public final static int SATURATION_CHANGED = 10001;

    protected int id;
    protected float brightness, saturation, particles, speed;
    protected String password;
  
    public SensorEvent(Object source, int id, float brightness, float saturation, float particles, float speed) {
      super(source);
      this.id = id;
      this.brightness = brightness;
      this.saturation = saturation; 
      this.particles = particles;
      this.speed = speed;
    }
    
    int getID() {
        return id;
    }
  
    public float getBrightness() {
      return brightness;
    }
    
    public float getSaturation() {
        return saturation;
      }
  
    public float getParticles() {
      return particles;
    }
    
    public float getSpeed() {
        return speed;
    }
    
  }