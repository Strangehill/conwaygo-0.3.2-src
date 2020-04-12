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

import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import de.tabacha.cgo.*;


/** Root pane for conwaygo with menu bar, board, etc.
   <br> $Id: CgoRootPane.java,v 1.35 2004/12/23 21:42:44 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.35 $
   @threadsafe false except for the methods that implements GameListener,
                     for these methods are called by another thread than the awt event queue.
*/
public class CgoRootPane
    extends JRootPane
    implements Constants, GameListener, ActionListener
{
    // ------------------------ constants ------------------------------------

    private static final String OVERALL_PROPERTIES = "/de/tabacha/cgo/conwaygo.properties";
    private static final String PROJECT_PROPERTIES = "/de/tabacha/cgo/project.properties";
    private static final String DOCUMENTATION_DIR = "/de/tabacha/cgo/doc/";
    private static final String DEFAULT_PLAYER = "de.tabacha.cgo.gui.HumanPlayer";


    // Constants for the GameEvent types.
    private static final int GAME_STARTED = 0;
    private static final int GAME_ABORTED = 1;
    private static final int GAME_ENDED = 2;
    private static final int HAS_MOVED = 3;
    private static final int PLAYER_CHANGED = 4;

    private static final Color COLOR_ACTIVE = Color.black;
    private static final Color COLOR_NOT_ACTIVE = Color.gray;


    /** Defines the names of the different types of strategies:
	They are divided in groups in the menues, but are all internally handled the same way.
    */
    private static final String[] STRATEGY_TYPES = new String[]{ "human","standard","extra" };


    // ------------------------ variables ------------------------------------

    // model
    private BackgroundGame game;
    private Properties conwaygoProperties;

    /** Is true when this root pane is part of an applet. */
    private boolean isApplet;

    // Menues and menu items
    private JMenuItem itemStartGame;
    private JMenuItem itemAbortGame;
    private BoardComponent boardView;


    // status line
    private JLabel pl1, pl2;
    private JLabel statusLine;

    private java.util.List strategies; // full package name because there also is a java.awt.List


    // ------------------------ constructor and initialization methods -------

    /** Constructor.
     */
    public CgoRootPane()
    { this(false); }

    /** Constructor.
	@param applet true when this root pane is part of an applet.
    */
    public CgoRootPane(boolean applet)
    {
	isApplet = applet;

	buildModel();
	buildGUI();
	hookupEvents();

	renewStatusText();
    }

    /** Creates the model objects.
     */
    private void buildModel()
    {
	game = new DefaultGame(null, null);

	// properties file
	conwaygoProperties = new Properties();
	try {
	    // getSystemResource does not work for applets
	    conwaygoProperties.load(getClass().getResourceAsStream(OVERALL_PROPERTIES));
	} catch (Exception exc) {
	    // IOException (as stated in the documentation) is not sufficient here,
	    // sometimes a NullPointerException is thrown when property file is not found.
	    showError("error.load_options");
	}

	// load engine list
	strategies = new ArrayList(7);
	for (int major=0; major < STRATEGY_TYPES.length; major++)
	    for (int minor=0; true; minor++)
		{
		    String key = "strategy." + STRATEGY_TYPES[major] + '.' + minor;
		    if (!conwaygoProperties.containsKey(key))
			break;
		    try {
			strategies.add(new StrategyActions(conwaygoProperties.getProperty(key), major));
		    } catch (Exception exc) {
			showError("error.load_engine", conwaygoProperties.getProperty(key));
		    }
		}
    }
    
    /** Builds up the graphical elements.
     */
    private void buildGUI()
    {
	// -------------- board ----------------

	try {
	    BoardComponent.setWaitMove(Integer.parseInt(conwaygoProperties.getProperty("animation.waitMove")));
	} catch (NumberFormatException exc) {
	    showError("error.load_options");
	}
	boardView = new BoardComponent();
	getContentPane().add(boardView, BorderLayout.CENTER);


	// --------- GAME menu ----------

	JMenuBar mainMenu = new JMenuBar();

	JMenu gameMenu = new JMenu(I18n.get("menu.game"));
	itemStartGame = createMenuItem(gameMenu, "action.start");
	itemAbortGame = createMenuItem(gameMenu, "action.abort");
	itemAbortGame.setEnabled(false);
	if (!isApplet)
	    {
		gameMenu.addSeparator();
		createMenuItem(gameMenu, "action.quit");
	    }
	mainMenu.add(gameMenu);


	// --------- PLAYER menues ----------

	JMenu menuUp = new JMenu(I18n.get("menu.up"));
	ButtonGroup radioGroupUp = new ButtonGroup();

	JMenu menuDn = new JMenu(I18n.get("menu.down"));
	ButtonGroup radioGroupDn = new ButtonGroup();

	mainMenu.add(menuUp);
	mainMenu.add(menuDn);


	// --------- OPTIONS menu -------

	JMenu options = new JMenu(I18n.get("menu.options"));
	createMenuItem(options, "action.options.gui");
	mainMenu.add(options);


	// --------- HELP menu ----------

	JMenu help = new JMenu(I18n.get("menu.help"));

	createMenuItem(help, "action.about");

	JMenu engineInfoSelect = new JMenu(I18n.get("menu.help.engine"));
	help.add(engineInfoSelect);

	// If applet, show help files in other browser pages, not here.
	// Keeps the applet jar small.
	if (!isApplet)
	    {
		help.addSeparator();
		createMenuItem(help, "action.rules");
		createMenuItem(help, "action.license");
	    }


	mainMenu.add(help);


	setJMenuBar(mainMenu);


	// ------- add engines to menues -------

	Iterator it = strategies.iterator();
	int type = 0;
	while (it.hasNext())
	    {
		StrategyActions wrapper = (StrategyActions)it.next();
		if (wrapper.strategyType != type)
		    {
			menuUp.addSeparator();
			menuDn.addSeparator();
			engineInfoSelect.addSeparator();
			type = wrapper.strategyType;
		    }
		radioGroupUp.add(menuUp.add(wrapper.menuUp));
		radioGroupDn.add(menuDn.add(wrapper.menuDn));
		engineInfoSelect.add(wrapper.menuInfo);
	    }


	// --------------- status line ---------------

	JPanel bottom = new JPanel(new FlowLayout());
	pl1 = new JLabel(); bottom.add(pl1);
	bottom.add(new JLabel("-"));
	pl2 = new JLabel(); bottom.add(pl2);
	statusLine = new JLabel(); bottom.add(statusLine);
	renewStatusText();
	getContentPane().add(bottom, BorderLayout.SOUTH);
    }

    /** Convenience method.
     */
    private JMenuItem createMenuItem(JMenu menu, String key)
    {
	JMenuItem item = new JMenuItem(I18n.get(key));
	item.setActionCommand(key);
	item.addActionListener(this);
	menu.add(item);
	return item;
    }

    /** Registers event listeners.
     */
    private void hookupEvents()
    {
	game.addGameListener(this);

	String upClass = conwaygoProperties.getProperty("strategy.selected.up", DEFAULT_PLAYER);
	String dnClass = conwaygoProperties.getProperty("strategy.selected.down", DEFAULT_PLAYER);
	Iterator it = strategies.iterator();
	while (it.hasNext())
	    {
		StrategyActions wrapper = (StrategyActions)it.next();
		if (wrapper.engineUp instanceof HumanPlayer)
		    ((HumanPlayer)wrapper.engineUp).setEditor(boardView);
		if (wrapper.engineDn instanceof HumanPlayer)
		    ((HumanPlayer)wrapper.engineDn).setEditor(boardView);
		if (wrapper.engineUp.getClass().getName().equals(upClass))
		    wrapper.menuUp.doClick();
		if (wrapper.engineDn.getClass().getName().equals(dnClass))
		    wrapper.menuDn.doClick();
	    }
    }


    // ----------------------- listener methods ------------------------------
    // ----------------- ActionListener ---------------

    /** Listen to menu item selections (except for engine selections).
     */
    public void actionPerformed(ActionEvent ae)
    {
	if (ae.getSource() == itemStartGame)
	    startGame();
	else if (ae.getSource() == itemAbortGame)
	    abortGame();
	else if ("action.quit".equals(ae.getActionCommand()))
	    quit();
	else if ("action.about".equals(ae.getActionCommand()))
	    showAbout();
	else if ("action.rules".equals(ae.getActionCommand()))
	    showHelp("rules",".html");
	else if ("action.license".equals(ae.getActionCommand()))
	    showHelp("license",".txt");
	else if ("action.options.gui".equals(ae.getActionCommand()))
	    showGUIOptions();
    }


    // ----------- from GameListener --------------

    /**
       @threadsafe true
    */
    public void gameStarted(GameEvent e)
    {
	if (EventQueue.isDispatchThread())
	    {
		itemStartGame.setEnabled(false);
		boardView.setBoard(e.getGame().getBoard());
		renewStatusText();
		itemAbortGame.setEnabled(true);
	    }
	else
	    try {
		EventQueue.invokeAndWait(new GameEventWorker(e, GAME_STARTED));
	    } catch (InvocationTargetException exc) {
		showError(exc.getCause().getMessage());
	    } catch (InterruptedException exc) {
		Thread.currentThread().interrupt();
	    }
    }

    /**
       @threadsafe true
    */
    public void gameAborted(GameEvent e)
    {
	if (EventQueue.isDispatchThread())
	    {
		itemAbortGame.setEnabled(false);
		renewStatusText();
		String displayText = I18n.get(e.getMessage());
		if (e.getMove() != null)
		    displayText += System.getProperty("line.separator") + e.getMove().toString();
		JOptionPane.showMessageDialog(this, displayText);
		itemStartGame.setEnabled(true);
	    }
	else
	    try {
		EventQueue.invokeAndWait(new GameEventWorker(e, GAME_ABORTED));
	    } catch (InvocationTargetException exc) {
		showError(exc.getCause().getMessage());
	    } catch (InterruptedException exc) {
		Thread.currentThread().interrupt();
	    }
    }

    /**
       @threadsafe true
    */
    public void gameEnded(GameEvent e)
    {
	if (EventQueue.isDispatchThread())
	    {
		itemAbortGame.setEnabled(false);
		renewStatusText();
		JOptionPane.showMessageDialog(this, I18n.get("msg.winner", game.getPlayer(e.getDirection()).getName()));
		itemStartGame.setEnabled(true);
	    }
	else
	    try {
		EventQueue.invokeAndWait(new GameEventWorker(e, GAME_ENDED));
	    } catch (InvocationTargetException exc) {
		showError(exc.getCause().getMessage());
	    } catch (InterruptedException exc) {
		Thread.currentThread().interrupt();
	    }
    }

    /**
       @threadsafe true
    */
    public void hasMoved(GameEvent e)
    {
	if (EventQueue.isDispatchThread())
	    {
		boardView.performMove(e.getMove());
		renewStatusText();
	    }
	else
	    {
		boardView.waitMove();
		try {
		    EventQueue.invokeAndWait(new GameEventWorker(e, HAS_MOVED));
		} catch (InvocationTargetException exc) {
		    showError(exc.getCause().getMessage());
		} catch (InterruptedException exc) {
		    Thread.currentThread().interrupt();
		}
	    }
    }

    /**
       @threadsafe true
    */
    public void playerChanged(GameEvent e)
    {
	if (EventQueue.isDispatchThread())
	    renewStatusText();
	else
	    try {
		EventQueue.invokeAndWait(new GameEventWorker(e, PLAYER_CHANGED));
	    } catch (InvocationTargetException exc) {
		showError(exc.getCause().getMessage());
	    } catch (InterruptedException exc) {
		Thread.currentThread().interrupt();
	    }
    }


    // ----------------------- getter & setter methods -----------------------

    /** Returns the model of this delegate.
     */
    public BackgroundGame getGame()
    { return game; }

    /** Sets the text to show in the status line.
     */
    public void setStatusText(String text)
    { statusLine.setText(text); }

    /** Writes appropriate text in the status line at the bottom.
     */
    protected void renewStatusText()
    {
	if (game.isAlive())
	    {
		setStatusText("");
		if (game.getBoard().whoseTurn() == UP)
		    {
			pl1.setForeground(COLOR_ACTIVE);
			pl2.setForeground(COLOR_NOT_ACTIVE);
		    }
		else
		    {
			pl1.setForeground(COLOR_NOT_ACTIVE);
			pl2.setForeground(COLOR_ACTIVE);
		    }
	    }
	else
	    {
		setStatusText(I18n.get("expr.not_playing"));
		pl1.setForeground(COLOR_NOT_ACTIVE);
		pl2.setForeground(COLOR_NOT_ACTIVE);
	    }

	Engine player;
	player = game.getPlayer(UP);
	pl1.setText( (player==null) ? I18n.get("expr.player_unset") : player.getName() );
	player = game.getPlayer(DOWN);
	pl2.setText( (player==null) ? I18n.get("expr.player_unset") : player.getName() );
    }


    // ----------------------- other methods ---------------------------------

    /** Starts game play.
     */
    public void startGame()
    {
	 try {
	     game.play(); 
	 } catch (IllegalStateException exc) {
	     if (!game.isAlive())
		 showWarning("warn.player_unset");
	     // if alive, do nothing. Game has already been started and menu item will be
	     // disabled soon. User has clicked twice on the menu item.
	 }
     }

    /** Aborts the game.
     */
    public void abortGame()
    {
	game.abort();
    }

    /** Closes conwaygo by disposing/destroying the parent.
	Game is aborted beforehand.
     */
    public void quit()
    {
	game.destroy();

	Container parent = getParent();
	if (parent instanceof Window)
	    {
		((Window)parent).dispose();
		System.exit(0);
	    }
	else if (parent instanceof JInternalFrame)
	    ((JInternalFrame)parent).dispose();
	else // if (parent instanceof Applet)
	    ;
    }

    /** Shows the dialog to configure the GUI.
     */
    public void showGUIOptions()
    {
	JPanel panel = new JPanel(new FlowLayout());

	panel.add(new JLabel(I18n.get("msg.animation_speed")));
	JSlider slider = new JSlider(0, 3000, BoardComponent.getWaitMove());
	slider.setExtent(100);
	slider.setInverted(true);
	panel.add(slider);

	int result = JOptionPane.showConfirmDialog(this, panel, I18n.get("action.options.gui"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
	if (result == JOptionPane.OK_OPTION)
	    {
		BoardComponent.setWaitMove(slider.getValue());
	    }
    }


    /** Shows a small dialog window with version information.
     */
    public void showAbout()
    {
	Properties versionInfo = new Properties();
	try {
	    versionInfo.load(getClass().getResourceAsStream(PROJECT_PROPERTIES));
	    JOptionPane.showMessageDialog
		(this,
		 new MapPanel(new String[][]{
		     { I18n.get("project.name"),     versionInfo.getProperty("name") },
		     { I18n.get("project.version"),  versionInfo.getProperty("version") },
		     { I18n.get("project.released"), versionInfo.getProperty("version_date") }
		 }),
		 I18n.get("action.about"),
		 JOptionPane.INFORMATION_MESSAGE);
	} catch (Exception exc) {
	    showError("error.load_version");
	}
    }
    
    /** Displays an window with information about an engine.
     */
    public void showEngineInfo(Engine engine)
    {
	try {
	    JOptionPane.showMessageDialog
		(CgoRootPane.this,
		 new MapPanel(new String[][]{
		     { I18n.get("engine.name"), engine.getName() },
		     { I18n.get("engine.description"), engine.getDescription() },
		     { I18n.get("engine.version"), engine.getVersion() },
		     { I18n.get("engine.author"), engine.getAuthor() },
		     { I18n.get("engine.class"), engine.getClass().getName() }
		 }),
		 I18n.get("engine.info", engine.getName()),
		 JOptionPane.INFORMATION_MESSAGE);
	} catch (MissingResourceException exc) {
	    showError("error.load_engine_property", new String[]{engine.getClass().getName(), exc.getKey()});
	}
    }

    /** Displays a help resource in a separate frame.
     */
    public void showHelp(String basename, String suffix)
    {
	try {
	    LocalizedResourcePanel.showFrame(I18n.get("expr.help"), DOCUMENTATION_DIR + basename, suffix);
	} catch (IOException exc) {
	    exc.printStackTrace();
	    showError("error.load_help");
	}
    }

    /** Displays a warning.
     */
    private void showWarning(String key)
    { JOptionPane.showMessageDialog(this, I18n.get(key), I18n.get("warn"), JOptionPane.WARNING_MESSAGE); }

    /** Displays information about an error.
     */
    private void showError(String key)
    { JOptionPane.showMessageDialog(this, I18n.get(key), I18n.get("error"), JOptionPane.ERROR_MESSAGE); }

    /** Displays information about an error.
     */
    private void showError(String key, String param)
    { JOptionPane.showMessageDialog(this, I18n.get(key, param), I18n.get("error"), JOptionPane.ERROR_MESSAGE); }

    /** Displays information about an error.
     */
    private void showError(String key, String[] params)
    { JOptionPane.showMessageDialog(this, I18n.get(key, params), I18n.get("error"), JOptionPane.ERROR_MESSAGE); }


    // ----------------------- inner classes ---------------------------------

    /** Worker class to ensure that the event coming in from the game are
	handled in the gui event queue.
	See the GameListener methods in CgoRootPane.
    */
    private class GameEventWorker
	implements Runnable
    {
	private int actionType;
	private GameEvent gev;

	/** Constructor.
	    @param actionType One of the constants defined in this class.
	*/
	public GameEventWorker(GameEvent e, int actionType)
	{ this.gev = e; this.actionType = actionType; }

	/** Performs the event.
	 */
	public void run()
	{
	    switch (actionType)
		{
		case GAME_STARTED:   gameStarted(gev); break;
		case GAME_ABORTED:   gameAborted(gev); break;
		case GAME_ENDED:     gameEnded(gev);   break;
		case HAS_MOVED:      hasMoved(gev);    break;
		case PLAYER_CHANGED: playerChanged(gev); break;
		}
	}
    }
    

    /**
     */
    private class StrategyActions
	    implements ActionListener
    {
	int strategyType;
	Engine engineUp, engineDn;
	JMenuItem menuUp, menuDn, menuInfo;

	/** Constructor.
	    @throws Exception If class cannot be loaded, instantiated or initialized.
	*/
	public StrategyActions(String className, int strategyType)
	    throws Exception
	{
	    this.strategyType = strategyType;

	    Class strategyClass = Class.forName(className);
	    engineUp = (Engine)strategyClass.newInstance();
	    engineDn = (Engine)strategyClass.newInstance();
	    
	    menuUp = new JRadioButtonMenuItem(engineUp.getName());
	    menuUp.addActionListener(this);
	    menuDn = new JRadioButtonMenuItem(engineDn.getName());
	    menuDn.addActionListener(this);
	    menuInfo = new JMenuItem(engineUp.getName());
	    menuInfo.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae)
	{
	    Object src = ae.getSource();
	    if (src == menuUp)
		game.setPlayer(UP, engineUp);
	    else if (src == menuDn)
		game.setPlayer(DOWN, engineDn);
	    else // (src == menuInfo)
		showEngineInfo(engineUp);
	}
    }

}
