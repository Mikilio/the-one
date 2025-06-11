package apocalypseSettingsGenerator;

import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

//Class to generate settings files from a Graph loaded as CSV file
public class MainZombieApocalypse {
    public static final AtomicInteger edgeIdCounter = new AtomicInteger(0);
    public static final AtomicInteger nodeIdCounter = new AtomicInteger(0);

    //build graph from two CSV files one for nodes and one for edges
    private static Graph<RoomNode, RoomEdge> buildingGraph(String nodeName, String edgeName) throws IOException {
        Graph<RoomNode, RoomEdge> graph = BuildingNodeImporter.importNodesFromCSV("example_graphs/" + nodeName);
        return BuildingEdgeImporter.importEdgesFromCSV(graph, "example_graphs/" + edgeName);

    }

    //Function to create a settings file for a specific node
    private static void createSettingsFile(String name, double RoomSizeX, double RoomSizeY, int nrOfHumans, int nrOfZombies, int simulationTime, Set<RoomEdge> incomingEdges, Set<RoomEdge> outgoingEdges, int runIndex) throws IOException {

        int groupCounter = 3;
        int eventCounter = 1;

        String entrances = "";
        String exits = "";
        String runs = "MovementModel.rngSeed = [" + java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 10001) + "]\n";
        String events = "";


        //Logic to create settings for Exits
        for (RoomEdge edge : outgoingEdges) {

            exits = exits.concat(
                    "################################\n" +
                            "# Group " + groupCounter + ": Exit\n" +
                            "################################\n" +
                            "# Exit number " + edge.getId() + "e\n" +
                            "Group" + groupCounter + ".groupID = " + edge.getId() + "e\n" +
                            "#Always stationary\n" +
                            "Group" + groupCounter + ".movementModel = StationaryMovement\n" +
                            "Group" + groupCounter + ".router = PassiveRouter\n" +
                            "Group" + groupCounter + ".nrofInterfaces = 1\n" +
                            "Group" + groupCounter + ".interface1 = exitInterface\n" +
                            "Group" + groupCounter + ".nodeLocation = " + (int) edge.getExit().getX() + ", " + (int) edge.getExit().getY() + "\n" +
                            "Group4.priority = " + edge.getExitPriority() + "\n" +
                            "Group" + groupCounter + ".nrofHosts = 1\n\n"
            );
            groupCounter++;
        }


        //Logic to create settings for Entrances
        if (!incomingEdges.isEmpty()) {
            events = events.concat("#One event per Entrance\n" + "Events.nrof = " + incomingEdges.size() + "\n");
        }

        for (RoomEdge edge : incomingEdges) {
            // Assuming the edge represents an entrance, you can modify this logic as needed
            entrances = entrances.concat("################################\n" +
                    "# Group " + groupCounter + ": Entrance for e" + edge.getId() + "e \n" +
                    "################################\n" +
                    "#Number should be the edge number in the graph \n" +
                    "Group" + groupCounter + ".groupID = e" + edge.getId() + "e\n" +
                    "Group" + groupCounter + ".movementModel = ApocalypseMovement\n" +
                    "Group" + groupCounter + ".initialMovement = NoMovement\n" +
                    "Group" + groupCounter + ".router = PassiveRouter\n" +
                    "Group" + groupCounter + ".nrofInterfaces = 2\n" +
                    "Group" + groupCounter + ".interface1 = agentInterface\n" +
                    "Group" + groupCounter + ".interface2 = exitInterface\n" +
                    "Group" + groupCounter + ".nodeLocation = " + (int) edge.getEntrance().getX() + ", " + (int) edge.getEntrance().getY() + "\n" +
                    //  "Group2.speed = 0.5, 1.5\n" +

                    "Group" + groupCounter + ".nrofHosts = 3\n" +
                    "\n");

            events = events.concat(
                    "Events" + eventCounter++ + ".filePath = reports/apocalypse/e" + edge.getId() + "e.binee\n");
        }

//Logic to add random seeds for runs if simulation is supposed to be run multiple times
  /*      if (runIndex > 1) {
            runs = runs.concat("MovementModel.rngSeed = [");
            for (int i = 1; i < runIndex; i++) {
                runs = runs.concat(i + ";");
            }
            runs = runs.concat(runIndex + "]");
        } else {
            runs = runs.concat("MovementModel.rngSeed = 1\n");
        }
*/

        //Merge above created logic into settings file template
        String settings = "Scenario.name = " + name + "\n" +
                //"Scenario.simulateConnections = true\n" +
                //"Scenario.updateInterval = 0.01\n" +
                "Scenario.endTime = " + simulationTime + "\n" +
                "\n" +
                "Scenario.nrofHostGroups = " + groupCounter + "\n" +
                "\n" +
                "################################\n" +
                "# Group 1: Random Humans (Students)\n" +
                "################################\n" +
                "Group1.groupID = rH\n" +
                "Group1.movementModel = ApocalypseMovement\n" +
                "Group1.initialMovement = HumanMovement\n" +
                "#Can be changed to global for all\n" +
                "Group1.router = PassiveRouter\n" +
                "# Custom router to handle infection logic\n" +
                "Group1.nrofInterfaces = 2\n" +
                "Group1.interface1 = agentInterface\n" +
                "Group1.interface2 = exitInterface\n" +
                " #measured in m/s, perhaps not needed configurated and set later in function (SwitchableMovement)\n" +
                //  "Group1.speed = 0.8, 1\n" +
                "Group1.nrofHosts = " + nrOfHumans + "\n" +
                "\n" +
                "\n" +
                "################################\n" +
                "# Group 2: Random Zombies\n" +
                "################################\n" +
                "Group2.groupID = rZ\n" +
                "Group2.movementModel = ApocalypseMovement\n" +
                "Group2.initialMovement = ZombieMovement\n" +
                "Group2.router = PassiveRouter\n" +
                "Group2.nrofInterfaces = 2\n" +
                "Group2.interface1 = agentInterface\n" +
                "Group2.interface2 = exitInterface\n" +
                //     "Group2.speed = 0.5, 1.5\n" +
                "Group2.nrofHosts = " + nrOfZombies + "\n" +
                "\n" +
                exits +
                "\n " +
                "\n" +
                entrances +
                "\n" +
                "\n" +
                "\n" +
                "##############################\n" +
                "# Movement model settings\n" +
                "##############################\n" +
                runs +
                "\n# classroom size\n" +
                "MovementModel.worldSize = " + RoomSizeX + " , " + RoomSizeY + "\n" +
                "# short warmup\n" +
                "MovementModel.warmup = 200\n" +
                "\n" +
                "\n" +
                "##############################\n" +
                "# Infection event handling\n" +
                "##############################\n" +
                events;
        Files.write(Path.of("apocalypse_settings/" + name + ".txt"), settings.getBytes());
    }

    //Function to print the Graph including all relevant data and its edges
    private static void printGraph(Graph<RoomNode, RoomEdge> graph) {
        RoomNode start = graph.vertexSet().iterator().next();
        DepthFirstIterator<RoomNode, RoomEdge> iterator = new DepthFirstIterator<>(graph, start);
        while (iterator.hasNext()) {
            RoomNode node = iterator.next();
            System.out.println("Processing node: " + node.toString() +
                    " (Size: " + node.getRoomSizeX() + "x" + node.getRoomSizeY() +
                    ", Humans: " + node.getNrOfHumans() +
                    ", Zombies: " + node.getNrOfZombies() +
                    ", Simulation Time: " + node.getSimulationTime() +
                    ", Number of Runs: " + node.getNumberOfRuns() + ")");
            for (RoomEdge edge : graph.outgoingEdgesOf(node)) {
                System.out.println("  Connected to edge: " + edge.toString() + " with ID: " + edge.getId());
            }
        }
    }

    //Function that uses the generates settings for each node (Room) in the Graph
    public static void generateSettingsFiles(Graph<RoomNode, RoomEdge> buildingLayout) throws IOException {

        RoomNode start = buildingLayout.vertexSet().iterator().next();
        DepthFirstIterator<RoomNode, RoomEdge> iterator = new DepthFirstIterator<>(buildingLayout, start);
        while (iterator.hasNext()) {

            RoomNode node = iterator.next();
            createSettingsFile(node.toString(), node.getRoomSizeX(), node.getRoomSizeY(), node.getNrOfHumans(), node.getNrOfZombies(), node.getSimulationTime(), buildingLayout.incomingEdgesOf(node), buildingLayout.outgoingEdgesOf(node), node.getNumberOfRuns());

        }
    }

    public static void main(String[] args) throws IOException {

        //Depth Search through the graph and create settings files for each room
        Graph<RoomNode, RoomEdge> buildingLayout = buildingGraph("nodeList.csv", "edgeList.csv");
       generateSettingsFiles(buildingLayout);
      //  printGraph(buildingLayout);

    }
}
