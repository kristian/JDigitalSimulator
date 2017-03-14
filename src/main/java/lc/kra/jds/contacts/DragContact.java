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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import lc.kra.jds.Simulation;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Wire;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;
import lc.kra.jds.exceptions.WireNotConnectable;
import lc.kra.jds.gui.Guitilities;

public final class DragContact extends Contact {
	private static final long serialVersionUID = 1l;

	public final Component component;
	private EventListener[] listener;

	public DragContact(final Simulation simulation, final Class<? extends Contact> cls) {
		this.component = new Component() {
			private static final long serialVersionUID = 1l;
			@Override public void paint(Graphics g) { }
			@Override public Dimension getSize() { return new Dimension(1, 1); }
			@Override public void calculate() {}
			@Override public Component clone() throws CloneNotSupportedException {
				throw new CloneNotSupportedException("You shall not try to clone the component of a drag contact.");
			}
		};
		listener = new EventListener[2];
		simulation.addMouseListener((MouseListener) (listener[0]=new MouseAdapter() {
			@Override public void mouseReleased(MouseEvent event) {
				simulation.removeComponent(component);
				simulation.removeMouseListener((MouseListener)listener[0]);
				simulation.removeMouseMotionListener((MouseMotionListener)listener[1]);
				Contact contact = simulation.searchContactAt(simulation.applyZoom(event.getPoint()), cls);
				Wire wire = getWire();
				if(contact!=null)
					try {
						if(wire.getSource().equals(DragContact.this)) wire.setSource(contact);
						else if(wire.getTarget().equals(DragContact.this)) wire.setTarget(contact);
					} catch(WireNotConnectable e) { wire.removeWire(); }
				else wire.removeWire();
				simulation.repaint();
			}
		}));
		simulation.addMouseMotionListener((MouseMotionListener) (listener[1]=new MouseMotionAdapter() {
			private Point center = new Point(-1, -1);
			@Override public void mouseDragged(MouseEvent event) {
				try {
					Point location = simulation.applyZoom(event.getPoint());
					Contact contact = simulation.searchContactAt(location, cls);
					if(contact!=null)
						component.setLocation(Guitilities.addPoints(contact.getComponent().getLocation(), contact.getLocation(), center));
					else component.setLocation(location);
				}	catch(LocationOutOfBoundsException e) { } //ignore this, if it is dropped outside, nothing happens
			}
		}));
	}
	@Override public float getVoltage() { return 0; }
	@Override public Point getLocation() { return new Point(1, 1); }
	@Override public void setLocation(Point location) {}
	@Override protected void validateLocation() {}
	@Override public Component getComponent() { return component; }
	@Override public void paint(Graphics g) { }
}
