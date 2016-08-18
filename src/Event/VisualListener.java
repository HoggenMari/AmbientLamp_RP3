package Event;

import java.util.EventListener;

public interface VisualListener extends EventListener {

	public void visualsChanged(VisualEvent e);

}