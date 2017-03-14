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
package lc.kra.jds.components.buildin.counter;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.Map;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;

/**
 * Loadable-Binary-Counter (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class LoadableBinaryCounter extends BinaryCounter {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.counter."+LoadableBinaryCounter.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.counter", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected InputContact inputL;
	protected ContactList<InputContact> inputs;
	private Contact[] contacts;

	public LoadableBinaryCounter() {
		inputL = new InputContact(this, new Point(0, 4));
		inputC.setLocation(new Point(0, 12));
		inputR.setLocation(new Point(0, 20));
		inputs = new ContactList<InputContact>(this, InputContact.class, 4);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL, inputC, inputR}, inputs.toArray(), outputs.toArray());
		this.setContactLocations(inputs);
	}

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.setColor(Color.BLACK);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawString("Loadable", 20, 12);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		graphics.drawString("L", 8, inputL.getLocation().y+4);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(inputL.isCharged()) {
			for(int input=0;input<inputs.getContactsCount();input++)
				outputs.getContact(input).setCharged(inputs.getContact(input).isCharged());
		} else super.calculate();
	}

	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		super.setConfiguration(configuration);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL, inputC, inputR}, inputs.toArray(), outputs.toArray());
	}
}