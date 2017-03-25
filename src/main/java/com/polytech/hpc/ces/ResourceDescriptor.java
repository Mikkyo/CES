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

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A resource descriptor that holds the parameters of a resource.
 * @class
 * @author Nicolas
 */
public class ResourceDescriptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceDescriptor.class);
	
	/** Number of virtual cores. */
	private int vcores;
	
	/** Amount of memory, in MB. */
	private int memory;
	
	/**
	 * Creates a new resource descriptor from a number of virtual cores and an amount of memory, 
	 * in MB.
	 * @param vcores The number of virtual cores.
	 * @param memory The amount of memory.
	 * @constructor
	 */
	public ResourceDescriptor(int vcores, int memory) {
		setVcores(vcores);
		setMemory(memory);
	}
	
	/**
	 * Creates a new empty resource descriptor.
	 * @constructor
	 */
	public ResourceDescriptor() {
		vcores = 0;
		memory = 0;
	}
	
	/**
	 * Creates a new resource descriptor from another one.
	 * @param res The resource descriptor to copy.
	 * @constructor
	 */
	public ResourceDescriptor(ResourceDescriptor res) {
		vcores = res.getVcores();
		memory = res.getMemory();
	}
	
	/**
	 * Creates a new resource descriptor from a JSON array.
	 * @param array The JSON array that holds the resource parameters.
	 * @constructor
	 */
	public ResourceDescriptor(JSONArray array) {
		setFromJSONArray(array);
	}
	
	/**
	 * Gets the number of virtual cores;
	 * @return the number of virtual cores;
	 */
	public int getVcores() {
		return vcores;
	}
	
	/**
	 * Gets the amount of memory, in MB.
	 * @return the amount of memory.
	 */
	public int getMemory() {
		return memory;
	}
	
	/**
	 * Sets the number of virtual cores.
	 * @param vcores The number of virtual cores.
	 */
	public void setVcores(int vcores) {
		if (vcores < 0) {
			LOGGER.error("Attempt to set a negative number of vcores");
			this.vcores = 0;
			return;
		}
		this.vcores = vcores;
	}
	
	/**
	 * Sets the amount of memory, in MB.
	 * @param memory The amount of memory.
	 */
	public void setMemory(int memory) {
		if (memory < 0) {
			LOGGER.error("Attempt to set a negative number of memory");
			this.memory = 0;
			return;
		}
		this.memory = memory;
	}
	
	/**
	 * Sets the resource parameters from a JSON array.
	 * @param array The JSON array that holds the resource parameters.
	 */
	public void setFromJSONArray(JSONArray array) {
		try {
			setVcores(array.getInt(0));
			setMemory(array.getInt(1));
		} catch (JSONException e) {
			LOGGER.error("JSONException occured: {}", e.getMessage());
		}
	}
	
	/**
	 * Resets the parameters of a resource descriptor.
	 */
	public void reset() {
		vcores = 0;
		memory = 0;
	}
	
	/**
	 * Adds the parameters of a resource descriptor.
	 * @param res The resource parameters to add.
	 */
	public void add(ResourceDescriptor res) {
		setVcores(vcores + res.getVcores());
		setMemory(memory + res.getMemory());
	}
	
	/**
	 * Subtracts the parameters of a resource descriptor.
	 * @param res The resource parameters to subtract.
	 */
	public void subtract(ResourceDescriptor res) {
		if (!this.isSuperSet(res)) {
			LOGGER.warn("Attempt to substract {} to {}", res, this);
		}
		setVcores(vcores - res.getVcores());
		setMemory(memory - res.getMemory());
	}
	
	/**
	 * Returns whether the resource descriptor is a super set of another one.
	 * @param res The resource descriptor to compare to.
	 * @return true if the resource descriptor is a super set of the given one.
	 */
	public boolean isSuperSet(ResourceDescriptor res) {
		return vcores >= res.getVcores() && memory >= res.getMemory();
	}
	
	/**
	 * Returns the string representation of the resource descriptor.
	 * @return a string representing the resource descriptor.
	 */
	@Override
	public String toString() {
		return "{" + vcores + "vcores, " + memory + "MB}";
	}
}
