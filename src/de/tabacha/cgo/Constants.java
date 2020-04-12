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

/** Defines all the constants needed throughout this package.
   <br> $Id: Constants.java,v 1.11 2004/12/23 21:42:43 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.11 $
*/
public interface Constants
{
    public final static boolean UP = true;
    public final static boolean DOWN = false;
    public final static int ROW_COUNT = 19; // must be odd, for fairness
    public final static int COL_COUNT = 19;
    public final static Field KICK_OFF_FIELD = new Field( (ROW_COUNT-1)/2, (COL_COUNT-1)/2 );
    
    public final static byte EMPTY = 0;
    /** For the outside world, this is named 'player'. To avoid misunderstandings, PIECE is used here for the blue balls. */
    public final static byte PIECE = 1;
    public final static byte BALL = 2;
    
    public final static byte N  = 0;
    public final static byte NE = N +1;
    public final static byte E  = NE+1;
    public final static byte SE = E +1;
    public final static byte S  = SE+1;
    public final static byte SW = S +1;
    public final static byte W  = SW+1;
    public final static byte NW = W +1;
    public final static byte FIRST_DIRECTION = N;
    public final static byte LAST_DIRECTION = NW;
    public final static byte[] UPSIDEDOWN = {S, SE, E, NE, N, NW, W, SW};
    public final static byte[] OPPOSITE = {S, SW, W, NW, N, NE, E, SE};

    public final static byte[] DCOL = { 0, 1, 1, 1, 0,-1,-1,-1};
    public final static byte[] DROW = {-1,-1, 0, 1, 1, 1, 0,-1};
}
