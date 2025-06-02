package movement;

import core.Settings;
import core.Coord;

public class ApocalypseMovement extends ExtendedMovementModel {

    private static final String IS_ZOMBIE_SETTING = "isZombie";

    private boolean isZombie;

    private NoMovement stationaryMovement;
    private HumanMovement humanMovement;
    private ZombieMovement zombieMovement;

    /**
     * Creates a new ApocalypseMovement instance.
     * @param settings The settings for the simulation.
     */
    public ApocalypseMovement(Settings settings) {
        super(settings);
        isZombie = settings.getBoolean(IS_ZOMBIE_SETTING, false);
        if (isZombie) {
            zombieMovement = new ZombieMovement(settings);
            setCurrentMovementModel(zombieMovement);
        } else {
            humanMovement = new HumanMovement(settings);
            setCurrentMovementModel(humanMovement);
        }
    }

    /**
     * Creates a new ApocalypseMovement instance from a prototype.
     * @param amv The ApocalypseMovement to copy.
     */
    protected ApocalypseMovement(ApocalypseMovement amv) {
        super(amv);
        isZombie = amv.isZombie;
        if (amv.zombieMovement != null) {
            zombieMovement = new ZombieMovement(amv.zombieMovement);
            setCurrentMovementModel(zombieMovement);
        } else if (amv.humanMovement != null) {
            humanMovement = new HumanMovement(amv.humanMovement);
            setCurrentMovementModel(humanMovement);
        } else {
            stationaryMovement = new NoMovement(amv.stationaryMovement);
            setCurrentMovementModel(stationaryMovement);
        }    
    }

    @Override
    public ApocalypseMovement replicate() {
        return new ApocalypseMovement(this);
    }

    @Override
    public Coord getInitialLocation() {
        if (isZombie) {
            return zombieMovement.getInitialLocation();
        } else if (humanMovement != null) {
            return humanMovement.getInitialLocation();
        } else {
            return stationaryMovement.getInitialLocation();
        }
    }

    @Override
    public boolean newOrders() {
        if (humanMovement != null && humanMovement.isCloseToExit()) {
            removeFromSimulation();
        }
        return true;
    }

    /** 
     * Turns the current movement model into a zombie movement model.
     * This method should only be called if the current model is a human movement model.
     */
    public void turnIntoZombie() {
        if (!isZombie) {
            isZombie = true;
            if (humanMovement != null) {
                zombieMovement = new ZombieMovement(humanMovement);
                humanMovement = null;
                setCurrentMovementModel(zombieMovement);
            } else {
                throw new IllegalStateException("Cannot turn into a zombie without a human movement model.");
            }
        }
    }

    /**
     * Virtually removes this movement model from the simulation.
     */
    public void removeFromSimulation() {
        if (isZombie) {
            zombieMovement.getControlSystem().unregisterZombie(zombieMovement.getID());
            zombieMovement = null;
        } else {
            humanMovement.getControlSystem().unregisterHuman(humanMovement.getID());;
            humanMovement = null;
        }
        stationaryMovement = new NoMovement();
        setCurrentMovementModel(stationaryMovement);
    }
    
}
