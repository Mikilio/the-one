package apocalypseSettingsGenerator;

import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

//Class to generate settings files from a Graph loaded as CSV file
public class MainZombieApocalypse {


    //Function to create a settings file for a specific node
    private static void createSettingsFile(String name, double RoomSizeX, double RoomSizeY, int nrOfHumans, int nrOfZombies, int simulationTime, Set<RoomEdge> incomingEdges, Set<RoomEdge> outgoingEdges, int numberOfRuns) throws IOException {
        String entrances = "";
        String exits = "################################\n" +
                "# Group 3: Exit\n" +
                "################################\n" +
                "# Exit number + e\n";
        String runs = "";
        String events = "";

        if(!incomingEdges.isEmpty()){
            events = events.concat("#One event per Entrance\n" + "Events.nrof = " + incomingEdges.size() + "\n");
        }
        for (RoomEdge edge : incomingEdges) {
            int eventCounter = 1;
            // Assuming the edge represents an entrance, you can modify this logic as needed
            entrances = entrances.concat("################################\n" +
                    "# Group 4: Entrance for e" + edge.getId() + "e \n" +
                    "################################\n" +
                    "#Number should be the edge number in the graph \n"  +
                    "Group4.groupID = e" + edge.getId() + "e\n" +
                    "Group4.movementModel = ApocalypseMovement\n" +
                    "Group4.initialMovement = NoMovement\n" +
                    "Group4.router = PassiveRouter\n" +
                    "Group4.nrofInterfaces = 2\n" +
                    "Group4.interface1 = agentInterface\n" +
                    "Group4.interface2 = exitInterface\n" +
                    "Group4.nodeLocation = 30, 20\n" +
                    "Group2.speed = 0.5, 1.5\n" +

                    "Group4.nrofHosts = 3\n" +
                    "\n");

            events = events.concat(
                    "Events"+ eventCounter++ + ".filePath = reports/apocalypse/e" +edge.getId() + "e.binee\n");
        }

        for(RoomEdge edge : outgoingEdges) {
            exits = exits.concat("Group3.groupID = "+edge.getId()+"e\n");
        }


        if(numberOfRuns > 1){
            runs =  runs.concat("MovementModel.rngSeed = [");
            for (int i = 1; i < numberOfRuns; i++) {
                runs = runs.concat(i +";");
            }
            runs = runs.concat(numberOfRuns + "]");
        }else {
            runs = runs.concat("MovementModel.rngSeed = 1\n");
        }




        String settings = "Scenario.name = classroom_zombie\n" +
                "Scenario.simulateConnections = true\n" +
                "Scenario.updateInterval = 0.01\n" +
                "Scenario.endTime = " + simulationTime + "\n" +
                "# ~1h 23min\n" +
                "\n" +
                "# Communication settings\n" +
                "#Interaction Human - Zombie\n" +
                "agentInterface.type = AgentInterface\n" +
                "agentInterface.transmitSpeed = 250k\n" +
                "agentInterface.transmitRange = 0.5\n" +
                "#Interactino Human & Zombie - Exit\n" +
                "exitInterface.type = ExitInterface\n" +
                "exitInterface.transmitSpeed = 250k\n" +
                "exitInterface.transmitRange = 1\n" +
                "# close contact only (~1 meter)\n" +
                "\n" +
                "Scenario.nrofHostGroups = 4\n" +
                "\n" +
                "Group.apocalypseControlSystemNr = 0\n" +
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
                "Group1.speed = 0.8, 1\n" +
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
                "Group2.speed = 0.5, 1.5\n" +
                "Group2.nrofHosts = " + nrOfZombies + "\n" +
                "\n" +
                exits +
                "\n #Always stationary\n" +
                "Group3.movementModel = StationaryMovement\n" +
                "Group3.router = PassiveRouter\n" +
                "Group3.nrofInterfaces = 1\n" +
                "Group3.interface1 = exitInterface\n" +
                "Group3.nodeLocation = 0, 0\n" +
                "Group3.nrofHosts = 1\n" +
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
                "MovementModel.warmup = 10\n" +
                "\n" +
                "\n" +
                "##############################\n" +
                "# Infection event handling\n" +
                "##############################\n" +
                events +
                "\n" +
                "\n" +
                "\n" +
                "# No need to change\n" +
                "##############################\n" +
                "# Reporting\n" +
                "##############################\n" +
                "ExitReport.Report.reportDir = reports/apocalypse/\n" +
                "Report.nrofReports = 1\n" +
                "Report.report1 = ExitReport\n" +
                "\n" +
                "##############################\n" +
                "# Optimization\n" +
                "##############################\n" +
                "Optimization.connectionAlg = 2\n" +
                "Optimization.cellSizeMult = 2\n" +
                "Optimization.randomizeUpdateOrder = true\n" +
                "\n" +
                "##############################\n" +
                "# GUI settings (optional)\n" +
                "##############################\n" +
                "GUI.UnderlayImage.fileName =\n" +
                "GUI.UnderlayImage.offset = 0, 0\n" +
                "GUI.UnderlayImage.scale = 1.0\n" +
                "GUI.UnderlayImage.rotate = 0\n" +
                "GUI.EventLogPanel.nrofEvents = 30\n";
        Files.write(Path.of("apocalypse_settings/" + name + ".txt"), settings.getBytes());
    }
    //Function to print the Graph including all relevant data and its edges
    private static void printGraph(Graph<RoomNode, RoomEdge> graph) {
        RoomNode start = graph.vertexSet().iterator().next();
        DepthFirstIterator<RoomNode, RoomEdge> iterator = new DepthFirstIterator<>(graph, start);
        while (iterator.hasNext()) {
            RoomNode node = iterator.next();
            System.out.println("Processing node: " + node.name +
                    " (Size: " + node.roomSizeX + "x" + node.roomSizeY +
                    ", Humans: " + node.nrOfHumans +
                    ", Zombies: " + node.nrOfZombies +
                    ", Simulation Time: " + node.simulationTime +
                    ", Number of Runs: " + node.numberOfRuns + ")");
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
            createSettingsFile(node.name, node.roomSizeX, node.roomSizeY, node.nrOfHumans, node.nrOfZombies, node.simulationTime, buildingLayout.incomingEdgesOf(node), buildingLayout.outgoingEdgesOf(node)
                    ,node.numberOfRuns);

        }
    }

    public static void main(String[] args) throws IOException {

        //Depth Search through the graph and create settings files for each room
        Graph<RoomNode, RoomEdge> buildingLayout = BuildingGraphImporter.importGraphFromCSV("example_graphs/buildingLayout.csv");
        generateSettingsFiles(buildingLayout);
        //printGraph(buildingLayout);

    }
}
