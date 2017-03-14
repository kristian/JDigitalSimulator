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
package lc.kra.jds.components.buildin.counter;

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
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Binary-Counter (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class BinaryCounter extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.counter."+BinaryCounter.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.counter", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected Dimension size;

	protected InputContact inputC, inputR;
	protected ContactList<OutputContact> outputs;
	private Contact[] contacts;

	protected boolean oldClock;

	public BinaryCounter() {
		size = new Dimension(75, 30+4*20);
		inputC = new InputContact(this, new Point(0, 6));
		inputR = new InputContact(this, new Point(0, 18));
		outputs = new ContactList<OutputContact>(this, OutputContact.class, 4);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputC, inputR}, outputs.toArray());
		this.setContactLocations(outputs);
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawPolyline(new int[]{9 , 9 , 5 , 5, size.width-5, size.width-5, size.width-9, size.width-9},
				new int[]{30, 25, 25, 0, 0           , 25          , 25          , 30          }, 8);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawString("Binary", 25, 22);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		graphics.drawString("C", 8, inputC.getLocation().y+4);
		graphics.drawString("R", 8, inputR.getLocation().y+4);
		for(int output=0;output<outputs.getContactsCount();output++) {
			int top = 30+output*20;
			graphics.drawRect(5, top, size.width-10, 20);
			graphics.drawString("+", size.width-12, top+13);
		}
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		int number = 0;
		if(!oldClock&&inputC.isCharged()) {
			for(int index=0;index<outputs.getContactsCount();index++)
				if(outputs.getContact(index).isCharged())
					number += Math.pow(2, index);
			number++;
			for(int index=outputs.getContactsCount()-1;index>=0;index--) {
				int pow = (int)Math.pow(2, index);
				OutputContact output = outputs.getContact(index);
				output.setCharged(number>=pow);
				if(output.isCharged())
					number -= pow;
			}
		}
		if(number!=0||inputR.isCharged())
			for(OutputContact output:outputs)
				output.setCharged(false);
		oldClock = inputC.isCharged();
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("output_count", Utilities.getTranslation("contact.output.count"), OptionType.NUMBER, 4)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int inputCount = (Integer)configuration.get(getOptions()[0]);
		if(inputCount<1) throw new PropertyVetoException(Utilities.getTranslation("contact.output.minimum", 1), null);
		if(inputCount>16) throw new PropertyVetoException(Utilities.getTranslation("contact.output.maximum", 16), null);
		outputs.setContacts(inputCount);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputC, inputR}, outputs.toArray());
		size = new Dimension(75, 30+outputs.getContactsCount()*20);
		setContactLocations(outputs);
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], outputs.getContactsCount());
		return configuration;
	}

	protected void setContactLocations(ContactList<?> list) {
		int x = (list.getContact(0) instanceof InputContact)?0:size.width;
		for(int output=0;output<list.getContactsCount();output++)
			list.getContact(output).setLocation(new Point(x, 30+output*20+(20/2)));
	}
}