package apocalypseSettingsGenerator;

import core.Coord;
import org.jgrapht.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static apocalypseSettingsGenerator.MainZombieApocalypse.edgeIdCounter;

public class BuildingEdgeImporter {
    // Function to import edges from a CSV file into an existing graph structure
    public static Graph<RoomNode, RoomEdge> importEdgesFromCSV(Graph<RoomNode, RoomEdge> graph, String edgeCsvFilePath) throws IOException {


   /*     // Map to access RoomNodes by id to build edges
        Map<Integer, RoomNode> nodeMap = new HashMap<>();
        for (RoomNode node : graph.vertexSet()) {
            nodeMap.put(node.getId(), node);
        }*/

        //Group room by type IDs
        Map<Integer, List<RoomNode>> roomsByTemplateID = new HashMap<>();
        for (RoomNode node : graph.vertexSet()) {
            roomsByTemplateID.computeIfAbsent(node.getTemplateID(), k -> new ArrayList<>()).add(node);
        }


        try (BufferedReader br = new BufferedReader(new FileReader(edgeCsvFilePath))) {

            //Skip header line
            br.readLine();

            String line;
            //Itterate over all lines of CSV, split and parse to required variables
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 4) continue;

                int fromTemplateID = Integer.parseInt(parts[0].trim());
                int toTemplateID = Integer.parseInt(parts[1].trim());
                String coordEntrance = parts[2].trim();
                String coordExit = parts[3].trim();
                int exitPriority = Integer.parseInt(parts[4].trim());

              /*  RoomNode fromNode = nodeMap.get(fromTemplateID);
                RoomNode toNode = nodeMap.get(toTemplateID);
*/
                List<RoomNode> fromNodes = roomsByTemplateID.get(fromTemplateID);
                List<RoomNode> toNodes = roomsByTemplateID.get(toTemplateID);

                // If no cooresponding id is found, skip this edge
                if (fromNodes == null || toNodes == null) continue;

                //build edge with parsed attributes and add it to graph
                for (int i = 0; i < toNodes.size(); i++) {

                    RoomNode fromNodeI = fromNodes.get(i % fromNodes.size());
                    RoomNode toNodeI = toNodes.get(i);
                    RoomEdge edge = new RoomEdge(
                            edgeIdCounter.getAndIncrement(),
                            fromNodeI.getId(),
                            toNodeI.getId(),
                            parseCoord(coordEntrance),
                            parseCoord(coordExit),
                            exitPriority
                    );
                    graph.addEdge(fromNodeI, toNodeI, edge);
                }
            }
        }

        return graph;
    }


    //Function to parse coordinates from a string with format "x#y"
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

