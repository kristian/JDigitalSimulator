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
package lc.kra.jds.components.buildin.register;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Register (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public abstract class Register extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 1l;

	private transient Shape shape;

	protected Dimension size;

	protected ContactList<OutputContact> outputs;

	public Register() {
		size = new Dimension(75, 40+4*20);
		makeShape();

		outputs = new ContactList<OutputContact>(this, OutputContact.class, 4);

		this.setContactLocations(outputs);
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		((Graphics2D)graphics).draw(shape);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawString("Register", 20, 30);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		for(int output=0;output<outputs.getContactsCount();output++) {
			int top = 40+output*20;
			graphics.drawRect(5, top, size.width-10, 20);
			graphics.drawString("+", size.width-12, top+13);
		}
	}

	@Override public Dimension getSize() { return size; }
	@Override public abstract Contact[] getContacts();
	@Override public abstract void calculate();

	@Override public Option[] getOptions() { return new Option[]{new Option("output_count", Utilities.getTranslation("contact.output.count"), OptionType.NUMBER, 4)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int outputCount = (Integer)configuration.get(getOptions()[0]);
		if(outputCount<1) throw new PropertyVetoException(Utilities.getTranslation("contact.output.minimum", 1), null);
		if(outputCount>16) throw new PropertyVetoException(Utilities.getTranslation("contact.output.maximum", 16), null);
		outputs.setContacts(outputCount);
		size = new Dimension(75, 40+outputs.getContactsCount()*20);
		makeShape();
		setContactLocations(outputs);
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], outputs.getContactsCount());
		return configuration;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		makeShape();
	}

	private void makeShape() {
		shape = new Polygon(new int[]{9 , 9 , 5 , 5, size.width-5, size.width-5, size.width-9, size.width-9},
				new int[]{40, 35, 35, 0, 0           , 35          , 35          , 40          }, 8);
	}

	protected void setContactLocations(ContactList<?> list) {
		int x = (list.getContact(0) instanceof InputContact)?0:size.width;
		for(int output=0;output<list.getContactsCount();output++)
			list.getContact(output).setLocation(new Point(x, 40+output*20+(20/2)));
	}
}