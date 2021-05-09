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
package lc.kra.jds.components.buildin.general;

import static lc.kra.jds.Utilities.getTranslation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import lc.kra.jds.Utilities;
import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Configurable;
import lc.kra.jds.components.Configurable.Option.OptionType;
import lc.kra.jds.gui.Guitilities;

/**
 * Text (build-in component)
 *
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class Text extends Component implements Configurable {
    private static final long serialVersionUID = 2l;

    private static final String KEY;

    static {
        KEY = "component.general." + Text.class.getSimpleName().toLowerCase();
    }

    public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.general", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

    private Dimension size;

    private String text;
    private Font font;

    public Text() {
        text = Utilities.getTranslation("component.text.default");
        font = new Font("Dialog", Font.BOLD, 14);
        size = Guitilities.getStringBounds(font, text);
    }

    @Override
    public void paint(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(text, 0, size.height / 2 + metrics.getAscent() / 2);
    }

    @Override
    public Dimension getSize() { return size; }

    @Override
    public void calculate() { } //there is nothing to calculate

    @Override
    public Option[] getOptions() {
        return new Option[]{new Option("text", Utilities.getTranslation("component.text.text"), OptionType.TEXT, "Text"),
                new Option("size", Utilities.getTranslation("component.text.size"), OptionType.NUMBER, 14)};
    }

    @Override
    public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
        String text = configuration.get(getOptions()[0]).toString().trim();
        int size = (Integer) configuration.get(getOptions()[1]);
        if (text.isEmpty()) throw new PropertyVetoException(Utilities.getTranslation("component.text.text.error"), null);
        if (size < 5) throw new PropertyVetoException(Utilities.getTranslation("component.text.size.minimum", 5), null);
        if (size > 72) throw new PropertyVetoException(Utilities.getTranslation("component.text.size.maximum", 72), null);
        this.text = text;
        this.size = Guitilities.getStringBounds(font, text);
        this.font = new Font("Dialog", Font.BOLD, size);
    }

    @Override
    public Map<Option, Object> getConfiguration() {
        Map<Option, Object> configuration = new HashMap<Option, Object>();
        configuration.put(getOptions()[0], text);
        configuration.put(getOptions()[1], font.getSize());
        return configuration;
    }

    public String getText() { return text; }
}
