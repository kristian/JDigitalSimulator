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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import lc.kra.jds.Utilities.RememberFileChooser;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.buildin.display.Voltmeter;
import lc.kra.jds.gui.Guitilities;

public class SimulationOscilloscope extends JPanel implements Printable {
	private static final long serialVersionUID = 1l;

	public enum Direction { LEFT_TO_RIGHT, RIGHT_TO_LEFT; }

	private final int GAP = 5, HEIGHT = 20;
	private Color foreground = Color.GREEN, background = new Color(14, 106, 40), grid = Color.BLACK;
	private Direction direction = Direction.LEFT_TO_RIGHT;

	private Map<Voltmeter, BufferedImage> buffers;

	public SimulationOscilloscope() {
		buffers = new TreeMap<Voltmeter, BufferedImage>();
		this.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent event) { if(event.isPopupTrigger()) showPopup(event); }
			@Override public void mouseReleased(MouseEvent event) { if(event.isPopupTrigger()) showPopup(event); }
			private void showPopup(MouseEvent event) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(Guitilities.createMenuItem("oscilloscope.direction", new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) {
						if(getDirection().equals(Direction.LEFT_TO_RIGHT))
							setDirection(Direction.RIGHT_TO_LEFT);
						else setDirection(Direction.LEFT_TO_RIGHT);
						Container container = SimulationOscilloscope.this.getParent();
						if(container instanceof JViewport)
							((JViewport)container).setViewPosition(new Point(direction.equals(Direction.LEFT_TO_RIGHT)?0:getPreferredSize().width, 0));
					}
				}));
				menu.add(Guitilities.createMenuItem("oscilloscope.clear", new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { clearOscilloscope(); }
				}));
				menu.add(new JSeparator());
				menu.add(Guitilities.createMenuItem("oscilloscope.save", new ActionListener() {
					@Override public void actionPerformed(ActionEvent event) { saveOscilloscope(); }
				}));
				menu.setLocation(event.getLocationOnScreen());
				menu.show(event.getComponent(), event.getX(), event.getY());
			}
		});
	}

	public void gaugeVoltage(Simulation simulation) {
		if(simulation==null) return;
		for(Component component:simulation.getAllComponents())
			if(component instanceof Voltmeter)
				gaugeVoltage((Voltmeter)component);
		repaint();
	}
	private void gaugeVoltage(Voltmeter voltmeter) {
		Graphics2D graphics = null;
		if(!buffers.containsKey(voltmeter)) {
			BufferedImage buffer = Guitilities.createTranslucentImage(getPreferredSize().width, HEIGHT+1); //1 pixel = 0.1 volt
			(graphics=(Graphics2D)buffer.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			buffers.put(voltmeter, buffer);
			setPreferredSize(new Dimension(getPreferredSize().width, buffers.size()*(HEIGHT+GAP*2)));
			revalidate();
		} else graphics = (Graphics2D) buffers.get(voltmeter).getGraphics();
		float voltage = voltmeter.getVoltage(), gauge = voltmeter.gaugeVoltage();
		graphics.setComposite(AlphaComposite.Src);
		int width = getPreferredSize().width, left = 0;
		switch(direction) {
		case LEFT_TO_RIGHT:	graphics.copyArea(0, 0, width-1, HEIGHT+1, 1, 0); left = 0;         break;
		case RIGHT_TO_LEFT: graphics.copyArea(1, 0, width-1, HEIGHT+1, -1, 0); left = width - 1;	break; }
		graphics.setColor(Guitilities.TRANSPARENT); graphics.drawLine(left, 0, left, HEIGHT);
		graphics.setColor(foreground);
		graphics.drawLine(left, (int)(HEIGHT-(voltage*(HEIGHT/5))),
				left, (int)(HEIGHT-(gauge  *(HEIGHT/5))));
	}

	public Direction getDirection() { return this.direction; }
	public void setDirection(Direction direction) {
		this.direction = direction;
		clearOscilloscope();
	}
	public void saveOscilloscope() {
		if(buffers.size()<=0)
			return;
		JFileChooser chooser = new RememberFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP (*.bmp; *.wbmp)", "bmp", "wbmp"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG (*.jpeg; *.jpg)", "jpeg", "jpg"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF (*.gif)", "gif"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
		if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)
			return;
		File file = chooser.getSelectedFile();
		String parts[] = file.getName().split("[.]"), extention = parts[parts.length-1];
		if(Arrays.binarySearch(new String[]{"jpg", "jpeg", "png", "gif", "bmp", "wbmp"}, extention)<0) {
			FileFilter filter = chooser.getFileFilter();
			if(filter instanceof FileNameExtensionFilter)
				extention = ((FileNameExtensionFilter)filter).getExtensions()[0];
			else extention = "png";
			file = new File(file.getPath()+'.'+extention);
		}
		try {
			Dimension size = getPreferredSize();
			BufferedImage image = Guitilities.createCompatibleImage(size.width, size.height);
			paint(image.getGraphics());
			ImageIO.write(image, extention, file);
		}	catch (IOException e) {
			JOptionPane.showMessageDialog(this, Utilities.getTranslation("oscilloscope.save.error"));
		}
	}
	public void clearOscilloscope() {
		buffers.clear();
		repaint();
	}

	@Override public void paint(Graphics default_graphics) {
		int number = 0;
		FontMetrics metrics = default_graphics.getFontMetrics();
		default_graphics.setColor(background);
		default_graphics.fillRect(0, 0, getWidth(), getHeight());
		default_graphics.setColor(grid);
		for(int x=0;x<getWidth();x+=30)
			default_graphics.drawLine(x, 0, x, getHeight());
		for(int y=0;y<getHeight();y+=30)
			default_graphics.drawLine(0, y, getWidth(), y);
		for(Entry<Voltmeter, BufferedImage> entry:buffers.entrySet()) {
			Graphics2D graphics = (Graphics2D) default_graphics.create();
			graphics.translate(0, GAP+(number++)*(HEIGHT+GAP*2));
			graphics.setColor(foreground);
			graphics.drawImage(entry.getValue(), 0, 0, null);
			String name = entry.getKey().getName(); int left = 5;
			if(direction.equals(Direction.RIGHT_TO_LEFT))
				left = getPreferredSize().width-5-graphics.getFontMetrics().stringWidth(name);
			graphics.drawString(name, left, HEIGHT/2+metrics.getAscent()/2);
		}
	}

	@Override public int print(Graphics default_graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if(pageIndex!=0)
			return NO_SUCH_PAGE;
		pageFormat.setOrientation(PageFormat.LANDSCAPE);

		Color foreground = this.foreground, background = this.background, grid = this.grid;
		this.foreground = Color.BLACK; this.background = Color.WHITE; this.grid = Color.LIGHT_GRAY;

		Graphics2D graphics = (Graphics2D) default_graphics;
		graphics.translate(pageFormat.getImageableX(),
				pageFormat.getImageableY());
		this.paint(graphics);
		Thread.yield(); //yield shortly that the graphics are printed properly

		this.foreground = foreground; this.background = background; this.grid = grid;
		return PAGE_EXISTS;
	}
}