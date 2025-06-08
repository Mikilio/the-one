package apocalypseSettingsGenerator;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class BuildingGraphImporter {
    //Counter for unique edge IDs
    private static final AtomicInteger edgeIdCounter = new AtomicInteger(0);


    //Function to import a graph from a CSV file
    public static Graph<RoomNode, RoomEdge> importGraphFromCSV(String filePath) {
        Supplier<RoomEdge> edgeSupplier = () -> new RoomEdge(edgeIdCounter.getAndIncrement());
        Graph<RoomNode, RoomEdge> graph = new SimpleDirectedGraph<>(null, edgeSupplier, false);

        Map<String, RoomNode> nodes = new HashMap<>();
        Map<String, List<String>> sourceToTargets = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            //Skip the top line of the file (header)
            String line = reader.readLine();

            // First pass: create RoomNode instances with full data
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";", -1);

               //Pick data from each column and store it in variables
                String sourceName = tokens[0].trim();
                String targets = tokens[1].trim();
                double roomSizeX = Double.parseDouble(tokens[2]);
                double roomSizeY = Double.parseDouble(tokens[3]);
                int humans = Integer.parseInt(tokens[4]);
                int zombies = Integer.parseInt(tokens[5]);
                int time = Integer.parseInt(tokens[6]);
                int runs = Integer.parseInt(tokens[7]);

                //Create new RoomNode with read data and add it to graph
                RoomNode node = new RoomNode(sourceName, roomSizeX, roomSizeY, humans, zombies, time, runs);
                nodes.put(sourceName, node);
                graph.addVertex(node);

                // Save to connected nodes (edges)
                if (!targets.isEmpty()) {
                    sourceToTargets.put(sourceName, Arrays.asList(targets.split(",")));
                }
            }

            // Second pass: build edges
            for (Map.Entry<String, List<String>> entry : sourceToTargets.entrySet()) {
                RoomNode source = nodes.get(entry.getKey());
                for (String rawTarget : entry.getValue()) {
                    String targetName = rawTarget.trim();
                    RoomNode target = nodes.computeIfAbsent(targetName,
                            name -> new RoomNode(name, 0, 0, 0, 0, 0, 0)); // in case target was not a source
                    graph.addVertex(target);
                    graph.addEdge(source, target);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graph;
    }

}
