package report;

import core.*;
import input.BinaryEventsReader;
import input.ExitEvent;
import input.ExternalEvent;
import movement.ApocalypseMovement;
import movement.ExitMovement;
import movement.ZombieMovement;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Link connectivity report generator for ONE StandardEventsReader input. Connections that start
 * during the warm up period are ignored.
 */
public class ExitReport extends Report implements ConnectionListener {

    private String reportDir;
    private int nrofEscapedHumans;
    private int nrofEscapedAny;
    private ArrayList<ExitLog> logs;
    /**
     * Constructor.
     */
    public ExitReport() {

        Settings settings = new Settings();
        settings = getSettings();

        String outDir = settings.getSetting(REPORTDIR_SETTING);
        if (!outDir.endsWith("/")) {
            outDir += "/"; // make sure dir ends with directory delimiter
        }
        this.reportDir = outDir;
        this.logs = new ArrayList<>();

        List<String> exits = getExits();
        for (String exit : exits) {
            logs.add(new ExitLog(exit));
        }
    }

    /**
     * Creates and returns a "@" prefixed time stamp of the current simulation time
     *
     * @return time stamp of the current simulation time
     */
    public static List<String> getExits() {
        List<String> entrances = new ArrayList<String>();
        Settings s = new Settings(SimScenario.SCENARIO_NS);
        int nrofGroups = s.getInt(SimScenario.NROF_GROUPS_S);
        for (int i = 1; i <= nrofGroups; i++) {
            Settings g = new Settings(SimScenario.GROUP_NS + i);
            g.setSecondaryNamespace(SimScenario.GROUP_NS);
            String gid = g.getSetting(SimScenario.GROUP_ID_S);
            if (gid.matches("^\\d+e$")) {
                entrances.add(gid);
            }
        }
        return entrances;
    }

    /**
     * Called when the simulation is done, user requested premature termination or intervalled report
     * generating decided that it's time for the next report.
     */
    public void done() {
        for (ExitLog log : logs) {
            log.done();
        }
        writeSummary();
    }

    public void hostsConnected(DTNHost h1, DTNHost h2) {
        if (h1.getMovement() instanceof ExitMovement
                && h2.getMovement() instanceof ApocalypseMovement) {
            ApocalypseMovement agentMovement = (ApocalypseMovement) h2.getMovement();

            Boolean zombie = agentMovement.getCurrentMovementModel() instanceof ZombieMovement;
            for (ExitLog log : logs) {
                if (log.groupId == h1.groupId) {
                    double time = getSimTime();
                    log.events.add(new ExitEvent("e" + h1.groupId, zombie, time));
                    if (!zombie) nrofEscapedHumans++;
                    nrofEscapedAny++;
                    System.out.println(
                            "Exit: " + (zombie ? "Zombie" : "Human") + " @" + time + " ->" + h1.groupId);
                    return;
                }
            }
        }
    }

    public void hostsDisconnected(DTNHost h1, DTNHost h2) {
    }

    private void writeSummary() {
        Path summaryDir = Paths.get(reportDir, "summaries");
        Path summaryFile = summaryDir.resolve(getScenarioName() + ".csv");

        try {
            // Create directories if they do not exist
            Files.createDirectories(summaryDir);

            // Use try-with-resources to write to the file
            try (BufferedWriter writer =
                         Files.newBufferedWriter(
                                 summaryFile,
                                 StandardCharsets.UTF_8,
                                 StandardOpenOption.CREATE,
                                 StandardOpenOption.TRUNCATE_EXISTING)) {
                // Write header
                writer.write("humans escaped;hosts escaped in total");
                for (ExitLog log : logs) {
                    writer.write(";" + log.groupId);
                }
                writer.write("\n");

                // write values
                writer.write(nrofEscapedHumans + ";" + nrofEscapedAny);
                for (ExitLog log : logs) {
                    writer.write(";" + log.events.size());
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing summary file: " + e.getMessage());
        }
    }

    private class ExitLog {
        public String groupId;
        private List<ExternalEvent> events;

        public ExitLog(String groupId) {
            this.groupId = groupId;
            this.events = new ArrayList<>();
        }

        public void done() {
            try {
                BinaryEventsReader.storeToBinaryFile(reportDir + "e" + groupId, events);
            } catch (IOException e) {
                throw new SimError("Coudn't write Logs for exit: " + groupId);
            }
        }
    }
}
