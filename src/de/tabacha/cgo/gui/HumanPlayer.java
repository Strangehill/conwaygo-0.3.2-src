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

import java.util.Arrays;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

import de.tabacha.cgo.*;


/** The strategy is: The user makes the move.
    Design pattern: routine strategy, move builder
   <br /> $Id: HumanPlayer.java,v 1.16 2004/07/30 21:06:35 mk Exp $
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.16 $
   @threadsafe false
*/
final class HumanPlayer
    extends AbstractEngine
    implements MouseInputListener
{

    private Board board;    
    private Move move;

    private BoardComponent editor;

    private Field[] jumpTargets = new Field[1 + Constants.LAST_DIRECTION-Constants.FIRST_DIRECTION];
    private Field from;

    /**
       -1=it's a put, no jumps possible
        0=it's a put, jumps are possible
	1=it's a jump, zero steps
	2=it's a jump, one or more steps
	3=it's a jump, completed.
    */
    private int jumpState;

    

    /** Constructor.
	@param editor The GUI object where moves are entered.
     */
    public HumanPlayer(BoardComponent editor)
    { this.editor = editor; }

    /** Constructor.
     */
    public HumanPlayer()
    { this(null); }

    /** Sets the GUI object where moves are entered.
	Must not be done during editing.
	@throws IllegalStateException When changing the editor during an edit.
     */
    public void setEditor(BoardComponent editor)
	throws IllegalStateException
    {
	if (editor == this.editor)
	    return;
	if (this.editor != null && this.editor.isEditing())
	    throw new IllegalStateException("Trying to change to editor during edit is not allowed");
	this.editor = editor;
    }


    // ---------------- implementing Engine ----------------------

    public String getVersion()
    { return "1"; }

    public boolean canPlayBothSides()
    { return true; }

    /** Registers this object to the editor as a mouse listener and
	waits until the move is completed.
	This method must not be called from the AWTEventThread.

	(Notification means: the move is completed.
 	 Interruption means: the game is aborted.)
     */
    public synchronized Move think(Board position)
    { 
	board = position;
	move = null;
	jumpState = buildJumpTargets() ? 0 : -1;

	editor.startEdit(this);

	try {
	    wait();

	    editor.endEdit(this);
	    return move;
	} catch (InterruptedException exc) {
	    editor.abortEdit(this);
	    Thread.currentThread().interrupt();
	    return null;
	}
    }


    // --------------------------- methods for editor --------------------------

    /** Returns the caret (field cursor) to show.
	@see de.tabacha.cgo.gui.BoardComponent.Caret
     */
    private byte calculateCaretType(Field f)
    {
	if (jumpState >= 0 && board.getBall().equals(f))
	    return BoardComponent.JUMP_CARET;
	switch (jumpState)
	    {
	    case -1:
	    case 0:
		return putAllowed(f) ? BoardComponent.PUT_CARET : BoardComponent.NO_CARET;
	    case 1:
	    case 2:
		return leapAllowed(f) ? BoardComponent.JUMP_CARET : BoardComponent.NO_CARET;
	    }
	return BoardComponent.NO_CARET;
    }
    
    /** User has clicked on a field.
	Returns the move (or part of move) to perform.
	Returns null if no move possible.
	Returns an empty leap if the jump is completed.       
    */
    private void clickOn(Field f)
    {
	switch (jumpState)
	    {
	    case -1:
		if (putAllowed(f))
		    put(f);
		break;
	    case 0: // in put mode
		if (putAllowed(f))
		    put(f);
		else if (board.getBall().equals(f))
		    beginJump();
		break;
	    case 1:
		if (board.getBall().equals(f))
		    jumpState = 0; // go back to put mode if click on starting ball field
		else
		    leap(f);
		break;
	    case 2:
		if (board.getBall().equals(f))
		    completeJump();
		else
		    leap(f);
		break;
	    case 3: // this shouldn't happen
	    }
    }

    private boolean putAllowed(Field f)
    { return (jumpState <= 0 && board.on(f) == Constants.EMPTY); }

    private boolean leapAllowed(Field f)
    {
	for (int i=0; i<jumpTargets.length; i++)
	    if (jumpTargets[i] != null && jumpTargets[i].equals(f))
		return true;
	return false;
    }

    /** Tries to make a put move.
	@param field Where to put the piece
    */
    private void put(Field field)
    {
	try {
	    move = new Put(field);
	    board.move(move);
	    editor.performMove(move);
	    moveCompleted();
	} catch (InvalidMoveException exc) {
	    // This shouldn't happen...
	    throw new RuntimeException(exc);
	}
    }

    /** Signalling that a jump has begun.
     */
    private void beginJump()
    {
	jumpState = 1;
	buildJumpTargets();
	move = new Jump();
    }

    /** One single leap.
     */
    private void leap(Field leapTarget)
    {
	for (byte i=0; i<jumpTargets.length; i++)
	    if (leapTarget.equals(jumpTargets[i]))
		{
		    ((Jump)move).push((byte)(i+Constants.FIRST_DIRECTION));
		    Jump thisLeap = new Jump(new byte[]{(byte)(i+Constants.FIRST_DIRECTION)});
		    try {
			board.move(thisLeap);
			editor.performMove(thisLeap);
		    } catch (InvalidMoveException exc) {
			// This shouldn't happen...
			throw new RuntimeException(exc);
		    }
		    jumpState = 2;
		    if (buildJumpTargets())
			updateCaret(leapTarget);
		    else
			completeJump();
		}
    }

    /** Signalling the end of the jump.
     */
    private void completeJump()
    {
	jumpState = 3;
	moveCompleted();
    }

    /** Signals that a valid move is built.
     */
    private synchronized void moveCompleted()
    { notify(); }


    // --------------------- jump targets -------------------------

    /** Cleans the array with the jump targets.
     */
    private void cleanJumpTargets()
    { Arrays.fill(jumpTargets, null); }

    /** Fills the array jumpTargets with the possible target fields of single jumps.
	@return false if no jumps are possible
     */
    private boolean buildJumpTargets()
    {
	Field ballField = board.getBall();
	Field act;
	byte dir;
	boolean possible = false;

	for (byte i = 0; i < jumpTargets.length; i++)
	    {
		dir = (byte)(i + Constants.FIRST_DIRECTION);
		act = ballField.fieldIn(dir);
		if (board.on(act) == Field.PIECE)
		    {
			do { act.moveIn(dir); }
			while (board.on(act) == Field.PIECE);
			if (!act.isOutside())
			    {
				possible = true;
				jumpTargets[i] = act;
			    }
			else if (act.isInGoal())
			    {
				do { act.moveIn(Constants.OPPOSITE[dir]); }
				while (act.isOutside());
				possible = true;
				jumpTargets[i] = act;
			    }
			else
			    jumpTargets[i] = null;
		    }
		else
		    jumpTargets[i] = null;
	    }
	return possible;
    }
		

    // ----------------- MouseInputListener methods ------------------------

    private void updateCaret(Field f)
    { editor.setCaret(f, calculateCaretType(f)); }
    
    public void mouseClicked(MouseEvent e)
    { clickOn(editor.point2Field(e.getPoint())); }
    
    public void mouseEntered(MouseEvent e)
    { updateCaret(editor.point2Field(e.getPoint())); }
    
    public void mouseExited(MouseEvent e)
    { editor.setCaret(null, BoardComponent.NO_CARET); }
    
    public void mouseMoved(MouseEvent e)
    { updateCaret(editor.point2Field(e.getPoint())); }

    public void mouseDragged(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
