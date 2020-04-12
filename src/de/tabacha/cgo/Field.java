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

import java.io.Serializable;


/** Just one field on the 19*19 board.
    <p>
    The minimum value of the row and the column is 0,
    the maximum value is ROW_COUNT-1 and COL_COUNT-1.
    (Both are defined in Constants.)
    But this class won't complain if the values are out
    of range; use method isOutside to test this.
    </p>
    This class does not
    contain any information about the board
    and the pieces thereon. To get these informations
    you have to use the methods of the class
    {@link Board}.
    <br> $Id: Field.java,v 1.8 2004/12/23 21:42:43 mk Exp $
    @threadsafe false
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.8 $
    @threadsafe false for performance
*/
public final class Field
    implements Cloneable, Serializable, Constants
{
    
    private int col;
    private int row;


    /** Constructor.
	@param row The row of the field
	@param col The column of the field

    */
    public Field(int col, int row)
    {
        this.col = col;
        this.row = row;
    }

    /** Copy constructor.
	@param otherField The field to be copied.
	@throws NullPointerException If otherField is null.
    */
    public Field (Field otherField)
    {
        this.col = otherField.col;
        this.row = otherField.row;
    }

    public Object clone()
    { return new Field(this); }
    
    /** Returns the field that lies one step from this field
	in the specified direction.
	This field isn't changed.
       @param direction One of the directions defined in Constants.
       @return A new field.
    */
    public final Field fieldIn(byte direction)
    { return new Field(col + DCOL[direction], row + DROW[direction]); }

    /** Changes the location of this field.
	Moves one step in the specified direction.
    */
    public final void moveIn(byte direction)
    {
	col += DCOL[direction];
	row  += DROW[direction];
    }
    
    public boolean equals(Object obj)
    {
	return ( (obj instanceof Field)
		 && (col == ((Field)obj).col)
		 && (row == ((Field)obj).row) );
    }
    
    /** Returns the equivalent field on the other half of the
	board, mirrored along the central row.
    */
    public final Field upsideDown()
    { return new Field(col, ROW_COUNT-1-row); }

    public int hashCode()
    { return col + row; }

    /** Returns true if this field is not on the board.
     */
    public final boolean isOutside()
    { return isOutside(col, row); }

    /** Returns true if the field with the specified row and
	column is not on the board.
    */
    public static boolean isOutside(int col, int row)
    {
	return ( (0 > col) || (col >= COL_COUNT) ||
	         (0 > row) || (row >= ROW_COUNT) );
    }


    /** Returns true if this field is in one of the goals.
     */
    public final boolean isInGoal()
    { return ( isInGoalOf(UP) || isInGoalOf(DOWN) ); }

    /** Returns true if this field is in the goal of the specified player.
	@param player One of the constants UP or DOWN defined in Constants
    */
    public final boolean isInGoalOf(boolean player)
    {
	if (player == DOWN)
	    return ( (row >= ROW_COUNT-1) || 
		     ((row == ROW_COUNT-1) && (0 <= col) && (col < COL_COUNT)) );
	// player == UP
	return ( (row < 0) ||
	         ((row == 0) && (0 <= col) && (col < COL_COUNT)) );
    }

    public final int col()
    { return col; }

    public final int row()
    { return row; }

    public void setCol(int newCol)
    { this.col = newCol; }

    public void setRow(int newRow)
    { this.row = newRow; }

    public String toString()
    { return "(" + col + "," + row + ")"; }

}
