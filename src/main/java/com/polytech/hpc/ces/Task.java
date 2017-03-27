/* 
 * This source file is part of CES.
 * 
 * Copyright(C) 2015 Nicolas Gougeon
 *
 * CES is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CES is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CES.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.polytech.hpc.ces;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a task in a task DAG.
 * @class
 * @author Nicolas
 */
public class Task {
	private static final Logger LOGGER = LoggerFactory.getLogger(Task.class);
	
	/** The name of the task. */
	private String name;
	
	/** The amount of resources required to run the task. */
	private ResourceDescriptor requiredResources;
	
	/** The expected duration of the task. */
	private int duration;
	
	/** The location of data required for this task in the cluster. */
	private Integer dataNodeId;
	
	/** The list of parent tasks in the task DAG. */
	private ArrayList<Task> parentTasks;
	
	/** The list of child tasks in the task DAG. */
	private ArrayList<Task> childTasks;
	
	/** The task DAG the task belongs to. */
	private TaskDAG dag;
	
	/** The minimum starting date of the task in the task DAG. */
	private int minStartDate;
	
	/** The maximum starting date of the task in the task DAG. */
	private int maxStartDate;
	
	/** The priority of the task in the task DAG. */
	private double priority;
	
	/** The container that runs the task. */
	// private Container container;
	
	/** The current status of the task in the task DAG execution pipeline. */
	private TaskStatus status;
	
	/** The list of execution attempts for the task. */
	private ArrayList<TaskExecutionRecord> executionAttempts;
	
	/**
	 * Creates a new task.
	 * @param name The name of the task.
	 * @param requiredResources The amount of resources required to run the task.
	 * @param duration The expected duration of the task.
	 * @param dataNodeId The location of data required for this task in the cluster.
	 * @param parentTasks The list of parent task nodes in the task DAG.
	 * @param childTasks The list of child task nodes in the task DAG.
	 * @constructor
	 */
	public Task(String name, ResourceDescriptor requiredResources, Integer dataNodeId,
			int duration, ArrayList<Task> parentTasks, ArrayList<Task> childTasks,
			TaskDAG dag) {
		dag = null;
		minStartDate = 0;
		maxStartDate = 0;
		priority = 1.0;
		// container = null;
		status = TaskStatus.UNKNOWN;
		this.name = name;
		this.requiredResources = requiredResources;
		setDuration(duration);
		setDataNodeId(dataNodeId);
		for (Task task : parentTasks) addParentTask(task);
		for (Task task : childTasks) addChildTask(task);
	}
	
	/**
	 * Creates a new task from a JSON object.
	 * @param dag The task DAG the task belongs to.
	 * @param taskObject The JSON object that holds the properties of the task.
	 * @constructor
	 */
	public Task(TaskDAG dag, JSONObject taskObject) {
		this.dag = dag;
		try {
			name = taskObject.getString("name");
			requiredResources = new ResourceDescriptor(
					taskObject.getJSONArray("requiredResources"));
			setDuration(taskObject.getInt("duration"));
			setDataNodeId(taskObject.optInt("dataNodeId"));
		} catch (JSONException e) {
			LOGGER.error("JSONException occured: {}", e.getMessage());
		}
	}
	
	/**
	 * Returns whether a task is a root task in the task DAG.
	 * @return true if the task is a root task.
	 */
	public boolean isRoot() {
		return parentTasks.size() == 0;
	}
	
	/**
	 * Returns whether a task is a leaf task in the task DAG.
	 * @return true if the task is a leaf task.
	 */
	public boolean isLeaf() {
		return childTasks.size() == 0;
	}
	
	/**
	 * Sets the expected duration of the task.
	 * @param duration The duration of the task.
	 */
	public void setDuration(int duration) {
		if (duration <= 0) {
			LOGGER.error("Attempt to set a invalid duration ({}) for task {}", duration,
					getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to modify the duration of the task {} which is {}",
					getName(), TaskStatus.toString(status));
			return;
		}
		this.duration = duration;
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Sets the location of data required for this task in the cluster.
	 * @param dataNodeId the node identifier of data required for this task.
	 */
	public void setDataNodeId(Integer dataNodeId) {
		if (dataNodeId < 0) {
			LOGGER.error("Attempt to set a negative data location for task {}",
					getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to modify the data location of task {} which is {}",
					getName(), TaskStatus.toString(status));
			return;
		}
		this.dataNodeId = dataNodeId;
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Adds a parent to the task in the task DAG.
	 * @param task The parent task to add.
	 */
	public void addParentTask(Task task) {
		if (task == this) {
			LOGGER.error("Attempt to add task {} as a parent of itself", getName());
			return;
		}
		if (parentTasks.contains(task)) {
			LOGGER.error("Attempt to add task {} which is already a parent of {}",
					task.getName(), getName());
			return;
		}
		if (childTasks.contains(task)) {
			LOGGER.error("Attempt to add task {} which is already a child of {}",
					task.getName(), getName());
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to add a parent task to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		parentTasks.add(task);
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Removes a parent to the task in the task DAG.
	 * @param task The parent task to remove.
	 */
	public void removeParentTask(Task task) {
		if (!parentTasks.contains(task)) {
			LOGGER.error("Attempt to remove parent task {} that is not a parent of {}",
					task.getName(), getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to add a parent task to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		parentTasks.remove(task);
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Removes all parents of the task in the task DAG.
	 */
	public void clearParentTasks() {
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to clear parent tasks to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		parentTasks.clear();
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Adds a child to the task in the task DAG.
	 * @param task The child task to add.
	 */
	public void addChildTask(Task task) {
		if (task == this) {
			LOGGER.error("Attempt to add task {} as a child of itself", getName());
			return;
		}
		if (childTasks.contains(task)) {
			LOGGER.error("Attempt to add task {} which is already a child of {}",
					task.getName(), getName());
			return;
		}
		if (parentTasks.contains(task)) {
			LOGGER.error("Attempt to add task {} which is already a parent of {}",
					task.getName(), getName());
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to add a child task to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		childTasks.add(task);
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Removes a child to the task in the task DAG.
	 * @param task The child task to remove.
	 */
	public void removeChildTask(Task task) {
		if (!childTasks.contains(task)) {
			LOGGER.error("Attempt to remove child task {} that is not a child of {}",
					task.getName(), getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to remove a child task to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		childTasks.remove(task);
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Removes all children of the task in the task DAG.
	 */
	public void clearChildTasks() {
		if (status.ordinal() >= TaskStatus.READY.ordinal()) {
			LOGGER.error("Attempt to clear child tasks to {} which is {}", getName(),
					TaskStatus.toString(status));
			return;
		}
		childTasks.clear();
		if (dag != null) {
			dag.update();
		}
	}
	
	/**
	 * Sets the task DAG to which the task belongs to.
	 * @param dag The task DAG the task belongs to.
	 * @warning The task is supposed to be added in the task DAG and removed from the
	 * older one.
	 */
	public void setDAG(TaskDAG dag) {
		this.dag = dag;
	}
	
	/**
	 * Gets the name of the task.
	 * @return the name of the task.
	 */
	public String getName() {
		String taskName = "";
		if (dag != null) {
			taskName += dag.getName() + ".";
		}
		taskName += name;
		return taskName;
	}
	
	/**
	 * Gets the amount of resources required to run the task.
	 * @return the amount of resources required.
	 */
	public ResourceDescriptor getRequiredResources() {
		return requiredResources;
	}
	
	/**
	 * Gets the expected duration of the task.
	 * @return the duration of the task.
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * Gets the location of data required for this task in the cluster.
	 * @return the node identifier of data required for this task.
	 */
	public Integer getDataNodeId() {
		return dataNodeId;
	}
	
	/**
	 * Gets the list of parent tasks in the task DAG.
	 * @return the list of parent tasks.
	 */
	public ArrayList<Task> getParentTasks() {
		return parentTasks;
	}
	
	/**
	 * Gets the list of child tasks in the task DAG.
	 * @return the list of child tasks.
	 */
	public ArrayList<Task> getChildTasks() {
		return childTasks;
	}
	
	/**
	 * Gets the DAG the task belongs to.
	 * @return the DAG of the task.
	 */
	public TaskDAG getDAG() {
		return dag;
	}
	
	/**
	 * Returns the string representation of the task.
	 * @return a string representing the task.
	 */
	@Override
	public String toString() {
		String parentTasksString = "";
		boolean first = true;
		for (Task parent : parentTasks) {
			if (!first) {
				parentTasksString += ", ";
			}
			parentTasksString += "\"" + parent.getName() + "\"";
			first = false;
		}
		String childTasksString = "";
		first = true;
		for (Task child : childTasks) {
			if (!first) {
				childTasksString += ", ";
			}
			childTasksString += "\"" + child.getName() + "\"";
			first = false;
		}
		String taskString = "Task {\n\tname: \"" + getName() + "\"\n\trequiredResources: "
				+ requiredResources + "\n\tduration: " + duration + "\n\tdataNodeId: "
				+ dataNodeId + "\n\tparentTasks: [" + parentTasksString + "]\n\t"
				+ "childTasks: [" + childTasksString + "]\n\tdag: \"" +  dag.getName()
				+ "\"\n\tminStartDate: " + minStartDate + "\n\tmaxStartDate: "
				+ maxStartDate + "\n\tpriority: " + priority + "\n\tstatus \""
				+ TaskStatus.toString(status) + "\"\n}";
		return taskString;
	}
}
