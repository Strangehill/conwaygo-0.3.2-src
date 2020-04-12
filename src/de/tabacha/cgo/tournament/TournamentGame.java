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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.tabacha.cgo.*;


/** Represents one game of conwaygo: two players and a board.
    <br />$Id: TournamentGame.java,v 1.9 2004/12/23 21:42:44 mk Exp $
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.9 $
    @threadsafe false
*/
public class TournamentGame
    implements Constants, Game
{
    private transient Collection gameListeners;

    private DefaultBoard board;
    private Engine playerUp, playerDown;

    private boolean winner;
    private int moveCount;


    /** Constructor.
     */
    public TournamentGame(Engine playerUp, Engine playerDown)
    {
	board = new DefaultBoard();
	gameListeners = new ArrayList();
	this.playerUp = playerUp;
	this.playerDown = playerDown;
    }


    public Engine getPlayer(boolean direction)
    { return (direction == UP) ? playerUp : playerDown; }

    /** Returns the player who has to move now.
     */
    public Engine getPlayerToMove()
    { return getPlayer(getBoard().whoseTurn()); }

    /** Sets the routine that makes the moves in the specified direction.
	Change will not take place for the current move.
     */
    public void setPlayer(boolean direction, Engine player)
    {
	if (direction == UP)
	    playerUp = player;
	else
	    playerDown = player;
	firePlayerChanged(direction);
    }   

    /** Returns the board with the game position.
	Never returns zero.
     */
    public Board getBoard()
    { return board; }

    /** Returns the playing direction of the winner of the game.
	No warning if the game is still played!.
    */
    public boolean getWinner()
    { return winner; }
    
    /** Returns the number of moves played in the game.
     */
    public int getMoveCount()
    { return moveCount; }

    public void addGameListener(GameListener listener)
    { gameListeners.add(listener); }

    public void removeGameListener(GameListener listener)
    { gameListeners.remove(listener); }


    /** Method to support listener events.
     */
    protected void fireGameStarted()
    {
	GameEvent event = new GameEvent(this, getBoard().whoseTurn());
	Iterator it = gameListeners.iterator();
	while (it.hasNext())
	    ((GameListener)it.next()).gameStarted(event);
    }

    /** Method to support listener events.
     */
    protected void fireGameAborted(boolean direction, Move move, String message)
    {
	GameEvent event = new GameEvent(this, direction, move, message);
	Iterator it = gameListeners.iterator();
	while (it.hasNext())
	    ((GameListener)it.next()).gameAborted(event);
    }

    /** Method to support listener events.
     */
    protected void fireGameEnded(boolean winnerDirection)
    {
	GameEvent event = new GameEvent(this, winnerDirection);
	Iterator it = gameListeners.iterator();
	while (it.hasNext())
	    ((GameListener)it.next()).gameEnded(event);
    }

    /** Method to support listener events.
     */
    protected void fireHasMoved(Move move, boolean direction)
    {
	GameEvent event = new GameEvent(this, direction, move);
	Iterator it = gameListeners.iterator();
	while (it.hasNext())
	    ((GameListener)it.next()).hasMoved(event);
    }

    /** Method to support listener events.
     */
    protected void firePlayerChanged(boolean newPlayerDirection)
    {
	GameEvent event = new GameEvent(this, newPlayerDirection);
	Iterator it = gameListeners.iterator();
	while (it.hasNext())
	    ((GameListener)it.next()).playerChanged(event);
    }


    // ------------- playing the game ----------------------

    public void play()
	throws IllegalStateException
    {
	if (getPlayer(UP) == null || getPlayer(DOWN) == null)
	    throw new IllegalStateException();

	long randSeed = System.currentTimeMillis();

	Move m;
	boolean direction;
	int moveNumber = 0;
	long time;

	board.reset();
	fireGameStarted();

	getPlayer(UP).initGame(randSeed);
	getPlayer(DOWN).initGame(randSeed+2758); // to prevent the same seed for both
	
	while (!board.getBall().isInGoal())
	    {
		direction = board.whoseTurn();
		Engine player = getPlayerToMove();

		time = System.currentTimeMillis();
		if (direction == UP || player.canPlayBothSides())
		    m = player.think((Board)board.clone());
		else
		    m = player.think(board.upsideDown()).upsideDown();
		/*
		for (int blub = 1; blub <100; blub++)
		    {
			if (direction == UP || player.canPlayBothSides())
			    m = player.think((Board)board.clone());
			else
			    m = player.think(board.upsideDown()).upsideDown();
		    }
		*/
		pushTime(System.currentTimeMillis() - time);

		try
		    {
			board.move(m);
			moveNumber++;
		    }
		catch (InvalidMoveException exc)
		    {
			fireGameAborted(direction, exc.getMove(), exc.getMessage());
			return; // stops the game
		    }
		fireHasMoved(m, direction);
	    }
	moveCount = moveNumber;
	if (board.getBall().isInGoalOf(UP))
	    {
		winner = UP;
		fireGameEnded(UP);
	    }
	else if (board.getBall().isInGoalOf(DOWN))
	    {
		winner = DOWN;
		fireGameEnded(DOWN);
	    }
	else
	    fireGameAborted(board.whoseTurn(), null, "Game aborted");
    }


    private long[] timeSum = new long[]{0L, 0L};
    private int[] moveNr = new int[]{0, 0};
    
    private void pushTime(long dist)
    {
	int nr = getBoard().whoseTurn() ? 0 : 1;
	moveNr[nr]++;
	timeSum[nr] +=dist;
    }

    public int averageTime(boolean direction)
    {
	int nr = direction ? 0 : 1;
	return (int)(timeSum[nr]/moveNr[nr]);
    }

    public long usedTime(boolean direction)
    { return timeSum[direction ? 0 : 1]; }

}
