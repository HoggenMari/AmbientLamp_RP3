package Event;

import java.util.EventObject;
import java.util.HashMap;

public class VisualEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int VISUAL_CHANGED = 10000;
	public final static int VISUAL_NAME = 10001;
	public final static int VISUAL_INDEX = 10002;
	public final static int VISUAL_COLORS = 10003;
	public final static int VISUAL_ACTIVE = 10004;
	public final static int VISUAL_GENIUSACTIVE = 10005;
	public final static int VISUAL_NOTIFICATION = 10006;
	public final static int VISUAL_CHECKED = 10007;

	
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
	
	public int getID(){
		return id;
	}
	
}
