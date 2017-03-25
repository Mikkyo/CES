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
 * CES base class.
 */
public class Simulator {
	
	private static int time;
	
	public Simulator() {
		time = 0;
	}
	
	public static void main(String[] args) {
		new Simulator();
		while (!isFinished()) {
			time++;
			// update all app
			// update rm
		}
	}
	
	public static int getTime() {
		return time;
	}
	
	private static boolean isFinished() {
		for (Application app : applications) {
			if (!app.isFinished()) return false;
		}
	}
	
	
}
