package routing;

import core.DTNHost;
import core.Settings;
import java.util.Random;
import core.Connection;

public class HumanRouter extends PassiveRouter {
    public static final double TURNING_RATE = 0.6;

    public HumanRouter(Settings s) {
        super(s);
    }

    protected HumanRouter(HumanRouter r) {
        super(r);
    }

    @Override
    public void changedConnection(Connection con) {
      if (!con.isUp()) {return;}

        DTNHost zombie = con.getOtherNode(getHost());
        MessageRouter zombieRouter = zombie.getRouter();
        if ( zombieRouter instanceof ZombieRouter) {
            System.out.println("Connected to Zombie: " + zombie.getName());
            Random rng = new Random();
            double r = rng.nextDouble();
            if (r < TURNING_RATE) {
              getHost().pauseMovement(100);
              zombie.pauseMovement(10);
              getHost().setName("Zombie (Ex: " + getHost().getInitialName() + ")");
              getHost().setRouter(zombieRouter.replicate());
              return;
            }
            getHost().pauseMovement(10);
            zombie.pauseMovement(15);
        }
    }
    @Override
    public MessageRouter replicate() {
        return new HumanRouter(this);
    }
}
