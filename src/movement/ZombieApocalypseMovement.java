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
public class ZombieApocalypseMovement extends MovementModel implements SwitchableMovement {

	public static final String IS_ZOMBIE_SETTING = "isZombie";

	public static final int HUMAN_STATIC = 0;
	public static final int HUMAN_WALKING = 1;
	public static final int HUMAN_FLEEING = 2;
	public static final int HUMAN_FIGHTING = 3;
	public static final int HUMAN_TURNING = 4;

	public static final int ZOMBIE_ROAMING = 5;
	public static final int ZOMBIE_CHASING = 6;
	public static final int ZOMBIE_FIGHTING = 7;
	public static final int ZOMBIE_STUNNED = 8;

	public static final double K_H = 1; // Repulsion constant of humans
	public static final double K_Z = 1; // Repulsion constant of zombies
	public static final int A_H = 3; // Repulsion exponent of humans
	public static final int A_Z = 2; // Repulsion exponent of zombies

	private int id;
	private static int nextID = 0;
	private ZombieApocalypseControlSystem controlSystem;

	private int state;

	private Coord lastWaypoint;
	private double distance; // distance between waypoints
	private List<Coord> exits;
	private List<Coord> humans;
	private List<Coord> zombies;

	public ZombieApocalypseMovement(Settings settings) {
		super(settings);
		boolean isZombie = settings.getBoolean(ZombieApocalypseMovement.IS_ZOMBIE_SETTING);

		int zacs = settings.getInt(ZombieApocalypseControlSystem.ZOMBIE_APOCALYPSE_CONTROL_SYSTEM_NR);
		controlSystem = ZombieApocalypseControlSystem.getZombieApocalypseControlSystem(zacs);
		id = nextID++;
		this.distance = 10; // TODO: change
		if (isZombie) {
			this.state = ZOMBIE_ROAMING;
			controlSystem.registerZombie(this);
		} else {
			this.state = HUMAN_FLEEING; // TODO: change
			controlSystem.registerHuman(this);
		}
		this.exits = new LinkedList<>();
		this.humans = new LinkedList<>();
		this.zombies = new LinkedList<>();
	}

	protected ZombieApocalypseMovement(ZombieApocalypseMovement zamv) {
		super(zamv);
		this.distance = zamv.distance;
		this.state = zamv.state;
		this.controlSystem = zamv.controlSystem;
		id = nextID++;
		this.lastWaypoint = zamv.lastWaypoint;
		
		if (isZombie()) {
			controlSystem.registerZombie(this);
		} else {
			controlSystem.registerHuman(this);
		}

		this.exits = new LinkedList<>(zamv.exits);
		this.humans = new LinkedList<>(zamv.humans);
		this.zombies = new LinkedList<>(zamv.zombies);
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
		
		if (state == HUMAN_STATIC || state == HUMAN_FIGHTING || state == HUMAN_TURNING || state == ZOMBIE_FIGHTING || state == ZOMBIE_STUNNED) {
			return null; // No movement in these states
		} else if (state == HUMAN_WALKING || state == ZOMBIE_ROAMING) {
			c = randomCoord();
		}
		else if (state == HUMAN_FLEEING) {
			exits = controlSystem.getExits();
			humans = controlSystem.getHumanCoords();
			zombies = controlSystem.getZombieCoords();
			c = calculateFleeingPath(lastWaypoint, exits, humans, zombies);
		} else {
			c = lastWaypoint.clone();
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
	public ZombieApocalypseMovement replicate() {
		return new ZombieApocalypseMovement(this);
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

	public boolean isZombie() {
		return (state == ZOMBIE_ROAMING || 
				state == ZOMBIE_CHASING || 
				state == ZOMBIE_FIGHTING || 
				state == ZOMBIE_STUNNED);
	}

	public int turnToZombie() {
		if (!isZombie()) {
			controlSystem.turnToZombie(id);
			state = ZOMBIE_ROAMING; // Default state for zombies
		}
		return id;
	}

	protected Coord randomCoord() {
		return new Coord(rng.nextDouble() * getMaxX(),
				rng.nextDouble() * getMaxY());
	}

	private Coord calculateFleeingPath(Coord currentLocation, List<Coord> exits, List<Coord> humans, List<Coord> zombies) {
		double x = currentLocation.getX();
		double y = currentLocation.getY();
		Coord c;
		// Select the closest attraction point or a 0,0 if none are available
		if (!exits.isEmpty()) {
			c = getClosestCoordinate(exits, currentLocation);
		} else {
			c = new Coord(0,0);
		}

		// Normalized vector towards the closest attraction point
		double dist = lastWaypoint.distance(c);
		if (dist == 0) {
			// If the last waypoint is the same as the attraction point, we can skip the movement
			return null;
		}
		double dx = c.getX() - x / dist;
		double dy = c.getY() - y / dist;

		// Adding repulsion
		for (Coord h : humans) {
			Coord repulsion = repulsionVector(currentLocation, h, K_H, A_H);
			dx += repulsion.getX();
			dy += repulsion.getY();
		}
		for (Coord z : zombies) {
			Coord repulsion = repulsionVector(currentLocation, z, K_Z, A_Z);
			dx += repulsion.getX();
			dy += repulsion.getY();
		}

		// Normalize the resulting vector
		double length = Math.sqrt(dx * dx + dy * dy);
		if (length == 0) {
			// If the vector length is zero, we can skip the movement
			return null;
		}
		dx /= length;
		dy /= length;

		// Scale the vector by the distance
		dx *= distance;
		dy *= distance;

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
	 * Calculates the repulsion vector between two coordinates.
	 * v = k * (c - other) / dist^a
	 * 
	 * @param c The coordinate from which the repulsion is calculated.
	 * @param other The coordinate to which the repulsion is directed.
	 * @param k The repulsion constant.
	 * @param a The exponent for the distance in the repulsion formula.
	 * @return A Coord representing the repulsion vector.
	 */
	private static Coord repulsionVector(Coord c, Coord other, double k, int a) {
		double dx = c.getX() - other.getX();
		double dy = c.getY() - other.getY();
		double dist = c.distance(other);
		if (dist == 0) {
			return new Coord(0,0); // Avoid division by zero
		}
		return new Coord(
			k * dx / Math.pow(dist, a+1), 
			k * dy / Math.pow(dist, a+1)
		);
	}
}
