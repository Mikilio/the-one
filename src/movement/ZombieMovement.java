package movement;

import core.Coord;
import core.Settings;

import java.util.LinkedList;
import java.util.List;

/**
 * Movement model for a zombie apocalypse simulation.
 * Entities can be either humans or zombies.
 *
 * @author Bisma Baubeau
 */
public class ZombieMovement extends ExtendedMovementModel implements SwitchableMovement {

	public static final int STATIC = 0;
	public static final int ROAMING = 1;
	public static final int CHASING = 2;

	public static final double DETECTION_RADIUS = 100000; // Radius for detecting other entities

	private int id;
	private static int nextID = 0;
	private ApocalypseControlSystem controlSystem;

	private int state;

	private Coord lastWaypoint;
	private Coord nextDestination;
	private final double distance = 10; // distance to move before recalculating path

	private List<Coord> humans;

	public ZombieMovement(Settings settings) {
		super(settings);
		setCurrentMovementModel(this);

		state = ROAMING;
		int acs = settings.getInt(ApocalypseControlSystem.APOCALYPSE_CONTROL_SYSTEM_NR);
		controlSystem = ApocalypseControlSystem.getApocalypseControlSystem(acs);
		id = nextID++;
		controlSystem.registerZombie(this);
		nextDestination = randomCoord();
		
		humans = new LinkedList<>();
	}

	protected ZombieMovement(ZombieMovement zmv) {
		super(zmv);
		setCurrentMovementModel(this);
		
		state = zmv.state;
		controlSystem = zmv.controlSystem;
		id = nextID++;
		controlSystem.registerZombie(this);
		nextDestination = zmv.nextDestination != null ? zmv.nextDestination.clone() : null;
		
		humans = new LinkedList<>(zmv.humans);
	}

	public ZombieMovement(HumanMovement hmv) {
		super(hmv);
		setCurrentMovementModel(this);

		state = ROAMING;
		controlSystem = hmv.getControlSystem();
		id = nextID++;
		controlSystem.registerZombie(this);
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
		double x = rng.nextDouble() * getMaxX();
		double y = rng.nextDouble() * getMaxY();
		Coord c = new Coord(x,y);

		this.lastWaypoint = c;
		return c;
	}

	@Override
	public Path getPath() {
		Path p;
		p = new Path(generateSpeed());
		p.addWaypoint(lastWaypoint.clone());

		Coord c;

		probeForHumans();
		
		if (state == STATIC) {
			return null; // No movement in these states
		} else if (state == ROAMING || state == CHASING) {
			double dx = nextDestination.getX() - lastWaypoint.getX();
			double dy = nextDestination.getY() - lastWaypoint.getY();
			double dist = lastWaypoint.distance(nextDestination);
			c = new Coord(
				lastWaypoint.getX() + (dx / dist) * distance,
				lastWaypoint.getY() + (dy / dist) * distance
			);
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


	/**
	 * Switches state between getPath() calls and calculates the next destination.
	 * @return Always 0
	 */
	@Override
	public boolean newOrders() {
		probeForHumans();

		// If in ROAMING state, set a new random destination if needed
		if (state == ROAMING && (nextDestination == null || nextDestination.distance(lastWaypoint) < distance)) {
			nextDestination = randomCoord();
		}

		return true; // Always return true to indicate new orders are set
	}

	private boolean probeForHumans() {
		// Check if there are humans within the detection radius
		humans = controlSystem.getHumanCoords();
		Coord closestHuman = getClosestCoordinate(humans, lastWaypoint);

		if (closestHuman != null && closestHuman.distance(lastWaypoint) <= DETECTION_RADIUS) {
			nextDestination = closestHuman.clone();
			state = CHASING; // Change state to chasing
			return true;
		} else {
			state = ROAMING; // No humans detected, continue roaming
			return false;
		}
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

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public boolean isReady() {
		return true;
	}

	protected Coord randomCoord() {
		return new Coord(rng.nextDouble() * getMaxX(),
				rng.nextDouble() * getMaxY());
	}

	/**
	 * Help method to find the closest coordinate from a list of coordinates,
	 * to a specific location
	 * @param allCoords list of coordinates to compare
	 * @param coord destination node
	 * @return closest to the destination
	 */
	private static Coord getClosestCoordinate(List<Coord> allCoords,
			Coord coord) {
		Coord closestCoord = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Coord temp : allCoords) {
			double distance = temp.distance(coord);
			if (distance < minDistance) {
				minDistance = distance;
				closestCoord = temp;
			}
		}
		return closestCoord.clone();
	}
}
