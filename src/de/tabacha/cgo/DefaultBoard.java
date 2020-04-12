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

import java.util.Arrays;
import java.io.Serializable;


/** The 19x19 board from the game Go.
   <br> $Id: DefaultBoard.java,v 1.9 2004/12/23 21:42:43 mk Exp $
   @threadsafe false
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.9 $
*/
public final class DefaultBoard
    implements Cloneable, Serializable, Constants, Board
{
    
    private byte[][] fields;
    private Field ballField;
    private boolean whoseTurn;


    /** Constructs and resets the board.
     */
    public DefaultBoard()
    {
	fields = new byte [ROW_COUNT] [COL_COUNT];
	reset();
    }
    
    /** Copy constructor.
     */
    private DefaultBoard(DefaultBoard original)
    {
	fields = new byte[ROW_COUNT][];
	for (int i=0; i<ROW_COUNT; i++)
	    fields[i] = (byte[])original.fields[i].clone();
	ballField = (Field)original.ballField.clone();
	whoseTurn = original.whoseTurn;
    }

    /** Constructor that receives direct values.
     * 	Will not test if the ball is set in the byte array at the place of the ball field.
     */
    public DefaultBoard(byte[][]fields, Field ballField, boolean whoseTurn)
    {
	this.fields = fields;
	this.ballField = ballField;
	this.whoseTurn = whoseTurn;
    }


    public Object clone()
    { return new DefaultBoard(this); }

    public byte[][] toArray()
    {
        byte[][] klon = new byte[ROW_COUNT][];
        for (int i=0; i<ROW_COUNT; i++)
            klon[i] = (byte[])fields[i].clone();
        return klon;
    }

    /** Removes all pieces from the board, set playing direction to up.
     * Does not touch the ball field. As this may lead to inconsistencies,
     * this method is private.
     */
    private void makeEmpty()
    {
    	for (int x=0; x < COL_COUNT; x++)
    	    for (int y=0; y < ROW_COUNT; y++)
    	        fields[y][x] = EMPTY;
        whoseTurn = UP;
    }

    /** Removes all pieces and the ball from the field.
     * The ball field will be outside the board.
     */
    public void clear()
    {
        makeEmpty();
        ballField = new Field(-1, -1); 
    }

    /** Sets the board to the start position.
     */
    public void reset()
    {
        makeEmpty();
        ballField = (Field)KICK_OFF_FIELD.clone();
        set(ballField, BALL);
    }

    public Board upsideDown()
    {
        byte[][] newFields = new byte[ROW_COUNT][COL_COUNT];
        for (int r=0; r < ROW_COUNT; r++)
            System.arraycopy(fields[ROW_COUNT-1-r], 0, newFields[r], 0, COL_COUNT);
        return new DefaultBoard(newFields, ballField.upsideDown(), !whoseTurn);
    }


    /** Switchs the player who has to move. */
    protected void nextTurn()
    { whoseTurn = !whoseTurn; }

    public boolean whoseTurn()
    { return whoseTurn; }


    public byte on(int col, int row)
    { return ( Field.isOutside(col,row) ? EMPTY : fields[row][col] ); }

    public byte on(Field field)
    { return ( field.isOutside() ? EMPTY : fields[field.row()][field.col()] ); }

    public Field getBall()
    { return ballField; }

    public void setBall(Field newBallField)
    {
	if (!ballField.isOutside())
	    set(ballField, EMPTY);
	this.ballField = newBallField;
	if (!ballField.isOutside())
	    set(ballField, BALL);
    }

    public void setBall(int col, int row)
    { setBall(new Field(col, row)); }

    public void addPiece(Field f)
    {
	if (on(f) == EMPTY)
	    set(f, PIECE);
    }

    public void addPiece(int col, int row)
    {
	if (on(col, row) == EMPTY)
	    set(col, row, PIECE);
    }

    public void removePiece(Field f)
    {
	if (on(f) == PIECE)
	    set(f, EMPTY);
    }

    public void removePiece(int col, int row)
    {
	if (on(col, row) == PIECE)
	    set(col, row, EMPTY);
    }


    /** Changes the contents of a field on the board.
	Will not check for validity.
	@param field The field to be changed. Must be on the board.
	@param what what to set on this field: EMPTY, PIECE or BALL
    */
    private void set(final Field field, final byte what)
    { fields[field.row()][field.col()] = what; }

    /** Changes the contents of a field on the board.
	Will not check for validity.
	@param what what to set on this field: EMPTY, PIECE or BALL
    */
    private void set(final int col, final int row, final byte what)
    { fields[row][col] = what; }
    

    public final void move(Move move)
	throws InvalidMoveException
    {
	if (move == null)
	    throw new InvalidMoveException("error.move.empty", move);
	if (move instanceof Put)
	    put((Put)move);
	else
	    jump((Jump)move);
	nextTurn();
    }

    /** Puts a piece on the board.
     * The player is not changed, so this must not be a public method.
	@exception InvalidMoveException If field is not empty or outside the board.
    */
    protected void put(Put move)
	throws InvalidMoveException
    {
	Field f = move.getField();
	if ( f.isOutside() )
		throw new InvalidMoveException("error.move.put_outside", move);
	if ( on(f) != EMPTY )
		throw new InvalidMoveException("error.move.put_not_empty", move);
	set(f, PIECE);
    }

    /** Moves the ball around the field.
	If the ball is in the goal, further jumps will be ignored.
	 The player is not changed, so this must not be a public method.
	@exception InvalidMoveException If no jumps are contained in the
	move or one of the jumps is not possible
    */
    protected void jump(Jump move)
	throws InvalidMoveException
    {
	move.startIteration();
	if (!move.hasNextLeap())
	    throw new InvalidMoveException("error.move.empty", move);
	do
	    jumpOnce(move.nextLeap(), move);
	while ( move.hasNextLeap() && !ballField.isOutside() && !ballField.isInGoal() );
	if ( ballField.isOutside() && !ballField.isInGoal() )
	    throw new InvalidMoveException("error.move.jump_outside", move);
    }

    /** Makes the ball jump once.
	@param dir Direction where to jump to. Defined in Constants.
	@param move The move this jump is a part of.
	@throws InvalidMoveException If jump is not possible.
    */
    protected void jumpOnce(byte dir, Move move)
	throws InvalidMoveException
    {
	// valid?
	if ((FIRST_DIRECTION > dir) || (dir > LAST_DIRECTION))
	    throw new InvalidMoveException("error.move.jump_nodir", move);
	Field actField = ballField.fieldIn(dir);
	if (on(actField) != PIECE)
	    throw new InvalidMoveException("error.move.jump_wrongdir", move);

	// remove pieces
	do
	    {
		set(actField, EMPTY);
		actField.moveIn(dir);
	    }
	while (on(actField) == PIECE);

	// move ball
	setBall(actField);
    }

    /** Returns true if the other object is also a DefaultBoard and represents the same position.
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof DefaultBoard))
            return false;
        DefaultBoard b = (DefaultBoard)o;
        if (b.whoseTurn != whoseTurn)
            return false;
        for (int i=0; i<COL_COUNT; i++)
            if (!Arrays.equals(fields[i], b.fields[i]))
                return false;
        return true;
    }
    
    /** As nearly all information about the position is in the byte arrays,
     * returns the hash code of the two-dimensional field array.
     */
    /* naive implementation. */
    public int hashCode()
    {
        int hash = 0;
        for (int c=0; c<COL_COUNT; c++)
            for (int r=0; r<ROW_COUNT; r++)
                hash += c * r * fields[c][r];
        return whoseTurn ? hash : hash;
    }

    /** Returns a string representation of the board, for debugging.
	Not localized.
    */
    public String toString()
    {
	StringBuffer erg = new StringBuffer("Board=(ball=")
	    .append(getBall().toString())
	    .append(";  pieces=");
	for (int x=0; x<COL_COUNT; x++)
	    for (int y=0; y<ROW_COUNT; y++)
		if (on(x,y) == PIECE)
		    erg.append((new Field(x,y)).toString()).append(' ');
	return erg.append(")").toString();
    }

}
