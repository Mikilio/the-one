package routing;

import core.Connection;
import core.DTNHost;
import core.Settings;
import core.SimClock;
import movement.RandomWalk;


public class HumanRouter extends ActiveRouter {
    private double infectionTime = -1;
    private Settings settings;

    public HumanRouter(Settings s) {
        super(s);
        settings = s;
    }

    protected HumanRouter(HumanRouter r) {
        super(r);
        this.infectionTime = r.infectionTime;
        this.settings = r.settings;
    }

    @Override
    public void update() {
        super.update();
        if (isConnectedToZombie()) {

            infectionTime = SimClock.getTime();
            getHost().pauseMovement(1.0); // Pause for 1 second
            getHost().setName("Zombie (Ex: " + getHost().getInitialName() + ")");
            getHost().setRouter(new ZombieRouter(settings));

        }
    }

    @Override
    public MessageRouter replicate() {
        return new HumanRouter(this);
    }

    public boolean isConnectedToZombie() {

        for (core.Connection con : getHost().getConnections()) {
            DTNHost other = con.getOtherNode(getHost());
            if (other.getRouter() instanceof ZombieRouter) {
                System.out.println("Connected to Zombie: " + other.getName());
                return true;
            }
        }
        return false;
    }
}