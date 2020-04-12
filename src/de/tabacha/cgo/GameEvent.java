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

import java.util.EventObject;


/** Holds information about a change in the game.
    Possible changes are:
    <ul>
      <li>A move has been performed.</li>
      <li>The game has been aborted.</li>
      <li>The game has ended in a normal way.</li>
    </ul>
    <br> $Id: GameEvent.java,v 1.7 2004/12/23 21:42:43 mk Exp $
    @see GameListener
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.7 $
    @threadsafe true Immutable
*/
public class GameEvent
    extends EventObject
{
    private boolean direction;
    private String message;
    private Move move;


    /** Constructor.
	@param direction Meaning depends on the type of event.
	@see #getDirection
	@see GameListener
     */
    public GameEvent(Game game, boolean direction)
    {
	super(game);
	this.direction = direction;
    }

    /** Called when a move has been performed.
	@param playerDirection Direction of the player that made the move
	@param move The move that has been performed.
     */
    public GameEvent(Game game, boolean playerDirection, Move move)
    {
	super(game);
	this.direction = playerDirection;
	this.move = move;
    }

    /** Constructor.
	@param direction Meaning depends on the type of event.
	@see #getDirection
	@see GameListener
	@param move The last move. May be null if information is not useful.
	@param message A short text with more information.
    */
    public GameEvent(Game game, boolean direction, Move move, String message)
    {
        super(game);
        this.direction = direction;
        this.message = message;
        this.move = move;
    }


    /** Returns the game where this event evolved.
     */
    public Game getGame()
    { return (Game)getSource(); }    

    /** Returns the key for the locale resource bundle for textual information about the event.
     */
    public String getMessage()
    { return message; }

    /** Meaning of return value depends.
	<ul>
	<li>gameStarted: Returns the playing direction of the first player.</li>
	<li>gameAborted: Returns the direction of the player that should have moved.</li>
	<li>gameEnded: Returns the direction of the winning player.</li>
	<li>hasMoved: Returns the direction of the player that has moved.</li>
	<li>playerChanged: Returns the direction of the new player.</li>
	</ul>
	@return {@link Constants#UP Constants.UP} or {@link Constants#DOWN Constants.DOWN}
    */
    public boolean getDirection()
    { return direction; }

    /** For hasMoved : Returns the move that has just been performed.
     * For gameAborted: Returns the erronous move.
     */
    public Move getMove()
    { return move; }

}
