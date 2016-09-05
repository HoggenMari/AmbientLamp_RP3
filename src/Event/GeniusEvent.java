package Event;
import java.util.EventObject;


public class GeniusEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public final static int GENIUS_MODE_CHANGED = 1001;
    public final static int GENIUS_PAUSED_CHANGED = 1002;

    protected int id;
    protected boolean genius;
    protected boolean geniusPaused;

    public GeniusEvent(Object source, int id, boolean genius, boolean geniusPaused) {
      super(source);
      this.id = id;
      this.genius = genius;
      this.geniusPaused = geniusPaused;
    }
    
    public boolean getGenius() {
        return genius;
    }
    
    public boolean getGeniusPaused() {
    	return geniusPaused;
    }

    public int getID() {
    	return id;
    }
    
  }