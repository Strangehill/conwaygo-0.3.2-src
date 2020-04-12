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


/** A data model for a game of ConwayGo played in another Thread in the background.
   <br> $Id: BackgroundGame.java,v 1.1 2004/07/30 21:05:58 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.1 $
*/
public interface BackgroundGame
    extends Game
{

    /** Starts playing the game.
	Game is played in the background, this method returns immediately.
	@throws IllegalStateException If a player is null or the game is already running.
    */
    public void play()
	throws IllegalStateException;

    
    /** Returns whether the game is stopped or running.
     */
    public boolean isAlive();

    /** Aborts the game.
     */
    public void abort();

    /** Aborts the game, kills all running processes, frees all resources.
	This method is a kind of finalizer and is needed to support the
	'destroy' method of applets.
    */
    public void destroy();
}
