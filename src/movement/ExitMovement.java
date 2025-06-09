/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Coord;
import core.Settings;

/**
 * A dummy stationary "movement" model where nodes do not move.
 * Might be useful for simulations with only external connection events.
 * 
 * @author Bisma Baubeau
 */
public class ExitMovement extends MovementModel {
	/** Per node group setting for setting the location ({@value}) */
	public static final String LOCATION_S = "nodeLocation";
	public static final String PRIORITY_S = "priority";
	private Coord loc; /** The location of the nodes */
	private int priority; /** The priority of the exit */

	/**
	 * Creates a new movement model based on a Settings object's settings.
	 * @param s The Settings object where the settings are read from
	 */
	public ExitMovement(Settings s) {
		super(s);
		int coords[];

		coords = s.getCsvInts(LOCATION_S, 2);
		this.loc = new Coord(coords[0],coords[1]);
		this.priority = s.getInt(PRIORITY_S, 1);

		int acs = s.getInt(ApocalypseControlSystem.APOCALYPSE_CONTROL_SYSTEM_NR);
		ApocalypseControlSystem controlSystem = ApocalypseControlSystem.getApocalypseControlSystem(s,acs);
		controlSystem.registerExit(this.loc, this.priority);
	}

	/**
	 * Copy constructor.
	 * @param sm The StationaryMovement prototype
	 */
	public ExitMovement(ExitMovement sm) {
		super(sm);
		this.loc = sm.loc;
		this.priority = sm.priority;

		// no need to register again, as this is a copy with the same location
	}

	/**
	 * Returns the only location of this movement model
	 * @return the only location of this movement model
	 */
	@Override
	public Coord getInitialLocation() {
		return loc;
	}

	/**
	 * Returns a single coordinate path (using the only possible coordinate)
	 * @return a single coordinate path
	 */
	@Override
	public Path getPath() {
		Path p = new Path(0);
		p.addWaypoint(loc);
		return p;
	}

	@Override
	public double nextPathAvailable() {
		return Double.MAX_VALUE;	// no new paths available
	}

	@Override
	public ExitMovement replicate() {
		return new ExitMovement(this);
	}

}
