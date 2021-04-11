# 2021
## Qualifying
### Results
- Problem 1: passed 1 of 1 test sets
- Problem 2: passed 2 of 3 test sets
- Problem 3: passed 2 of 2 test sets
- Problem 4: not attempted
- Problem 5: not attempted

### Reflection
- On problem 2, I failed test set 3, the hidden verdict test set. I'd made a mistake in a function that handled an edge case. In my own testing, I hadn't properly tested this edge case
- I probably could have gotten problem 4 working in the time if I hadn't been busy on the second day of the competition
- I don't think I would have solved problem 5 in the time

## Round 1A
- Ran from 1am to 3:30am in my timezone so I didn't participate

### Reflection
- Problem 1 was simple but for the size of the numbers that were being used. I got something working for test set 1 pretty quickly, but ran into issues with test set 2 as numbers could be up to 10^9 and the program is adding digits, so it quickly exceeded the size of Int.MAX_VALUE. I wasn't 100% confident that a Long would be able to hold the values I was ending up with (theoretically up to 10^109 the list began at the max value and I appended an extra digit on every number in a full list) and Doubles, though 64 bits, would have been difficult with how much I was swapping between string and number types. In the end I wrote a function that could compare two strings which contained arbitrary length numerical values.
 