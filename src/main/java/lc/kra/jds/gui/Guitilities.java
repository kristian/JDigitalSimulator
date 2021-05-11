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

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.StringTokenizer;

import static lc.kra.jds.Utilities.getTranslation;

public class Guitilities {
				public static final Border MENU_EMPTY_BORDER = new EmptyBorder(3, 5, 3, 25);
				public static final Border SMALL_EMPTY_BORDER = new EmptyBorder(6, 6, 6, 6);
				public static final Border MEDIUM_EMPTY_BORDER = new EmptyBorder(10, 10, 10, 10);
				public static final Border LARGE_EMPTY_BORDER = new EmptyBorder(18, 18, 18, 18);
				public static final Border HUGE_EMPTY_BORDER = new EmptyBorder(30, 30, 30, 30);

				public static final Border CHISEL_BORDER = new ChiselBorder();
				public static final Border TITLE_BORDER = new CompoundBorder(CHISEL_BORDER, new EmptyBorder(6, 8, 6, 0));
				public static final Border FOOTER_BORDER = new CompoundBorder(CHISEL_BORDER, new EmptyBorder(0, 8, 6, 0));
				public static final Border CATEGORY_BORDER = new CompoundBorder(CHISEL_BORDER, new EmptyBorder(0, 0, 10, 0));

				public static final String TITLE_FOREGROUND = "titleForegroundColor";
				public static final String TITLE_GRADIENT_COLOR_A = "titleGradientColor1";
				public static final String TITLE_GRADIENT_COLOR_B = "titleGradientColor2";
				public static final String TITLE_FONT = "titleFont";

				public static final String FOOTER_GRADIENT_COLOR_A = "footerGradientColor1";
				public static final String FOOTER_GRADIENT_COLOR_B = "footerGradientColor2";

				public static final String SUB_PANEL_BACKGROUND = "subPanelBackgroundColor";

				public static final String FILECHOOSER_READONLY = "FileChooser.readOnly";

				public static GridBagConstraints pair_left_constraint	   = new GridBagConstraints(0, 0, 1, 1, 0.1d, 0d, GridBagConstraints.LINE_END		, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0);
				public static GridBagConstraints pair_right_constraint	  = new GridBagConstraints(1, 0, 1, 1, 0.9d, 0d, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0);
				public static GridBagConstraints pair_additional_constraint = new GridBagConstraints(2, 0, 1, 1, 0.3d, 0d, GridBagConstraints.LINE_START	  , GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0);
				private static final GridBagConstraints PAIR_LEFT_DEFAULT_CONSTRAINT = (GridBagConstraints)pair_left_constraint.clone(),
												PAIR_RIGHT_DEFAULT_CONSTRAINT = (GridBagConstraints)pair_right_constraint.clone(),
												PAIR_ADDITIONAL_DEFAULT_CONSTRAINT = (GridBagConstraints)pair_additional_constraint.clone();

				public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

				private static boolean usingNimbusLookAndFeel = false;

				public enum Size { REGULAR, MINI, SMALL, LARGE;
								@Override
								public String toString() { return super.toString().toLowerCase(); };
				}
				public enum Direction { HORRIZONTAL, VERTICAL }

				public static void setComponentSize(JComponent component, Size size) {
								component.putClientProperty("JComponent.sizeVariant", size.toString());
				}

				public static void initializeUIManager() {
								Color color = UIManager.getColor(Guitilities.usingNimbusLookAndFeel()? "nimbusBase" : "activeCaption");
								if(color==null)
												color = UIManager.getColor("control");
								float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
								UIManager.put(TITLE_GRADIENT_COLOR_A, Color.getHSBColor(hsb[0]-.013f, .15f, .85f));
								UIManager.put(TITLE_GRADIENT_COLOR_B, Color.getHSBColor(hsb[0]-.005f, .24f, .80f));
								UIManager.put(TITLE_FOREGROUND, Color.getHSBColor(hsb[0], .54f, .40f));

								color = UIManager.getColor(Guitilities.usingNimbusLookAndFeel()? "nimbusSelectionBackground" : "inactiveCaption");
								if(color==null)
												color = UIManager.getColor("control");
								UIManager.put(FOOTER_GRADIENT_COLOR_A, Color.getHSBColor(hsb[0]-.013f, .10f, .90f));
								UIManager.put(FOOTER_GRADIENT_COLOR_B, Color.getHSBColor(hsb[0]-.005f, .14f, .85f));

								UIManager.put(SUB_PANEL_BACKGROUND, Guitilities.deriveColorHSB(UIManager.getColor("Panel.background"), 0, 0, -.15f));

								Font font = UIManager.getFont("Label.font");
								UIManager.put(TITLE_FONT, font.deriveFont(Font.BOLD, font.getSize()+4f));

								UIManager.put(FILECHOOSER_READONLY, Boolean.TRUE);
				}

				public static boolean usingNimbusLookAndFeel() { return usingNimbusLookAndFeel; }
				public static boolean setLookAndFeel(String classname, String name) {
								if((classname==null||classname.isEmpty())&&(name==null||name.isEmpty()))
												return false;
								try {
												UIManager.setLookAndFeel(classname);
												usingNimbusLookAndFeel = UIManager.getLookAndFeel().getName().equals("Nimbus");
												return true;
								}	catch(Exception e_a) {
												try {
																for(LookAndFeelInfo look_and_feel:UIManager.getInstalledLookAndFeels())
																				if(name.equals(look_and_feel.getName())) {
																								UIManager.setLookAndFeel(look_and_feel.getClassName());
																								usingNimbusLookAndFeel = UIManager.getLookAndFeel().getName().equals("Nimbus");
																								return true;
																				}
												} catch(Exception e_b) {}
								}
								return false;
				}

				public static Point addPoints(Point... points) {
								Point result = new Point();
								for(Point point:points)
												if(point!=null)
																result.translate(point.x, point.y);
								return result;
				}
				public static Point invertPoint(Point point) {
								return new Point(-point.x, -point.y);
				}
				public static class RelativeMouseEvent extends MouseEvent {
								private static final long serialVersionUID = 1l;
								public RelativeMouseEvent(MouseEvent event, Point point, Point relative) {
												super(event.getComponent(), event.getID(), event.getWhen(), event.getModifiers(),
																				point.x-relative.x, point.y-relative.y, point.x, point.y,
																				event.getClickCount(), event.isPopupTrigger(), event.getButton());
								}
				}

				public static Image makeColorTransparent(Image image, final Color color) {
								ImageFilter filter = new RGBImageFilter() {
												public int markerRGB = color.getRGB()|0xFF000000;
												@Override
												public final int filterRGB(int x, int y, int rgb) {
																if((rgb|0xFF000000)==markerRGB)
																				return 0x00FFFFFF & rgb;
																else return rgb;
												}
								};
								return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
				}

				private static GraphicsConfiguration getDefaultGraphicsConfiguration() { return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); }
				public static Dimension getStringBounds(Font font, String text) { return font.getStringBounds(text, new FontRenderContext(getDefaultGraphicsConfiguration().getDefaultTransform(), true, false)).getBounds().getSize(); }
				public static LineMetrics getLineMetrics(Font font, String text) { return font.getLineMetrics(text, new FontRenderContext(getDefaultGraphicsConfiguration().getDefaultTransform(), true, false)); }
				public static BufferedImage createCompatibleImage(int width, int height) { return getDefaultGraphicsConfiguration().createCompatibleImage(width, height); }
				public static BufferedImage createTranslucentImage(int width, int height) { return getDefaultGraphicsConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT); }
				public static BufferedImage createWhiteImage(int width, int height) {
								BufferedImage image = createCompatibleImage(width, height);
								Graphics graphics = image.getGraphics();
								graphics.setColor(Color.WHITE);
								graphics.fillRect(0, 0, width, height);
								return image;
				}
				public static BufferedImage createGradientImage(int width, int height, Color gradient_a, Color gradient_b) {
								BufferedImage image = createCompatibleImage(width, height);
								GradientPaint gradient = new GradientPaint(0, 0, gradient_a, 0, height, gradient_b, false);
								Graphics2D graphics = (Graphics2D) image.getGraphics();
								graphics.setPaint(gradient);
								graphics.fillRect(0, 0, width, height);
								graphics.dispose();
								return image;
				}

				public static GradientPanel createGradientTitle(String title) {
								GradientPanel panel = new GradientPanel(UIManager.getColor(TITLE_GRADIENT_COLOR_A), UIManager.getColor(TITLE_GRADIENT_COLOR_B));
								panel.setLayout(new BorderLayout());
								panel.setBorder(TITLE_BORDER);
								panel.setPreferredSize(new Dimension(0, 40));
								JLabel label = new JLabel(title);
								label.setOpaque(false);
								label.setFont(UIManager.getFont(TITLE_FONT));
								label.setForeground(UIManager.getColor(TITLE_FOREGROUND));
								label.setHorizontalAlignment(JLabel.LEADING);
								panel.add(label, BorderLayout.CENTER);
								return panel;
				}
				public static GradientPanel createGradientFooter() {
								GradientPanel panel = new GradientPanel(UIManager.getColor(FOOTER_GRADIENT_COLOR_A), UIManager.getColor(FOOTER_GRADIENT_COLOR_B));
								panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
								panel.setBorder(FOOTER_BORDER);
								panel.setPreferredSize(new Dimension(0, 40));
								return panel;
				}

				public static JLabel createLabel(String text) {
								JLabel label = new JLabel();
								label.setFocusable(false);
								label.setText(new StringBuilder("<html>").append(text.replaceAll("\n", "<br>")).append("</html>").toString());
								return label;
				}
				public static void setFittingLabelSize(JLabel label, int width) {
								int wordWidth, lines=1, size=0;
								if(width<=0||label.getText().isEmpty())
												return;
								StringTokenizer words = new StringTokenizer(label.getText());
								FontMetrics metrics = label.getFontMetrics(label.getFont());
								while(words.hasMoreTokens())
												if((size+=(wordWidth=metrics.stringWidth(words.nextToken())))>width) {
																size = wordWidth;
																lines++;
												}
								label.setPreferredSize(new Dimension(width, lines*metrics.getHeight()));
								label.revalidate();
				}

				public static JMenuItem createMenuItem(String key, ActionListener listener) { return createMenuItem(key, (ImageIcon)null, listener); }
				public static JMenuItem createMenuItem(String key, String text, ActionListener listener) { return createMenuItem(key, text, (ImageIcon)null, listener); }
				public static JMenuItem createMenuItem(String key, String text, String tooltip, ActionListener listener) { return createMenuItem(key, text, tooltip, (ImageIcon)null, listener); }
				public static JMenuItem createMenuItem(String key, Icon icon, ActionListener listener) { return createMenuItem(key, getTranslation("menu."+key), getTranslation("menu."+key, TranslationType.TOOLTIP), icon, listener); }
				public static JMenuItem createMenuItem(String key, String text, Icon icon, ActionListener listener) { return createMenuItem(key, text, null, icon, listener); }
				public static JMenuItem createMenuItem(String key, String text, String tooltip, Icon icon, ActionListener listener) {
								JMenuItem item = new JMenuItem(text);
								item.setToolTipText(tooltip);
								item.setActionCommand(key);
								item.setBorder(MENU_EMPTY_BORDER);
								if(icon!=null)
												item.setIcon(icon);
								item.addActionListener(listener);
								return item;
				}

				public static Color deriveColorAlpha(Color color, int alpha) { return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha); }
				public static Color deriveColorHSB(Color color, float dH, float dS, float dB) {
								float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
								hsb[0] += dH; hsb[1] += dS; hsb[2] += dB;
								return Color.getHSBColor(hsb[0] < 0 ? 0 : (hsb[0] > 1 ? 1 : hsb[0]),
																hsb[1] < 0 ? 0 : (hsb[1] > 1 ? 1 : hsb[1]),
																hsb[2] < 0 ? 0 : (hsb[2] > 1 ? 1 : hsb[2]));
				}

				public static Point getAbsoluteLocation(Component component) {
								if(component==null||component instanceof Frame)
												return new Point();
								return addPoints(component.getLocation(), getAbsoluteLocation(component.getParent()));
				}
				public static Dimension getActualSize(Frame frame) {
								try {
												int extendedState = frame.getExtendedState();
												java.awt.Rectangle bounds = frame.getMaximizedBounds(), systemBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
												return new Dimension((extendedState&Frame.MAXIMIZED_HORIZ)==Frame.MAXIMIZED_HORIZ?(bounds!=null&&bounds.width !=Integer.MAX_VALUE?bounds.width :systemBounds.width ):frame.getWidth(),
																				(extendedState&Frame.MAXIMIZED_VERT) ==Frame.MAXIMIZED_VERT ?(bounds!=null&&bounds.height!=Integer.MAX_VALUE?bounds.height:systemBounds.height):frame.getHeight());
								} catch(HeadlessException e) { return frame.getSize(); }
				}

				public static JScrollPane createScrollPane(Component component) { return createScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); }
				public static JScrollPane createScrollPane(Component component, int vsbPolicy, int hsbPolicy) { return new JScrollPane(component, vsbPolicy, hsbPolicy); }
				public static JScrollPane createScrollPane(Component component, int vsbPolicy, int hsbPolicy, int vsbUnitIncrement, int hsbUnitIncrement) {
								JScrollPane scroll = createScrollPane(component, vsbPolicy, hsbPolicy);
								scroll.getVerticalScrollBar().setUnitIncrement(vsbUnitIncrement);
								scroll.getHorizontalScrollBar().setUnitIncrement(hsbUnitIncrement);
								return scroll;
				}

				public static JScrollPane createExtendedScrollPane(Component component) { return createExtendedScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); }
				public static JScrollPane createExtendedScrollPane(Component component, int vsbPolicy, int hsbPolicy) {
								JScrollPane scroll = new JScrollPane(vsbPolicy, hsbPolicy);
								final JViewport viewport = new JViewport() {
												private static final long serialVersionUID = 1l;
												@Override public Dimension getViewSize() {
																Component component = getView();
																if(component==null)
																				return new Dimension(0, 0);
																if(isViewSizeSet)
																				return component.getSize();
																else if(component instanceof Scrollable)
																				return ((Scrollable) component).getPreferredScrollableViewportSize();
																else return component.getPreferredSize();
												}
								};
								viewport.addChangeListener(new ChangeListener() {
												private Dimension size;
												@Override public void stateChanged(ChangeEvent event) {
																Dimension size = viewport.getViewSize();
																if(this.size!=null&&!this.size.equals(size)) {
																				viewport.setViewPosition(new Point());
																				this.size = size;
																} else this.size = size;
												}
								});
								scroll.setViewport(viewport);
								scroll.setViewportView(component);
								return scroll;
				}

				public static String showPasswordDialog(Component component, String text) { return showPasswordDialog(component, text, null); }
				public static String showPasswordDialog(Component component, String text, String title) {
								JPasswordField field = new JPasswordField(); field.setEchoChar('\u25CF');
								JPanel panel = new JPanel();
								panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
								panel.add(createLabel(text));
								panel.add(field);
								if(JOptionPane.showConfirmDialog(component, panel, title, JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
												return new String(field.getPassword());
								else return null;
				}

				public static ImageIcon createImageIcon(String name) { return createImageIcon(name, null); }
				public static ImageIcon createImageIcon(String name, ImageIcon fallback) {
								URL resource = Utilities.getResource(name);
								if(resource!=null)
												return new ImageIcon(resource);
								else return fallback!=null?fallback:null;
				}


				public static class BoundaryPoints {
								public Point minimum, maximum;
								public Dimension dimension;
								public BoundaryPoints(Point point, Dimension dimension) {
												minimum = new Point(point); maximum = new Point(point);
												maximum.translate(dimension.width, dimension.height);
												this.dimension = dimension;
								}
								public void checkBoundary(Point point, Dimension dimension) {
												minimum.x = Math.min(minimum.x, point.x);
												minimum.y = Math.min(minimum.y, point.y);
												maximum.x = Math.max(maximum.x, point.x+dimension.width);
												maximum.y = Math.max(maximum.y, point.y+dimension.height);
												this.dimension.setSize(maximum.x-minimum.x, maximum.y-minimum.y);
								}
				}

				public static class Rectangle {
								public Point point_ul, point_lr;
								public Dimension size;
								public Rectangle(Point2D point_a, Point2D point_b) { this(new Point((int)point_a.getX(), (int)point_a.getY()),
																new Point((int)point_b.getX(), (int)point_b.getY())); }
								public Rectangle(Point point, Dimension dimension) { this(point, new Point(point.x+dimension.width, point.y+dimension.height)); }
								public Rectangle(Point point_a, Point point_b) {
												this.point_ul = new Point(point_a);
												this.point_lr = new Point(point_b);
												if(point_a.x>point_b.x) {
																int x = point_b.x;
																this.point_lr.x = point_a.x;
																this.point_ul.x = x;
												}
												if(point_a.y>point_b.y) {
																int y = point_b.y;
																this.point_lr.y = point_a.y;
																this.point_ul.y = y;
												}
												this.size = new Dimension(this.point_lr.x-this.point_ul.x,
																				this.point_lr.y-this.point_ul.y);
								}
								public boolean contains(Point point) { return contains(point, new Dimension()); }
								public boolean contains(Point point, Dimension dimension) {
												return point_ul.x<=point.x&& point.x+dimension.width <=point_lr.x
																				&& point_ul.y<=point.y&& point.y+dimension.height<=point_lr.y;
								}
								public long getArea() { return size.width*size.height; }

								public java.awt.Rectangle toRectangle() { return new java.awt.Rectangle(point_ul, size);	}
								public Area toArea() { return new Area(toRectangle()); }
				}

				private static class ChiselBorder implements Border {
								private Insets insets = new Insets(1, 0, 1, 0);
								@Override
								public Insets getBorderInsets(java.awt.Component component) { return insets; }
								@Override
								public boolean isBorderOpaque() { return true; }
								@Override
								public void paintBorder(java.awt.Component component, Graphics g, int x, int y, int width, int height) {
												Color color = component.getBackground();
												g.setColor(Guitilities.deriveColorHSB(color, 0, 0, .2f));
												g.drawLine(x, y, x + width, y);
												g.setColor(Guitilities.deriveColorHSB(color, 0, 0, -.2f));
												g.drawLine(x, y + height - 1, x + width, y + height - 1);
								}
				}

				public static TitledBorder createTitledBorder(String title) {
								return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title);
				}

				public static JDialog createDialog(Frame owner, String title) { return createDialog(owner, title, title); }
				public static JDialog createDialog(Frame owner, String title, String gradientTitle) {
								JDialog dialog = new JDialog(owner, title, true);
								dialog.setLayout(new BorderLayout());
								dialog.add(Guitilities.createGradientTitle(title), BorderLayout.NORTH);
								dialog.setLocationRelativeTo(owner);
								dialog.setLocationByPlatform(true);
								dialog.setResizable(false);
								return dialog;
				}

				public static JPanel createSeparator(String text) {
								JLabel title = new JLabel(text);
								title.setVerticalAlignment(0);
								title.setHorizontalAlignment(JLabel.LEFT);
								title.setFont(UIManager.getFont(TITLE_FONT).deriveFont(12f));
								title.setForeground(UIManager.getColor(TITLE_FOREGROUND));
								return createSeparator(title);
				}
				public static JPanel createSeparator(JLabel label) {
								JPanel panel = new JPanel(new TitledSeperatorLayout(true));
								panel.setOpaque(false);
								panel.add(label);
								panel.add(new JSeparator());
								return panel;
				}

				public static <Type extends JComponent> Type addGridPairLine(JPanel panel, int gridy, JComponent left, Type right) { return addGridPairLine(panel, gridy, left, right, null); }
				public static <Type extends JComponent> Type addGridPairLine(JPanel panel, int gridy, JComponent left, Type right, JComponent additional) {
								pair_left_constraint.gridy = pair_right_constraint.gridy = pair_additional_constraint.gridy = gridy;
								if(additional!=null)
												pair_right_constraint.weightx = 0.6d;

								panel.add(left, pair_left_constraint);
								panel.add(right, pair_right_constraint);
								if(additional!=null)
												panel.add(additional, pair_additional_constraint);

								if(!pair_left_constraint.equals(PAIR_LEFT_DEFAULT_CONSTRAINT))
												pair_left_constraint = (GridBagConstraints)PAIR_LEFT_DEFAULT_CONSTRAINT.clone();
								if(!pair_right_constraint.equals(PAIR_RIGHT_DEFAULT_CONSTRAINT))
												pair_right_constraint = (GridBagConstraints)PAIR_RIGHT_DEFAULT_CONSTRAINT.clone();
								if(!pair_additional_constraint.equals(PAIR_ADDITIONAL_DEFAULT_CONSTRAINT))
												pair_additional_constraint = (GridBagConstraints)PAIR_ADDITIONAL_DEFAULT_CONSTRAINT.clone();

								return right;
				}

				public static JButton createButton(String text, ActionListener listener) {
								JButton button = new JButton(text);
								button.addActionListener(listener);
								return button;
				}

				public static int centerText(Direction direction, Graphics graphics, Dimension size, String text) { return centerText(direction, graphics, new java.awt.Rectangle(size), text); }
				public static int centerText(Direction direction, Graphics graphics, java.awt.Rectangle bounds, String text) {
								FontMetrics metrics = graphics.getFontMetrics();
								switch(direction) {
												case HORRIZONTAL: return bounds.x+bounds.width/2-metrics.stringWidth(text)/2;
												case VERTICAL: return bounds.y+(int)((bounds.height-metrics.getStringBounds(text, graphics).getHeight())/2+metrics.getAscent());
												default: return 0; }
				}

				public static void selectFrame(final JInternalFrame frame) {
								SwingUtilities.invokeLater(new Runnable(){
												@Override public void run() {
																frame.requestFocusInWindow();
																try { frame.setSelected(true); }	catch (PropertyVetoException e) {}
																frame.toFront();
												}
								});
				}
}
