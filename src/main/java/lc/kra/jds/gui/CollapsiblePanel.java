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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CollapsiblePanel extends JPanel {
	private static final long serialVersionUID = 1l;

	public enum Orientation{HORIZONTAL, VERTICAL}

	private JPanel panel;
	private JComponent child;

	private JCheckBox expand;
	private Orientation orientation = Orientation.VERTICAL;
	private boolean expanded = true;

	public CollapsiblePanel(JComponent child) {
		this(child, Orientation.VERTICAL);
	}

	public CollapsiblePanel(JComponent child, Orientation orientation) {
		this.orientation = orientation; this.child = child;
		setLayout(new BorderLayout());
		panel = new JPanel(new BorderLayout());
		panel.add(child, BorderLayout.CENTER);
		add(panel, BorderLayout.CENTER);
	}

	public CollapsiblePanel(JComponent child, String title, String tooltip) {
		this(child, Orientation.VERTICAL, title, tooltip);
	}

	public CollapsiblePanel(JComponent child, String title) {
		this(child, Orientation.VERTICAL, title, null);
	}

	public CollapsiblePanel(JComponent child, Orientation orientation, String title, String tooltip) {
		this(child, orientation);
		add(createCollapseControl(title, tooltip), orientation == Orientation.HORIZONTAL ? BorderLayout.WEST : BorderLayout.NORTH);
	}

	protected Component createCollapseControl(String title, String tooltip) {
		Box box = Box.createHorizontalBox();
		expand = new JCheckBox(title);
		expand.setBorder(new EmptyBorder(0, 4, 0, 0));
		expand.setToolTipText(tooltip);
		expand.setHorizontalTextPosition(JCheckBox.RIGHT);
		expand.setIcon(new ArrowIcon(ArrowIcon.EAST));
		setCollapsedIcon(new ArrowIcon(ArrowIcon.EAST));
		setExpandedIcon(new ArrowIcon(ArrowIcon.SOUTH));
		expand.setSelected(isExpanded());
		expand.setFocusPainted(false);
		expand.addChangeListener(new CollapseListener());
		box.add(expand);
		return box;
	}

	public void setExpanded(boolean expanded) {
		if(this.expanded!=expanded) {
			if(expand != null)
				expand.setSelected(expanded);
			this.expanded = expanded;
			Dimension size = child.getPreferredSize();
			if(orientation!=Orientation.HORIZONTAL)
				this.setCollapseHeight(size, !expanded ? 0 : size.height);
			else this.setCollapseWidth(size, !expanded ? 0 : size.width);
			firePropertyChange("expanded", !expanded, expanded);
		}
	}

	public void setCollapseHeight(Dimension size, int height) {
		panel.setPreferredSize(new Dimension(size.width, height));
		child.revalidate();
		repaint();
	}
	public void setCollapseWidth(Dimension size, int width) {
		panel.setPreferredSize(new Dimension(width, size.height));
		child.revalidate();
		repaint();
	}

	public boolean isExpanded() { return expanded; }
	public void setExpandedIcon(Icon icon) {
		if(expand!=null) {
			expand.setSelectedIcon(icon);
			expand.setRolloverSelectedIcon(icon);
		}
	}
	public void setCollapsedIcon(Icon icon) {
		if(expand!=null) {
			expand.setIcon(icon);
			expand.setRolloverIcon(icon);
		}
	}

	@Override public void setFont(Font font) {
		super.setFont(font);
		if(expand!=null)
			expand.setFont(font);
	}
	@Override public void setForeground(Color foreground) {
		super.setForeground(foreground);
		if(expand!=null)
			expand.setForeground(foreground);
	}
	@Override
	public void updateUI() {
		super.updateUI();
		configureDefaults();
	}
	protected void configureDefaults() {
		if (expand != null)
			if(UIManager.getLookAndFeel().getName().equals("Nimbus"))
				expand.setBorder(new EmptyBorder(0, 4, 0, 0));
			else expand.setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	private class CollapseListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent event) {
			setExpanded(expand.isSelected());
		}
	}
}