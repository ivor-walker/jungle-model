/**
 * SquareBoard: Grid representation of all the squares
*/
public class SquareBoard extends Grid<Square> {
/**
 *	Initialises board by setting all squares to plain
 *	Then adding the three types of special squares
*/
	public int[] WATER_ROWS = {3, 4, 5};
	public int[] WATER_COLS = {1, 2, 4, 5};
	
	public SquareBoard(Player[] players) {	
		//Set the whole board to plainSquares (ALL_ROWS inherited from grid)
		this.setGridLocation(ALL_ROWS, ALL_COLS, PlainSquare::new);
		//Set water squares
		this.setGridLocation(WATER_ROWS, WATER_COLS, WaterSquare::new);

		//Set traps and dens for all players
		for(Player player: players) {
			this.addTrapsAndDen(player);
		}
	}

	@Override
	protected Square getGridLocation(Coordinate targetLocation) {
		return grid[targetLocation.row()][targetLocation.col()];
	}
/**
 *	Set den and traps for a given player	
 *	@param player 
*/
	public int DEN_COL = 3;
	private int ROW_TRAP_PADDING = 1;
	private int COL_TRAP_PADDING = 1;
	private void addTrapsAndDen(Player player) {
		//Get column location of home	
		int[] targetCols = getSequence(DEN_COL - ROW_TRAP_PADDING, DEN_COL + COL_TRAP_PADDING);
		int[] targetRows;
	
		//Get player number specific things	
		int denRow;
		Coordinate leftCorner;
		Coordinate rightCorner;
	
		//Player 0 case
		if(player.getPlayerNumber() == 0){
			//Den at top row
			denRow = 0;
			targetRows = getSequence(denRow + ROW_TRAP_PADDING);
			//Corners at bottom left and right	
			leftCorner = new Coordinate(denRow + ROW_TRAP_PADDING, DEN_COL - COL_TRAP_PADDING);
			rightCorner = new Coordinate(denRow + ROW_TRAP_PADDING, DEN_COL + COL_TRAP_PADDING);


		//Player 1 case 
		} else {
			//Den at bottom row
			denRow = HEIGHT;
			targetRows = getSequence(denRow - ROW_TRAP_PADDING, denRow);
			//Corners at top left and right	
			leftCorner = new Coordinate(denRow - ROW_TRAP_PADDING, DEN_COL - COL_TRAP_PADDING);
			rightCorner = new Coordinate(denRow - ROW_TRAP_PADDING, DEN_COL + COL_TRAP_PADDING);
		}
	
		//Set a full rectangle of traps
		this.setGridLocation(targetRows, targetCols, () -> new Trap(player));
		//Locate and set den	
		Coordinate denCoordinate = new Coordinate(denRow, DEN_COL);
		this.setGridLocation(denCoordinate, new Den(player));
		//Remove corners
		this.setGridLocation(leftCorner, new PlainSquare());
		this.setGridLocation(rightCorner, new PlainSquare());	
	}
}
