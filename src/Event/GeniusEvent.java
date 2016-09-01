package Event;
import java.util.EventObject;


public class GeniusEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public final static int GENIUS_MODE_CHANGED = 1001;

    protected boolean genius;
    protected boolean geniusPaused;

    public GeniusEvent(Object source, boolean genius, boolean geniusPaused) {
      super(source);
      this.genius = genius;
      this.geniusPaused = geniusPaused;
    }
    
    public boolean getGenius() {
        return genius;
    }
    
    public boolean getGeniusPaused() {
    	return geniusPaused;
    }
    
  }