package Event;

import java.util.EventListener;

public interface MusicListener extends EventListener {

	public void averagePowerValueChanged(MusicEvent e);

}