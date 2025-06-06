package report;

import core.ConnectionListener;
import core.DTNHost;
import core.Settings;
import core.SimScenario;
import input.BinaryEventsReader;
import input.ExternalEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import movement.ApocalypseMovement;
import movement.StationaryMovement;

/**
 * Link connectivity report generator for ONE StandardEventsReader input. Connections that start
 * during the warm up period are ignored.
 */
public class ExitReport extends Report implements ConnectionListener {

  private class ExitLog {
    public String groupId;
    public List<ExternalEvent> events;

    public ExitLog(String groupId) {
      this.groupId = groupId;
      this.events = new ArrayList<>();
    }

    public void done() {
      try {
        BinaryEventsReader.storeToBinaryFile(reportDir + groupId, events);
      } catch (IOException e) {
      }
      ;
    }
  }

  private String reportDir;
  private ArrayList<ExitLog> logs;

  /** Constructor. */
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
      logs.add(new ExitLog("e" + exit));
    }
  }

  /**
   * Called when the simulation is done, user requested premature termination or intervalled report
   * generating decided that it's time for the next report.
   */
  public void done() {
    for (ExitLog log : logs) {
      log.done();
    }
  }

  public void hostsConnected(DTNHost h1, DTNHost h2) {
    if (h1.getMovement() instanceof StationaryMovement) {

      Boolean zombie = h2.getMovement() instanceof ApocalypseMovement;
      String line = String.join(" ", "e" + h1.groupId, zombie.toString(), createTimeStamp());

      newEvent();
      write(line);
    }
  }

  public void hostsDisconnected(DTNHost h1, DTNHost h2) {}

  /**
   * Creates and returns a "@" prefixed time stamp of the current simulation time
   *
   * @return time stamp of the current simulation time
   */
  private String createTimeStamp() {
    return String.format("%.2f", getSimTime());
  }

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
}
