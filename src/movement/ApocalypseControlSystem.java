package movement;

import core.Coord;
import core.DTNSim;
import core.Settings;
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
  public static final String EXITS_LOCATIONS_SETTING = "exitsLocations";

  private static HashMap<Integer, ApocalypseControlSystem> systems;

  private HashMap<Integer, HumanMovement> humans;
  private HashMap<Integer, ZombieMovement> zombies;
  private List<Coord> exits;

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

    if (s != null) {
      int[] exitCoords = s.getCsvInts(EXITS_LOCATIONS_SETTING);
      for (int i = 0; i < exitCoords.length; i += 2) {
        if (i + 1 < exitCoords.length) {
          exits.add(new Coord(exitCoords[i], exitCoords[i + 1]));
        }
      }
    }
  }

  public static void reset() {
    systems = new HashMap<>();
  }

  /**
   * Returns a reference to a ZombieApocalypseControlSystem with ID provided as parameter. If a
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

  /**
   * @return A list of all exits belonging to this system
   */
  public List<Coord> getExits() {
    return exits;
  }

  public void setExits(List<Coord> exits) {
    this.exits = exits;
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

