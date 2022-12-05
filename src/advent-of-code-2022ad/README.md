# advent-of-code-2022
Taking part in [Advent of Code](https://adventofcode.com/2022) in Haskell! An advent calendar of coding puzzles :)

## File Structure
### /app/Main.hs
Read inputs and execute the correct PuzzleSolver

### /res
Input files. Each puzzle's input should be in the form `puzzleX.txt` where X is the puzzle number.
- `/actual` is for the actual input
- `/test` is for the examples given in the problem. This version will be used when `-d` flag is given as an argument


### src/PuzzleSolvers
Solutions to each problem. Any with the `Final` suffix contain tweaks and notes I took after looking at other people's solutions.

### Scripts
#### run.bat
- Builds and runs the project (because apparently I'm too lazy to type two commands)

Contents (see comment on main in /app/Main.hs for args):
```batch
stack build
stack exec advent-of-code-2022ad-exe -- %*
```

#### newPuzzleSolver.bat
- Copies `src/PuzzleSolvers/PuzzleTemplate.hs` to make a new puzzle solver
- Adds the new puzzle solver to `app/Main.hs`
- Creates a blank file in `res/actual` and `res/test` ready to paste the input into
- Opens the newly created puzzle solver file in VS Code
