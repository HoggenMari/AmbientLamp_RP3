package Event;
import java.util.EventListener;

public interface CursorListener extends EventListener {

  public void cursorChanged(CursorEvent e);


}