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
package lc.kra.jds.components;

import static lc.kra.jds.gui.Guitilities.addPoints;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import lc.kra.jds.Utilities;
import lc.kra.jds.components.Component.HiddenComponent;
import lc.kra.jds.components.buildin.general.Junction;
import lc.kra.jds.contacts.Charged;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.exceptions.ForbiddenVoltageLevel;
import lc.kra.jds.exceptions.WireNotConnectable;
import lc.kra.jds.exceptions.WiringException;
import lc.kra.jds.gui.Guitilities;

@HiddenComponent public class Wire extends Component implements Sociable, Charged {
	private static final long serialVersionUID = 2l;

	private float voltage;

	private Contact source, target;
	private Contact[] contacts;

	private Point preferredLocation;

	private LocationChangeListener listener;
	private transient GeneralPath line;
	private transient Area selection;

	public Wire(Contact source, Contact target) throws WireNotConnectable {
		listener = new LocationChangeListener();
		setSource(source);
		setTarget(target);
		contacts = new Contact[] {};
	}

	public Contact getSource() { return this.source; }
	public Contact getTarget() { return this.target; }
	public void setSource(Contact source) throws WireNotConnectable {
		if(source==null)
			throw new WiringException("You can not remove one side of a wire.");
		if(this.source!=null)
			removeWire(this.source);
		addWire(this.source = source);
	}
	public void setTarget(Contact target) throws WireNotConnectable {
		if(target==null)
			throw new WiringException("You can not remove one side of a wire.");
		if(this.target!=null)
			removeWire(this.target);
		addWire(this.target = target);
	}
	public void removeWire() {
		source.getComponent().removePropertyChangeListener("location", listener);
		source.removeWire(this);
		target.getComponent().removePropertyChangeListener("location", listener);
		target.removeWire(this);
	}

	private void addWire(Contact contact) throws WireNotConnectable {
		contact.addWire(this);
		contact.getComponent().addPropertyChangeListener("location", listener);
		revalidate();
	}
	private void removeWire(Contact contact) {
		contact.getComponent().removePropertyChangeListener("location", listener);
		contact.removeWire(this);
	}

	@Override public void paint(Graphics defaultGraphics) { paint(defaultGraphics, (Point[])null); }
	public void paint(Graphics defaultGraphics, Point... offset) {
		Graphics2D graphics = ((Graphics2D)defaultGraphics);
		graphics.setColor(isCharged()?Color.RED:Color.BLUE);
		if(offset!=null)
			revalidate(offset);
		else if(line==null)
			revalidate();
		graphics.draw(line);
	}
	@Override public Contact[] getContacts() { return contacts; }
	@Override public Dimension getSize() { return null; }
	@Override public void calculate() { //note this calculation method will be called prior to all others
		if(source!=null) //should never happen
			this.voltage = source.getVoltage();
		else this.voltage = Float.NaN;
	}

	@Override public float getVoltage() { return voltage; }
	@Override public boolean isCharged() throws ForbiddenVoltageLevel {
		float voltage = Math.abs(getVoltage());
		//everything between below 1 is 0
		return Math.floor(voltage)!=0;
	}

	public boolean containsLocation(Point location) { return selection.contains(location); }
	public void setPreferredLocation(Point location) {
		preferredLocation = location;
		revalidate();
	}

	@Override public Wire clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("You can not clone a single wire. You have to create a new wire to connect some components.");
	}

	public void revalidate() { revalidate((Point[])null); }
	public void revalidate(Point... offset) {
		if(source==null||source.getComponent()==null||target==null||target.getComponent()==null)
			return;
		Point from = addPoints(source.getComponent().getLocation(), source.getLocation()),
				to = addPoints(target.getComponent().getLocation(), target.getLocation());
		if(offset!=null) {
			if(offset.length>=1) from = addPoints(from, offset[0]);
			if(offset.length>=2)   to = addPoints(to  , offset[1]);
		}
		if(line==null)
			line = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		else line.reset();
		line.moveTo(from.x, from.y);
		if(from.x<to.x) {
			int m_x;
			if(preferredLocation!=null)
				m_x = Math.max(from.x, Math.min(to.x, preferredLocation.x));
			else if(source.getComponent() instanceof Junction)
				m_x = from.x;
			else m_x = (from.x+to.x)/2;
			line.lineTo(m_x, from.y);
			line.lineTo(m_x, to.y);
			line.lineTo(to.x, to.y);
		} else {
			int m_y;
			if(preferredLocation!=null)
				if(Utilities.getConfiguration("limit")!=null)
					if(from.y<to.y)
						m_y = Math.max(from.y, Math.min(to.y, preferredLocation.y));
					else m_y = Math.max(to.y, Math.min(from.y, preferredLocation.y));
				else m_y = preferredLocation.y;
			else m_y = (from.y+to.y)/2;
			if(!(source.getComponent() instanceof Junction)) {
				line.lineTo(from.x+20, from.y);
				line.lineTo(from.x+20, m_y);
			} else line.lineTo(from.x, m_y);
			line.lineTo(to.x-20, m_y);
			line.lineTo(to.x-20, to.y);
			line.lineTo(to.x, to.y);
		}

		selection = new Area();
		PathIterator iterator = line.getPathIterator(null);
		float[] current = null;
		while(!iterator.isDone()) {
			float[] last = current; current = new float[2];
			iterator.currentSegment(current); iterator.next();
			if(last==null)
				continue;
			if(last[0]!=current[0]) //horrizontal (else vertical) line
				selection.add(new Guitilities.Rectangle(new Point2D.Float(last[0], last[1]-3), new Point2D.Float(current[0], current[1]+4)).toArea());
			else selection.add(new Guitilities.Rectangle(new Point2D.Float(last[0]-3, last[1]), new Point2D.Float(current[0]+4, current[1])).toArea());
			selection.add(new Area(new Rectangle2D.Double(current[0]-3, current[1]-3, 7, 7))); //corner point
		}
	}

	public Area getSelectionArea() {
		if(line==null)
			revalidate();
		return this.selection;
	}

	protected class LocationChangeListener implements PropertyChangeListener, Serializable {
		private static final long serialVersionUID = 1l;
		@Override public void propertyChange(PropertyChangeEvent event) {
			preferredLocation = null;
			revalidate();
		}
	}
}
