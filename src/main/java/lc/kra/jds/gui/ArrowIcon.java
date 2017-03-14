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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ArrowIcon implements Icon, SwingConstants {
	private static final float DB = -.06f;
	private int direction;
	private int size;
	private Color color;
	private BufferedImage arrowImage;

	public ArrowIcon(int direction) {
		this(direction, 10, null);
	}

	public ArrowIcon(int direction, Color color) {
		this(direction, 10, color);
	}

	public ArrowIcon(int direction, int size, Color color) {
		this.size = size;
		this.direction = direction;
		this.color = color;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.drawImage(getArrowImage(), x, y, c);
	}

	protected Image getArrowImage() {
		if (arrowImage == null) {
			arrowImage = Guitilities.createTranslucentImage(size, size);
			AffineTransform atx = direction != SOUTH ? new AffineTransform() : null;
			switch (direction) {
			case NORTH:
				atx.setToRotation(Math.PI, size / 2, size / 2);
				break;
			case EAST:
				atx.setToRotation(-(Math.PI / 2), size / 2, size / 2);
				break;
			case WEST:
				atx.setToRotation(Math.PI / 2, size / 2, size / 2);
			case SOUTH:
			default: { /* no xform */}
			}
			Graphics2D ig = (Graphics2D) arrowImage.getGraphics();
			if (atx != null) {
				ig.setTransform(atx);
			}
			int width = size;
			int height = size / 2 + 1;
			int xx = (size - width) / 2;
			int yy = (size - height + 1) / 2;

			Color base = color != null ? color : UIManager.getColor("controlDkShadow").darker();

			paintArrow(ig, base, xx, yy);
			paintArrowBevel(ig, base, xx, yy);
			paintArrowBevel(ig, Guitilities.deriveColorHSB(base, 0f, 0f, .20f), xx, yy + 1);
		}
		return arrowImage;
	}

	protected void paintArrow(Graphics2D g, Color base, int x, int y) {
		g.setColor(base);
		int len = size - 2;
		int xx = x;
		int yy = y - 1;
		while (len >= 2) {
			xx++;
			yy++;
			g.fillRect(xx, yy, len, 1);
			len -= 2;
		}
	}

	protected void paintArrowBevel(Graphics g, Color base, int x, int y) {
		int len = size;
		int xx = x;
		int yy = y;
		Color c2 = Guitilities.deriveColorHSB(base, 0f, 0f, (-DB) * (size / 2));
		while (len >= 2) {
			c2 = Guitilities.deriveColorHSB(c2, 0f, 0f, DB);
			g.setColor(c2);
			g.fillRect(xx, yy, 1, 1);
			g.fillRect(xx + len - 1, yy, 1, 1);
			len -= 2;
			xx++;
			yy++;
		}

	}

	public static void main(String args[]) {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.add(panel);

		panel.add(new JLabel("north", new ArrowIcon(ArrowIcon.NORTH), JLabel.CENTER));
		panel.add(new JLabel("west", new ArrowIcon(ArrowIcon.WEST), JLabel.CENTER));
		panel.add(new JLabel("south", new ArrowIcon(ArrowIcon.SOUTH), JLabel.CENTER));
		panel.add(new JLabel("east", new ArrowIcon(ArrowIcon.EAST), JLabel.CENTER));
		panel.add(new JLabel("east-20", new ArrowIcon(ArrowIcon.EAST, 20, Color.blue), JLabel.CENTER));

		frame.pack();
		frame.setVisible(true);
	}
}