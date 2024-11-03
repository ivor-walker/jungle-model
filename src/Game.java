public class Game {
	public int HEIGHT = 9;
	public int WIDTH = 7;
	private int ALL_ROWS[] = getSequence(WIDTH);
	private int ALL_COLS[] = getSequence(HEIGHT);
	public int[] WATER_ROWS = {3, 4, 5};
	public int[] WATER_COLS = {1, 2, 4, 5};
	public int DEN_COL = 3;	
		
	private Player p0;
	private Player p1;
	private Square[][]board = new Square[HEIGHT][WIDTH];
	private Piece[][]pieces = new Piece[HEIGHT][WIDTH];
	
/**
 * 	Constructor for game: creates the board, adds pieces to their starting position
*/
	public Game(Player p0, Player p1) {
		this.p0 = p0;
		this.p1 = p1;
		this.addStartingSquares();
		this.addStartingPieces();		
	}

/**
 * 	Initialises board by setting all squares to plain
 * 	Then adding the three types of special squares
*/	
	private void addStartingSquares() {
		//Set the whole board to plainSquares
		this.bulkSetBoardSquare(PlainSquare::new, ALL_ROWS, ALL_COLS);
		
		//Set water squares
		this.bulkSetBoardSquare(WaterSquare::new, WATER_ROWS, WATER_COLS);

		//Set traps and dens for both players
		this.addTrapsAndDen(p0);
		this.addTrapsAndDen(p1);
	}

	private int[] getSequence(n) {
		return java.util.stream.IntStream
			.rangeClosed(0, n)
			.toArray();
	}

	private int[] getSequence(start, n) {
		return java.util.stream.IntStream
			.rangeClosed(start, n)
			.toArray();
	}
/**
 * 	Individual square setter for board
*/	
	private void setBoardSquare(Coordinate targetLocation, Square targetSquare) {
		board[targetLocation.col(), targetLocation.row()] = targetSquare;	
	}

/**
 * 	Bulk square setter for board
 * 	@param squareSupplier factory for generating the requested subclass of Square 
 * 	@param targetRows 
 * 	@param targetCols 
*/
	
	private void bulkSetBoardSquare(Supplier<? extends Square> squareSupplier, int[] targetRows, int[] targetCols) {
		for (row: targetRows) {
			for (col: targetCols) {
				Coordinate targetLocation = new Coordinate(row, col);
				Square newSquare = squareSupplier.get();
				this.setBoardSquare(targetLocation, newSquare);	
			}
		}	
	}
/**
 * 	Set den and traps manually for a given player	
 * 	@param player 
*/
	private void addTrapsAndDen(Player player) {
		//Get column location of home	
		int COL_TRAP_PADDING = 1;
		int[] targetCols = getSequence(DEN_COL - TRAP_PADDING, DEN_COL + TRAP_PADDING);
		
		//Get player number specific things	
		int denRow;
		int ROW_TRAP_PADDING = 1;
		Coordinate leftCorner;
		Coordinate rightCorner;
	
		//Player 0 case
		if(player.getPlayerNumber()==0){
			//Den at top row
			denRow = 0;
			targetRows = getSequence(denRow + ROW_TRAP_PADDING);
			//Corners at bottom left and right	
			leftCorner = new Coordinate(denRow + ROW_TRAP_PADDING, DEN_COL - COL_TRAP_PADDING);
			rightCorner = new Coordinate(denRow + ROW_TRAP_PADDING, DEN_COL + COL_TRAP_PADDING);


		//Player 1 case 
		} else if(player.getPlayerNumber()==1){
			//Den at bottom row
			denRow = HEIGHT;
			targetRows = getSequence(denRow - ROW_TRAP_PADDING);
			//Corners at top left and right	
			leftCorner = new Coordinate(denRow - ROW_TRAP_PADDING, DEN_COL - COL_TRAP_PADDING);
			rightCorner = new Coordinate(denRow - ROW_TRAP_PADDING, DEN_COL - COL_TRAP_PADDING);
		
		//TODO exception if not player 0 or 1
		} else {
		
		}
	
		//Set a full rectangle of traps
		this.bulkSetBoardSquare(
			() -> new Trap(player), targetCols, targetRows
		)
		//Locate and set den for p0	
		Coordinate denCoordinateP0 = new Coordinate(denRow, DEN_COL);
		this.setBoardSquare(denCoordinateP0, new Den(owner));
		//Remove corners
		this.setBoardSquare(PlainSquare::new, leftCorner);
		this.setBoardSquare(PlainSquare::new, rightCorner);	
	}	
	
	
}

public class Coordinate {
	private int x;
	private int y;
	
/**
 * 	Constructor
 * 	@param row x coordinate 
 * 	@param column y of coordinate
*/
	public Coordinate(row, col) {
		this.x = row;
		this.y = col;	
	}

/**
 * 	Getter for row (x)
 * 	@return row of this coordinate
*/
	public int row() {
		return x;
	}

/**
 * 	Getter for column (y)
 * 	@return column of this coordinate
*/
	public int col() {
		return y;
	}
}
