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

import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedList;

import lc.kra.jds.components.Component;
import lc.kra.jds.components.Wire;
import lc.kra.jds.gui.Guitilities;

public class ContactList<Type extends Contact> implements Cloneable, Serializable, Iterable<Type> {
	private static final long serialVersionUID = 1l;

	public enum Alignment { HORIZONTAL, VERTICAL; }

	private LinkedList<Type> contacts;
	private Class<Type> cls;
	private Component component;

	public ContactList(Component component, Class<Type> cls) { this(component, cls, 0); }
	public ContactList(Component component, Class<Type> cls, int count) {
		this.component = component; this.cls = cls;
		contacts = new LinkedList<Type>();
		setContacts(count);	setContactLocations();
	}

	public Type getContact(int index) { return contacts.get(index); }
	public int getContactsCount() { return contacts.size(); }
	public boolean setContacts(int count) {
		if(count<0) throw new IllegalArgumentException("Only positive number of contacts allowed.");
		while(contacts.size()>count) {
			Contact contact = contacts.getLast();
			for(Wire wire=null;(wire=contact.getWire())!=null;)
				wire.removeWire();
			contacts.remove(contact);
		}
		try {
			Constructor<Type> constructor = cls.getConstructor(Component.class);
			while(contacts.size()<count)
				this.contacts.add(constructor.newInstance(component));
		} catch(Exception e) { return false; }
		return true;
	}

	public void setContactLocations() { this.setContactLocations(new Point()); }
	public void setContactLocations(Point location) { this.setContactLocations(location, Alignment.VERTICAL); }
	public void setContactLocations(Alignment alignment) { this.setContactLocations(new Point(), alignment); }
	public void setContactLocations(Point location, Alignment alignment) { setContactLocations(component, contacts.toArray(new Contact[0]), location, alignment); }
	public static <Type extends Contact> void setContactLocations(Component component, Type[] contacts) { setContactLocations(component, contacts, new Point()); }
	public static <Type extends Contact> void setContactLocations(Component component, Type[] contacts, Point location) { setContactLocations(component, contacts, location, Alignment.VERTICAL); }
	public static <Type extends Contact> void setContactLocations(Component component, Type[] contacts, Alignment alignment) { setContactLocations(component, contacts, new Point(), alignment); }
	public static <Type extends Contact> void setContactLocations(Component component, Type[] contacts, Point location, Alignment alignment) {
		float intervalX, intervalY, positionX, positionY;
		intervalX = intervalY = positionX = positionY = 0f;
		switch(alignment) {
		case HORIZONTAL: intervalX = (float)(component.getSize().width+1-location.x)/(contacts.length+1); intervalY = 0; break;
		case VERTICAL:   intervalX = 0; intervalY = (float)(component.getSize().height+1-location.y)/(contacts.length+1); break; }
		for(Contact contact:contacts) {
			positionX += intervalX; positionY += intervalY;
			contact.setLocation(Guitilities.addPoints(location, new Point((int)positionX,
					(int)positionY)));
		}
	}

	public Contact[] toArray() { return contacts.toArray(new Contact[0]); }
	@Override public Iterator<Type> iterator() { return contacts.iterator(); }

	public void paintSolderingJoints(Graphics graphics) { this.paintSolderingJoints(graphics, 5, 5); }
	public void paintSolderingJoints(Graphics graphics, int marginLeft, int marginRight) {
		ContactUtilities.paintSolderingJoints(graphics, marginLeft, marginRight, contacts.toArray(new Contact[0]));
	}

	@Override public ContactList<Type> clone() throws CloneNotSupportedException {
		return cloneForComponent(null);
	}
	public ContactList<Type> cloneForComponent(Component component) throws CloneNotSupportedException {
		@SuppressWarnings("unchecked") ContactList<Type> clone = (ContactList<Type>)super.clone();
		clone.contacts = new LinkedList<Type>();
		for(Type contact:contacts)
			clone.contacts.add(ContactUtilities.cloneForComponent(contact, component));
		clone.component = component;
		return clone;
	}
}