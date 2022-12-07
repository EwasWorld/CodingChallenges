module PuzzleSolvers.Puzzle9
      ( solvePuzzle9
      ) where

import Data.List.Split (splitOn)

solvePuzzle9 :: Int -> String -> String
solvePuzzle9 part input
   | part == 1 = "Not implemented puzzle solver 9"
   | part == 2 = "Not implemented puzzle solver 9"
   | otherwise = error "Unsupported puzzle 9 part"
   where splitInput = [splitOn "" line | line <- splitOn "" input]
