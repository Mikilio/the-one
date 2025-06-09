package apocalypseSettingsGenerator;

import org.jgrapht.graph.DefaultEdge;


//New edge type that has a unique ID inorder for the events to connect properly over multiple simulation runs
public class RoomEdge extends DefaultEdge {
    private final int id;
    public RoomEdge(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}
