module Main (main) where

import System.Environment (getArgs)
import Data.List (intercalate,nub,find)
import Data.Maybe (isNothing)
import Data.List.Split (splitOn)

import PuzzleSolvers.Puzzle1
import PuzzleSolvers.Puzzle2
import PuzzleSolvers.Puzzle3
import PuzzleSolvers.Puzzle3Final
import PuzzleSolvers.Puzzle4
import PuzzleSolvers.Puzzle5
import PuzzleSolvers.Puzzle5Final
-- import PuzzleSolvers.Puzzle<NUMBER>

-- command line options (see main) are parsed into this form
data RunMode = Debug | Final | All deriving Eq

-- Params: puzzle part and input file contents
-- Returns: solution
type PuzzleSolver = Int -> String -> String

-- Maps the puzzle number to the function that solves it
puzzleSolvers :: [(Int, PuzzleSolver)]  
puzzleSolvers =
      [ (1, solvePuzzle1)
      , (2, solvePuzzle2)
      , (3, solvePuzzle3)
      , (4, solvePuzzle4)
      , (5, solvePuzzle5)
      -- , (<NUMBER>, solvePuzzle<NUMBER>)
      ]

finalPuzzleSolvers :: [(Int, PuzzleSolver)]  
finalPuzzleSolvers =
      [ (3, solvePuzzle3Final)
      , (5, solvePuzzle5Final)
      ]

-- 
-- args: puzzleNumber puzzlePart [options]
-- 
-- puzzleNumber Int: which puzzle to run
-- puzzlePart Int: each puzzle has two parts - specify which to run
-- options:
--      -d: use the input from res\test (otherwise uses res\actual)
--      -f: use the Final version of the puzzle solver
--      -a: runs every puzzle with the actual and test inputs
-- 
main :: IO ()
main = do
   args <- getArgs
   let parseIntArg i = read (args !! i) :: Int
   let modes = (getModes (drop 3 args))
   let singlePuzzle = puzzleSwitcher (parseIntArg 1) (parseIntArg 2) modes
   output <- if elem All modes then runAll else singlePuzzle
   putStrLn output

-- Parse a list of command line args into a list of modes
getModes :: [String] -> [RunMode]
getModes modeArgs
   | modeArgs == [] = []
   | elem "-d" modeArgs = Debug : recurse "-d"
   | elem "-f" modeArgs = Final : recurse "-f"
   | elem "-a" modeArgs = All : recurse "-a"
   | otherwise = []
   where recurse arg = getModes [ x | x <- modeArgs, x /= arg ]

-- Reads the given puzzle's input file and returns the solution
puzzleSwitcher :: Int -> Int -> [RunMode] -> IO String
puzzleSwitcher puzzle part modes = do
      let useFinalSolver = elem Final modes
      let solvers = if useFinalSolver then finalPuzzleSolvers else puzzleSolvers
      let isTest = elem Debug modes
      inputFile <- readFile $ getInputFile isTest puzzle
      let solve Nothing
            | useFinalSolver = error "Puzzle does not have a final solver"
            | otherwise = error "Puzzle not found"
          solve (Just puzzleSolver) = puzzleSolver part inputFile
      let myAnswer = solve (lookup puzzle solvers)
      actualAnswer <- getSolution puzzle part isTest
      let comparison = if myAnswer == actualAnswer then "THEY MATCH!" else "They do not match :("
      pure $ intercalate "\n" [myAnswer,actualAnswer,comparison]


-- Solve parts 1 and 2 and concatenate them (empty string on a Nothing PuzzleSolver)
solveParts :: String -> Maybe PuzzleSolver -> String
solveParts _ Nothing = ""
solveParts input (Just solver) = intercalate "  -  " solutions
   where solutions = map (flip solver input) [1,2]


-- Solve all parts for all inputs and all given puzzle solvers
solvePuzzle :: String -> String -> Maybe PuzzleSolver -> Maybe PuzzleSolver -> String
solvePuzzle actualInput testInput mainSolver finalSolver
   | isNothing finalSolver = joinList [outputs1,outputs3]
   | otherwise = joinList [outputs1,outputs2,outputs3,outputs4]
   where outputs1 = "Test-Main:  " ++ solveParts testInput mainSolver
         outputs2 = "    Final:  " ++ solveParts testInput finalSolver
         outputs3 = "Actu-Main:  " ++ solveParts actualInput mainSolver
         outputs4 = "    Final:  " ++ solveParts actualInput finalSolver
         joinList = intercalate "\n" . map ((++) "   ")


solveAll :: [Int] -> [String] -> [String] -> [Maybe PuzzleSolver] -> [Maybe PuzzleSolver] -> String
solveAll [] _ _ _ _ = ""
solveAll _ [] _ _ _ = ""
solveAll _ _ [] _ _ = ""
solveAll _ _ _ [] _ = ""
solveAll _ _ _ _ [] = ""
solveAll (puzzle:ps) (actualInput:as) (testInput:ts) (mainSolver:ms) (finalSolver:fs) 
   | next == "" = result
   | otherwise = result ++ "\n\n" ++ next
   where title = "Puzzle " ++ (show puzzle) ++ "\n"
         result = (++) title $ solvePuzzle actualInput testInput mainSolver finalSolver
         next = solveAll ps as ts ms fs


runAll :: IO String
runAll = do
      let allPuzzleNumbers = nub $ map fst (puzzleSolvers ++ finalPuzzleSolvers)
      let mainSolvers = map (flip lookup puzzleSolvers) allPuzzleNumbers
      let finalSolvers = map (flip lookup finalPuzzleSolvers) allPuzzleNumbers
      let getInputs isTest = sequence $ map (readFile . (getInputFile isTest)) allPuzzleNumbers
      testInputs <- getInputs True
      actualInputs <- getInputs False
      let output = solveAll allPuzzleNumbers actualInputs testInputs mainSolvers finalSolvers
      pure output


getInputFile :: Bool -> Int -> String
getInputFile isTest puzzle = "res\\" 
   ++ (if isTest then "test" else "actual")
   ++ "\\puzzle" ++ (show puzzle) ++ ".txt"


getSolution' :: Maybe [String] -> Int -> Bool -> String
getSolution' Nothing _ _ = "No solution found"
getSolution' (Just [ _, test1, actual1, test2, actual2 ]) part isTest
   | part /= 1 && part /= 2 = "Invalid part: " ++ (show part)
   | part == 1 && isTest = test1
   | part == 1 = actual1
   | isTest = test2
   | otherwise = actual2
getSolution' _ _ _ = "getSolution' unexpected pattern"


getSolution :: Int -> Int -> Bool -> IO String
getSolution puzzle part isTest = do
      solutionContents <- fmap (map (splitOn ":") . (splitOn "\n")) $ readFile "res\\solutions.txt"
      let solutionLine = find ((== (show puzzle)) . head) solutionContents
      pure $ getSolution' solutionLine part isTest
