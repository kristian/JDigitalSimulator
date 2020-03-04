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
package lc.kra.jds.components.buildin.alu;

import static lc.kra.jds.Utilities.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.components.Component;
import lc.kra.jds.components.Sociable;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * ALU 74181 (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class ALU74181 extends Component implements Sociable {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.alu."+ALU74181.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.alu", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	private transient Shape shape;

	private Dimension size;

	private InputContact[] inputs, inputsA, inputsB, inputsS;
	private transient boolean m, nc, a[], b[], s[], f[];
	private OutputContact[] outputs, outputsF;
	private Contact[] contacts;

	public ALU74181() {
		size = new Dimension(75, 75+4*20);
		makeShape();

		inputs = new InputContact[] {
				new InputContact(this), //M
				new InputContact(this), //Cn
		};
		inputsA = new InputContact[] {
				new InputContact(this), //A0
				new InputContact(this), //A1
				new InputContact(this), //A2
				new InputContact(this), //A3
		}; a = new boolean[inputsA.length];
		inputsB = new InputContact[] {
				new InputContact(this), //B0
				new InputContact(this), //B1
				new InputContact(this), //B2
				new InputContact(this), //B3
		}; b = new boolean[inputsB.length];
		inputsS = new InputContact[] {
				new InputContact(this), //S0
				new InputContact(this), //S1
				new InputContact(this), //S2
				new InputContact(this), //S3
		}; s = new boolean[inputsS.length];
		outputs = new OutputContact[] {
				new OutputContact(this), //P
				new OutputContact(this), //G
				new OutputContact(this), //Cn+4
				new OutputContact(this), //A=B
		};
		outputsF = new OutputContact[] {
				new OutputContact(this), //F0
				new OutputContact(this), //F1
				new OutputContact(this), //F2
				new OutputContact(this)  //F3
		}; f = new boolean[outputsF.length];
		contacts = ContactUtilities.concatenateContacts(inputs, inputsA, inputsB, inputsS, outputs, outputsF);
		setContactLocations();
	}

	@Override public void paint(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		((Graphics2D)graphics).draw(shape);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawString("ALU", 30, 14);
		graphics.drawString("74181", 25, 30);
		graphics.setFont(graphics.getFont().deriveFont(8f));
		graphics.drawString("M", 10, 56);
		graphics.drawString("Cn", 10, 64);
		graphics.drawString("\u00af", size.width-14, 19);
		graphics.drawString("P", size.width-14, 20);
		graphics.drawString("\u00af", size.width-14, 28);
		graphics.drawString("G", size.width-14, 29);
		graphics.drawString("Cn+4", size.width-28, 46);
		graphics.drawString("A=B", size.width-24, 64);
		for(int number=0;number<=3;number++) {
			int top = 75+number*20;
			graphics.drawRect(5, top, size.width-10, 20);
			graphics.drawString("\u00af", 11, top+9);
			graphics.drawString("A"+number, 11, top+9);
			graphics.drawString("\u00af", 11, top+17);
			graphics.drawString("B"+number, 11, top+18);
			graphics.drawString("S"+number, 10, 12+number*10);
			graphics.drawString("\u00af", size.width-18, top+12);
			graphics.drawString("F"+number, size.width-18, top+13);
		}
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Dimension getSize() { return size; }
	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		boolean h[] = new boolean[8], nm=!inputs[0].isCharged(), cn=inputs[1].isCharged();

		/* first check if there was any change to the inputs, reduces calculation! */
		boolean c = nm!=this.m|cn!=this.nc; this.m = nm; this.nc = cn;
		for(int i=0;i<4;i++) {
			boolean a = inputsA[i].isCharged(); if(a!=this.a[i]) { this.a[i] = a; c = true; }
			boolean b = inputsB[i].isCharged(); if(b!=this.b[i]) { this.b[i] = b; c = true; }
			boolean s = inputsS[i].isCharged(); if(s!=this.s[i]) { this.s[i] = s; c = true; }
		} //if(!c) return; // no change

		h[0] = !( ( b[3]& s[3] & a[3]) |
		          ( a[3]& s[2] &!b[3]) );
		h[1] = !( (!b[3]& s[1]) |
				  ( s[0]& b[3]) |
				  ( a[3]) );

		h[2] = !( ( b[2]& s[3]& a[2]) |
				  ( a[2]& s[2]&!b[2]) );
		h[3] = !( (!b[2]& s[1]) |
				  ( s[0]& b[2]) |
				  ( a[2]) );

		h[4] = !( ( b[1]& s[3]& a[1]) |
				  ( a[1]& s[2]&!b[1]) );
		h[5] = !( (!b[1]& s[1]) |
				  ( s[0]& b[1]) |
				  ( a[1]) );

		h[6] = !( ( b[0]& s[3]& a[0]) |
				  ( a[0]& s[2]&!b[0]) );
		h[7] = !( (!b[0]& s[1]) |
				  ( s[0]& b[0]) |
				  ( a[0]) );

		f[0] = ( h[6]&!h[7]) ^
			   (!(nm&cn));
		f[1] = ( h[4]&!h[5]) ^
		       (!((cn&h[6]&nm)|(h[7]&nm)) );
		f[2] = ( h[2]&!h[3]) ^
			   (!( (h[4]&h[6]&cn&nm) | (h[4]&h[7]&nm) | (h[5]&nm) ) );	   
		f[3] = ( (h[0]&!h[1]) ^
               (!( (h[2]&h[4]&h[6]&cn&nm) | (h[2]&h[4]&h[7]&nm) | (h[2]&h[5]&nm) | (h[3]&nm))));
		outputsF[0].setCharged(f[0]);
		outputsF[1].setCharged(f[1]);
		outputsF[2].setCharged(f[2]);
		outputsF[3].setCharged(f[3]);

		outputs[0].setCharged((h[0]&h[2]&h[4]&h[6])); //^P
		boolean g = ((h[1]) | (h[0]&h[3]) | (h[0]&h[2]&h[5]) | (h[0]&h[2]&h[4]&h[7]));
		outputs[1].setCharged(g); //^G
		outputs[2].setCharged(((!g)|(!(h[0]&h[2]&h[4]&h[6]&cn)))); //Cn+4
		outputs[3].setCharged(f[0]&f[1]&f[2]&f[3]); //A=B
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		makeShape();
		a = new boolean[inputsA.length]; b = new boolean[inputsB.length]; s = new boolean[inputsS.length]; f = new boolean[outputsF.length];
	}

	private void makeShape() {
		shape = new Polygon(new int[]{9 , 9 , 5 , 5, size.width-5, size.width-5, size.width-9, size.width-9},
				new int[]{75, 70, 70, 0, 0, 70, 70, 75}, 8);
	}

	protected void setContactLocations() {
		inputs[0].setLocation(new Point(0, 52)); //M
		inputs[1].setLocation(new Point(0, 60)); //Cn
		outputs[0].setLocation(new Point(size.width, 17)); //P
		outputs[1].setLocation(new Point(size.width, 26)); //G
		outputs[2].setLocation(new Point(size.width, 43)); //Cn+4
		outputs[3].setLocation(new Point(size.width, 61)); //A=B
		for(int number=0;number<4;number++) {
			int top = 63+number*20;
			inputsA[number].setLocation(new Point(0, top+18)); //A
			inputsB[number].setLocation(new Point(0, top+28)); //B
			inputsS[number].setLocation(new Point(0, 9+number*10)); //S
			outputsF[number].setLocation(new Point(size.width, top+22)); //F
		}
	}
}