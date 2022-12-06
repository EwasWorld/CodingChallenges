module PuzzleSolvers.Puzzle6
      ( solvePuzzle6
      ) where

import Data.List (intercalate,nub)
import Data.List.Split (splitOn)

-- 
-- Given a string of characters, find the index of the end of the first unique X-character sequence.
-- AKA if the first 4 chars are unique while looking for a 4-char sequence, return 4
-- 
-- Part 1: X = 4
-- Part 2: X = 14
-- 
solvePuzzle6 :: Int -> String -> String
solvePuzzle6 part input
   | part == 1 = solve 4
   | part == 2 = solve 14
   | otherwise = error "Unsupported puzzle 6 part"
   where solve x = intercalate "," . map (show . findUnique x) $ splitOn "\n" input

findUnique :: Int -> String -> Int
findUnique _ "" = error "Start not found"
findUnique size full@(_:xs)
   | hasDup = size
   | otherwise = (+) 1 $ findUnique size xs
   where hasDup = (==) size . length . nub $ take size full
