package jungle;
import jungle.pieces.Piece;
import jungle.squares.Square;
import jungle.Player;
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
 * Game: Grid representation of pieces
*/
public class Game {
	private static int NUM_PLAYERS = 2;
	private Player[] players = new Player[NUM_PLAYERS];
	public static int HEIGHT = 9;
	public static int WIDTH = 7;	
	private Grid<Piece> pieces = new Grid<Piece>(Piece.class, HEIGHT, WIDTH);
	private SquareBoard board;
	//Redundant due to better definition at Grid, but required else tests will crash
	private int lastMoved = 1;
/**
 *	Constructor for game: creates the board, adds pieces to their starting position
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
		Square square = this.getSquare(row, col);
		Player owner = players[playerNumber];

		//Initialise piece	
		if(rank == RAT_RANK) {
			//Power 1 piece: rat	
			piece = new Rat(owner, square);
		} else if(rank == TIGER_RANK) {
			//Power 6 piece: tiger
			piece = new Tiger(owner, square);	
		} else if(rank == LION_RANK) {
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
	    this.addPiece(6, 6, 8, 1);
	    this.addPiece(6, 2, 5, 1);
	    this.addPiece(6, 4, 4, 1);
	    this.addPiece(6, 0, 1, 1);
	}

	public Piece getPiece(int row, int col) {
		Coordinate targetLocation = new Coordinate(row, col);	
		return pieces.getGridLocation(targetLocation);
	}

	public Square getSquare(int row, int col) {
		Coordinate targetLocation = new Coordinate(row, col);	
		return board.getGridLocation(targetLocation);
	}

	public void move(int fromRow, int fromCol, int toRow, int toCol){
		//Check if correct player is moving	
		Piece fromPiece = this.getPiece(fromRow, fromCol);
		
		Coordinate toLocation = new Coordinate(toRow, toCol);
		//Check if move is legal
		List<Coordinate> legalMoves = getLegalMoves(fromRow, fromCol);
		if(!legalMoves.contains(toLocation)) {
			throw new IllegalMoveException();
		}	

		Square fromSquare = this.getSquare(fromRow, fromCol);	
		
		//Check if capture is occuring	
		Piece toPiece = this.getPiece(toRow, toCol);
		if(toPiece != null){
			//Capture
			toPiece.beCaptured();	
		}

		//Move piece on board
		pieces.setGridLocation(new Coordinate(fromRow, fromCol), null);
		pieces.setGridLocation(new Coordinate(toRow, toCol), fromPiece);	

		//Move piece
		Square toSquare = this.getSquare(toRow, toCol);
		fromPiece.move(toSquare);

		//End of turn logic
		lastMoved = fromPiece.getOwner().getPlayerNumber();
		this.checkVictory();
	}
	
	public List<Coordinate> getLegalMoves(int row, int col){
		List<Coordinate> legalMoves = new ArrayList<>();
		
		//Situations in which no legal moves apply:
		//Game is over
		if(this.gameOver) {	
			return legalMoves;	
		}
	
		//No piece at target	
		Piece startingPiece = this.getPiece(row, col);
		if(startingPiece == null) {
			return legalMoves;	
		}
		
		//Incorrect player moving
		int movingPlayerNumber = startingPiece.getOwner().getPlayerNumber();
		if(movingPlayerNumber == lastMoved){
			return legalMoves;	
		}
		
		Coordinate startingLocation = new Coordinate(row, col);	
		Square startingSquare = this.getSquare(row, col);
		
		//Add all structurally possible moves 		
		List<Coordinate> startingMoves = getNextNodes(startingLocation);
		legalMoves.addAll(startingMoves);
	
		//Add leaps	
		List<Coordinate> leaps = new ArrayList<>();	
		if(startingPiece.canLeapHorizontally()) {	
			leaps.addAll(getLeaps(startingPiece, startingLocation, legalMoves, false)); 
		}
		
		if(startingPiece.canLeapVertically()) { 
			leaps.addAll(getLeaps(startingPiece, startingLocation, legalMoves, true));
		}
		legalMoves.addAll(leaps);		
			
		//Remove rule breaking moves
		List<Coordinate> illegalMoves = this.getIllegalMoves(startingPiece, startingSquare, legalMoves);
		legalMoves.removeAll(illegalMoves);
			
		return legalMoves; 
	}
	
		
/**
 * 	Get all possible illegal moves, to be removed in getLegalMoves
 *	@param startingPiece piece to be moved
 *	@param startingSquare square where piece is to be moved from 
 *	@param legalMoves all possible legal moves
 *	@return all illegal moves
*/	
	private List<Coordinate> getIllegalMoves(Piece startingPiece, Square startingSquare, List<Coordinate> legalMoves) {
		Set<Coordinate> excludedMoves = new HashSet<>();
		for(Coordinate move: legalMoves) {
			Piece targetPiece = getPiece(move.row(), move.col());
			Square targetSquare = this.getSquare(move.row(), move.col());
						
			boolean exclude = false;
			
			//Pieces on the square of the attempted move	
			boolean targetPiecePresent = targetPiece != null;
			if(targetPiecePresent) {
				boolean isEnemyPiece = (targetPiece.getOwner().getPlayerNumber() == lastMoved);

				if(!isEnemyPiece) {
					exclude = true;
				} else if(isEnemyPiece && !startingPiece.canDefeat(targetPiece)) {
					exclude = true;
				}
			}

			//Swimming
			boolean attemptingSwim = targetSquare.isWater();	
			boolean canSwim = startingPiece.canSwim();
				
			if(attemptingSwim && !canSwim) {
				exclude = true;	
			}

			if(exclude) {
				excludedMoves.add(move);
			}
		}		
		
		return new ArrayList<>(excludedMoves);
	}	
	
/**
 *	Gets all legal moves from a given point	
 *	Breadth-first traversal of grid enables custom move ranges and step sizes	
*/
	private int MOVE_RANGE = 1;
	private List<Coordinate> getNextNodes(Coordinate startingNode) {
		int currentBudget = MOVE_RANGE;
		List<Coordinate> affordableNodes = new ArrayList<>();
		//History is a set to avoid duplicates	
		Set<Coordinate> history = new HashSet<>();
		List<Coordinate> queueNodes = new ArrayList<>();
		
		queueNodes.add(startingNode);
		history.add(startingNode);
	
		while(currentBudget > 0 && !queueNodes.isEmpty()){
			List<Coordinate> toBeQueued = new ArrayList<>();	
			//Traverse all nodes in queue	
			for(Coordinate node: queueNodes){
				//Get neighbours	
				List<Coordinate> neighbours = this.traverse(node);
				
				neighbours = neighbours.stream()
					//Remove invalid nodes	
					.filter(Objects::nonNull)
					//Remove already traversed	
					.filter(neighbour -> !history.contains(neighbour))
					.toList();
				
				//Update
				affordableNodes.addAll(neighbours);
				toBeQueued.addAll(neighbours);
				history.addAll(neighbours);
			}

			//Update queue	
			queueNodes = toBeQueued;	
			//Decrease budget	
			currentBudget-=STEP_SIZE;
						}
		//Remove origin node
		affordableNodes.remove(startingNode);
		
		
		return affordableNodes;	
	}

/**
 * 	Helper methods for traversal
*/

/**
 * 	Checks if node is valid 
*/
	private Coordinate checkIfValidNode(int row, int col) {
		try {
			Coordinate targetLocation = new Coordinate(row, col);
			//using SquareBoard's testBounds method, inherited from Grid	
			board.testBounds(targetLocation);
			return targetLocation;	
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
/**
 * 	Lists nearest neighbours of node
*/	
	private int STEP_SIZE = 1;
	private List<Coordinate> traverse(Coordinate node) {
		return Arrays.asList(
			checkIfValidNode(node.row() - STEP_SIZE, node.col()),
			checkIfValidNode(node.row() + STEP_SIZE, node.col()),
			checkIfValidNode(node.row(), node.col() - STEP_SIZE),
			checkIfValidNode(node.row(), node.col() + STEP_SIZE)
		);

	}
/**
 * Leaps: if a water square is passed as a possible legal move, add an appropriate horizontal or vertical leap
*/
	private List<Coordinate> getLeaps(Piece startingPiece, Coordinate startingLocation, List<Coordinate>legalMoves, boolean isVertical) {
		List<Coordinate> leaps = new ArrayList<>();
		for(Coordinate move: legalMoves) {
			Square targetSquare = this.getSquare(move.row(), move.col());
			Piece targetPiece = this.getPiece(move.row(), move.col()); 
			//If proposed square is water and not blocked by another piece 
			if(targetSquare.isWater() && targetPiece == null) {
				//Get proposed direction	
				Direction direction = determineDirection(startingLocation, move, isVertical);
				//If direction proposed is invalid for horizontal/vertical
				if(direction != null){
					Coordinate leap = getLeaps(startingLocation, direction);
					leaps.add(leap);
				}	
			}
		}
		
		return leaps;			
	}
	
	private enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private Direction determineDirection(Coordinate start, Coordinate end, boolean isVertical) {
	    if (isVertical) {
	
		if (start.row() + MOVE_RANGE == end.row()) {
		    return Direction.DOWN;

		} else if (start.row() - MOVE_RANGE == end.row()){
		    return Direction.UP;

		} else {
			return null;
		}

	    } else {

		if (start.col() - MOVE_RANGE == end.col()) {
		    	return Direction.LEFT;

		} else if(start.col() + MOVE_RANGE == end.col()){
			return Direction.RIGHT;

		} else {
			return null;
		}

	    }
	}


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
		}

		return new Coordinate(row, col);
	}

	public Player getPlayer(int playerNumber) {
		if(playerNumber == 0 || playerNumber == 1) {	
			return players[playerNumber];
		}
		throw new IllegalArgumentException();
	}
	
	private boolean gameOver = false;
	private Player winner;

/**
 * 	Checks if game has been won and assigns winner if it has
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
		if(alivePlayers.size() == 1) {
			this.setWinner(alivePlayers.get(0));				
		} else if(capturedDens.size() == 1) {
			this.setWinner(capturedDens.get(0));				
		//or restart game	
		} else if(this.isGameOver() == true) {
			this.restartGame();			
		}
		//or do nothing	
	}
/**
 * 	Getter and setters regarding victory conditions
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


