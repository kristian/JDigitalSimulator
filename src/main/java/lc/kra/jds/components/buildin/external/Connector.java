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
package lc.kra.jds.components.buildin.external;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;

/**
 * Connector (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public abstract class Connector extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 1l;

	protected Dimension size;

	private String name;

	public Connector() {
		size = new Dimension(75, 30);
		name = getOptions()[0].getDefault().toString();
	}

	@Override public void paint(Graphics graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.BLACK);
		graphics.drawRect(5, 0, size.width-10, size.height);
		graphics.drawString(name, 5+(size.width-5)/2-metrics.stringWidth(name)/2,
				(size.height/2+metrics.getAscent()/2)-1);
	}

	@Override public Dimension getSize() { return size; }
	@Override public abstract Contact[] getContacts();
	@Override public void calculate() { } //the connector does only provide some contacts to elsewhere

	@Override public Option[] getOptions() { return new Option[]{new Option("name", Utilities.getTranslation("component.connector.name"), OptionType.TEXT, Utilities.getTranslation("component.connector.name.default"))}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		String name = configuration.get(getOptions()[0]).toString();
		if(name.isEmpty()) throw new PropertyVetoException(Utilities.getTranslation("component.connector.name.error"), null);
		this.name = name;
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], name);
		return configuration;
	}

	public String getName() { return this.name; }
}