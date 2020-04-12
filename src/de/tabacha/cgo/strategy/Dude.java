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
package de.tabacha.cgo.strategy;

import de.tabacha.cgo.*;


/** Dumb strategy, mainly for testing purposes.
    <br />$Id: Dude.java,v 1.13 2004/12/23 21:42:44 mk Exp $
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.13 $
    @threadsafe true No data held
*/
public class Dude
    extends AbstractEngine
{

    /** Constructor.
     */
    public Dude()
    { super(); }

    public String getVersion()
    { return "1 (The Dude)"; }

    public String getAuthor()
    { return "michael@tabacha.de"; }

    /** Returns the best move.
	Strategy:
	<ul>
	  <li>If the ball can jump forward, do it.</li>
	  <li>Otherwise, set a piece in front of the ball.</li>
	</ul>
	This is the straight way to victory.
     */
    public Move think(Board position)
    {
	Field stepAhead = position.getBall().fieldIn(N);
	if (position.on(stepAhead) == PIECE)
	    return new Jump(new byte[]{N});
	else
	    return new Put(stepAhead);
    }

}
