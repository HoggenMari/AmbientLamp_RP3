package Event;

import java.util.EventListener;

public interface SolarListener extends EventListener {

	public void solarChanged(SolarEvent e);

}