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

import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.Map;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Guarded-Bit-Memory (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class GuardedBitMemory extends BitMemory {
	private static final long serialVersionUID = 1l;

	private static final String KEY;
	static { KEY = "component.memory."+GuardedBitMemory.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.memory", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected InputContact inputG;
	private Contact[] contacts;

	public GuardedBitMemory() {
		inputG = new InputContact(this, new Point(0, 22));
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL, inputG}, inputs.toArray(), outputs.toArray());
	}

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.drawString("Guarded", 20, size.height-35);
		graphics.drawString("G", 10, inputG.getLocation().y+4);
		ContactUtilities.paintSolderingJoints(graphics, inputG);
	}

	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(inputG.isCharged()) {
			for(OutputContact contact:outputs)
				contact.setCharged(false);
		} else super.calculate();
	}

	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		super.setConfiguration(configuration);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputL, inputG}, inputs.toArray(), outputs.toArray());
	}
}