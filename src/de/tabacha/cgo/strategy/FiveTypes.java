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

import java.util.Random;

import de.tabacha.cgo.*;


/** Strategy for ConwayGo.
    Slow, but good for developing new strategies.
    Distinguishes between five types of moves:
    <ul>
    <li>offensive put: put a piece to have a wider jump</li>
    <li>offensive jump: jump as far as you can</li>
    <li>defensive jump: jump to prevent a good jump of the opponent</li>
    <li>passive put: put a piece to have a defensive jump next turn</li>
    <li>block: put a piece to shorten the opponent's jump</li>
    </ul>
   <br />$Id: FiveTypes.java,v 1.16 2004/07/30 21:06:47 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.16 $
   @threadsafe false
*/
public class FiveTypes
    extends NoRecursionTemplate
{

    // ------------------------ static attributes ----------------------------
    // ------------------------ static methods -------------------------------
    // ------------------------ variables ------------------------------------

    private boolean debug;
    private Evaluator offensiveJump, defensiveJump, offensivePut, passivePut, block;

    private int minJumpRow;
    private int maxJumpRow;


    // ------------------------ constructor ----------------------------------

    /** Constructor.
     */
    public FiveTypes()
    { this(false); }

    /** Constructor.
	@param debug true for output debugging info
     */
    public FiveTypes(boolean debug)
    {
	this.debug = debug;

	ValueCalculator ballRowAfterMoveValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return b.getBall().row(); }
	    };
	ValueCalculator minJumpRowAfterMoveValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return minRow; }
	    };
	ValueCalculator maxJumpRowAfterMoveValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return maxRow; }
	    };
	ValueCalculator leapCountValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return ((Jump)m).size(); }
	    };
	ValueCalculator rowsForOpponentValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return minJumpRow>minRow ? minRow-minJumpRow : 0; } // minimal reachable line after the put - minimal reachable line before it
	    };
	ValueCalculator putRowValue = new ValueCalculator()
	    {
		public int getValue(Board b, Move m, int minRow, int maxRow)
		{ return ((Put)m).getField().row(); }
	    };
	ValueCalculator curvedPut = new ValueCalculator()
	    {
		/** Returns 1 for a put adjacent to the ball field if the field in the opposite direction of the first leap is empty.
		    Returns -1 for a put adjacent to the ball field if the field in the opposite direction of the first leap contains a piece.
		    Returns 0 otherwise.
		 */
		public int getValue(final Board b, final Move m, final int minRow, final int maxRow)
		{
		    Field f = ((Put)m).getField();
		    Field ball = b.getBall();
		    int dcol = f.col() - ball.col();
		    int drow = f.row() - ball.row();
		    if (Math.abs(dcol) > 1 || Math.abs(drow) > 1)
			return 0;
		    return b.on(ball.col() - dcol, ball.row() - drow) == EMPTY ? 1 : -1;
		}
	    };
	ValueCalculator straightPut = new ValueCalculator()
	    {
		/** The opposite of curvedPut.
		    Returns -1 for a put adjacent to the ball field if the field in the opposite direction of the first leap is empty.
		    Returns 1 for a put adjacent to the ball field if the field in the opposite direction of the first leap contains a piece.
		    Returns 0 otherwise.
		 */
		public int getValue(final Board b, final Move m, final int minRow, final int maxRow)
		{
		    Field f = ((Put)m).getField();
		    Field ball = b.getBall();
		    int dcol = f.col() - ball.col();
		    int drow = f.row() - ball.row();
		    if (Math.abs(dcol) > 1 || Math.abs(drow) > 1)
			return 0;
		    return b.on(ball.col() - dcol, ball.row() - drow) == EMPTY ? -1 : 1;
		}
	    };
	
	offensiveJump = new Evaluator(random(), new ValueCalculator[]
	    { ballRowAfterMoveValue, leapCountValue });
	defensiveJump = new Evaluator(random(), new ValueCalculator[]
	    { minJumpRowAfterMoveValue, leapCountValue });
	offensivePut = new Evaluator(random(), new ValueCalculator[]
	    { rowsForOpponentValue, maxJumpRowAfterMoveValue, straightPut, putRowValue }); // += backwardPutRows
	passivePut = new Evaluator(random(), new ValueCalculator[]
	    { rowsForOpponentValue, maxJumpRowAfterMoveValue, curvedPut, putRowValue }); // += backwardPutRows
        block = new Evaluator(random(), new ValueCalculator[]
	    { minJumpRowAfterMoveValue, maxJumpRowAfterMoveValue });
    }


    // ----------------------- initialization methods ---------------------------------

    protected void initNewMoveSearch()
    {
	minJumpRow = maxJumpRow = board().getBall().row();

	offensiveJump.initNewMoveSearch();
	defensiveJump.initNewMoveSearch();
	offensivePut.initNewMoveSearch();
	passivePut.initNewMoveSearch();
	block.initNewMoveSearch();
    }


    // ----------------------- getter & setter methods -----------------------

    public String getVersion()
    { return "1"; }

    public String getAuthor()
    { return "michael@tabacha.de"; }


    public void setDebug(boolean debug)
    { this.debug = debug; }


    // ----------------------- other methods ---------------------------------

    protected boolean jumpFound(Jump j, int min, int max)
    {
	if (min < minJumpRow) minJumpRow = min;
	if (max > maxJumpRow) maxJumpRow = max;

	offensiveJump.evaluate(board(), j, min, max);
	defensiveJump.evaluate(board(), j, min, max);

	return false;
    }

    protected boolean putFound(Put p)
    {
	int[] minMax = getMinMaxRow();

	offensivePut.evaluate(board(), p, minMax[0], minMax[1]);
	passivePut.evaluate(board(), p, minMax[0], minMax[1]);
	block.evaluate(board(), p, minMax[0], minMax[1]);
	
	return false;
    }

    protected Move bestMove()
    {
	int brow = board().getBall().row();
	int maxMinJumpRow = defensiveJump.getBestMoveValue(0);
	if (maxMinJumpRow == Integer.MIN_VALUE) maxMinJumpRow = brow;
	// int maxPutRow = offensivePut.getBestMoveValue(0);
	int maxMinPutRow = block.getBestMoveValue(0);

	if (debug)
	    {
		System.err.println(toString());
		System.err.println("brow="+brow+",minJumpRow="+minJumpRow+",maxJumpRow="+maxJumpRow+",maxMinJumpRow="+maxMinJumpRow+",maxMinPutRow="+maxMinPutRow);
	    }

	// good jump available?
	if (maxJumpRow >= 18 || maxJumpRow-brow >= 4)
	    return offensiveJump.getBestMove();

	// need to defend?
	if (minJumpRow <= 0 || brow-minJumpRow > 4)
	    {
		// good jump away?
		if (maxMinJumpRow >= brow)
		    return defensiveJump.getBestMove();
		// good block?
		else if (maxMinPutRow > minJumpRow + 2 || minJumpRow == 0)
		    return block.getBestMove();
		// mediocre jump away?
		else if (maxMinJumpRow > minJumpRow + 2)
		    return defensiveJump.getBestMove();
		// can't help it: put to jump in next move
		else
		    return passivePut.getBestMove();
	    }

	// no danger; who has initiative?
	if (brow - minJumpRow > maxJumpRow - brow)
	    return passivePut.getBestMove();
	else
	    return offensivePut.getBestMove();
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer("FiveTypes: ");
	buf.append("offensiveJump=").append(offensiveJump)
	    .append(", defensiveJump=").append(defensiveJump)
	    .append(", offensivePut=").append(offensivePut)
	    .append(", block=").append(block)
	    .append(", passivePut=").append(passivePut);
	return buf.toString();
    }
}




// ===================================================================================
/*
vorher
brow, bcol, min, max

Jump:
nachher
brow, bcol, min, max, nrofjumps

OffensiveJump
prioritäten (alle n):
brow, max, min, (nrofjumps, bcol)

DefensiveJump
prioritäten (alle n):
min, max, brow, (nrofjumps, bcol)


Put
nachher
min, max, prow, pcol, schiefer, ags, trapez

OffensivePut
prioritäten (alle n)
max, min, trapez, ags, (pcol, -schiefer, prow)

PassivePut
prioritäten (alle n)
min, max, schiefer, trapez, (ags, pcol, prow)

Block
prioritäten (alle n)
min, (...)



min brow max typ
9 9 9 op
7 9 11 op
5 9 13 oj/op
11 13 13 op
5 13 15 dj
5 15 15 dp(12)
*/


class Evaluator
{
    private Random random;
    private ValueCalculator[] valueCalculators;

    private int[] value;
    private Move bestMove;
    private int bestMoveCount;


    public Evaluator(Random r, ValueCalculator[] valueCalculators)
    {
	this.random = r;
	this.valueCalculators = valueCalculators;
	this.value = new int[valueCalculators.length];
    }

    public void initNewMoveSearch()
    {
	value[0] = Integer.MIN_VALUE;
	bestMove = null;
	bestMoveCount = 0;
    }
    
    public void evaluate(Board b, Move m, int minRow, int maxRow)
    { evaluateAtLevel(0, b, m, minRow, maxRow); }

    protected void evaluateAtLevel(int i, Board b, Move m, int minRow, int maxRow)
    {
	int mValue = valueCalculators[i].getValue(b, m, minRow, maxRow);
	if (mValue > value[i])
	    {
		bestMove = m;
		bestMoveCount = 1;
		value[i] = mValue;
		for (int j=i+1; j<value.length; j++)
		    value[j] = valueCalculators[j].getValue(b, m, minRow, maxRow);
		// Hier müssen alle weiteren Werte berechnet werden, auch wenn
		// man sie später nicht mit anderen vergleichen muß.
		// Eine andere Möglichkeit wäre, das Brett abzuspeichern.
		// (Beim von NoRecursionTemplate übergebenen Board-Objekt ist clone() inzwischen implementiert.)
	    }
	else if (mValue == value[i])
	    {
		if (i < valueCalculators.length-1)
		    evaluateAtLevel(i+1, b, m, minRow, maxRow);
		else if (random.nextInt(++bestMoveCount) == 0)
		    bestMove = m;
	    }
    }
    
    public Move getBestMove()
    { return bestMove; }

    public int getBestMoveValue(int i)
    { return value[i]; }

    public String toString()
    { return "Evaluator:"
	  + "bestMove=" + (bestMove==null ? "none" : bestMove.toString());
    }
}

interface ValueCalculator
{
    public abstract int getValue(Board b, Move m, int minRow, int maxRow);
}
