/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package interfaces;

import core.DTNHost;
import core.NetworkInterface;
import core.Settings;
import java.util.Random;

/**
 * A simple Network Interface that provides a constant bit-rate service, where one transmission can
 * be on at a time.
 */
public class AgentInterface extends SimpleBroadcastInterface implements Activatable {

  private static final double TURNING_RATE = 0.2;

  private boolean isZombie;

  private boolean active;

  /** Reads the interface settings from the Settings file */
  public AgentInterface(Settings s) {
    super(s);
    active = false;
    isZombie = false;
  }

  /**
   * Copy constructor
   *
   * @param ni the copied network interface object
   */
  public AgentInterface(AgentInterface ni) {
    super(ni);
    this.active = ni.active;
    this.isZombie = ni.isZombie;
  }

  public NetworkInterface replicate() {
    return new AgentInterface(this);
  }

  /**
   * Updates the state of current connections (i.e. tears down connections that are out of range and
   * creates new ones).
   */
  public void connect(NetworkInterface anotherInterface) {
    super.connect(anotherInterface);
    AgentInterface other = (AgentInterface) anotherInterface;
    if ((isZombie && !other.isZombie()) || (!isZombie && other.isZombie())) {
      DTNHost zombie = isZombie ? this.getHost() : other.getHost();
      DTNHost human = isZombie ? other.getHost() : this.getHost();
      Random rng = new Random();
      double r = rng.nextDouble();
      if (r < TURNING_RATE) {
        human.pauseMovement(100);
        zombie.pauseMovement(10);
        human.setName("zombie (Ex: " + getHost().getInitialName() + ")");
        this.turn();
        other.turn();
        return;
      }
      human.pauseMovement(10);
      zombie.pauseMovement(15);
    }
  }

  public void turn() {
    isZombie = true;
  }

  public boolean isZombie() {
    return isZombie;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public void activate() {
    active = true;
  }

  public void deactivate() {
    active = false;
  }
}
