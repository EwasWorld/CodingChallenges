# 2021 - Qualifying Round - Problem 3 - Reversort Engineering 
## Important Info
```
Reversort(L):
  for i := 1 to length(L) - 1
    j := position with the minimum value in L between i and length(L), inclusive
    Reverse(L[i..j])
```
- Cost of one iteration is the length of the sublist passed to Reverse

Example of Reversort([4,2,1,3]):
1. i=1, j=3 -> [1,2,4,3]
2. i=2, j=2 -> [1,2,4,3]
3. i=3, j=4 -> [1,2,3,4]

- Input: list size, N, and a target cost, C
- Output: a list, L, of space-separated integers of size N where the cost of Reversort(L) is C


## Musings