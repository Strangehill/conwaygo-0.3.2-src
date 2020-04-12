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


/** A move where the ball jumps.
   <br> $Id: Jump.java,v 1.7 2004/07/27 20:50:20 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.7 $
   @threadsafe false
*/
public final class Jump
    extends Move
{
    private static final int INITIAL_LENGTH = 5;
    private static final int LENGTH_INCREMENT = 10;

    private byte[] leaps;
    private int size;
    private transient int index;


    /** Empty Constructor.
     */
    public Jump()
    {
	leaps = new byte[INITIAL_LENGTH];
	size = 0;
    }

    /** Constructor.
	@param directions The list of the directions of the subsequent jumps.
    */
    public Jump( byte [] directions )
    {
	leaps = (byte[])directions.clone(); 
	size = leaps.length;
    }
    
    public Object clone()
    {
	Jump temp = new Jump(leaps);
	temp.size = size;
	return temp;
    }
    
    /** Returns true if this move contains zero jumps.
     */
    public boolean isEmpty()
    { return size == 0; }

    /** Returns the number of leaps of this jump.
     */
    public int size()
    { return size; }

    /** Minimize memory usage.
     */
    public void trimToSize()
    {
	byte[] temp = new byte[size];
	System.arraycopy(leaps, 0, temp, 0, size);
	leaps = temp;
    }

    public Move upsideDown()
    {
	Jump temp = new Jump();
	temp.leaps = new byte[size];
	for (int i=0; i<size; i++)
	    temp.leaps[i] = UPSIDEDOWN[leaps[i]];
	temp.size = size;
	return temp;
    }

    public void startIteration()
    { index = 0; }

    public boolean hasNextLeap()
    { return index < size; }

    public byte nextLeap()
    { return leaps[index++]; }


    /** Removes and returns the last jump from this move.
	@return -1 if stack is empty.
     */
    public byte pop()
    { return (size==0) ? -1 : leaps[--size]; }

    /** Adds a jump to the end of this move.
	@param direction Where to jump in the end
    */
    public void push(byte direction)
    {
	if (size == leaps.length)
	    {
		byte[] temp = new byte[size + LENGTH_INCREMENT];
		System.arraycopy(leaps, 0, temp, 0, size);
		leaps = temp;
	    }
	leaps[size] = direction;
	size++;
    }
    
    public String toString()
    {
	if (size == 0)
	    return "Zero Jumps";
	StringBuffer a = new StringBuffer("Jumps:");
	for (int i=0; i<size; i++)
	    a.append(" ").append(new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"}[leaps[i]]);
	return a.toString();
    }
    
}
