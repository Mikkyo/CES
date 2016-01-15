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

/**
 * Represents a task status in the task DAG execution pipeline.
 * @enum
 * @author Nicolas
 */
public enum TaskStatus {
	UNKNOWN,  // The task is waiting for being updated in the task DAG.
	PENDING,  // The task is waiting for other tasks to finish.
	READY,    // The task is ready to be run.
	RUNNING,  // The task is running.
	FINISHED; // The task is finished.
	
	/**
	 * Gets the string representation of a task status.
	 * @param status The input task status.
	 * @return a string representing the task status.
	 */
	public static String toString(TaskStatus status) {
		switch (status) {
		case UNKNOWN: return "unknown";
		case PENDING: return "pending";
		case READY: return "ready";
		case RUNNING: return "running";
		case FINISHED: return "finished";
		default: return "?";
		}
	}
}
