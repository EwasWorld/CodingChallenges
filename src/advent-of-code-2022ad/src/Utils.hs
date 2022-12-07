module Utils
    ( replaceAt
    ) where

replaceAt :: Int -> a -> [a] -> [a]
replaceAt _ _ [] = []
replaceAt idx new xs
    | idx < 0 || idx > (length xs) = xs
    | otherwise = let (left, right) = splitAt idx xs
                  in left ++ [new] ++ (tail right)
