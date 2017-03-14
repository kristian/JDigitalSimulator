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
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.OutputContact;

/**
 * Clock (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Clock extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.switches."+Clock.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.switches", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private OutputContact output;

	private transient int clock;
	private int high = 100;
	private int low = 100;
	private Contact[] contacts;

	public Clock() {
		size = new Dimension(35, 30);
		output = new OutputContact(this, new Point(size.width, size.height/2));
		contacts = new Contact[]{output};
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, size.width-5, size.height);
		int x=size.width-28, yt=size.height/2-2, yb=size.height/2;
		graphics.drawPolyline(new int[]{x , x+=2, x , x+=3, x , x+=3, x , x+=2, x , x+=2, x , x+=2},
				new int[]{yt, yt  , yb, yb  , yt, yt  , yb, yb  , yt, yt  , yb, yb  }, 12);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts;	}
	@Override public void calculate() { //the calculate method is called every hunreth of a second
		boolean charged = output.isCharged();
		if((charged&&high<=++clock) //note: clock gets only incremented once
				||(!charged&&low<=++clock)) {
			output.setCharged(!charged);
			clock = 0;
		}
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("low" , Utilities.getTranslation("component.clock.low" ), OptionType.NUMBER, 100),
			new Option("high", Utilities.getTranslation("component.clock.high"), OptionType.NUMBER, 100)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int low = (Integer)configuration.get(getOptions()[0]),
				high = (Integer)configuration.get(getOptions()[1]);
		if(low<1||high<1) throw new PropertyVetoException(Utilities.getTranslation("component.clock.minimum"), null);
		this.low = low; this.high = high;
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], low);
		configuration.put(getOptions()[1], high);
		return configuration;
	}
}
