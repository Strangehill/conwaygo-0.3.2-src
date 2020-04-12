/*  ConwayGo: also called philosophers' football, a game on a go board
    Copyright (C) 2004  Michael Keuchen
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package de.tabacha.cgo.gui;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JRootPane;


/** Class with main method for starting ConwayGo GUI.
   <br> $Id: ConwayGo.java,v 1.10 2004/07/30 22:14:53 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.10 $
   @threadsafe false
*/
public class ConwayGo
    extends JFrame
{

    /** Main method.
	Parameters are ignored.
     */
    public static void main(String[] args)
    {
	JFrame frame = new ConwayGo();
	((CgoRootPane)frame.getRootPane()).startGame();
	frame.show();
    }

    /** Constructor.
     */
    private ConwayGo()
    {
	super("ConwayGo");
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));

	pack();
    }

    /** Called by the constructor methods to create the default rootPane.
     */
    protected JRootPane createRootPane()
    { return new CgoRootPane(); }

}
