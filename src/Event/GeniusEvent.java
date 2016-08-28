package Event;
import java.util.EventObject;


public class GeniusEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    public final static int GENIUS_MODE_CHANGED = 1001;

    protected boolean genius;
  
    public GeniusEvent(Object source, boolean genius) {
      super(source);
      this.genius = genius;
    }
    
    public boolean getGenius() {
        return genius;
    }
    
  }