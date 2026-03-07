# SlidingBlockPuzzleSolver
Solves a 4x4 sliding block puzzle.

The algorithm focuses on one row or column at a time. After a row or column has been solved, the algorithm runs recursively on the remaining unsolved portion of the puzzle.
States are explored using a priority queue ordered by the Manhattan distance of relevant tiles. Visited states are stored to prevent the algorithm from revisiting previously explored configurations.

The board configuration is read from the user through console.
Example:
    2 8 11 4
    6 1 5 0
    3 7 14 15
    10 9 12 13
The input is assumed to be solvable and correctly formatted.

(Originally implemented November 2025; documented and revised in March 2026)