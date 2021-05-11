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
package lc.kra.jds.components.buildin.switches;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Interactable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.OutputContact;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import static lc.kra.jds.Utilities.getTranslation;

/**
 * Switch (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Switch extends Component implements Sociable, Interactable {
    private static final long serialVersionUID = 2l;

    private static final String KEY;
    static { KEY = "component.switches."+Switch.class.getSimpleName().toLowerCase(); }
    public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.switches", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

    private Dimension size;

    private OutputContact output;
    private Contact[] contacts;

    public Switch() {
        size = new Dimension(28, 28);
        output = new OutputContact(this, new Point(size.width, size.height/2));
        output.setCharged(false);
        contacts = new Contact[] {output};
    }

    @Override public void paint(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        if(output.isCharged())
            graphics.drawLine(10, 5, 23, 14);
        else graphics.drawLine(10, 23, 23, 14);
        graphics.drawString("1", 0, 10);
        graphics.drawString("0", 0, 28);
        ContactUtilities.paintSolderingJoints(graphics, contacts);
    }

    @Override public Dimension getSize() { return size; }
    @Override public Contact[] getContacts() { return contacts;	}
    @Override public void calculate() { } //the output is always charged like the switch

    @Override public void mouseClick(MouseEvent event) { output.setCharged(!output.isCharged()); }
    @Override public void mouseDoubleClick(MouseEvent event) { }
    @Override public void mouseDown(MouseEvent event) { }
    @Override public void mouseUp(MouseEvent event) { }
}
