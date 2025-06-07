package movement.map;

import core.Coord;

public class ApocalypseMapNode extends MapNode{
    /**
     * Constructor. Creates a map node to a location.
     *
     * @param location The location of the node.
     */
    public double roomSizeX;
    public double roomSizeY;
    public int nrOfExits;
    public int nrOfEntrances;
    public int nrOfHumans;
    public int nrOfZombies;
    public int SimulationTime;


    public ApocalypseMapNode(Coord location) {
        super(location);
    }
    public ApocalypseMapNode(Coord location, double roomSizeX, double roomSizeY, int nrOfExits, int nrOfEntrances, int nrOfHumans, int nrOfZombies, int SimulationTime) {
        super(location);
        this.roomSizeX = roomSizeX;
        this.roomSizeY = roomSizeY;
        this.nrOfExits = nrOfExits;
        this.nrOfEntrances = nrOfEntrances;
        this.nrOfHumans = nrOfHumans;
        this.nrOfZombies = nrOfZombies;
        this.SimulationTime = SimulationTime;
    }

    public double getRoomSizeX() {
        return roomSizeX;
    }

    public double getRoomSizeY() {
        return roomSizeY;
    }

    public int getNrOfExits() {
        return nrOfExits;
    }

    public int getNrOfEntrances() {
        return nrOfEntrances;
    }

    public int getNrOfHumans() {
        return nrOfHumans;
    }

    public int getNrOfZombies() {
        return nrOfZombies;
    }

    public int getSimulationTime() {
        return SimulationTime;
    }
}
