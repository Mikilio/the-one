package routing;

import core.*;

public class ZombieRouter extends ActiveRouter {

    public ZombieRouter(Settings s) {
        super(s);
    }

    protected ZombieRouter(ZombieRouter r) {
        super(r);
    }

    @Override
    public void update() {
        super.update();
        if( isConnectedToHuman() ) {
            getHost().pauseMovement(1.0);
        }

    }

    public boolean isConnectedToHuman() {

        for (core.Connection con : getHost().getConnections()) {
            DTNHost other = con.getOtherNode(getHost());
            if (other.getRouter() instanceof HumanRouter) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MessageRouter replicate() {
        return new ZombieRouter(this);
    }
}
