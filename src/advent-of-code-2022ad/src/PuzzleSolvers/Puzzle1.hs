module PuzzleSolvers.Puzzle1
      ( solvePuzzle1
      ) where

import Data.List.Split (splitOn)
import Data.List (sort)

-- 
-- Each person is carrying snacks which each have a certain number of calories
-- Each person's snacks are given as a single number (the calories) per line
-- People are separated by an empty line
-- 
-- Part 1: find the total calories carried by the person with the most calories
-- Part 2: find the sum carried calories of the 3 people with the most calories
-- 
solvePuzzle1 :: Int -> String -> String
solvePuzzle1 part input
   | part == 1 = calculateTotal 1
   | part == 2 = calculateTotal 3
   | otherwise = error "Unsupported puzzle 1 part"
   where splitInput = [splitOn "\n" line | line <- splitOn "\n\n" input]
         calculateTotal size = show . sum $ getMaxTotals size splitInput []

-- 
-- (param 1) is X
-- Each item in (param 2) is a list of numbers represented as strings
-- Each of these lists are totalled
-- 
-- Returns: The top X totals
-- 
-- (param 3) is the initial list of the top X totals
-- 
getMaxTotals :: Int -> [[String]] -> [Int] -> [Int]
getMaxTotals _ [] maxSeens = maxSeens
getMaxTotals size (x:xs) maxSeens = getMaxTotals size xs $ sort newMaxes
   where newMaxes
            | length maxSeens < size = total:maxSeens
            | otherwise = (max total (head maxSeens)) : (tail maxSeens)
            where total = foldl (\acc a -> acc + (read a :: Int)) 0 x
