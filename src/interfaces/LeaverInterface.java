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
public class LeaverInterface extends SimpleBroadcastInterface {

	/**
	 * Reads the interface settings from the Settings file
	 */
	public LeaverInterface(Settings s)	{
		super(s);
    active = false;
	}

	/**
	 * Copy constructor
	 * @param ni the copied network interface object
	 */
	public LeaverInterface(LeaverInterface ni) {
		super(ni);
	}

  public boolean active;

	public NetworkInterface replicate()	{
		return new LeaverInterface(this);
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
