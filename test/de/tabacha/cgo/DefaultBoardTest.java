/*
 * Created on 22.10.2004
 */
package de.tabacha.cgo;

import junit.framework.TestCase;


/**
 * @author michael_k
 */
public class DefaultBoardTest extends TestCase {

    private DefaultBoard board;
    
    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        board = new DefaultBoard();
    }

    /**
     * Constructor for DefaultBoardTest.
     * @param arg0
     */
    public DefaultBoardTest(String arg0) {
        super(arg0);
    }

    /*
     * Class under test for void DefaultBoard()
     */
    public void testDefaultBoard() {
        board = new DefaultBoard();
        for (int i=0; i < Constants.ROW_COUNT; i++)
            for (int j=0; j < Constants.COL_COUNT; j++)
                if (i!=Constants.KICK_OFF_FIELD.col() || j!=Constants.KICK_OFF_FIELD.row())
                    assertTrue(board.on(i,j) == Constants.EMPTY);
                else
                    assertTrue(board.on(i,j) == Constants.BALL);
        assertEquals(board.getBall(), Constants.KICK_OFF_FIELD);
    }

    /*
     * Class under test for void DefaultBoard(byte[][], Field, boolean)
     */
    public void testDefaultBoardbyteArrayArrayFieldboolean() {
        board.addPiece(4,7);
        board.setBall(15,15);
        board.nextTurn();
        
        DefaultBoard b2 = new DefaultBoard(board.toArray(), board.getBall(), board.whoseTurn());
        assertEquals(board, b2);
    }

    /*
     * Class under test for Object clone()
     */
    public void testClone()
    {
        DefaultBoard b2 = (DefaultBoard)board.clone();
        assertEquals(board, b2);
        assertNotSame(board, b2);
        
        b2.nextTurn();
        assertFalse(board.equals(b2));
    }

    public void testToArray()
    {
        board.clear();
        board.addPiece(15,17);
        board.setBall(12,4); // col,row
        byte[][] fields = board.toArray();
        assertEquals(fields[17][15], DefaultBoard.PIECE); // row,col
        assertEquals(fields[4][12], DefaultBoard.BALL);
        assertEquals(fields[4][1], DefaultBoard.EMPTY);
        fields[4][1] = DefaultBoard.PIECE;
        assertEquals(board.on(1,4), DefaultBoard.EMPTY);
    }

    public void testClear()
    {
        assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.BALL);
        assertFalse(board.getBall().isOutside());
        board.clear();
        assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.EMPTY);
        assertTrue(board.getBall().isOutside());
    }

    public void testReset()
    {
        board.clear();
        assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.EMPTY);
        assertTrue(board.getBall().isOutside());
        board.reset();
        assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.BALL);
        assertFalse(board.getBall().isOutside());
    }

    public void testUpsideDown()
    {
        board.addPiece(16,4);
        board.setBall(0,0);
        boolean color = board.whoseTurn();
        
        DefaultBoard b2 = (DefaultBoard)board.upsideDown();
        
        assertEquals(b2.on(16,4), DefaultBoard.EMPTY);
        assertEquals(b2.on(16,18-4), DefaultBoard.PIECE);
        assertEquals(b2.on(0,0), DefaultBoard.EMPTY);
        assertEquals(b2.on(0,18-0), DefaultBoard.BALL);
        assertEquals(b2.getBall(), new Field(0,18-0));
        assertTrue(b2.whoseTurn() == !color);
    }

    public void testNextTurn()
    {
        boolean c1 = board.whoseTurn();
        board.nextTurn();
        assertFalse(board.whoseTurn() == c1);
        board.nextTurn();
        assertTrue(board.whoseTurn() == c1);
    }

    public void testWhoseTurn()
    {
        assertTrue(board.whoseTurn() == board.whoseTurn());
    }

    /*
     * Class under test for byte on(int, int)
     */
    public void testOnintint()
    {
        assertEquals(board.on(-1,-1), DefaultBoard.EMPTY);
        assertEquals(board.on(9,9), DefaultBoard.BALL);
        assertEquals(board.on(14,4), DefaultBoard.EMPTY);
        board.addPiece(14,4);
        assertEquals(board.on(14,4), DefaultBoard.PIECE);
    }

    /*
     * Class under test for byte on(Field)
     */
    public void testOnField()
    {
        assertEquals(board.on(new Field(-1,-1)), DefaultBoard.EMPTY);
        assertEquals(board.on(new Field(9,9)), DefaultBoard.BALL);
        assertEquals(board.on(new Field(14,4)), DefaultBoard.EMPTY);
        board.addPiece(14,4);
        assertEquals(board.on(new Field(14,4)), DefaultBoard.PIECE);
    }

    public void testGetBall()
    {
        board.setBall(4,3);
        assertEquals(board.getBall(), new Field(4,3));
    }

    /*
     * Class under test for void setBall(Field)
     */
    public void testSetBallField()
    {
        Field in1 = new Field(1,2);
        Field in2 = new Field(10,11);
        Field out1 = new Field(-1,-1);
        Field out2 = new Field(6,19);
        
        // initial positioning
        board.setBall(in1);
        assertEquals(board.getBall(), in1);
        assertEquals(board.on(in1), DefaultBoard.BALL);

        // from inside to inside
        board.setBall(in2);
        assertEquals(board.getBall(), in2);
        assertEquals(board.on(in1), DefaultBoard.EMPTY);
        assertEquals(board.on(in2), DefaultBoard.BALL);
        
        // from inside to outside
        board.setBall(out1);
        assertEquals(board.getBall(), out1);
        assertEquals(board.on(in2), DefaultBoard.EMPTY);
        assertEquals(board.on(out1), DefaultBoard.EMPTY); // !

        // from outside to outside
        board.setBall(out2);
        assertEquals(board.getBall(), out2);
        assertEquals(board.on(out1), DefaultBoard.EMPTY);
        assertEquals(board.on(out2), DefaultBoard.EMPTY); // !

        // from outside to inside
        board.setBall(in1);
        assertEquals(board.getBall(), in1);
        assertEquals(board.on(out2), DefaultBoard.EMPTY);
        assertEquals(board.on(in1), DefaultBoard.BALL);
    }

    /*
     * Class under test for void setBall(int, int)
     */
    public void testSetBallintint()
    {
        Field in1 = new Field(1,2);
        Field in2 = new Field(10,11);
        Field out1 = new Field(-1,-1);
        Field out2 = new Field(6,19);
        
        // initial positioning
        board.setBall(in1.col(), in1.row());
        assertEquals(board.getBall(), in1);
        assertEquals(board.on(in1), DefaultBoard.BALL);

        // from inside to inside
        board.setBall(in2.col(), in2.row());
        assertEquals(board.getBall(), in2);
        assertEquals(board.on(in1), DefaultBoard.EMPTY);
        assertEquals(board.on(in2), DefaultBoard.BALL);
        
        // from inside to outside
        board.setBall(out1.col(), out1.row());
        assertEquals(board.getBall(), out1);
        assertEquals(board.on(in2), DefaultBoard.EMPTY);
        assertEquals(board.on(out1), DefaultBoard.EMPTY); // !

        // from outside to outside
        board.setBall(out2.col(), out2.row());
        assertEquals(board.getBall(), out2);
        assertEquals(board.on(out1), DefaultBoard.EMPTY);
        assertEquals(board.on(out2), DefaultBoard.EMPTY); // !

        // from outside to inside
        board.setBall(in1.col(), in1.row());
        assertEquals(board.getBall(), in1);
        assertEquals(board.on(out2), DefaultBoard.EMPTY);
        assertEquals(board.on(in1), DefaultBoard.BALL);
    }
    
    /*
     * Class under test for void setPiece(Field)
     */
    public void testSetPieceField()
    {
       assertEquals(board.on(13,7), DefaultBoard.EMPTY);
       board.addPiece(new Field(13,7));
       assertEquals(board.on(13,7), DefaultBoard.PIECE);
       
       // nothing happens if setting on an occupied field
       board.addPiece(DefaultBoard.KICK_OFF_FIELD);
       assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.BALL);
    }

    /*
     * Class under test for void setPiece(int, int)
     */
    public void testSetPieceintint()
    {
        assertEquals(board.on(13,7), DefaultBoard.EMPTY);
        board.addPiece(13,7);
        assertEquals(board.on(13,7), DefaultBoard.PIECE);
        
        // nothing happens if setting on an occupied field
        board.addPiece(9,9);
        assertEquals(board.on(DefaultBoard.KICK_OFF_FIELD), DefaultBoard.BALL);
    }

    /*
     * Class under test for void removePiece(Field)
     */
    public void testRemovePieceField()
    {
        board.addPiece(0,18);
        assertEquals(board.on(0,18), DefaultBoard.PIECE);

        board.removePiece(new Field(0,18));
        assertEquals(board.on(0,18), DefaultBoard.EMPTY);

        board.removePiece(new Field(0,1));
        assertEquals(board.on(0,1), DefaultBoard.EMPTY);
        
        board.removePiece(new Field(9,9));
        assertEquals(board.on(9,9), DefaultBoard.BALL);
    }

    /*
     * Class under test for void removePiece(int, int)
     */
    public void testRemovePieceintint()
    {
        board.addPiece(0,18);
        assertEquals(board.on(0,18), DefaultBoard.PIECE);

        board.removePiece(0,18);
        assertEquals(board.on(0,18), DefaultBoard.EMPTY);

        board.removePiece(0,1);
        assertEquals(board.on(0,1), DefaultBoard.EMPTY);
        
        board.removePiece(9,9);
        assertEquals(board.on(9,9), DefaultBoard.BALL);
    }

    public void testMove()
    {
        boolean color;
        
        // empty move
        try {
            board.move(null);
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.empty");
        }
        
        // simple put
        try {
            color = board.whoseTurn();
            board.move(new Put(8,9));
            assertTrue(board.whoseTurn() != color);
            assertEquals(board.on(8,9), DefaultBoard.PIECE);
        } catch (InvalidMoveException e) {
            fail();
        }

        // simple jump
        try {
            color = board.whoseTurn();
            board.move(new Jump(new byte[]{DefaultBoard.W}));
            assertTrue(board.whoseTurn() != color);
            assertEquals(board.on(9,9), DefaultBoard.EMPTY);
            assertEquals(board.on(8,9), DefaultBoard.EMPTY);
            assertEquals(board.on(7,9), DefaultBoard.BALL);
        } catch (InvalidMoveException e) {
            fail();
        }
    }

    public void testPut()
    {
        // simple put
        try {
            board.put(new Put(8,9));
            assertEquals(board.on(8,9), DefaultBoard.PIECE);
        } catch (InvalidMoveException e) {
            fail();
        }
        
        // put a piece onto a piece
        try {
            board.put(new Put(8,9));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.put_not_empty");
        }
        
        // put a piece onto the ball
        try {
            board.put(new Put(9,9));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.put_not_empty");
        }

        // put a piece outside the board
        try {
            board.put(new Put(75,9));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.put_outside");
        }
    }

    public void testJump()
    {
        // jump zero times
        try {
            board.reset();
            board.addPiece(10,9);
            board.jump(new Jump());
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.empty");
        }

        // jump once - a good one
        try {
            board.reset();
            board.addPiece(10,9);
            board.jump(new Jump(new byte[]{Constants.E}));
            assertEquals(board.on(9,9), Constants.EMPTY);
            assertEquals(board.on(10,9), Constants.EMPTY);
            assertEquals(board.on(11,9), Constants.BALL);
            assertEquals(board.getBall(), new Field(11,9));
        } catch (InvalidMoveException e) {
            fail();
        }
 
        // jump to goal
        try {
            board.reset();
            int r;
            for (r=10; r<19; r++)
                board.addPiece(9,r);
            board.jump(new Jump(new byte[]{Constants.S}));
            assertTrue(board.getBall().isInGoal());
            for (r=9; r<19; r++)
                assertEquals(board.on(9,r), Constants.EMPTY);
            assertTrue(board.getBall().isOutside());
        } catch (InvalidMoveException e) {
            fail();
        }

        // jump outside
        try {
            board.reset();
            int r;
            for (r=10; r<19; r++)
                board.addPiece(r,9);
            board.jump(new Jump(new byte[]{Constants.E}));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.jump_outside");
        }
    }

    public void testJumpOnce()
    {
        // jump over zero pieces
        try {
            board.reset();
            board.addPiece(8,8);
            board.jumpOnce(Constants.E, new Jump(new byte[]{Constants.E}));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.jump_wrongdir");
        }

        // jump in nonexisting direction
        try {
            board.jumpOnce((byte)60, new Jump(new byte[]{60}));
            fail();
        } catch (InvalidMoveException e) {
            assertEquals(e.getMessage(), "error.move.jump_nodir");
        }

        // a good jump
        try {
            board.reset();
            board.addPiece(10,9);
            board.jumpOnce(Constants.E, new Jump(new byte[]{Constants.E}));
            assertEquals(board.on(9,9), Constants.EMPTY);
            assertEquals(board.on(10,9), Constants.EMPTY);
            assertEquals(board.on(11,9), Constants.BALL);
            assertEquals(board.getBall(), new Field(11,9));
        } catch (InvalidMoveException e) {
            fail();
        }
        
        // jump in the corner
        try {
            board.clear();
            board.setBall(1,1);
            board.addPiece(0,0);
            board.jumpOnce(Constants.NW, new Jump(new byte[]{Constants.NW}));
            assertEquals(board.on(1,1), Constants.EMPTY);
            assertEquals(board.on(0,0), Constants.EMPTY);
            assertTrue(board.getBall().isOutside());
            assertTrue(board.getBall().isInGoal());
        } catch (InvalidMoveException e) {
            fail();
        }
    }

    public void testEquals()
    {
        assertFalse(board.equals(new Integer(13)));
        DefaultBoard b2 = (DefaultBoard)board.clone();
        assertTrue(board.equals(b2));
        b2.nextTurn();
        assertFalse(board.equals(b2));
        b2.nextTurn();
        assertTrue(board.equals(b2));
        b2.setBall(20,20);
        assertFalse(board.equals(b2));
    }

    public void testHashCode()
    {
        // Test if clone have the same hash code
        assertEquals(board.hashCode(), board.clone().hashCode());
        
        // no overflow?
        board.setBall(18,18);
        for (int c=0; c<18; c++)
            for (int r=0; r<18; r++)
                board.addPiece(c,r);
        assertTrue(board.hashCode() < Integer.MAX_VALUE);
    }

    public void testToString()
    {
        // not much to test here...output is meant for debugging only
        board.addPiece(4,8); // for 100% code coverage
        String output = board.toString();
        assertNotNull(output);
    }

}
