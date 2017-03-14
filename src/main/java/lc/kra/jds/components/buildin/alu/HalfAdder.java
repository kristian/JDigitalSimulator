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
package lc.kra.jds.components.buildin.alu;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Half-Adder (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class HalfAdder extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.alu."+HalfAdder.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.alu", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private InputContact input_a, input_b;
	private OutputContact output_s, output_c;
	private Contact[] contacts;

	public HalfAdder() {
		size = new Dimension(50, 50);
		int top = size.height/3;
		input_a = new InputContact(this, new Point(0, top));
		input_b = new InputContact(this, new Point(0, top*2));
		output_s = new OutputContact(this, new Point(size.width, top));
		output_c = new OutputContact(this, new Point(size.width, top*2));
		contacts = new Contact[] {input_a, input_b, output_s, output_c};
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		int top = size.height/4, height=graphics.getFontMetrics().getAscent()/2;
		graphics.drawRect(5, 0, size.width-10, size.height);
		graphics.drawString("A", 10, top+height);
		graphics.drawString("B", 10, top*3+height);
		graphics.drawString("S", size.width-15, top+height);
		graphics.drawString("C", size.width-15, top*3+height);
		graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 15f));
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.drawString("HA", size.width/2-metrics.stringWidth("HA")/2,
				size.height/2+metrics.getAscent()/3);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		output_s.setCharged(input_a.isCharged()^input_b.isCharged());
		output_c.setCharged(input_a.isCharged()&input_b.isCharged());
	}
}