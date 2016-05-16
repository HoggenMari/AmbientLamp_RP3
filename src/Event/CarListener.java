package Event;

import java.util.EventListener;

public interface CarListener extends EventListener {

	public void carChanged(CarEvent e);

}