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

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Interactable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.OutputContact;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import static lc.kra.jds.Utilities.getTranslation;

/**
 * Switch (build-in component)
 *
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Switch extends Component implements Sociable, Interactable, Configurable {
    private static final long serialVersionUID = 2l;

    private static final String KEY;

    static {
        KEY = "component.switches." + Switch.class.getSimpleName().toLowerCase();
    }

    public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.switches", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

    private Dimension size;

    private OutputContact output;
    private Contact[] contacts;

    private String name = "Input" + this.hashCode();

    public boolean toAnalyse() {
        return analyse;
    }

    private boolean analyse = false;

    public Switch() {
        size = new Dimension(28, 28);
        output = new OutputContact(this, new Point(size.width, size.height / 2));
        output.setCharged(false);
        contacts = new Contact[]{output};
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        if (output.isCharged())
            graphics.drawLine(10, 5, 23, 14);
        else graphics.drawLine(10, 23, 23, 14);
        graphics.drawString("1", 0, 10);
        graphics.drawString("0", 0, 28);
        if (analyse) graphics.drawString(name, -graphics.getFontMetrics().stringWidth(name) / 2 + size.width / 2, -7);
        ContactUtilities.paintSolderingJoints(graphics, contacts);
    }

    @Override
    public Dimension getSize() { return size; }

    @Override
    public Contact[] getContacts() { return contacts; }

    @Override
    public void calculate() { } //the output is always charged like the switch

    @Override
    public void mouseClick(MouseEvent event) { output.setCharged(!output.isCharged()); }

    @Override
    public void mouseDoubleClick(MouseEvent event) { }

    @Override
    public void mouseDown(MouseEvent event) { }

    @Override
    public void mouseUp(MouseEvent event) { }

    @Override
    public Option[] getOptions() {
        return new Option[]{
                new Option("analyse", Utilities.getTranslation("component.switch.position"), Option.OptionType.BOOLEAN, false),
                new Option("name", "Name", Option.OptionType.TEXT, "Input" + this.hashCode())
        };
    }

    @Override
    public Map<Option, Object> getConfiguration() {
        Map<Option, Object> configuration = new HashMap<Option, Object>();
        configuration.put(getOptions()[0], analyse);
        configuration.put(getOptions()[1], name);
        return configuration;
    }

    @Override
    public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
        analyse = (boolean) configuration.get(getOptions()[0]);
        NAMES.remove(name);
        String text = configuration.get(getOptions()[1]).toString().trim();
        if (NAMES.contains(text)) throw new PropertyVetoException(Utilities.getTranslation("component.switches.error"), null);
        this.name = text;
        NAMES.add(text);
    }

    @Override
    public String toString() {
        return "PushSwitch{" +
                "output=" + output +
                ", name='" + name + '\'' +
                ", analyse=" + analyse +
                '}';
    }
}
