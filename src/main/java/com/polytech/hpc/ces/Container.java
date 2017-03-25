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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a container used to run task.
 * Containers are a subset of the node resources.
 * @class
 * @author Nicolas
 */
public class Container {
	private static final Logger LOGGER = LoggerFactory.getLogger(Container.class);
	
	/** The cluster node on which the container is deployed. */
	private int node;
	
	/** The resource capacity of the container. */
	private ResourceDescriptor capacity;
	
	/** The list of tasks being executed on this container. */
	private ArrayList<Task> tasks;
	
	/** The time at which the container starts to be empty. */
	private int emptyTime;
	
	/**
	 * Creates a new container.
	 * @param node The node on which the container is deployed.
	 * @param capacity The resource capacity of the container.
	 * @constructor
	 */
	public Container(int node, ResourceDescriptor capacity) {
		this.node = node;
		this.capacity = capacity;
		tasks = new ArrayList<Task>();
		emptyTime = 0;
	}
	
	/**
	 * Gets the cluster node on which the container is deployed.
	 * @return the node of the container.
	 */
	public int getNode() {
		return node;
	}
	
	/**
	 * Gets the resource capacity of the container.
	 * @return the resource capacity of the container.
	 */
	public ResourceDescriptor getCapacity() {
		return capacity;
	}
	
	/**
	 * Gets the resource used by the container.
	 * @return the resource used by the container.
	 */
	public ResourceDescriptor getResourcesUsed() {
		ResourceDescriptor res = new ResourceDescriptor();
		for (Task t : tasks) {
			res.add(t.getResources());
		}
		return res;
	}
	
	/**
	 * Gets the resource available in the container.
	 * @return the resource available in the container.
	 */
	public ResourceDescriptor getResourcesAvail() {
		ResourceDescriptor res = new ResourceDescriptor(capacity);
		for (Task t : tasks) {
			res.subtract(t.getResources());
		}
		return res;
	}
	
	public ArrayList<Task> getTasks() {
		return tasks;
	}
	
	public boolean isEmpty() {
		return tasks.isEmpty();
	}
	
	public int getEmptyTime() {
		return emptyTime;
	}
	
	/**
	 * Adds a task to be executed on the container.
	 * @param task The task to execute.
	 */
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	/**
	 * Removes a task to executed on the container.
	 * @param task The task to remove.
	 */
	public void removeTask(Task task) {
		tasks.remove(task);
		if (isEmpty()) {
			emptyTime = Simulator.getTime();
		}
	}
	
	public float getPriorityRaw() {
		float priority = 0.0f;
		if (tasks.size() > 0) {
			for (Task t : tasks) {
				priority += t.getCriticity();
			}
			priority /= tasks.size();
		}
		return priority;
	}
}
