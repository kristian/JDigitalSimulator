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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lc.kra.jds.Locatable;
import lc.kra.jds.Paintable;
import lc.kra.jds.Utilities;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Wire;
import lc.kra.jds.exceptions.ForbiddenVoltageLevel;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;
import lc.kra.jds.exceptions.WireNotConnectable;

public abstract class Contact implements Cloneable, Paintable, Locatable, Charged, Serializable {
	private static final long serialVersionUID = 1l;

	protected static final int CONTACT_SIZE = 4;

	protected List<Wire> wires;

	public Contact() { wires = new ArrayList<Wire>(); }
	public Contact(Wire wire) throws WireNotConnectable {
		this();
		this.setWire(wire);
	}

	public void setWire(Wire wire) throws WireNotConnectable { setWires(wire); }
	public void setWires(Wire... wires) throws WireNotConnectable { this.setWires(Arrays.asList(wires)); }
	public void setWires(List<Wire> wires) throws WireNotConnectable {
		this.wires.clear();
		for(Wire wire:wires)
			addWire(wire);
	}
	public void addWire(Wire wire) throws WireNotConnectable {
		if(!this.wires.contains(wire))
			this.wires.add(wire);
	}
	public Wire getWire() { return !isWired()?null:this.wires.get(0); }
	public List<Wire> getWires() { return this.wires; }
	public boolean isWired() { return countWires()!=0; }
	public int countWires() { return wires.size(); }
	public boolean removeWire(Wire wire) { return this.wires.remove(wire); }
	public void removeWires() { this.wires.clear(); }

	public abstract Component getComponent();

	@Override public abstract float getVoltage();
	@Override public boolean isCharged() throws ForbiddenVoltageLevel {
		float voltage = Math.abs(getVoltage());
		//everything between below 1 is 0
		return Math.floor(voltage)!=0;
	}

	@Override public void paint(Graphics g) {
		try { validateLocation(); }
		catch(LocationOutOfBoundsException e) {
			return; //this location does not exist for this component
		}
		Point location = getLocation();
		g.setColor(Color.BLACK);
		g.fillRect(location.x-CONTACT_SIZE/2, location.y-CONTACT_SIZE/2,
				CONTACT_SIZE, CONTACT_SIZE);
	}

	protected abstract void validateLocation() throws LocationOutOfBoundsException;

	@Override public Contact clone() throws CloneNotSupportedException {
		Contact clone = (Contact)super.clone();
		clone.wires = new ArrayList<Wire>(); //do not clone wires!
		return clone;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		List<Wire> wires = this.wires;
		if(Utilities.isCopying()) //do not clone wires!
			this.wires = new ArrayList<Wire>();
		out.defaultWriteObject();
		this.wires = wires;
	}
}
