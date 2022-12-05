module PuzzleSolvers.Puzzle2
      ( solvePuzzle2
      ) where

import Data.List.Split (splitOn)

data RpsChoice = Rock | Paper | Scissors deriving Eq
data RpsResult = Won | Lost | Draw deriving Eq


-- See explaination calculateScore and calculateScorePart2 for descriptions
solvePuzzle2 :: Int -> String -> String
solvePuzzle2 part input
   | part == 1 = show (calculateScore splitInput)
   | part == 2 = show (calculateScorePart2 splitInput)
   | otherwise = error "Unsupported puzzle 2 part"
   where parseAbcChoice choice = case choice of
            'A' -> Rock
            'B' -> Paper
            'C' -> Scissors
            _ -> error "Unsupported input for parseAbcChoice"
         parseMatch line = (parseAbcChoice (line !! 0), line !! 2)
         splitInput = [parseMatch line | line <- splitOn "\n" input]

-- 
-- Each item in (param 1) is a Rock Paper Scissors match
-- Each match is (what the opponent played, what you played)
-- You play X/Y/Z for Rock/Paper/Scissors respectively
-- You get a score for each match depending on what you played and the outcome (see getFinalScore)
-- 
-- Returns: your total score across all games
-- 
calculateScore :: [(RpsChoice, Char)] -> Int
calculateScore [] = 0
calculateScore ((theirChoice,x):xs) = (getFinalScore result myChoice) + calculateScore xs
   where myChoice = case x of
            'X' -> Rock
            'Y' -> Paper
            'Z' -> Scissors
            _ -> error "Unsupported myChoice input for calculateScore"
         result
            | theirChoice == myChoice = Draw
            | myChoice == (getWinningThrow theirChoice) = Won
            | otherwise = Lost


-- 
-- Same as calculateScore except:
-- Each match is (what the opponent played, THE RESULT OF THE MATCH)
-- The result is X/Y/Z for Lost/Draw/Won respectively
-- 
calculateScorePart2 :: [(RpsChoice, Char)] -> Int
calculateScorePart2 [] = 0
calculateScorePart2 ((theirChoice,x):xs) = (getFinalScore result myChoice) + calculateScorePart2 xs
   where result = case x of
            'X' -> Lost
            'Y' -> Draw
            'Z' -> Won
            _ -> error "Unsupported result input for calculateScorePart2"
         myChoice = case result of
            Won -> getWinningThrow theirChoice
            Lost -> getLosingThrow theirChoice
            Draw -> theirChoice


getWinningThrow :: RpsChoice -> RpsChoice
getWinningThrow Rock = Paper
getWinningThrow Paper = Scissors
getWinningThrow Scissors = Rock

getLosingThrow :: RpsChoice -> RpsChoice
getLosingThrow Rock = Scissors
getLosingThrow Paper = Rock
getLosingThrow Scissors = Paper

-- Won 6, Draw 3, Lost 0
-- Rock 1, Paper 2, Scissors 3
getFinalScore :: RpsResult -> RpsChoice -> Int
getFinalScore result choice = resultScore + choiceScore
   where resultScore = case result of
            Won -> 6
            Draw -> 3
            Lost -> 0
         choiceScore = case choice of
            Rock -> 1
            Paper -> 2
            Scissors -> 3
