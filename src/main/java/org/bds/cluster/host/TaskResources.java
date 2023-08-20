package org.bds.cluster.host;

import org.bds.executioner.Executioners;
import org.bds.executioner.Executioners.ExecutionerType;
import org.bds.run.BdsThread;
import org.bds.scope.GlobalScope;

/**
 * Represents resources consumed by a task
 *
 * @author pcingola
 */
public class TaskResources extends Resources {

    private static final long serialVersionUID = 761051924090735902L;

    public TaskResources() {
        super();
    }

    public TaskResources(TaskResources hr) {
        super(hr);
    }

    /**
     * Create an appropriate resource type, given an executioner
     */
    public static TaskResources factory(ExecutionerType exType) {
        switch (exType) {
            case AWS:
                return new TaskResourcesAws();

            case CLUSTER:
            case FAKE:
            case GENERIC:
            case MOAB:
            case PBS:
            case SGE:
            case SLURM:
                return new TaskResourcesCluster();

            case LOCAL:
            case SSH:
                return new TaskResources();

            default:
                throw new RuntimeException("Unknown resource type for executioner '" + exType + "'. This should never happen!");
        }
    }

    /**
     * Set resources from bdsThread
     *
     * @param bdsThread
     */
    public void setFromBdsThread(BdsThread bdsThread) {
        debug("Setting resources from bds variables");
        setCpus((int) bdsThread.getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_CPUS));
        setMem(bdsThread.getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_MEM));
        setWallTimeout(bdsThread.getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_WALL_TIMEOUT));
        setTimeout(bdsThread.getInt(GlobalScope.GLOBAL_VAR_TASK_OPTION_TIMEOUT));

        // Check custom resources
        var systemName = bdsThread.getString(GlobalScope.GLOBAL_VAR_TASK_OPTION_SYSTEM);
        var exType = Executioners.ExecutionerType.parseSafe(systemName);
        var resources = Executioners.getInstance().getCustomResources(exType);
        // Check each variable named like a resource
        if (resources != null) {
            for (String resourceName : resources.keySet()) {
                var count = bdsThread.getInt(resourceName);
                setCustomResource(resourceName, count);
            }
        }
    }
}
