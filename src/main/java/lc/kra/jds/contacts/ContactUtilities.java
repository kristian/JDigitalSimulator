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

import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.Array;

public class ContactUtilities {
				public static Contact[] concatenateContacts(Contact contact, Contact[] contacts) { return concatenateContacts(new Contact[]{contact}, contacts); }
				public static Contact[] concatenateContacts(Contact[]... contacts) {
								int length = 0; for(Contact[] contact:contacts) length += contact.length;
								int offset = 0; Contact[] concatenated = new Contact[length];
								for(Contact[] contact:contacts) {
												System.arraycopy(contact, 0, concatenated, offset, contact.length);
												offset += contact.length;
								}
								return concatenated;
				}

				public static void paintSolderingJoint(Graphics graphics, Contact contact) { paintSolderingJoints(graphics, contact); }
				public static void paintSolderingJoint(Graphics graphics, int marginLeft, int marginRight, Contact contact) { paintSolderingJoints(graphics, marginLeft, marginRight, contact); }
				public static void paintSolderingJoints(Graphics graphics, Contact... contacts) { paintSolderingJoints(graphics, 5, 5, contacts); }
				public static void paintSolderingJoints(Graphics graphics, int marginLeft, int marginRight, Contact... contacts) {
								for(Contact contact:contacts) {
												Point location = contact.getLocation();
												int width = contact.getComponent().getSize().width;
												if(location.x<width/2)
																graphics.drawLine(location.x, location.y, location.x+marginLeft, location.y);
												else graphics.drawLine(location.x-marginRight, location.y, location.x, location.y);
								}
				}

				@SuppressWarnings("unchecked") protected static <Type extends Contact> Type clone(Type contact) throws CloneNotSupportedException {
								return (Type)contact.clone();
				}
				@SuppressWarnings("unchecked") protected static <Type extends Contact> Type cloneForComponent(Type contact, Component component) throws CloneNotSupportedException {
								if(contact instanceof ComponentContact)
												return (Type)((ComponentContact)contact).cloneForComponent(component);
								else return (Type)contact.clone();
				}

				protected static <Type extends Contact> Type[] clone(Type[] contacts) throws CloneNotSupportedException {
								if(contacts==null) throw new CloneNotSupportedException();
								Class<?> type = contacts.getClass().getComponentType();
								@SuppressWarnings("unchecked") Type[] clone = (Type[])Array.newInstance(type, contacts.length);
								for(int index=0;index<contacts.length;index++)
												clone[index] = ContactUtilities.clone(contacts[index]);
								return clone;
				}
				protected static <Type extends Contact> Type[] cloneForComponent(Type[] contacts, Component component) throws CloneNotSupportedException {
								if(contacts==null) throw new CloneNotSupportedException();
								Class<?> type = contacts.getClass().getComponentType();
								@SuppressWarnings("unchecked") Type[] clone = (Type[])Array.newInstance(type, contacts.length);
								for(int index=0;index<contacts.length;index++)
												clone[index] = ContactUtilities.cloneForComponent(contacts[index], component);
								return clone;
				}

				protected static <Type extends Contact> ContactList<Type> clone(ContactList<Type> contacts) throws CloneNotSupportedException {
								return contacts.clone();
				}
				protected static <Type extends Contact> ContactList<Type> cloneForComponent(ContactList<Type> contacts, Component component) throws CloneNotSupportedException {
								return contacts.cloneForComponent(component);
				}
}
