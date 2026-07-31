# Results

(The following results are averages over a fixed set of 20 random solvable puzzle states)

|Version|Runtime|Solution length|States explored|Unique states queued|Visited state tracking|Priority queue|State value calculation|
|-|-|-|-|-|-|-|-|
|**1**|4995.7ms|110.5 moves|18554.3|26687.35|Linked list|Linked list|Old heuristic, no path cost|
|**2**|218.0ms|110.5 moves|18554.3|26687.35|HashSet|Linked list|Old heuristic, no path cost|
|**2b**|1.5ms|115.6 moves|812.05|1213.55|HashSet|Linked list|Corrected heuristic, no path cost|
|**3**|1.3ms|125.3 moves|616.1|916.1|HashSet|Min heap|Corrected heuristic, no path cost|
|**Current**|8.25ms|72.3 moves|3403.75|5223.15|HashSet|Min heap|Corrected heuristic, path cost included|

# Observations
### Heuristic bug fix (_v2_ -> _v2b_)

After running some initial performance tests on versions 1, 2, and 3, I noticed that the performance difference between _v2_ and _v3_ was too large to be explained by the priority queue implementation alone. Changing the implementation of my priority queue should not have been able to cause a ×23 decrease in the average number of states explored.

After reviewing _v2_'s code, I realized that I had made a mistake with the row/column indexing used in the heuristic calculation, causing the search to explore far more states than necessary. This bug was unintentionally fixed in _v3_ while improving the readability of its code. Fixing this bug in _v2b_ reduced its runtime dramatically.

*Note: v2b is not included in this repository's commit history, having only been used in performance testing*

### Priority Queue Implementation (_v2b_ -> _v3_)

Replacing the sorted linked list with a min heap had less impact on runtime than I expected. Below is a possible explanation:

Theoretically, a sorted linked list has worst-case ```O(n)``` insertion. However in _v2b_ and _v3_ (versions using the corrected heuristic without considering path cost) parent states can only generate children whose values differ from its own by a maximum of 1. Since child states always have values very close to their parent states and parent states are already at the front of the sorted list, new insertions usually appear closer to the beginning of the list.

Meanwhile, a min heap always inserts new states at the bottom before restoring heap order. In _v3_ of this program, this likely required a large number of swaps because the new states generated always had a relatively high priority (low value).

Additional note: Changing the priority queue implementation also changed tiebreaking behavior, which happened to cause less states to be explored on average in _v3_ than in _v2_.