/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package input;

import java.util.Iterator;

import core.DTNHost;
import core.World;
import movement.ApocalypseMovement;
import movement.HumanMovement;
import movement.NoMovement;
import movement.ZombieMovement;

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
    System.out.println("---------------------------------------------");
		Iterator<DTNHost> hosts = world.getHosts().iterator();
    DTNHost newActor;
    while (hosts.hasNext()) {
      newActor = hosts.next();
      if (!newActor.groupId.contentEquals(groupId)) {continue;}
      ApocalypseMovement movement = (ApocalypseMovement)newActor.getMovement();
      if (movement.getCurrentMovementModel() instanceof NoMovement) {
        NoMovement nom = (NoMovement) movement.getCurrentMovementModel();
        System.out.println(this);
        System.out.println(newActor);
        System.out.println(nom.getLastLocation());
      }
    }
    System.out.println("-------------");
		hosts = world.getHosts().iterator();
    while (hosts.hasNext()) {
      newActor = hosts.next();
      if (!newActor.groupId.contentEquals(groupId)) {continue;}
      ApocalypseMovement movement = (ApocalypseMovement)newActor.getMovement();
      if (movement.getCurrentMovementModel() instanceof NoMovement) {
        NoMovement nom = (NoMovement) movement.getCurrentMovementModel();
        movement.setCurrentMovementModel(zombie ? new ZombieMovement(nom) : new HumanMovement(nom));
        System.out.println(this);
        System.out.println(newActor);
        System.out.println(nom.getLastLocation());
        return;
      }
    }
	}

	@Override
	public String toString() {
    return "Entry: " + (zombie ? "Zombie" : "Human") + " @" + this.time + " ->"+this.groupId;
	}
}
