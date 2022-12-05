module PuzzleSolvers.Puzzle5
      ( solvePuzzle5
      ) where

import Data.List (transpose)
import Data.List.Split (splitOn, chunksOf)

-- Move (number of crates) from (stack index) to (stack index)
data MoveInstr = Instr Int Int Int

type Stack = [Char]


-- 
-- Execute move instructions on a stack of crates
-- 
-- Inputs: see parseStacks and parseMoveInstr (separated by an empty line)
-- Output: the item on the top of each stack of crates 
--       after the move instructions have been applied to the stack of crates
-- 
-- Part 1: crates are moved one at a time from one stack to another (ending up in reverse order on the destination)
-- Part 2: crates are moved all at once from one stack to another (ending up in the same order on the destination)
-- 
solvePuzzle5 :: Int -> String -> String
solvePuzzle5 part input
   | part == 1 = solve reverse
   | part == 2 = solve (\x -> x)
   | otherwise = error "Unsupported puzzle 5 part"
   where sections = splitOn "\n\n" input
         moveInstrs = map parseMoveInstr . splitOn "\n" $ sections !! 1
         initialStacks = parseStacks $ sections !! 0
         solve f = map head $ resolveMoveInstrs f moveInstrs initialStacks



-- 
-- Example input: "move 1 from 2 to 1"
-- Example output: (MoveInstr 1 1 0)
-- 
-- Extracts how many items to move and from/to which stack indexes
-- Converts stack indexes from 1-indexed to 0-indexed
-- 
parseMoveInstr :: String -> MoveInstr
parseMoveInstr str = Instr (parse 1) (parseDecrement 3) (parseDecrement 5)
   where splitLine = splitOn " " str
         parse = read . (!!) splitLine
         parseDecrement = subtract 1 . parse

-- 
-- Example input:
-- ```
--     [D]
-- [N] [C]
-- [Z] [M] [P]
--  1   2   3 
-- ```
-- Example output: ["NZ","DCM","P"]
-- 
-- Input string is expected in the form given above
--       - Each stack is a column (the last row - the stack's number - is ignored)
--       - Each crate on the stack is represented by a single character
-- Output: A list of stacks
--       - The first stack is the leftmost stack from the input
--       - Each stack is a list of chars representing the crates in the stack
--       - The first item in each output stack is the topmost item from that stack in the input
-- 
parseStacks :: String -> [Stack]
parseStacks str =  splitLetters 
   where splitLines = [ map getSecondElem (chunksOf 4 line) | line <- init (splitOn "\n" str)]
         splitLetters = map (reverse . dropBlanks) . transpose . reverse $ splitLines
         getSecondElem = flip (!!) 1
         dropBlanks [] = []
         dropBlanks (' ':_) = []
         dropBlanks (x:xs) = x : (dropBlanks xs)



-- When a set of crates is moved from one stack to another, (param 1) defines their tranfromation
-- This transformation is usually do nothing or reverse 
-- (if the crates are being moved one at a time, they will end up in the reverse order on the new stack)
resolveMoveInstrs :: (Stack -> Stack) -> [MoveInstr] -> [Stack] -> [Stack]
resolveMoveInstrs _ [] s = s
resolveMoveInstrs f ((Instr number from to):xs) stacks = resolveMoveInstrs f xs newStacks
   where splitFromStack = splitAt number $ stacks !! from
         newFromStack = snd splitFromStack
         newToStack = (f (fst splitFromStack)) ++ (stacks !! to)
         newStacks = replaceAt to newToStack $ replaceAt from newFromStack stacks



replaceAt :: Int -> a -> [a] -> [a]
replaceAt _ _ [] = []
replaceAt idx new xs
    | idx < 0 || idx > (length xs) = xs
    | otherwise = let (left, right) = splitAt idx xs
                  in left ++ [new] ++ (tail right)
