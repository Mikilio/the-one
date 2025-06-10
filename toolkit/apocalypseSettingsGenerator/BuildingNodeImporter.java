package apocalypseSettingsGenerator;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.Graph;
import java.io.*;
import java.util.*;

public class BuildingNodeImporter {
    public static Graph<RoomNode, RoomEdge> importNodesFromCSV(String csvFilePath) throws IOException {
        Graph<RoomNode, RoomEdge> graph = new DefaultDirectedGraph<>(RoomEdge.class);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String header = br.readLine(); // Header überspringen
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 8) continue;
                RoomNode node = getRoomNode(parts);
                graph.addVertex(node);
            }
        }
        return graph;
    }

    private static RoomNode getRoomNode(String[] parts) {
        String source = parts[0].trim();
        int id = Integer.parseInt(parts[1].trim());
        double roomSizeX = Double.parseDouble(parts[2].trim());
        double roomSizeY = Double.parseDouble(parts[3].trim());
        int nrOfHumans = Integer.parseInt(parts[4].trim());
        int nrOfZombies = Integer.parseInt(parts[5].trim());
        int simulationTime = Integer.parseInt(parts[6].trim());
        int numberOfRuns = Integer.parseInt(parts[7].trim());
        RoomNode node = new RoomNode(source, id, roomSizeX, roomSizeY, nrOfHumans, nrOfZombies, simulationTime, numberOfRuns);
        return node;
    }
}
