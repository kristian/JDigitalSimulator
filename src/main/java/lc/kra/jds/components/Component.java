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
package lc.kra.jds.components;

import lc.kra.jds.Locatable;
import lc.kra.jds.Moveable;
import lc.kra.jds.Paintable;
import lc.kra.jds.Utilities;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;
import lc.kra.jds.exceptions.LocationOutOfBoundsException;
import lc.kra.jds.exceptions.WireNotConnectable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.*;

import static lc.kra.jds.Utilities.getField;

public abstract class Component implements Paintable, Locatable, Moveable, Cloneable, Serializable {
    private static final long serialVersionUID = 1l;

    public static final ComponentAttributes componentAttributes = null;
    public static final ArrayList<String> NAMES;

    static {
        NAMES = new ArrayList<>();
    }

    private Point location;
    protected PropertyChangeSupport change;

    public Component() { this.location = new Point(); }

    public Component(Point location) { this.setLocation(location); }

    public abstract Dimension getSize();

    @Override
    public void moveTo(Point location) { this.setLocation(location); }

    @Override
    public void moveRelative(Point location) {
        if (this.location.x + location.x < 0 || this.location.y + location.y < 0)
            throw new LocationOutOfBoundsException();
        Point oldLocation = new Point(this.location);
        this.location.translate(location.x, location.y);
        firePropertyChange("location", oldLocation, this.location);
    }

    @Override
    public final Point getLocation() { return this.location; }

    @Override
    public final void setLocation(Point location) throws LocationOutOfBoundsException {
        if (location.x < 0 || location.y < 0)
            throw new LocationOutOfBoundsException();
        firePropertyChange("location", new Point(this.location), this.location = location);
    }

    protected void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) return;
        else if (change == null) change = new PropertyChangeSupport(this);
        change.addPropertyChangeListener(listener);
    }

    protected void addPropertyChangeListener(String name, PropertyChangeListener listener) {
        if (listener == null) return;
        else if (change == null) change = new PropertyChangeSupport(this);
        change.addPropertyChangeListener(name, listener);
    }

    protected PropertyChangeListener[] getPropertyChangeListeners(String name) {
        if (change == null) return new PropertyChangeListener[0];
        return change.getPropertyChangeListeners(name);
    }

    protected void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || change == null) return;
        change.removePropertyChangeListener(listener);
    }

    protected void removePropertyChangeListener(String name, PropertyChangeListener listener) {
        if (listener == null || change == null) return;
        change.removePropertyChangeListener(name, listener);
    }

    private void firePropertyChange(String name, Object oldValue, Object newValue) {
        if (change == null || (oldValue != null && newValue != null && oldValue.equals(newValue)))
            return;
        change.firePropertyChange(name, oldValue, newValue);
    }

    public abstract void calculate();

    public final ComponentAttributes getAttributes() { return Component.getAttributes(this.getClass()); }

    public final static ComponentAttributes getAttributes(Class<? extends Component> cls) {
        return getField(cls, "componentAttributes", new ComponentAttributes(cls.getSimpleName(), "group.general"));
    }

    @Override
    public Component clone() throws CloneNotSupportedException {
        Component clone = null;
        if ((clone = (Component) Utilities.copy(this)) instanceof Sociable)
            for (Contact contact : ((Sociable) clone).getContacts())
                contact.removeWires();
        return clone;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        PropertyChangeSupport change = this.change;
        if (Utilities.isCopying())
            this.change = null;
        out.defaultWriteObject();
        this.change = change;
    }

    public static Set<Component> clone(Collection<Component> components) throws CloneNotSupportedException {
        HashMap<Component, Component> clonedComponents = new HashMap<Component, Component>();
        for (Component component : components) {
            Component clone = component.clone();
            clonedComponents.put(component, clone);
        }
        HashMap<Component, List<Wire>> clones = new HashMap<Component, List<Wire>>();
        List<Wire> wires;
        for (Component component : components) {
            Component clone = clonedComponents.get(component);
            clones.put(clone, wires = new Vector<Wire>());
            if (component instanceof Sociable) {
                Iterator<Contact> contacts = Arrays.asList(((Sociable) component).getContacts()).iterator(),
                        clonedContacts = Arrays.asList(((Sociable) clone).getContacts()).iterator();
                for (; contacts.hasNext() && clonedContacts.hasNext(); ) {
                    try {
                        Contact contact = contacts.next(), clonedContact = clonedContacts.next();
                        if (contact instanceof OutputContact)
                            for (Wire wire : contact.getWires()) {
                                Component target = wire.getTarget().getComponent(),
                                        cloneTarget = clonedComponents.get(target);
                                if (cloneTarget != null) {
                                    Iterator<Contact> targetContacts = Arrays.asList(((Sociable) target).getContacts()).iterator(),
                                            cloneTargetContacts = Arrays.asList(((Sociable) cloneTarget).getContacts()).iterator();
                                    for (; targetContacts.hasNext() && cloneTargetContacts.hasNext(); ) {
                                        Contact targetContact = targetContacts.next(),
                                                cloneTargetContact = cloneTargetContacts.next();
                                        if (targetContact instanceof InputContact && targetContact.equals(wire.getTarget())) {
                                            wires.add(new Wire(clonedContact, cloneTargetContact));
                                            break;
                                        }
                                    }
                                }
                            }
                    } catch (WireNotConnectable e) {
                        //well, wire could not be connected... will never happen?
                    }
                }
            }
        }
        return new HashSet<Component>(clonedComponents.values());
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface HiddenComponent {
    }

    public static class ComponentAttributes {
        public final String key, name, group, description, author;
        public final int version;

        public ComponentAttributes(String name, String group) { this(name, group, 0); }

        public ComponentAttributes(String name, String group, int version) { this(name, group, null, null, version); }

        public ComponentAttributes(String name, String group, String description, String author, int version) { this(name.replaceAll("\\W", new String()), name, group, description, author, version); }

        public ComponentAttributes(String key, String name, String group) { this(key, name, group, 0); }

        public ComponentAttributes(String key, String name, String group, int version) { this(key, name, group, null, null, version); }

        public ComponentAttributes(String key, String name, String group, String description, String author, int version) {
            this.key = key;
            this.name = name;
            this.group = group;
            this.description = description;
            this.author = author;
            this.version = version;
        }

        @Override
        public int hashCode() { return (key + group).hashCode(); }

        @Override
        public boolean equals(Object object) {
            if (super.equals(object))
                return true;
            if (!(object instanceof ComponentAttributes))
                return false;
            ComponentAttributes attributes = (ComponentAttributes) object;
            return key.equals(attributes.key) && group.equals(attributes.group);
        }
    }

    public static class ComponentFlavor extends DataFlavor {
        private static ComponentFlavor flavour;

        public ComponentFlavor() { super(Component.class, "Component"); }

        public static ComponentFlavor getFlavor() {
            if (flavour == null)
                flavour = new ComponentFlavor();
            return flavour;
        }
    }
}
