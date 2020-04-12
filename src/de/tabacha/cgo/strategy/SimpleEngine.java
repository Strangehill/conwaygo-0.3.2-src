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


/** Sample implementation of NoRecursionTemplate.
   <br />$Id: SimpleEngine.java,v 1.9 2004/07/30 21:06:47 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.9 $
   @threadsafe false
*/
public class SimpleEngine
    extends NoRecursionTemplate
{
    // ------------------------ variables ------------------------------------

    private int minJumpRow;

    private Jump bestOffensiveJump;
    private int bestOffensiveJumpCount;
    private int maxJumpRow;

    private Jump bestDefensiveJump;
    private int bestDefensiveJumpCount;
    private int maxMinJumpRow;

    private Put bestOffensivePut;
    private int bestOffensivePutCount;
    private int maxPutRow;

    private Put bestDefensivePut;
    private int bestDefensivePutCount;
    private int maxMinPutRow;


    // ------------------------ constructor ----------------------------------

    /** Constructor.
     */
    public SimpleEngine()
    {}


    // ----------------------- getter & setter methods -----------------------

    public String getVersion()
    { return "2"; }

    public String getAuthor()
    { return "michael@tabacha.de"; }


    // ----------------------- other methods ---------------------------------

    protected void initNewMoveSearch()
    {
	bestOffensiveJump = bestDefensiveJump = null;
	bestOffensiveJumpCount = bestDefensiveJumpCount = 0;
	bestOffensivePut = bestDefensivePut = null;
	bestOffensivePutCount = bestDefensivePutCount = 0;
	maxJumpRow = maxMinJumpRow = maxPutRow = maxMinPutRow = -50;
	minJumpRow = 50;
    }

    protected boolean jumpFound(Jump j, int min, int max)
    {
	int brow = board().getBall().row();

	if (min < minJumpRow)
	    minJumpRow = min;

	if (brow > maxJumpRow)
	    {
		maxJumpRow = brow;
		bestOffensiveJump = j;
		bestOffensiveJumpCount = 1;
	    }
	else if (brow == maxJumpRow && (random().nextInt(++bestOffensiveJumpCount) == 0))
	    bestOffensiveJump = j;

	if (min > maxMinJumpRow)
	    {
		maxMinJumpRow = brow;
		bestDefensiveJump = j;
		bestDefensiveJumpCount = 1;
	    }
	else if (min == maxMinJumpRow && (random().nextInt(++bestDefensiveJumpCount) == 0))
	    bestDefensiveJump = j;
		
	return false;
    }

    protected boolean putFound(Put p)
    {
	int[] minMax = getMinMaxRow();

	if (minMax[1] > maxPutRow)
	    {
		maxPutRow = minMax[1];
		bestOffensivePut = p;
		bestOffensivePutCount = 1;
	    }
	else if (minMax[1] == maxPutRow && (random().nextInt(++bestOffensiveJumpCount) == 0))
	    bestOffensivePut = p;
	      
	if (minMax[0] > maxMinPutRow)
	    {
		maxMinPutRow = minMax[0];
		bestDefensivePut = p;
	    }
	else if (minMax[0] == maxMinPutRow && (random().nextInt(++bestDefensivePutCount) == 0))
	    bestDefensivePut = p;

	return false;
    }

    protected Move bestMove()
    {
	int brow = board().getBall().row();

	// good jump available?
	if (maxJumpRow >= 18 || maxJumpRow-brow > 4)
	    return bestOffensiveJump;

	// need to defend?
	if (minJumpRow <= 0 || brow-minJumpRow > 4)
	    {
		if (maxMinJumpRow > maxMinPutRow)
		    return bestDefensiveJump;
		else if (maxMinPutRow > minJumpRow)
		    return bestDefensivePut;
		else // can't do anything
		    return bestOffensivePut;
	    }

	// no danger
	return bestOffensivePut;
    }

    public String toString()
    {
	StringBuffer buf = new StringBuffer("Computer (advanced): ");
	buf.append("bestOffensiveJump =").append(bestOffensiveJump)
	    .append(", bestDefensiveJump =").append(bestDefensiveJump)
	    .append(", bestOffensivePut =").append(bestOffensivePut)
	    .append(", bestDefensivePut =").append(bestDefensivePut);
	/*
	  maxJumpRow;
	  maxMinJumpRow;
	  minJumpRow;
	  maxPutRow;
	  maxMinPutRow;
	*/
	return buf.toString();
    }
}
