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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lc.kra.jds.Utilities;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.contacts.Contact;

import java.util.Vector;

/**
 * Flip-Flop (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public abstract class FlipFlop extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 2l;

	protected Dimension size;

	protected boolean isMasterSlave = false;
	protected TiggerType tiggerType = null;

	public FlipFlop() {
		this.size = new Dimension(60, 56);
		if(getClass().isAnnotationPresent(Clocked.class)) {
			String configuration = Utilities.getConfiguration("flipflop.clock");
			if(configuration!=null)
				try { tiggerType = TiggerType.valueOf(configuration.toUpperCase()); }
			catch(IllegalArgumentException e) {}
			if(tiggerType==null)
				tiggerType = TiggerType.values()[0];
		}
		if(getClass().isAnnotationPresent(MasterSlave.class)) {
			String configuration = Utilities.getConfiguration("flipflop.masterslave");
			if(configuration!=null)
				isMasterSlave = configuration.toLowerCase().equals(Boolean.TRUE.toString());
		}
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(10, 0, size.width-20, size.height);
		Class<? extends FlipFlop> cls = this.getClass();
		if(cls.isAnnotationPresent(Clocked.class)) {
			if(tiggerType!=TiggerType.PULSE_TIGGERED) {
				graphics.drawLine(10, size.height/2-5, 20, size.height/2);
				graphics.drawLine(10, size.height/2+5, 20, size.height/2);
				if(tiggerType==TiggerType.NEGATIVE_EDGE_TIGGERED)
					graphics.drawOval(2, size.height/2-4, 8, 8);
			}
		}
		if(cls.isAnnotationPresent(MasterSlave.class)&&isMasterSlave) {
			Polygon polygon = new Polygon(new int[]{0, 8, 8},
					new int[]{0, 0, 8}, 3);
			polygon.translate(size.width-22, 5);
			graphics.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
			polygon.translate(0, size.height-17);
			graphics.drawPolyline(polygon.xpoints, polygon.ypoints, polygon.npoints);
		}
	}
	protected void paintDefaultContacts(Graphics graphics, String input) { paintDefaultContacts(graphics, input, null); }
	protected void paintDefaultContacts(Graphics graphics, String input_a, String input_b) {
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.BLACK);
		if(getClass().isAnnotationPresent(Clocked.class)) {
			int top = size.height / 4;
			if(input_a!=null) graphics.drawString(input_a, 15, top+metrics.getAscent()/2);
			if(input_b!=null) graphics.drawString(input_b, 15, top*3+metrics.getAscent()/2);
		} else {
			int top = size.height / 3;
			if(input_a!=null) graphics.drawString(input_a, 15, top+metrics.getAscent()/2);
			if(input_b!=null) graphics.drawString(input_b, 15, top*2+metrics.getAscent()/2);
		}
		graphics.drawOval(size.width-10, (size.height/3)*2-3, 6, 6);
	}

	@Override public Dimension getSize() { return size; }
	@Override public abstract Contact[] getContacts();
	@Override public abstract void calculate();

	@Override public Option[] getOptions() {
		List<Option> options = new Vector<Option>();
		Class<? extends FlipFlop> cls = this.getClass();
		if(cls.isAnnotationPresent(Clocked.class))
			options.add(new Option("tigger_type", Utilities.getTranslation("component.flipflop.tigger"), OptionType.LIST, TiggerType.values()));
		if(cls.isAnnotationPresent(MasterSlave.class))
			options.add(new Option("master_slave", Utilities.getTranslation("component.flipflop.masterslave"), OptionType.BOOLEAN, false));
		return options.toArray(new Option[0]);
	}
	private Option getOption(String key) {
		for(Option option:getOptions())
			if(option.getKey().equals(key))
				return option;
		return null;
	}
	@Override public void setConfiguration(Map<Option, Object> configuration) {
		for(Entry<Option, Object> entry:configuration.entrySet()) {
			String key = entry.getKey().getKey();
			if(key.equals("tigger_type" )) tiggerType = TiggerType.parseString(entry.getValue().toString());
			else if(key.equals("master_slave")) isMasterSlave = (Boolean)entry.getValue();
		}
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		Class<? extends FlipFlop> cls = this.getClass();
		if(cls.isAnnotationPresent(Clocked.class))
			configuration.put(getOption("tigger_type"), tiggerType);
		if(cls.isAnnotationPresent(MasterSlave.class))
			configuration.put(getOption("master_slave"), isMasterSlave);
		return configuration;
	}

	@Target(ElementType.TYPE) @Retention(RetentionPolicy.RUNTIME)
	protected @interface Clocked {}
	@Target(ElementType.TYPE)	@Retention(RetentionPolicy.RUNTIME)
	protected @interface MasterSlave {}

	protected enum TiggerType { POSITIVE_EDGE_TIGGERED, NEGATIVE_EDGE_TIGGERED, EDGE_TIGGERED, PULSE_TIGGERED;
		@Override
		public String toString() { return Utilities.getTranslation("component.flipflop.type."+super.toString().toLowerCase()); }
		public String getName() { return super.toString().toLowerCase(); }
		public static TiggerType parseString(String parse) {
			for(TiggerType type:values())
				if(type.toString().equals(parse))
					return type;
			return null;
		}
	}

	protected boolean isTiggered(boolean clock, boolean oldClock) {
		if(!getClass().isAnnotationPresent(Clocked.class)||tiggerType==null)
			return true; //if it is not clocked, simly calculate every-time
		switch(tiggerType) {
		case PULSE_TIGGERED: return clock;
		case EDGE_TIGGERED:  return clock!=oldClock;
		case NEGATIVE_EDGE_TIGGERED: return !clock&&oldClock;
		case POSITIVE_EDGE_TIGGERED: return clock&&!oldClock;
		default: return false; }
	}
}
