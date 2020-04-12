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

import java.util.EventListener;


/** Classes that implement this listener are notified when
    something in the game has changed.
   <br> $Id: GameListener.java,v 1.3 2004/07/17 22:18:35 mk Exp $
   @see GameEvent
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.3 $
*/
public interface GameListener
    extends EventListener
{

    /** Signals that a game has been started.
     */
    public void gameStarted(GameEvent e);

    /** Signals that the game has been aborted.
     */
    public void gameAborted(GameEvent e);

    /** Signals that the game is over.
	(If it has been aborted, this method will not be called.)
    */
    public void gameEnded(GameEvent e);

    /** Signals that a move has been done.
     */ 
    public void hasMoved(GameEvent e);

    /** Signals that one of the players has changed.
	No guarantee that other classes allow a player to be changed during the game.
    */
    public void playerChanged(GameEvent e);

}
