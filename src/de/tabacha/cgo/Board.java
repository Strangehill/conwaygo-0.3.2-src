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

/** A data model for (conway)go boards.
 * <br>
 * Why using an interface when there is only one implementation?
 * Because there isn't only one, one of the AI classes provides an own
 * read-only implementation, and you're free to write others.
 * A synchronized implementation does not exist yet.
   <br> $Id: Board.java,v 1.11 2004/12/23 21:42:43 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.11 $
*/
public interface Board
    extends Cloneable
{

    /** Initializes the field, setting it to the start position.
     */
    public void reset();

    /** Returns a copy of this board mirrored along the x-axis.
    */
    public Board upsideDown();

    /** Returns a deep copy of this object.
     */
    public Object clone();

    /** Returns this object as a two-dimensional array containing the contents of the fields.
     */
    public byte[][] toArray();


    /** Returns the player who has to move.
	@return UP or DOWN, declared in Constants.
    */
    public boolean whoseTurn();


    /** Returns the content of the field on the specified row and column.
	@return EMPTY, PIECE or BALL; declared in Constants.
    */
    public byte on(int col, int row);

    /** Returns the content of the specified field on the board.
	@return EMPTY, PIECE or BALL; declared in Constants.
     */
    public byte on(Field field);

    /** Returns the actual field of the ball.
     */
    public Field getBall();

    /** Sets the actual field of the ball.
     */
    public void setBall(Field newBallField);

    /** Sets the actual field of the ball.
     */
    public void setBall(int col, int row);

    /** Puts a piece (aka player) on the board.
	No warning if the field wasn't empty.
     */
    public void addPiece(Field f);

    /** Puts a piece (aka player) on the board.
	No warning if the field wasn't empty.
     */
    public void addPiece(int col, int row);

    /** Removes a piece (aka player) from the board.
	No warning if there wasn't a piece on this field.
    */
    public void removePiece(Field f);

    /** Removes a piece (aka player) from the board.
	No warning if there wasn't a piece on this field.
    */
    public void removePiece(int col, int row);


    /** Makes a move.
	If successfully performed, it's the next player's turn.
       @exception InvalidMoveException If the move was not correct.
    */
    public void move(Move move)
	throws InvalidMoveException;

}
