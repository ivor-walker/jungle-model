package jungle;import java.util.stream.IntStream;
import java.util.function.Supplier;
import java.lang.reflect.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;
import java.util.Arrays;

/**
 * Grid: generic class for storing 2d representations
*/
public class Grid<T> {
	private T[][] grid;
	private int height;
	private int width; 
	private int allCols[];
	private int allRows[];

	public Grid(Class<T> type, int height, int width) {
		this.grid = (T[][]) Array.newInstance(type, height, width);
		this.height = height;
		this.width = width;
		this.allRows = getSequence(height);
		this.allCols = getSequence(width);
	}

/**Getters
 *Protected as inheriting classes (i.e SquareBoard) need these
*/
	protected int getHeight() {
		return this.height;
	}

	protected int getWidth() {
		return this.width;
	}

	protected int[] getAllRows() {
		return this.allRows;
	}

	protected int[] getAllCols() {
		return this.allCols;
	}
/**
 * Tests if given coordinate is legal for this grid
*/
	protected void testBounds(Coordinate targetLocation) {
		if(targetLocation.row() < 0 || targetLocation.col() < 0 || targetLocation.row() >= this.height || targetLocation.col() >= this.width) {
			throw new IndexOutOfBoundsException();	
		}
	}
/**
 * Getter of element at coordinate in grid
*/
	protected T getGridLocation(Coordinate targetLocation){
		this.testBounds(targetLocation);	
		return grid[targetLocation.row()][targetLocation.col()];
	}

/**
 *	Individual coordinate setter for grid
*/	
	protected void setGridLocation(Coordinate targetLocation, T targetElement) {
		this.testBounds(targetLocation);	
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
			.range(0, n)
			.toArray();
	}

	protected int[] getSequence(int start, int n) {
		return IntStream
			.range(start, n)
			.toArray();
	}

/**
 *      getNextNodes: Gets all nearest neighbours coordinates of a given point. 
 *      Breadth-first traversal of grid enables custom move ranges and step sizes.
 *      Guaranteed to conform to bounds of grid.
 *
 *      @param startingNode     
*/
	public List<Coordinate> getNextNodes(Coordinate startingNode, int moveRange, int stepSize) {
                int currentBudget = moveRange;
                List<Coordinate> affordableNodes = new ArrayList<>();
                //History is a set to avoid duplicates  
                Set<Coordinate> history = new HashSet<>();
                List<Coordinate> queueNodes = new ArrayList<>();

                queueNodes.add(startingNode);
                history.add(startingNode);

                while(currentBudget > 0 && !queueNodes.isEmpty()) {
                        List<Coordinate> toBeQueued = new ArrayList<>();
                        //Traverse all nodes in queue   
                        for (Coordinate node: queueNodes) {
                                //Get neighbours        
                                List<Coordinate> neighbours = this.traverse(node, stepSize);

                                neighbours = neighbours.stream()
                                        //Remove invalid nodes  
                                        .filter(Objects::nonNull)
                                        //Remove already traversed      
                                        .filter(neighbour -> !history.contains(neighbour))
                                        .toList();

                                //Update
                                affordableNodes.addAll(neighbours);
                                toBeQueued.addAll(neighbours);
                                history.addAll(neighbours);
                        }

                        //Update queue  
                        queueNodes = toBeQueued;
                        //Decrease budget       
                        currentBudget-=stepSize;
                }
                //Remove origin node
                affordableNodes.remove(startingNode);


                return affordableNodes;
        }

/**
 *      Helper methods for traversal
*/

/**
 *      checkIfValidNode: Checks if node at (row, col) is valid.
*/
        private Coordinate checkIfValidNode(int row, int col) {
                try {
                        Coordinate targetLocation = new Coordinate(row, col);
                        this.testBounds(targetLocation);
                        return targetLocation;
                } catch (IndexOutOfBoundsException e) {
                        return null;
                }
        }
/**
 *      traverse: List nearest neighbours of node.
*/
        private List<Coordinate> traverse(Coordinate node, int stepSize) {
                return Arrays.asList(
                        checkIfValidNode(node.row() - stepSize, node.col()),
                        checkIfValidNode(node.row() + stepSize, node.col()),
                        checkIfValidNode(node.row(), node.col() - stepSize),
                        checkIfValidNode(node.row(), node.col() + stepSize)
                );

        }

}
