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


/** Template to simplify the writing of simple engines that only test the first move.
    To use this class, overwrite the methods
    {@link #initNewMoveSearch},
    {@link #jumpFound},
    {@link #minMaxFound},
    {@link #putFound} and
    {@link #bestMove}.
   <br />$Id: NoRecursionTemplate.java,v 1.10 2004/12/23 21:42:44 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.10 $
   @threadsafe false
*/
public abstract class NoRecursionTemplate
    extends AbstractEngine
{

    private static final int MAXSUCHTIEFE = 49;

    private byte[][] board;
    private boolean whoseTurn;

    /** Ist es sinnvoll, auf dieses Feld einen Pöppel zu setzen? */
    private boolean[][] gutzug;
    private int min, max;

    /** Ball coordinates. */
    private int bcol, brow;

    private byte[] suchZug;
    private int suchZugLaenge;

    private int findMin, findMax;
    private boolean moveFound;

    private Board myBoard = new Board()
	{
	    public Object clone()
	    { return new DefaultBoard(toArray(), new Field(bcol, brow), DOWN); }
	    public byte[][] toArray()
	    {
		byte[][] newFields = new byte[ROW_COUNT][COL_COUNT];
		for (int r=0; r < ROW_COUNT; r++)
		    System.arraycopy(board[r], 0, newFields[r], 0, COL_COUNT);
		return newFields;
	    }
	    public Board upsideDown()
	    {
		byte[][] newFields = new byte[ROW_COUNT][COL_COUNT];
		for (int r=0; r < ROW_COUNT; r++)
		    System.arraycopy(board[ROW_COUNT-1-r], 0, newFields[r], 0, COL_COUNT);
		return new DefaultBoard(newFields, new Field(bcol, ROW_COUNT-1-brow), UP);
	    }

	    public boolean whoseTurn()
	    { return DOWN; }
	    public byte on(int col, int row)
	    { return NoRecursionTemplate.this.on(col, row); }
	    public byte on(Field field)
	    { return NoRecursionTemplate.this.on(field.col(), field.row()); }
	    public Field getBall()
	    { return new Field(bcol, brow); }

	    public void reset()
	    { throw new UnsupportedOperationException(); }
	    public void setBall(Field f)
	    { throw new UnsupportedOperationException(); }
	    public void setBall(int col, int row)
	    { throw new UnsupportedOperationException(); }
	    public void addPiece(Field f)
	    { throw new UnsupportedOperationException(); }
	    public void addPiece(int col, int row)
	    { throw new UnsupportedOperationException(); }
	    public void removePiece(Field f)
	    { throw new UnsupportedOperationException(); }
	    public void removePiece(int col, int row)
	    { throw new UnsupportedOperationException(); }
	    public void move(Move m)
	    { throw new UnsupportedOperationException(); }
	};

    /** Constructor.
     */
    protected NoRecursionTemplate()
    {}

    public final boolean canPlayBothSides() { return false; }


    /**
     */
    protected void initNewMoveSearch()
    {}

    /**
       @return true if the move is found
     */
    protected abstract boolean jumpFound(Jump j, int min, int max);

    /**
       @return true if the move is found
     */
    protected boolean minMaxFound(int min, int max)
    { return false; }

    /**
       @return true if the move is found
     */
    protected abstract boolean putFound(Put p);

    /**
     */
    protected abstract Move bestMove();

    /** Returns a fast and small and immutable implementation of the Board interface.
	Methods that would change the board (or would enable a change) throw an
	UnsupportedOperationException.
	To get a changeable board, use the clone() method.
     */
    protected Board board()
    { return myBoard; }



    /** Thinking here.
	@param position The actual position
	@return The move the routine makes
    */
    public final Move think(Board position)
    {
	// copy position
	this.whoseTurn = position.whoseTurn();
	// sorge dafür, daß immer nach unten, nach 18, gezogen wird.
	if (whoseTurn == DOWN)
	    {
		this.board = position.toArray();
		this.brow = position.getBall().row();
	    }
	else
	    {
		this.board = position.upsideDown().toArray();
		this.brow = 18 - position.getBall().row();
	    }
	this.bcol = position.getBall().col();

	moveFound = false;
	suchZug = new byte[MAXSUCHTIEFE];
	suchZugLaenge = 0;
	gutzug = new boolean[23][23];
	int i,j;
	for (i=0; i<23; i++)
	    for (j=0; j<23; j++)
		gutzug[i][j] = false;

	initNewMoveSearch(); 

	int[] minmax = new int[2];
	springe(minmax);
	if (!moveFound)
	    {
		min = minmax[0];
		max = minmax[1];
		moveFound = minMaxFound(min, max);
	    }
	if (!moveFound)
	    probiereBoings();
	Move m = bestMove();
	return (whoseTurn == DOWN) ? m : m.upsideDown();
    }

    // _________________________________________________________________________

    private byte on(int col, int row)
    {
	if (0<=col && col<=18 && 0<=row && row <=18)
	    return board[row][col];
	else
	    return EMPTY;
    }

    private void set(int col, int row, byte type)
    {
	if (0<=col && col<=18 && 0<=row && row <=18)
	    board[row][col] = type;
    }

    // _________________________________________________________________________

    private void springe(int[] this_minmax)
    {
	this_minmax[0] = this_minmax[1] = brow; // minimale und maximale von hier aus erreichbare Zeile
	int[] sub_minmax = new int[2]; // gleiches für weitere Sprünge

	suchZugLaenge++;
	int testcol, testrow;
	int anz = 0;
	int wert;
	for (byte r = 0; r < 8; r++)
	    {
		testcol = bcol + DCOL[r];
		testrow = brow + DROW[r];
		if (on(testcol,testrow) == EMPTY)
		    gutzug[testcol+2][testrow+2] = true;
		else
		    {
			set(bcol, brow, EMPTY);
			anz = 1; bcol += DCOL[r]; brow += DROW[r];
			do {
			    set(bcol, brow, EMPTY);
			    anz++; bcol += DCOL[r]; brow += DROW[r];
			} while (on(bcol, brow) != EMPTY);
			gutzug[bcol+2][brow+2] = true;
			
			if ((0 <= bcol) && (bcol <= 18) || (brow < 0) || (brow > 18)) // inside or in goal
			    {
				set(bcol, brow, BALL);
				suchZug[suchZugLaenge-1] = r;

				// weiterspringen
				if ((suchZugLaenge < MAXSUCHTIEFE) && (0 < brow) && (brow < 18))
				    springe(sub_minmax);
				else
				    sub_minmax[0] = sub_minmax[1] = brow;
				if (sub_minmax[0] < this_minmax[0]) this_minmax[0] = sub_minmax[0];
				if (sub_minmax[1] > this_minmax[1]) this_minmax[1] = sub_minmax[1];
				
				Jump ju = new Jump();
				for (int i=0; i<suchZugLaenge; i++)
				    ju.push(suchZug[i]);
				moveFound = jumpFound(ju, sub_minmax[0], sub_minmax[1]);
			    }

			// setze Zug zurück
			set(bcol, brow, EMPTY);
			anz--; bcol -= DCOL[r]; brow -= DROW[r];
			while (anz > 0)
			    {
				set(bcol, brow, PIECE);
				anz--; bcol -= DCOL[r]; brow -= DROW[r];
			    }
			set(bcol, brow, BALL);
		    }
	    }
	suchZugLaenge--;
    }


    // _________________________________________________________________________ 

    private void probiereBoings()
    {	    
	int wert;
	int x, y;
	for (x=0; x<=18; x++)
	    for (y=0; y<=18; y++)
		if (gutzug[x+2][y+2] && on(x, y) == EMPTY)
		    {
			set(x, y, PIECE);
			moveFound = putFound(new Put(x, y));
			set(x, y, EMPTY);
		    }
    }


    // _________________________________________________________________________


    /**
     */
    protected int[] getMinMaxRow()
    {
	findMin = brow; findMax = brow;
	getMinMaxRow_springe();
	return new int[]{findMin, findMax};
    }

    private void getMinMaxRow_springe()
    {
	int anz;
	for (int r=0; r < 8; r++)
	    {
		if (on(bcol+DCOL[r], brow+DROW[r]) == PIECE)
		    {
			anz = 1; bcol += DCOL[r]; brow += DROW[r];
			do {
			    set(bcol, brow, EMPTY);
			    anz++; bcol += DCOL[r]; brow += DROW[r];
			} while (on(bcol, brow) == PIECE);

			if  (brow <= 0)
			    findMin = 0;
			else if (brow >= 18)
			    findMax = 18;
			else if ((0 <= bcol) && (bcol <= 18))
			    {
				getMinMaxRow_springe();
				if (brow > findMax)
				    findMax = brow;
				if (brow < findMin)
				    findMin = brow;
			    }

			bcol -= DCOL[r]; brow -= DROW[r];
			for (int d=0; d<anz-1; d++)
			    {
				set(bcol, brow, PIECE);
				bcol -= DCOL[r]; brow -= DROW[r];
			    }
		    }
	    }
    }

}
