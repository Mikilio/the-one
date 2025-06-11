package movement;

import core.Coord;
import core.Settings;
import core.SimClock;
import core.Tuple;
import java.util.LinkedList;
import java.util.List;

/**
 * Movement model for a zombie in a zombie apocalypse simulation.
 *
 * @author Bisma Baubeau
 */
public class ZombieMovement extends MovementModel implements SwitchableMovement {

	public static final int STATIC = 0;
	public static final int ROAMING = 1;
	public static final int CHASING = 2;

	public static final double DETECTION_RADIUS = 20; // Radius for detecting other entities

	boolean registered = false; // to register the zombie only once after the warmup phase

	private int id;
	protected static int nextID = 0;
	private ApocalypseControlSystem controlSystem;

	private int state;

	private Coord lastWaypoint;
	private Coord nextDestination;
	private final double distance = 1; // distance to move before recalculating path

	private List<Coord> humans;
	private List<Tuple<Coord, Integer>> exits;

	public ZombieMovement(Settings settings) {
		super(settings);

		state = ROAMING;
		int acs = settings.getInt(ApocalypseControlSystem.APOCALYPSE_CONTROL_SYSTEM_NR);
		controlSystem = ApocalypseControlSystem.getApocalypseControlSystem(settings,acs);
		id = nextID++;
		nextDestination = randomCoord();
		
		humans = new LinkedList<>();
	}

	protected ZombieMovement(ZombieMovement zmv) {
		super(zmv);
		
		state = zmv.state;
		controlSystem = zmv.controlSystem;
		id = nextID++;
		lastWaypoint = zmv.lastWaypoint != null ? zmv.lastWaypoint.clone() : null;
		nextDestination = zmv.nextDestination != null ? zmv.nextDestination.clone() : null;
		
		humans = new LinkedList<>(zmv.humans);
	}

	public ZombieMovement(HumanMovement hmv) {
		super(hmv);

		state = ROAMING;
		controlSystem = hmv.getControlSystem();
		id = nextID++;

		humans = new LinkedList<>(controlSystem.getHumanCoords());
	}

  	public ZombieMovement(NoMovement nmv) {
		super(nmv);

		state = ROAMING;
		controlSystem = nmv.getControlSystem();
		id = nextID++;
		nextDestination = null; // No initial roaming destination

		humans = new LinkedList<>(controlSystem.getHumanCoords());
}

	/**
	 * Returns a possible (random) placement for a host
	 * @return Random position on the map
	 */
	@Override
	public Coord getInitialLocation() {
		assert rng != null : "MovementModel not initialized!";
		
		this.lastWaypoint = randomCoord();
		return lastWaypoint.clone();
	}

	@Override
	public Path getPath() {
		// Register the zombie with the control system after the warmup phase
		if (!registered && SimClock.getTime() > 0) {
			controlSystem.registerZombie(this);
			registered = true;
		}

		Path p;
		p = new Path(generateSpeed());
		p.addWaypoint(lastWaypoint.clone());

		Coord c;

		probeForHumans();
		// If in ROAMING state, set a new random destination if needed
		if (state == ROAMING && (nextDestination == null || nextDestination.distance(lastWaypoint) < distance)) {
			// Prioritize exits if in range
			exits = controlSystem.getExits();
			LinkedList<Coord> exitsCoords = new LinkedList<>();
			for (Tuple<Coord, Integer> exit : exits) {
				exitsCoords.add(exit.getFirst());
			}
			Coord closestExit = getClosestCoordinateInRange(exitsCoords, lastWaypoint, DETECTION_RADIUS);
			if (closestExit != null) {
				nextDestination = closestExit.clone();
			} else {
				nextDestination = randomCoord();
			}
		}
		
		if (state == STATIC) {
			return null; // No movement in these states
		} else if (state == ROAMING || state == CHASING) {
			double dx = nextDestination.getX() - lastWaypoint.getX();
			double dy = nextDestination.getY() - lastWaypoint.getY();
			double dist = lastWaypoint.distance(nextDestination);
			if (dist != 0) { // human at zombie location, don't need to move
				c = new Coord(
					lastWaypoint.getX() + (dx / dist) * distance,
					lastWaypoint.getY() + (dy / dist) * distance
				);
			} else {
				return null;
			}
		} else {
			return null;
		}

		// Ensure the new coordinates are within bounds
		double maxX = getMaxX();
		double maxY = getMaxY();
		double x = c.getX();
		double y = c.getY();
		if (x < 0) x = 0;
		if (x > maxX) x = maxX;
		if (y < 0) y = 0;
		if (y > maxY) y = maxY;

		c.setLocation(x,y);
	
		// Add the new waypoint to the path
		p.addWaypoint(c);

		this.lastWaypoint = c;
		return p;
	}

	@Override
	public ZombieMovement replicate() {
		return new ZombieMovement(this);
	}

	public int getID() {
		return id;
	}

	public Coord getLastLocation() {
		return lastWaypoint;
	}

	public void setLocation(Coord lastWaypoint) {
		this.lastWaypoint = lastWaypoint;
	}

	public ApocalypseControlSystem getControlSystem() {
		return controlSystem;
	}

	public boolean isReady() {
		return true;
	}

	/**
	 * Probes for humans within the detection radius and updates the state accordingly.
	 * 
	 * @return true if a human was detected, false otherwise.
	 */
	private boolean probeForHumans() {
		// Check if there are humans within the detection radius
		humans = controlSystem.getHumanCoords();
		Coord closestHuman = getClosestCoordinateInRange(humans, lastWaypoint, DETECTION_RADIUS);

		if (closestHuman != null) {
			nextDestination = closestHuman.clone();
			state = CHASING; // Change state to chasing
			return true;
		} else {
			state = ROAMING; // No humans detected, continue roaming
			return false;
		}
	}

	/**
	 * Generates a random coordinate within the bounds of the simulation area.
	 * @return a random coordinate
	 */
	protected Coord randomCoord() {
		return new Coord(rng.nextDouble() * getMaxX(),
				rng.nextDouble() * getMaxY());
	}

	/**
	 * Help method to find the closest coordinate from a list of coordinates,
	 * to a specific location, within a specified range.
	 * @param allCoords list of coordinates to compare
	 * @param coord destination node
	 * @param range maximum distance to consider
	 * @return closest to the destination
	 */
	private static Coord getClosestCoordinateInRange(List<Coord> allCoords,
			Coord coord, double range) {
		Coord closestCoord = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Coord temp : allCoords) {
			double distance = temp.distance(coord);
			if (distance < Math.min(minDistance, range)) {
				minDistance = distance;
				closestCoord = temp;
			}
		}
		return closestCoord != null ? closestCoord.clone() : null;
	}
}
