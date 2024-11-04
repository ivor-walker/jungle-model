public class Player {
/**
 * 	Constructor method: sets name and playerNumber provided by Game
*/
	public Player(String name, int playerNumber) {
		this.name = name;
		this.playerNumber = playerNumber;
	}

	private String name;
/**
 * 	Getter method for a player's name
*/
	public String getName() {
		return this.name;	
	}

	private int playerNumber;
/**
 * 	Getter method for a player's number
*/
	public int getPlayerNumber() {
		return this.playerNumber;	
	}

/**
 * 	Setter method for denCaptured	
*/
	public void captureDen() {
		this.denCaptured = true;	
	}

	private boolean denCaptured = false;
/**
 *	Getter method for denCaptured
*/
	public boolean hasCapturedDen() {
		return this.denCaptured;
	}
	
	private	int pieceCount = 0;
/**
 *	Check if player has pieces
 *	Note that pieces themselves are owned by Game, so this returns int count only	
*/
	public boolean hasPieces() {
		return pieceCount > 0;	
	}

/**
 *	Adder for pieceCount
 *	Note that pieces themselves are owned by Game, so this adds int count only	
*/ 
	public void gainOnePiece() {
		pieceCount++;
	}

/**
 *	Subtractor for pieceCount
 *	Note that pieces themselves are owned by Game, so this subtracts int count only	
*/ 
	public void loseOnePiece() {
		pieceCount--;
	}
}
