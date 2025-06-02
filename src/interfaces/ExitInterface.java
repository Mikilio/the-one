/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package interfaces;

import core.NetworkInterface;
import core.Settings;
import core.SimClock;
import core.CBRConnection;
import core.Connection;

/**
 * A Network Interface that connects to LeverInterfaces one at a time.
 * A host with this interface should also be stationary.
 */
public class ExitInterface extends SimpleBroadcastInterface {

  /** Reads the interface settings from the Settings file */
  public ExitInterface(Settings s) {
    super(s);
  }

  /**
   * Copy constructor
   *
   * @param ni the copied network interface object
   */
  public ExitInterface(ExitInterface ni) {
    super(ni);
  }

  private double lastConnectionTime;

  private final double EXIT_INTERVAL = 2.0;

  public NetworkInterface replicate() {
    return new ExitInterface(this);
  }

  @Override
  public void connect(NetworkInterface anotherInterface) {
    
    if (!(anotherInterface instanceof LeaverInterface)) return;
      
    double simTime = SimClock.getTime();

    if (simTime < lastConnectionTime + EXIT_INTERVAL) return;

    super.connect(anotherInterface);
  }

  @Override
  public void update() {
    for (int i=0; i<this.connections.size(); ) {
      Connection con = this.connections.get(i);
      NetworkInterface anotherInterface = con.getOtherInterface(this);

      // all connections should be up at this stage
      assert con.isUp() : "Connection " + con + " was down!";
      
      // Let leavers leave (deactivate their interface)
      if (anotherInterface instanceof LeaverInterface) {
        LeaverInterface leaver = (LeaverInterface) anotherInterface;
        disconnect(con, anotherInterface);
        connections.remove(i);
        leaver.deactivate();
      }
    }
  }

  // Make this Interface Invisible to others
  @Override
  public boolean isActive() {
    return false;
  }
}
