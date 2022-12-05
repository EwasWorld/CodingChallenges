# 
# Copies src/PuzzleSolvers/PuzzleTemplate.hs to make a new puzzle solver
# Adds the new puzzle solver to app/Main.hs
# Creates a blank file in res/actual and res/test ready to paste the input into
# 
# Input: The puzzle number (must not be used already)
# 

$puzzleNumber=$args[0]

if ([string]::IsNullOrWhiteSpace($puzzleNumber))
{
   write-host "Error: must give the puzzle number as the first arg"
   exit 2
}

if (test-path -Path "src/PuzzleSolvers/Puzzle$puzzleNumber.hs")
{
   write-host "Error: puzzle $puzzleNumber already exists"
   exit 3
}


# Create the input files
New-Item "res/actual/puzzle$puzzleNumber.txt" | Out-Null
New-Item "res/test/puzzle$puzzleNumber.txt" | Out-Null

# Create the puzzleSolver's hs file
$newHsFilename="src/PuzzleSolvers/Puzzle$puzzleNumber.hs"
Copy-Item "src/PuzzleSolvers/PuzzleTemplate.hs" -Destination $newHsFilename
(Get-Content -Path ".\$newHsFilename").replace("module PuzzleSolvers.PuzzleTemplate () where", '') | Set-Content .\$newHsFilename
(Get-Content -Path ".\$newHsFilename").replace('-- ', '') | Set-Content .\$newHsFilename
(Get-Content -Path ".\$newHsFilename").replace('<NUMBER>', $puzzleNumber) | Set-Content .\$newHsFilename

# Update Main.hs
$mainFilename="app/Main.hs"
(Get-Content -Path ".\$mainFilename").replace('-- import PuzzleSolvers.Puzzle<NUMBER>', "import PuzzleSolvers.Puzzle<NUMBER>`r`n-- import PuzzleSolvers.Puzzle<NUM_BER>") | Set-Content .\$mainFilename
(Get-Content -Path ".\$mainFilename").replace('      -- , (<NUMBER>, solvePuzzle<NUMBER>)', "      , (<NUMBER>, solvePuzzle<NUMBER>)`r`n      -- , (<NUM_BER>, solvePuzzle<NUM_BER>)") | Set-Content .\$mainFilename
(Get-Content -Path ".\$mainFilename").replace('<NUMBER>', $puzzleNumber) | Set-Content .\$mainFilename
(Get-Content -Path ".\$mainFilename").replace('<NUM_BER>', '<NUMBER>') | Set-Content .\$mainFilename
