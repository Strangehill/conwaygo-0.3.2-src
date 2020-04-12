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


/** Does roughly the same as MikeGo, but uses the NoRecursionTemplate class.
   <br />$Id: ModernMikeGo.java,v 1.7 2004/07/30 21:06:47 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.7 $
   @threadsafe false
*/
public class ModernMikeGo
    extends NoRecursionTemplate
{

    // ------------------------ static attributes ----------------------------
    // ------------------------ static methods -------------------------------
    // ------------------------ variables ------------------------------------

    private int minrowfactor;
    private int maxrowfactor;
    private int ballrowfactor;
    private int jumpmalus;

    private Move bestMove;
    private int bestMoveCount;
    private int bestMoveValue;


    // ------------------------ constructor ----------------------------------

    /** Constructor.
     */
    public ModernMikeGo()
    { this(1,2,1,3); }

    public ModernMikeGo(int minrow, int maxrow, int ballrow, int jumpmalus)
    {
	this.minrowfactor = minrow;
	this.maxrowfactor = maxrow;
	this.ballrowfactor = ballrow;
	this.jumpmalus = jumpmalus;
    }


    // ----------------------- getter & setter methods -----------------------

    public String getVersion()
    { return "1 (ModernMikeGo " + minrowfactor + maxrowfactor + ballrowfactor + jumpmalus + ")"; }

    public String getAuthor()
    { return "michael@tabacha.de"; }


    // ----------------------- other methods ---------------------------------

    protected void initNewMoveSearch()
    {
	bestMove = null;
	bestMoveCount = 0;
	bestMoveValue = -100;
    }

    protected boolean jumpFound(Jump j, int min, int max)
    {
	int brow = board().getBall().row();

	int moveValue = min * minrowfactor + max * maxrowfactor + brow * ballrowfactor - jumpmalus;
	if (moveValue > bestMoveValue)
	    {
		bestMove = j;
		bestMoveCount = 1;
		bestMoveValue = moveValue;
	    }
	else if (moveValue == bestMoveValue && (random().nextInt(++bestMoveCount) == 0))
	    bestMove = j;

	return false;
    }

    protected boolean putFound(Put p)
    {
	int brow = board().getBall().row();

	int[] minMax = getMinMaxRow();
	int min = minMax[0];
	int max = minMax[1];

	int moveValue = min * minrowfactor + max * maxrowfactor + brow * ballrowfactor - jumpmalus;
	if (moveValue > bestMoveValue)
	    {
		bestMove = p;
		bestMoveCount = 1;
		bestMoveValue = moveValue;
	    }
	else if (moveValue == bestMoveValue && (random().nextInt(++bestMoveCount) == 0))
	    bestMove = p;

	return false;
    }

    protected Move bestMove()
    {
	return bestMove;
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer(super.toString());
	buf.append(" (bestMove =").append(bestMove)
	    .append(", bestMoveValue =").append(bestMoveValue)
	    .append(")");
	return buf.toString();
    }
}
