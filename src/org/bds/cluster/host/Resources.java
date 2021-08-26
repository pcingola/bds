package org.bds.cluster.host;

import org.bds.BdsLog;
import org.bds.util.Gpr;
import org.bds.util.Timer;

import java.io.Serializable;
import java.util.*;

/**
 * Represents the resources in a host or resources consumed by a task (e.g. cpus, memory, etc.)
 * <p>
 * Can be either a host's resources (how many CPU it has) or a job resources (how much time it needs)
 * <p>
 * Custom resources allow for adding non-standard resources (e.g. GPUs, TPUs, FFPGA and other special devices). The counters
 * are indexed by names ('gpu', 'ffpga', etc.).
 * <p>
 * Any negative number means "information not available"
 *
 * @author pcingola
 */
public class Resources implements Comparable<Resources>, Cloneable, Serializable, BdsLog {

    private static final long serialVersionUID = 764782969174543552L;

    private static int hostResourcesNum = 0;

    protected int id;
    protected int cpus; // Number of CPUs
    protected long mem; // Total memory (in Bytes)
    protected long timeout; // Time before the process is killed (in seconds). Only processing time, it does not include the time the process is queued for execution by the cluster scheduler.
    protected long wallTimeout; // Real time (wall time) before the process is killed (in seconds). This includes the time the process is waiting to be executed.
    protected Map<String, Long> customResources;

    public Resources() {
        cpus = 1; // One CPU
        mem = -1; // No memory info
        timeout = 0; // No timeout
        wallTimeout = 0; // No timeout
        customResources = new HashMap<>();
        id = nextId();
    }

    public Resources(Resources hr) {
        id = nextId();
        set(hr);
    }

    protected static int nextId() {
        return ++hostResourcesNum;
    }

    /**
     * Add resources
     */
    public void add(Resources hr) {
        if (hr == null) return;
        addCpus(hr.cpus);
        addMem(hr.mem);
        for (String rname : hr.getCustomResources().keySet()) {
            setCustomResource(rname, hr.getCustomResource(rname));
        }
    }

    public void addCpus(int cpus) {
        if (this.cpus <= 0) this.cpus = cpus;
        else this.cpus += cpus;
    }

    public void addMem(long mem) {
        if (this.mem <= 0) this.mem = mem;
        else this.mem += mem;
    }

    @Override
    public Resources clone() {
        try {
            var clonedResources = (Resources) super.clone();
            clonedResources.set(this);
            return clonedResources;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int compareTo(Resources hr) {
        int cmpCpu = 0;
        if (cpus >= 0) {
            cmpCpu = cpus - hr.cpus;
            if (cmpCpu < 0) return -1;
            if (cmpCpu > 0) return 1;
        }

        long cmpMem = 0;
        if (mem >= 0) {
            cmpMem = mem - hr.mem;
            if (cmpMem < 0) return -1;
            if (cmpMem > 0) return 1;
        }

        // Compare custom resources
        if (!customResources.isEmpty()) {
            // Compare all resources (sorted by name)
            for (String rname : getCustomResourcesNames()) {
                if (!hr.hasCustomResource(rname)) return 1;
                var rdiff = getCustomResource(rname) - hr.getCustomResource(rname);
                if (rdiff > 0) return 1;
                if (rdiff < 0) return -1;
            }

            // Does 'hr' have more resources?
            if (customResources.size() < hr.customResources.size()) return -1;
        }

        return 0;
    }

    /**
     * Consume resources (subtract resources)
     */
    public void consume(Resources hr) {
        if (hr == null) return;
        if ((cpus >= 0) && (hr.cpus >= 0)) cpus = Math.max(0, cpus - hr.cpus);
        if ((mem >= 0) && (hr.mem >= 0)) mem = Math.max(0, mem - hr.mem);
        // Consume all custom resources
        for (String rname : hr.customResources.keySet()) {
            consumeCustomResource(rname, hr.getCustomResource(rname));
        }
    }

    /**
     * Consume a custom resource 'name'
     *
     * @return true on success, false on error
     */
    protected boolean consumeCustomResource(String name, long quantity) {
        var count = getCustomResource(name);
        if (count <= 0) return false; // We cannot consume a resource we don't have
        var newCount = Math.max(0, count - quantity);
        setCustomResource(name, newCount);
        return true;
    }

    public int getCpus() {
        return cpus;
    }

    public void setCpus(int cpus) {
        this.cpus = cpus;
    }

    public Map<String, Long> getCustomResources() {
        return customResources;
    }

    public long getCustomResource(String name) {
        var count = customResources.get(name);
        return count != null ? count : -1;
    }

    public void setCustomResource(String name, long count) {
        customResources.put(name, count);
    }

    public Collection<String> getCustomResourcesNames() {
        // Note: We sort resources names to ensure comparisons are stable and predictable (i.e. not changing according to how names are hashed)
        var shouldSort = customResources.size() > 1; // If there is only one item, there is no need to sort
        Collection<String> resourceNames;
        if (!shouldSort) return customResources.keySet();

        // Create a list and sort it
        List<String> names = new ArrayList<>();
        names.addAll(customResources.keySet());
        Collections.sort(names);
        return names;
    }

    public long getMem() {
        return mem;
    }

    public void setMem(long mem) {
        this.mem = mem;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;

        // Wall-timeout cannot be shorter than timeout
        if (wallTimeout > 0 && timeout > wallTimeout) wallTimeout = timeout;
    }

    public long getWallTimeout() {
        return wallTimeout;
    }

    public void setWallTimeout(long walltimeout) {
        wallTimeout = walltimeout;
    }

    public boolean hasCustomResource(String name) {
        return customResources.containsKey(name);
    }

    /**
     * Does this resource have at least 'hr' resources?
     */
    public boolean hasResources(Resources r) {
        if (cpus >= 0 && r.cpus > 0 && (cpus - r.cpus) < 0) return false;
        if ((mem >= 0) && r.mem > 0 && (mem - r.mem) < 0) return false;

        // Compare custom resources
        var hrCustomResources = r.getCustomResources();
        if (!hrCustomResources.isEmpty()) {
            // Compare all resources (sorted by name)
            for (String rname : r.getCustomResourcesNames()) {
                if (!hasCustomResource(rname)) return false;
                if (getCustomResource(rname) - r.getCustomResource(rname) < 0) return false;
            }
        }

        return true;
    }

    /**
     * Are all resources consumed?
     */
    public boolean isConsumed() {
        return (cpus <= 0) && (mem <= 0);
    }

    public boolean isValid() {
        if (cpus == 0) return false;
        return mem != 0;
    }

    public void set(Resources r) {
        cpus = r.cpus;
        mem = r.mem;
        timeout = r.timeout;
        wallTimeout = r.wallTimeout;
        // Copy all custom resources
        customResources = new HashMap<>();
        for (Map.Entry<String, Long> e : r.customResources.entrySet()) {
            setCustomResource(e.getKey(), e.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("cpus: " + cpus);
        sb.append("\tmem: " + Gpr.toStringMem(mem));
        if (timeout > 0) sb.append("\ttimeout: " + timeout);
        if (wallTimeout > 0) sb.append("\twall-timeout: " + wallTimeout);
        if (!customResources.isEmpty()) {
            for (String rname : getCustomResourcesNames()) {
                sb.append("\t" + rname + ": " + getCustomResource(rname));
            }
        }
        return sb.toString();
    }

    public String toStringMultiline() {
        StringBuilder sb = new StringBuilder();
        if (cpus > 0) sb.append("cpus: " + cpus + ", ");
        if (mem > 0) sb.append((sb.length() > 0 ? ", " : "") + "mem: " + Gpr.toStringMem(mem));
        if (timeout > 0) sb.append((sb.length() > 0 ? ", " : "") + "timeout: " + Timer.toDDHHMMSS(timeout * 1000));
        if (wallTimeout > 0)
            sb.append((sb.length() > 0 ? ", " : "") + "walltimeout: " + Timer.toDDHHMMSS(wallTimeout * 1000));
        if (!customResources.isEmpty()) {
            for (String rname : getCustomResourcesNames()) {
                sb.append(rname + ": " + getCustomResource(rname) + "\n");
            }
        }
        return sb.toString();
    }
}
