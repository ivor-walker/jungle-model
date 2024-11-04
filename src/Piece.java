/**
 * Regular piece
*/

public class Piece {
	private Player owner;
	private Square square;
	private int rank;
	private int strength;
	private boolean active = true;

/**
 * 	Constructor
 * 	@param owner Owner of piece
 * 	@param square Square on which piece sits
 * 	@param rank Starting strength of piece 
*/
	public Piece(Player owner, Square square, int rank) {
		this.owner = owner;
		this.square = square;
		this.rank = rank;	
		this.strength = rank;
	}

/**
 *      Checks if specified player owns the piece
 *      @param player specified player  
*/
        public boolean isOwnedBy(Player player) {
                return owner.equals(player);
        }
	
	public int getStrength() {
		return strength;
	}
	
	public void trap() {
		strength = 0;
	}

	public void untrap() {
		strength = rank;
	}
	
	public int getRank() {
		return rank;
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

/**
 * Special pieces
*/

public class Rat extends Piece {
	//Has rank 1
	private int ratRank = 1;
	public Rat(Player owner, Square square){
		super(owner, square, ratRank);
	}

	//Can swim	
	@Override
	public boolean canSwim() {
		return true;
	}
	
	//Can defeat elephants	
	@Override
	public boolean canDefeat(Piece target) {
		return super.canDefeat(target) || target.getRank()==8;
	}
}

public class Tiger extends Piece {
	//Has rank 6
	private int tigerRank = 6;
	public Tiger(Player owner, Square square) {
		super(owner, square, tigerRank);
	}
	
	//Can leap horizontally only
	@Override
	public boolean canLeapHorizontally() {
		return true;	
	}
}

public class Lion extends Piece {
	//Has rank 7
	public int lionRank = 7;
	public Lion(Player owner, Square square) {
		super(owner, square, lionRank);
	}

	//Can leap horizontally and vertically
	@Override
	public boolean canLeapHorizontally() {
		return true;	
	}

	@Override
	public boolean canLeapVertically() {
		return true;	
	}
}
