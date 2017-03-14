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

import lc.kra.jds.Utilities.TranslationType;

/**
 * XNOR-Gate (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class XnorGate extends XorGate {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.gate."+XnorGate.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.gate", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		paintNot(graphics);
	}

	@Override public void calculate() {
		super.calculate();
		output.setCharged(!output.isCharged());
	}
}
