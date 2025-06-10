package apocalypseSettingsGenerator;

import core.Coord;
import org.jgrapht.Graph;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static apocalypseSettingsGenerator.MainZombieApocalypse.edgeIdCounter;

public class BuildingEdgeImporter {

    public static Graph<RoomNode, RoomEdge> importEdgesFromCSV(
            Graph<RoomNode, RoomEdge> graph,
            String edgeCsvFilePath
    ) throws IOException {
        // Map to access RoomNodes by id to build edges
        Map<Integer, RoomNode> nodeMap = new HashMap<>();
        for (RoomNode node : graph.vertexSet()) {
            nodeMap.put(node.getId(), node);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(edgeCsvFilePath))) {

            //Map for header
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                int fromID = Integer.parseInt(parts[0].trim());
                int toID = Integer.parseInt(parts[1].trim());
                String coordEntrance = parts[2].trim();
                String coordExit = parts[3].trim();

                RoomNode fromNode = nodeMap.get(fromID);
                RoomNode toNode = nodeMap.get(toID);

                // If no cooresponding id is found, skip this edge
                if (fromNode == null || toNode == null) continue;

                RoomEdge edge = new RoomEdge(
                        edgeIdCounter.getAndIncrement(),
                        fromNode.getId(),
                        toNode.getId(),
                        parseCoord(coordEntrance),
                        parseCoord(coordExit));
                graph.addEdge(fromNode, toNode, edge);
            }
        }

        return graph;
    }


    //Function to parse coordinates from a string with format "x;y"
    public static Coord parseCoord(String coordStr) {
        String[] parts = coordStr.split("#");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid coordinate format: " + coordStr);
        }
        double x = Double.parseDouble(parts[0].trim());
        double y = Double.parseDouble(parts[1].trim());
        return new Coord(x, y);
    }
}

