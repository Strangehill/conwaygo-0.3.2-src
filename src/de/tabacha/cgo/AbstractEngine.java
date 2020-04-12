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

import java.util.Random;
import java.util.ResourceBundle;


/** Basic implementation of Engine supports initialization and internationalization.
   <br> $Id: AbstractEngine.java,v 1.2 2004/07/30 21:43:34 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.2 $
*/
public abstract class AbstractEngine
    implements Engine, Constants
{
    /** The one and only random number generator of this class. */
    private Random random = new Random();

    /** Constructor.
     */
    protected AbstractEngine()
    {}

    /** Returns a localized version of the requested property.
	@throws java.util.MissingResourceException if the property cannot be found.
	@see java.util.ResourceBundle
     */
    protected String getI18nProperty(String name)
    { return ResourceBundle.getBundle(this.getClass().getName()).getString(name); }


    /** Returns the localized version of the name property.
	If this property is the same for all languages or needs special treatment, overwrite this method.
	@throws java.util.MissingResourceException if the property cannot be found.
	@see Engine#getName
     */
    public String getName()
    { return getI18nProperty("name"); }

    /** Returns the localized version of the version property.
	If this property is the same for all languages or needs special treatment, overwrite this method.
	@throws java.util.MissingResourceException if the property cannot be found.
	@see Engine#getVersion
     */
    public String getVersion()
    { return getI18nProperty("version"); }

    /** Returns the localized version of the description property.
	If this property is the same for all languages or needs special treatment, overwrite this method.
	@throws java.util.MissingResourceException if the property cannot be found.
	@see Engine#getDescription
     */
    public String getDescription()
    { return getI18nProperty("description"); }

    /** Returns the localized version of the author property.
	If this property is the same for all languages or needs special treatment, overwrite this method.
	@throws java.util.MissingResourceException if the property cannot be found.
	@see Engine#getAuthor
     */
    public String getAuthor()
    { return getI18nProperty("author"); }


    /** This default implementation always returns false.
	@see Engine#canPlayBothSides
     */
    public boolean canPlayBothSides()
    { return false; }

    /** Initializes the random number generator.
     */
    public void initGame(long randSeed)
    { random.setSeed(randSeed); }

    /** Subclasses should use this object for random decisions.
     */
    protected final Random random()
    { return random; }

}
