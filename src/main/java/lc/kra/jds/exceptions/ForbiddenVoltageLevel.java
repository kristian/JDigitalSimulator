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
package lc.kra.jds.exceptions;

public class ForbiddenVoltageLevel extends RuntimeException {
	private static final long serialVersionUID = -1672766943560975795L;
	public ForbiddenVoltageLevel(float voltage) { this(voltage, null); }
	public ForbiddenVoltageLevel(float voltage, float[] level) {
		super(getMessage(voltage, level));
	}
	private static String getMessage(float voltage, float[] level) {
		StringBuilder message = new StringBuilder("The voltage level of "+voltage+" is not permitted in this context.");
		if(level!=null)
			if(level.length<=4) //allowed voltage level
				message.append(" (The allowed voltage level is between ").append(level[0]).append(" and ").append(level[1])
				.append("and between ").append(level[2]).append(" and ").append(level[3])
				.append(')');
			else if(level.length<=2) //forbidden voltage level
				message.append(" (The forbbidden voltage level is between ").append(level[0]).append(" and ").append(level[1])
				.append(')');
		return message.toString();
	}
}
