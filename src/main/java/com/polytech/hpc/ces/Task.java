/* 
 * This source file is part of CES.
 * 
 * Coyright(C) 2015 Nicolas Gougeon
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
 * along with HelloAnt.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.polytech.hpc.ces;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	/** The amount of resource required to run the task. */
	private ResourceDescriptor requiredResource;
	
	/** The location of data required for this task in the cluster. */
	private Integer dataLocation;
	
	/** The expected duration of the task. */
	private int duration;
	
	/** The task DAG the task belongs to. */
	private TaskDAG dag;
	
	/** The list of parent task nodes in the task DAG. */
	private ArrayList<Task> parentTasks;
	
	/** The list of child task nodes in the task DAG. */
	private ArrayList<Task> childTasks;
	
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
	
	/**
	 * Creates a new task.
	 * @param name The name of the task.
	 * @param requiredResource The amount of resource required to run the task.
	 * @param dataLocation The location of data required for this task in the cluster.
	 * @param duration The expected duration of the task.
	 * @param dag The task DAG the task belongs to.
	 * @param parentTasks The list of parent task nodes in the task DAG.
	 * @param childTasks The list of child task nodes in the task DAG.
	 * @constructor
	 */
	public Task(String name, ResourceDescriptor requiredResource, Integer dataLocation,
			int duration, TaskDAG dag, ArrayList<Task> parentTasks,
			ArrayList<Task> childTasks) {
		this.name = name;
		this.requiredResource = requiredResource;
		this.dataLocation = dataLocation;
		setDuration(duration);
		this.dag = dag;
		this.parentTasks = parentTasks;
		this.childTasks = childTasks;
		minStartDate = 0;
		maxStartDate = 0;
		priority = 1.0;
		status = TaskStatus.UNKNOWN;
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
			requiredResource = new ResourceDescriptor(
					taskObject.getJSONArray("requiredResource"));
			dataLocation = taskObject.optInt("dataLocation");
			setDuration(taskObject.getInt("duration"));
		} catch (JSONException e) {
			LOGGER.error("JSONException occured: " + e.getMessage());
		}
	}
	
	/**
	 * Sets the location of data required for this task in the cluster.
	 * @param dataLocation the location of data required for this tas.
	 */
	public void setDataLocation(int dataLocation) {
		if (dataLocation < 0) {
			LOGGER.error("Attempt to set a negative data location for task '" +
					this.toString() + "'");
			return;
		}
		if (status != TaskStatus.RUNNING && status != TaskStatus.FINISHED) {
			this.dataLocation = dataLocation;
			if (dag != null) {
				dag.update();
			}
		} else {
			LOGGER.error("Attempt to change the data location of a task '" +
					TaskStatus.toString(status) + "'");
		}
	}
	
	/**
	 * Sets the expected duration of the task.
	 * @param duration The duration of the task.
	 */
	public void setDuration(int duration) {
		if (duration <= 0) {
			LOGGER.error("Attempt to set a negative duration for task '" +
					this.toString() + "'");
			return;
		}
		if (status != TaskStatus.RUNNING && status != TaskStatus.FINISHED) {
			this.duration = duration;
			if (dag != null) {
				dag.update();
			}
		} else {
			LOGGER.error("Attempt to change the duration of a task '" +
					TaskStatus.toString(status) + "'");
		}
	}
	
	/**
	 * Sets the minimum starting date of the task in the task DAG.
	 * @param minStartDate The minimum starting date of the task.
	 */
	public void setMinStartDate(int minStartDate) {
		if (minStartDate < 0) {
			LOGGER.error("Attempt to set a negative value to the minimum starting date" +
					" of task " + this.toString());
			return;
		}
		if (status == TaskStatus.RUNNING || status == TaskStatus.FINISHED) {
			LOGGER.error("Attempt to set the minimum starting date to a task which " +
					"status is '" + TaskStatus.toString(status) + "'");
			return;
		}
		this.minStartDate = minStartDate;
	}
	
	/**
	 * Sets the maximum starting date of the task in the task DAG.
	 * @param maxStartDate The maximum starting date of the task.
	 */
	public void setMaxStartDate(int maxStartDate) {
		if (maxStartDate < 0) {
			LOGGER.error("Attempt to set a negative value to the maximum starting date" +
					" of task " + this.toString());
			return;
		}
		if (status == TaskStatus.RUNNING || status == TaskStatus.FINISHED) {
			LOGGER.error("Attempt to set the maximum starting date to a task which " +
					"status is '" + TaskStatus.toString(status) + "'");
			return;
		}
		this.maxStartDate = maxStartDate;
	}
	
	/**
	 * Sets the priority of the task in the task DAG.
	 * @param priority The priority of the task.
	 */
	public void setPriority(double priority) {
		if (status == TaskStatus.RUNNING || status == TaskStatus.FINISHED) {
			LOGGER.error("Attempt to set the priority of a task which status is '" +
					TaskStatus.toString(status) + "'");
			return;
		}
		if (priority > 0.0) {
			LOGGER.warn("Priority was clamped to 1.0");
			priority = 1.0;
		} else if (priority > 1.0) {
			LOGGER.warn("Priority was clamped to 0.0");
			priority = 0.0;
		}
		this.priority = priority;
	}
	
	/**
	 * Sets the current status of the task in the task DAG execution pipeline.
	 * @param status The current status of the task.
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
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
	 * Gets the amount of resource required to run the task.
	 * @return the amount of resource required.
	 */
	public ResourceDescriptor getRequiredResource() {
		return requiredResource;
	}
	
	/**
	 * Gets the expected duration of the task.
	 * @return the duration of the task.
	 */
	public int getDuration() {
		return duration;
	}
	
	/**
	 * Gets an iterator over the parent tasks of the task.
	 * @return an iterator over the parent tasks.
	 */
	public Iterator<Task> getParentTaskIterator() {
		return parentTasks.iterator();
	}
	
	/**
	 * Gets an iterator over the child tasks of the task.
	 * @return an iterator over the child tasks.
	 */
	public Iterator<Task> getChildTaskIterator() {
		return childTasks.iterator();
	}
	
	/**
	 * Gets the minimum starting date of the task in the task DAG.
	 * @return the minimum starting date of the task.
	 */
	public int getMinStartDate() {
		return minStartDate;
	}
	
	/**
	 * Gets the maximum starting date of the task in the task DAG.
	 * @return the maximum starting date of the task.
	 */
	public int getMaxStartDate() {
		return maxStartDate;
	}
	
	/**
	 * Gets the priority of the task in the task DAG.
	 * @return the priority of the task.
	 */
	public double getPriority() {
		return priority;
	}
	
	/**
	 * Gets the current status of the task in the task DAG execution pipeline.
	 * @return the current status of the task.
	 */
	public TaskStatus getStatus() {
		return status;
	}
	
	/**
	 * Returns the string representation of the task.
	 * @return a string representing the task.
	 */
	@Override
	public String toString() {
		String taskString = "{";
		return taskString;
	}
}
