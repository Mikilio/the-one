package movement;

import core.Coord;

/**
 * A stationary movement model that implements the SwitchableMovement interface.
 * This model does not change location and is always ready.
 */
public class NoMovement extends MovementModel implements SwitchableMovement {

    /**
     * Creates a new SwitchableStationaryMovement based on a Settings object's settings.
     * @param s The Settings object where the settings are read from
     */
    public NoMovement() {
        super();
    }

    /**
     * Copy constructor.
     * @param sm The SwitchableStationaryMovement prototype
     */
    public NoMovement(NoMovement sm) {
        super(sm);
    }

    @Override
    public NoMovement replicate() {
        return new NoMovement(this);
    }

    @Override
    public void setLocation(Coord lastWaypoint) {
        // No operation, as this movement model does not change location
    }

    @Override
    public Coord getInitialLocation() {
        return new Coord(0, 0); // Default initial location
    }

    @Override
    public Path getPath() {
        Path p = new Path(0);
        p.addWaypoint(getInitialLocation());
        return p; // Returns a path with the initial location only
    }

    @Override
    public Coord getLastLocation() {
        return getInitialLocation();
    }

    @Override
    public boolean isReady() {
        return true; // Always ready since it's stationary
    }
    
}
