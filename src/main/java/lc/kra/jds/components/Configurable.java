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

import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

public interface Configurable {
	public Option[] getOptions();
	public Map<Option, Object> getConfiguration();
	public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException;

	public static class Option {
		public enum OptionType {
			NUMBER(Number.class),
			BOOLEAN(Boolean.class),
			TEXT(String.class),
			LIST((new Object[0]).getClass()),
			FILE(FileFilter.class);
			private Class<?> cls;
			private OptionType(Class<?> cls) { this.cls = cls; }
			public boolean checkType(Object object) {
				return this.cls.isAssignableFrom(object.getClass());
			}
		}
		private final String key, label;
		private final OptionType type;
		private final Object value;
		public Option(String label, OptionType type) { this(label, type, null); }
		public Option(String label, OptionType type, Object dfault) { this(label.toLowerCase().replaceAll("\\W", new String()), label, type, dfault);	}
		public Option(String key, String label, OptionType type) { this(key, label, type, null); }
		public Option(String key, String label, OptionType type, Object value) {
			this.key = key; this.label = label; this.type = type;
			if(type.equals(OptionType.LIST)&&value==null)
				throw new NullPointerException("You need to provide a default value for option "+key+", because it is of type 'list'.");
			if(value!=null&&!type.checkType(value))
				throw new ClassCastException("Default value of option "+key+" has to be of type "+type.toString().toLowerCase()+" but is actually of type "+value.getClass().getSimpleName());
			this.value = value;
		}
		public String getKey() { return key; }
		public String getLabel() { return label; }
		public OptionType getType() { return type; }
		public Object getDefault() { return value; }
		public boolean hasDefault() { return value!=null; }
		@Override public int hashCode() { return this.key.hashCode(); }
		@Override public boolean equals(Object object) {
			if(!(object instanceof Option))
				return false;
			return this.key.equals(((Option)object).key);
		}
	}
}