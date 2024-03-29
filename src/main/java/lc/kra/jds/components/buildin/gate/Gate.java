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

import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Gate (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public abstract class Gate extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final Dimension ISO_SIZE = new Dimension(50, 48), ANSI_SIZE = new Dimension(55, 35);

	protected Dimension size;

	public Gate() {
		checkSymbolStandard();
	}

	protected void changeSymbolStandard() {
		if (useAnsiSymbols) {
			size = ANSI_SIZE;
		} else {
			size = ISO_SIZE;
		}
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		if (!useAnsiSymbols) {
			graphics.drawRect(5, 0, size.width-15, size.height);
		}
	}
	protected void paintLabel(Graphics graphics, String label) { graphics.drawString(label, 5+(size.width-15)/2-graphics.getFontMetrics().stringWidth(label)/2, 15); }
	protected void paintNot(Graphics graphics) { graphics.drawOval(size.width-10, size.height/2-3, 6, 6); }

	@Override public abstract Contact[] getContacts();
	@Override public Dimension getSize() { return size; }
	@Override public abstract void calculate();
}
