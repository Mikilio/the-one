package routing;

import core.*;

public class ZombieRouter extends PassiveRouter{

    public ZombieRouter(Settings s) {
        super(s);
    }

    protected ZombieRouter(ZombieRouter r) {
        super(r);
    }

    @Override
    public MessageRouter replicate() {
        return new ZombieRouter(this);
    }
}
