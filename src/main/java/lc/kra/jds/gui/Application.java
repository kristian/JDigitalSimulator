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
package lc.kra.jds.gui;

import static lc.kra.jds.Utilities.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;

import lc.kra.jds.Simulation;
import lc.kra.jds.SimulationOscilloscope;
import lc.kra.jds.Utilities;
import lc.kra.jds.Simulation.ChangeEvent;
import lc.kra.jds.Simulation.Clipboard;
import lc.kra.jds.Simulation.Layer;
import lc.kra.jds.Utilities.RememberFileChooser;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Component.ComponentAttributes;
import lc.kra.jds.components.Component.ComponentFlavor;
import lc.kra.jds.components.Component.HiddenComponent;
import lc.kra.jds.exceptions.PasswordRequiredException;

/**
 * JDigitalSimulator
 * @author Kristian Kraljic
 * @version 2.5.2
 */
public class Application extends JFrame {
	private static final long serialVersionUID = -4693271310855486553L;

	public static final String FILE_EXTENSION = "jdsim";
	public static File pluginDirectory, currentDirectory;

	private static final String VERSION = "2.5.2", COPYRIGHT = "2010-2024", LINES_OF_CODE = "9.509", WORDS_OF_CODE = "36.133", PAGES_OF_CODE = "245";

	private static final String[]
		TOOLBAR_FRAME_FOCUS = new String[]{"save", "print", "print_level", "simulate", "left", "right", "up", "down", "grid", "secure", "zoom_default", "zoom", "zoom_in", "zoom_out"},
		TOOLBAR_COMPONENT_FOCUS = new String[]{"front", "back"},
		MENUBAR_FRAME_FOCUS = new String[]{"file.close", "file.save", "file.save_as", "file.print", "file.print_level", "edit.paste", "edit.select_all"},
		MENUBAR_COMPONENT_FOCUS = new String[]{"edit.cut", "edit.copy", "edit.delete", "edit.front", "edit.back"};

	private static final Set<Application> applications = new HashSet<Application>(1);

	private Map<String, HashMap<ComponentAttributes, Class<? extends Component>>> componentGroups;
	private List<ComponentButton> componentButtons;
	private List<CollapsiblePanel> collapsiblePanels;

	private JToolBar toolbar;
	private SimulationDesktop desktop;
	private SimulationOscilloscope oscilloscope;
	private Clipboard clipboard;

	public Application() {
		clipboard = new Clipboard();
		componentGroups = new HashMap<String, HashMap<ComponentAttributes, Class<? extends Component>>>();
		componentButtons = new Vector<ComponentButton>(); collapsiblePanels = new Vector<CollapsiblePanel>();
		loadBuildinComponents(); loadPluginComponents();
		this.configureGlassPane();
		this.setJMenuBar(createMenuBar());
		this.add(toolbar=createToolBar(), BorderLayout.NORTH);
		this.setSize(new Dimension(1000, 600));
		this.setLocationByPlatform(true);
		if(!hasConfiguration(CONFIGURATION_WINDOW_MAXIMIZED)||!Boolean.parseBoolean(getConfiguration(CONFIGURATION_WINDOW_MAXIMIZED))) {
			if(hasConfiguration(CONFIGURATION_WINDOW_SIZE)) {
				String[] size = getConfiguration(CONFIGURATION_WINDOW_SIZE).split(", ", 3);
				if(size.length==2&&Utilities.isNumeric(size[0])&&Utilities.isNumeric(size[1]))
					this.setSize(new Dimension(Integer.parseInt(size[0]), Integer.parseInt(size[1])));
			}
			if(hasConfiguration(CONFIGURATION_WINDOW_LOCATION)) {
				String[] location = getConfiguration(CONFIGURATION_WINDOW_LOCATION).split(", ", 3);
				if(location.length==2&&Utilities.isNumeric(location[0])&&Utilities.isNumeric(location[1]))
					this.setLocation(Integer.parseInt(location[0]), Integer.parseInt(location[1]));
			}
		} else this.setExtendedState(MAXIMIZED_BOTH);
		this.add(createSplitPane(JSplitPane.VERTICAL_SPLIT, Guitilities.getActualSize(this).height-200, true, 0.95d, createSplitPane(JSplitPane.HORIZONTAL_SPLIT, 200, true, 0.1d, createSidePanel(), new JScrollPane(desktop=createDesktopPanel())), new JScrollPane(oscilloscope=createOscilloscope())), BorderLayout.CENTER);
		this.setToolBarButtonHeight(40);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setIconImage(Guitilities.createImageIcon("icon.png").getImage());
		this.setTitle("JDigitalSimulator "+getTranslation("translation.title"));
		this.addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent event) { exitApplication(); }
		});
		this.setVisible(true);
		applications.add(this);
	}

	private void configureGlassPane() {
		JPanel panel = (JPanel) getGlassPane();
		panel.setVisible(true);
		panel.setLayout(null);
	}

	private JSplitPane createSplitPane(int orientation, int location, boolean continuous, double resizeWeight, java.awt.Component component_a, java.awt.Component component_b) {
		JSplitPane split = new JSplitPane(orientation, continuous, component_a, component_b);
		split.setResizeWeight(resizeWeight);
		split.setDividerLocation(location);
		split.setOneTouchExpandable(true);
		return split;
	}

	private void loadBuildinComponents() {
		loadComponents("lc.kra.jds.components");
		if(componentGroups.isEmpty())
			JOptionPane.showMessageDialog(Application.this, getTranslation("components.load.error"), getTranslation("components.load.error", TranslationType.TITLE), JOptionPane.WARNING_MESSAGE);
	}
	private void loadPluginComponents() {
		if(pluginDirectory==null||!pluginDirectory.exists())
			pluginDirectory = Utilities.getFile(Utilities.getLocalPath());
		while(pluginDirectory!=null&&pluginDirectory.exists()&&!pluginDirectory.isDirectory())
			pluginDirectory = pluginDirectory.getParentFile();
		if(pluginDirectory==null||!pluginDirectory.exists()) return;
		File[] files = pluginDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) { return name.equalsIgnoreCase("plugins"); }
		});
		if(files!=null&&files.length>=1) {
			Utilities.initLegacyClassLoader(files[0]);
			loadComponents(files[0], null);
		}
	}

	@SuppressWarnings("unused")
	private void loadComponents() { loadComponents(null); }
	private void loadComponents(String checkPackage) { loadComponents(Utilities.getLocalPath(), checkPackage); }
	private void loadComponents(URL ressource, String checkPackage) {
		if(ressource.getProtocol().equals("jar"))
			try { loadComponents(new JarFile(Utilities.getFile(ressource)), checkPackage); }
		catch(Throwable t) { }
		else loadComponents(Utilities.getFile(ressource), checkPackage);
	}
	private void loadComponents(File likelyFile, String checkPackage) { loadComponents(likelyFile, checkPackage, null); }
	private void loadComponents(File likelyFile, String checkPackage, String currentPackage) {
		if(likelyFile.isDirectory())
			for(File file:likelyFile.listFiles())
				if(file.isDirectory())
					 loadComponents(file, checkPackage, currentPackage!=null&&!currentPackage.isEmpty()?currentPackage+'.'+file.getName():file.getName());
				else loadComponents(file, checkPackage, currentPackage);
		else if(likelyFile.getName().matches("asm.*\\.jar")) {}
		else try { loadComponents(new JarFile(likelyFile), checkPackage); }
		catch(Throwable t) {
			try { loadComponent(likelyFile.getName(), checkPackage, currentPackage); }
			catch(Throwable t_a) {
				try { loadComponent(Utilities.getSimpleClassLoader().loadClass(likelyFile)); }
				catch(Throwable t_b) {
					if (Utilities.hasLegacyClassLoader()) {
						try { loadComponent(Utilities.getLegacyClassLoader().loadClass(likelyFile)); }
						catch(Throwable t_c) { t_c.printStackTrace(); } //that was our last trick... nothing to do here anymore
					}
				}
			}
		}
	}
	private void loadComponents(final JarFile file, String checkPackage) {
		Enumeration<JarEntry> enumeration = file.entries();
		while(enumeration.hasMoreElements()) {
			final JarEntry entry = enumeration.nextElement();
			try { loadComponent(entry.getName(), checkPackage); }
			catch (Throwable t) {
				try { loadComponent(Utilities.getSimpleClassLoader().loadClass(file, entry)); }
				catch(Throwable t_a) {
					if (Utilities.hasLegacyClassLoader()) {
						try { loadComponent(Utilities.getLegacyClassLoader().loadClass(file, entry)); }
						catch(Throwable t_b) { t_b.printStackTrace(); } //that was our last trick... nothing to do here anymore
					}
				}
			}
		}
	}

	private boolean loadComponent(String name, String checkCurrentPackage) throws ClassNotFoundException { return loadComponent(name, checkCurrentPackage, checkCurrentPackage); }
	private boolean loadComponent(String name, String checkPackage, String currentPackage) throws ClassNotFoundException {
		if(name.indexOf('/')<0) {
			if(currentPackage!=null) name = currentPackage.isEmpty()?name:currentPackage+'.'+name;
			else if(checkPackage  !=null) name = checkPackage.isEmpty()  ?name:checkPackage+'.'+name;
		} else name = name.replace('/', '.');
		if((checkPackage==null||name.startsWith(checkPackage))&&name.endsWith(".class"))
			return loadComponent(Class.forName(name.substring(0, name.length()-6)));
		return false;
	}
	@SuppressWarnings("unchecked")
	private boolean loadComponent(Class<?> cls) {
		if(cls.isAnnotationPresent(HiddenComponent.class)
				|| Modifier.isAbstract(cls.getModifiers())
				||!Component.class.isAssignableFrom(cls)
				|| cls.isLocalClass() || cls.isMemberClass() || cls.isAnonymousClass())
			return false;
		for(Class<?> superCls=cls;;superCls=superCls.getSuperclass())
			if(superCls.equals(Component.class)) break;
			else if(superCls.equals(Object.class)) return false;
		Class<? extends Component> componentCls = null;
		try { componentCls = (Class<? extends Component>) cls; }
		catch(ClassCastException e) { return false; }
		ComponentAttributes attributes = Component.getAttributes(componentCls);
		HashMap<ComponentAttributes, Class<? extends Component>> component_group;
		if(!componentGroups.containsKey(attributes.group)) {
			component_group = new HashMap<ComponentAttributes, Class<? extends Component>>();
			componentGroups.put(attributes.group, component_group);
		} else component_group = componentGroups.get(attributes.group);
		component_group.put(attributes, componentCls);
		return true;
	}

	private JPanel createSidePanel() {
		JPanel side = new JPanel(new BorderLayout());
		side.add(createTitlePanel(), BorderLayout.NORTH);
		side.add(Guitilities.createScrollPane(createCatalogPanel(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, 14, 0), BorderLayout.CENTER);
		return side;
	}

	private JPanel createTitlePanel() {
		JPanel title = new JPanel(new BorderLayout());
		title.add(Guitilities.createGradientTitle(getTranslation("components.title")), BorderLayout.NORTH);

		JPanel interact = new JPanel();
		interact.setBackground(UIManager.getColor(Guitilities.SUB_PANEL_BACKGROUND));
		interact.setLayout(new BoxLayout(interact, BoxLayout.X_AXIS));
		interact.setBorder(new CompoundBorder(Guitilities.CHISEL_BORDER, new EmptyBorder(12, 8, 12, 8)));
		final JCheckBox checkImages = new JCheckBox(getTranslation("group.images"), true);
		checkImages.setBackground(UIManager.getColor(Guitilities.SUB_PANEL_BACKGROUND));
		checkImages.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				for(ComponentButton button:componentButtons)
					button.setIconVisible(checkImages.isSelected());
				for(CollapsiblePanel panel:collapsiblePanels)
					panel.setExpanded(false);
			}
		});
		interact.add(checkImages);
		title.add(interact, BorderLayout.CENTER);
		return title;
	}

	private JPanel createCatalogPanel() {
		JPanel selector = new JPanel();

		Icon expandedIcon = new ArrowIcon(ArrowIcon.SOUTH, UIManager.getColor(Guitilities.TITLE_FOREGROUND));
		Icon collapsedIcon = new ArrowIcon(ArrowIcon.EAST, UIManager.getColor(Guitilities.TITLE_FOREGROUND));

		GridBagLayout gridbag = new GridBagLayout();
		selector.setLayout(gridbag);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = constraints.gridy = 0; constraints.weightx = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;

		GridBagConstraints categoryConstraints = new GridBagConstraints();
		categoryConstraints.gridx = categoryConstraints.gridy = 0; categoryConstraints.weightx = 1;
		categoryConstraints.fill = GridBagConstraints.HORIZONTAL;

		CollapsiblePanel collapsible;
		for(Entry<String, HashMap<ComponentAttributes, Class<? extends Component>>> group_entry:componentGroups.entrySet()) {
			JPanel category = new JPanel();
			GridBagLayout categoryGridbag = new GridBagLayout();
			category.setLayout(categoryGridbag);

			collapsible = new CollapsiblePanel(category, getTranslation(group_entry.getKey()), getTranslation("group.expand"));
			collapsible.setBorder(Guitilities.CATEGORY_BORDER);
			collapsible.setFont(UIManager.getFont("CheckBox.font").deriveFont(Font.BOLD));
			collapsible.setForeground(UIManager.getColor(Guitilities.TITLE_FOREGROUND));
			collapsible.setExpandedIcon(expandedIcon);
			collapsible.setCollapsedIcon(collapsedIcon);
			collapsible.setExpanded(false);
			collapsiblePanels.add(collapsible);

			gridbag.addLayoutComponent(collapsible, constraints);
			selector.add(collapsible);
			constraints.gridy++;

			for(Entry<ComponentAttributes, Class<? extends Component>> entry:group_entry.getValue().entrySet())
				try {
					ComponentButton button = new ComponentButton(entry.getKey(), entry.getValue());
					categoryGridbag.addLayoutComponent(button, categoryConstraints);
					categoryConstraints.gridy++;
					category.add(button);
					componentButtons.add(button);
				} catch (Exception e) { e.printStackTrace(); }
		}

		JPanel trailer = new JPanel();
		constraints.weighty = 1.0;
		gridbag.addLayoutComponent(trailer, constraints);
		selector.add(trailer);

		return selector;
	}

	private SimulationDesktop createDesktopPanel() {
		final SimulationDesktop desktop = new SimulationDesktop();
		desktop.setAutoscrolls(true);
		desktop.addContainerListener(new ContainerListener() {
			@Override public void componentRemoved(ContainerEvent event) { revalidate(); }
			@Override public void componentAdded(ContainerEvent event) { revalidate(); }
		});
		return desktop;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu(getTranslation("menubar.file"));
		file.add(createMenuItem("file.new", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { newWorksheet(); }
		}));
		file.add(createMenuItem("file.open", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { openWorksheet(); }
		}));
		file.add(createMenuItem("file.close", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				try { desktop.getSelectedFrame().setClosed(true); }
				catch (PropertyVetoException e) { }
			}
		}));
		file.add(createMenuItem("file.save", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { saveWorksheet(); }
		}));
		file.add(createMenuItem("file.save_as", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { saveWorksheetAs(); }
		}));
		file.add(new JSeparator());
		file.add(createMenuItem("file.print", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { printWorksheet(); }
		}));
		file.add(createMenuItem("file.print_level", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { printWorksheetLevel(); }
		}));
		file.add(new JSeparator());
		file.add(createMenuItem("file.exit", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { exitApplication(); }
		}));
		menubar.add(file);

		JMenu edit = new JMenu(getTranslation("menubar.edit"));
		edit.add(createMenuItem("edit.undo", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				getActiveSimulation().undoChange();
				revalidate();
			}
		}));
		edit.add(createMenuItem("edit.redo", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				getActiveSimulation().redoChange();
				revalidate();
			}
		}));
		edit.add(new JSeparator());
		edit.add(createMenuItem("edit.cut", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				try { getActiveSimulation().cutIntoClipboard(); }
				catch (CloneNotSupportedException e) { }
				revalidate();
			}
		}));
		edit.add(createMenuItem("edit.copy", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				try { getActiveSimulation().copyToClipboard(); }
				catch (CloneNotSupportedException e) { }
				revalidate();
			}
		}));
		edit.add(createMenuItem("edit.paste", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				try { getActiveSimulation().pasteFromClipboard(); }
				catch (CloneNotSupportedException e) { }
				revalidate();
			}
		}));
		edit.add(new JSeparator());
		edit.add(createMenuItem("edit.delete", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.removeComponents(simulation.getSelectedComponents());
				revalidate();
			}
		}));
		edit.add(createMenuItem("edit.select_all", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setSelectedComponents(simulation.getAllComponents());
				revalidate();
			}
		}));
		edit.add(new JSeparator());
		edit.add(createMenuItem("edit.front", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setComponentLayers(simulation.getSelectedComponents(), Layer.TOPMOST);
				revalidate();
			}
		}));
		edit.add(createMenuItem("edit.back", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setComponentLayers(simulation.getSelectedComponents(), Layer.BOTTOMMOST);
				revalidate();
			}
		}));
		menubar.add(edit);

		JMenu properties = new JMenu(getTranslation("menubar.properties"));
		properties.add(createMenuItem("properties.worksheet", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				final SimulationFrame frame = desktop.getSelectedFrame();
				final Simulation simulation = frame.getSimulation();
				final Simulation.SimulationProperies properties = simulation.properties;
				String fileName = getTranslation("properties.worksheet", TranslationType.TITLE, frame.getFileName());
				final JDialog dialog = Guitilities.createDialog(Application.this, fileName);

				JPanel centerPane = new JPanel();
				centerPane.setBorder(Guitilities.LARGE_EMPTY_BORDER);
				centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
				dialog.add(centerPane, BorderLayout.CENTER);

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.author")));
				JPanel authorPane = new JPanel(new GridBagLayout());
				final JTextField authorName = Guitilities.addGridPairLine(authorPane, 0, new JLabel(getTranslation("properties.author.name")), new JTextField(properties.author.name));
				final JTextField authorMail = Guitilities.addGridPairLine(authorPane, 1, new JLabel(getTranslation("properties.author.mail")), new JTextField(properties.author.mail));
				centerPane.add(authorPane);

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.circuit")));
				JPanel circuitPane = new JPanel(new GridBagLayout());
				final JTextField         circuitName = Guitilities.addGridPairLine(circuitPane, 0, new JLabel(getTranslation("properties.circuit.name")), new JTextField(properties.circuit.name));
				Guitilities.pair_left_constraint.anchor = GridBagConstraints.FIRST_LINE_END;
				final JScrollPane circuitDescription = Guitilities.addGridPairLine(circuitPane, 1, new JLabel(getTranslation("properties.circuit.description")), new JScrollPane(new JTextArea(properties.circuit.description, 6, 0)));
				final JTextField      circuitVersion = Guitilities.addGridPairLine(circuitPane, 2, new JLabel(getTranslation("properties.circuit.version")), new JTextField(properties.circuit.version));
				centerPane.add(circuitPane);

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.worksheet")));
				JPanel worksheetPane = new JPanel(new GridBagLayout());
				final JSpinner  worksheetWidth = Guitilities.addGridPairLine(worksheetPane, 0, new JLabel(getTranslation("properties.worksheet.width")), new JSpinner(new SpinnerNumberModel((properties.size != null ? properties.size : Simulation.DEFAULT_SIZE).getWidth(), 100, Short.MAX_VALUE, 100)), new JLabel(getTranslation("properties.worksheet.pixels")));
				final JSpinner worksheetHeight = Guitilities.addGridPairLine(worksheetPane, 1, new JLabel(getTranslation("properties.worksheet.height")), new JSpinner(new SpinnerNumberModel((properties.size != null ? properties.size : Simulation.DEFAULT_SIZE).getHeight(), 100, Short.MAX_VALUE, 100)), new JLabel(getTranslation("properties.worksheet.pixels")));
				centerPane.add(worksheetPane);

				JPanel bottomPane = Guitilities.createGradientFooter();
				bottomPane.add(Guitilities.createButton(getTranslation("properties.secure"), new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { getToolBarButton("secure").getActionListeners()[0].actionPerformed(event); }
				}));
				bottomPane.add(new JSeparator(JSeparator.VERTICAL));
				final ActionListener cancelListener = new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { dialog.dispose(); }
				}, applyListener = new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						properties.author.name = authorName.getText();
						properties.author.mail = authorMail.getText();
						properties.circuit.name = circuitName.getText();
						properties.circuit.description = ((JTextArea)circuitDescription.getViewport().getComponent(0)).getText();
						properties.circuit.version = circuitVersion.getText();
						properties.size.setSize((double) worksheetWidth.getValue(), (double) worksheetHeight.getValue());
						simulation.setPreferredSize(properties.size); if (simulation.getParent() != null) simulation.getParent().revalidate();
					}
				};
				bottomPane.add(Guitilities.createButton(getTranslation("properties.okay"), new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						applyListener.actionPerformed(event);
						cancelListener.actionPerformed(event);
					}
				}));
				bottomPane.add(Guitilities.createButton(getTranslation("properties.cancel"), cancelListener));
				bottomPane.add(Guitilities.createButton(getTranslation("properties.apply"), applyListener));
				dialog.add(bottomPane, BorderLayout.SOUTH);

				dialog.pack(); dialog.setSize(new Dimension(500, dialog.getHeight()));
				dialog.setVisible(true);
			}
		}));
		properties.add(new JSeparator());
		properties.add(createMenuItem("properties.general", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				final JDialog dialog = Guitilities.createDialog(Application.this, getTranslation("properties.general", TranslationType.TITLE));

				JPanel centerPane = new JPanel();
				centerPane.setBorder(Guitilities.LARGE_EMPTY_BORDER);
				centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
				dialog.add(centerPane, BorderLayout.CENTER);

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.localization")));
				JPanel localizationPane = new JPanel(new GridBagLayout());
				final JComboBox<String> localizationLanguage = Guitilities.addGridPairLine(localizationPane, 0, new JLabel(getTranslation("properties.localization.language")), new JComboBox<String>());
				for(Locale locale:Utilities.SUPPORTED_LOCALES)
					localizationLanguage.addItem(locale.getDisplayLanguage());
				localizationLanguage.setSelectedItem(Utilities.getCurrentLocale().getDisplayLanguage());
				centerPane.add(localizationPane);

				centerPane.add(Box.createVerticalStrut(20));

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.lookandfeel")));
				JPanel lookAndFeelPane = new JPanel(new GridBagLayout());
				final JComboBox<String> lookAndFeel = Guitilities.addGridPairLine(lookAndFeelPane, 0, new JLabel(getTranslation("properties.lookandfeel.name")), new JComboBox<String>());
				for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels())
					lookAndFeel.addItem(info.getName());
				lookAndFeel.setSelectedItem(UIManager.getLookAndFeel().getName());
				centerPane.add(lookAndFeelPane);

				centerPane.add(javax.swing.Box.createVerticalStrut(20));

				centerPane.add(Guitilities.createSeparator(getTranslation("properties.symbols")));
				javax.swing.JPanel symbolStandardPane = new javax.swing.JPanel(new java.awt.GridBagLayout());
				final javax.swing.JComboBox<String> symbolStandard = Guitilities.addGridPairLine(symbolStandardPane, 0, new javax.swing.JLabel(getTranslation("properties.symbols.name")), new javax.swing.JComboBox<String>());
				symbolStandard.addItem("IEC 60617-12 : 1997");
				symbolStandard.addItem("ANSI/IEEE Std 91/91a-1991");
				symbolStandard.setSelectedIndex(useAnsiSymbols() ? 1 : 0);
				centerPane.add(symbolStandardPane);

				JPanel bottomPane = Guitilities.createGradientFooter();
				final ActionListener cancelListener = new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { dialog.dispose(); }
				}, applyListener = new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						String displayName = localizationLanguage.getSelectedItem().toString();
						for(Locale locale:Utilities.SUPPORTED_LOCALES)
							if(displayName.equals(locale.getDisplayName()) && !locale.equals(Utilities.getCurrentLocale())) {
								Utilities.setCurrentLocale(locale);
								Utilities.setConfiguration(Utilities.CONFIGURATION_LOCALIZATION_LANGUAGE, locale.getLanguage());
								JOptionPane.showMessageDialog(dialog, getTranslation("properties.localization.info"));
							}
						String lookAndFeelName = lookAndFeel.getSelectedItem().toString();
						for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels())
							if(lookAndFeelName.equals(info.getName()) && !info.getName().equals(UIManager.getLookAndFeel().getName())) {
								Guitilities.setLookAndFeel(info.getClassName(), info.getName());
								Utilities.setConfiguration(Utilities.CONFIGURATION_LOOK_AND_FEEL_CLASS, info.getClassName());
								Utilities.setConfiguration(Utilities.CONFIGURATION_LOOK_AND_FEEL_NAME, info.getName());
								SwingUtilities.updateComponentTreeUI(dialog);
								SwingUtilities.updateComponentTreeUI(Application.this);
							}
						boolean useAnsiSymbols = symbolStandard.getSelectedIndex() == 1;
						if (useAnsiSymbols != useAnsiSymbols()) {
							setUseAnsiSymbols(useAnsiSymbols);
							Utilities.setConfiguration(Utilities.CONFIGURATION_ANSI_SYMBOLS, Boolean.toString(useAnsiSymbols));
							for(ComponentButton button:componentButtons)
								button.refreshIcon();
						}
						revalidate();
						repaint();
					}
				};
				bottomPane.add(Guitilities.createButton(getTranslation("properties.okay"), new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						applyListener.actionPerformed(event);
						cancelListener.actionPerformed(event);
					}
				}));
				bottomPane.add(Guitilities.createButton(getTranslation("properties.cancel"), cancelListener));
				bottomPane.add(Guitilities.createButton(getTranslation("properties.apply"), applyListener));
				dialog.add(bottomPane, BorderLayout.SOUTH);

				dialog.pack(); dialog.setSize(new Dimension(500, dialog.getHeight()));
				dialog.setVisible(true);
			}
		}));
		menubar.add(properties);

		JMenu display = new JMenu(getTranslation("menubar.display"));
		display.add(createMenuItem("display.zoom_default", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				getActiveSimulation().setZoom(1.0f);
			}
		}));
		display.add(createMenuItem("display.zoom_in", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setZoom(simulation.getZoom()*1.25);
			}
		}));
		display.add(createMenuItem("display.zoom_out", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setZoom(simulation.getZoom()/1.25);
			}
		}));
		display.add(new JSeparator());
		display.add(createMenuItem("display.fullscreen", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				GraphicsDevice device = getGraphicsConfiguration().getDevice();
				if(!device.isFullScreenSupported())
					JOptionPane.showMessageDialog(Application.this, getTranslation("fullscreen.not_supported"));
				JWindow window = (JWindow)device.getFullScreenWindow();
				if(window!=null) {
					device.setFullScreenWindow(null);
					Application.this.setContentPane(window.getContentPane());
					Application.this.toolbar.setFloatable(true);
					SwingUtilities.updateComponentTreeUI(Application.this);
				} else {
					window = new JWindow();
					window.setContentPane(Application.this.getContentPane());
					Application.this.toolbar.setFloatable(false);
					device.setFullScreenWindow(window);
				}
			}
		}));
		menubar.add(display);

		JMenu window = new JMenu(getTranslation("menubar.window"));
		window.add(createMenuItem("window.cascade", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { desktop.cascadeAllFrames(); }
		}));
		window.add(createMenuItem("window.tile", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) { desktop.tileAllFrames(); }
		}));
		menubar.add(window);

		JMenu help = new JMenu(getTranslation("menubar.help"));
		help.add(createMenuItem("help.about", new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				final JDialog dialog = Guitilities.createDialog(Application.this, getTranslation("about", TranslationType.TITLE));

				JPanel centerPane = new JPanel();
				centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
				final JLabel labelTitle, labelCopyright, labelUrl, labelDescription;
				centerPane.add(labelTitle=new JLabel("JDigitalSimulator ["+VERSION+"]"));
				centerPane.add(labelCopyright=new JLabel("Copyright "+COPYRIGHT+", Kristian Kraljic"));
				centerPane.add(labelUrl=new JLabel("http://kra.lc/projects/jdigitalsimulator/"));
				centerPane.add(labelDescription=Guitilities.createLabel("This software is a remake of Andreas Herz &quot;DigitalSimulator Version 5.57&quot; available at FreeGroup.de. The goal was to adapt the appearance and behaviour of the old application as best as possible, but build it, based on the newest object oriented standards and techniques. The whole application was effectively developed in one week by one person. "+LINES_OF_CODE+" lines of pure hand coded Java (without comments, no external libraries were used), "+WORDS_OF_CODE+" words, "+PAGES_OF_CODE+" pages of plain A4 paper."));
				labelTitle.setAlignmentX(JLabel.LEFT_ALIGNMENT);
				labelTitle.setFont(labelTitle.getFont().deriveFont(Font.BOLD));
				labelTitle.setBorder(Guitilities.MEDIUM_EMPTY_BORDER);
				labelCopyright.setBorder(new EmptyBorder(10, 20, 20, 0));
				labelUrl.addMouseListener(new MouseAdapter() {
					@Override public void mouseClicked(MouseEvent event) {
						try { Desktop.getDesktop().browse(new URI(labelUrl.getText())); }
						catch(Exception e) {}
					}
				});
				labelUrl.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 20, 0), Guitilities.TITLE_BORDER));
				Guitilities.setFittingLabelSize(labelDescription, 400);
				centerPane.add(Box.createVerticalStrut(10));
				centerPane.setBorder(Guitilities.HUGE_EMPTY_BORDER);
				dialog.add(centerPane, BorderLayout.CENTER);

				JPanel bottomPane = Guitilities.createGradientFooter();;
				bottomPane.add(Guitilities.createButton(getTranslation("about.donate"), new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { labelUrl.getMouseListeners()[0].mouseClicked(null); }
				}));
				bottomPane.add(Guitilities.createButton(getTranslation("about.close"), new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { dialog.dispose(); }
				}));
				dialog.add(bottomPane, BorderLayout.SOUTH);

				dialog.pack(); dialog.setSize(new Dimension(500, dialog.getHeight()));
				dialog.setVisible(true);
			}
		}));
		menubar.add(help);

		return menubar;
	}

	private JMenuItem createMenuItem(String key, ActionListener listener) {
		return Guitilities.createMenuItem(key, Guitilities.createImageIcon("images/menubar/"+key.replace('.', '-')+".png"), listener);
	}
	private JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar(getTranslation("toolbar"));
		toolbar.add(createToolBarButton("new", getMenuBarItem("file.new").getActionListeners()[0]));
		toolbar.add(createToolBarButton("open", getMenuBarItem("file.open").getActionListeners()[0]));
		toolbar.add(createToolBarButton("save", getMenuBarItem("file.save").getActionListeners()[0]));
		toolbar.add(createToolBarButton("exit", getMenuBarItem("file.exit").getActionListeners()[0]));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton("print", getMenuBarItem("file.print").getActionListeners()[0]));
		toolbar.add(createToolBarButton("print_level", getMenuBarItem("file.print_level").getActionListeners()[0]));
		toolbar.addSeparator();
		JToggleButton simulateButton = new JToggleButton(getTranslation("toolbar.simulate"));
		simulateButton.setFont(simulateButton.getFont().deriveFont(Font.BOLD, 14f));
		simulateButton.setToolTipText(getTranslation("toolbar.simulate", TranslationType.TOOLTIP));
		simulateButton.setActionCommand("simulate");
		simulateButton.setPreferredSize(new Dimension(simulateButton.getPreferredSize().width, 40));
		simulateButton.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				Simulation simulation = getActiveSimulation();
				if(((JToggleButton)e.getSource()).isSelected())
					simulation.startSimulating();
				else simulation.stopSimulating();
			}
		});
		toolbar.add(simulateButton);
		toolbar.addSeparator();
		class TraverseViewportActionListener implements ActionListener {
			private Point traverse;
			public TraverseViewportActionListener(Point traverse) { this.traverse = traverse; }
			@Override public void actionPerformed(ActionEvent event) {
				JViewport viewport = (JViewport) getActiveSimulation().getParent();
				Point position = Guitilities.addPoints(viewport.getViewPosition(), traverse);
				if(position.x<0) position.x = 0;
				if(position.y<0) position.y = 0;
				viewport.setViewPosition(position);
			}
		}
		toolbar.add(createToolBarButton("left", new TraverseViewportActionListener(new Point(-50, 0))));
		toolbar.add(createToolBarButton("right", new TraverseViewportActionListener(new Point(50, 0))));
		toolbar.add(createToolBarButton("up", new TraverseViewportActionListener(new Point(0, -50))));
		toolbar.add(createToolBarButton("down", new TraverseViewportActionListener(new Point(0, 50))));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton("front", getMenuBarItem("edit.front").getActionListeners()[0]));
		toolbar.add(createToolBarButton("back", getMenuBarItem("edit.back").getActionListeners()[0]));
		toolbar.add(createToolBarButton("grid", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Simulation simulation = getActiveSimulation();
				simulation.setGridVisible(!simulation.isGridVisible());
			}
		}));
		toolbar.add(createToolBarButton("secure", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Simulation.SimulationProperies properties = getActiveSimulation().properties;
				boolean hadPassword;
				if(hadPassword=properties.hasPassword())
					if(!properties.removePassword(Guitilities.showPasswordDialog(Application.this, getTranslation("password.enter.old"), getTranslation("password.enter", TranslationType.TITLE)))) {
						JOptionPane.showMessageDialog(Application.this, getTranslation("password.wrong"), null, JOptionPane.WARNING_MESSAGE);
						return;
					}
				properties.setPassword(Guitilities.showPasswordDialog(Application.this, getTranslation("password.enter.new"), getTranslation("password.enter", TranslationType.TITLE)));
				if(properties.hasPassword())
					JOptionPane.showMessageDialog(Application.this, getTranslation("password.changed"));
				else if(hadPassword&&!properties.hasPassword())
					JOptionPane.showMessageDialog(Application.this, getTranslation("password.removed"));
			}
		}));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton("zoom_default", getMenuBarItem("display.zoom_default").getActionListeners()[0]));
		toolbar.add(createToolBarButton("zoom_in", getMenuBarItem("display.zoom_in").getActionListeners()[0]));
		toolbar.add(createToolBarButton("zoom_out", getMenuBarItem("display.zoom_out").getActionListeners()[0]));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton("fullscreen", getMenuBarItem("display.fullscreen").getActionListeners()[0]));
		toolbar.addSeparator();
		toolbar.add(createToolBarButton("properties", getMenuBarItem("properties.worksheet").getActionListeners()[0]));
		return toolbar;
	}

	private JFileChooser getFileChooser() {
		JFileChooser chooser = new RememberFileChooser() {
			private static final long serialVersionUID = 1l;
			@Override
			public void approveSelection() {
				File file = getSelectedFile();
				if(!file.getName().toLowerCase().endsWith('.'+FILE_EXTENSION))
					setSelectedFile(file=new File(file.getAbsolutePath()+'.'+FILE_EXTENSION));
				if(getDialogType()==SAVE_DIALOG&&file!=null&&file.exists())
					if(JOptionPane.showOptionDialog(Application.this, getTranslation("persist.overwrite", file.getName()), getTranslation("persist.overwrite", TranslationType.TITLE), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null)==JOptionPane.NO_OPTION)
						return;
				currentDirectory = file.getParentFile();
				if(currentDirectory!=null&&!currentDirectory.isDirectory())
					currentDirectory = null;
				super.approveSelection();
			}
		};
		chooser.setFileFilter(new FileFilter() {
			@Override public String getDescription() { return getTranslation("persist.filter.description"); }
			@Override public boolean accept(File file) { return file.isDirectory()||file.getName().toLowerCase().endsWith('.'+FILE_EXTENSION); }
		});
		return chooser;
	}

	private JButton createToolBarButton(String key, ActionListener listener) {
		JButton button = new JButton(getTranslation("toolbar."+key));
		button.setActionCommand(key);
		button.setToolTipText(getTranslation("toolbar."+key, TranslationType.TOOLTIP));
		URL resource = getResource("images/toolbar/"+key+".gif");
		if(resource!=null) {
			button.setText(new String());
			button.setIcon(new ImageIcon(getResource("images/toolbar/"+key+".gif"), getTranslation("toolbar."+key, TranslationType.ALTERNATIVE)));
		}
		button.addActionListener(listener);
		return button;
	}
	private void setToolBarButtonHeight(int height) {
		for(java.awt.Component component:toolbar.getComponents())
			component.setSize(new Dimension(component.getSize().width, height));
		toolbar.setPreferredSize(new Dimension(toolbar.getPreferredSize().width, height));
		toolbar.revalidate();
	}

	private SimulationOscilloscope createOscilloscope() {
		SimulationOscilloscope oscilloscope = new SimulationOscilloscope();
		oscilloscope.setPreferredSize(new Dimension(2000, 0));
		return oscilloscope;
	}

	public Simulation getActiveSimulation() {
		SimulationFrame selected = desktop.getSelectedFrame();
		if(selected!=null)
			return selected.getSimulation();
		else return null;
	}

	public void newWorksheet() { addWorksheet(new SimulationFrame()); }
	public void openWorksheet(File file) throws IOException, GeneralSecurityException { addWorksheet(new SimulationFrame(file));	}
	public void openWorksheet() {
		JFileChooser chooser = getFileChooser();
		if(chooser.showOpenDialog(Application.this)!=JFileChooser.APPROVE_OPTION)
			return;
		try {
			openWorksheet(chooser.getSelectedFile());
		} catch(StreamCorruptedException e) { //wrong file or wrong password
			JOptionPane.showMessageDialog(Application.this, getTranslation("password.wrong_file"), getTranslation("password.wrong", TranslationType.TITLE), JOptionPane.WARNING_MESSAGE);
		} catch(PasswordRequiredException e) {
			JOptionPane.showMessageDialog(Application.this, getTranslation("password.error.missing"), getTranslation("password.error", TranslationType.TITLE), JOptionPane.WARNING_MESSAGE);
		} catch(GeneralSecurityException e) {
			JOptionPane.showMessageDialog(Application.this, getTranslation("password.error.security"), getTranslation("password.error", TranslationType.TITLE), JOptionPane.WARNING_MESSAGE);
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(Application.this, getTranslation("persist.error.load"), getTranslation("persist.error.load", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	public void addWorksheet(final SimulationFrame frame) {
		final int gap=25, number=frame.getNumber()-1, count=5, bound=gap+gap*(number%count), width=Math.max(100, (int)((getWidth()-2*gap)*0.75)), height=(int)((width/4)*1.8);
		frame.setBounds(bound+(number/count*gap*3), bound, width, height);
		frame.setVisible(true);
		frame.addInternalFrameListener(new InternalFrameAdapter() {
			@Override public void internalFrameOpened(InternalFrameEvent event) { revalidate(); }
			@Override public void internalFrameActivated(InternalFrameEvent event) { revalidate(); }
			@Override public void internalFrameDeactivated(InternalFrameEvent event) { revalidate(); }
			@Override public void internalFrameClosed(InternalFrameEvent event) { revalidate(); }
		});
		desktop.add(frame);
		Guitilities.selectFrame(frame);
	}

	public void saveWorksheet() { saveWorksheet(desktop.getSelectedFrame()); }
	public void saveWorksheet(SimulationFrame frame) {
		if(frame==null)
			return;
		File file = frame.getFile();
		if(file!=null)
			saveWorksheet(desktop.getSelectedFrame(), file);
		else saveWorksheetAs(frame);
	}
	public void saveWorksheetAs() { saveWorksheetAs(desktop.getSelectedFrame()); }
	public void saveWorksheetAs(SimulationFrame frame) {
		if(frame==null)
			return;
		JFileChooser chooser = getFileChooser();
		chooser.setSelectedFile(new File(frame.getFileName()));
		if(chooser.showSaveDialog(Application.this)!=JFileChooser.APPROVE_OPTION)
			return;
		saveWorksheet(desktop.getSelectedFrame(), chooser.getSelectedFile());
	}
	private void saveWorksheet(SimulationFrame frame, File file) {
		Simulation simulation = frame.getSimulation();
		try {
			simulation.writeSimulation(new BufferedOutputStream(new FileOutputStream(file)));
			frame.setChanged(false);
			frame.setFile(file);
		}	catch(IOException e) {
			JOptionPane.showMessageDialog(Application.this, getTranslation("persist.error.save"), getTranslation("persist.error.save", TranslationType.TITLE), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void printWorksheet() {
		PrinterJob print = PrinterJob.getPrinterJob();
		print.setPrintable(desktop.getSelectedFrame());
		if(print.printDialog(new HashPrintRequestAttributeSet()))
			try { print.print(); }
		catch(PrinterException e) {}
	}
	public void printWorksheetLevel() {
		PrinterJob print = PrinterJob.getPrinterJob();
		PrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
		set.add(OrientationRequested.LANDSCAPE);
		print.setPrintable(oscilloscope);
		if(print.printDialog(set))
			try { print.print(); }
		catch(PrinterException e) {}
	}

	public void exitApplication() {
		for(SimulationFrame frame:desktop.getAllFrames())
			try { frame.setClosed(true); }
		catch(PropertyVetoException e) { return; }
		this.dispose();
		setConfiguration(CONFIGURATION_WINDOW_SIZE, new StringBuilder().append(getSize().width).append(',').append(getSize().height).toString());
		setConfiguration(CONFIGURATION_WINDOW_LOCATION, new StringBuilder().append(getLocation().x).append(',').append(getLocation().y).toString());
		setConfiguration(CONFIGURATION_WINDOW_MAXIMIZED, Boolean.toString((getExtendedState()&JFrame.MAXIMIZED_BOTH)==JFrame.MAXIMIZED_BOTH));
		System.exit(0);
	}

	@Override
	public void revalidate() {
		SimulationFrame frame = desktop.getSelectedFrame();
		boolean selected = frame!=null;
		setToolBarEnabled(selected, TOOLBAR_FRAME_FOCUS);
		setMenuBarEnabled(selected, MENUBAR_FRAME_FOCUS);
		if(selected) {
			Simulation simulation = frame.getSimulation();
			getToolBarButton("simulate").setSelected(simulation.isSimulating());
			boolean selected_components = simulation.getSelectedComponents().size()!=0;
			setToolBarEnabled(selected_components, TOOLBAR_COMPONENT_FOCUS);
			setMenuBarEnabled(selected_components, MENUBAR_COMPONENT_FOCUS);
		} else getToolBarButton("simulate").setSelected(false);
	}

	private void setMenuBarEnabled(boolean enabled, String... items) { setMenuBarEnabled(getJMenuBar(), enabled, items); }
	private void setMenuBarEnabled(JComponent menu, boolean enabled, String... items) {
		java.awt.Component[] menubar_components = menu instanceof JMenu?((JMenu)menu).getMenuComponents():menu.getComponents();
		List<String> list = null;
		if(items!=null&&items.length>0)
			list = Arrays.asList(items);
		for(java.awt.Component menu_component:menubar_components)
			if(menu_component instanceof JMenuItem) {
				if(list==null||list.contains(((JMenuItem) menu_component).getActionCommand()))
					menu_component.setEnabled(enabled);
				else if(menu_component instanceof JMenu)
					setMenuBarEnabled((JMenu)menu_component, enabled, items);
			}
	}
	private void setToolBarEnabled(boolean enabled, String... buttons) {
		java.awt.Component[] toolbar_components = toolbar.getComponents();
		if(buttons!=null&&buttons.length>0) {
			List<String> list = Arrays.asList(buttons);
			for(java.awt.Component component:toolbar_components)
				if(component instanceof AbstractButton
						&& list.contains(((AbstractButton)component).getActionCommand()))
					component.setEnabled(enabled);
		} else for(java.awt.Component toolbar_component:toolbar_components)
			toolbar_component.setEnabled(enabled);
	}

	private JMenuItem getMenuBarItem(String item) { return getMenuBarItem(getJMenuBar(), item); }
	private JMenuItem getMenuBarItem(JComponent menu, String item) {
		java.awt.Component[] menubar_components = menu instanceof JMenu?((JMenu)menu).getMenuComponents():menu.getComponents();
		for(java.awt.Component menu_component:menubar_components)
			if(menu_component instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) menu_component;
				if(menuItem.getActionCommand().equals(item))
					return menuItem;
				else if(menu_component instanceof JMenu) {
					JMenuItem subMenuItem = getMenuBarItem((JMenu)menu_component, item);
					if(subMenuItem!=null)
						return subMenuItem;
				}
			}
		return null;
	}

	private AbstractButton getToolBarButton(String button) {
		java.awt.Component[] toolbar_components = toolbar.getComponents();
		for(java.awt.Component component:toolbar_components)
			if(component instanceof AbstractButton
					&& ((AbstractButton)component).getActionCommand().equals(button))
				return (AbstractButton) component;
		return null;
	}

	protected class ComponentButton extends JButton {
		private static final long serialVersionUID = 1l;
		protected ComponentAttributes attributes;
		private Component component;
		private ImageIcon icon;

		public ComponentButton(ComponentAttributes attributes, final Class<? extends Component> cls) throws InstantiationException, IllegalAccessException {
			this.component = createComponent(cls);
			setText((this.attributes = attributes).name);
			setToolTipText(attributes.description);
			refreshIcon();
			setIconTextGap(10);
			setHorizontalTextPosition(JButton.TRAILING);
			setHorizontalAlignment(JButton.LEADING);
			setOpaque(false);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setTransferHandler(new TransferHandler("digital-simulator-component") {
				private static final long serialVersionUID = 1l;
				@Override protected Transferable createTransferable(JComponent component) {
					return new Transferable() {
						@Override public boolean isDataFlavorSupported(DataFlavor flavor) { return flavor instanceof ComponentFlavor;	}
						@Override public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[]{ComponentFlavor.getFlavor()}; }
						@Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
							if(!isDataFlavorSupported(flavor))
								throw new UnsupportedFlavorException(flavor);
							return cls;
						}
					};
				}
				@Override public int getSourceActions(JComponent component) { return TransferHandler.COPY; }
			});

			addMouseListener(new MouseAdapter() {
				@Override public void mouseReleased(MouseEvent event) {
					JInternalFrame frame = desktop.getSelectedFrame();
					if(frame==null)
						return;
					try {
						Component component = cls.newInstance();
						component.setLocation(new Point(20, 20));
						((SimulationFrame)frame).getSimulation().addComponent(component);
					} catch (Exception e) {}
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override public void mouseDragged(MouseEvent event) {
					JComponent component = (JComponent) event.getSource();
					TransferHandler handler = component.getTransferHandler();
					handler.exportAsDrag(component, event, TransferHandler.COPY);
					((JButton)event.getSource()).transferFocusUpCycle(); //revalidate 'button-down'
				}
			});
		}

		public void refreshIcon() {
			setIcon(icon=createComponentImage(component, attributes.description));
			repaint();
		}
		public void setIconVisible(boolean visible) {
			if(!visible)
				 this.setIcon(null);
			else this.setIcon(icon);
			repaint();
		}

		private Component createComponent(Class<? extends Component> cls) throws InstantiationException, IllegalAccessException { return cls.newInstance(); }
		private ImageIcon createComponentImage(Component component, String tooltip) {
			component.checkSymbolStandard();
			Dimension size = component.getSize();
			Image image = Guitilities.createTranslucentImage(size.width+1, size.height+1);
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			component.paint(graphics);
			return new ImageIcon(image, tooltip);
		}
	}

	public static Simulation[] getSimulations() {
		Set<Simulation> simulations = new HashSet<Simulation>();
		for(Application application:applications)
			for(SimulationFrame frame:application.desktop.getAllFrames())
				simulations.add(frame.getSimulation());
		return simulations.toArray(new Simulation[0]);
	}
	public static Simulation getSimulation(Component component) {
		for(Simulation simulation:getSimulations())
			if(simulation.hasComponent(component))
				return simulation;
		return null;
	}

	public static class SimulationDesktop extends JDesktopPane {
		private static final long serialVersionUID = 1l;
		private static int simulationNumber = 0;
		public static int getSimulationNumber() { return simulationNumber; }
		@Override protected void addImpl(java.awt.Component component, Object obj, int layer) {
			super.addImpl(component, obj, layer);
			if(component instanceof SimulationFrame)
				simulationNumber++;
		}
		@Override public SimulationFrame selectFrame(boolean flag) { return (SimulationFrame) super.selectFrame(flag); }
		@Override public SimulationFrame getSelectedFrame() { return (SimulationFrame) super.getSelectedFrame();	}
		@Override public SimulationFrame[] getAllFrames() { return cast(super.getAllFrames());	}
		@Override public SimulationFrame[] getAllFramesInLayer(int layer) { return cast(super.getAllFramesInLayer(layer)); }
		private SimulationFrame[] cast(JInternalFrame[] frames) {
			Vector<SimulationFrame> list = new Vector<SimulationFrame>(frames.length);
			for(JInternalFrame frame:frames)
				list.add((SimulationFrame)frame);
			return list.toArray(new SimulationFrame[0]);
		}

		public void tileAllFrames() { tileFrames(this.getAllFrames()); }
		public void tileAllFramesInLayer(int layer) { tileFrames(this.getAllFramesInLayer(layer)); }
		private void tileFrames(JInternalFrame[] frames) {
			if(frames.length<=0) return;
			Dimension size = getSize();
			int columns = (int) Math.sqrt(frames.length), rows = (int) (Math.ceil(((double) frames.length) / columns));
			int lastRow = frames.length - columns * (rows - 1);
			int width, height;

			if (lastRow == 0) {
				rows--;
				height = size.height / rows;
			} else {
				height = size.height / rows;
				if (lastRow < columns) {
					rows--;
					width = size.width / lastRow;
					for (int i = 0; i < lastRow; i++) {
						frames[columns * rows + i].setBounds(i * width, rows * height, width, height);
					}
				}
			}

			width = size.width / columns;
			for (int j = 0; j < rows; j++) {
				for (int i = 0; i < columns; i++) {
					frames[i + j * columns].setBounds(i * width, j * height, width, height);
				}
			}
		}

		public void cascadeAllFrames() {	cascadeFrames(this.getAllFrames(), 25); }
		public void cascadeAllFramesInLayer(int layer) { cascadeFrames(this.getAllFramesInLayer(layer), 25);	}
		private void cascadeFrames(JInternalFrame[] frames, int separation) {
			if(frames.length<=0) return;
			java.awt.Rectangle bounds = getBounds();
			int margin = frames.length*separation+separation;
			for(int frame=0;frame<frames.length;frame++)
				frames[frame].setBounds(separation+bounds.x+frame*separation,
						separation+bounds.y+frame*separation, bounds.width-margin,
						bounds.height-margin);
		}
	}
	public class SimulationFrame extends JInternalFrame implements Printable {
		private static final long serialVersionUID = 1l;

		private Simulation simulation;
		private int number;
		private File file;
		private boolean changed;

		public SimulationFrame() { this(new Simulation()); }
		public SimulationFrame(File file) throws IOException, GeneralSecurityException {
			this(new BufferedInputStream(new FileInputStream(file)));
			this.setFile(file);
		}
		public SimulationFrame(InputStream in) throws IOException, GeneralSecurityException {
			try {
				try { simulation = Simulation.readSimulation(in); }
				catch(PasswordRequiredException e) {
					simulation = Simulation.readSimulation(in, Guitilities.showPasswordDialog(Application.this, getTranslation("password.enter"), getTranslation("password.enter", TranslationType.TITLE)));
				}
			} catch(ClassNotFoundException e) { throw new IOException(e); }
			initialize();
		}
		public SimulationFrame(Simulation simulation) {
			this.simulation = simulation;
			initialize();
		}
		public SimulationFrame(Simulation simulation, File file) {
			this(simulation);
			this.setFile(file);
		}

		private void initialize() {
			number = SimulationDesktop.getSimulationNumber()+1;
			this.setFile(null);
			this.setClosable(true);
			this.setResizable(true);
			this.setMaximizable(true);
			this.setIconifiable(true);
			this.addVetoableChangeListener(new VetoableChangeListener() {
				@Override public void vetoableChange(PropertyChangeEvent event) throws PropertyVetoException {
					if(event.getPropertyName().equals("closed")&&event.getNewValue().equals(true))
						if(hasChanged())
							switch(JOptionPane.showConfirmDialog(Application.this, getTranslation("persist.changes", getFileName()), getTranslation("persist.changes", TranslationType.TITLE), JOptionPane.YES_NO_CANCEL_OPTION)) {
							case JOptionPane.YES_OPTION:
								saveWorksheet(SimulationFrame.this);
								if(!hasChanged())
									return; //file was saved successfully
							case JOptionPane.CANCEL_OPTION: throw new PropertyVetoException(new String(), event);
							}
				}
			});
			this.add(Guitilities.createExtendedScrollPane(simulation, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
			simulation.setClipboard(clipboard);
			simulation.addFocusListener(new FocusListener() {
				@Override public void focusLost(FocusEvent event) { focusGained(event); }
				@Override public void focusGained(FocusEvent event) {
					boolean selected_components = simulation.getSelectedComponents().size()!=0;
					setToolBarEnabled(selected_components, TOOLBAR_COMPONENT_FOCUS);
					setMenuBarEnabled(selected_components, MENUBAR_COMPONENT_FOCUS);
					setMenuBarEnabled(simulation.canUndoChange(), "edit.undo");
					setMenuBarEnabled(simulation.canRedoChange(), "edit.redo");
					getToolBarButton("simulate").setSelected(simulation.isSimulating());
				}
			});
			simulation.addChangeListener(new Simulation.ChangeListener() {
				@Override public void changed(ChangeEvent event) { setChanged(true); }
			});
			simulation.addKeyListener(new KeyAdapter() {
				@Override public void keyPressed(KeyEvent event) {
					if(event.isControlDown())
						switch(event.getKeyCode()) {
						case KeyEvent.VK_O:
							if(event.isAltDown()&&event.isShiftDown()) {
								JFileChooser chooser = new RememberFileChooser();
								chooser.setFileFilter(new FileFilter() {
									@Override public String getDescription() { return "Class (*.class), Jar-File (*.jar)"; }
									@Override public boolean accept(File file) {
										return file.isDirectory()||file.getName().endsWith(".class")
												||file.getName().endsWith(".jar");
									}
								});
								if(chooser.showOpenDialog(Application.this)==JFileChooser.APPROVE_OPTION)
									loadComponents(chooser.getSelectedFile(), null);
							} else openWorksheet();
							break;
						case KeyEvent.VK_S:
							saveWorksheet();
							break;
						}
				}
			});
			simulation.setOscilloscope(oscilloscope);
		}

		public boolean hasChanged() { return this.changed; }
		public void setChanged(boolean changed) {
			if(changed!=this.changed) {
				this.changed = changed;
				updateTitle();
			}
		}

		public File getFile() { return this.file; }
		public void setFile(File file) { this.file = file; updateTitle(); }
		public String getFileName() {
			if(file==null)
				return super.getTitle().replaceAll("[ \\/:*?\"<>|]", new String()).trim();
			else return file.getName();
		}

		private void updateTitle() {
			StringBuilder title = new StringBuilder();
			if(file==null)
				title.append(getTranslation("worksheet.title")).append(number);
			else title.append(file.getName());
			if(hasChanged())
				title.append(" *");
			this.setTitle(title.toString());
		}

		public Simulation getSimulation() { return simulation; }
		public int getNumber() { return number; }

		@Override public int print(Graphics default_graphics, PageFormat pageFormat, int pageNumber) throws PrinterException {
			if(simulation.print(default_graphics, pageFormat, pageNumber)!=NO_SUCH_PAGE) {
				Graphics2D graphics = (Graphics2D) default_graphics.create((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY(), (int)pageFormat.getImageableWidth(), (int)pageFormat.getImageableHeight());
				graphics.setColor(Color.BLACK); graphics.setFont(default_graphics.getFont().deriveFont(Font.BOLD, 14));
				String title = getFileName();	FontMetrics metrics = default_graphics.getFontMetrics();
				graphics.drawString(title, (int)(pageFormat.getImageableWidth()/2-metrics.stringWidth(title)/2),
						(metrics.getAscent()));
				return PAGE_EXISTS;
			} else return NO_SUCH_PAGE;
		}
	}

	public static void main(String[] args) {
		Guitilities.initializeUIManager();
		String lookAndFeelClass = Utilities.getConfiguration(Utilities.CONFIGURATION_LOOK_AND_FEEL_CLASS),
				lookAndFeelName = Utilities.getConfiguration(Utilities.CONFIGURATION_LOOK_AND_FEEL_NAME);
		if(!Guitilities.setLookAndFeel(lookAndFeelClass, lookAndFeelName))
			if(!Guitilities.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", "Nimbus"))
				if(!Guitilities.setLookAndFeel(UIManager.getSystemLookAndFeelClassName(), "System"))
					Guitilities.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName(), "CrossPlatform");
		String localizationLanguage = Utilities.getConfiguration(Utilities.CONFIGURATION_LOCALIZATION_LANGUAGE);
		if(localizationLanguage!=null)
			for(Locale locale:Utilities.SUPPORTED_LOCALES)
				if(locale.getLanguage().equals(localizationLanguage))
					Utilities.setCurrentLocale(locale);
		setUseAnsiSymbols(Boolean.parseBoolean(lc.kra.jds.Utilities.getConfiguration(CONFIGURATION_ANSI_SYMBOLS)));
		Application application = new Application();
		if(args.length!=0)
			try { application.openWorksheet(new File(args[0])); }
		catch(Exception e) { application.newWorksheet(); }
		else application.newWorksheet();
	}
}