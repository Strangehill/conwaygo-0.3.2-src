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
package de.tabacha.cgo.gui;

import java.util.Map;
import java.util.Iterator;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.JLabel;


/** A graphical element that shows a Map.
    The keys are shown in the left, the values in the right column.
    The keys and values in the map are rendered using the method toString.
   <br />$Id: MapPanel.java,v 1.3 2004/12/23 21:42:44 mk Exp $
   @see java.util.Map
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.3 $
   @threadsafe false
*/
public class MapPanel
    extends JPanel
{
    // ------------------------ constants ------------------------------------
    // ------------------------ static attributes ----------------------------
    // ------------------------ static methods -------------------------------
    
    /** Creates the grid bag constraints object for all labels on the left.
     */
    private static final GridBagConstraints createKeyConstraints()
    {
    	GridBagConstraints gbcName = new GridBagConstraints();
    	gbcName.gridx = 0;
    	gbcName.anchor = GridBagConstraints.FIRST_LINE_START;
    	gbcName.ipadx = 5;
    	gbcName.ipady = 5;
    	return gbcName;	
    }

    /** Creates the grid bag constraints object for all labels on the right.
     */
    private static final GridBagConstraints createValueConstraints()
    {
        GridBagConstraints gbcValue = new GridBagConstraints();
        gbcValue.gridwidth = GridBagConstraints.REMAINDER;
        gbcValue.weightx = 1.0;
        gbcValue.anchor = GridBagConstraints.FIRST_LINE_START;
        gbcValue.ipadx = 5;
        gbcValue.ipady = 5;
        return gbcValue;
    }

	// ------------------------ variables ------------------------------------
    // ------------------------ constructor and initialization methods -------

    /** Constructor.
	@param content An array of key/value pairs, as in {@link java.util.ListResourceBundle#getContents}
     */
    public MapPanel(Object[][] content)
    {
        super(new GridBagLayout());
        GridBagConstraints gbcName = createKeyConstraints();
        GridBagConstraints gbcValue = createValueConstraints();
	
        for (int i=0; i<content.length; i++)
        {
            add(new JLabel(content[i][0].toString()), gbcName);
            add(new JLabel(content[i][1].toString()), gbcValue);
	    }
    }

    /** Constructor.
     * @param map The data to show in the table.
     *
     */
    public MapPanel(Map map)
    {
        super(new GridBagLayout());
        GridBagConstraints gbcName = createKeyConstraints();
        GridBagConstraints gbcValue = createValueConstraints();
	
        Iterator it = map.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry entry = (Map.Entry)(it.next());
            add(new JLabel(entry.getKey().toString()), gbcName);
            add(new JLabel(entry.getValue().toString()), gbcValue);
	    }
    }
        
    // ----------------------- listener methods ------------------------------
    // ----------------------- getter & setter methods -----------------------
    // ----------------------- other methods ---------------------------------
    // ----------------------- inner classes ---------------------------------
}
