/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package interfaces;

import core.Connection;
import core.NetworkInterface;
import core.Settings;
import core.SimClock;
import java.util.Collection;
import movement.ApocalypseMovement;
import movement.StationaryMovement;

/**
 * A Network Interface that connects to LeverInterfaces one at a time. A host with this interface
 * should also be stationary.
 */
public class ExitInterface extends SimpleBroadcastInterface implements Activatable {

  public boolean active;

  /** Reads the interface settings from the Settings file */
  public ExitInterface(Settings s) {
    super(s);
    active = false;
  }

  /**
   * Copy constructor
   *
   * @param ni the copied network interface object
   */
  public ExitInterface(ExitInterface ni) {
    super(ni);
  }

  private double lastConnectionTime = 0;

  private final double EXIT_INTERVAL = 100.0;

  public NetworkInterface replicate() {
    return new ExitInterface(this);
  }

  @Override
  public void update() {
    if (optimizer == null) {
      return; /* nothing to do */
    }

    double simTime = SimClock.getTime();

    optimizer.updateLocation(this);
    if (this.connections.size() > 0) {
      if (getHost().getMovement() instanceof StationaryMovement) return;

      Connection con = this.connections.get(0);
      NetworkInterface anotherInterface = con.getOtherInterface(this);
      // all connections should be up at this stage
      assert this.connections.size() == 1 : "Too many connections on exit!";

      disconnect(con, anotherInterface);
      connections.remove(0);
      for (Object item : getHost().getInterfaces()) {
        if (item instanceof Activatable) {
          Activatable activatable = (Activatable) item;
          activatable.deactivate();
        }
      }
      ApocalypseMovement movement = (ApocalypseMovement) getHost().getMovement();
      movement.removeFromSimulation();
    } else {
      if (simTime < lastConnectionTime + EXIT_INTERVAL) return;
      // Then find new possible connections
      Collection<NetworkInterface> interfaces = optimizer.getNearInterfaces(this);
      for (NetworkInterface i : interfaces) {
        lastConnectionTime = simTime;
        connect(i);
        return;
      }
    }
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
  }
}
