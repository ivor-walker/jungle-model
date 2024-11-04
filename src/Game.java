/**
 * Coordinate: class for expression location in a grid
*/
public class Coordinate {
	private int x;
	private int y;
	
/**
 *	Constructor
 *	@param row x coordinate 
 *	@param column y of coordinate
*/
	public Coordinate(int row, int col) {
		if(row < 0 || col < 0) {
			throw IndexOutOfBoundsException;	
		}
		this.x = row;
		this.y = col;	
	}


/**
 *	Getter for row (x)
 *	@return row of this coordinate
*/
	public int row() {
		return x;
	}

/**
 *	Getter for column (y)
 *	@return column of this coordinate
*/
	public int col() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj) {
		Coordinate objCoordinate = (Coordinate) obj;
		return this.row() == obj.row() && this.col() == obj.col();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(row, col);
	}
}

/**
 * Grid: class for storing 2d representations
*/
public abstract class Grid {
	public int HEIGHT = 9;
	public int WIDTH = 7;
	private int ALL_ROWS[] = getSequence(WIDTH);
	private int ALL_COLS[] = getSequence(HEIGHT);
	private Object[][] grid = Object[HEIGHT][WIDTH];
	
/**
 * Getter of element at coordinate in grid
*/
	private Object getGridLocation(Coordinate targetLocation){
		return grid[targetlocation.row()][targetLocation.col()];
	}

/**
 *	Individual coordinate setter for grid
*/	
	private void setGridLocation(Coordinate targetLocation, Object targetElement) {
		grid[targetLocation.row()][targetLocation.col()] = targetElement;	
	}

/**
 *	Wrapper for individual coordinate setter to enable large scale setting of grid	
 *	@param squareSupplier factory for generating the requested object 
 *	@param targetRows 
 *	@param targetCols 
*/
	private void setGridLocation(int[] targetRows, int[] targetCols, Supplier<? extends Square> elementSupplier) {
		for (int row: targetRows) {
			for (int col: targetCols) {
				Coordinate targetLocation = new Coordinate(row, col);
				Object targetElement = elementSupplier.get();
				this.setGridSquare(targetLocation, targetElement);	
			}
		}	
	}
/**
 *	Helper methods 
*/	
	private int[] getSequence(int n) {
		return java.util.stream.IntStream
			.rangeClosed(0, n)
			.toArray();
	}

	private int[] getSequence(int start, int n) {
		return IntStream
			.rangeClosed(start, n)
			.toArray();
	}
}

/**
 * SquareBoard: Grid representation of all the squares
*/
public abstract class SquareBoard extends Grid {

/**
 *	Initialises board by setting all squares to plain
 *	Then adding the three types of special squares
*/
	public int[] WATER_ROWS = {3, 4, 5};
	public int[] WATER_COLS = {1, 2, 4, 5};
	public int DEN_COL = 3;
	
	public SquareBoard(Player[] players) {	
		//Set the whole board to plainSquares
		this.setGridLocation(ALL_ROWS, ALL_COLS, PlainSquare::new);
		//Set water squares
		this.setGridLocation(WATER_ROWS, WATER_COLS, WaterSquare::new);

		//Set traps and dens for all players
		for(Player player: players) {
			this.addTrapsAndDen(player);
		}
	}
/**
 *	Set den and traps for a given player	
 *	@param player 
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
		
		} else {
			throw IllegalArgumentException();	
		}
	
		//Set a full rectangle of traps
		this.setGridLocation(
			() -> new Trap(player), targetCols, targetRows
		);
		//Locate and set den	
		Coordinate denCoordinate = new Coordinate(denRow, DEN_COL);
		this.setGridLocation(denCoordinateP0, new Den(player));
		//Remove corners
		this.setGridLocation(leftCorner, PlainSquare::new);
		this.setGridLocation(rightCorner, PlainSquare::new);	
	}
}

/**
 * Game: Grid representation of pieces
 * Inheritance chain: able to manipulate SquareBoard
*/
public class Game extends SquareBoard {
	private static int NUM_PLAYERS = 2;
	private Player[] players = new Player[NUM_PLAYERS];
	private Grid pieces = new Grid(HEIGHT, WIDTH);
	private int lastMoved = 1;
	
/**
 *	Constructor for game: creates the board, adds pieces to their starting position
*/
	public Game(Player p0, Player p1) {
		players[0] = p0;
		players[1] = p1;	
		//Initialise board with dens
		super(players);
	
		this.addStartingPieces();		
	}
	
	//Special cases: rat (rank 1), tiger (rank 6), lion (rank 7)
	private static int RAT_RANK = 1;
	private static int TIGER_RANK = 6;
	private static int LION_RANK = 7;

	//Set to allow repeated lookups
	private static Set<Integer> SPECIAL_RANKS = Set.of(RAT_RANK, TIGER_RANK, LION_RANK);

/**
 * 	addPiece:
 * 		-get square based on row and col parameters
 * 		-initialise correct form of piece at square
 * 		-set it on board
 * 	@param row
 * 	@param col
 * 	@param rank
 * 	@param playerNumber
 */	
	public void addPiece(int row, int col, int rank, int playerNumber) {
		Piece piece;
		//Find square at location
		targetLocation = new Coordinate(row, col);	
		Square square = board.getGridLocation(targetLocation);
		
		if(!SPECIAL_RANKS.contains(rank)){
			//Base case: not special piece
			piece = new Piece(owner, square, rank);
		} else if(rank == RAT_RANK) {
			//Power 1 piece: rat	
			piece = new Rat(owner, square);
		} else if(rank == TIGER_RANK) {
			//Power 6 piece: tiger
			piece = new Tiger(owner, square);	
		} else if(rank == LION_RANK) {
			//Power 7 piece: lion
			piece = new Lion(owner, square);
		}

		pieces.setGridLocation(targetLocation, piece);
		players[playerNumber].gainOnePiece();
		checkTraps(piece, square);
	}

	public void addStartingPieces() {
	    // Player 0 pieces
	    this.addPiece(0, 0, 7, 0); 
	    this.addPiece(0, 6, 6, 0); 
	    this.addPiece(1, 5, 2, 0); 
	    this.addPiece(1, 1, 3, 0); 
	    this.addPiece(2, 0, 1, 0); 
	    this.addPiece(2, 4, 5, 0); 
	    this.addPiece(2, 2, 4, 0); 
	    this.addPiece(2, 6, 8, 0); 
	
	    // Player 1 pieces
	    this.addPiece(8, 6, 7, 1);
	    this.addPiece(8, 0, 6, 1);
	    this.addPiece(7, 5, 3, 1);
	    this.addPiece(7, 1, 2, 1);
	    this.addPiece(6, 6, 8, 1);
	    this.addPiece(6, 2, 5, 1);
	    this.addPiece(6, 4, 4, 1);
	    this.addPiece(6, 0, 1, 1);
	}

	public Piece getPiece(int row, int col) {
		Coordinate targetLocation = new Coordinate(row, col);	
		return pieces.getGridLocation(targetLocation);
	}

	public void move(int fromRow, int fromCol, int toRow, int toCol){
		//Check if correct player is moving	
		Piece fromPiece = this.getPiece(fromRow, fromCol);
		Player movingPlayerNumber = getOwner(fromPiece).playerNumber;
		if(movingPlayerNumber != lastMoved){
			throw IllegalMoveException();
		}

		//Check if move is legal
		List<Coordinate> legalMoves = getLegalMoves(fromRow, fromCol);
		Coordinate toLocation = Coordinate(toRow, toCol);
		if(!toLocation.contains(legalMoves)) {
			throw IllegalMoveException();
		}	

		Coordinate fromLocation = Coordinate(fromRow, fromCol);
		
		//Check if capture is occuring	
		toPiece = this.getPiece(toRow, toCol);
		if(toPiece != null){
			//Capture
			toPiece.beCaptured();	
		}

		//Move piece on board	
		pieces.setGridLocation(fromLocation, null);
		pieces.setGridLocation(toLocation, fromPiece);	

		//Move piece's square
		Square toSquare = board.getGridLocation(toLocation);
		fromPiece.move(toSquare);

		//End of turn logic
		lastMoved = movingPlayerNumber;
		checkTraps(fromPiece, toSquare, fromSquare);
		this.checkVictory(toSquare);
	}
	
	
/**
 *	Helper method to check if piece needs to be trapped
 *	@param piece evaluated piece
 *	@param toSquare piece square is currently sitting on 
*/ 
	private void checkTraps(Piece piece, Square toSquare) {
		if(toSquare.isTrap()) {
			piece.trap();	
		}
	}	
/**
 *	Extention of checkTraps to see if a given move will trap or untrap a piece
 *	@param piece
 *	@param toSquare future square
 *	@param fromSquare present square 
*/	
	private void checkTraps(Piece piece, Square toSquare, Square fromSquare) {
		checkTraps(piece, toSquare);	

		if(fromSquare.isTrap()) {
			fromPiece.untrap();	
		}
	}

	public List<Coordinate> getLegalMoves(int row, int col){
		//If game is over, no legal moves	
		if(gameOver) {	
			return Collections.emptyList();	
		}

		Coordinate startingLocation = new Coordinate(row, col);	
		//Guaranteed all structurally possible moves (i.e not out of bounds)	
		List<Coordinate> legalMoves = getNextNodes(startingLocation);
		Piece startingPiece = getPiece(row, col);
		Square startingSquare = board.getGridLocation(startingLocation);
		
		//Add leaps	
		if(startingPiece.canLeapHorizontally()) {	
			legalMoves.addAll(getLeaps(startingPiece, startingLocation, legalMoves, false)); 
		}
		
		if(startingPiece.canLeapVertically()) { 
			legalMoves.addAll(getLeaps(startingPiece, startingLocation, legalMoves, true)); 
		}

		//Remove rule breaking moves	
		legalMoves = filterMoves(startingPiece, startingSquare, legalMoves);
		
		return legalMoves; 
	}

	

	private List<Coordinate> getLeaps(Piece startingPiece, Coordinate startingLocation, List<Coordinate>legalMoves) {
		List<Coordinate> leaps = new ArrayList<>();
		for(Coordinate move: legalMoves) {
			Square targetSquare = this.getSquare(move.row(), move.col());
 
			//If proposed square is water 
			if(targetSquare.isWater()) {
				//Get proposed direction & row, and calculate associated leap	
				Direction direction = determineDirection(startingLocation, move, isVertical);
				Coordinate leap = calculatedLeap(startingLocation, direction);
				leaps.add(leap);	
			}
		}

		return horizontalLeaps;			
	}
	
	private enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Direction determineDirection(Coordinate start, Coordinate end, boolean isVertical) {
	    if (isVertical) {
		if (start.row() + MOVE_RANGE == end.row()) {
		    return Direction.DOWN;
		} else if (start.row() - MOVE_RANGE == end.row()) {
		    return Direction.UP;
		}
	    } else {
		if (start.col() - MOVE_RANGE == end.col()) {
		    return Direction.LEFT;
		} else if (start.col() + MOVE_RANGE == end.col()) {
		    return Direction.RIGHT;
		}
	    }
	}

	private Coordinate calculateLeap(Coordinate start, Direction direction) {
		//Initialise search for first non-water square
		int row = start.row();
		int col = start.col();
	
		switch (direction) {
			case UP:
				//Move up until non-water square found 
            			while (row > 0 && getSquare(row - 1, col).isWater()) {
            			    row--;
            			}
            			//Move one more square up to reach the first non-water square
				row--;
            			break;
	
			case DOWN:
				//Move down until non-water square found 
            			while (row > 0 && getSquare(row + 1, col).isWater()) {
            			    row++;
            			}
            			//Move one more square down to reach the first non-water square
				row++;
            			break;	
			
			case LEFT:
				//Move left until non-water square found 
            			while (row > 0 && getSquare(row, col - 1).isWater()) {
            			    row--;
            			}
            			//Move one more square left to reach the first non-water square
				row--;
            			break;	
			
			case RIGHT:
				//Move right until non-water square found 
            			while (row > 0 && getSquare(row, col + 1).isWater()) {
            			    row++;
            			}
            			//Move one more square right to reach the first non-water square
				row++;
            			break;
		}

		return new Coordinate(row, col);
	}

/**
 *	Excludes illegal moves from legalMoves
 *	@param startingPiece piece to be moved
 *	@param startingSquare square where piece is to be moved from 
 *	@param legalMoves all possible legal moves
 *	@return filtered list of legalMoves
*/	
	private List<Coordinate> filterMoves(Piece startingPiece, Square startingSquare, List<Coordinate> legalMoves) {
		List<Coordinate> excludedMoves;
		for(Coordinate move: legalMoves) {
			Piece targetPiece = getPiece(move.row(), move.col());
			Square targetSquare = getSquare(move.row(), move.col());
						
			boolean exclude = false;
			while(exclude == false){	
				//Pieces on the square of the attempted move	
				boolean targetPiecePresent = targetPiece != null;
				boolean targetPieceEnemy = getOwner(targetPiece).getPlayerNumber == lastMoved;
				boolean targetPieceDefeatable = startingPiece.canDefeat(targetPiece);
				exclude = strongerPieces(targetPiecePresent, targetPieceEnemy, targetPieceDefeatable);
				exclude = alliedPieces(targetPiecePresent, !targetPieceEnemy, targetPieceDefeatable);
				
				//Swimming
				boolean attemptingSwim = targetSquare.isWater();	
				boolean canSwim = startingPiece.canSwim();
				exclude = attemptingSwim && !canSwim;
				break;
			}
			
			if(exclude == true) {
				excludedMoves.add(move);
			}
		}		
		
		legalMoves.removeAll(excludedMoves);
		return excludedMoves;
	}	

	private boolean strongerPieces(boolean targetPiecePresent, boolean targetPieceEnemy, boolean targetPieceDefeatable) {
		return targetPiecePresent && (targetPieceEnemy && !targetPieceDefeatable);
	}
	
	private boolean alliedPieces(boolean targetPiecePresent, boolean targetPieceAllied) { 
		return targetPiecePresent && targetPieceAllied;
	}
	
/**
 *	Gets all legal moves from a given point	
 *	This is essentially a graph traversal problem
*/
	private int MOVE_RANGE = 1;
	private int STEP_SIZE = 1;
	private List<Coordinate> getNextNodes(Coordinate startingNode) {
		int currentBudget = MOVE_RANGE;
		List<Coordinate> affordableNodes = new ArrayList<>();
		//History is a set to avoid duplicates	
		Set<Coordinate> history = new HashSet<>();
	
		List<Coordinate> queueNodes = new ArrayList<>();
		queueNodes.add(startingNode);
	
		while(currentBudget > 0){
			List<Coordinate> toBeQueued = new ArrayList<>();	
			//Traverse all nodes in queue	
			for(Coordinate node: queueNodes){
				//Add queue to affordable nodes
				affordableNodes.add(node);

				//Find all possible neighbours	
				List<Coordinate> neighbours = Arrays.asList(
					checkIfValidNode(node.row() - STEP_SIZE, node.col()),
					checkIfValidNode(node.row() + STEP_SIZE, node.col()),
					checkIfValidNode(node.row(), node.col() - STEP_SIZE),
					checkIfValidNode(node.row(), node.col() + STEP_SIZE)
				);
				
				//Process each neighbour
				for(Coordinate neighbour: neighbours) {
					//If neighbour is valid and hasn't been traversed before
					if(neighbour!=null && !history.contains(neighbour)){
						//Add to be traversed
						toBeQueued.add(neighbour);
						//Ensure node can't be retraversed
						history.add(neighbour);
					}
				}
			}
			//Update queue	
			queueNodes = toBeQueued;	
			//Decrease budget	
			currentBudget-=STEP_SIZE;
		}

		return affordableNodes;	
	}

	private Coordinate checkIfValidNode(int row, int col) {
		try {
			return new Coordinate(row, col);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	private Player getOwner(Piece piece) {
		return players
			.stream(players)
			.filter(player -> piece.isOwnedBy(player))
			.findFirst();
	}

	private boolean gameOver = false;
	private Player winner;
	
	private void checkVictory(Square winningTarget) {
		List<Player> alivePlayers = players.stream()
			.filter(Player::hasPieces)
			.collect(Collectors.toList());

		if(winningTarget.isDen() || alivePlayer.size() == 1) {
			this.endGame();
			this.setWinner(alivePlayers(0));				
		}	
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	private boolean endGame() {
		this.gameOver = true;
	}

	public Player getWinner() {
		return winner;
	}

	private void setWinner(Player player) {
		this.winner = player;	
	}	
}

public class IllegalMoveException extends RuntimeException {

}
