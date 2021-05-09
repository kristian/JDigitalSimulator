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

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Graphics;

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.contacts.Contact;

/**
 * XOR-Gate (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class XorGate extends AbstractGate {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.gate."+XorGate.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.gate", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		if (useAnsiSymbols) {
			graphics.drawArc(5 - (size.height / 3) / 2, 0, size.height / 3, size.height, 90, -180);
			graphics.drawArc(1 - (size.height / 3) / 2, 0, size.height / 3, size.height, 90, -180);
			graphics.drawArc(5 - ((int) (size.width * 1.6)) / 2, 0, (int) (size.width * 1.6), (int) (size.height * 1.7), 90, -65);
			graphics.drawArc(5 - ((int) (size.width * 1.6)) / 2, size.height - ((int) (size.height * 1.7)), (int) (size.width * 1.6), (int) (size.height * 1.7), -90, 65);
		} else {
			paintLabel(graphics, "=1");
		}
	}

	@Override public void calculate() {
		int count = 0;
		for(Contact input:inputs)
			if(input.isCharged())
				count++;
		output.setCharged(Utilities.isOdd(count));
	}
}
