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
package lc.kra.jds.components.buildin.switches;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.OutputContact;

/**
 * Ground (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Gnd extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.switches."+Gnd.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.switches", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private OutputContact output;
	private Contact[] contacts;

	public Gnd() {
		size = new Dimension(40, 20);
		output = new OutputContact(this, new Point(size.width, size.height/2));
		output.setCharged(false);
		contacts = new Contact[] {output};
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, size.width-5, size.height);
		graphics.drawString("GND", 5, 15);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts;	}
	@Override public void calculate() { } //the output is always charged
}