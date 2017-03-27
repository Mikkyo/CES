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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the execution context of a task.
 * Every task to be executed has a priority that depends on the minimum and maximum
 * starting date in the task DAG.
 * @class
 * @author Nicolas
 */
public class TaskExecutionContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(
			TaskExecutionContext.class);
	
	/** The task wrapped in the execution context. */
	private Task task;
	
	/** The minimum starting date of the task in the task DAG. */
	private int minStartDate;
	
	/** The maximum starting date of the task in the task DAG. */
	private int maxStartDate;
	
	/** The priority of the task in the task DAG. */
	private double priority;
	
	/** The container used to run the task. */
	private Container container;
	
	/** The current status of the task in the task DAG execution pipeline. */
	private TaskStatus status;
	
	/**
	 * Creates a new task execution context.
	 * @param task The task wrapped in the execution context.
	 * @constructor
	 */
	public TaskExecutionContext(Task task) {
		this.task = task;
		minStartDate = 0;
		maxStartDate = Integer.MAX_VALUE;
		priority = 0.0;
		container = null;
		status = TaskStatus.UNKNOWN;
	}
	
	/**
	 * Sets the minimum starting date of the task in the task DAG.
	 * @param minStartDate The minimum starting date of the task.
	 */
	public void setMinStartDate(int minStartDate) {
		/*
		if (minStartDate < 0) {
			LOGGER.error("Attempt to set a negative minimum starting date to task {}",
					task.getName());
			return;
		}
		if (minStartDate > maxStartDate) {
			LOGGER.error("Attempt to set a minimum starting date ({}) greater than the "
					+ "maximum starting date ({}) to task {}", minStartDate, maxStartDate,
					task.getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.RUNNING.ordinal()) {
			LOGGER.error("Attempt to modify the minimum starting date to task {} which is"
					+ " {}", task.getName(), TaskStatus.toString(status));
			return;
		}
		*/
		this.minStartDate = Math.max(minStartDate, this.minStartDate);
		for (Task child : task.getChildTasks()) {
			child.setMinStartDate(this.minStartDate + task.getDuration());
		}
	}
	
	/**
	 * Sets the maximum starting date of the task in the task DAG.
	 * @param maxStartDate The maximum starting date of the task.
	 */
	public void setMaxStartDate(int maxStartDate) {
		/*
		if (maxStartDate < 0) {
			LOGGER.error("Attempt to set a negative maximum starting date to task {}",
					task.getName());
			return;
		}
		if (maxStartDate < minStartDate) {
			LOGGER.error("Attempt to set a maximum starting date ({}) lower than the "
					+ "minimum starting date ({}) to task {}", maxStartDate, minStartDate,
					task.getName());
			return;
		}
		if (status.ordinal() >= TaskStatus.RUNNING.ordinal()) {
			LOGGER.error("Attempt to modify the maximum starting date to task {} which "
					+ " is {}", task.getName(), TaskStatus.toString(status));
			return;
		}
		*/
		this.maxStartDate = Math.min(maxStartDate, this.maxStartDate);
		for (Task parent : task.getParentTasks()) {
			parent.setMaxStartDate(this.maxStartDate - task.getDuration());
		}
	}
	
	/**
	 * Sets the priority of the task in the execution context.
	 * @param priority The priority of the task.
	 */
	public void setPriority(double priority) {
		if (priority > 0.0) {
			LOGGER.warn("Priority was clamped to 1.0 for task {}", task.getName());
			priority = 1.0;
		} else if (priority > 1.0) {
			LOGGER.warn("Priority was clamped to 0.0 for task {}", task.getName());
			priority = 0.0;
		}
		this.priority = priority;
	}
	
	/**
	 * Sets the container used to run the task.
	 * @param container The container used to run the task.
	 */
	public void setContainer(Container container) {
		this.container = container;
	}
	
	/**
	 * Sets the current status of the task in the task DAG execution pipeline.
	 * @param status The current status of the task.
	 */
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	
	/**
	 * Gets the task wrapped in the execution context.
	 * @return the task of the execution context.
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * Gets the minimum starting date of the task in the task DAG.
	 * @return the minimum starting date of the task.
	 */
	public Integer getMinStartDate() {
		return minStartDate;
	}
	
	/**
	 * Gets the maximum starting date of the task in the task DAG.
	 * @return the maximum starting date of the task.
	 */
	public Integer getMaxStartDate() {
		return maxStartDate;
	}
	
	/**
	 * Gets the priority of the task in the task DAG.
	 * @return the priority of the task.
	 */
	public Double getPriority() {
		return priority;
	}
	
	/**
	 * Gets the container used to run the task.
	 * @return the container that runs the task.
	 */
	public Container getContainer() {
		return container;
	}
	
	/**
	 * Gets the current status of the task in the task DAG execution pipeline.
	 * @return the current status of the task.
	 */
	public TaskStatus getStatus() {
		return status;
	}
	
	/**
	 * Gets the string representation of the task execution context.
	 * @return a string representing the execution context of the task.
	 */
	public String toString() {
		return task.getName() + ": d=(" + minStartDate + "," + maxStartDate + ") p="
				+ priority + " s=\"" + TaskStatus.toString(status) + "\"";
	}
}
