package core;


import movement.map.ApocalypseMapNode;
import movement.map.SimMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainZombieApocalypse {


    private static SimMap<ApocalypseMapNode> buildingLayout(){
        ApocalypseMapNode origin = new ApocalypseMapNode(new Coord(0, 0), 30, 20, 1, 1, 12, 2, 5000);
        ApocalypseMapNode hallwayOrigin = new ApocalypseMapNode(new Coord(0, 0), 100, 4, 1, 1, 0, 0, 5000);
        ApocalypseMapNode classSameFinger = new ApocalypseMapNode(new Coord(0, 0), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode balconyEtage1 = new ApocalypseMapNode( new Coord(2, -1), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode hallwayUninfected = new ApocalypseMapNode( new Coord(3, 1), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode bridge = new ApocalypseMapNode(new Coord(3, -1), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode stairs = new ApocalypseMapNode( new Coord(4, 0), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode classEtageNord = new ApocalypseMapNode( new Coord(4, 1), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode magistrale = new ApocalypseMapNode( new Coord(5, -1), 30, 20, 1, 1, 0, 0, 5000);
        ApocalypseMapNode otherEtages = new ApocalypseMapNode( new Coord(6, 1), 30, 20, 1, 1, 0, 0, 5000);

        //Build connections between nodes
        origin.addNeighbor(hallwayOrigin);
        hallwayOrigin.addNeighbor(classSameFinger);
        hallwayOrigin.addNeighbor(balconyEtage1);
        classSameFinger.addNeighbor(hallwayUninfected);
        balconyEtage1.addNeighbor(bridge);
        hallwayUninfected.addNeighbor(stairs);
        bridge.addNeighbor(stairs);
        stairs.addNeighbor(classEtageNord);
        stairs.addNeighbor(magistrale);
        classEtageNord.addNeighbor(otherEtages);

    // Create map node list
        Map<Coord, ApocalypseMapNode> nodesMap = new HashMap<>();
        for (ApocalypseMapNode node : Arrays.asList(origin, hallwayOrigin, classSameFinger, balconyEtage1, hallwayUninfected, bridge, stairs, classEtageNord, magistrale, otherEtages)) {
            nodesMap.put(node.getLocation(), node);
        }

        return new SimMap(nodesMap);
    }
    //Function to create a settings file for each scenario listed in the Graph
    private static void createSettingsFile(String name, double RoomSizeX, double RoomSizeY, int nrOfHumans, int nrOfZombies, int simulationTime, int nrOfEntrances) throws IOException {
        String entrances = "";
        for (int i = 0; i < nrOfEntrances; i++) {
            entrances.concat("");
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
                "################################\n" +
                "# Group 3: Exit\n" +
                "################################\n" +
                "# Exit number + e\n" +
                "Group3.groupID = 1e\n" +
                " #Always stationary\n" +
                "Group3.movementModel = StationaryMovement\n" +
                "Group3.router = PassiveRouter\n" +
                "Group3.nrofInterfaces = 1\n" +
                "Group3.interface1 = exitInterface\n" +
                "Group3.nodeLocation = 0, 0\n" +
                "Group3.nrofHosts = 1\n" +
                "\n" +
                "################################\n" +
                "# Group 4: Entrance for 1e \n" +
                "################################\n" +
                "Group4.groupID = e1e\n" +
                "Group4.movementModel = ApocalypseMovement\n" +
                "Group4.initialMovement = NoMovement\n" +
                "Group4.router = PassiveRouter\n" +
                "Group4.nrofInterfaces = 2\n" +
                "Group4.interface1 = agentInterface\n" +
                "Group4.interface2 = exitInterface\n" +
                "Group4.nodeLocation = 30, 20\n" +
                "Group2.speed = 0.5, 1.5\n" +
                "Group4.nrofHosts = 3\n" +
                "\n" +
                "\n" +
                "\n" +
                "##############################\n" +
                "# Movement model settings\n" +
                "##############################\n" +
                "MovementModel.rngSeed = 1\n" +
                "# classroom size\n" +
                "MovementModel.worldSize = " + RoomSizeX + " , " + RoomSizeY + "\n" +
                "# short warmup\n" +
                "MovementModel.warmup = 10\n" +
                "\n" +
                "\n" +
                "##############################\n" +
                "# Infection event handling\n" +
                "##############################\n" +
                "#One event per Entrance\n" +
                "Events.nrof = 1\n" +
                "Events1.filePath = reports/apocalypse/e1e.binee\n" +
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
        Files.write(Path.of("example_settings/" + name + ".txt"), settings.getBytes());
    }

    public static void main(String[] args) throws IOException {
        // Initialize settings
        createSettingsFile("TestRoom", 1000, 20000, 50, 60, 5000, 1);

       SimMap<ApocalypseMapNode> buildingLayout = buildingLayout();

        DTNSim.main( new String[] {"example_settings/TestRoom.txt" });
        for (ApocalypseMapNode currentNode : buildingLayout.getNodes()){
            String nodeName = currentNode.toString();
            createSettingsFile(nodeName, currentNode.getRoomSizeX(), currentNode.getRoomSizeY(), currentNode.getNrOfHumans(), currentNode.getNrOfZombies(), currentNode.getSimulationTime(), currentNode.getNrOfEntrances());

        }

    }
}
