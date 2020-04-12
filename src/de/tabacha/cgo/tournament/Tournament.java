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
package de.tabacha.cgo.tournament;

import java.util.*;

import javax.swing.*;

import de.tabacha.cgo.*;
import de.tabacha.cgo.strategy.*;


/** For playing tournaments between different engines.
   <br />$Id: Tournament.java,v 1.7 2004/12/23 21:42:44 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.7 $
   @threadsafe false
*/
public class Tournament
{

    // ------------------------ static attributes ----------------------------
    // ------------------------ static methods -------------------------------

    /** Main method.
     */
    public static void main(String[] args)
    {
	Tournament t = new Tournament();
	t.frame.pack();
	t.frame.show();
	t.start();
    }


    // ------------------------ variables ------------------------------------

    private int engineCount;
    private List engines;
    private Vector engineNames;

    private int[][] result;
    private int[][] gameCount;
    private int[][] moves;
    private long[][] time;

    private JTable resultTable;
    private JFrame frame;


    // ------------------------ constructor and initialization methods -------

    /** Constructor.
     */
    public Tournament()
    {
	engineCount = 6;
	engines = new ArrayList(engineCount);
	engineNames = new Vector(engineCount);

	result = new int[engineCount][engineCount];
	gameCount = new int[engineCount][engineCount];
	moves = new int[engineCount][engineCount];
	time = new long[engineCount][engineCount];

	resultTable = new JTable(engineCount, engineCount);

	addEngine(0, new Dude());
	addEngine(1, new ModernMikeGo());
	addEngine(2, new SimpleEngine());
	addEngine(3, new MikeGo());
	addEngine(4, new FiveTypes());
	addEngine(5, new ModernMikeGo(1,1,0,5));

	frame = new JFrame();
	frame.getContentPane().add(resultTable);
    }


    // ----------------------- listener methods ------------------------------
    // ----------------------- getter & setter methods -----------------------
    // ----------------------- other methods ---------------------------------

    private void addEngine(int i, Engine e)
    {
	    engines.add(e);
	    engineNames.add(e.getName());
	    
	    Arrays.fill(result[i],0);
	    Arrays.fill(gameCount[i],0);
	    
	    resultTable.setValueAt("X", i, i);
    }

    
    private void start()
    {
	int rounds = 100;
	TournamentGame g;

	for (int round=0; round<rounds; round++)
	for (int i=0; i<engineCount; i++)
	    for (int j=0; j<engineCount; j++)
		    if (i != j)
			{
			    g = new TournamentGame((Engine)engines.get(i), (Engine)engines.get(j));
			    g.play();
			    /*
			    gameCount[i][j]++;
			    gameCount[j][i]++;
			    time[i][j] += g.averageTime(g.UP);
			    time[j][i] += g.averageTime(g.DOWN);
			    resultTable.setValueAt("" + time[i][j]/gameCount[i][j], i, j);
			    resultTable.setValueAt("" + time[j][i]/gameCount[j][i], j, i);
			    */
			    gameCount[i][j]++;
			    // gameCount[j][i]++;
			    if (g.getWinner() == Constants.UP)
				result[i][j]++;
			    // else
			    //   result[j][i]++;
			    moves[i][j] += g.getMoveCount();
			    resultTable.setValueAt(""+ result[i][j] + "/" + gameCount[i][j] + "(" + moves[i][j]/gameCount[i][j] + ")",
						   i, j);
			}
    }


    // ----------------------- inner classes ---------------------------------
}
