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


/** A move where a piece is put on the board.
   <br> $Id: Put.java,v 1.5 2004/07/17 22:18:36 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.5 $
   @threadsafe false Same as Field.
*/
public final class Put
    extends Move
{
    
    private Field where;
    

    /** Constructor.
	@param field Where the figure is put on the field.
    */
    public Put(Field field)
    { where = (Field)field.clone(); }

    /** Constructor.
     */
    public Put(int row, int col)
    { this(new Field(row, col)); }


    public Object clone()
    { return new Put((Field)where.clone()); }

    public boolean equals(Object obj)
    { return ( (obj instanceof Put) && (where.equals(((Put)obj).where)) ); }

    public Move upsideDown()
    { return new Put(where.upsideDown()); }

    /** Returns the field where the figure is set.
     */
    public Field getField()
    { return where; }

    public int hashCode()
    { return where.hashCode(); }

    public String toString()
    { return "Put" + where.toString(); }

}
