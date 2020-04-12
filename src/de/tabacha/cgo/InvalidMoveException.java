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

/** Indicates that a move returned by a routine is invalid.
   <br> $Id: InvalidMoveException.java,v 1.4 2004/12/23 21:42:43 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.4 $
*/
public class InvalidMoveException
    extends Exception
{
    
    /** The invalid move. */
    private Move move;

    /** Constructor.
	@param message error message
	@param move The invalid move itself
    */
    public InvalidMoveException(String message, Move move)
    {
        super(message);
        this.move = move;
    }

    /** Returns the invalid move.
    */
    public Move getMove()
    { return move; }
 
}
