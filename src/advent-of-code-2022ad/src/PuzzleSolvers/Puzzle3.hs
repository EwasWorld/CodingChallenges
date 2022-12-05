module PuzzleSolvers.Puzzle3
      ( solvePuzzle3
      ) where

import Data.List.Split (splitOn)
import Data.List (group,sort)
import Data.Char (ord)



-- See explaination part1 and part2 for descriptions
solvePuzzle3 :: Int -> String -> String
solvePuzzle3 part input
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
part1 xs = sum $ map (sumPriorities . removeDuplicates . getCharsInBothSides . splitInHalf) xs
   where removeDuplicates = map head . group . sort
         splitInHalf str = let len = div (length str) 2 in ((take len str), (drop len str))
         getCharsInBothSides (x,y) = findAllCharsInBothSides (sort x) (sort y)
         sumPriorities = sum . map asPriority

findAllCharsInBothSides :: String -> String -> [Char]
findAllCharsInBothSides [] _ = []
findAllCharsInBothSides _ [] = []
findAllCharsInBothSides one@(x:xs) two@(y:ys)
   | x == y = x : (findAllCharsInBothSides xs ys)
   | otherwise = findAllCharsInBothSides (takeUntilLargest one) (takeUntilLargest two)
   where largest = max x y
         takeUntilLargest = takeUntil largest



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
   where commonLetter = findCommonLetter (sort x) (sort y) (sort z)
part2 _ = error "Number of lines in input must be divisible by 3"

findCommonLetter :: String -> String -> String -> Char
findCommonLetter [] _ _ = error "No common character"
findCommonLetter _ [] _ = error "No common character"
findCommonLetter _ _ [] = error "No common character"
findCommonLetter one@(x:_) two@(y:_) three@(z:_)
   | x == y && x == z = x
   | otherwise = findCommonLetter (takeUntilLargest one) (takeUntilLargest two) (takeUntilLargest three)
   where largest = maximum [x,y,z]
         takeUntilLargest = takeUntil largest



takeUntil :: Char -> String -> String
takeUntil _ [] = []
takeUntil x (y:ys) = if x <= y then (y:ys) else takeUntil x ys

asPriority :: Char -> Int
asPriority x
   | ascii >= ord 'a' && ascii <= ord 'z' = ascii + 1 - ord 'a'
   | ascii >= ord 'A' && ascii <= ord 'Z' = ascii + 1 + 26 - ord 'A'
   | otherwise = error "Unsupported char to priority"
   where ascii = ord x
