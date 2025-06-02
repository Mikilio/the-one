package routing;

import core.DTNHost;
import core.Settings;
import java.util.Random;
import core.Connection;

public class HumanRouter extends PassiveRouter {
    public static final double TURNING_RATE = 1;

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
              getHost().pauseMovement(300);
              zombie.pauseMovement(50);
              getHost().setName("Zombie (Ex: " + getHost().getInitialName() + ")");
              getHost().setRouter(zombieRouter.replicate());
              return;
            }
            getHost().pauseMovement(50);
            zombie.pauseMovement(300);
        }
    }
    @Override
    public MessageRouter replicate() {
        return new HumanRouter(this);
    }
}
