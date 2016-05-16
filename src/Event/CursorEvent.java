package Event;
import java.util.EventObject;


public class CursorEvent extends EventObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int CURSOR_CHANGED = 10001;

    protected int id;
	private float cursorX;
	private float cursorY;
  
    public CursorEvent(Object source, int id, float cursorX, float cursorY) {
      super(source);
      this.id = id;
      this.cursorX = cursorX;
      this.cursorY = cursorY;
    }
    
    int getID() {
        return id;
    }
  
    public float getCursorX() {
      return cursorX;
    }
  
    public float getCursorY() {
      return cursorY;
    }
    
   
  }