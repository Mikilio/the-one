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
public class HumanMovement extends MovementModel implements SwitchableMovement {
	
	public static final int STATIC = 0;
	public static final int WALKING = 1;
	public static final int FLEEING = 2;

	public static final double DETECTION_RADIUS = 20; // Radius to detect zombies and alerted humans

	public static final double K_H = 1; // Repulsion constant of humans
	public static final double K_Z = 4; // Repulsion constant of zombies
	public static final double A_H = 2; // Repulsion exponent of humans
	public static final double A_Z = 1.3; // Repulsion exponent of zombies

	private int id;
	public static int nextID = 0;
	private ApocalypseControlSystem controlSystem;

	private int state;

	private Coord lastWaypoint;
	private Coord walkingDestination; // for WALKING state
	private final double distance = 1; // distance to move before recalculating path (TODO: change)

	private List<Coord> exits;
	private List<Coord> humans;
	private List<Coord> zombies;

	public HumanMovement(Settings settings) {
		super(settings);

		int acs = settings.getInt(ApocalypseControlSystem.APOCALYPSE_CONTROL_SYSTEM_NR);
		controlSystem = ApocalypseControlSystem.getApocalypseControlSystem(settings,acs);
		id = nextID++;
		controlSystem.registerHuman(this);
		state = FLEEING; // TODO: change, for testing purposes

		this.exits = new LinkedList<>();
		this.humans = new LinkedList<>();
		this.zombies = new LinkedList<>();
	}

	protected HumanMovement(HumanMovement hmv) {
		super(hmv);

		state = hmv.state;
		controlSystem = hmv.controlSystem;
		id = nextID++;
		controlSystem.registerHuman(this);
		walkingDestination = hmv.walkingDestination != null ? hmv.walkingDestination.clone() : null;
		
		exits = new LinkedList<>(hmv.exits);
		humans = new LinkedList<>(hmv.humans);
		zombies = new LinkedList<>(hmv.zombies);
	}

  public HumanMovement(NoMovement nmv) {
		super(nmv);

		state = FLEEING;
		controlSystem = nmv.getControlSystem();
		id = nextID++;
		controlSystem.registerHuman(this);

		exits = new LinkedList<>(controlSystem.getExits());
		humans = new LinkedList<>(controlSystem.getHumanCoords());
		zombies = new LinkedList<>(controlSystem.getZombieCoords());

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
		
		if (state == STATIC || state == WALKING) {
			probeForZombies();
		}
		
		if (state == STATIC) {
			return null;
		} else if (state == WALKING) {
			if (walkingDestination == null || walkingDestination.distance(lastWaypoint) < distance) {
				// If no next waypoint is set, generate a random one
				walkingDestination = randomCoord();
			}
			double dx = walkingDestination.getX() - lastWaypoint.getX();
			double dy = walkingDestination.getY() - lastWaypoint.getY();
			double dist = lastWaypoint.distance(walkingDestination);
			c = new Coord(
				lastWaypoint.getX() + (dx / dist) * distance,
				lastWaypoint.getY() + (dy / dist) * distance
			);
		}
		else if (state == FLEEING) {
			exits = controlSystem.getExits();
			humans = controlSystem.getHumanCoords();
			zombies = controlSystem.getZombieCoords();
			c = calculateFleeingPath(lastWaypoint, exits, humans, zombies);
		} else {
			// Default case, generate a random coordinate
			c = randomCoord();
		}

    if (c == null) {
      return p;
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
	public HumanMovement replicate() {
		return new HumanMovement(this);
	}

	public int getID() {
		return id;
	}

	public ApocalypseControlSystem getControlSystem() {
		return controlSystem;
	}

	public Coord getLastLocation() {
		return lastWaypoint;
	}

	public void setLocation(Coord lastWaypoint) {
		this.lastWaypoint = lastWaypoint;
	}

	public boolean isReady() {
		return true;
	}

	protected Coord randomCoord() {
		return new Coord(rng.nextDouble() * getMaxX(),
				rng.nextDouble() * getMaxY());
	}

	private boolean probeForZombies() {
		zombies = controlSystem.getZombieCoords();
		Coord closestZombie = getClosestCoordinate(zombies, lastWaypoint);
		if (closestZombie != null && closestZombie.distance(lastWaypoint) < DETECTION_RADIUS) {
			state = FLEEING; // Alerted by a zombie, switch to fleeing state
			return true;
		}
		return false;
	}

	private Coord calculateFleeingPath(Coord currentLocation, List<Coord> exits, List<Coord> humans, List<Coord> zombies) {
		double x = currentLocation.getX();
		double y = currentLocation.getY();
		Coord e;
		// Select the closest attraction point or a 0,0 if none are available
		if (!exits.isEmpty()) {
			e = getClosestCoordinate(exits, currentLocation);
		} else {
			e = currentLocation.clone();
		}

		// Normalized vector towards the closest attraction point
		double dist = currentLocation.distance(e);
		if (dist == 0) {
			// If the last waypoint is the same as the attraction point, we can skip the movement
			return null;
		}
		double dx = e.getX() - x / dist;
		double dy = e.getY() - y / dist;

		// Adding repulsion
		for (Coord h : humans) {
			Coord repulsion = polynomialRepulsionVector(currentLocation, h, K_H, A_H);
			dx += repulsion.getX();
			dy += repulsion.getY();
		}
		for (Coord z : zombies) {
			Coord repulsion = exponentialRepulsionVector(currentLocation, z, K_Z, A_Z);
			dx += repulsion.getX();
			dy += repulsion.getY();
		}

		// Normalize the resulting vector and scale it by the distance
		double length = Math.sqrt(dx * dx + dy * dy);
		if (length == 0) {
			// If the vector length is zero, we can skip the movement
			return null;
		}
		dx *= distance / length;
		dy *= distance / length;

        return new Coord(x + dx, y + dy);
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

	/**
	 * Calculates a polynomial decreasing repulsion vector between two coordinates.
	 * v = - k * 1 / dist^a * (other - c) / dist
	 * 
	 * @param c The coordinate from which the repulsion is calculated.
	 * @param other The coordinate to which the repulsion is directed.
	 * @param k The repulsion constant.
	 * @param a The exponent for the distance in the repulsion formula.
	 * @return A Coord representing the repulsion vector.
	 */
	private static Coord polynomialRepulsionVector(Coord c, Coord other, double k, double a) {
		// (dx, dy) is the vector from c to other
		double dx = other.getX() - c.getX();
		double dy = other.getY() - c.getY();
		double dist = c.distance(other);
		if (dist == 0) {
			return new Coord(0,0); // Avoid division by zero
		}
		return new Coord(
			- k * dx / Math.pow(dist, a+1), 
			- k * dy / Math.pow(dist, a+1)
		);
	}

	/**
	 * Calculates an exponention repulsion vector between two coordinates.
	 * The vector is scaled by a constant and an exponent.
	 * 
	 * v = k * exp(-a * dist) * (c - other) / dist
	 * 
	 * @param c The coordinate from which the repulsion is calculated.
	 * @param other The coordinate to which the repulsion is directed.
	 * @param k The repulsion constant.
	 * @param a The exponent for the distance in the repulsion formula.
	 * @return A Coord representing the repulsion vector.
	 */
	private static Coord exponentialRepulsionVector(Coord c, Coord other, double k, double a) {
		// (dx, dy) is the vector from c to other
		double dx = other.getX() - c.getX();
		double dy = other.getY() - c.getY();
		double dist = c.distance(other);
		if (dist == 0) {
			return new Coord(0,0); // Avoid division by zero
		}
		return new Coord(
			- k * Math.exp(-a * dist) * dx / dist, 
			- k * Math.exp(-a * dist) * dy / dist
		);
	}
}
