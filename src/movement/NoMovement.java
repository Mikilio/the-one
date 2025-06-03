package movement;

import core.Coord;
import core.Settings;

/**
 * A stationary movement model that implements the SwitchableMovement interface.
 * This model does not change location and is always ready.
 */
public class NoMovement extends MovementModel implements SwitchableMovement {

    /** Per node group setting for setting the location ({@value}) */
    public static final String LOCATION_S = "nodeLocation";
    private Coord loc; /** The location of the nodes */
    private ApocalypseControlSystem controlSystem;

    /**
     * Creates a new SwitchableStationaryMovement based on a Settings object's settings.
     * @param s The Settings object where the settings are read from
     */
    public NoMovement(Settings s) {
      super(s);
      int coords[];

      coords = s.getCsvInts(LOCATION_S, 2);
      this.loc = new Coord(coords[0],coords[1]);
      int acs = s.getInt(ApocalypseControlSystem.APOCALYPSE_CONTROL_SYSTEM_NR);
      controlSystem = ApocalypseControlSystem.getApocalypseControlSystem(s,acs);
    }

    /**
     * Copy constructor.
     * @param sm The SwitchableStationaryMovement prototype
     */
    public NoMovement(NoMovement sm) {
        super(sm);
        this.loc = sm.loc;
    }
    public NoMovement(HumanMovement sm) {
      super(sm);
      this.loc = sm.getLastLocation();
    }
    public NoMovement(ZombieMovement sm) {
      super(sm);
      this.loc = sm.getLastLocation();
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
        return loc; // Default initial location
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
    
    public ApocalypseControlSystem getControlSystem() {
      return controlSystem;
    }
}
