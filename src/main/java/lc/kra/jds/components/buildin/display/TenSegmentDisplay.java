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
package lc.kra.jds.components.buildin.display;

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;

/**
 * Ten segment display (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class TenSegmentDisplay extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.display."+TenSegmentDisplay.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.display", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private static final int POLYGONS_X[][] = {
			{ 4, 8, 32, 36, 32, 8, 4},
			{36, 40, 40, 36, 32, 32, 36},
			{36, 40, 40, 36, 32, 32, 36},
			{ 4, 8, 32, 36, 32, 8, 4},
			{ 4, 8, 8, 4, 0, 0, 4},
			{ 4, 8, 8, 4, 0, 0, 4},
			{ 4, 8, 16, 20, 16, 8, 4},
			{20, 24, 32, 36, 32, 24, 20},
			{20, 23, 23, 20, 17, 17, 20},
			{20, 23, 23, 20, 17, 17, 20},
	};
	private static final int POLYGONS_Y[][] = {
			{ 4, 0, 0, 4, 8, 8, 4},
			{ 4, 8, 32, 36, 32, 8, 4},
			{36, 40, 64, 68, 64, 40, 36},
			{68, 64, 64, 68, 72, 72, 68},
			{36, 40, 64, 68, 64, 40, 36},
			{ 4, 8, 32, 36, 32, 8, 4},
			{36, 32, 32, 36, 40, 40, 36},
			{36, 32, 32, 36, 40, 40, 36},
			{ 9, 12, 31, 34, 31, 12, 9},
			{38, 41, 60, 63, 60, 41, 38},
	};

	private Dimension size;

	private InputContact[] inputs;

	public TenSegmentDisplay() {
		size = new Dimension(60, 80);
		inputs = new InputContact[10];
		for(int input=0;input<inputs.length;input++)
			inputs[input] = new InputContact(this);
		ContactList.setContactLocations(this, inputs);
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(10, 0, size.width-10, size.height);
		for(int input=0;input<inputs.length;input++) {
			Polygon polygon = new Polygon(POLYGONS_X[input], POLYGONS_Y[input], 7);
			polygon.translate(15, 4);
			if(inputs[input].isCharged()) {
				graphics.setColor(Color.RED);
				graphics.fillPolygon(polygon);
			}
			graphics.setColor(Color.BLACK);
			graphics.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}
		ContactUtilities.paintSolderingJoints(graphics, 10, 0, inputs);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return inputs; }
	@Override public void calculate() { } //nothing has to be calculated
}