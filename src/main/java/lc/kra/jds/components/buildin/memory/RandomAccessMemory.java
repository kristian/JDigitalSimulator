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
package lc.kra.jds.components.buildin.memory;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
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
 * Random-Access-Memory (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class RandomAccessMemory extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 1l;

	private static final String KEY;
	static { KEY = "component.memory."+RandomAccessMemory.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.memory", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected Dimension size;

	private InputContact inputCS, inputRW;
	private ContactList<InputContact> inputsA, inputsD;
	private ContactList<OutputContact> outputsD;
	private Contact[] contacts;

	private int addressCount, wordSize;
	private transient boolean memory[];

	public RandomAccessMemory() {
		size = new Dimension(80, 155);
		memory = new boolean[memorySize(addressCount=(Integer)getOptions()[0].getDefault(), wordSize=(Integer)getOptions()[1].getDefault())];

		inputCS = new InputContact(this, new Point(0, 10));
		inputRW = new InputContact(this, new Point(0, 22));
		inputsA = new ContactList<InputContact>(this, InputContact.class, addressCount);
		inputsD = new ContactList<InputContact>(this, InputContact.class, wordSize);
		outputsD = new ContactList<OutputContact>(this, OutputContact.class, wordSize);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputCS, inputRW}, inputsA.toArray(), inputsD.toArray(), outputsD.toArray());

		setContactLocations();
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.drawRect(5, 0, size.width-10, size.height);
		String text = memory.length+"-Bit";
		graphics.drawString(text, size.width/2-graphics.getFontMetrics().stringWidth(text)/2, size.height-45);
		graphics.drawString("Random", 16, size.height-35);
		graphics.drawString("Access", 20, size.height-25);
		graphics.drawString("Memory", 18, size.height-15);
		graphics.drawString("CS", 10, inputCS.getLocation().y+4);
		graphics.drawString("R/W", 10, inputRW.getLocation().y+4);
		graphics.drawString("A", 10, 67);
		ContactUtilities.paintSolderingJoints(graphics, contacts);

		graphics.drawRect(25, 45, 33, 33);
		for(int index=0;index<memory.length;index++) {
			int x = 26+(index%32), y = 46+(index/32);
			graphics.setColor(memory[index]?Color.RED:Color.WHITE);
			graphics.drawLine(x, y, x, y);
		}
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(!inputCS.isCharged()) return;
		int address = 0;
		for(int index=0;index<inputsA.getContactsCount();index++)
			if(inputsA.getContact(index).isCharged())
				address += Math.pow(2, index);
		address *= wordSize;
		if(inputRW.isCharged())
			for(int offset=0;address+offset<memory.length&&offset<wordSize;offset++)
				memory[address+offset] = inputsD.getContact(offset).isCharged();
		else
			for(int offset=0;address+offset<memory.length&&offset<wordSize;offset++)
				outputsD.getContact(offset).setCharged(memory[address+offset]);
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("address_count", Utilities.getTranslation("component.memory.address.count"), OptionType.NUMBER, 4), new Option("word_size", Utilities.getTranslation("component.memory.word.size"), OptionType.NUMBER, 4)}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		int addressCount = (Integer)configuration.get(getOptions()[0]);
		if(addressCount<0) throw new PropertyVetoException(Utilities.getTranslation("component.memory.address.minimum", 0), null);
		if(addressCount>10) throw new PropertyVetoException(Utilities.getTranslation("component.memory.address.maximum", 10), null);

		int wordSize = (Integer)configuration.get(getOptions()[1]);
		if(wordSize<1) throw new PropertyVetoException(Utilities.getTranslation("component.memory.word.size.minimum", 1), null);
		if(wordSize>8) throw new PropertyVetoException(Utilities.getTranslation("component.memory.word.size.maximum", 8), null);

		int memorySize = memorySize(addressCount, wordSize);
		if(memorySize>1024) throw new PropertyVetoException(Utilities.getTranslation("component.memory.size.overflow", 1024, memorySize), null);

		this.addressCount = addressCount;
		this.wordSize = wordSize;
		memory = new boolean[memorySize];

		inputsA.setContacts(addressCount);
		inputsD.setContacts(wordSize);
		outputsD.setContacts(wordSize);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputCS, inputRW}, inputsA.toArray(), inputsD.toArray(), outputsD.toArray());

		setContactLocations();
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], addressCount);
		configuration.put(getOptions()[1], wordSize);
		return configuration;
	}

	protected void setContactLocations() {
		int top = 30;
		float interval = 70.f/(inputsA.getContactsCount()+1); top += interval;
		for(int index=0;index<inputsA.getContactsCount();index++) {
			inputsA.getContact(index).setLocation(new Point(0, top));
			top += interval;
		}

		top = 95;
		interval = 60.f/(inputsD.getContactsCount()+1); top += interval;
		for(int index=0;index<inputsD.getContactsCount();index++) {
			inputsD.getContact(index).setLocation(new Point(0, top));
			outputsD.getContact(index).setLocation(new Point(size.width, top));
			top += interval;
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		memory = new boolean[memorySize(addressCount, wordSize)];
	}

	private static final int memorySize(int addressCount, int wordSize) {
		if(addressCount<=0)
			return wordSize;
		return (int)Math.pow(2, addressCount)*wordSize;
	}
}