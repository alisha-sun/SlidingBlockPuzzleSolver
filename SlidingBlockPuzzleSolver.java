import java.util.Scanner;

public class SlidingBlockPuzzleSolver{
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
		final int rows = 4;
		final int columns = 4;
		int[][] state = new int[rows][columns];
		String s = "";
		Scanner stringScanner;
		
		for (int i = 0; i < rows; i++){
			System.out.print("Enter row " + (i+1) + ": ");
			s = input.nextLine();
			stringScanner = new Scanner(s);
			for (int j = 0; j<columns; j++){
				state[i][j] = stringScanner.nextInt();
			}
		}

		System.out.println(stateToString(state,0,-1));
		input.nextLine();
		
		int[][] solution = {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}};
	
		GameState start = new GameState();
		start.state = state;
		
		StateQueueNode bottom = new StateQueueNode();
		StateQueueNode top = new StateQueueNode();
		bottom.value = start;
		top.value = start;
		top.next = bottom;
		
		VisitedListNode newVisitedListHead = new VisitedListNode();
		newVisitedListHead.value = stateToString(state,0,-1);
		
		bottom = move(bottom, top, solution, newVisitedListHead, 0, -1);
		//System.out.print("\nexited");
		
		start = bottom.value;
		s = "";
		//System.out.println("dfafdsaf "+start.parent);
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
	}
	
	private static StateQueueNode move(StateQueueNode bottom, StateQueueNode top, int[][] solution, VisitedListNode visitedListHead, int currentRow, int currentColumn){
		int c = currentColumn;
		if (currentColumn < 0) {
			c = 0;
		}
		
		if (solvedRowAndColumn(top.value.state,solution,currentRow,currentColumn)){ //row/column solved already
			System.out.println("Solved row" + currentRow + " with move NONE");
			printContents(top.value.state);
			if (currentColumn == 2){ //puzzle finished
				top.value.end = true;
				System.out.println("puzzle solved "+ top.value.end);
				printContents(top.value.state);
				return top;
			}
			else{
				StateQueueNode newBottom = new StateQueueNode();
				StateQueueNode newTop = new StateQueueNode();
				newBottom.value = top.value;
				newTop.value = top.value;
				newTop.next = newBottom;
				
				VisitedListNode newVisitedListHead = new VisitedListNode();
				StateQueueNode sol;
				
				if (currentRow <= currentColumn) {
					newVisitedListHead.value = stateToString(top.value.state, currentRow+1, currentColumn);
					sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow+1, currentColumn);
				}
				else {
					newVisitedListHead.value = stateToString(top.value.state, currentRow, currentColumn+1);
					sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow, currentColumn+1);
				}
				
				if (sol != null && sol.value.end){
					return sol;
				}
				else {
					System.out.println("Something went wrong. ");
					System.out.println("Row: " + currentRow);
					System.out.println("Column: " + currentColumn);
					Scanner input = new Scanner(System.in);
					input.nextLine();
				}
			}
		}
		
		while (bottom != null){
			GameState current = top.value;
			int[][] newValue;
		
			for (int i = currentRow; i <= 3; i++){ //ignore rows that have already been solved
				for (int j = c; j <= 3; j++){ //ignore columns that have already been solved
					if (current.state[i][j] == 0){ //find empty tile
						if (i != 3 && current.state[i+1][j] != current.target){ //move up; don't undo previous move
							newValue = arrayCopy(current.state);
							newValue[i][j] = newValue[i+1][j];
							newValue[i+1][j] = 0;
							
							if (!visitedState(newValue,visitedListHead,currentRow,currentColumn)){
								printContents(newValue);
								System.out.println(currentRow);
								
								GameState newNode = new GameState();
								newNode.state = newValue;
								newNode.dir = "up";
								newNode.target = newValue[i][j];
								newNode.parent = current;

								enqueueNode(top, newNode, currentRow, currentColumn);

								if (solvedRowAndColumn(newValue,solution,currentRow,currentColumn)){
									printContents(newNode.state);
									if (currentColumn == 2){
										newNode.end = true;
										System.out.println("afdasdf/n"+ top.value.end);
										printContents(top.value.state);
										StateQueueNode sol = new StateQueueNode();
										sol.value = newNode;
										return sol;
									}
									else{	
										StateQueueNode newBottom = new StateQueueNode();
										StateQueueNode newTop = new StateQueueNode();
										newBottom.value = newNode;
										newTop.value = newNode;
										newTop.next = newBottom;
										
										VisitedListNode newVisitedListHead = new VisitedListNode();
										StateQueueNode sol;
										
										if (currentRow <= currentColumn) {
											newVisitedListHead.value = stateToString(newNode.state, currentRow+1, currentColumn);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow+1, currentColumn);
										}
										else {
											newVisitedListHead.value = stateToString(newNode.state, currentRow, currentColumn+1);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow, currentColumn+1);
										}
										
										if (sol != null && sol.value.end){
											return sol;
										}
										else {
											System.out.println("Something went wrong. ");
											System.out.println("Row: " + currentRow);
											System.out.println("Column: " + currentColumn);
											Scanner input = new Scanner(System.in);
											input.nextLine();
										}
									}
								}
							}
						}
						if (i != currentRow && current.state[i-1][j] != current.target){ //move down
							newValue = arrayCopy(current.state);
							newValue[i][j] = newValue[i-1][j];
							newValue[i-1][j] = 0;
							
							if (!visitedState(newValue,visitedListHead,currentRow,currentColumn)){
								printContents(newValue);
								System.out.println(currentRow);
								
								GameState newNode = new GameState();
								newNode.state = newValue;
								newNode.dir = "down";
								newNode.target = newValue[i][j];
								newNode.parent = current;

								enqueueNode(top, newNode, currentRow, currentColumn);

								if (solvedRowAndColumn(newValue,solution,currentRow,currentColumn)){
									printContents(newNode.state);
									if (currentColumn == 2){
										newNode.end = true;
										System.out.println("afdasdf/n"+ top.value.end);
										printContents(top.value.state);
										StateQueueNode sol = new StateQueueNode();
										sol.value = newNode;
										return sol;
									}
									else{	
										StateQueueNode newBottom = new StateQueueNode();
										StateQueueNode newTop = new StateQueueNode();
										newBottom.value = newNode;
										newTop.value = newNode;
										newTop.next = newBottom;
										
										VisitedListNode newVisitedListHead = new VisitedListNode();
										StateQueueNode sol;
										
										if (currentRow <= currentColumn) {
											newVisitedListHead.value = stateToString(newNode.state, currentRow+1, currentColumn);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow+1, currentColumn);
										}
										else {
											newVisitedListHead.value = stateToString(newNode.state, currentRow, currentColumn+1);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow, currentColumn+1);
										}
										
										if (sol != null && sol.value.end){
											return sol;
										}
										else {
											System.out.println("Something went wrong. ");
											System.out.println("Row: " + currentRow);
											System.out.println("Column: " + currentColumn);
											Scanner input = new Scanner(System.in);
											input.nextLine();
										}
									}
								}
							}
						}
						if (j != current.state[0].length-1 && current.state[i][j+1] != current.target){ //move left
							newValue = arrayCopy(current.state);
							newValue[i][j] = newValue[i][j+1];
							newValue[i][j+1] = 0;
							
							if (!visitedState(newValue,visitedListHead,currentRow,currentColumn)){
								printContents(newValue);
								System.out.println(currentRow);
								
								GameState newNode = new GameState();
								newNode.state = newValue;
								newNode.dir = "left";
								newNode.target = newValue[i][j];
								newNode.parent = current;

								enqueueNode(top, newNode, currentRow, currentColumn);

								if (solvedRowAndColumn(newValue,solution,currentRow,currentColumn)){
									printContents(newNode.state);
									if (currentColumn == 2){
										newNode.end = true;
										System.out.println("afdasdf/n"+ top.value.end);
										printContents(top.value.state);
										StateQueueNode sol = new StateQueueNode();
										sol.value = newNode;
										return sol;
									}
									else{	
										StateQueueNode newBottom = new StateQueueNode();
										StateQueueNode newTop = new StateQueueNode();
										newBottom.value = newNode;
										newTop.value = newNode;
										newTop.next = newBottom;
										
										VisitedListNode newVisitedListHead = new VisitedListNode();
										StateQueueNode sol;
										
										if (currentRow <= currentColumn) {
											newVisitedListHead.value = stateToString(newNode.state, currentRow+1, currentColumn);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow+1, currentColumn);
										}
										else {
											newVisitedListHead.value = stateToString(newNode.state, currentRow, currentColumn+1);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow, currentColumn+1);
										}
										
										if (sol != null && sol.value.end){
											return sol;
										}
										else {
											System.out.println("Something went wrong. ");
											System.out.println("Row: " + currentRow);
											System.out.println("Column: " + currentColumn);
											Scanner input = new Scanner(System.in);
											input.nextLine();
										}
									}
								}
							}
						}
						if (j != c && current.state[i][j-1] != current.target){ //move right
							newValue = arrayCopy(current.state);
							newValue[i][j] = newValue[i][j-1];
							newValue[i][j-1] = 0;
							
							if (!visitedState(newValue,visitedListHead,currentRow,currentColumn)){
								printContents(newValue);
								System.out.println(currentRow);
								
								GameState newNode = new GameState();
								newNode.state = newValue;
								newNode.dir = "right";
								newNode.target = newValue[i][j];
								newNode.parent = current;

								enqueueNode(top, newNode, currentRow, currentColumn);

								if (solvedRowAndColumn(newValue,solution,currentRow,currentColumn)){
									printContents(newNode.state);
									if (currentColumn == 2){
										newNode.end = true;
										System.out.println("afdasdf/n"+ top.value.end);
										printContents(top.value.state);
										StateQueueNode sol = new StateQueueNode();
										sol.value = newNode;
										return sol;
									}
									else{	
										StateQueueNode newBottom = new StateQueueNode();
										StateQueueNode newTop = new StateQueueNode();
										newBottom.value = newNode;
										newTop.value = newNode;
										newTop.next = newBottom;
										
										VisitedListNode newVisitedListHead = new VisitedListNode();
										StateQueueNode sol;
										
										if (currentRow <= currentColumn) {
											newVisitedListHead.value = stateToString(newNode.state, currentRow+1, currentColumn);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow+1, currentColumn);
										}
										else {
											newVisitedListHead.value = stateToString(newNode.state, currentRow, currentColumn+1);
											sol = move(newBottom, newTop, solution, newVisitedListHead, currentRow, currentColumn+1);
										}
										
										if (sol != null && sol.value.end){
											return sol;
										}
										else {
											System.out.println("Something went wrong. ");
											System.out.println("Row: " + currentRow);
											System.out.println("Column: " + currentColumn);
											Scanner input = new Scanner(System.in);
											input.nextLine();
										}
									}
								}
							}
						}
					}
				}
			}
			if (top.next != null){
				top = top.next; //dequeue
			}
			else{
				bottom = null;//quit
			}
		}
		System.out.println("no solution, " + currentRow);
		return bottom;
	}
	
	private static boolean visitedState(int[][] state, VisitedListNode visitedListHead, int currentRow, int currentColumn){
		VisitedListNode node = visitedListHead;
		VisitedListNode newNode;
		
		String str = stateToString(state, currentRow, currentColumn);
		
		int comparisonNumber = str.compareTo(visitedListHead.value);
		
		if (comparisonNumber == 0){
			return true;
		}
		else if (comparisonNumber < 0){
			newNode = new VisitedListNode();
			newNode.value = visitedListHead.value;
			newNode.next = visitedListHead.next;
			visitedListHead.value = str;
			visitedListHead.next = newNode;
			
			return false;
		}
		
		while (node.next != null){
			comparisonNumber = str.compareTo(node.next.value);
			if (comparisonNumber == 0){
				return true;
			}
			else if (comparisonNumber < 0){ //add between current and next
				newNode = new VisitedListNode();
				newNode.value = str;
				newNode.next = node.next;
				node.next = newNode;
				
				return false;
			}
			node = node.next;
		}
		
		newNode = new VisitedListNode();
		newNode.value = str;
		newNode.next = null;
		node.next = newNode;
		return false;
	}
	
	private static void enqueueNode(StateQueueNode top, GameState newNode,  int currentRow, int currentColumn){
		StateQueueNode newItem = new StateQueueNode();
		newItem.value = newNode;
		StateQueueNode node = top;
		int stateValue = 0;
		for (int i = 0; i < newNode.state.length; i++){
			for (int j = 0; j < newNode.state[i].length; j++){
				if (currentRow <= currentColumn && newNode.state[i][j] <= (currentRow+1)*4 && newNode.state[i][j] > currentRow*4){
					stateValue = stateValue + Math.abs(j-(newNode.state[i][j]-1)%4) + Math.abs(i-(newNode.state[i][j]-1)/4);
				}
				else if (currentRow > currentColumn && newNode.state[i][j]%4 - 1 == currentColumn) {
					stateValue = stateValue + Math.abs(j-(newNode.state[i][j]-1)%4) + Math.abs(i-(newNode.state[i][j]-1)/4);
				}
			}
		}
		newItem.stateValue = stateValue;
		
		while (node.next != null){
			if (stateValue < node.next.stateValue){
				newItem.next = node.next;
				node.next = newItem;
				return;
			}
			node = node.next;
		}
		node.next = newItem;
	}
	
	private static String stateToString(int[][] state, int currentRow, int currentColumn) {
		char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		String s = "";
		for (int i = 0; i <= state.length -1; i++) {
			for (int j = 0; j <= state[i].length - 1; j++) {
				if (state[i][j] <= (currentRow+1)*4 || (state[i][j]-1)%4 <= currentColumn)
					s = s + hex[state[i][j]];
				else{
					s = s + 'X';
				}
			}
		}
		return s;
	}
	
	private static int[][] arrayCopy(int[][] array){
		int[][] newArray = new int[array.length][array[0].length];
		for (int i = 0; i < array.length; i++){
			for (int j = 0; j < array[i].length; j++){
				newArray[i][j] = array[i][j];
			}
		}
		return newArray;
	}
	
	private static boolean solvedRowAndColumn(int[][] state, int[][] solution, int row, int column){
		for (int j = 0; j < state[row].length; j++){
			if (state[row][j] != solution[row][j]){
				return false;
			}
		}
		if (column < 0) return true;
		for (int i = 0; i < state.length; i++){
			if (state[i][column] != solution[i][column]){
				return false;
			}
		}
		return true;
	}
	
	
	public static void printContents(int[][] array){
		System.out.print("{");
		for (int i = 0; i < array.length; i++){
			System.out.print("{");
			for (int j = 0; j < array[i].length; j++){
				System.out.print("'" + array[i][j] + "'");
				if (j < array[i].length-1){
					System.out.print(", ");
				}
			}
			System.out.println("}");
		}
		System.out.println("}");
	}
	
	private static class GameState{
		public int[][] state;
		public GameState parent;
		public String dir;
		public int target;
		public boolean end = false;
	}

	private static class StateQueueNode{
		public GameState value;
		public int stateValue;
		public StateQueueNode next;
	}

	private static class VisitedListNode{
		public String value;
		public VisitedListNode next;
	}
}