/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package input;

import core.DTNHost;
import core.NetworkInterface;
import core.World;
import interfaces.LeaverInterface;
import java.util.Iterator;

/**
 * A connection up/down event.
 */
public class ExitEvent extends ExternalEvent {
	/** groupId of the host to be activated */
	protected String groupId;
	/** address of the node the (dis)connection is to */
	protected boolean zombie;

	/**
	 * Creates a new connection event
	 * @param groupId Which Exit/Entrance did this occur
	 * @param zombie Was it a Zombie?
	 * @param time Time when the Connection event occurs
	 */
	public ExitEvent(String groupId, boolean zombie, double time) {
		super(time);
		this.groupId = groupId;
		this.zombie = zombie;
	}

	@Override
	public void processEvent(World world) {
		Iterator<DTNHost> hosts = world.getHosts().iterator();
    DTNHost newActor;
    while (hosts.hasNext()) {
      newActor = hosts.next();
      if (newActor.groupId != groupId) {continue;}
      NetworkInterface netInterface = newActor.getInterface(0);
      if (netInterface instanceof LeaverInterface) {
        LeaverInterface leaver = (LeaverInterface) netInterface;
        leaver.activate();
      }
    }
	}

	@Override
	public String toString() {
    return "EX: " + (zombie ? "Zombie" : "Human") + " @" + this.time + " ->"+this.groupId;
	}
}
