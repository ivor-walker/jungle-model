package jungle.squares;public class WaterSquare extends Square {
	public WaterSquare() {
		//Passing null as WaterSquares cannot be owned by a Player
		super(null);
	}

	@Override
	public boolean isWater() {
		return true;
	}	
}
