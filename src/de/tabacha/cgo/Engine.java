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
package de.tabacha.cgo;

/** Implementing classes think about positions
    and try to find the best moves.
    "This is where the intelligence is located."
   <br> $Id: Engine.java,v 1.6 2004/07/30 21:05:58 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.6 $
*/
public interface Engine
{

    /** Returns the general (human-readable) name of the routine.
	Must not return null.
     */
    public String getName();

    /** Returns an internal identification string
	in addition to the official name.
	Must not return null.
     */
    public String getVersion();

    /** Returns a longer text about this routine.
	Just write a few lines of general information.
     */
    public String getDescription();

    /** Returns the name of the author.
	Should be the same as in the author tag
	in the documentation comment for your class,
	maybe with an eMail address.
    */
    public String getAuthor();

    /** Returns true if routine can play in direction UP and DOWN.
	Returns false if routine can only play in direction UP.
	<br />
	Most routines will return false here; it is easier to implement
	a strategy that always play in the same direction.
	But some routines may want to use different strategies
	for different playing directions. And for the human
	players, this option is necessary.
     */
    public boolean canPlayBothSides();

    /** Starts a new game.
	For initialization.
	@param randSeed For initializing a random number generator.
	Makes it possible to repeat a game entirely; useful for testing.
     */
    public void initGame(long randSeed);

    /** Routine thinks here.
	@param position The actual position
	@return The move the routine makes
    */
    public Move think(Board position);

}
