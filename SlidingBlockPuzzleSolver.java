import java.util.Scanner;
import java.util.HashSet;
/**
 * Solves a 4x4 sliding block puzzle.
 *
 * The algorithm focuses on one row or column at a time. After a
 * row or column has been solved, the algorithm runs recursively
 * on the remaining unsolved portion of the puzzle.
 *
 * States are explored using a priority queue ordered by the Manhattan
 * distance of relevant tiles. Visited states are stored to prevent
 * the algorithm from revisiting previously explored configurations.
 * 
 * (Originally implemented November 2025; documented and revised in March 2026; switched to HashSet for visited state tracking in May 2026)
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
	
		GameState start = new GameState(state, null, null, 0);
		
		StateQueueNode top = new StateQueueNode(start);
		
		HashSet<String> visited = new HashSet<String>();
		visited.add(start.stateToString(0,-1));
		
		start = move(top, visited, 0, -1); //starts with the first row
		
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
	 * @param top The top of the priority queue of game states to be searched
	 * @param visitedListHead The head of the list of visited states
	 * @param currentRow The row that is currently being solved (if currentRow <= currentColumn), or was solved in the previous recursion (if currentRow > currentColumn)
	 * @param currentColumn The column that is currently being solved (if currentRow > currentColumn), or was solved in the previous recursion (if currentRow <= currentColumn)
	 * @return The solved game state (GameState objects contain references to their parent state)
	 * */
	private static GameState move(StateQueueNode top, HashSet<String> visited, int currentRow, int currentColumn){
		int c = currentColumn;
		if (currentColumn < 0) {
			c = 0;
		}
		
		if (top.value.solvedRowAndColumn(currentRow,currentColumn)){ //intended row/column already solved at beginning of function call
			if (currentColumn == columns-1 -1){ //puzzle finished
				top.value.end = true;
				return top.value;
			}
			else{
				GameState sol = processNewState(top.value, currentRow, currentColumn);
				if (sol != null){
					return sol;
				}
			}
		}
		
		while (true){
			GameState current = top.value;
			int[][] newBoard;
		
			for (int i = currentRow; i < rows; i++){ //ignore rows that have already been solved
				for (int j = c; j < columns; j++){ //ignore columns that have already been solved
					if (current.state[i][j] == 0){ //find empty tile
						if (i != rows-1){
							newBoard = arrayCopy(current.state);
							newBoard[i][j] = newBoard[i+1][j];
							newBoard[i+1][j] = 0;
							GameState newGameState = new GameState(newBoard,current,GameState.Directions.up,newBoard[i][j]);
							
							if (!visited.contains(newGameState.stateToString(currentRow,currentColumn))){
								visited.add(newGameState.stateToString(currentRow,currentColumn));
								top.enqueueNode(newGameState, currentRow, currentColumn);
								if (newGameState.solvedRowAndColumn(currentRow,currentColumn)){
									GameState sol = processNewState(newGameState, currentRow, currentColumn);
									if (sol != null){
										return sol;
									}
								}
							}
						}
						if (i != currentRow){ //move down
							newBoard = arrayCopy(current.state);
							newBoard[i][j] = newBoard[i-1][j];
							newBoard[i-1][j] = 0;
							GameState newGameState = new GameState(newBoard,current,GameState.Directions.down,newBoard[i][j]);
							
							if (!visited.contains(newGameState.stateToString(currentRow,currentColumn))){
								visited.add(newGameState.stateToString(currentRow,currentColumn));
								top.enqueueNode(newGameState, currentRow, currentColumn);
								if (newGameState.solvedRowAndColumn(currentRow,currentColumn)){
									GameState sol = processNewState(newGameState, currentRow, currentColumn);
									if (sol != null){
										return sol;
									}
								}
							}
						}
						if (j != columns-1){ //move left
							newBoard = arrayCopy(current.state);
							newBoard[i][j] = newBoard[i][j+1];
							newBoard[i][j+1] = 0;
							GameState newGameState = new GameState(newBoard,current,GameState.Directions.left,newBoard[i][j]);
							
							if (!visited.contains(newGameState.stateToString(currentRow,currentColumn))){
								visited.add(newGameState.stateToString(currentRow,currentColumn));
								top.enqueueNode(newGameState, currentRow, currentColumn);
								if (newGameState.solvedRowAndColumn(currentRow,currentColumn)){
									GameState sol = processNewState(newGameState, currentRow, currentColumn);
									if (sol != null){
										return sol;
									}
								}
							}
						}
						if (j != c){ //move right
							newBoard = arrayCopy(current.state);
							newBoard[i][j] = newBoard[i][j-1];
							newBoard[i][j-1] = 0;
							GameState newGameState = new GameState(newBoard,current,GameState.Directions.right,newBoard[i][j]);
							
							if (!visited.contains(newGameState.stateToString(currentRow,currentColumn))){
								visited.add(newGameState.stateToString(currentRow,currentColumn));
								top.enqueueNode(newGameState, currentRow, currentColumn);
								if (newGameState.solvedRowAndColumn(currentRow,currentColumn)){
									GameState sol = processNewState(newGameState, currentRow, currentColumn);
									if (sol != null){
										return sol;
									}
								}
							}
						}
					}
				}
			}
			if (top.next != null){
				top = top.next;
			}
			else {
				break;
			}
		}
		System.out.println("No solution, " + currentRow +", "+ currentColumn);
		return null;
	}
	
	/**
	 * Starts a new search phase after a row or column has been solved.
	 * Creates a new game state queue and visited state list, used to recursively
	 * run the solver on the remaining unsolved portion of the puzzle.
	 * @param newGameState The game state with the newly solved row or column
	 * @param currentRow The row was just solved, or was solved in the previous recursion
	 * @param currentColumn The column was just solved, or was solved in the previous recursion
	 * @return The solved GameState if the puzzle is successfully completed
	 * */
	private static GameState processNewState(GameState newGameState, int currentRow, int currentColumn){
		StateQueueNode newTop = new StateQueueNode(newGameState);
		
		HashSet<String> newVisited = new HashSet<String>();
		GameState sol;
		
		if (currentRow <= currentColumn) { //row solved. Solve a column next
			newVisited.add(newGameState.stateToString(currentRow+1, currentColumn));
			sol = move(newTop, newVisited, currentRow+1, currentColumn); //Run the exact same algorithm, but on a smaller version of the puzzle
		}
		else { //column solved. Solve a row next
			newVisited.add(newGameState.stateToString(currentRow, currentColumn+1));
			sol = move(newTop, newVisited, currentRow, currentColumn+1);
		}
		
		if (sol != null && sol.end){
			return sol;
		}
		else {
			System.out.println("Something went wrong. ");
			return null;
		}
	}
	
	/**
	 * Creates a deep copy of a 2D array of integers
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
	
	/**
	 * Represents a 4x4 sliding block puzzle game state.
	 * A reference to the previous game state is kept (tree structure),
	 * along with the number that was moved to create the current state,
	 * and the direction that it was moved in.
	 * */
	private static class GameState{
		private int[][] state;
		private GameState parent;
		private Directions dir;
		private int target;
		private boolean end;
		
		private static enum Directions {left,right,up,down};
		
		private GameState(int[][] state, GameState parent, Directions dir, int target) {
			this.state = state;
			this.parent = parent;
			this.dir = dir;
			this.target = target;
			this.end = false;
		}
		
		/**
		 * Checks if the specified row and column of a game state match the solution.
		 * @param row The row to be checked
		 * @param column The column to be checked
		 * @return true if the specified rows and columns are solved, otherwise false
		 * */
		private boolean solvedRowAndColumn(int row, int column){
			for (int j = 0; j < columns; j++){
				if (this.state[row][j] != solution[row][j]){
					return false;
				}
			}
			if (column < 0) return true;
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
		 * row and to right of the current column are ignored and replaced by Xs
		 * @param currentRow The row that is currently being solved, or was solved in the previous recursion
		 * @param currentColumn The column that is currently being solved, or was solved in the previous recursion
		 * @return A string representing the game state.
		 * */
		private String stateToString(int currentRow, int currentColumn) {
			char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
			String s = "";
			for (int i = 0; i < rows; i++){
				for (int j = 0; j <columns; j++){
					if (this.state[i][j] <= (currentRow+1)*4 || (this.state[i][j]-1)%4 <= currentColumn)
						s = s + hex[this.state[i][j]];
					else{
						s = s + 'X';
					}
				}
			}
			return s;
		}
	}

	/*
	 * A priority queue of game states to be searched.
	 * Nodes are sorted by Manhattan distance. Tiles that are not part of
	 * the row or column that is currently being solved are not considered.
	 * */
	private static class StateQueueNode{
		private GameState value;
		private int stateValue;
		private StateQueueNode next;
		
		private StateQueueNode(GameState value) {
			this.value = value;
		}
		
		/**
		 * Adds a game state node to a priority queue of game states (to be explored later).
		 * Must be called from the top node of the game state priority queue.
		 * @param newGameState The game state to be added
		 * @param currentRow The row that is currently being solved, or was solved in the previous recursion
		 * @param currentColumn The column that is currently being solved, or was solved in the previous recursion
		 * */
		private void enqueueNode(GameState newGameState,  int currentRow, int currentColumn){
			StateQueueNode newNode = new StateQueueNode(newGameState);
			StateQueueNode node = this;
			int stateValue = 0;
			for (int i = 0; i < rows; i++){
				for (int j = 0; j < columns; j++){
					if (currentRow <= currentColumn && newGameState.state[i][j] <= (currentRow+1)*4 && newGameState.state[i][j] > currentRow*4){
						stateValue = stateValue + Math.abs(j-(newGameState.state[i][j]-1)%4) + Math.abs(i-(newGameState.state[i][j]-1)/4);
					}
					else if (currentRow > currentColumn && newGameState.state[i][j]%4 - 1 == currentColumn) {
						stateValue = stateValue + Math.abs(j-(newGameState.state[i][j]-1)%4) + Math.abs(i-(newGameState.state[i][j]-1)/4);
					}
				}
			}
			newNode.stateValue = stateValue;
			
			while (node.next != null){
				if (stateValue < node.next.stateValue){
					newNode.next = node.next;
					node.next = newNode;
					return;
				}
				node = node.next;
			}
			node.next = newNode;
		}
	}
}