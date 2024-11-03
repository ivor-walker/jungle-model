public abstract class Square {
	private Player owner;

	/*
 * 	Constructor: sets owner
 * 	*/
	public Square(Player owner) {
		this.owner = owner;
	}

	/*
 * 	Checks if
 * 	a) square is of type that can be owned (i.e not PlainSquare or WaterSquare)
 * 	b) specified player owns the square
 * 	@param specified player
 * 	*/
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

public class WaterSquare extends Square {
	
	/*
 * 	Constructor, passing null as WaterSquares cannot be owned
 * 	*/	
	public WaterSquare() {
		super(null);
	}

	@Override
	public boolean isWater() {
		return true;
	}	
}

public class Den extends Square {
	public Den(owner) {
		super(owner);
	}

	@Override
	public boolean isDen() {
		return true;
	}	
}

public class Trap extends Square {
        public Trap(owner) {
                super(owner);
        }

        @Override
        public boolean isTrap() {
		return true;
	}
}

public class PlainSquare extends Square {
	/*
 * 	Constructor, passing null as WaterSquares cannot be owned
 * 	*/
	public PlainSquare() {
		super(null);
	}
}
~                                                                                                                     
~                                                                                                                     
~                                                                                                                     
~                       
