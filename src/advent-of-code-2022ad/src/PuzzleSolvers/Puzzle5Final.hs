module PuzzleSolvers.Puzzle5Final
      ( solvePuzzle5Final
      ) where

import Data.Char (isSpace)
import Data.List (transpose)
import Data.List.Split (splitOn, chunksOf)

-- Notes:
-- 
-- Can use `id` for (\x -> x)
-- Now that when parsing the crates input the stack is being reversed, can dropWhile to dump the preceeding spaces
--       (probably could have used take while when it wasn't being reversed)
-- Char has an `isSpace` function - it's slightly nicer than (== ' ')
-- Can unpack a pair like in Kotlin `( cratesInput , moveInstrInput ) = (a,b)`
-- `span` splits a list in two. Splits at the first element that doesn't satisfy the predicate 
-- 


-- Move (number of crates) from (stack index) to (stack index)
data MoveInstr = Instr Int Int Int deriving Show

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
solvePuzzle5Final :: Int -> String -> String
solvePuzzle5Final part input
   | part == 1 = solve reverse
   | part == 2 = solve id
   | otherwise = error "Unsupported puzzle 5 part"
   where ( cratesInput , moveInstrInput ) = span (/= "") $ splitOn "\n" input
         moveInstrs = map parseMoveInstr $ tail moveInstrInput
         initialStacks = parseStacks cratesInput
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
parseStacks :: [String] -> [Stack]
parseStacks str =  splitLetters 
   where splitLines = [ map getSecondElem (chunksOf 4 line) | line <- init str]
         splitLetters = map (dropWhile isSpace . reverse) . transpose . reverse $ splitLines
         getSecondElem = flip (!!) 1



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
