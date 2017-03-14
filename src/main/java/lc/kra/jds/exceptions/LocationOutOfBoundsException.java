/*
 * JDigitalSimulator
 * Copyright (C) 2017 Kristian Kraljic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lc.kra.jds.exceptions;

import java.awt.Dimension;
import java.awt.Point;

public class LocationOutOfBoundsException extends RuntimeException {
	private static final long serialVersionUID = 3291698016184856236L;
	public LocationOutOfBoundsException() { super("The location you have tried to set is out of bounds."); }
	public LocationOutOfBoundsException(Point location) { super("The location "+location.x+"/"+location.y+" is out of bounds."); }
	public LocationOutOfBoundsException(Point location, Dimension bounds) {
		super("The location "+location.x+"/"+location.y+" is out of the bounds "+bounds.width+"/"+bounds.height);
	}
}