package jungle;import java.util.stream.IntStream;
import java.util.function.Supplier;


/**
 * Grid: generic class for storing 2d representations
*/
public class Grid<T> {
	public int HEIGHT = 9;
	public int WIDTH = 7;
	protected int ALL_ROWS[] = getSequence(WIDTH);
	protected int ALL_COLS[] = getSequence(HEIGHT);
	protected T[][] grid; 
	
	public Grid() {
		this.grid = (T[][]) new Object[HEIGHT][WIDTH];
	}
/**
 * Getter of element at coordinate in grid
*/
	protected T getGridLocation(Coordinate targetLocation){
		return grid[targetLocation.row()][targetLocation.col()];
	}

/**
 *	Individual coordinate setter for grid
*/	
	protected void setGridLocation(Coordinate targetLocation, T targetElement) {
		grid[targetLocation.row()][targetLocation.col()] = targetElement;	
	}

/**
 *	Wrapper for individual coordinate setter to enable large scale setting of grid	
 *	@param squareSupplier factory for generating the requested object 
 *	@param targetRows 
 *	@param targetCols 
*/
	protected void setGridLocation(int[] targetRows, int[] targetCols, Supplier<T> elementSupplier) {
		for (int row: targetRows) {
			for (int col: targetCols) {
				Coordinate targetLocation = new Coordinate(row, col);
				T targetElement = elementSupplier.get();
				this.setGridLocation(targetLocation, targetElement);	
			}
		}	
	}
/**
 *	Helper methods 
*/	
	protected int[] getSequence(int n) {
		return IntStream
			.rangeClosed(0, n)
			.toArray();
	}

	protected int[] getSequence(int start, int n) {
		return IntStream
			.rangeClosed(start, n)
			.toArray();
	}
}
