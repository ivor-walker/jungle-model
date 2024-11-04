package jungle.squares;
import jungle.Player;

public abstract class Square {
	private Player owner;

/**
 * 	Constructor: sets owner
*/
	public Square(Player owner) {
		this.owner = owner;
	}

/**
 * 	Checks if
 * 	a) square is of type that can be owned (i.e not PlainSquare or WaterSquare)
 * 	b) specified player owns the square
 * 	@param specified player
*/
	public boolean isOwnedBy(Player player) {
		//Owner for unownable squares is null 
		return owner != null && owner.equals(player);	
	}

	public boolean isWater() {
		return false;
	}

	public boolean isDen() {
		return false;
	}
	
	public boolean isTrap() {
		return false;
	}
}








