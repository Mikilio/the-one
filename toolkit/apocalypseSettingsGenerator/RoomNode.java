package apocalypseSettingsGenerator;


//New node class to represent University Rooms
public class RoomNode {
    public String name;
    public double roomSizeX;
    public double roomSizeY;
    public int nrOfHumans;
    public int nrOfZombies;
    public int simulationTime;
    public int numberOfRuns;

    public RoomNode(String name, double roomSizeX, double roomSizeY,

                    int nrOfHumans, int nrOfZombies, int simulationTime, int numberOfRuns) {
        this.name = name;
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
}
