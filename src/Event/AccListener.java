package Event;

import java.util.EventListener;

public interface AccListener extends EventListener {

  public void throwEvent(AccEvent e);
  public void accelerometerEvent(AccEvent e);
  //public void particlesChanged(AccEvent e);
  //public void speedChanged(AccEvent e);

}