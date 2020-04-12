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

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


	/** A panel that shows a localized help text.
	 The resource is found in the same way as a ResourceBundle.
	 For example, given a basename of "introduction", a suffix of ".html" and
	 Swiss german, it will be first searched in introduction_de_CH.html,
	 then in introduction_de.html, at last in introduction.html.
    <br />$Id: LocalizedResourcePanel.java,v 1.2 2004/12/23 21:42:44 mk Exp $
    @author michael@tabacha.de
    @author $Author: mk $
    @version $Revision: 1.2 $
    @threadsafe false
    @see java.util.ResourceBundle
*/
public class LocalizedResourcePanel
    extends JEditorPane
{

    // ------------------------ constants ------------------------------------
    // ------------------------ static attributes ----------------------------
    // ------------------------ static methods -------------------------------

    /** Shows a frame with the localized document.
	@throws java.io.IOException Unable to load document
     */
    public static JFrame showFrame(String title, String basename, String suffix)
	throws IOException
    {
	JFrame frame = new JFrame(title);
	frame.setSize(600, 600);
	frame.getContentPane().add(new JScrollPane(new LocalizedResourcePanel(basename, suffix)));
	frame.setVisible(true);
	return frame;
    }

    /**
     * Calculate the resource names along the search path.
     (Inspired from java.util.ResourceBundle.calculateBundleNames)
     * @param baseName the base bundle name
     * @param locale the locale
     */
    private static String[] calculateResourceNames(String baseName, Locale locale) {
        final String[] result = new String[4];
        final String language = locale.getLanguage();
        final int languageLength = language.length();
        final String country = locale.getCountry();
        final int countryLength = country.length();
        final String variant = locale.getVariant();
        final int variantLength = variant.length();

	result[0] = baseName;

        if (languageLength + countryLength + variantLength == 0)
            return result;
        final StringBuffer temp = new StringBuffer(baseName);
        temp.append('_').append(language);
        if (languageLength > 0)
            result[1] = temp.toString();

        if (countryLength + variantLength == 0)
            return result;
        temp.append('_').append(country);
        if (countryLength > 0)
            result[2] = temp.toString();

        if (variantLength == 0)
            return result;
        temp.append('_').append(variant);
        result[3] = temp.toString();

        return result;
    }


    // ------------------------ variables ------------------------------------

    // ------------------------ constructor and initialization methods -------

    /** Constructor.
	@throws java.io.IOException Unable to load document
     */
    public LocalizedResourcePanel(String basename, String suffix)
	throws IOException
    { this(basename, suffix, Locale.getDefault()); }

    /** Constructor.
	@throws java.io.IOException Unable to load document
     */
    public LocalizedResourcePanel(String basename, String suffix, Locale locale)
	throws IOException
    {
	super();
	setPage(calculateResource(basename, suffix, locale));
	setEditable(false);
    }

    // ----------------------- listener methods ------------------------------
    // ----------------------- getter & setter methods -----------------------
    // ----------------------- other methods ---------------------------------

    /** Returns the resource that most fits the given locale.
	(This method is not static because the resources are loaded with the method getResource
	of Class/ClassLoader. ClassLoader.getSystemResource does not work for applets.)
     */
    private URL calculateResource(String basename, String suffix, Locale locale)
    {
	URL found;
	String[] names = calculateResourceNames(basename, Locale.getDefault());

	for (int i=names.length-1; i>=0; i--)
		if (names[i] != null)
		    {
			found = getClass().getResource(names[i] + suffix);
			if (found != null)
			    return found;
		    }
	return null;
    }

    // ----------------------- inner classes ---------------------------------
}
