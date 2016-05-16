package Event;

import java.util.EventObject;

public class ModeEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int MODE_CHANGED = 10001;

	protected int id;
	int modeIndex;

	public ModeEvent(Object source, int id, int modeIndex) {
		super(source);
		this.id = id;
		this.modeIndex = modeIndex;
	}

	public int getID() {
		return id;
	}

	public int getModeIndex() {
		return modeIndex;
	}

}