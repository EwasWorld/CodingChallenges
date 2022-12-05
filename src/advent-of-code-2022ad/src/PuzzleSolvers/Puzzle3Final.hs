module PuzzleSolvers.Puzzle3Final
      ( solvePuzzle3Final
      ) where

import Data.List.Split (splitOn)
import Data.List (intersect)
import Data.Char (ord,isAsciiLower,isAsciiUpper)


-- Notes:
-- 
-- RTQ: part 1: only one item will appear in both halves of the list so can use head instead of a removeDuplicates fn
-- Useful fns:
   -- isAsciiLower,isAsciiUpper: is char upper/lower case
   -- intersect: items that are common to two lists
   -- uncurry: (a -> b -> c) -> (a,b) -> c
      -- e.g. `f (x,y) = g x y` becomes `f x = uncurry g x`
   -- splitAt: Int -> [a] -> ([a],[a])
   -- chunksOf: Int -> [a] -> [[a]]
-- 



-- See explaination part1 and part2 for descriptions
solvePuzzle3Final :: Int -> String -> String
solvePuzzle3Final part input
   | part == 1 = show $ part1 splitInput
   | part == 2 = show $ part2 splitInput
   | otherwise = error "Unsupported puzzle 3 part"
   where splitInput = splitOn "\n" input



-- 
-- Input: 
--    - Each string is a person's rucksack of items (represented by Chars)
--    - Each rucksack has two compartments. The first half of the string is the first compartment
--    - Each item has a numeric priority (see asPriority)
-- 
-- Output: 
--    The sum of priorities of all the items that are in the first and second compartments of a rucksack
-- 
part1 :: [String] -> Int
part1 xs = sum $ map (asPriority . head . getCharsInBothSides . splitInHalf) xs
   where splitInHalf str = splitAt (div (length str) 2) str
         getCharsInBothSides x = uncurry intersect x



-- 
-- Input: 
--    - Each string is a person's rucksack of items (represented by Chars)
--    - Each set of 3 rucksacks is a group of people
--    - In each group of people there is exactly one item that is common to all of them
-- 
-- Output:
--    The sum of the priority each group's common item
-- 
part2 :: [String] -> Int
part2 [] = 0
part2 (x:y:z:as) = (asPriority commonLetter) + (part2 as)
   where commonLetter = head $ intersect z (intersect x y)
part2 _ = error "Number of lines in input must be divisible by 3"



asPriority :: Char -> Int
asPriority x
   | isAsciiLower x = ascii + 1 - ord 'a'
   | isAsciiUpper x = ascii + 1 + 26 - ord 'A'
   | otherwise = error "Unsupported char to priority"
   where ascii = ord x
