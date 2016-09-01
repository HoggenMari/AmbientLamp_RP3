package Event;
import java.util.EventListener;

public interface SensorListener extends EventListener {

  public void brightnessChanged(SensorEvent e);
  public void saturationChanged(SensorEvent e);
  public void particlesChanged(SensorEvent e);
  public void speedChanged(SensorEvent e);

}