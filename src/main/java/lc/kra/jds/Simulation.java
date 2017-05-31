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
package lc.kra.jds;

import static lc.kra.jds.Utilities.computeHash;
import static lc.kra.jds.Utilities.getTranslation;
import static lc.kra.jds.gui.Guitilities.addPoints;
import static lc.kra.jds.gui.Guitilities.invertPoint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

import lc.kra.jds.Utilities.LegacyObjectInputStream;
import lc.kra.jds.Utilities.RememberFileChooser;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Component.ComponentFlavor;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Configurable.Option;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.components.Interactable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.components.Wire;
import lc.kra.jds.components.buildin.display.Voltmeter;
import lc.kra.jds.components.buildin.external.ExternalSimulation;
import lc.kra.jds.components.buildin.general.Junction;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.DragContact;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;
import lc.kra.jds.exceptions.PasswordRequiredException;
import lc.kra.jds.exceptions.WireNotConnectable;
import lc.kra.jds.exceptions.WiringException;
import lc.kra.jds.gui.Application;
import lc.kra.jds.gui.Application.SimulationFrame;
import lc.kra.jds.gui.Guitilities;
import lc.kra.jds.gui.Guitilities.Direction;
import lc.kra.jds.gui.Guitilities.Rectangle;
import lc.kra.jds.gui.Guitilities.RelativeMouseEvent;

public class Simulation extends JComponent implements Scrollable, Printable {
	private static final long serialVersionUID = 1l;

	public enum Layer { TOPMOST, BOTTOMMOST; }

	private static final Color SELECTION = new Color(10, 200, 10, 75);
	private static final Dimension SIZE = new Dimension(2000, 2000);
	private static final int GRID_STEPS = 20;
	private static final int STACK_SIZE = 200;

	public SimulationProperies properties;
	private SimulationData data;

	private Timer timer;
	private SimulationOscilloscope oscilloscope;

	private AffineTransform transform;

	private long paint;
	private Set<Component> selected;
	private Point select, current, last;
	private Clipboard clipboard;

	private Wire selectWire;
	private Contact selectContact;
	private Component selectComponent;

	private DragContact dragContact;
	private BufferedImage dragImage;
	private Set<Component> dragComponents;
	private Set<Wire> dragWires;

	private double zoom;
	private boolean gridVisible;
	private BufferedImage gridBuffer;

	protected List<EventListener> listeners;

	private LinkedList<ChangeEvent> past, future;

	public Simulation() { this(null); }
	public Simulation(Clipboard clipboard) {
		properties = new SimulationProperies();
		data = new SimulationData();
		this.initialize();
		this.setClipboard(clipboard);
		this.revalidate();
	}
	protected Simulation(SimulationProperies properties, SimulationData data) {
		this.properties = properties!=null?properties:new SimulationProperies();
		this.data = data!=null?data:new SimulationData();
		this.initialize();
		this.createClipboard();
		this.revalidate();
	}

	private void initialize() {
		selected = new HashSet<Component>();
		transform = new AffineTransform();
		past = new LinkedList<ChangeEvent>();
		future = new LinkedList<ChangeEvent>();
		this.setZoom(1.0d);
		this.setFocusable(true);
		this.setPreferredSize(SIZE);
		initializeListeners();
		initializeDropTarget();
	}
	private void initializeListeners() {
		listeners = new Vector<EventListener>();
		addListeners();
	}

	@Override protected void processMouseEvent(MouseEvent event) {
		super.processMouseEvent(event);
		switch(event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if(event.getButton()!=MouseEvent.BUTTON1)
				return;
			Point point = applyZoom(event.getPoint());
			Component component = searchComponentAt(point);
			if(component==null) return;
			else if(component instanceof Interactable) {
				event = new RelativeMouseEvent(event, point, component.getLocation());
				if(Utilities.isOdd(event.getClickCount()))
					((Interactable) component).mouseClick(event);
				else ((Interactable) component).mouseDoubleClick(event);
			}
			if(!isSimulating()&&event.getButton()==MouseEvent.BUTTON1&&
					event.getClickCount()>=2)
				showComponentConfigurationDialog(component);
		}
	}

	private void addListeners() {
		removeListeners();
		this.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent event) {
				switch(event.getKeyCode()) {
				case KeyEvent.VK_DELETE:
					if(selectWire!=null) {
						selectWire.removeWire();
						selectWire = null;
					} else removeComponents(selected);
					for(FocusListener listener:getFocusListeners())
						listener.focusLost(null);
					repaint();
					break;
				case KeyEvent.VK_Z:
					if(event.isControlDown())
						undoChange();
					break;
				case KeyEvent.VK_Y:
					if(event.isControlDown())
						redoChange();
					break;
				case KeyEvent.VK_C:
					if(event.isControlDown())
						try {	copyToClipboard(); }
					catch (CloneNotSupportedException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(Simulation.this, getTranslation("clipboard.copy.error"), getTranslation("clipboard.copy.error", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
						setClipboard(null);
					}
					break;
				case KeyEvent.VK_X:
					if(event.isControlDown())
						try {	cutIntoClipboard(); }
					catch (CloneNotSupportedException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(Simulation.this, getTranslation("clipboard.cut.error"), getTranslation("clipboard.cut.error", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
						setClipboard(null);
					}
					break;
				case KeyEvent.VK_V:
					if(event.isControlDown())
						try {
							if(clipboard==null)
								return;
							selected.clear();
							Set<Component> clipboard = pasteFromClipboard();
							if(clipboard!=null)
								selected.addAll(clipboard);
							if(isGridVisible())
								for(Component component:clipboard)
									snapComponentToGrid(component);
							repaint();
						} catch (CloneNotSupportedException e) {
							JOptionPane.showMessageDialog(Simulation.this, getTranslation("clipboard.paste.error"), getTranslation("clipboard.paste.error", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
						}
					break;
				case KeyEvent.VK_A:
					if(event.isControlDown()) {
						setSelectedComponents(getAllComponents());
						repaint();
					}
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			private void showPopup(final MouseEvent event, final Component component) {
				JPopupMenu menu = new JPopupMenu();
				if(!(component instanceof Wire)) {
					if(component instanceof ExternalSimulation) {
						menu.add(Guitilities.createMenuItem("component.open.simulation", new ActionListener() {
							@Override public void actionPerformed(ActionEvent actionevent) {
								ExternalSimulation externalSimulation = (ExternalSimulation) component;
								Simulation simulation = externalSimulation.getSimulation();
								if(simulation==null)
									return;
								Container parent = simulation.getParent();
								if(parent==null||!parent.isDisplayable()||!parent.isVisible()) {
									Application application = getApplication();
									if(application!=null)
										application.addWorksheet(application.new SimulationFrame(simulation, externalSimulation.getFile()));
								} else {
									SimulationFrame frame = getSimulationFrame();
									if(frame != null)
										Guitilities.selectFrame(frame);
								}
							}
						}));
						menu.add(new JSeparator());
					}
					if(component instanceof Configurable)
						menu.add(Guitilities.createMenuItem("component.properties", new ActionListener() {
							@Override public void actionPerformed(ActionEvent actionevent) { showComponentConfigurationDialog(component); }
						}));
					menu.add(Guitilities.createMenuItem("component.delete", new ActionListener() {
						@Override public void actionPerformed(ActionEvent actionevent) { removeComponent(component); }
					}));
				} else { //is a wire
					final Wire wire = (Wire)component;
					menu.add(Guitilities.createMenuItem("wire.add.junction", new ActionListener() {
						@Override public void actionPerformed(ActionEvent actionevent) {
							Junction junction = new Junction(event.getPoint());
							Contact source = wire.getSource(), target = wire.getTarget();
							wire.removeWire();
							try {
								new Wire(source, junction.input);
								new Wire(junction.output, target);
								addComponent(junction);
								selectWire = null;
							}	catch (WireNotConnectable e_a) {
								junction.input.removeWires(); junction.output.removeWires();
								try { new Wire(source, target); }
								catch (WireNotConnectable e_b) { }
							}
						}
					}));
					menu.add(Guitilities.createMenuItem("wire.add.voltmeter", new ActionListener() {
						@Override public void actionPerformed(ActionEvent actionevent) {
							Voltmeter voltmeter = new Voltmeter(event.getPoint());
							try {
								new Wire(wire.getSource(), voltmeter.getContacts()[0]);
								addComponent(voltmeter);
							}	catch (WireNotConnectable e) { }
						}
					}));
				}
				menu.setLocation(event.getLocationOnScreen());
				menu.show(event.getComponent(), event.getX(), event.getY());
			}
			@Override public void mousePressed(MouseEvent event) {
				last = null; select=current=applyZoom(event.getPoint());
				selectContact = null; selectWire = null; selectComponent = null;
				selectContact = event.isControlDown()?null:searchContactAt(current);
				if(selectContact==null) {
					selectWire = event.isControlDown()?null:searchWireAt(current);
					if(selectWire==null) {
						selectComponent = searchComponentAt(current);
						if(selectComponent instanceof Interactable) {
							((Interactable) selectComponent).mouseDown(event);
							repaint();
						}
						if(event.isPopupTrigger())
							showPopup(event, selectComponent);
					} else { //a wire is selected
						selected.clear();
						if(event.isPopupTrigger())
							showPopup(event, selectWire);
					}
				} else { //a contact is selected
					selected.clear();
					Wire wire = selectContact.getWire();
					try {
						if(!selectContact.isWired()||event.isShiftDown()&&selectContact instanceof OutputContact) {
							if(selectContact instanceof OutputContact) {
								dragContact = new DragContact(Simulation.this, InputContact.class);
								new Wire(selectContact, dragContact);
							} else {
								dragContact = new DragContact(Simulation.this, OutputContact.class);
								new Wire(dragContact, selectContact);
							}
						} else { //the contact has already got a wire
							dragContact = new DragContact(Simulation.this, selectContact.getClass());
							if(selectContact instanceof OutputContact)
								wire.setSource(dragContact);
							else wire.setTarget(dragContact);
						}
						dragContact.component.setLocation(current);
						addComponent(dragContact.component);
					}	catch (WireNotConnectable e) {
						if(wire!=null)
							wire.removeWire();
					}
				}
			}
			@Override public void mouseReleased(MouseEvent event) {
				if(select==null)
					return;
				if(selectWire!=null) {
					if(event.isPopupTrigger())
						showPopup(event, selectWire);
				} else if(selectComponent!=null) {
					if(selectComponent instanceof Interactable)
						((Interactable)selectComponent).mouseUp(event);
					if(dragComponents!=null) {
						moveComponents(dragComponents, select, current);
						dragImage = null; dragComponents = null; dragWires = null;
					}
					if(event.isPopupTrigger())
						showPopup(event, selectComponent);
					if(isGridVisible())
						if(!selected.contains(selectComponent))
							snapComponentToGrid(selectComponent);
						else snapComponentsToGrid(Simulation.this.selected);
					if(!selected.contains(selectComponent)) {
						if(!event.isControlDown())
							selected.clear();
						selected.add(selectComponent);
					}
				} else selected = searchComponentsIn(new Rectangle(select, current));
				select = null; selectComponent = null;
				for(FocusListener listener:getFocusListeners()) //likely but not 100% sure
					if(selected.size()!=0)
						listener.focusGained(null);
					else listener.focusLost(null);
				repaint();
			}
		});
		this.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent event) { current = applyZoom(event.getPoint()); }
			@Override
			public void mouseDragged(MouseEvent event) {
				Point current = applyZoom(event.getPoint());
				if(last==null&&select!=null&&current.distance(select)<5) return;
				last = Simulation.this.current; Simulation.this.current = current;
				if(selectComponent!=null) {
					if(dragComponents!=null) {
						if((System.currentTimeMillis()-paint)>20)
							repaint();
						return;
					}

					dragComponents = new HashSet<Component>();
					if(selected.contains(selectComponent))
						dragComponents.addAll(selected);
					else dragComponents.add(selectComponent);
					dragWires = new HashSet<Wire>();
					for(Component dragComponent:dragComponents)
						if(dragComponent instanceof Sociable)
							for(Contact dragContact:((Sociable)dragComponent).getContacts())
								for(Wire dragWire:dragContact.getWires())
									if(dragContact instanceof InputContact) {
										if(!dragComponents.contains(dragWire.getSource().getComponent()))
											dragWires.add(dragWire);
									} else if(dragContact instanceof OutputContact) {
										if(!dragComponents.contains(dragWire.getTarget().getComponent()))
											dragWires.add(dragWire);
									}

					GraphicsConfiguration context = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
					dragImage = context.createCompatibleImage(SIZE.height, SIZE.width, Transparency.TRANSLUCENT);
					Graphics2D graphics = prepareGraphics(dragImage.createGraphics());
					graphics.setColor(new Color(0, 0, 0, 0));
					graphics.fillRect(0, 0, SIZE.height, SIZE.width);
					paintComponents(graphics, dragComponents, dragWires);
				} else if(selectWire!=null) {
					selectWire.setPreferredLocation(event.getPoint());
					repaint();
				} else repaint();
			}
		});
	}
	private void removeListeners() {
		for(KeyListener listener:this.getKeyListeners())
			this.removeKeyListener(listener);
		for(MouseListener listener:this.getMouseListeners())
			this.removeMouseListener(listener);
		for(MouseMotionListener listener:this.getMouseMotionListeners())
			this.removeMouseMotionListener(listener);
	}

	protected void snapComponentsToGrid(Collection<Component> components) { for(Component component:components) snapComponentToGrid(component); }
	protected void snapComponentToGrid(Component component) {
		if(component==null)
			return;
		Point location = new Point(component.getLocation());
		location.x = ((location.x+GRID_STEPS/2)/GRID_STEPS)*GRID_STEPS;
		location.y = ((location.y+GRID_STEPS/2)/GRID_STEPS)*GRID_STEPS;
		component.setLocation(location);
	}

	protected void showComponentConfigurationDialog(Component component) {
		if(!(component instanceof Configurable))
			return;
		final Configurable configurable = (Configurable)component;
		Option[] options = configurable.getOptions();
		if(options==null||options.length<=0)
			return;
		Map<Option, Object> configuration = configurable.getConfiguration();
		if(configuration==null)
			return;
		final HashMap<Option, JComponent> inputs = new HashMap<Option, JComponent>();
		final JDialog dialog = Guitilities.createDialog(null, getTranslation("component.configuration", TranslationType.TITLE),
				getTranslation("component.configuration", component.getAttributes().name));
		JPanel centerPane = new JPanel(new GridBagLayout()); int grid = 0;
		centerPane.setBorder(Guitilities.HUGE_EMPTY_BORDER);
		for(final Option option:options) {
			Object value = configuration.containsKey(option)?configuration.get(option):option.getDefault();
			JComponent input = null;
			switch(option.getType()) {
			case NUMBER:
				input = new JSpinner();
				JFormattedTextField textField = ((JSpinner.NumberEditor)((JSpinner)input).getEditor()).getTextField();
				((NumberFormatter)textField.getFormatter()).setAllowsInvalid(false);
				((JSpinner)input).setValue(value);
				break;
			case TEXT:
				input = new JTextField();
				((JTextField)input).setText(value.toString());
				break;
			case BOOLEAN:
				input = new JCheckBox();
				((JCheckBox)input).setSelected((Boolean)value);
				break;
			case LIST:
				input = new JComboBox<Object>((Object[])option.getDefault());
				((JComboBox<?>)input).getModel().setSelectedItem(value.toString());
				break;
			case FILE:
				final JTextField fileText = new JTextField() {
					private static final long serialVersionUID = 1l;
					@Override public void paint(Graphics graphics) {
						super.paint(graphics);
						if(getText().isEmpty()) {
							graphics.setColor(Color.GRAY);
							graphics.setFont(graphics.getFont().deriveFont(10f));
							String explanation = getTranslation("component.simulation.file.choose");
							graphics.drawString(explanation, 10, Guitilities.centerText(Direction.VERTICAL, graphics, getSize(), explanation));
						}
					}
				};
				fileText.setEditable(false);
				if(value!=null)
					fileText.setText(value.toString());
				fileText.addMouseListener(new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent event) {
						JFileChooser chooser = new RememberFileChooser();
						chooser.setFileFilter((FileFilter)option.getDefault());
						if(chooser.showOpenDialog(dialog)!=JFileChooser.APPROVE_OPTION)
							return;
						fileText.setText(chooser.getSelectedFile().getPath());
					}
				});
				input = fileText;
				break;
			}
			inputs.put(option, input);

			String caption = null;
			if(option.hasDefault())
				if(!option.getType().equals(OptionType.FILE)) {
					String variable = null;
					switch(option.getType()) {
					case LIST: variable = ((Object[])option.getDefault())[0].toString(); break;
					default:   variable = option.getDefault().toString(); }
					caption = getTranslation("component.configuration.default", getTranslation(variable, TranslationType.EXTERNAL));
				} else caption = ((FileFilter)option.getDefault()).getDescription();
			Guitilities.addGridPairLine(centerPane, grid++, new JLabel(getTranslation(option.getLabel(), TranslationType.EXTERNAL)+":"), input, caption!=null?new JLabel(caption):null);
		}
		dialog.add(centerPane, BorderLayout.CENTER);

		JPanel bottomPane = Guitilities.createGradientFooter();
		bottomPane.add(Guitilities.createButton(getTranslation("component.configuration.okay"), new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				Map<Option, Object> configuration = new HashMap<Option, Object>();
				try {
					for(Option option:inputs.keySet()) {
						JComponent component = inputs.get(option); Object value = null;
						switch(option.getType()) {
						case NUMBER: value = Integer.parseInt(((JSpinner)component).getValue().toString()); break;
						case TEXT:   value = ((JTextField)component).getText(); break;
						case BOOLEAN:value = ((JCheckBox)component).isSelected(); break;
						case LIST:   value = ((JComboBox<?>)component).getSelectedItem().toString(); break;
						case FILE:   value = new File(((JTextField)component).getText()); break; }
						configuration.put(option, value);
					}
					configurable.setConfiguration(configuration);
					dialog.dispose();
					repaint();
				} catch(NumberFormatException e) {
					JOptionPane.showMessageDialog(dialog, getTranslation("component.configuration.error.number"), getTranslation("component.configuration.error", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
				} catch(PropertyVetoException e) {
					JOptionPane.showMessageDialog(dialog, e.getMessage(), getTranslation("component.configuration.error", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
				}
			}
		}));
		bottomPane.add(Guitilities.createButton(getTranslation("component.configuration.cancel"), new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) { dialog.dispose(); }
		}));
		dialog.add(bottomPane, BorderLayout.SOUTH);

		dialog.pack(); dialog.setSize(new Dimension(500, dialog.getHeight()));
		dialog.setVisible(true);
	}

	public void setEditable(boolean editable) {
		if(editable)
			addListeners();
		else removeListeners();
		getDropTarget().setActive(editable);
	}

	public Clipboard createClipboard() { return setClipboard(null); }
	public Clipboard setClipboard(Clipboard clipboard) {
		if(clipboard==null)
			this.clipboard = new Clipboard();
		else this.clipboard = clipboard;
		return this.clipboard;
	}

	public void copyToClipboard() throws CloneNotSupportedException { copyToClipboard(selected); }
	public void copyToClipboard(Collection<Component> components) throws CloneNotSupportedException {
		clipboard.setContent(Component.clone(components));
	}

	public void cutIntoClipboard() throws CloneNotSupportedException { cutIntoClipboard(selected); }
	public void cutIntoClipboard(Collection<Component> components) throws CloneNotSupportedException {
		copyToClipboard();
		removeComponents(components);
	}

	public Set<Component> pasteFromClipboard() throws CloneNotSupportedException {
		if(!clipboard.hasContent())
			return null;
		Set<Component> components = this.clipboard.getContent();
		for(Component component:components)
			component.moveRelative(new Point(25, 25));
		this.clipboard.setContent(Component.clone(components));
		addComponents(components);
		return components;
	}

	private void initializeDropTarget() {
		new DropTarget(this, TransferHandler.COPY, new DropTargetAdapter() {
			@Override public void dragOver(DropTargetDragEvent event) {
				if(!event.isDataFlavorSupported(ComponentFlavor.getFlavor()))
					event.rejectDrag();
				else event.acceptDrag(TransferHandler.COPY);
			}
			@Override public void drop(DropTargetDropEvent event) {
				try {
					Object data = event.getTransferable().getTransferData(ComponentFlavor.getFlavor());
					if(!(data instanceof Class))
						throw new UnsupportedClassVersionError();
					Object object = ((Class<?>)data).newInstance();
					if(object instanceof Component) {
						Component component = (Component)object;
						Dimension size = component.getSize();
						component.setLocation(applyZoom(event.getLocation()));
						try { component.moveRelative(new Point(-size.width/2, 0)); }
						catch(LocationOutOfBoundsException e) { component.moveTo(new Point(0, component.getLocation().y)); }
						try { component.moveRelative(new Point(0, -size.height/2)); }
						catch(LocationOutOfBoundsException e) { component.moveTo(new Point(component.getLocation().x, 0)); }
						addComponent(component);
						repaint();
					}
				}	catch(Exception e) { e.printStackTrace(); event.rejectDrop(); }
			}
		}, true);
	}

	@Override public Dimension getPreferredSize() { return applyZoom(super.getPreferredSize()); }
	@Override public void paint(Graphics defaultGraphics) {
		super.paint(defaultGraphics);
		Graphics2D graphics = prepareGraphics(defaultGraphics);
		AffineTransform transform = graphics.getTransform();
		graphics.transform(this.transform);

		paint = System.currentTimeMillis();
		if(isGridVisible())
			paintGrid(graphics, super.getPreferredSize());

		List<Component> components = new ArrayList<Component>(getAllComponents());
		if(dragComponents!=null)
			components.removeAll(dragComponents);
		paintComponents(graphics, components, dragWires);
		if(selectWire!=null) {
			graphics.setColor(SELECTION);
			graphics.fill(selectWire.getSelectionArea());
		} else if(selectComponent==null)
			paintSelection(graphics);
		if(dragContact!=null&&dragContact.isWired())
			dragContact.getWire().paint(graphics);
		if(dragComponents!=null) {
			Point point = invertPoint(addPoints(select, invertPoint(current)));
			graphics.drawImage(dragImage, point.x, point.y, null);
			if(dragWires!=null) for(Wire dragWire:dragWires) {
				if(dragComponents.contains(dragWire.getSource().getComponent()))
					dragWire.paint(graphics, point);
				else if(dragComponents.contains(dragWire.getTarget().getComponent()))
					dragWire.paint(graphics, null, point);
			}
		}

		graphics.setTransform(transform);
	}

	private Graphics2D prepareGraphics(Graphics defaultGraphics) {
		Graphics2D graphics = (Graphics2D)defaultGraphics;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return graphics;
	}
	private void paintGrid(Graphics graphics, Dimension size) {
        if(gridBuffer==null || gridBuffer.getHeight()!=size.getHeight() || gridBuffer.getWidth()!=size.getWidth()){
            gridBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics gridImage=gridBuffer.getGraphics();
            gridImage.setColor(Color.LIGHT_GRAY);
            for(int x=0;x<size.width+GRID_STEPS;x+=GRID_STEPS)
                for(int y=0;y<size.height+GRID_STEPS;y+=GRID_STEPS) {
                    gridImage.drawLine(x-3, y, x+3, y);
                    gridImage.drawLine(x, y-3, x, y+3);
                }
        }
        graphics.drawImage(gridBuffer,0,0,null);
	}
	@SuppressWarnings("unused") private void paintComponents(Graphics graphics, Collection<Component> components) { paintComponents(graphics, components, null); }
	private void paintComponents(Graphics graphics, Collection<Component> components, Collection<?> ignore) {
		List<Wire> wires = new ArrayList<Wire>();
		if(ignore==null) ignore = Collections.EMPTY_LIST;
		for(Component component:components)
			if(!ignore.contains(component))
				wires.addAll(paintComponent(graphics, component));
		for(Wire wire:wires)
			if(!ignore.contains(wire))
				wire.paint(graphics);
	}
	private List<Wire> paintComponent(Graphics graphics, Component component) {
		Point location = component.getLocation();
		Dimension size = component.getSize();
		graphics.setColor(Color.BLACK);
		graphics.setFont(Font.decode(null));
		graphics.translate(location.x, location.y);
		component.paint(graphics);
		List<Wire> wires = new ArrayList<Wire>();
		if(component instanceof Sociable)
			for(Contact contact:((Sociable)component).getContacts()) {
				contact.paint(graphics);
				if(contact instanceof OutputContact&&contact.isWired())
					wires.addAll(contact.getWires());
			}
		graphics.translate(-location.x, -location.y);
		if(selected.contains(component)) {
			graphics.setColor(Color.GREEN);
			graphics.drawRect(location.x-5, location.y-5,
					size.width+10, size.height+10);
		}
		return wires;
	}
	private void paintSelection(Graphics graphics) {
		if(select==null||(dragContact!=null&&dragContact.isWired()))
			return;
		graphics.setColor(SELECTION);
		Rectangle rectangle = new Rectangle(select, current);
		graphics.fillRect(rectangle.point_ul.x, rectangle.point_ul.y,
				rectangle.size.width, rectangle.size.height);
		graphics.drawRect(rectangle.point_ul.x, rectangle.point_ul.y,
				rectangle.size.width-1, rectangle.size.height-1);
	}

	public boolean canUndoChange() { return !past.isEmpty(); }
	public void undoChange() {
		if(!canUndoChange())
			return;
		ChangeEvent event = past.removeLast();
		event.undo();
		future.add(event);
	}
	public boolean canRedoChange() { return !future.isEmpty(); }
	public void redoChange() {
		if(!canRedoChange())
			return;
		ChangeEvent event = future.removeLast();
		event.redo();
		past.add(event);
	}
	private void propergateChange(ChangeEvent event) {
		if(recentChange()||(!past.isEmpty()&&past.getLast().join(event)))
			return;
		while(past.size()>=STACK_SIZE)
			past.remove(0);
		past.add(event);
		future.clear();
	}
	private boolean recentChange() {
		for(StackTraceElement element:Thread.currentThread().getStackTrace())
			if(element.getClassName().equals(Simulation.class.getName())
					&&(element.getMethodName().equals("undoChange")
							||element.getMethodName().equals("redoChange")))
				return true;
		return false;
	}

	public void addChangeListener(ChangeListener listener) { addListener(listener); }
	public void removeChangeListener(ChangeListener listener) { removeListener(listener); }
	protected void fireChangedEvent(ChangeEvent event) {
		if(dragContact!=null&&event instanceof ComponentEvent&&((ComponentEvent)event).components.contains(dragContact.component))
			return;
		propergateChange(event);
		for(EventListener listener:listeners)
			if(listener instanceof ChangeListener)
				((ChangeListener)listener).changed(event);
	}

	protected void addListener(EventListener listener) { if(listener!=null&&!listeners.contains(listener)) listeners.add(listener); }
	protected boolean removeListener(EventListener listener) { if(listener==null) return false; return listeners.remove(listener); }

	public boolean hasComponent(Component component) { return getAllComponents().contains(component); }
	public List<Component> getAllComponents() { return data.components; }
	public Set<Component> getSelectedComponents() { return this.selected; }
	public void setSelectedComponents(Collection<Component> components) {
		selected.clear();
		selected.addAll(components);
		repaint();
	}

	public void setComponentLayer(Component component, Layer layer) {
		Vector<Component> vector = new Vector<Component>();
		vector.add(component);
		this.setComponentLayers(vector, layer);
	}
	public void setComponentLayers(Collection<? extends Component> components, Layer layer) {
		data.components.removeAll(components);
		switch(layer) {
		case TOPMOST:    data.components.addAll(components);   break;
		case BOTTOMMOST: data.components.addAll(0, components); break; }
		repaint();
	}

	public Component searchComponentAt(Point point) {
		List<Component> components = new Vector<Component>(getAllComponents());
		Collections.reverse(components);
		for(Component component:components) {
			Point location = component.getLocation();
			Dimension size = component.getSize();
			int dx = point.x-location.x, dy = point.y-location.y;
			if(dx>0&&dy>0&&dx<=size.width&&dy<=size.height)
				return component;
		}
		return null;
	}
	public Set<Component> searchComponentsIn(Rectangle rectangle) {
		Set<Component> components = new HashSet<Component>();
		if(rectangle.getArea()<=0) //there can't be any components in 'no' area
			return components;
		for(Component component:getAllComponents())
			if(rectangle.contains(component.getLocation(), component.getSize()))
				components.add(component);
		return components;
	}

	public void addComponent(Component component) {
		innerAddComponent(component);
		fireChangedEvent(new ComponentAdded(component));
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				Simulation.this.requestFocus();
			}
		});
		repaint();
	}
	protected void innerAddComponent(Component component) {
		if(component instanceof Wire)
			throw new WiringException("A wire can not be added to the board directly, they only connect two components.");
		if(!data.components.contains(component))
			data.components.add(component);
		if(isGridVisible())
			snapComponentToGrid(component);
	}
	public void addComponents(Collection<? extends Component> components) {
		innerAddComponents(components);
		fireChangedEvent(new ComponentAdded(components));
		repaint();
	}
	protected void innerAddComponents(Collection<? extends Component> components) {
		for(Component component:components)
			innerAddComponent(component);
	}

	public boolean removeComponent(Component component) {
		if(innerRemoveComponent(component)) {
			fireChangedEvent(new ComponentRemoved(component));
			repaint();
			return true;
		} else return false;
	}
	protected boolean innerRemoveComponent(Component component) {
		Collection<Wire> wires = new HashSet<Wire>();
		if(component instanceof Sociable)
			for(Contact contact:((Sociable)component).getContacts())
				wires.addAll(contact.getWires());
		Contact source = null; Collection<Contact> targets = new Vector<Contact>();
		if(component instanceof Junction) {
			Junction junction = (Junction)component;
			if(junction.input.isWired())
				source = junction.input.getWire().getSource();
			for(Wire wire:junction.output.getWires())
				targets.add(wire.getTarget());
		}
		for(Wire wire:wires)
			wire.removeWire();
		if(selected.contains(component))
			selected.remove(component);
		boolean removed = data.components.remove(component);
		if(source!=null)
			for(Contact target:targets)
				try { new Wire(source, target); }
		catch(WireNotConnectable e) { }
		return removed;
	}
	public void removeComponents(Collection<? extends Component> components) {
		components = new ArrayList<Component>(components); //important, create a new list, if the components list is the selections list it gets motified!
		innerRemoveComponents(components);
		fireChangedEvent(new ComponentRemoved(components));
		repaint();
	}
	protected void innerRemoveComponents(Collection<? extends Component> components) {
		for(Component component:components.toArray(new Component[0])) //convert to an array to avoid concurrent modifications
			innerRemoveComponent(component);
	}

	public void moveComponent(Component component, Point from, Point to) {
		innerMoveComponent(component, from, to);
		fireChangedEvent(new ComponentMoved(component, from, to));
		repaint();
	}
	protected void innerMoveComponent(Component component, Point from, Point to) {;
	Point relative = new Point(to.x-from.x, to.y-from.y);
	try { component.moveRelative(relative);	}
	catch(LocationOutOfBoundsException e) {
		//this exception can be ignored the component is not moved either way
	}
	}
	public void moveComponents(Collection<? extends Component> components, Point from, Point to) {
		innerMoveComponents(components, from, to);
		fireChangedEvent(new ComponentMoved(components, from, to));
		repaint();
	}
	protected void innerMoveComponents(Collection<? extends Component> components, Point from, Point to) {
		innerMoveComponents(components, new Point(to.x-from.x, to.y-from.y));
	}
	protected void innerMoveComponents(Collection<? extends Component> components, Point relative) {
		for(Component component:components)
			try { component.moveRelative(relative);	}
		catch(LocationOutOfBoundsException e) {
			Point location = component.getLocation(), point = new Point(relative);
			if(location.x+relative.x<0) point.x = -location.x;
			if(location.y+relative.y<0) point.y = -location.y;
			component.moveRelative(point);
		}
	}

	public Contact searchContactAt(Point point) { return searchContactAt(point, Contact.class); }
	public Contact searchContactAt(Point point, Class<? extends Contact> cls) {
		List<Contact> contacts = searchContactIn(new Rectangle(addPoints(point, new Point(-5, -5)),
				addPoints(point, new Point( 5, 5))), cls);
		if(!contacts.isEmpty()) {
			Contact nearbyContact = null; double nearbyDistance = Double.MAX_VALUE;
			for(Contact contact:contacts) {
				double distance = addPoints(contact.getComponent().getLocation(), contact.getLocation()).distance(point);
				if(distance<=nearbyDistance) {
					nearbyContact = contact;
					nearbyDistance = distance;
				}
			}
			return nearbyContact;
		} else return null;
	}
	public List<Contact> searchContactIn(Rectangle rectangle) { return searchContactIn(rectangle, Contact.class); }
	public List<Contact> searchContactIn(Rectangle rectangle, Class<? extends Contact> cls) {
		List<Contact> contacts = new LinkedList<Contact>();
		for(Component component:getAllComponents())
			if(component instanceof Sociable) {
				Point location = component.getLocation();
				for(Contact contact:((Sociable)component).getContacts())
					if(cls.isAssignableFrom(contact.getClass())
							&&rectangle.contains(addPoints(location, contact.getLocation())))
						contacts.add(0, contact);
			}
		Collections.sort(contacts, new Comparator<Contact>() {
			@Override
			public int compare(Contact contact_a, Contact contact_b) {
				return (int) addPoints(contact_a.getComponent().getLocation(), contact_a.getLocation())
						.distance(addPoints(contact_b.getComponent().getLocation(), contact_b.getLocation()));
			}
		});
		return contacts;
	}

	public Wire searchWireAt(Point point) {
		List<Wire> wires = searchWiresAt(point);
		if(!wires.isEmpty())
			return wires.get(0);
		else return null;
	}
	public List<Wire> searchWiresAt(Point point) {
		List<Wire> wires = new LinkedList<Wire>();
		for(Component component:getAllComponents())
			if(component instanceof Sociable)
				for(Contact contact:((Sociable)component).getContacts())
					for(Wire wire:contact.getWires())
						if(wire.containsLocation(point))
							wires.add(wire);
		return wires;
	}

	public double getZoom() { return zoom; }
	public void setZoom(double zoom) {
		if(zoom>8||zoom<0.25)
			return;
		this.zoom = zoom;
		transform.setToScale(zoom, zoom);
		revalidate(); repaint();
	}
	public Point applyZoom(Point point) {
		return new Point((int)(point.x/this.zoom),
				(int)(point.y/this.zoom));
	}
	public Dimension applyZoom(Dimension dimension) {
		return new Dimension((int)(dimension.width*this.zoom),
				(int)(dimension.height*this.zoom));
	}

	public void setOscilloscope(SimulationOscilloscope oscilloscope) { this.oscilloscope = oscilloscope; }
	public SimulationOscilloscope getOscilloscope() { return this.oscilloscope; }

	public boolean isGridVisible() { return gridVisible; }
	public void setGridVisible(boolean gridVisible) { this.gridVisible = gridVisible; repaint(); }

	@Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize();	}
	@Override public int getScrollableUnitIncrement(java.awt.Rectangle rectangle, int left, int top) { return GRID_STEPS/2; }
	@Override public int getScrollableBlockIncrement(java.awt.Rectangle rectangle, int left, int top) { return getScrollableUnitIncrement(rectangle, left, top)*10;	}
	@Override public boolean getScrollableTracksViewportHeight() { return false; }
	@Override public boolean getScrollableTracksViewportWidth() { return false; }

	@Override public int print(Graphics default_graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		List<Image> images = getPrintPages(pageFormat);
		if(pageIndex>=images.size())
			return NO_SUCH_PAGE;
		Graphics2D graphics = (Graphics2D)default_graphics.create();
		graphics.translate(pageFormat.getImageableX(),
				pageFormat.getImageableY());
		graphics.drawImage(images.get(pageIndex), 0, 0, null);
		Thread.yield(); //yield shortly, so that the image is painted
		return PAGE_EXISTS;
	}

	public boolean isSimulating() { return timer!=null; }
	public void stopSimulating() { if(isSimulating()) timer.cancel(); timer = null; }
	public void startSimulating() {
		if(isSimulating())
			return;
		this.timer = new Timer(true);
		timer.scheduleAtFixedRate(new TimerTask() {
			private int repaintDelay = 0;
			@Override public void run() {
				doSimulationStep();
				SimulationOscilloscope oscilloscope = getOscilloscope();
				if(oscilloscope!=null)
					oscilloscope.gaugeVoltage(Simulation.this);
				if(++repaintDelay>=5) {
					repaintDelay = 0;
					repaint();
				}
			}
		}, 0, 10);
	}
	public void doSimulationStep() {
		for(Component component:data.components)
			if(component instanceof Sociable)
				for(Contact contact:((Sociable)component).getContacts())
					for(Wire wire:contact.getWires())
						wire.calculate();
		for(Component component:data.components)
			component.calculate();
	}

	private List<Image> getPrintPages(PageFormat pageFormat) {
		double zoom = getZoom(); setZoom(1d);
		boolean doubleBuffered = false;
		if(doubleBuffered=this.isDoubleBuffered())
			this.setDoubleBuffered(false);

		Dimension size = getPreferredSize(), imagable = new Dimension((int)pageFormat.getImageableWidth(),
				(int)pageFormat.getImageableHeight());
		Image image = createImage(size.width, size.height);
		Graphics image_graphics = image.getGraphics();
		image_graphics.setColor(Color.WHITE);
		image_graphics.fillRect(0, 0, size.width, size.height);
		this.paint(image_graphics);

		setZoom(zoom);
		if(doubleBuffered)
			this.setDoubleBuffered(doubleBuffered);

		List<Image> images = new Vector<Image>();
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(image, 0);
		try { tracker.waitForID(0); }
		catch (InterruptedException e) {
			//nothing to do here
		}

		int rows = (int)Math.ceil((double)size.height/imagable.height);
		for(int pageIndex=0;;pageIndex++) {
			int column = pageIndex/rows,
					row = pageIndex-(pageIndex/rows)*rows;
			Point position = new Point(row*imagable.height, column*imagable.width);
			if(position.y>size.width) {
				try { tracker.waitForAll(); }
				catch (InterruptedException e) {
					//nothing to do here
				}
				return images;
			} else if(searchComponentsIn(new Rectangle(position, imagable)).size()<=0)
				continue;
			Image imagePart = createImage(imagable.width, imagable.height);
			imagePart.getGraphics().drawImage(image, 0, 0, imagable.width, imagable.height,
					position.x, position.y, position.x+imagable.width,
					position.y+imagable.height, null);
			images.add(imagePart); tracker.addImage(imagePart, pageIndex+1);
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		throw new NotSerializableException("Only the model of a simulation is serializable (properties and data).");
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		throw new NotSerializableException("You may only deserialize a the model of a simulation (properties and data) to pass it to the prepared constuctor.");
	}

	public final void writeSimulation(OutputStream out) throws IOException {
		try {
			writeSimulation(out, properties.getHash());
		} catch(GeneralSecurityException e_a) {
			//assume that when this method is called (without an explicit hash) the encryption is rather obligatory
			try { writeSimulation(out, null); }
			catch (GeneralSecurityException e_b) {}
		}
	}
	public final void writeSimulation(OutputStream out, String hash) throws IOException, GeneralSecurityException {
		if(out==null)
			throw new IOException("Please specify an valid OutputStream to write the simulation to.");
		ObjectOutputStream objectOut;
		if(hash==null||hash.isEmpty())
			objectOut = new ObjectOutputStream(out);
		else objectOut = new ObjectOutputStream(new CipherOutputStream(out, Utilities.createCipher(hash, Cipher.ENCRYPT_MODE)));
		objectOut.writeObject(properties);
		objectOut.writeObject(data);
		objectOut.flush(); objectOut.close();
	}

	public final static Simulation readSimulation(InputStream in) throws IOException, ClassNotFoundException, PasswordRequiredException {
		try { return readSimulation(in, null); }
		catch(PasswordRequiredException e) { throw e; }
		catch(GeneralSecurityException e) { throw new IOException(e); }
	}
	public final static Simulation readSimulation(InputStream in, String password) throws IOException, ClassNotFoundException, GeneralSecurityException {
		Object objectProperties, objectData;
		if(in==null)
			throw new IOException("Please specify an valid InputStream to read the simulation from.");
		try {
			ObjectInputStream objectIn = new LegacyObjectInputStream(in);
			try {
				objectProperties = objectIn.readObject();
				objectData = objectIn.readObject();
			} finally { objectIn.close(); }
		} catch(StreamCorruptedException e) { //cypher required or corrupted input
			if(password==null||password.isEmpty())
				throw new PasswordRequiredException();
			ObjectInputStream objectIn = new LegacyObjectInputStream(new CipherInputStream(in, Utilities.createCipher(Utilities.computeHash(password), Cipher.DECRYPT_MODE)));
			try {
				objectProperties = objectIn.readObject();
				objectData = objectIn.readObject();
			} finally { objectIn.close(); }
		}
		if(!(objectProperties instanceof SimulationProperies))
			throw new ClassNotFoundException();
		if(!(objectData instanceof SimulationData))
			objectData = null;
		return new Simulation((SimulationProperies)objectProperties, (SimulationData)objectData);
	}

	public static class SimulationProperies implements Serializable {
		private static final long serialVersionUID = 1l;

		public AuthorDescription author = new AuthorDescription();
		public CircuitDescription circuit = new CircuitDescription();
		private String hash;

		public class AuthorDescription implements Serializable {
			private static final long serialVersionUID = 1l;
			public String name;
			public String mail;
		}
		public class CircuitDescription implements Serializable {
			private static final long serialVersionUID = 1l;
			public String name;
			public String description;
			public String version;
		}

		public String getHash() { return hash; }
		public boolean hasPassword() { return hash!=null&&!hash.isEmpty(); }
		public boolean checkPassword(String password) {
			if(!hasPassword()||this.hash==null||this.hash.isEmpty())
				return true;
			else if(password==null)
				return false;
			return (this.hash.equals(computeHash(password))||
					this.hash.equals(Integer.toString(password.hashCode())));
		}
		public boolean removePassword() { return setPassword(null); }
		public boolean setPassword(String password) {
			if(hasPassword())
				return false;
			this.hash = computeHash(password);
			return true;
		}
		public boolean removePassword(String old_password) { return setPassword(old_password, null); }
		public boolean setPassword(String old_password, String password) {
			if(!checkPassword(old_password))
				return false;
			this.hash = computeHash(password);
			return true;
		}
	}

	public static class SimulationData implements Serializable {
		private static final long serialVersionUID = 1l;

		public List<Component> components;

		public SimulationData() {
			components = Collections.synchronizedList(new ArrayList<Component>());
		}
	}

	public final static class Clipboard {
		private Set<Component> content;
		private Set<Component> getContent() { return content; }
		private void setContent(Set<Component> content) { this.content = content; }
		private boolean hasContent() { return this.content!=null&&content.size()!=0; }
	}

	private Application getApplication() {
		Container parent = this.getTopLevelAncestor();
		if(parent instanceof Application)
			return (Application)parent;
		parent = this.getParent();
		while((parent=parent.getParent())!=null)
			if(parent instanceof Application)
				break;
		return parent!=null?(Application)parent:null;
	}
	private SimulationFrame getSimulationFrame() {
		Container parent = this.getFocusCycleRootAncestor();
		if(parent instanceof SimulationFrame)
			return (SimulationFrame)parent;
		parent = this.getParent();
		while((parent=parent.getParent())!=null)
			if(parent instanceof SimulationFrame)
				break;
		return parent!=null?(SimulationFrame)parent:null;
	}

	public static interface ChangeListener extends EventListener { void changed(ChangeEvent event); }
	public interface ChangeEvent{
		public void undo();
		public void redo();
		public boolean join(ChangeEvent event);
	}
	public abstract class ComponentEvent implements ChangeEvent {
		protected final List<? extends Component> components;
		public ComponentEvent(Collection<? extends Component> components) {
			if(!(components instanceof List))
				components = new ArrayList<Component>(components);
			this.components = Collections.unmodifiableList((List<? extends Component>)components);
		}
		public ComponentEvent(Component component) {
			List<Component> components = new ArrayList<Component>();
			components.add(component);
			this.components = Collections.unmodifiableList(components);
		}
	}
	public class ComponentAdded extends ComponentEvent {
		public ComponentAdded(Component component) { super(component); }
		public ComponentAdded(Collection<? extends Component> components) { super(components); }
		@Override public void undo() { Simulation.this.removeComponents(this.components); }
		@Override public void redo() { Simulation.this.addComponents(this.components); }
		@Override public boolean join(ChangeEvent event) { return false; }
	}
	public class ComponentRemoved extends ComponentEvent {
		public ComponentRemoved(Component component) { super(component); }
		public ComponentRemoved(Collection<? extends Component> components) { super(components); }
		@Override public void undo() { Simulation.this.addComponents(this.components); }
		@Override public void redo() { Simulation.this.removeComponents(this.components); }
		@Override public boolean join(ChangeEvent event) { return false; }
	}
	public class ComponentMoved extends ComponentEvent {
		private final Point from, to;
		public ComponentMoved(Component component, Point from, Point to) { super(component); this.from = from; this.to = to; }
		public ComponentMoved(Collection<? extends Component> components, Point from, Point to) { super(components); this.from = from; this.to = to; }
		@Override public void undo() { Simulation.this.moveComponents(this.components, to, from); }
		@Override public void redo() { Simulation.this.moveComponents(this.components, from, to); }
		@Override public boolean join(ChangeEvent event) {
			if(!(event instanceof ComponentMoved))
				return false;
			ComponentMoved moveEvent = (ComponentMoved)event;
			if(!components.equals(moveEvent.components))
				return false;
			Point relative = new Point(moveEvent.to.x-moveEvent.from.x, moveEvent.to.y-moveEvent.from.y);
			to.translate(relative.x, relative.y);
			return true;
		}
	}
}
