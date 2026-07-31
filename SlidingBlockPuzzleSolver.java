import java.util.Scanner;
import java.util.HashSet;
import java.util.ArrayList;
	
	/**
	 * Solves a 4x4 sliding block puzzle.
	 *
	 * The algorithm focuses on one row or column at a time. After a
	 * row or column has been solved, the algorithm runs recursively
	 * on the remaining unsolved portion of the puzzle.
	 *
	 * States are explored using a priority queue ordered by the weighted sum of: 
	 *     - The Manhattan distances of relevant tiles
	 *     - The number of moves it took to get to that state
	 *     
	 * Visited states are stored to prevent the algorithm from revisiting previously explored configurations.
	 * 
	 * (Originally implemented November 2025; documented and revised in March 2026; switched to HashSet for visited state tracking in May 2026; switched to a heap implementation of the priority queue in July 2026; started adding path cost when calculating state value in July 2026)
	 * */
	public class SlidingBlockPuzzleSolver{
		/**
		 * Reads a 4x4 sliding block puzzle configuration, runs the solver,
		 * then prints the sequence of moves it used to reach the solution.
		 * 
		 * The user can see the next move by pressing Enter.
		 * 
		 * Input format: Four lines containing four integers each, representing the 
		 * puzzle rows. The empty tile is represented by a 0. The numbers from 1-15
		 * should be used exactly once. Input is assumed to be valid.
		 * */
		final static int[][] solution = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
		final static int rows = 4;
		final static int columns = 4;
		final static char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
		public static void main(String[] args){
			Scanner input = new Scanner(System.in);
			int[][] state = new int[rows][columns];
			String s = "";
			Scanner stringScanner;
			
			for (int i = 0; i < rows; i++){
				System.out.print("Enter row " + (i+1) + ": ");
				s = input.nextLine();
				stringScanner = new Scanner(s);
				for (int j = 0; j < columns; j++){
					state[i][j] = stringScanner.nextInt();
				}
			}
		
			GameState start = new GameState(state, null, 0, null, -1, -1);
			
			start = move(start, -1, -1); //starts with the first row
			
			s = "";
	
			while (start.parent != null){
				s = "Move " + start.target + " " + start.dir + "\n" + s;
				start = start.parent;
			}
			
			System.out.print("Press Enter to see next move... ");
			stringScanner = new Scanner(s);
			while (stringScanner.hasNextLine()){
				input.nextLine();
				System.out.print(stringScanner.nextLine());
			}
			stringScanner.close();
			input.close();
		}
		
		/**
		 * The main solver algorithm.
		 * Solves a 4x4 sliding block puzzle row-by-row and column-by-column.
		 * 
		 * @param queue The priority queue of nodes to visit
		 * @param visited The set of visited nodes
		 * @param solvedRows The index of the highest row that has already been solved
		 * @param solvedColumns The index of the highest column that has already been solved
		 * @return The solved game state (GameState objects contain references to their parent state)
		 * */
		public static GameState move(GameState start, int solvedRows, int solvedColumns){
			PriorityQueue queue = new PriorityQueue();
			queue.enqueue(start); //the state value of newGameState will be wrong for the new row/column values, but it doesn't matter because it will be removed from the queue before anything else is added to it
			HashSet<String> visited = new HashSet<String>();
			visited.add(start.stateToString(solvedRows, solvedColumns));
			
			while (!queue.isEmpty()){
				GameState top = queue.pop();
				if (top.isSolved(solvedRows, solvedColumns)){ //intended row/column already solved at beginning of function call
					if (solvedColumns == columns-1 -1){ //puzzle finished
						top.end = true;
						return top;
					}
					else{
						GameState sol;
						
						if (solvedRows <= solvedColumns) { //row solved. Solve a column next
							sol = move(top, solvedRows+1, solvedColumns); //Run the exact same algorithm, but on a smaller version of the puzzle
						}
						else { //column solved. Solve a row next
							sol = move(top, solvedRows, solvedColumns+1);
						}
						
						if (sol != null && sol.end){
							return sol;
						}
						else {
							System.out.println("Something went wrong. ");
							return null;
						}
					}
				}
				
				
				int[][] newBoard;
				int i = top.emptyRow;
				int j = top.emptyColumn;
				
				if (i != rows-1 && top.dir != GameState.Directions.down){ //move up
					newBoard = arrayCopy(top.state);
					newBoard[i][j] = newBoard[i+1][j];
					newBoard[i+1][j] = 0;
					GameState newGameState = new GameState(newBoard,top,newBoard[i][j],GameState.Directions.up,solvedRows,solvedColumns);
					
					String id = newGameState.stateToString(solvedRows,solvedColumns);
					if (!visited.contains(id)){
						visited.add(id);
						queue.enqueue(newGameState);
					}
				}
				if (i > solvedRows+1 && top.dir != GameState.Directions.up){ //move down
					newBoard = arrayCopy(top.state);
					newBoard[i][j] = newBoard[i-1][j];
					newBoard[i-1][j] = 0;
					GameState newGameState = new GameState(newBoard,top,newBoard[i][j],GameState.Directions.down,solvedRows,solvedColumns);
					
					String id = newGameState.stateToString(solvedRows,solvedColumns);
					if (!visited.contains(id)){
						visited.add(id);
						queue.enqueue(newGameState);
					}
				}
				if (j != columns-1 && top.dir != GameState.Directions.right){ //move left
					newBoard = arrayCopy(top.state);
					newBoard[i][j] = newBoard[i][j+1];
					newBoard[i][j+1] = 0;
					GameState newGameState = new GameState(newBoard,top,newBoard[i][j],GameState.Directions.left,solvedRows,solvedColumns);
					
					String id = newGameState.stateToString(solvedRows,solvedColumns);
					if (!visited.contains(id)){
						visited.add(id);
						queue.enqueue(newGameState);
					}
				}
				if (j > solvedColumns+1 && top.dir != GameState.Directions.left){ //move right
					newBoard = arrayCopy(top.state);
					newBoard[i][j] = newBoard[i][j-1];
					newBoard[i][j-1] = 0;
					GameState newGameState = new GameState(newBoard,top,newBoard[i][j],GameState.Directions.right,solvedRows,solvedColumns);
					
					String id = newGameState.stateToString(solvedRows,solvedColumns);
					if (!visited.contains(id)){
						visited.add(id);
						queue.enqueue(newGameState);
					}
				}
			}
			System.out.println("No solution, " + solvedRows +", "+ solvedColumns);
			return null;
		}
		
		/**
		 * Represents a 4x4 sliding block puzzle game state.
		 * A reference to the previous game state is kept, along with 
		 * the number that was moved to create the current state and
		 * the direction that it was moved in. The row and column of the
		 * empty tile are also stored. The game state has a "value"
		 * calculated using the weighted sum of its path cost and 
		 * a heuristic function.
		 * */
		public static class GameState{
			private int[][] state;
			private int emptyRow;
			private int emptyColumn;
			private GameState parent;
			private int target; //tile that was moved to reach this state
			private Directions dir; //direction that the tile was moved to reach this state
			private boolean end; //is this state the final solution?
			private int cost; //number of moves taken to reach this state
			private int value; //priority value used by the queue
			
			public static enum Directions {left,right,up,down};
			
			public GameState(int[][] state, GameState parent, int target, Directions dir, int solvedRows, int solvedColumns) {
				this.state = state;
				this.parent = parent;
				this.dir = dir;
				this.target = target;
				this.end = false;
				if (parent == null) {
					this.cost = 0;
					
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < columns; j++) {
							if (state[i][j] == 0){ //find empty tile
								this.emptyRow = i;
								this.emptyColumn= j;
								i = rows; break;
							}
						}
					}
				}
				else{
					this.cost = parent.cost + 1;
					
					this.emptyRow = parent.emptyRow;
					this.emptyColumn = parent.emptyColumn;
					
					if (this.dir == Directions.left) this.emptyColumn++;
					else if (this.dir == Directions.right) this.emptyColumn--;
					else if (this.dir == Directions.up) this.emptyRow++;
					else this.emptyRow--;
				}
				this.value = getStateValue(solvedRows,solvedColumns);
			}
			
			/**
			 * Calculates the "value" of the game state using path cost and a
			 * Manhattan distance heuristic on the numbers in the current row or column.
			 * 
			 * @param solvedRows The index of the highest row that has already been solved
			 * @param solvedColumns The index of the highest column that has already been solved
			 * @return The "value" of the game state
			 * */
			public int getStateValue(int solvedRows, int solvedColumns) {
				int stateValue = 0;
				if (solvedRows <= solvedColumns) {
					for (int i = 0; i < rows; i++){
						for (int j = 0; j < columns; j++){
							if (getRow(state[i][j]) == solvedRows+1){
								stateValue += Math.abs(j-getColumn(state[i][j])) + Math.abs(i-getRow(state[i][j]));
							}
						}
					}
				}
				else {
					for (int i = 0; i < rows; i++){
						for (int j = 0; j < columns; j++){
							if (getColumn(state[i][j]) == solvedColumns+1) {
								stateValue += Math.abs(j-getColumn(state[i][j])) + Math.abs(i-getRow(state[i][j]));
							}
						}
					}
				}
				return 2*stateValue + this.cost;
			}
			
			/**
			 * Checks if the the current game state has solved the current row or column.
			 * 
			 * @param solvedRows The index of the highest row that has already been solved
			 * @param solvedColumns The index of the highest column that has already been solved
			 * @return true if the specified rows and columns are solved, otherwise false
			 * */
			public boolean isSolved(int solvedRows, int solvedColumns){
				if (solvedRows <= solvedColumns) return solvedRow(solvedRows+1);
				else return solvedColumn(solvedColumns+1);
			}
			
			/**
			 * Checks if the specified row of a game state matches the solution.
			 * 
			 * @param row The row to be checked
			 * @return true if the specified rows and columns are solved, otherwise false
			 * */
			private boolean solvedRow(int row){
				for (int j = 0; j < columns; j++){
					if (this.state[row][j] != solution[row][j]){
						return false;
					}
				}
				return true;
			}
			
			/**
			 * Checks if the specified column of a game state matches the solution.
			 * 
			 * @param column The column to be checked
			 * @return true if the specified rows and columns are solved, otherwise false
			 * */
			private boolean solvedColumn(int column){
				for (int i = 0; i < rows; i++){
					if (this.state[i][column] != solution[i][column]){
						return false;
					}
				}
				return true;
			}
			
			/**
			 * Creates a String representing a 4x4 sliding block puzzle game state.
			 * Multi-digit numbers are replaced by letters to ensure uniqueness.
			 * Numbers that will be placed (in the puzzle solution) both below the current
			 * row and to right of the current column are ignored and replaced by Xs.
			 * 
			 * @param solvedRows The index of the highest row that has already been solved
			 * @param solvedColumns The index of the highest column that has already been solved
			 * @return A string representing the game state.
			 * */
			public String stateToString(int solvedRows, int solvedColumns) {
				String s = "";
				for (int i = 0; i < rows; i++){
					for (int j = 0; j < columns; j++){
						if (getRow(this.state[i][j]) <= solvedRows || getColumn(this.state[i][j]) <= solvedColumns) s = s + hex[this.state[i][j]];
						else if (solvedRows <= solvedColumns && getRow(this.state[i][j]) == solvedRows+1) s = s + hex[this.state[i][j]];
						else if (solvedRows > solvedColumns && getColumn(this.state[i][j]) == solvedColumns+1) s = s + hex[this.state[i][j]];
						else s = s + 'X';
					}
				}
				return s;
			}
			
			private static int getRow(int n) {if (n <= 0) return -1; else return (n-1)/rows;}
			private static int getColumn(int n) {if (n <= 0) return -1; else return (n-1)%columns;}
			
		}
	
		/*
		 * A priority queue of game states to be searched.
		 * Nodes are sorted in ascending order by state value.
		 * */
		public static class PriorityQueue{
			private ArrayList<GameState> arr = new ArrayList<GameState>(); //used as a min-heap
			
			/**
			 * Adds a game state node to the priority queue.
			 * 
			 * @param newGameState The game state to be added
			 * */
			public void enqueue(GameState newGameState){
				int index = arr.size();
				arr.add(newGameState);
				
				while (index > 0 && newGameState.value < arr.get(parentIndex(index)).value) {
					arr.set(index, arr.get(parentIndex(index)));
					index = parentIndex(index);
				}
				
				arr.set(index, newGameState);
			}
			
			/**
			 * Removes and returns the lowest-value game state in the queue.
			 * 
			 * @return The lowest value game state
			 * */
			public GameState pop() {
				if (arr.size() == 0) return null;
				
				GameState top = arr.get(0);
				arr.set(0, arr.get(arr.size()-1));
				GameState g = arr.remove(arr.size()-1);
				
				if (arr.size() == 0) return top;
				
				int index = 0;
				int leftIndex = leftChildIndex(0);
				int rightIndex = rightChildIndex(0);
				
				while (true) {
					boolean left = leftIndex < arr.size() && arr.get(leftIndex).value < g.value;
					boolean right = rightIndex < arr.size() && arr.get(rightIndex).value < g.value && (!left || arr.get(leftIndex).value > arr.get(rightIndex).value);
	
					if (right) {
						arr.set(index, arr.get(rightIndex));
						index = rightIndex;
					}
					else if (left) {
						arr.set(index, arr.get(leftIndex));
						index = leftIndex;
					}
					else {
						arr.set(index, g);
						return top;
					}
	
					leftIndex = leftChildIndex(index);
					rightIndex = rightChildIndex(index);
				}
			}
			
			/**
			 * Returns true if the queue is empty, false otherwise.
			 * 
			 * @return True if the queue is empty
			 * */
			public boolean isEmpty() {
				return arr.size() == 0;
			}
			
			private static int leftChildIndex(int index) {return 2*index + 1;}
			private static int rightChildIndex(int index) {return 2*index + 2;}
			private static int parentIndex(int index) {if (index == 0) return -1; else return (index-1)/2;}
		}
		
		/**
		 * Creates a deep copy of a 2D array of integers.
		 * 
		 * @param array The array to be copied
		 * @return A deep copy of the array
		 * */
		private static int[][] arrayCopy(int[][] array){
			int[][] newArray = new int[array.length][array[0].length];
			for (int i = 0; i < array.length; i++){
				for (int j = 0; j < array[i].length; j++){
					newArray[i][j] = array[i][j];
				}
			}
			return newArray;
		}
	}