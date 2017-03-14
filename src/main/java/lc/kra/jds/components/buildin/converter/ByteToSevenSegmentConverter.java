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
package lc.kra.jds.components.buildin.converter;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Byte-to-Seven-Segment-Converter (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class ByteToSevenSegmentConverter extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.converter."+ByteToSevenSegmentConverter.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.converter", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private static final boolean DIGITS[][] = {
			{true , true , true , true , true , true , false},
			{false, true , true , false, false, false, false},
			{true , true , false, true , true , false, true },
			{true , true , true , true , false, false, true },
			{false, true , true , false, false, true , true },
			{true , false, true , true , false, true , true },
			{true , false, true , true , true , true , true },
			{true , true , true , false, false, false, false},
			{true , true , true , true , true , true , true },
			{true , true , true , true , false, true , true },
			{true , true , true , false, true , true , true },
			{false, false, true , true , true , true , true },
			{true , false, false, true , true , true , false},
			{false, true , true , true , true , false, true },
			{true , false, false, true , true , true , true },
			{true , false, false, false, true , true , true }
	};

	private Dimension size;

	private InputContact[] inputs;
	private OutputContact[] outputs;
	private Contact[] contacts;

	public ByteToSevenSegmentConverter() {
		size = new Dimension(75, 85);
		inputs = new InputContact[4];
		for(int input=0;input<inputs.length;input++)
			inputs[input] = new InputContact(this);
		ContactList.setContactLocations(this, inputs);
		outputs = new OutputContact[7];
		for(int output=0;output<outputs.length;output++)
			outputs[output] = new OutputContact(this);
		contacts = ContactUtilities.concatenateContacts(inputs, outputs);
		ContactList.setContactLocations(this, outputs, new Point(size.width, 0));
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(5, 0, size.width-10, size.height);
		graphics.setFont(graphics.getFont().deriveFont(14f));
		graphics.drawString("Byte", 10, 20);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawLine(15, 35, size.width-15, 20);
		graphics.drawString("7-Segment", 15, 50);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		int number = 0;
		for(int input=0;input<inputs.length;input++)
			if(inputs[input].isCharged())
				number += Math.pow(2, input);
		for(int output=0;output<outputs.length;output++)
			outputs[output].setCharged(DIGITS[number][output]);
	}
}