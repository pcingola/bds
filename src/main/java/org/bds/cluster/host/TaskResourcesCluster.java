package org.bds.cluster.host;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bds.BdsLog;
import org.bds.lang.value.Value;
import org.bds.lang.value.ValueList;
import org.bds.lang.value.ValueMap;
import org.bds.lang.value.ValueString;
import org.bds.run.BdsThread;
import org.bds.scope.GlobalScope;

/**
 * Represents resources consumed by a task
 *
 * @author pcingola
 */
public class TaskResourcesCluster extends TaskResources implements BdsLog {

	private static final long serialVersionUID = -1849044143332368043L;

	protected String queue; // Preferred execution queue
	protected String runCmdOptionsString;
	protected List<String> runCmdOptionsList;
	protected Map<String, String> runCmdOptionsMap;

	public TaskResourcesCluster() {
		super();
	}

	public TaskResourcesCluster(TaskResourcesCluster hr) {
		super(hr);
	}

	public String getQueue() {
		return queue;
	}

	public List<String> getRunCmdOptionsList() {
		return runCmdOptionsList;
	}

	public Map<String, String> getRunCmdOptionsMap() {
		return runCmdOptionsMap;
	}

	public String getRunCmdOptionsString() {
		return runCmdOptionsString;
	}

	@Override
	public void setFromBdsThread(BdsThread bdsThread) {
		super.setFromBdsThread(bdsThread);

		// Set queue
		setQueue(bdsThread.getString(GlobalScope.GLOBAL_VAR_TASK_OPTION_QUEUE));

		// Try to find a 'resources' value
		Value taskResources = bdsThread.getValue(GlobalScope.GLOBAL_VAR_TASK_OPTION_RESOURCES);
		if (taskResources == null) return; // Nothing to do

		// If the value a hash?
		if (taskResources.getType().isMap()) setRunCmdOptions((ValueMap) taskResources, bdsThread.isDebug());
		else if (taskResources.getType().isList()) setRunCmdOptions((ValueList) taskResources, bdsThread.isDebug());
		else if (taskResources.getType().isString()) setRunCmdOptions((ValueString) taskResources, bdsThread.isDebug());
		else error("Unable to parse '" + GlobalScope.GLOBAL_VAR_TASK_OPTION_RESOURCES + "', type " + taskResources.getType());
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public void setRunCmdOptions(ValueList taskResources, boolean debug) {
		runCmdOptionsList = new ArrayList<>();
		for (Value v : taskResources) {
			String vstr = v.asString();
			if (debug) debug("Task resource list: Adding value '" + vstr + "'");
			runCmdOptionsList.add(vstr);
		}
	}

	public void setRunCmdOptions(ValueMap taskResources, boolean debug) {
		runCmdOptionsMap = new HashMap<>();
		for (Value k : taskResources.keySet()) {
			String kstr = k.asString();
			Value v = taskResources.getValue(k);
			String vstr = v != null ? v.asString() : "";
			if (debug) debug("Task resource map: Adding key '" + kstr + "', value '" + vstr + "'");
			runCmdOptionsMap.put(kstr, vstr);
		}
	}

	public void setRunCmdOptions(ValueString taskResources, boolean debug) {
		runCmdOptionsString = taskResources.asString();
		if (debug) debug("Task resource string: Setting value '" + runCmdOptionsString + "'");
	}
}
