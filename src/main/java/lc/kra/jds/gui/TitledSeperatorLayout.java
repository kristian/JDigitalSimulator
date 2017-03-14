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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class TitledSeperatorLayout implements LayoutManager {
	@Override
	public void addLayoutComponent(String s, Component component) {}

	@Override
	public void removeLayoutComponent(Component component) {}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Component label = getLabel(parent);
		Dimension labelSize = label.getPreferredSize();
		Insets insets = parent.getInsets();
		int width = labelSize.width + insets.left + insets.right;
		int height = labelSize.height + insets.top + insets.bottom;
		return new Dimension(width, height);
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Dimension size = parent.getSize();
			Insets insets = parent.getInsets();
			int width = size.width - insets.left - insets.right;

			JComponent label = getLabel(parent);
			Dimension labelSize = label.getPreferredSize();
			int labelWidth = labelSize.width;
			int labelHeight = labelSize.height;
			Component separator1 = parent.getComponent(1);
			int separatorHeight = separator1.getPreferredSize().height;

			FontMetrics metrics = label.getFontMetrics(label.getFont());
			int ascent = metrics.getMaxAscent();
			int hGapDlu = centerSeparators ? 3 : 1;
			int hGap = hGapDlu + 2;
			int vOffset = centerSeparators ? 1 + (labelHeight - separatorHeight) / 2 : ascent - separatorHeight / 2;

			int alignment = 2;
			if(label instanceof JLabel) alignment = ((JLabel) label).getHorizontalAlignment();

			int y = insets.top;
			if (alignment == 2) {
				int x = insets.left;
				label.setBounds(x, y, labelWidth, labelHeight);
				x += labelWidth;
				x += hGap;
				int separatorWidth = size.width - insets.right - x;
				separator1.setBounds(x, y + vOffset, separatorWidth, separatorHeight);
			} else if (alignment == 4) {
				int x = (insets.left + width) - labelWidth;
				label.setBounds(x, y, labelWidth, labelHeight);
				x -= hGap;
				int separatorWidth = --x - insets.left;

				separator1.setBounds(insets.left, y + vOffset, separatorWidth, separatorHeight);
			} else {
				int xOffset = (width - labelWidth - 2 * hGap) / 2;
				int x = insets.left;
				separator1.setBounds(x, y + vOffset, xOffset - 1, separatorHeight);
				x += xOffset;
				x += hGap;
				label.setBounds(x, y, labelWidth, labelHeight);
				x += labelWidth;
				x += hGap;
				Component separator2 = parent.getComponent(2);
				int separatorWidth = size.width - insets.right - x;
				separator2.setBounds(x, y + vOffset, separatorWidth, separatorHeight);
			}
		}
	}

	private JComponent getLabel(Container parent) {
		return (JComponent) parent.getComponent(0);
	}
	private final boolean centerSeparators;

	public TitledSeperatorLayout(boolean centerSeparators) {
		this.centerSeparators = centerSeparators;
	}
}