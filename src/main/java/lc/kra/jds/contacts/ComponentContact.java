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
package lc.kra.jds.contacts;

import lc.kra.jds.components.Component;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;

import java.awt.Dimension;
import java.awt.Point;

public abstract class ComponentContact extends Contact implements Cloneable {
				private static final long serialVersionUID = 1l;

				protected Point location;
				protected Component component;

				public ComponentContact(Component component) { this(component, new Point()); }
				public ComponentContact(Component component, Point location) {
								super();
								this.component = component;
								this.setLocation(location);
				}

				@Override public Component getComponent() { return component; }
				@Override public Point getLocation() { return this.location; }
				@Override public void setLocation(Point location) throws LocationOutOfBoundsException {
								this.location = location;
								if(location.x+location.y!=0)
												validateLocation(); //store location but throw a exception
				}

				@Override protected void validateLocation() throws LocationOutOfBoundsException {
								Component component = getComponent();
								if(component==null)
												return;
								Dimension size = getComponent().getSize();
								Point location = this.getLocation();
								if(size.width<location.x||size.height<location.y)
												throw new LocationOutOfBoundsException(location, size);
				}

				@Override public ComponentContact clone() throws CloneNotSupportedException {
								return this.cloneForComponent(null);
				}
				public ComponentContact cloneForComponent(Component component) throws CloneNotSupportedException {
								ComponentContact clone = (ComponentContact)super.clone();
								clone.location = (Point)location.clone();
								if((clone.component=component)!=null);
								clone.validateLocation();
								return clone;
				}
}
