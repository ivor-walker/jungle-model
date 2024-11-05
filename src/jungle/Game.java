package jungle;
import jungle.pieces.Piece;
import jungle.squares.Square;
import jungle.pieces.Rat;
import jungle.pieces.Tiger;
import jungle.pieces.Lion;

import java.util.Set;
import java.util.List;
import java.util.function.Supplier;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * Game: class to manipulate pieces and board.
*/
public class Game {
	//initialise players	
	private static int NUM_PLAYERS = 2;
	private Player[] players = new Player[NUM_PLAYERS];
/**
 * 	Getter for player.
*/
	public Player getPlayer(int playerNumber) {
		if (playerNumber == 0 || playerNumber == 1) {	
			return players[playerNumber];
		}
		throw new IllegalArgumentException();
	}

	//initialise grids
	//These static variables are public to satisfy tests, ideally private with a getter.	
	public static int HEIGHT = 9;
	public static int WIDTH = 7;
	//pieces: Grid filled with pieces, represents 2d location of all pieces
	private Grid<Piece> pieces = new Grid<Piece>(Piece.class, HEIGHT, WIDTH);
	//pieces: SquareBoard filled with squares, represents 2d location of all squares
	private SquareBoard board;
	//Stores player who last moved (e.g for same-round move checks)
	private int lastMoved = 1;
/**
 *	Constructor for Game.
 *	Creates the board, set players and adds pieces to their starting position.
 *	@param p0 first player
 *	@param p1 second player
*/
	public Game(Player p0, Player p1) {
		//Initialise board with dens
		players[0] = p0;
		players[1] = p1;

		board = new SquareBoard(players, HEIGHT, WIDTH);	
	}
	

	//Special cases: rat (rank 1), tiger (rank 6), lion (rank 7)
	private static int RAT_RANK = 1;
	private static int TIGER_RANK = 6;
	private static int LION_RANK = 7;

	//Set to allow repeated lookups
	private static Set<Integer> SPECIAL_RANKS = Set.of(RAT_RANK, TIGER_RANK, LION_RANK);

/**
 * 	addPiece: get square based on row and col parameters, initialise correct form of piece at square, and set it on board.
 * 	@param row
 * 	@param col
 * 	@param rank rank of new piece, note that ranks listed in SPECIAL_RANKS (i.e 1, 6, 7) create associated special pieces
 * 	@param playerNumber playerNumber associated with new piece's owner
 */	
	public void addPiece(int row, int col, int rank, int playerNumber) {
		Piece piece;
		Square square = this.getSquare(row, col);
		Player owner = players[playerNumber];

		//Initialise piece	
		if (rank == RAT_RANK) {
			//Power 1 piece: rat	
			piece = new Rat(owner, square);
		} else if (rank == TIGER_RANK) {
			//Power 6 piece: tiger
			piece = new Tiger(owner, square);	
		} else if (rank == LION_RANK) {
			//Power 7 piece: lion
			piece = new Lion(owner, square);
		} else {
			//Base case: not special piece
			piece = new Piece(owner, square, rank);
		}
	
		//Set new piece on grid	
		Coordinate targetLocation = new Coordinate(row, col);
		pieces.setGridLocation(targetLocation, piece);

		checkVictory();
	}
/**
 * 	addStartingPieces: adds starting pieces onto Game's board.
*/
	public void addStartingPieces() {
	    // Player 0 pieces
	    this.addPiece(0, 0, 7, 0); 
	    this.addPiece(0, 6, 6, 0); 
	    this.addPiece(1, 5, 2, 0); 
	    this.addPiece(1, 1, 3, 0); 
	    this.addPiece(2, 0, 1, 0); 
	    this.addPiece(2, 4, 4, 0); 
	    this.addPiece(2, 2, 5, 0); 
	    this.addPiece(2, 6, 8, 0); 
	
	    // Player 1 pieces
	    this.addPiece(8, 6, 7, 1);
	    this.addPiece(8, 0, 6, 1);
	    this.addPiece(7, 5, 3, 1);
	    this.addPiece(7, 1, 2, 1);
	    this.addPiece(6, 6, 1, 1);
	    this.addPiece(6, 2, 4, 1);
	    this.addPiece(6, 4, 5, 1);
	    this.addPiece(6, 0, 8, 1);
	}
/**
 * 	getPiece: piece-friendly wrapper around piece's grid getGridLocation getter.
 * 	@param row Row of target coordinate to be passed to getGridLocation 
 * 	@param col Column of target coordinate
 * 	@return Piece at Coordinate(row, col)
*/
	public Piece getPiece(int row, int col) {
		Coordinate targetLocation = new Coordinate(row, col);	
		return pieces.getGridLocation(targetLocation);
	}
/**
 * 	getSquare: square-friendly wrapper around board's squareboard (inherited from grid) getGridLocation getter.
 * 	@param row Row of target coordinate to be passed to getGridLocation 
 * 	@param col Column of target coordinate
 * 	@return Square at Coordinate(row, col)
*/
	public Square getSquare(int row, int col) {
		Coordinate targetLocation = new Coordinate(row, col);	
		return board.getGridLocation(targetLocation);
	}
/**
 * 	move: setter for pieces grid. 
 * 	Ensures proposed move is legal, tries to capture piece at target, updates underlying grid and handles end of turn logic
*/

	public void move(int fromRow, int fromCol, int toRow, int toCol) {
		//Check if correct player is moving	
		Piece fromPiece = this.getPiece(fromRow, fromCol);
		
		Coordinate toLocation = new Coordinate(toRow, toCol);
		//Check if move is legal
		List<Coordinate> legalMoves = getLegalMoves(fromRow, fromCol);
		if (!legalMoves.contains(toLocation)) {
			throw new IllegalMoveException();
		}	

		Square fromSquare = this.getSquare(fromRow, fromCol);	
		
		//Check if capture is occuring	
		Piece toPiece = this.getPiece(toRow, toCol);
		if (toPiece != null) {
			//Capture
			toPiece.beCaptured();	
		}

		//Move piece on board
		pieces.setGridLocation(new Coordinate(fromRow, fromCol), null);
		pieces.setGridLocation(new Coordinate(toRow, toCol), fromPiece);	

		//Move piece
		Square toSquare = this.getSquare(toRow, toCol);
		fromPiece.move(toSquare);

		//End of turn logic: update last moved, check if game is over
		lastMoved = fromPiece.getOwner().getPlayerNumber();
		this.checkVictory();
	}

	private static int MOVE_RANGE = 1;
	private static int STEP_SIZE = 1; 
/**
 *	getLegalMoves: gets all legal moves at a point.
 *	@param row row of point being inspected
 *	@param col
 *	@return list of all legal moves
*/
	public List<Coordinate> getLegalMoves(int row, int col) {
		List<Coordinate> legalMoves = new ArrayList<>();
		
		//Situations in which no legal moves apply:
		//Game is over
		if (this.gameOver) {	
			return legalMoves;	
		}
	
		//No piece at target	
		Piece startingPiece = this.getPiece(row, col);
		if (startingPiece == null) {
			return legalMoves;	
		}
		
		//Incorrect player moving
		int movingPlayerNumber = startingPiece.getOwner().getPlayerNumber();
		if (movingPlayerNumber == lastMoved) {
			return legalMoves;	
		}
		
		Coordinate startingLocation = new Coordinate(row, col);	
		Square startingSquare = this.getSquare(row, col);
		
		//Add all structurally possible moves 		
		List<Coordinate> startingMoves = pieces.getNextNodes(startingLocation, MOVE_RANGE, STEP_SIZE);
		legalMoves.addAll(startingMoves);
	
		//Add leaps	
		List<Coordinate> leaps = new ArrayList<>();	
		if (startingPiece.canLeapHorizontally()) {	
			leaps.addAll(getLeaps(startingLocation, legalMoves, false)); 
		}
		
		if (startingPiece.canLeapVertically()) { 
			leaps.addAll(getLeaps(startingLocation, legalMoves, true));
		}
		legalMoves.addAll(leaps);		
			
		//Remove rule breaking moves
		List<Coordinate> illegalMoves = this.getIllegalMoves(startingPiece, startingSquare, legalMoves);
		legalMoves.removeAll(illegalMoves);
			
		return legalMoves; 
	}
	
		
/**
 * 	Get all possible illegal moves, to be removed in getLegalMoves.
 *	@param startingPiece piece to be moved
 *	@param startingSquare square where piece is to be moved from 
 *	@param legalMoves all possible legal moves
 *	@return all illegal moves
*/	
	private List<Coordinate> getIllegalMoves(Piece startingPiece, Square startingSquare, List<Coordinate> legalMoves) {
		Set<Coordinate> excludedMoves = new HashSet<>();
		for (Coordinate move: legalMoves) {
			Piece targetPiece = getPiece(move.row(), move.col());
			Square targetSquare = this.getSquare(move.row(), move.col());
						
			boolean exclude = false;
			
			//Pieces on the square of the attempted move	
			if (targetPiece != null) {
				boolean isEnemyPiece = (targetPiece.getOwner().getPlayerNumber() == lastMoved);
				//If attempting to move onto an allied piece	
				if (!isEnemyPiece) {
					exclude = true;
				//If attempting to move onto enemy piece and cannot capture
				} else if (isEnemyPiece && !startingPiece.canDefeat(targetPiece)) {
					exclude = true;
				}
			}

			//Swimming
			boolean attemptingSwim = targetSquare.isWater();	
			boolean canSwim = startingPiece.canSwim();
				
			if (attemptingSwim && !canSwim) {
				exclude = true;	
			}

			if (exclude) {
				excludedMoves.add(move);
			}
		}		
		
		return new ArrayList<>(excludedMoves);
	}	
	
/**
 * 	getLeaps: if a water square is passed as a possible legal move, add an appropriate horizontal or vertical leap.
 * 	@param startingLocation origin location of jump, used to calculate relative direction of legal move
 * 	@param legalMoves list of structurally possible moves
 * 	@param isVertical whether jump is horizontal or vertical, used to constrain possible directions
*/
	private List<Coordinate> getLeaps(Coordinate startingLocation, List<Coordinate> legalMoves, boolean isVertical) {
		List<Coordinate> leaps = new ArrayList<>();
		for (Coordinate move: legalMoves) {
			Square targetSquare = this.getSquare(move.row(), move.col());
			Piece targetPiece = this.getPiece(move.row(), move.col()); 
			//If proposed square is water and not blocked by another piece 
			if (targetSquare.isWater() && targetPiece == null) {
				//Get proposed direction	
				Direction direction = determineDirection(startingLocation, move, isVertical);
				//If direction proposed is valid for horizontal/vertical
				if (direction != null) {
					Coordinate leap = getLeaps(startingLocation, direction);
					leaps.add(leap);
				}	
			}
		}
		
		return leaps;			
	}
	
	//All possible directions	
	private enum Direction {
		UP, DOWN, LEFT, RIGHT
	}
/**
 * 	determinesDirection: Determines which member of enum Direction is descriptive of the move between start and end.
 * 	@param start Origin coordinate (i.e startingLocation)
 * 	@param end Destination coordinate (i.e the legal move under consideration)
 * 	@param isVertical Direction to move, passed from getLeaps argument
 * 	@return either appropriate member of Direction or null if no available jump
*/
	private Direction determineDirection(Coordinate start, Coordinate end, boolean isVertical) {
	    if (isVertical) {
	
		if (start.row() + MOVE_RANGE == end.row()) {
		    return Direction.DOWN;

		} else if (start.row() - MOVE_RANGE == end.row()) {
		    return Direction.UP;

		//Null case: the leap being proposed isn't the expected vertical leap, so ignore it.
		} else {
			return null;
		}

	    } else {

		if (start.col() - MOVE_RANGE == end.col()) {
		    	return Direction.LEFT;

		} else if (start.col() + MOVE_RANGE == end.col()) {
			return Direction.RIGHT;

		//Null case: the leap being proposed isn't the expected horizontal leap, so ignore it.
		} else {
			return null;
		}

	    }
	}

/**
 * 	getLeaps: Calculate target coordinates for a leap in the given direction.
 * 	@param start Location to start looking for jump from
 * 	@param direction Direction of leap
 * 	@return Target coordinates for a leap in given direction
*/
	private Coordinate getLeaps(Coordinate start, Direction direction) {
		//Initialise search for first non-water square
		int row = start.row();
		int col = start.col();
	
		switch (direction) {
			case UP:
				//Move up until non-water square found 
            			while (row > 0 && this.getSquare(row - 1, col).isWater()) {
            			    row--;
            			}
            			//Move one more square up to reach the first non-water square
				row--;
            			break;
	
			case DOWN:
				//Move down until non-water square found 
            			while (row < this.HEIGHT - 1 && this.getSquare(row + 1, col).isWater()) {
            			    row++;
            			}
            			//Move one more square down to reach the first non-water square
				row++;
            			break;	
			
			case LEFT:
				//Move left until non-water square found 
            			while (col > 0 && this.getSquare(row, col - 1).isWater()) {
            			    col--;
            			}
            			//Move one more square left to reach the first non-water square
				col--;
            			break;	
			
			case RIGHT:
				//Move right until non-water square found 
            			while (col < this.HEIGHT && this.getSquare(row, col + 1).isWater()) {
            			    col++;
            			}
            			//Move one more square right to reach the first non-water square
				col++;
            			break;

			//Default is impossible to reach
			default NULL:
				return null;	
		}

		return new Coordinate(row, col);
	}
	
	private boolean gameOver = false;
	private Player winner;
/**
 * 	checkVictory: Checks if game has been won and assigns winner if it has.
 * 	Win conditions: player.hasCapturedDen() is true OR player is only remaining player with pieces
*/	
	private void checkVictory() {
		//Get all players with pieces	
		List<Player> alivePlayers = Arrays
			.stream(players)
			.filter(Player::hasPieces)
			.collect(Collectors.toList());
	
		//Get all players who have captured a den	
		List<Player> capturedDens = Arrays
			.stream(players)
			.filter(Player::hasCapturedDen)
			.collect(Collectors.toList());
		

		//End the game and declare remaining player winner
		if (alivePlayers.size() == 1) {
			this.setWinner(alivePlayers.get(0));				
		} else if (capturedDens.size() == 1) {
			this.setWinner(capturedDens.get(0));				
		//or restart game	
		} else if (this.isGameOver()) {
			this.restartGame();			
		}
		//or do nothing	
	}
/**
 * 	Getter and setters regarding victory conditions.
*/
	public boolean isGameOver() {
		return gameOver;
	}

	private void restartGame() {
		this.gameOver = false;
		this.winner = null;
	}

	public Player getWinner() {
		return winner;
	}

	private void setWinner(Player player) {
		this.gameOver = true;
		this.winner = player;	
	}	
}


