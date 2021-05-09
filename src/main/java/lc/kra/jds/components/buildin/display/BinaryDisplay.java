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
package lc.kra.jds.components.buildin.display;

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import static lc.kra.jds.Utilities.getTranslation;

/**
 * Binary display (build-in component)
 *
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class BinaryDisplay extends Component implements Sociable, Configurable {
    private static final long serialVersionUID = 2l;

    private static final String KEY;

    static {
        KEY = "component.display." + BinaryDisplay.class.getSimpleName().toLowerCase();
    }

    public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.display", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

    private Dimension size;

    private InputContact input;
    private Contact[] contacts;

    private String name = "Input" + this.hashCode();

    public boolean toAnalyse() {
        return analyse;
    }

    private boolean analyse = false;

    public BinaryDisplay() {
        size = new Dimension(25, 20);
        input = new InputContact(this, new Point(0, size.height / 2));
        contacts = new Contact[]{input};
    }

    @Override
    public void paint(Graphics graphics) {
        if (input.isCharged()) {
            graphics.setColor(Color.RED);
            graphics.fillOval(5, 0, size.height, size.height);
        }
        graphics.setColor(Color.BLACK);
        graphics.drawOval(5, 0, size.height, size.height);
        int corner = (int) (Math.cos(Math.PI / 4) * (size.height / 2)) - size.height / 4 + 1;
        graphics.drawLine(5 + corner, corner, 5 + size.height - corner, size.height - corner);
        graphics.drawLine(5 + corner, size.height - corner, 5 + size.height - corner, corner);
        if (analyse) graphics.drawString(name, -graphics.getFontMetrics().stringWidth(name) / 2 + size.width / 2, -7);
        ContactUtilities.paintSolderingJoints(graphics, contacts);
    }

    @Override
    public Dimension getSize() { return size; }

    @Override
    public Contact[] getContacts() { return contacts; }

    @Override
    public void calculate() { } //nothing has to be calculated

    @Override
    public Option[] getOptions() {
        return new Option[]{
                new Option("analyse", Utilities.getTranslation("component.switch.position"), Option.OptionType.BOOLEAN, false),
                new Option("name", "Name", Option.OptionType.TEXT, "Input" + this.hashCode())};
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
        return "BinaryDisplay{" +
                "input=" + input +
                ", name='" + name + '\'' +
                ", analyse=" + analyse +
                '}';
    }
}
