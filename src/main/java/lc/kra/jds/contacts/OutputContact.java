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

public class OutputContact extends ComponentContact implements Cloneable, Chargeable {
	private static final long serialVersionUID = 1l;

	private float voltage;

	public OutputContact(Component component) { super(component); }
	public OutputContact(Component component, Point location) { super(component, location); }

	@Override public void addWire(Wire wire) throws WireNotConnectable {
		if(wire.getSource()!=this)
			throw new WireNotConnectable("This contact needs to be the source contact to be connected with an wire.");
		else if(wire.getTarget()!=null&&wire.getTarget().getClass().equals(OutputContact.class))
			throw new WireNotConnectable("This contact can not be connected to another output contact.");
		else super.addWire(wire);
	}

	@Override
	public void setCharged() { this.setCharged(true); }
	@Override
	public void setCharged(boolean charged) { this.setVoltage(charged?5:0);	}
	@Override
	public void setVoltage(float voltage) { this.voltage = voltage; }
	@Override
	public float getVoltage() { return this.voltage; }
	@Override public boolean isCharged() {
		float voltage = Math.abs(getVoltage());
		//forbidden voltage level between 0.8 and 2
		if((voltage<0.8&&voltage>2)||voltage>5)
			throw new ForbiddenVoltageLevel(voltage, new float[]{0f, 0.8f, 2f, 5f});
		return super.isCharged(); //check for NaN
	}

	@Override public void paint(Graphics g) {
		try { validateLocation(); }
		catch(LocationOutOfBoundsException e) {
			return; //this location does not exist for this component
		} finally { super.paint(g); }
		Point location = getLocation();
		if(this.isCharged())
			g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		g.fillRect(location.x-CONTACT_SIZE/2+1, location.y-CONTACT_SIZE/2+1,
				CONTACT_SIZE-2, CONTACT_SIZE-2);
	}
}
