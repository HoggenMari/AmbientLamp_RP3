package Event;

import java.util.EventObject;
import java.util.HashMap;

public class VisualEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int VISUAL_CHANGED = 10001;
	
	HashMap<String,Visual> visuals = new HashMap<String,Visual>();
    protected int id;

	public VisualEvent(Object source, int id, HashMap<String, Visual> visuals) {
		super(source);
		this.id = id;
		this.visuals = visuals;
		// TODO Auto-generated constructor stub
	}
	
	public HashMap<String, Visual> getVisualList(){
		return visuals;
	}
	
}
