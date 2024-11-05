package jungle.pieces;
import jungle.Player;
import jungle.squares.Square;
/**
 * Regular piece.
*/
public class Piece {
	private Player owner;
	private Square square;
	private int rank;
	private int strength;
	private boolean active = true;

/**
 * 	Constructor.
 * 	@param owner Owner of piece
 * 	@param square Square on which piece sits
 * 	@param rank Starting strength of piece 
*/
	public Piece(Player owner, Square square, int rank) {
		this.owner = owner;
		this.square = square;
		this.rank = rank;	
		this.strength = rank;
		
		this.checkTrap(square);
		this.owner.gainOnePiece();
	}

/**
 *      Checks if specified player owns the piece.
 *      @param player specified player  
*/
        public boolean isOwnedBy(Player player) {
                return owner.equals(player);
        }
/**
 * 	Direct getter for owner.
*/	
	public Player getOwner() {
		return owner;
	}
	
	public int getStrength() {
		return strength;
	}
/**
 * 	Trap behaviour: reduce piece strength to zero if trapped, and return it if untrapped.
*/	
	public void trap() {
		this.strength = 0;
	}

	public void untrap() {
		this.strength = rank;
	}
/**
 * 	Original rank of piece.
 * 	As strength can be modified during game (i.e during trap), store of original argument is kept.
*/	
	public int getRank() {
		return rank;
	}
/**
 * 	Getters and setters of default behaviour for pieces.
 * 	Default pieces cannot swim or leap in any direction
*/
	public boolean canSwim() {
		return false;
	}

	public boolean canLeapHorizontally() {
		return false;
	}

	public boolean canLeapVertically() {
		return false;
	}

/**
 * 	Setter for square: move this piece, check for traps and handle den capturing.
 * 	@param toSquare new Square position for this piece
*/	
	public void move(Square toSquare) { 
		Square fromSquare = this.square;
		
		square = toSquare;
	
		this.checkTrap(fromSquare, toSquare);	
		
		boolean movingToDen = toSquare.isDen();
		if (movingToDen) {
			Player denOwner = toSquare.getOwner();
			//Can't capture player's own den	
			if (!this.isOwnedBy(denOwner)) {
				this.owner.captureDen();
			}
		}
	}
/**
 * 	checkTrap: Check if piece will be trapped.
 * 	@param toSquare Proposed square to move to 
*/	
	private void checkTrap(Square toSquare) {
		//Check if target is enemy's trap
		if (toSquare.isTrap() && (toSquare.getOwner().getPlayerNumber() != this.getOwner().getPlayerNumber())) {
			this.trap();
		}
	}

/**
 * 	checkTrap: Check if proposed move will trap or untrap piece.
 * 	@param fromSquare origin location, used for untrapping
 * 	@param toSquare Proposed square to move to, passed to single parameter version of this method
*/	
	private void checkTrap(Square fromSquare, Square toSquare) {
		checkTrap(toSquare);	
		boolean currentlyTrapped = fromSquare.isTrap();
		boolean movingToTrap = toSquare.isTrap();
		if (currentlyTrapped && !movingToTrap) {
			this.untrap();
		}
	}
/**
 * 	canDefeat: Checks if this piece can defeat target piece.
 * 	A piece can capture a piece of its strength or lower
*/
	public boolean canDefeat(Piece target) {
		return target.strength <= this.strength;
	}

/**
 * 	Setter for active: capture this piece.
*/
	public void beCaptured() {
		active = false;
		owner.loseOnePiece();		
	}	
}






