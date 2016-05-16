package Event;

import java.util.EventListener;

public interface ModeListener extends EventListener {

	public void modeChanged(ModeEvent e);

}