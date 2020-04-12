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


/** A data model for a game of ConwayGo.
   <br> $Id: Game.java,v 1.7 2004/07/30 21:05:58 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.7 $
*/
public interface Game
{

    /** Adds a GameListener that will be notified of game events.
     */
    public void addGameListener(GameListener listener);

    /** Removes a GameListener.
	Nothing will happen if the listener has not been registered.
     */
    public void removeGameListener(GameListener listener);


    /** Returns the routine that makes the moves in the specified direction.
     */
    public Engine getPlayer(boolean direction);

    /** Returns the player that has to move now.
     */
    public Engine getPlayerToMove();

    /** Sets the routine that makes the moves in the specified direction.
     */
    public void setPlayer(boolean direction, Engine player);

    /** Returns the board with the game position.
     */
    public Board getBoard();


    /** Starts playing the game.
	Listeners are informed of game events, so no return values are necessary.
	@throws IllegalStateException If a player is null or the game is already running.
    */
    public void play()
	throws IllegalStateException;
}
