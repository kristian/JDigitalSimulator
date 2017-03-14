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
package lc.kra.jds.components.buildin.general;

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Component.HiddenComponent;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Junction (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
@HiddenComponent public class Junction extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.general."+Junction.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.general", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	public final InputContact input;
	public final OutputContact output;
	private Contact[] contacts;

	public Junction(Point location) { this(); setLocation(location); }
	public Junction() {
		size = new Dimension(20, 20);
		input = new InputContact(this, new Point(size.width/2, size.height/2));
		output = new OutputContact(this, new Point(size.width/2, size.height/2)) {
			private static final long serialVersionUID = 1l;
			@Override public float getVoltage() { return input.getVoltage(); }
		};
		contacts = new Contact[]{input, output};
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillOval(6, 6, size.width-12, size.height-12);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {} //no calculation requried, special beaviour of outbound contact
}