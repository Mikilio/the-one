package apocalypseSettingsGenerator;


//New node class to represent University Rooms
public class RoomNode {
    private final String name;
    private final int id;
    private final double roomSizeX;
    private final double roomSizeY;
    private final int nrOfHumans;
    private final int nrOfZombies;
    private final int simulationTime;
    private final int numberOfRuns;

    public RoomNode(String name, int id, double roomSizeX, double roomSizeY,

                    int nrOfHumans, int nrOfZombies, int simulationTime, int numberOfRuns) {
        this.name = name;
        this.id = id;
        this.roomSizeX = roomSizeX;
        this.roomSizeY = roomSizeY;
        this.nrOfHumans = nrOfHumans;
        this.nrOfZombies = nrOfZombies;
        this.simulationTime = simulationTime;
        this.numberOfRuns = numberOfRuns;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double getRoomSizeX() {
        return roomSizeX;
    }

    public double getRoomSizeY() {
        return roomSizeY;
    }

    public int getNrOfHumans() {
        return nrOfHumans;
    }

    public int getNrOfZombies() {
        return nrOfZombies;
    }

    public int getSimulationTime() {
        return simulationTime;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }
}
