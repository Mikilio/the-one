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
import test.StationaryMovement;

/**
 * A Network Interface that connects to LeverInterfaces one at a time. A host with this interface
 * should also be stationary.
 */
public class ExitInterface extends SimpleBroadcastInterface implements Activatable {

  public boolean active;

  private double lastConnectionTime = 0;

  private final double EXIT_INTERVAL = 1.0;

  /** Reads the interface settings from the Settings file */
  public ExitInterface(Settings s) {
    super(s);
    active = true;
  }

  /**
   * Copy constructor
   *
   * @param ni the copied network interface object
   */
  public ExitInterface(ExitInterface ni) {
    super(ni);
    this.active = ni.active;
  }

  public NetworkInterface replicate() {
    return new ExitInterface(this);
  }

  @Override
  public void update() {
    if (getHost().getMovement() instanceof StationaryMovement) {
      System.out.println("OK");
    }
    if (optimizer == null) {
      return; /* nothing to do */
    }

    double simTime = SimClock.getTime();

    optimizer.updateLocation(this);
    if (this.connections.size() > 0) {

      Connection con = this.connections.get(0);
      NetworkInterface anotherInterface = con.getOtherInterface(this);

      // all connections should be up at this stage
      assert this.connections.size() == 1 : "Too many connections on exit!";

      if (getHost().getMovement() instanceof ApocalypseMovement) {
        disconnect(con, anotherInterface);
        connections.remove(0);

        ConnectivityGrid grid = (ConnectivityGrid) optimizer;
        grid.removeInterface(this);

        ApocalypseMovement movement = (ApocalypseMovement) getHost().getMovement();
        movement.removeFromSimulation();
      }

    } else {
      if (simTime < lastConnectionTime + EXIT_INTERVAL) return;
      if (getHost().getMovement() instanceof ApocalypseMovement) return;

      // Then find new possible connections
      Collection<NetworkInterface> interfaces = optimizer.getNearInterfaces(this);
      for (NetworkInterface i : interfaces) {
        ExitInterface leaver = (ExitInterface) i;
        assert leaver.isActive() : "Inactive interface in grid";
        connect(leaver);
        if (connections.size() > 0) break;
      }
      lastConnectionTime = simTime;
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
