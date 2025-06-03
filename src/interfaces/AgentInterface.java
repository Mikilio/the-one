/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package interfaces;

import java.util.Collection;

import core.CBRConnection;
import core.Connection;
import core.NetworkInterface;
import core.Settings;

/**
 * A simple Network Interface that provides a constant bit-rate service, where
 * one transmission can be on at a time.
 */

public class AgentInterface extends SimpleBroadcastInterface implements Activatable  {

	/**
	 * Reads the interface settings from the Settings file
	 */
	public AgentInterface(Settings s)	{
		super(s);
    active = false;
	}

	/**
	 * Copy constructor
	 * @param ni the copied network interface object
	 */
	public AgentInterface(AgentInterface ni) {
		super(ni);
	}

  public boolean active;

	public NetworkInterface replicate()	{
		return new AgentInterface(this);
	}

  /**
   * Updates the state of current connections (i.e. tears down connections that are out of range and
   * creates new ones).
   */
  @Override
  public void update() {
    if (optimizer == null) {
      return; /* nothing to do */
    }

    // First break the old ones
    optimizer.updateLocation(this);
    for (int i = 0; i < this.connections.size(); ) {
      Connection con = this.connections.get(i);
      NetworkInterface anotherInterface = con.getOtherInterface(this);

      // all connections should be up at this stage
      assert con.isUp() : "Connection " + con + " was down!";

      if (!isWithinRange(anotherInterface)) {
        disconnect(con, anotherInterface);
        connections.remove(i);
      } else {
        i++;
      }
    }
    // Then find new possible connections
    Collection<NetworkInterface> interfaces = optimizer.getNearInterfaces(this);
    for (NetworkInterface i : interfaces) {
      if (i instanceof AgentInterface) {
        connect(i);
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
