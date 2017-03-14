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

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
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
import lc.kra.jds.contacts.InputContact;

/**
 * Voltmeter (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Voltmeter extends Component implements Sociable, Configurable, Comparable<Voltmeter> {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.display."+Voltmeter.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.display", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private static int count;

	private Dimension size;

	private int number;
	private String name;
	private float voltage;

	private InputContact input;
	private Contact[] contacts;

	public Voltmeter(Point location) { this(); this.setLocation(location); }
	public Voltmeter() {
		size = new Dimension(25, 20);

		number = Voltmeter.count++;
		name = getOptions()[0].getDefault().toString();

		input = new InputContact(this, new Point(0, size.height/2));
		contacts = new Contact[]{input};
	}

	@Override public void paint(Graphics graphics) {
		graphics.setFont(graphics.getFont().deriveFont(8f));
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.BLACK);
		graphics.drawOval(5, 0, size.height, size.height);
		graphics.drawString(Utilities.cropString(name, 3), 5+(size.width-5)/2-metrics.stringWidth(name)/2,
				(size.height/2+metrics.getAscent()/2)-1);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() { } //nothing has to be calculated

	@Override public Option[] getOptions() { return new Option[]{new Option("name", Utilities.getTranslation("component.voltmeter.name"), OptionType.TEXT, Utilities.getTranslation("component.voltmeter.name.default", number))}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		String name = configuration.get(getOptions()[0]).toString();
		if(name.isEmpty()) throw new PropertyVetoException(Utilities.getTranslation("component.voltmeter.name.error"), null);
		this.name = name;
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], name);
		return configuration;
	}

	@Override public int compareTo(Voltmeter voltmeter) {
		int compare = this.getName().compareTo(voltmeter.getName());
		if(compare!=0)
			return compare;
		return new Integer(hashCode()).compareTo(voltmeter.hashCode());
	}

	public float getVoltage() { return this.voltage; }
	public float gaugeVoltage() { return (this.voltage=this.input.getVoltage()); }
	public String getName() { return this.name; }
}