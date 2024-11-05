package jungle;

/**
 * Player class: represents players of a given Game.
*/
public class Player {
	private String name;
	private int playerNumber;
/**
 * 	Constructor method: sets name and playerNumber provided by Game.
*/
	public Player(String name, int playerNumber) {
		this.name = name;
		this.playerNumber = playerNumber;
	}

/**
 * 	Getter method for a player's information.
*/
	public String getName() {
		return this.name;	
	}
	
	public int getPlayerNumber() {
		return this.playerNumber;	
	}


	private boolean denCaptured = false;
/**
 * 	Setter and getter for denCaptured.	
*/
	public void captureDen() {
		this.denCaptured = true;	
	}
	
	public boolean hasCapturedDen() {
		return this.denCaptured;
	}
	
	private	int pieceCount = 0;
/**
 *	Check if player has pieces.
*/
	public boolean hasPieces() {
		return pieceCount > 0;	
	}
/**
 *	Adder and subtractor for pieceCount.
*/ 
	public void gainOnePiece() {
		pieceCount++;
	}

	public void loseOnePiece() {
		pieceCount--;
	}
}
