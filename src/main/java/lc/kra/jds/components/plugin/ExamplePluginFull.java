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
package lc.kra.jds.components.plugin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.components.Component;
import lc.kra.jds.components.Component.HiddenComponent;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.components.Interactable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactList;
import lc.kra.jds.contacts.ContactList.Alignment;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Example-Plugin Component Full
 * This example was developed to show and describe every aspect of the JDigitalSimulator
 * plugin development shortly, but fully covered. Due to the complete object oriented approach
 * of the JDigitalSimulator is is very easy to create and deploy new plugin-Components to the
 * JDigitalSimulator.
 * It was tried to explain a new behavior and/or feature of the JDigitalSimulator in every single
 * line of coding. Due to this many coding fragments are may not required to create the plugin
 * of your choice. The minimum plugin requirements are very simple: extend Component. Everything
 * else is optional, but provides many additional features.
 * I will mark every important/required step in this tutorial with an //XXX marker.
 * Have fun coding an own plugin, so I had while developing the JDigitalSimulator.
 * Regards,
 * @author Kristian Kraljic (kris@kra.lc)
 */

/**
 * Please make sure you have imported the <code>JDigitalSimulator.jar</code> to your classpath //XXX
 * so you will get nice value-helps and some documentations for all of the methods available.
 */

/** The annotation HiddenComponent means, that this component is not selectable in the catalog **/
@HiddenComponent
/**
 * There is basically one requirement to develop your own component:
 *   <code>extend lc.kra.jds.components.Component</code>. //XXX
 *   So you will have to implement the following methods:
 *     - <code>paint</code>: Paints the component to a graphics-Object
 *     - <code>getSize</code>: Has to return the actual component dimension
 *     - <code>calculate</code>: Calculates the required information for this component
 *   Simple, isn't it? But we will go into detail for each of those methods.
 * Following interfaces are optional, but provide (mostly needed) features:
 *   - <code>Sociable</code>: Means that this component can be connected to other components (via wires)
 *   - <code>Interactable</code>: Means that this component has events to interact with the user (clicks)
 *   - <code>Configurable</code>: Means that this component can be configured, so it has an configuration dialog
 * But, guess what... We will go into detail of those as well.
 */
public class ExamplePluginFull extends Component implements Sociable, Configurable, Interactable {
	/**
	 * The first very small, but indeed very important, information is, that every component
	 * must be <code>Serializable</code>, necessary when the component is copied, cloned or stored. //XXX
	 * This means, every single non-static, non-transient field will get stored into a file
	 * or the memory, even if you do not intend to have your component physically stored.
	 * So just use serializable classes or make sure the required field are transient. You may also
	 * make use of the writeObject/readObject methods as described in the Serializable Java Docs.
	 * You should put serialVersionUID into each of your plugins as well.
	 */
	private static final long serialVersionUID = 1l;

	/**
	 * The second very small, but again very important, information you will have to provide is
	 * the obligatory field <code>public static final ComponentAttributes componentAttributes</code> //XXX
	 * This field contains information about the Component such as the name and group entry.
	 * Following informations may be provided to enable JDigitalSimulator to get the required info:
	 * 	 <code>new ComponentAttributes(</code>
	 *     - name: The name of the component. This name will get displayed in the JDigitalSimulator
	 *             and is also used as unique identifier. So make sure there is no similar plugin
	 *             already existing
	 *     - group: The group of the component to categorize it in the menu. The following default
	 *              groups are available: group.general, group.display, group.switch, group.gate,
	 *                                    group.flipflop, group.register, group.selector, group.alu,
	 *                                    group.counter, group.converter, group.interface,
	 *                                    group.additional, group.others or any other group name.
	 *     - description: (optional) The description should provide more detailed information.
	 *     - author: (optional) The author of this plugin Component (may with some additional info-
	 *               rmation, like your homepage or contact information.
	 *     - version: (optional) The actual version of this component to track the changes.
	 *   <code>);</code>
	 * The constructors of <code>ComponentAttributes</code> will guide you through the information
	 * which is necessary to create an appropriate <code>componentAttributes</code> attribute.
	 */
	public static final ComponentAttributes componentAttributes = new ComponentAttributes("Example-Plugin", "group.general", "This is an template component for how to create an own plugin.", "Kristian Kraljic (kris@kra.lc)", 1);

	/**
	 * Let us implement our first method. So we will have to
	 *    <code>@Override public void paint(Graphics graphics)</code> //XXX
	 * of the <code>Paintable</code> interface.
	 * This method is used to draw the whole component and is called for the first time when
	 * generating a preview image for the component-catalog of the JDigitalSimulator.
	 * You may use every possibility to paint your component provided by the java
	 * <code>Graphics2D</code> classes, as described in the java documentation.
	 * It is strictly recommended that you only draw into the bounds of your own Component! It is
	 * a known-feature that you are able to draw outside of your component bounds (returned by
	 * <code>getSize</code>, but do not do it if you do not know exactly what you are doing.
	 * Note: Do <bold>not</bold> use this method for calculating anything! This method will be
	 *       likely get called when something changes on the frontend, but it is not guaranteed in
	 *       any ways, that it: a) is called every time b) called in fixed time intervals.
	 * Please use the calculate method for this reason, described later on.
	 */
	@Override public void paint(Graphics graphics) {
		Dimension size = getSize(); /** you may call other (even private) methods in here! */
		graphics.setColor(Color.BLACK); /** do not assume the color does fit right away */
		graphics.drawRect(0, 0, size.width, size.height); /** only draw inside this bounds! */
		graphics.drawString(getAttributes().name, 0, 30);
		/** there is no need to call the super.paint method for any reason */
	}

	/**
	 * The second method we will have to overwrite is the
	 *   <code>@Override public Dimension getSize()</code> //XXX
	 * method of the <code>Sizable</code> interface.
	 * This method is used to determine the bounds of your application and is later used whenever
	 * your component gets placed, dragged, moved or displayed. So assume this method gets called
	 * quite often while it is getting displayed in the JDigitalSimulator.
	 * Note: Please do <bold>never</bold> return null, or stupid values like: 0, -1, Integer.MAX_VALUE
	 * (You will not have to care about any zoom or move-action. This is handled by the framework)
	 */
	@Override public Dimension getSize() {
		Dimension dimension = new Dimension(80, 80); /** create a new suitable size for your component */
		boolean niceWeather = System.currentTimeMillis()%10000>9000; /** you may do basic calculations */
		if(niceWeather) dimension.height += 20; /** and change the size of your component dynamically
		                                            (even at runtime! or based on user configuration. */
		return dimension; /** but you will have to return a correct dimension anyways! */
	}

	/**
	 * Now we will have to discuss the benefits of the <code>Sociable</code> interface. Most times
	 * we want to create a component which may gets connected to other components using wires.
	 * (some components may not be connected to others like text or images). For this reason we
	 * will have to implement the <code>Sociable</code> interface. This will lead to that we have to
	 *   <code>@Override public Contact[] getContacts()</code> //XXX
	 * which has to return all <code>Contact</code>s of this Component. Wait, wait, wait? What the
	 * heck is a contact? A contact is the Object which is used to communicate to other components.
	 * You will not have to care about any wire, you will just have to create a interface for wire's
	 * using Contacts. Therefore there are two standard implementations of the contact-class:
	 *   - <code>InputContact</code>: Used to receive values from other components
	 *                                (Note: only one wire is connectable to an InboundContact)
	 *   - <code>OutputContact</code>: Used to set the results of your calculations and make them
	 *                                 available to other components.
	 *                                 (Note: Several wires may be connected to one OutputContact)
	 * You component may use both of them as desired in any combination. Each Contact has got a
	 * unique location in your component, so it is recommended to initialize it right when the
	 * component gets created in the constructor. But for this example we will do it in the
	 * <code>getContacts</code> method directly, to show that also a dynamic amount of Contacts is
	 * possible. To explain every detail of what a Contact is capable of would take way to long. So
	 * you have to know only a few methods to work with Contacts right away.
	 *   - <code>isCharged</code>: For Input- and OutputContacts //XXX
	 *   - <code>setCharged</code>: Set the Output-Contact as charged or uncharged
	 *   - <code>setLocation</code>: To set the location of your contact (relative in your Component)
	 * Note: You may use the class <code>ContactList</code> to create a dynamic amount of contacts,
	 *       and place them in your Component accordingly. The class also provides some additional
	 *       useful Methods to work with Conacts like the static methods <code>concatenateContacts</code>
	 *       or <code>setContactLocations</code>.
	 * So lets begin by creating some new instance variables to store the Contacts we will create.
	 */
	private InputContact input; /** create a single input contact */
	private OutputContact[] outputs; /** some more output contacts as an array of contacts */
	private ContactList<OutputContact> moreOutputs; /** and using the contact list, even more outputs */
	/**
	 * Note: only the contacts which will be returned by this method are available in your component
	 *        later on. Please to not experiment with this method, simply return all of the contacts
	 *        of your component, no matter what or where there are placed.
	 */
	@Override public Contact[] getContacts() {
		if(input==null) { /** use the utility methods of the class <code>Utilities</code> */
			/** create a new input contact, you will have to pass your component and a default location */
			input = new InputContact(this, new Point(10, 10));
			input.setLocation(new Point(20, 20)); /** you may move the contact around later on */
			input.isCharged(); /** a input contact is charged by default if no wire is connected */
			input.isWired(); /** you may check if there are already wires connected using this method */
		}
		if(outputs==null||outputs.length==0) { /** please be careful with array to not generate any exception */
			/** create a new array of output contacts, we will arrange them later on automatically */
			outputs = new OutputContact[] { new OutputContact(this), new OutputContact(this) };
			/** place all the created output contacts dynamically in an horizontal alignment at the
			 * top of the component. You may use other constructors to modify the placement of the Contacts */
			ContactList.setContactLocations(this, outputs, Alignment.HORIZONTAL);
		}
		if(moreOutputs==null) { /** I would even recommend to use static imports for some methods */
			/** if you are creating a ContactList, you will have to pass a class to generate the Contacts */
			moreOutputs = new ContactList<OutputContact>(this, OutputContact.class);
			moreOutputs.setContacts(3); /** you can simply create a dynamic number of contacts by using
			                                the <code>setContacts</code> method of the ContactList class */
			moreOutputs.setContactLocations(); /** don't forget to place the contacts in your component */
			moreOutputs.getContact(0).setCharged(); /** you may charge some of your outputs by default */
		}
		/** finally we have to return an array of all created contacts to the JDigitalSimulator, so
		 * the framework knows how many of them are available, and how to interact with this plugin.
		 * Note: Check out the <code>concatenateContacts</code> methods of the ContactList class.
		 */
		return ContactUtilities.concatenateContacts(new Contact[]{input}, outputs, moreOutputs.toArray());
	}

	/**
	 * The last method I want to describe in detail is the calculate method. It should be used
	 * for all calculations of your component. So at the best (and recommended case) every interaction
	 * of Contacts, so setting and getting Contact charges, should be done <bold>only</bold> in here.
	 * To enable a great possibility to calculate your component it is absolutely guaranteed that
	 *   the <code>calculate</code> method is called once every 100th of a second by the Simulation //XXX
	 * once the simulation has started.
	 */
	@Override public void calculate() {
		outputs[0].setCharged(input.isCharged());
		outputs[1].setCharged(!input.isCharged());
		OutputContact contact = moreOutputs.getContact(0);
		contact.setCharged(!contact.isCharged());
		if(moreOutputs.getContact(1).isWired())
			moreOutputs.getContact(2).setCharged();
	}

	/**
	 * Now I'd like to skip through the methods when implementing the <code>Configurable</code> interface.
	 * After implementing the <code>Configurable</code> interface your component will likely show an conf-
	 * iguration dialog after the user has double-clicked on the component. He can choose from any number
	 * of options to configure your component and pick the appropriate option, therefore the following methods
	 * have to be implemented:
	 *   - <code>getOptions</code>: Returns a list of all available options for your component
	 *   - <code>getConfiguration</code>: Gets the actual configuration of all options defined
	 *   - <code>setConfiguration</code>: Sets the new configuration of all options you returned
	 * Lets begin implementing the getOptions method. Is is recommended to buffer the options you want
	 * to return, but it is also no problem to create a new array of options every time the method is called.
	 * Each Option has to have an unique option-id, an option label, an option type and may have a default-value.
	 * Following <code>OptionType</code>s generally are acceptable:
	 * 	 - <code>OptionType.TEXT</code>: A user defined text (or even an empty one) may be entered by the user
	 * 	 - <code>OptionType.NUMBER</code>: A number convering the whole integer range may be entered by the user
	 * 	 - <code>OptionType.BOOLEAN</code>: A boolish value (true/false) may be picked by the user
	 * 	 - <code>OptionType.LIST</code>: You can propose a list of values, one of the values will be returned
	 * There is no limitation how may options are returned by this method.
	 */
	@Override public Option[] getOptions() {
		return new Option[]{ /** create a new list of options */
				/** the first option is of type number, it is checked if the default value is of the same type */
				new Option("number", "Number:", OptionType.NUMBER, 10),
				/** the second option is of type text and has the initial value 'Default text' */
				new Option("text", "Text:", OptionType.TEXT, "Default text"),
				/** the third option is of type boolean, has no default option and option-id, so the boolish value
				 * false will be taken as default value and the label-text as option-identifier. */
				new Option("Boolean:", OptionType.BOOLEAN),
				/** the last option is of type list, so you have to return any object array (also sub-types allowed)
				 * containing the options which may be picked by the user. Please note that the first element of the
				 * array is the option which is selected by default. */
				new Option("list", "List:", OptionType.LIST, new Object[]{"Default list option", "Second list option"})
		};
	}
	/**
	 * The getConfiguration method has to return the actual configuration for all options every time it is
	 * called. It is used to determine the current option state by the JDititalSimulator if the user is
	 * requesting to change a value. Therefore a mapping between the options and option values has to be made.
	 * I would recommend to use the default Java HashMap, but feel free to use any other class implementing
	 * the map interface. It is recommended to call the getOptions Method in this method to receive a set
	 * of all options and fill the values for all of them. But first we have to create some field to store the
	 * configuration in. (Please be aware that also this fields have to be serializable or the configuration
	 * will not be stored properly!)
	 */
	private int number = 10; private String text = "Default text"; private boolean bool = false; private String list = "Default list option";
	/**
	 * It is recommended that you initialize your variables with the same values as you return as default-
	 * values in the <code>getOptions</code>-method, or move this logic into the getConfiguration method. */
	@Override public Map<Option, Object> getConfiguration() {
		Option[] options = getOptions(); /** first get all defined options */
		Map<Option, Object> configuration = new HashMap<Option, Object>(); /** create a new map for the config */
		configuration.put(options[0], number); /** put all the options into the map */
		/** please make sure that you do not return any nonsense like a value of the wrong type or null */
		configuration.put(options[1], text!=null?text:new String());
		if(!bool&&list.isEmpty()) return null; /** return no configuration to cancel the configuration dialog */
		/** if some values are not available in the configurations array, the default value will get picked
		 * every time the user chooser to re-configure the component. */
		return configuration; /** return the configuration, the next step is now likely the call of setConfiguration */
	}
	/**
	 * The <code>setConfiguration</code> is the call back method, when the user has finished customizing
	 * the component options has has closed the dialog by clicking okay. You will get a new Map with an
	 * Option-Value association, and you may now fill your configuration fields again. It is again recommended
	 * to use the options array returned by <code>getOptions</code> to retrieve the value. You can be sure
	 * that there is an entry for all the options you returned in getOptions recently. If you are not
	 * satisfied with one or more values the user entered, you may throw a <code>PropertyVetoException</code>,
	 * with an appropriate message, so the user will not be able to leave the dialog until he has entered
	 * the correct value.
	 */
	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		Option[] options = getOptions(); /** get a list of all defined options again */
		/** please make sure that you do all required checks at the beginning of this method so there is no
		 * option set to any other value until the users enters all values correctly. */
		if(!((Boolean)configuration.get(options[3]))) /** throw a new veto exception to cancel this method */
			throw new PropertyVetoException("Please choose the boolish value true.", null);
		this.bool = (Boolean) configuration.get(options[3]); /** store the value after it has been checked */
		this.number = (Integer)configuration.get(options[0]); /** you may do a direct cast to the appropriate class */
		this.text = configuration.get(options[1]).toString(); /** or you use the appropriate cast-methods */
		this.list = configuration.get(options[4]).toString(); /** if it is a list option the selection is returned */
	}

	/**
	 * The last methods I want to describe in this tutorial are the methods defined in the <code>Interactable</code>
	 * interface. These are (so far) mouseClick, mouseDoubleClick, mouseDown and mouseUp. And they will be
	 * called if the component implements this interface, every time the user performs one of these events
	 * directly on your object (in your object bounds). Please be aware that the Simulator has no necesarrily
	 * to be in the simulation mode. Therefore use this options with care. You may use all 	of the
	 * methods available in the event-objects passed to the methods. I will not describe the methods any further
	 * because I think they are pretty self-explaining. You may modify, change or alter any thing of your
	 * Components in all of the methods.
	 */
	@Override public void mouseClick(MouseEvent event) {}
	@Override public void mouseDoubleClick(MouseEvent event) {}
	@Override public void mouseDown(MouseEvent event) {}
	@Override public void mouseUp(MouseEvent event) {}
}

/**
 * Finally we are through our small tutorial. Now we need to test our component in the JDigitalSimulator.
 * Place the class file or any jar file containing your component into the "plugins"-directory //XXX
 * of your JDigitalSimulator (were you have probably found this file as well) and start the
 * JDigitalSimulator. If you have set the componentAttributes field correctly the component
 * will get directly displayed in the menu of the JDigitalSimulator. */