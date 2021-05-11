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
import lc.kra.jds.components.Wire;
import lc.kra.jds.exceptions.ForbiddenVoltageLevel;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;
import lc.kra.jds.exceptions.WireNotConnectable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class InputContact extends ComponentContact implements Cloneable {
				private static final long serialVersionUID = 1l;

				public InputContact(Component component) { super(component); }
				public InputContact(Component component, Point location) { super(component, location); }

				@Override public void addWire(Wire wire) throws WireNotConnectable {
								if(countWires()>=1)
												throw new WireNotConnectable("Is is only allowed to connect one wire to an input contact.");
								else if(wire.getTarget()!=this)
												throw new WireNotConnectable("This contact needs to be the target contact to be connected with an wire.");
								else if(wire.getSource()!=null&&wire.getSource().getClass().equals(InputContact.class))
												throw new WireNotConnectable("This contact can not be connected to another input contact.");
								else super.addWire(wire);
				}

				@Override public float getVoltage() {
								if(!isWired())
												return Float.NaN;
		/*Contact contact = getWire().getSource();
		if(contact.equals(this)) {
			System.err.println("Wrong wiring!");
			return Float.NaN;
		}*/
								return getWire().getVoltage();
				}
				@Override
				public boolean isCharged() throws ForbiddenVoltageLevel {
								float voltage = Math.abs(getVoltage());
								//forbidden voltage level between 0.4 and 2.4
								if((voltage<0.4&&voltage>2.4)||voltage>5)
												throw new ForbiddenVoltageLevel(voltage, new float[]{0f, 0.4f, 2.4f, 5f});
								return super.isCharged(); //check for NaN
				}

				@Override public void paint(Graphics g) {
								try { validateLocation(); }
								catch(LocationOutOfBoundsException e) {
												return; //this location does not exist for this component
								} finally { super.paint(g); }
								Point location = getLocation();
								g.setColor(Color.GRAY);
								g.fillRect(location.x-CONTACT_SIZE/2+1, location.y-CONTACT_SIZE/2+1,
																CONTACT_SIZE-2, CONTACT_SIZE-2);
				}
}
