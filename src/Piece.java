public class Piece {
	private Player owner;
	private Square square;
	private int strength;
	private boolean active = true;

/**
 * 	Constructor
 * 	@param owner Owner of piece
 * 	@param square Square on which piece sits
 * 	@param rank Starting strength of piece 
*/
	public Piece(Player owner, Square square, int strength) {
		this.owner = owner;
		this.square = square;
		this.strength = strength;
	}

/**
 *      Checks if specified player owns the piece
 *      @param player specified player  
*/
        public boolean isOwnedBy(Player player) {
                return owner.equals(player);
        }

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
 * 	Setter for square: move this piece
 * 	@param toSquare new Square position for this piece
*/	
	public void move(Square toSquare) { 
		square = toSquare;		
	}

/**
 * 	Checks if this piece can defeat target piece
*/
	public boolean canDefeat(Piece target) {
		//'a piece can capture a piece of its strength or lower'
		return target.strength <= strength;
	}
/**
 * 	Setter for active: capture this piece
*/
	public void beCaptured() {
		active = false;
		owner.loseOnePiece();		
	}	
}
