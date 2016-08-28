package Event;
import java.util.EventListener;

public interface GeniusListener extends EventListener {

  public void geniusModeChanged(GeniusEvent e);

}