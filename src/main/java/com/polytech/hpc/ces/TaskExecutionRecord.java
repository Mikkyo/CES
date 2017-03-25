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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an execution attempt of a task.
 * Tasks are executed by a container from a starting date to an ending date. Tasks may be
 * preempted. This class is used to represent an execution of a task in a diagram.
 * The entity is supposed to be created every time a task starts to run.
 * @class
 * @author Nicolas
 */
public class TaskExecutionRecord {
	public static final Logger LOGGER = LoggerFactory.getLogger(
			TaskExecutionRecord.class);
	
	/** The task executed. */
	private Task task;
	
	/** The starting date of the execution attempt. */
	private int startDate;
	
	/** The ending date of the execution attempt. */
	private Integer endDate;
	
	/** The container used to execute the task for this execution attempt. */
	private Container container;
	
	/** Whether the task was preempted during the execution attempt. */
	private boolean preempted;
	
	/**
	 * Creates a new task execution record.
	 * The entity is supposed to be created every time a task starts to run.
	 * @param task The task executed.
	 * @param startDate The starting date of the execution attempt.
	 * @param container The container used to execute the task.
	 * @constructor
	 */
	public TaskExecutionRecord(Task task, int startDate, Container container) {
		this.task = task;
		this.startDate = startDate;
		endDate = null;
		this.container = container;
		preempted = false;
	}
	
	/**
	 * Sets the ending date of the execution attempt.
	 * @param endDate the ending date of the execution attempt.
	 */
	public void setEndDate(int endDate) {
		if (endDate <= startDate) {
			LOGGER.error("Attempt to end the task {} at {} whereas it starts at {}",
					task.getName(), endDate, startDate);
			return;
		}
		this.endDate = endDate;
	}
	
	/**
	 * Sets whether the task was preempted during the execution attempt.
	 * True means the execution attempt failed.
	 * @param preempted Whether the task was preempted.
	 */
	public void setPreempted(boolean preempted) {
		this.preempted = preempted;
	}
	
	/**
	 * Gets the task of the record.
	 * @return the task executed.
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * Gets the starting date of the execution attempt.
	 * @return the starting date of the execution attempt.
	 */
	public int getStartDate() {
		return startDate;
	}
	
	/**
	 * Gets the ending date of the execution attempt.
	 * A null ending date means the task is still running.
	 * @return the ending date of the execution attempt.
	 */
	public Integer getEndDate() {
		return endDate;
	}
	
	/**
	 * Gets the container used to execute the task for this execution attempt.
	 * @return the container used to execute the task.
	 */
	public Container getContainer() {
		return container;
	}
	
	/**
	 * Returns whether the task was preempted during the execution attempt.
	 * @return true if the task was preempted (the execution failed), false otherwise.
	 */
	public boolean isPreempted() {
		return preempted;
	}
	
	/**
	 * Returns the string representation of a task execution attempt.
	 * @return a string representing the attempt of execution of the task.
	 */
	public String toString() {
		String endDateString = endDate != null ? endDate.toString() : "?";
		return task.getName() + ": d=(" + startDate + "," + endDateString + ") p="
				+ preempted;
	}
}
