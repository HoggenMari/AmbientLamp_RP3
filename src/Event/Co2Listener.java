package Event;

import java.util.EventListener;

public interface Co2Listener extends EventListener {

	public void co2Changed(Co2Event e);

}