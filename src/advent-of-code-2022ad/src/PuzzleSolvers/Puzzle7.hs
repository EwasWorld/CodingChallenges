module PuzzleSolvers.Puzzle7
      ( solvePuzzle7
      ) where

import Utils (replaceAt)
import Data.List (sort,findIndex)
import Data.Maybe (isNothing)



totalSpace :: Int
totalSpace = 70000000

neededSpace :: Int
neededSpace = 30000000

type Pwd = [String]

data Input = Command Command | Node Node

data Command = CdTop | CdUp | Cd String | Ls deriving (Show, Eq)

data Node = File Int String | Dir String [Node] deriving (Eq)
instance Show Node where
   show node = unlines $ buildShowNode node
      where buildShowNode :: Node -> [String]
            buildShowNode (File size name) = [name ++ " " ++ (show size)]
            buildShowNode (Dir name nodes) = (name:nodeList)
                  where nodeList = map ((++) "|   ") . concat $ map buildShowNode nodes

data Puzzle7Error = InvalidCommand deriving (Show, Eq)


solvePuzzle7 :: Int -> String -> String
solvePuzzle7 part input
   | part == 1 = show $ sumSizesUnder100k tree
   | part == 2 = show $ findDeleteFile tree
   | otherwise = error "Unsupported puzzle 7 part"
   where parsedInput = destroyEither . sequence . (map parseLine) $ lines input
         root = ([],(Dir "/" []))
         (_,tree) = buildTree parsedInput root



destroyEither :: (Show a) => Either a b -> b
destroyEither (Right x) = x
destroyEither (Left x) = error (show x)

parseLine :: String -> Either Puzzle7Error Input
parseLine line
   | line == "$ cd /" = Right (Command CdTop)
   | line == "$ cd .." = Right (Command CdUp)
   | line == "$ ls" = Right (Command Ls)
   | (splitLine !! 1) == "cd" = Right (Command (Cd (splitLine !! 2)))
   | (head splitLine) == "dir" = Right (Node (Dir (last splitLine) []))
   | otherwise = Right (Node (File (read (head splitLine)) (last splitLine)))
   where splitLine = words line



buildTree :: [Input] -> (Pwd,Node) -> (Pwd,Node)
buildTree [] tree = tree
buildTree ((Node _):_) _ = error "Invalid"
buildTree ((Command CdTop):is) (_,node) = buildTree is ([],node)
buildTree ((Command CdUp):is) (pwd,node) = buildTree is ((init pwd),node)
buildTree ((Command (Cd dir)):is) (pwd,node) = buildTree is ((pwd ++ [dir]),node)
buildTree ((Command Ls):is) tree = buildTree inputs $ addNode tree (asNodes files)
   where (files,inputs) = span isNode is

isNode :: Input -> Bool
isNode (Node _) = True
isNode _ = False

asNodes :: [Input] -> [Node]
asNodes [] = []
asNodes ((Node x):xs) = x : asNodes xs
asNodes _ = error "Invalid"



addNode :: (Pwd,Node) -> [Node] -> (Pwd,Node)
addNode ([],(Dir name nodes)) new = ( [] , (Dir name (new ++ nodes)) )
addNode ([],_) _ = error "Cannot add to a non-dir"
addNode (pwd@(x:xs),(Dir name dirNodes)) new
   | isNothing newNodeList = error ("Can't find directory: " ++ x)
   | otherwise = ( pwd , (Dir name (destroyMaybe newNodeList)) )
   where newNodeList = do 
               nodeIndex <- findIndex (isNamedDir x) dirNodes
               let (_,newNode) = addNode (xs,(dirNodes !! nodeIndex)) new
               pure (replaceAt nodeIndex newNode dirNodes)
addNode _ _ = error "Pwd is invalid"

destroyMaybe :: Maybe a -> a
destroyMaybe Nothing = error "The maybe destroyed you"
destroyMaybe (Just x) = x

isNamedDir :: String -> Node -> Bool
isNamedDir search (Dir name _) = name == search
isNamedDir _ _ = False




-- 
-- Sum of the size of all directories smaller than 100,000
-- 
sumSizesUnder100k :: Node -> Int
sumSizesUnder100k tree = sum $ filter (<= 100000) sizes
   where (sizes,_) = getAllDirSizes tree

-- 
-- Param: rootNode
-- 
-- (free space) = [totalSpace] - (size of [rootNode])
-- Required space = [neededSpace] - (free space)
-- 
-- Returns: The smallest file that is larger than the required space
-- 
findDeleteFile :: Node -> Int
findDeleteFile tree = head . dropWhile ((>) requiredSpace) $ sortedSizes
   where (allSizes,_) = getAllDirSizes tree
         sortedSizes = sort allSizes
         freeSpace = totalSpace - (last sortedSizes)
         requiredSpace = neededSpace - freeSpace

-- 
-- Recursively search (param) for all directories and return their sizes
-- Returns (allSizes,inputNodeSize)
-- 
getAllDirSizes :: Node -> ([Int],Int)
getAllDirSizes (File size _) = ([],size)
getAllDirSizes (Dir _ nodes) = ( size:(concat sizes) , size )
   where (sizes,total) = unzip $ map getAllDirSizes nodes
         size = sum total
