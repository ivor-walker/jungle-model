/**
 * Grid: class for storing 2d representations
*/

public class Grid {
	private grid;
	
/**
 * 	Constructor for grid: creates the board
*/
	public Grid(height, width) {
		grid = [height][width];	
	}

/**
 * 	Individual coordinate setter for grid
*/	
	private void setGridSquare(Coordinate targetLocation, Object targetElement) {
		grid[targetLocation.col(), targetLocation.row()] = targetElement;	
	}

/**
 * 	Bulk coordinate setter for board
 * 	@param squareSupplier factory for generating the requested object 
 * 	@param targetRows 
 * 	@param targetCols 
*/
	
	private void bulkSetGridSquare(Supplier<? extends Square> elementSupplier, int[] targetRows, int[] targetCols) {
		for (row: targetRows) {
			for (col: targetCols) {
				Coordinate targetLocation = new Coordinate(row, col);
				Object newElement = elementSupplier.get();
				this.setGridSquare(targetLocation, newElement);	
			}
		}	
	}
}

/**
 * Coordinate: class for expression location in a grid
*/

public class Coordinate {
	private int x;
	private int y;
	
/**
 * 	Constructor
 * 	@param row x coordinate 
 * 	@param column y of coordinate
*/
	public Coordinate(row, col) {
		this.x = row;
		this.y = col;	
	}

/**
 * 	Getter for row (x)
 * 	@return row of this coordinate
*/
	public int row() {
		return x;
	}

/**
 * 	Getter for column (y)
 * 	@return column of this coordinate
*/
	public int col() {
		return y;
	}
}
