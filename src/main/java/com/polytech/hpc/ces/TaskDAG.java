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
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a Directed Acyclic Diagram of tasks.
 * @see https://goo.gl/6Sba20
 * @class
 * @author Nicolas
 */
public class TaskDAG {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskDAG.class);
	
	/** The name of the task DAG. */
	private String name;
	
	/** The list of task nodes. */
	private ArrayList<Task> tasks;
	
	/**
	 * Creates a new task DAG.
	 * @constructor
	 */
	public TaskDAG() {
		tasks = new ArrayList<Task>();
	}
	
	/**
	 * Creates a new task DAG from a JSON object.
	 * @param dagObject The JSON object that holds the task DAG.
	 */
	public TaskDAG(JSONObject dagObject) {
		try {
			setName(dagObject.getString("name"));
			// task identifier association
			// warning: task name can appear several times
			HashMap<String, Integer> taskId = new HashMap<String, Integer>();
			/*for (JSONObject taskObject : dagObject.getJSONArray("tasks")) {
				addTask(new Task(this, taskObject));
			}*/
		} catch (JSONException e) {
			LOGGER.error("JSONException occured: " + e.getMessage());
		}
	}
	
	/**
	 * Sets the name of the task DAG.
	 * @param name the name of the task DAG.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the name of the task DAG.
	 * @return the name of the task DAG.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a task in the task DAG.
	 * @param task The task to add.
	 */
	public void addTask(Task task) {
		if (tasks.contains(task)) {
			LOGGER.error("Attempt to add the task {} which is already in DAG {}",
					task.getName(), getName());
			return;
		}
		tasks.add(task);
		// Setup child tasks
		task.clearChildTasks();
		for (Task parentTask : tasks) {
			
		}
		update();
	}
	
	/**
	 * Updates the tasks parameters of the task DAG.
	 */
	public void update() {
		// TODO: minStartDate
		// TODO: maxStartDate
		// TODO: priority
		int maxStartDate = 0;
	}
	
	/**
	 * Returns the list of root tasks in the task DAG.
	 * @return the list of root tasks.
	 */
	public ArrayList<Task> getRootTasks() {
		ArrayList<Task> rootTasks = new ArrayList<Task>();
		for (Task task : tasks) {
			if (task.isRoot()) {
				rootTasks.add(task);
			}
		}
		return rootTasks;
	}
	
	/**
	 * Returns the list of leaf tasks in the task DAG.
	 * @return the list of lead tasks.
	 */
	public ArrayList<Task> getLeafTasks() {
		ArrayList<Task> leafTasks = new ArrayList<Task>();
		for (Task task : tasks) {
			if (task.isLeaf()) {
				leafTasks.add(task);
			}
		}
		return leafTasks;
	}
	
	/**
	 * Returns the string representation of a task DAG.
	 * @return a string representing the task DAG.
	 */
	@Override
	public String toString() {
		String dagString = name + ": [";
		boolean first = true;
		for (Task task : tasks) {
			if (!first) {
				dagString += ", ";
			}
			dagString += task.toString();
			first = false;
		}
		dagString += "]";
		return dagString;
	}
}
