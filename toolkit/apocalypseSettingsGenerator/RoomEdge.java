package apocalypseSettingsGenerator;

import core.Coord;
import org.jgrapht.graph.DefaultEdge;


//New edge type that has a unique ID inorder for the events to connect properly over multiple simulation runs
public class RoomEdge extends DefaultEdge {
    private final int id;
    private final int fromId;
    private final int toId;
    private final Coord entrance;
    private final Coord exit;
    private final int exitPriority;

    public RoomEdge(int id, int fromId, int toId, Coord entrance, Coord exit, int exitPriority) {
        this.id = id;
        this.fromId = fromId;
        this.toId = toId;
        this.entrance = entrance;
        this.exit = exit;
        this.exitPriority = exitPriority;
    }

    public int getExitPriority() {
        return exitPriority;
    }

    public int getId() {
        return id;
    }

    public Coord getExit() {
        return exit;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }

    public Coord getEntrance() {
        return entrance;
    }
}
