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
package lc.kra.jds.components.buildin.memory;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Bit-Memory (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class BitMemory extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 1l;

	private static final String KEY;
	static { KEY = "component.memory."+BitMemory.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.memory", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected Dimension size;

	protected InputContact inputL;
	protected ContactList<InputContact> inputs;
	protected ContactList<OutputContact> outputs;
	private Contact[] contacts;

	public BitMemory() {
		size = new Dimension(80, 60);
		if(this instanceof GuardedBitMemory)
			size.height += 15;

		int number = (Integer)getOptions()[0].getDefault(), top = 15+((this instanceof GuardedBitMemory)?15:0);
		inputL = new InputContact(this, new Point(0, 10));
		inputs = new ContactList<InputContact>(this, InputContact.class, number); inputs.setContactLocations(new Point(0, top));
		outputs = new ContactList<OutputContact>(this, OutputContact.class, number); outputs.setContactLocations(new Point(size.width, top));
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL}, inputs.toArray(), outputs.toArray());
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(5, 0, size.width-10, size.height);
		String text = inputs.getContactsCount()+"-Bit";
		graphics.drawString(text, size.width/2-graphics.getFontMetrics().stringWidth(text)/2, size.height-25);
		graphics.drawString("Memory", 20, size.height-15);
		graphics.drawString("L", 10, inputL.getLocation().y+4);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(inputL.isCharged())
			for(int index=0;index<inputs.getContactsCount();index++)
				outputs.getContact(index).setCharged(inputs.getContact(index).isCharged());
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("input_count", Utilities.getTranslation("contact.count"), OptionType.NUMBER, 1)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int count = (Integer)configuration.get(getOptions()[0]);
		if(count<1) throw new PropertyVetoException(Utilities.getTranslation("contact.minimum", 1), null);
		if(count>8) throw new PropertyVetoException(Utilities.getTranslation("contact.maximum", 8), null);
		int top = 15+((this instanceof GuardedBitMemory)?15:0);
		inputs.setContacts(count); inputs.setContactLocations(new Point(0, top));
		outputs.setContacts(count); outputs.setContactLocations(new Point(size.width, top));
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL}, inputs.toArray(), outputs.toArray());
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], inputs.getContactsCount());
		return configuration;
	}
}