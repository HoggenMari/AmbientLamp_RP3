package SolarAPI;

import java.util.EventListener;

public interface SolarListener extends EventListener {

  public void liveSiteDataChanged();

}