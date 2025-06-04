package movement;

import core.Coord;
import core.NetworkInterface;
import core.Settings;
import interfaces.Activatable;
import interfaces.AgentInterface;

public class ApocalypseMovement extends ExtendedMovementModel {

  private static final String INITIAL_MOVEMENT_SETTING = "initialMovement";
  

  /**
   * Creates a new ApocalypseMovement instance.
   *
   * @param settings The settings for the simulation.
   */
  public ApocalypseMovement(Settings settings) {
    super(settings);
    SwitchableMovement mmInitial =
        (SwitchableMovement)
            settings.createIntializedObject(
                "movement." + settings.getSetting(INITIAL_MOVEMENT_SETTING));
    setCurrentMovementModel(mmInitial);
  }

  /**
   * Creates a new ApocalypseMovement instance from a prototype.
   *
   * @param amv The ApocalypseMovement to copy.
   */
  protected ApocalypseMovement(ApocalypseMovement amv) {
    super(amv);
    setCurrentMovementModel((SwitchableMovement)((MovementModel) amv.getCurrentMovementModel()).replicate());
  }

  @Override
  public ApocalypseMovement replicate() {
    return new ApocalypseMovement(this);
  }

  @Override
  public Coord getInitialLocation() {
    SwitchableMovement curr = getCurrentMovementModel();
    if (curr instanceof ZombieMovement) {
      ZombieMovement zombieMovement = (ZombieMovement) curr;
      return zombieMovement.getInitialLocation();
    } else if (curr instanceof HumanMovement) {
      HumanMovement humanMovement = (HumanMovement) curr;
      return humanMovement.getInitialLocation();
    } else {
      NoMovement noMovement = (NoMovement) curr;
      return noMovement.getInitialLocation();
    }
  }
  ;

  @Override
  public boolean newOrders() {
    SwitchableMovement curr = getCurrentMovementModel();
    AgentInterface currIface = null;
    for (NetworkInterface i : getHost().getInterfaces()) {
      if (i instanceof AgentInterface) currIface = (AgentInterface)i;
    }

    assert currIface != null: "Found an Agent without interface";

    //This is needed form zombies that exist at the start of simulation;
    if (curr instanceof ZombieMovement) currIface.turn();

    if (curr instanceof HumanMovement && currIface.isZombie()) {
      HumanMovement oldMovement = (HumanMovement) curr;
      oldMovement.getControlSystem().unregisterHuman(oldMovement.getID());
      ZombieMovement newMovement = new ZombieMovement(oldMovement);
      setCurrentMovementModel(newMovement);
      return true;
    }
    if (curr instanceof HumanMovement || curr instanceof ZombieMovement) {
      for (Object item : getHost().getInterfaces()) {
        if (item instanceof Activatable) {
          Activatable activatable = (Activatable) item;
          activatable.activate();
        }
      }
    }
    return true;
  }

  /** Virtually removes this movement model from the simulation. */
  public void removeFromSimulation() {
    SwitchableMovement curr = getCurrentMovementModel();
    NoMovement noMovement;

    for (Object item : getHost().getInterfaces()) {
      if (item instanceof Activatable) {
        Activatable activatable = (Activatable) item;
        activatable.deactivate();
      }
    }
    if (curr instanceof ZombieMovement) {
      ZombieMovement zombieMovement = (ZombieMovement) curr;
      zombieMovement.getControlSystem().unregisterZombie(zombieMovement.getID());
      noMovement = new NoMovement(zombieMovement);
    } else if (curr instanceof HumanMovement) {
      HumanMovement humanMovement = (HumanMovement) curr;
      humanMovement.getControlSystem().unregisterHuman(humanMovement.getID());
      noMovement = new NoMovement(humanMovement);
    } else return;

    setCurrentMovementModel(noMovement);
    System.out.println(getHost().getName() + " left the room");
  }
}
