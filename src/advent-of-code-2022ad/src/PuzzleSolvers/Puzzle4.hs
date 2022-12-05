module PuzzleSolvers.Puzzle4
      ( solvePuzzle4
      ) where

import Data.List.Split (splitOn)

-- 
-- Each line represents the jobs of two people (separated by a comma)
-- The jobs are given as a range of indexes (3-5 means jobs 3, 4, and 5)
-- Example line: "1-3,2-7"
-- 
-- Part 1: in how many pairs do one person's jobs completely encompass their partner's?
-- Part 2: in how many pairs do the two people's jobs partially overlap?
-- 
-- In the example line above, these jobs only partially overlap (sharing jobs 2 and 3)
-- 
solvePuzzle4 :: Int -> String -> String
solvePuzzle4 part input
   | part == 1 = execute doesFullyContain
   | part == 2 = execute doesPartiallyContain
   | otherwise = error "Unsupported puzzle 4 part"
   where execute f = show . sum $ map f splitInput
         splitInput = [ splitMap "," (splitMap "-" read) line | line <- (splitOn "\n" input)] 
         splitMap split f = (map f) . splitOn split

doesFullyContain :: [[Int]] -> Int
doesFullyContain [ [a1,b1] , [a2,b2] ]
   | a1 <= a2 && b1 >= b2 = 1
   | a2 <= a1 && b2 >= b1 = 1
   | otherwise = 0
doesFullyContain _ = error "Error in doesFullyContain: invalid list"

doesPartiallyContain :: [[Int]] -> Int
doesPartiallyContain [ [a1,b1] , [a2,b2] ]
   | b1 < a2 || a1 > b2 = 0
   | otherwise = 1
doesPartiallyContain _ = error "Error in doesPartiallyContain: invalid list"
