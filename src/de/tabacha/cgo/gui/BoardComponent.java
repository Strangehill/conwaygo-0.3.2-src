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

import java.awt.*;
import javax.swing.JPanel;

import de.tabacha.cgo.*;


/** A swing component for displaying a (conway)go board.
   <br />$Id: BoardComponent.java,v 1.15 2004/12/23 21:42:43 mk Exp $
   @see HumanPlayer for support to enter a move.
   @author michael@tabacha.de
   @author $Author: mk $
   @version $Revision: 1.15 $
   @threadsafe false
*/
public class BoardComponent
    extends JPanel
    implements Constants
{

    // --------------- constants ---------------------------
    
    // The different types of Carets (the cursor on the board).
    static final byte NO_CARET = Constants.EMPTY;
    static final byte JUMP_CARET = Constants.BALL;
    static final byte PUT_CARET = Constants.PIECE;

    // Colors
    private static final Color LINE_COLOR = Color.black;
    private static final Color BG_COLOR = Color.white;
    private static final Color BALL_COLOR = Color.red;
    private static final Color PIECE_COLOR = Color.blue;
    private static final Color PIECE_FADING_COLOR = Color.cyan;
    private static final Color PUT_COLOR = PIECE_COLOR;
    private static final Color JUMP_COLOR = BALL_COLOR;

    // Sizes
    private static final int MIN_WIDTH = 7 * COL_COUNT;
    private static final int MIN_HEIGHT = 7 * ROW_COUNT;
    private static final int PREF_WIDTH = 25 * COL_COUNT;
    private static final int PREF_HEIGHT = 25 * ROW_COUNT;

    // Times
    private static final int PREF_WAIT_MOVE = 1000;
    private static final int PREF_WAIT_ATOMIC = 200;


    // ------------------------ static attributes ----------------------------

    /** The time to wait between two moves, in sec/1000. */
    private static int waitMove = PREF_WAIT_MOVE;
    /** The time to wait between changes in a move, in sec/1000. */
    private static int waitAtomic = PREF_WAIT_ATOMIC;


    // ------------------------ static methods -------------------------------

    /** Returns 0 if number is zero,
	-1 if number is negative,
	+1 if number is positive.
     */
    private static final int sgn(int number)
    {
	if (number > 0) return +1;
	else if (number < 0) return -1;
	else return 0;
    }


    /** Returns the time to wait between two moves, in sec/1000.
     */
    public static int getWaitMove()
    { return waitMove; }

    /** Sets the time to wait between two moves, in sec/1000.
     */
    public static void setWaitMove(int mSec)
    {
	waitMove = mSec;
	waitAtomic = mSec/5;
    }


    // ---------------- variables ---------------------

    private int leftSpace;
    private int topSpace;
    private int fieldWidth;
    private int fieldHeight;
    private int circleWidth;
    private int circleHeight;
    // distance from begin of field to begin of circle
    private int circleSpaceX;
    private int circleSpaceY;
    private int ringSpaceX;
    private int ringSpaceY;
    private int ringWidth;
    private int ringHeight;

    private Board board;

    private boolean editing;
    private boolean ignoreNextMove;

    private byte caretType;
    private Field caretField;


    // -------------- initialization ------------------

    /** Constructor.
     */
    public BoardComponent()
    {
	super(true); // double buffering

	// model
	setBoard(null);
	caretType = NO_CARET;
	editing = false;
	ignoreNextMove = false;

	// buildGUI
	computeSizes(PREF_WIDTH, PREF_HEIGHT);
	setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
	setPreferredSize(new Dimension(PREF_WIDTH, PREF_HEIGHT));
	setOpaque(true);
	setBackground(BG_COLOR);

	// events: none
    }

    /** Calculates the sizes of the elements.
     */
    protected void computeSizes(int width, int height)
    {
	// fields have a center, so dimension must be odd.
	fieldWidth = width / COL_COUNT;
	if ((fieldWidth & 1) == 0) fieldWidth--;
	fieldHeight = height / ROW_COUNT;
	if ((fieldHeight & 1) == 0) fieldHeight--;

	leftSpace = (width - fieldWidth * COL_COUNT) / 2;
	topSpace = (height - fieldHeight * ROW_COUNT) / 2;

	circleWidth = fieldWidth / 3 * 2; // must be even
	circleHeight = fieldHeight / 3 * 2; // must be even
	circleSpaceX = (fieldWidth - circleWidth - 1) / 2; // distance from begin of field to begin of circle
	circleSpaceY = (fieldHeight - circleHeight - 1) / 2; // distance from begin of field to begin of circle

	ringSpaceX = circleSpaceX / 2;
	ringSpaceY = circleSpaceY / 2;
	ringWidth = fieldWidth - ringSpaceX * 2 - 1;
	ringHeight = fieldHeight - ringSpaceY * 2 - 1;
    }


    // --------------- helper methods ----------------------

    /** Converts a point in the component's pixel coordinates
	to a field on the board.
    */
    public Field point2Field(Point point)
    { return new Field((point.x - leftSpace) / fieldWidth, (point.y - topSpace) / fieldHeight); }


    /** Wait for user to realize completion of move.
     */
    public void waitMove()
    {
	if (ignoreNextMove)
	    return;
	try {
	    Thread.sleep(waitMove);
	} catch (InterruptedException exc) {
	    Thread.currentThread().interrupt();
	}
    }

    /** Wait for user to realize atomic change.
     */
    public void waitAtomic()
    {
	try {
	    Thread.sleep(waitAtomic);
	} catch (InterruptedException exc) {
	    Thread.currentThread().interrupt();
	}
    }


    // ------------------ get/set methods ----------------

    /** Returns the board model.
     */
    public Board getBoard()
    { return board; }

    /** Sets a new board.
     *  Changes on this board will not automatically seen on the screen,
     * use the method "performMove" instead.
     * @param board the new board. If it is null, an empty board will be created and shown.
     */
    public void setBoard(Board board)
    {
        if (board == null)
        {
            board = new DefaultBoard();
            ((DefaultBoard)board).clear();
        }
        else
        {
            // All moves must be performed on this clone in this class, to track all movements of one move.
            // DefaultBoard is not synchronized, so this class needs its own board.
            this.board = (Board)board.clone();
        }
	repaint();
    }

    /** Returns true if this component is in editor mode.
     */
    public boolean isEditing()
    { return editing; }


    /** Changes size and/or location of the component.
	(This method is officially deprecated but all similar methods rely on it.)
    */
    public void reshape(int x, int y, int width, int height)
    {
	if (width != getWidth() || height != getHeight())
	    computeSizes(width, height);
	super.reshape(x, y, width, height);
    }


    // ---------------------- MoveEditor methods --------------------------------

    /**
        @threadsafe true
    */
    public void startEdit(HumanPlayer moveBuilder)
    { EventQueue.invokeLater(new EditOnOff(moveBuilder, EditOnOff.START)); }

    /**
        @threadsafe true
    */
    public void endEdit(HumanPlayer moveBuilder)
    { EventQueue.invokeLater(new EditOnOff(moveBuilder, EditOnOff.END)); }

    /**
        @threadsafe true
    */
    public void abortEdit(HumanPlayer moveBuilder)
    { EventQueue.invokeLater(new EditOnOff(moveBuilder, EditOnOff.ABORT)); }


    // ------------------- perform moves ----------------------

    /** Shows a move on the screen.
	No check for validity.
	@param move The move to perform
     */
    public void performMove(Move move)
    {
	if (ignoreNextMove)
	    {
		ignoreNextMove = false;
		return;
	    }
	if (editing)
	    removeCaret(getGraphics());

	if (move instanceof Put)
	    performPut(((Put)move).getField());
	else
	    performJump((Jump)move);
    }

    /** Performs a put.
     */
    protected void performPut(Field field)
    {
	Graphics g = getGraphics();
	drawCircle(g, field.col(), field.row(), PIECE_FADING_COLOR);
	waitAtomic();
	drawCircle(g, field.col(), field.row(), PIECE_COLOR);
	board.addPiece(field.col(), field.row());
    }

    /** Performs a jump.
     */
    protected void performJump(Jump jump)
    {
	Field ball = board.getBall();
	jump.startIteration();
	while (jump.hasNextLeap())
	    {
		ball = performLeap(ball, jump.nextLeap());
		if (jump.hasNextLeap() && !isEditing()) waitAtomic();
	    }
    }

    /** Performs a leap.
	@return The new ball field.
    */
    protected Field performLeap(Field from, byte direction)
    {
	// Find field to move the ball to.
	Field to = from.fieldIn(direction);
	while (board.on(to) == PIECE)
	    to.moveIn(direction);

	int dx = sgn(to.col()-from.col());
	int dy = sgn(to.row()-from.row());
	Graphics g = getGraphics();
	
	// pieces fading
	int x = from.col() + dx;
	int y = from.row() + dy;
	do
	    {
		drawCircle(g, x, y, PIECE_FADING_COLOR);
		x += dx; y += dy;
	    }
	while (x != to.col() || y != to.row());
	
	// moving ball
	waitAtomic();
	clearField(g, from.col(), from.row());
	drawCircle(g, to.col(), to.row(), BALL_COLOR);
	board.setBall(to);
	
	// removing pieces
	waitAtomic();
	x = from.col() + dx;
	y = from.row() + dy;
	do
	    {
		clearField(g, x, y);
		board.removePiece(x, y);
		x += dx; y += dy;
	    }
	while (x != to.col() || y != to.row());

	return to;
    }


    // ----------- Caret methods -----------------

    /** Shows, changes or removes the caret.
	@param selected Where to show the caret. May only be null if type is NO_CARET.
	@param type What caret to show. One of the constants NO_CARET, JUMP_CARET, PUT_CARET.
     */
    void setCaret(Field selected, byte type)
    {
	Graphics g = getGraphics();
	if (type == NO_CARET)
	    removeCaret(g);
	else if (!selected.equals(caretField) || type != caretType)
	    {
		removeCaret(g);
		
		// draw new Caret
		caretType = type;
		caretField = (Field)selected.clone();
		drawRing(g, selected.col(), selected.row(), (type == PUT_CARET) ? PUT_COLOR : JUMP_COLOR);
	    }
    }

    /** Removes the caret from the board.
     */
    private void removeCaret(Graphics g)
    {
	if (caretType == NO_CARET)
	    return;
	caretType = NO_CARET;
	repaintField(g, caretField);
    }


    // ----------------- painting ------------------

    /** Actually paints the board on the screen.
     */
    protected void paintComponent(Graphics g)	
    {
	super.paintComponent(g);
	drawLines(g);

	byte what;
	for (int x=0; x<COL_COUNT; x++)
	    for (int y=0; y<ROW_COUNT; y++)
		{
		    what = board.on(x,y);
		    if (what == PIECE)
			drawCircle(g, x, y, PIECE_COLOR);
		    else if (what == BALL)
			drawCircle(g, x, y, BALL_COLOR);
		}
	if (caretType != NO_CARET)
	    drawRing(g, caretField.col(), caretField.row(), (caretType == PUT_CARET) ? PUT_COLOR : JUMP_COLOR );
    }


    // --------------- private painting ---------------------

    /**
     */
    private void repaintField(Graphics g, Field field)
    {
	if (field.isOutside())
	    return;
	clearField(g, field.col(), field.row());
	byte what = board.on(field.col(),field.row());
	if (what == PIECE)
	    drawCircle(g, field.col(), field.row(), PIECE_COLOR);
	else if (what == BALL)
	    drawCircle(g, field.col(), field.row(), BALL_COLOR);
	if (caretType != NO_CARET && field.equals(caretField))
	    drawRing(g, field.col(), field.row(), (caretType == PUT_CARET) ? PUT_COLOR : JUMP_COLOR);
    }

    /** Draws the background lines of the go board.
     */
    private void drawLines(Graphics g)
    {
	int i;
	g.setColor(LINE_COLOR);
	
	// horizontal
	int hori = fieldHeight/2;
	int firstCol = fieldWidth/2;
	int lastCol = fieldWidth * COL_COUNT - fieldWidth/2 - 1;
	for (i=0; i<ROW_COUNT; i++, hori += fieldHeight)
	    g.drawLine(leftSpace + firstCol, topSpace + hori, leftSpace + lastCol, topSpace + hori);

	// vertical
	int vert = fieldWidth/2;
	int firstRow = fieldHeight/2;
	int lastRow = fieldHeight*ROW_COUNT - fieldHeight/2 - 1;
	for (i=0; i<COL_COUNT; i++, vert += fieldWidth)
	    g.drawLine(leftSpace + vert, topSpace + firstRow, leftSpace + vert, topSpace + lastRow);
    }

    /** Removes contents of a single field.
     */
    private void clearField(Graphics g, int col, int row)
    {	
	if (Field.isOutside(col, row))
	    return;
	g.setColor(BG_COLOR);
	g.fillRect(leftSpace + col * fieldWidth, topSpace + row * fieldHeight,
		   fieldWidth, fieldHeight);

	// repaint lines
	int middleX = col*fieldWidth + fieldWidth/2;
	int middleY = row*fieldHeight + fieldHeight/2;
	g.setColor(LINE_COLOR);
	g.drawLine(leftSpace + middleX, topSpace + (row==0 ? middleY : middleY-fieldHeight/2),
		   leftSpace + middleX, topSpace + (row==ROW_COUNT-1 ? middleY : middleY+fieldHeight/2));
	g.drawLine(leftSpace + (col==0 ? middleX : middleX-fieldWidth/2), topSpace + middleY,
		   leftSpace + (col==COL_COUNT-1 ? middleX : middleX+fieldWidth/2), topSpace + middleY);
    }

    /** Paints a circle on a field.
    */
    private void drawCircle(Graphics g, int col, int row, Color color)
    {
	if (Field.isOutside(col, row))
	    return;
	g.setColor(color);
	g.fillOval(leftSpace + col*fieldWidth + circleSpaceX,
		   topSpace + row*fieldHeight + circleSpaceY,
		   circleWidth,  // windows: +1; linux: +0
		   circleHeight); // windows: +1; linux: +0	
    }

    /** Paints a ring around a field on the board.
     */
    private void drawRing(Graphics g, int col, int row, Color color)
    {
	if (Field.isOutside(col, row))
	    return;
	g.setColor(color);
	g.drawOval(leftSpace + col*fieldWidth + ringSpaceX,
		   topSpace + row*fieldHeight + ringSpaceY,
		   ringWidth,
		   ringHeight);
	
/* Test code
	g.setColor(java.awt.Color.green);
	
	// According to the javadocs,
	g.fillRoundRect(20,20, 20+1,20+1, 20,20);
	// should produce the same results as
	// g.fillOval(20,20, 20,20, 20,20);
	// This is true for Linux, but in Windows the equivalent is
	// g.fillOval(20,20, 20+1,20+1, 20,20);
	
	// javadoc for java.awt.Graphics says:
	// for the methods drawOval, fillOval, drawRect and fillRect the right edge is at x+width,
	// whereas for drawRoundRect and fillRoundRect the right edge is at x+width-1.
	// Under Windows, the right edge for fillOval is at x+width-1.
	
	g.setColor(java.awt.Color.red);
	g.drawOval(20,20,20,20);
	g.setColor(java.awt.Color.black);
	g.drawRect(20,20,20,20);
	*/
    }


    // ----------------------- inner classes ---------------------------------

    private class EditOnOff
	implements Runnable
    {
	private static final byte START = 0;
	private static final byte END   = 1;
	private static final byte ABORT = 2;

	private HumanPlayer moveBuilder;
	private byte mode;

	public EditOnOff(HumanPlayer moveBuilder, byte mode)
	{
	    this.moveBuilder = moveBuilder;
	    this.mode = mode;
	}

	public void run()
	{
	    switch (mode)
		{
		case START:
		    editing = true;
		    addMouseListener(moveBuilder);
		    addMouseMotionListener(moveBuilder);
		    // Is there any way to locate the mouse cursor when no mouse events were received?
		    // Problem is that the caret is not shown immediately, but after the first mouse event.
		    break;
		case END:
		    if (editing)
			{
			    removeMouseListener(moveBuilder);
			    removeMouseMotionListener(moveBuilder);
			    removeCaret(getGraphics());
			    editing = false;
			    ignoreNextMove = true;
			}
		    break;
		case ABORT:
		    removeMouseListener(moveBuilder);
		    removeMouseMotionListener(moveBuilder);
		    removeCaret(getGraphics());
		    editing = false;
		    ignoreNextMove = false;
		    break;
		}
	}
    }

}
