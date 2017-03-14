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

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.filechooser.FileNameExtensionFilter;

import lc.kra.jds.Simulation;
import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;
import lc.kra.jds.exceptions.PasswordRequiredException;
import lc.kra.jds.gui.Application;

/**
 * External Simulation (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class ExternalSimulation extends Component implements Sociable, Configurable {
	private static final long serialVersionUID = 1l;

	private static final String KEY;
	static { KEY = "component.external."+ExternalSimulation.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.external", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private Dimension size;

	private File file;
	private transient Simulation simulation;

	private transient Map<Contact, Connector> connectors;
	private ContactList<InputContact> inputs;
	private ContactList<OutputContact> outputs;
	private Contact[] contacts;

	private transient int repaintDelay = 0;

	public ExternalSimulation() {
		size = new Dimension(150, 100);
		inputs = new ContactList<InputContact>(this, InputContact.class);
		outputs = new ContactList<OutputContact>(this, OutputContact.class);
		contacts = new Contact[0];
	}

	@Override public void paint(Graphics graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		graphics.setColor(Color.BLACK);
		graphics.drawRect(5, 0, size.width-10, size.height);
		String name = Utilities.getTranslation("component.simulation.file.unknown");
		if(file!=null) {
			name = file.getName();
			if(name.endsWith(".jdsim"))
				name = name.substring(0, name.length()-".jdsim".length());
			if(simulation==null)
				graphics.setColor(Color.RED);
		} else graphics.setColor(Color.GRAY);
		graphics.drawString(Utilities.cropString(name, 20), 5+(size.width-5)/2-metrics.stringWidth(name)/2,
				(size.height/2+metrics.getAscent()/2)-1);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		metrics = graphics.getFontMetrics();
		int height = metrics.getAscent()/3;
		for(InputContact input:inputs) {
			Connector connector = connectors.get(input);
			if(connector!=null)
				graphics.drawString(connector.getName(), 10, input.getLocation().y+height);
		}
		for(OutputContact output:outputs) {
			Connector connector = connectors.get(output);
			if(connector!=null) {
				name = connector.getName();
				graphics.drawString(name, size.width-metrics.stringWidth(name)-10, output.getLocation().y+height);
			}
		}
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if(file==null) return;
		if(!file.exists()) file = new File(Utilities.getFile(Utilities.getLocalPath()).getPath()+File.separatorChar+file.getName());
		if(!file.exists()&&Application.currentDirectory!=null)
			this.file = new File(Application.currentDirectory.getPath()+File.separatorChar+file.getName());
		loadSimulation(this.file);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		if(simulation==null)
			return;
		for(InputContact input:inputs) {
			Connector connector = connectors.get(input);
			if(connector instanceof InboundConnector)
				((InboundConnector)connector).setCharged(input.isCharged());
		}
		simulation.doSimulationStep();
		for(OutputContact output:outputs) {
			Connector connector = connectors.get(output);
			if(connector instanceof OutboundConnector)
				output.setCharged(((OutboundConnector)connector).isCharged());
		}
		if(++repaintDelay>=10) {
			repaintDelay = 0;
			simulation.repaint();
		}
	}

	@Override public Option[] getOptions() { return new Option[]{new Option("file", Utilities.getTranslation("component.simulation.file"), OptionType.FILE, new FileNameExtensionFilter(getTranslation("persist.filter.description"), Application.FILE_EXTENSION))}; }
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		loadSimulation(file = (File)configuration.get(getOptions()[0]));
	}
	@Override public Map<Option, Object> getConfiguration() {
		Map<Option, Object> configuration = new HashMap<Option, Object>();
		configuration.put(getOptions()[0], file);
		return configuration;
	}

	public Simulation getSimulation() { return this.simulation; }
	public File getFile() { return this.file; }

	private void loadSimulation(File file) {
		if(file==null||!file.exists()||!file.isFile()||!file.canRead())
			return;
		try {
			int inputs=0, outputs=0;
			//important use of LINKEDHashMap, because sort ordering is required because keySet method is used for getContacts
			connectors = new LinkedHashMap<Contact, Connector>();
			simulation = Simulation.readSimulation(new BufferedInputStream(new FileInputStream(file)));
			for(Component component:simulation.getAllComponents())
				if(component instanceof InboundConnector) {
					inputs++;
					if(this.inputs.getContactsCount()<inputs)
						this.inputs.setContacts(inputs);
					connectors.put(this.inputs.getContact(inputs-1), (Connector)component);
				} else if(component instanceof OutboundConnector) {
					outputs++;
					if(this.outputs.getContactsCount()<outputs)
						this.outputs.setContacts(outputs);
					connectors.put(this.outputs.getContact(outputs-1), (Connector)component);
				}
			this.inputs.setContacts(inputs);
			this.inputs.setContactLocations(new Point(0, 0));
			this.outputs.setContacts(outputs);
			this.outputs.setContactLocations(new Point(size.width, 0));
			contacts = connectors.keySet().toArray(contacts);
		} catch (PasswordRequiredException e) {
			//TODO
		}	catch(Exception e) {
			e.printStackTrace();
		}
	}
}