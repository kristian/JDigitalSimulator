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
package lc.kra.jds.components.buildin.gate;

import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Abstract-Gate (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public abstract class AbstractGate extends Gate implements Configurable {
	private static final long serialVersionUID = 2l;

	protected ContactList<InputContact> inputs;
	protected OutputContact output;
	private Contact[] contacts;

	public AbstractGate() {
		inputs = new ContactList<InputContact>(this, InputContact.class, (Integer)getOptions()[0].getDefault());
		output = new OutputContact(this, new Point(size.width, size.height/2));
		output.setCharged(false);
		contacts = ContactUtilities.concatenateContacts(output, inputs.toArray());
	}

	@Override
	protected void checkSymbols() {
		if (currentlyUsesAnsiSymbols != Utilities.useAnsiSymbols()) {
			currentlyUsesAnsiSymbols = Utilities.useAnsiSymbols();
			recalcSize();
			inputs.setContactLocations();
			output.setLocation(new Point(this.size.width, this.size.height / 2));
			for (Contact contact : contacts) {
				for (Wire wire : contact.getWires()) {
					wire.revalidate();
				}
			}
		}
	}

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		Class<? extends AbstractGate> cls = this.getClass();
		if (cls.equals(NandGate.class) || cls.equals(NorGate.class) || cls.equals(XnorGate.class))
			ContactUtilities.paintSolderingJoint(graphics, 5, 3, output);
		else ContactUtilities.paintSolderingJoint(graphics, 5, 10, output);
		if (currentlyUsesAnsiSymbols) {
			if (cls.equals(OrGate.class) || cls.equals(NorGate.class) || cls.equals(XorGate.class) || cls.equals(XnorGate.class)) {
				inputs.paintSolderingJoints(graphics, 9, 10);
			} else {
				inputs.paintSolderingJoints(graphics, 5, 10);
			}
		} else {
			inputs.paintSolderingJoints(graphics, 5, 10);
		}
	}

	@Override public Contact[] getContacts() { return contacts; }
	@Override public abstract void calculate();

	@Override public Option[] getOptions() { return new Option[]{new Option("input_count", Utilities.getTranslation("contact.input.count"), OptionType.NUMBER, 2)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int inputCount = (Integer)configuration.get(getOptions()[0]);
		if(inputCount<2) throw new PropertyVetoException(Utilities.getTranslation("contact.input.minimum", 2), null);
		if(inputCount>8) throw new PropertyVetoException(Utilities.getTranslation("contact.input.maximum", 8), null);
		inputs.setContacts(inputCount); inputs.setContactLocations();
		contacts = ContactUtilities.concatenateContacts(output, inputs.toArray());
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], inputs.getContactsCount());
		return configuration;
	}
}