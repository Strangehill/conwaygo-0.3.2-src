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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/** Represents one game of conwaygo: two players and a board.
    <br />$Id: DefaultGame.java,v 1.16 2004/12/23 21:42:43 mk Exp $
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.16 $
    @threadsafe false
*/
public class DefaultGame
    implements Constants, BackgroundGame
{

    private DefaultBoard board;
    private Engine playerUp, playerDown;

    private transient Collection gameListeners;

    private GameRunner gameRunner;


    /** Constructor.
     */
    public DefaultGame(Engine playerUp, Engine playerDown)
    {
	board = new DefaultBoard();
	gameListeners = new ArrayList();

	gameRunner = new GameRunner();
	gameRunner.start();

	this.playerUp = playerUp;
	this.playerDown = playerDown;
    }


    /** Returns the engine that plays in the specified direction.
     */
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
	    {
		if (player != playerUp)
		    {
			playerUp = player;
			long randSeed = System.currentTimeMillis();
			playerUp.initGame(randSeed);
			firePlayerChanged(UP);
		    }
	    }
	else // direction == DOWN
	    if (player != playerDown)
		{
		    playerDown = player;
		    long randSeed = System.currentTimeMillis();
		    playerDown.initGame(randSeed + 2758);
		    firePlayerChanged(DOWN);
		}
    }   

    /** Returns the board with the game position.
	Never returns zero.
     */
    public Board getBoard()
    { return board; }


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
    protected void fireHasMoved(boolean direction, Move move)
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

    /** Starts the game.
     */
    public void play()
	throws IllegalStateException
    {
	if (getPlayer(UP) == null || getPlayer(DOWN) == null || isAlive())
	    throw new IllegalStateException();

	gameRunner.playing = true;
	gameRunner.interrupt();
    }

    /** Returns true if a game is running.
     */
    public boolean isAlive()
    { return gameRunner.playing; }

    /** Interrupts the game.
     */
    public void abort()
    {
	gameRunner.playing = false;
	gameRunner.interrupt();
    }

    /** Kills the threads.
	<p>
	In a normal application, the threads will run forever - as long as the VM.
	This is intended to save the overhead of initializing and starting new threads for each new game.
	</p><p>
	But when the applet is destroyed, it has to free system resources even if the VM works on.
	</p>
    */
    public void destroy()
    {
	gameRunner.destroy = true;
	gameRunner.interrupt();
    }


    // ----------------------- inner classes ---------------------------------

    /** .
	<br />$Id: DefaultGame.java,v 1.16 2004/12/23 21:42:43 mk Exp $
	@author michael@tabacha.de
	@author $Author: mk $
	@version $Revision: 1.16 $
	@threadsafe false
    */
    private class GameRunner
	extends Thread
    {

	public volatile boolean playing;
	public volatile boolean destroy;

	private ThinkRunner thinkRunner;

	/** Constructor.
	 */
	public GameRunner()
	{
	    playing = false;
	    destroy = false;
	    setDaemon(true);

	    thinkRunner = new ThinkRunner(this);
	    thinkRunner.setDaemon(true);
	}

	
	/** 
	    (Notification means: The thinkRunner has a move.
	     Interruption means: Start or abortion of game. Check variable 'playing'.)
	 */
	public synchronized void run()
	{
	    Engine player;
	    Move m;
	    boolean direction;
	    boolean turnBoard;
	    long randSeed;

	    thinkRunner.start();

	    while (true)
		{
		    while (!playing)
			{
			    try {
				wait();
			    } catch (InterruptedException exc) {
				if (destroy)
				    {
					thinkRunner.destroy = true;
					thinkRunner.interrupt();
					return;
				    }
			    }
			}

		    board.reset();

		    randSeed = System.currentTimeMillis();
		    getPlayer(UP).initGame(randSeed);
		    getPlayer(DOWN).initGame(randSeed+2758); // never the same seed for both

		    fireGameStarted();


		    // We need this for the "catch InvalidMoveException" statement to initialize values.
		    direction = UP;
		    player = getPlayer(UP);

		    try {
			while (!board.getBall().isInGoal())
			    {
				direction = board.whoseTurn();
				player = getPlayerToMove();
				
				turnBoard = (direction == DOWN && !player.canPlayBothSides());
				thinkRunner.setBoard(turnBoard ? board.upsideDown() : (Board)board.clone());
				thinkRunner.setEngine(player);

				synchronized (thinkRunner) {
				    thinkRunner.notify();
				}
				wait();

				m = turnBoard ? thinkRunner.getMove().upsideDown() : thinkRunner.getMove();
				
				board.move(m);
				fireHasMoved(direction, m);
			    }
			playing = false;
			if (board.getBall().isInGoalOf(UP))
			    fireGameEnded(UP);
			else // if (board.getBall().isInGoalOf(DOWN))
			    fireGameEnded(DOWN);
		    } catch (InvalidMoveException exc) {
			playing = false;
			fireGameAborted(direction, exc.getMove(), exc.getMessage());
		    } catch (InterruptedException exc) {
			playing = false;
			if (destroy)
			    {
				thinkRunner.destroy = true;
				thinkRunner.interrupt();
				return;
			    }
			thinkRunner.interrupt();
			fireGameAborted(board.whoseTurn(), null, "msg.game_aborted");
		    }
		}
	}
    }


    /** .
	<br />$Id: DefaultGame.java,v 1.16 2004/12/23 21:42:43 mk Exp $
	@author michael@tabacha.de
	@author $Author: mk $
	@version $Revision: 1.16 $
	@threadsafe false
    */
    private class ThinkRunner
	extends Thread
    {
	public volatile boolean destroy = false;

	private Thread gameRunner;
	
	private Board board;
	private Engine engine;
	private Move move;

	/** Constructor.
	 */
	public ThinkRunner(Thread gameRunner)
	{ this.gameRunner = gameRunner; }

	public void setBoard(Board b)
	{ board = b; }

	public void setEngine(Engine e)
	{ engine = e; }

	public Move getMove()
	{ return move; }

	/** 
	    (Notification means: Has to think about the next move.
	     Interruption means: the game is aborted or has ended.)
	 */
	public synchronized void run()
	{
		while (true)
		    {
			try {
			    wait();

			    move = engine.think(board);

			    synchronized (gameRunner) {
				if (!isInterrupted())
				    gameRunner.notify();
			    }
			} catch (InterruptedException exc) {
			    if (destroy)
				return;
			    // otherwise stay in loop and wait for the next notification
			}
		    }
	}

    }
}
