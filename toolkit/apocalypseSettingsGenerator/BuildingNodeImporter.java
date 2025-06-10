package apocalypseSettingsGenerator;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.Graph;
import java.io.*;

//Function to import nodes from a CSV file into a graph structure
public class BuildingNodeImporter {
    public static Graph<RoomNode, RoomEdge> importNodesFromCSV(String csvFilePath) throws IOException {
        Graph<RoomNode, RoomEdge> graph = new DefaultDirectedGraph<>(RoomEdge.class);
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            //Skip header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 8) continue;
                //Check how often the node is supposed to be created, create a RoomNode for each instance the id is incremented for each node
                for (int i = 0; i < Integer.parseInt(parts[7].trim()); i++) {
                    RoomNode node = getRoomNode(parts);
                    graph.addVertex(node);
                }
            }
        }
        return graph;
    }
// Function to create a RoomNode from the parts of a CSV line
    private static RoomNode getRoomNode(String[] parts) {
        String source = parts[0].trim();
        int id = MainZombieApocalypse.nodeIdCounter.getAndIncrement();
        int typeID = Integer.parseInt(parts[1].trim());
        double roomSizeX = Double.parseDouble(parts[2].trim());
        double roomSizeY = Double.parseDouble(parts[3].trim());
        int nrOfHumans = Integer.parseInt(parts[4].trim());
        int nrOfZombies = Integer.parseInt(parts[5].trim());
        int simulationTime = Integer.parseInt(parts[6].trim());
        int numberOfRuns = Integer.parseInt(parts[7].trim());

        return new RoomNode(source, id, typeID, roomSizeX, roomSizeY, nrOfHumans, nrOfZombies, simulationTime, numberOfRuns);
    }
}
