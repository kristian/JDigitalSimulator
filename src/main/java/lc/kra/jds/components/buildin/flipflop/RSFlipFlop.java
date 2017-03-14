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
package lc.kra.jds.components.buildin.flipflop;

import static lc.kra.jds.Utilities.*;

import java.awt.Graphics;
import java.awt.Point;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.buildin.flipflop.FlipFlop.Clocked;
import lc.kra.jds.components.buildin.flipflop.FlipFlop.MasterSlave;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;

/**
 * RS-Flip-Flop (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
@Clocked @MasterSlave
public class RSFlipFlop extends SimpleRSFlipFlop {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.flipflop."+RSFlipFlop.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.flipflop", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private InputContact inputC;
	private Contact[] contacts;

	transient private boolean oldR, oldS, oldClock;

	public RSFlipFlop() {
		int top = getSize().height / 4;
		inputR.setLocation(new Point(0, top));
		inputC = new InputContact(this, new Point(0, top*2));
		inputS.setLocation(new Point(0, top*3));
		contacts = new Contact[]{inputR, inputC, inputS, outputQ, outputQi};
	}

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		ContactUtilities.paintSolderingJoint(graphics, 10, 10, inputC);
	}
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(isMasterSlave) {
			if(inputC.isCharged()&&!oldClock) { //positive edge
				oldR = inputR.isCharged();
				oldS = inputS.isCharged();
			}
		} else { oldR = inputR.isCharged(); oldS = inputS.isCharged(); }
		if(isTiggered(inputC.isCharged(), oldClock)) {
			if(oldS&&!oldR) outputQ.setCharged(true ); //set
			else if(oldR&&!oldS) outputQ.setCharged(false); //reset
			else if(oldR&& oldS) outputQ.setCharged(false); //undefined
			outputQi.setCharged(!outputQ.isCharged());
		}
		oldClock = inputC.isCharged();
	}
}
