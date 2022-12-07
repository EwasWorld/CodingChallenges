module PuzzleSolvers.Puzzle8
      ( solvePuzzle8
      ) where

import Data.List.Split (splitOn)

solvePuzzle8 :: Int -> String -> String
solvePuzzle8 part input
   | part == 1 = "Not implemented puzzle solver 8"
   | part == 2 = "Not implemented puzzle solver 8"
   | otherwise = error "Unsupported puzzle 8 part"
   where splitInput = [splitOn "" line | line <- splitOn "" input]
