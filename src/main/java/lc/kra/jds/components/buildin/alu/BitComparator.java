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
package lc.kra.jds.components.buildin.alu;

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
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Bit-Comparator (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class BitComparator extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.alu."+BitComparator.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.alu", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private ContactList<InputContact> inputs;
	private OutputContact[] outputs;
	private Contact[] contacts;

	public BitComparator() {
		size = new Dimension(75, 20+8*10);
		inputs = new ContactList<InputContact>(this, InputContact.class, (Integer)getOptions()[0].getDefault()*2);
		outputs = new OutputContact[] {
				new OutputContact(this), //equal
				new OutputContact(this), //not-equal
				new OutputContact(this), //greater
				new OutputContact(this), //greater-equal
				new OutputContact(this), //lower
				new OutputContact(this)  //lower-equal
		};
		contacts = ContactUtilities.concatenateContacts(inputs.toArray(), outputs);
		setContactLocations();
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		FontMetrics metrics = graphics.getFontMetrics();
		int top = size.height/7, half = inputs.getContactsCount()/2, height = metrics.getAscent()/2;
		graphics.drawRect(5, 0, size.width-10, size.height);
		graphics.drawString("A=B", size.width-metrics.stringWidth("A=B")-10, top+height);
		graphics.drawString("A\u2260B", size.width-metrics.stringWidth("A\u2260B")-10, top*2+height);
		graphics.drawString("A>B", size.width-metrics.stringWidth("A>B")-10, top*3+height);
		graphics.drawString("A\u2265B", size.width-metrics.stringWidth("A\u2265B")-10, top*4+height);
		graphics.drawString("A<B", size.width-metrics.stringWidth("A<B")-10, top*5+height);
		graphics.drawString("A\u2264B", size.width-metrics.stringWidth("A\u2264B")-10, top*6+height);
		top = size.height/(inputs.getContactsCount()+1);
		for(int input=0;input<inputs.getContactsCount();input++)
			if(input<half)
				graphics.drawString("A"+input, 10, (8+input*10)+height);
			else graphics.drawString("B"+(input-half), 10, (8+(input+1)*10)+height);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		int numberA = 0, numberB = 0, half = inputs.getContactsCount()/2;
		for(int input=0;input<inputs.getContactsCount();input++)
			if(inputs.getContact(input).isCharged())
				if(input<half)
					numberA += Math.pow(2, input);
				else numberB += Math.pow(2, input-half);
		outputs[0].setCharged(numberA==numberB);
		outputs[1].setCharged(numberA!=numberB);
		outputs[2].setCharged(numberA> numberB);
		outputs[3].setCharged(numberA>=numberB);
		outputs[4].setCharged(numberA< numberB);
		outputs[5].setCharged(numberA<=numberB);
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("input_count", Utilities.getTranslation("contact.input.count"), OptionType.NUMBER, 4)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int inputCount = (Integer)configuration.get(getOptions()[0]);
		if(inputCount<2) throw new PropertyVetoException(Utilities.getTranslation("contact.input.minimum", 1), null);
		if(inputCount>8) throw new PropertyVetoException(Utilities.getTranslation("contact.input.maximum", 8), null);
		inputs.setContacts(inputCount*2);
		size = new Dimension(75, 20+inputs.getContactsCount()*10);
		contacts = ContactUtilities.concatenateContacts(inputs.toArray(), outputs);
		setContactLocations();
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], inputs.getContactsCount()/2);
		return configuration;
	}

	protected void setContactLocations() {
		int half = inputs.getContactsCount()/2;
		for(int input=0;input<inputs.getContactsCount();input++)
			inputs.getContact(input).setLocation(new Point(0, input<half?(10+input*10):(10+(input+1)*10)));
		ContactList.setContactLocations(this, outputs, new Point(size.width, 0));
	}
}