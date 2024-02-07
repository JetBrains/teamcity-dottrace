

package jetbrains.buildServer.dotTrace.agent;

import java.io.File;
import jetbrains.buildServer.dotNet.buildRunner.agent.CommandLineSetup;
import org.jetbrains.annotations.NotNull;

public class Context {
  private final CommandLineSetup myBaseSetup;
  private final File myProjectFile;
  private final File mySnapshotFile;
  private final File myPatternsFile;
  private final File myReportFile;

  public Context(
    @NotNull final CommandLineSetup baseSetup,
    @NotNull final File projectFile,
    @NotNull final File snapshotFile,
    @NotNull final File patternsFile,
    @NotNull final File reportFile) {
    myBaseSetup = baseSetup;
    myProjectFile = projectFile;
    mySnapshotFile = snapshotFile;
    myPatternsFile = patternsFile;
    myReportFile = reportFile;
  }

  @NotNull
  public CommandLineSetup getBaseSetup() {
    return myBaseSetup;
  }

  @NotNull
  public File getProjectFile() {
    return myProjectFile;
  }

  @NotNull
  public File getSnapshotFile() {
    return mySnapshotFile;
  }

  @NotNull
  public File getPatternsFile() {
    return myPatternsFile;
  }

  @NotNull
  public File getReportFile() {
    return myReportFile;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Context context = (Context)o;

    if (!getBaseSetup().equals(context.getBaseSetup())) return false;
    if (!getProjectFile().equals(context.getProjectFile())) return false;
    if (!getSnapshotFile().equals(context.getSnapshotFile())) return false;
    if (!getPatternsFile().equals(context.getPatternsFile())) return false;
    return getReportFile().equals(context.getReportFile());

  }

  @Override
  public int hashCode() {
    int result = getBaseSetup().hashCode();
    result = 31 * result + getProjectFile().hashCode();
    result = 31 * result + getSnapshotFile().hashCode();
    result = 31 * result + getPatternsFile().hashCode();
    result = 31 * result + getReportFile().hashCode();
    return result;
  }
}