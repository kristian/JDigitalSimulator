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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class GradientPanel extends JPanel {
	private static final long serialVersionUID = 1l;

	private final Color color_a, color_b;
	private Image image;

	public GradientPanel(Color color_a, Color color_b) {
		setOpaque(false);
		setBackground(color_a);
		this.color_a = color_a;
		this.color_b = color_b;
	}

	protected Image getGradientImage() {
		Dimension size = getSize();
		if (image==null||image.getWidth(null) !=size.width
				||image.getHeight(null)!=size.height)
			image = Guitilities.createGradientImage(size.width, size.height, color_a, color_b);
		return image;
	}

	@Override protected void paintComponent(Graphics graphics) {
		Image gradientImage = getGradientImage();
		graphics.drawImage(gradientImage, 0, 0, null);
		super.paintComponent(graphics);
	}
}