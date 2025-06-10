package movement;

import core.Coord;
import core.DTNSim;
import core.Settings;
import core.Tuple;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Control system for a zombie apocalypse.
 *
 * @author Bisma Baubeau
 */
public class ApocalypseControlSystem {

  public static final String APOCALYPSE_CONTROL_SYSTEM_NR = "apocalypseControlSystemNr";

  private static HashMap<Integer, ApocalypseControlSystem> systems;

  private HashMap<Integer, HumanMovement> humans;
  private HashMap<Integer, ZombieMovement> zombies;
  private List<Tuple<Coord, Integer>> exits;

  static {
    DTNSim.registerForReset(BusControlSystem.class.getCanonicalName());
    reset();
  }

  /**
   * Creates a new instance of ApocalypseControlSystem without any entities.
   *
   * @param systemID The unique ID of this system.
   */
  private ApocalypseControlSystem(Settings s, int systemID) {
    exits = new LinkedList<>();
    humans = new HashMap<>();
    zombies = new HashMap<>();
  }

  public static void reset() {
    systems = new HashMap<>();
  }

  /**
   * Returns a reference to a ApocalypseControlSystem with ID provided as parameter. If a
   * system does not already exist with the requested ID, a new one is created.
   *
   * @param systemID unique ID of the system
   * @return The bus control system with the provided ID
   */
  public static ApocalypseControlSystem getApocalypseControlSystem(Settings s, int systemID) {
    Integer id = systemID;

    if (systems.containsKey(id)) {
      return systems.get(id);
    } else {
      ApocalypseControlSystem acs = new ApocalypseControlSystem(s, systemID);
      systems.put(id, acs);
      return acs;
    }
  }

  public void registerHuman(HumanMovement human) {
    humans.put(human.getID(), human);
  }

  public void unregisterHuman(int humanID) {
    humans.remove(humanID);
  }

  public void registerZombie(ZombieMovement zombie) {
    zombies.put(zombie.getID(), zombie);
  }

  public void unregisterZombie(int zombieID) {
    zombies.remove(zombieID);
  }

  public void registerExit(Coord c, int priority) {
    Tuple<Coord, Integer> exit = new Tuple<>(c, priority);
    if (!exits.contains(exit)) {
      exits.add(exit);
    }
  }

  /**
   * @return A list of all exits belonging to this system
   */
  public List<Tuple<Coord, Integer>> getExits() {
    return exits;
  }

  public List<Coord> getHumanCoords() {
    List<Coord> coords = new LinkedList<>();
    for (HumanMovement human : humans.values()) {
      if (human.getLastLocation() == null) {
        continue;
      }
      coords.add(human.getLastLocation());
    }
    return coords;
  }

  public List<Coord> getZombieCoords() {
    List<Coord> coords = new LinkedList<>();
    for (ZombieMovement zombie : zombies.values()) {
      if (zombie.getLastLocation() == null) {
        continue;
      }
      coords.add(zombie.getLastLocation());
    }
    return coords;
  }
}

