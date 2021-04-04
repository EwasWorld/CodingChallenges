2021 Problem 1 - Reversort

# Important info:
```
Reversort(L):
  for i := 1 to length(L) - 1
    j := position with the minimum value in L between i and length(L), inclusive
    Reverse(L[i..j])
```
Cost of one iteration is the length of the sublist passed to Reverse

Example of Reversort([4,2,1,3]):
1. i=1, j=3 -> [1,2,4,3]
2. i=2, j=2 -> [1,2,4,3]
3. i=3, j=4 -> [1,2,3,4]

Input: list size on one line followed by the list (items are separated by a space). All items in list are distinct
Output: cost to sort the list


# Musings
Example list: 4213
1 is smallest so reverse: [421]3 (becomes 1243)