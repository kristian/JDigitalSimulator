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
package lc.kra.jds.components.buildin.display;

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

/**
 * Display-Array (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class DisplayArray extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.display."+DisplayArray.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.display", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private ContactList<InputContact> inputs;
	private Contact[] contacts;

	public DisplayArray() {
		size = new Dimension(20, 2*8);
		inputs = new ContactList<InputContact>(this, InputContact.class, (Integer)getOptions()[0].getDefault());
		contacts = inputs.toArray();
	}

	@Override public void paint(Graphics graphics) {
		for(int input=0;input<inputs.getContactsCount();input++) {
			if(inputs.getContact(input).isCharged()) {
				graphics.setColor(Color.RED);
				graphics.fillRect(5, input*8, size.width-5, 8);
			}
			graphics.setColor(Color.BLACK);
			graphics.drawRect(5, input*8, size.width-5, 8);
		}
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() { } //nothing has to be calculated

	@Override public Option[] getOptions() { return new Option[]{new Option("input_count", Utilities.getTranslation("contact.input.count"), OptionType.NUMBER, 2)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int inputCount = (Integer)configuration.get(getOptions()[0]);
		if(inputCount<1 ) throw new PropertyVetoException(Utilities.getTranslation("contact.input.minimum", 1 ), null);
		if(inputCount>20) throw new PropertyVetoException(Utilities.getTranslation("contact.input.maximum", 20), null);
		inputs.setContacts(inputCount);
		contacts = inputs.toArray();
		size = new Dimension(20, inputs.getContactsCount()*8);
		inputs.setContactLocations();
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], inputs.getContactsCount());
		return configuration;
	}
}