# 2021 - Qualifying Round - Problem 5 - Cheating Detection
## Important info:
- 100 players
- 10,000 trivia questions
- Each question has a difficulty, Q, and each player has a skill level, S, both are in the range -3 and 3
- Probability player answers a question correctly is `1/(1 + e^-(S-Q))`
    - This is the sigmoid function
    - e ~ 2.718
- One player is a cheater
    - They have a 50% chance to cheat on any given question
    - If they cheat, they will get it right. Otherwise, they will answer as normal
- Goal: find the cheater in at least P% of test cases
- Input:
    - Test cases
    - P
    - 100 lines representing 100 players, each line is 10,000 1s or 0s indicating whether the player got the question right or wrong


## Musings:
Sort players by number of correct answers and choose 5 high, mid, low scores to analyse first
Make an assumption about each group's skill and try to work out question difficulty from that

Sort the players by number of correct answers and split them into 7 levels, giving them the assumed skill levels of -3 for the worst group, then -2, -1, 0, 1, 2, and 3
- Split them into too many levels, and you can't average their scores, there's too much noise
- Split them into too few levels, and it's harder to work out the thresholds
Try to work out question difficulties

If a player has skill level -3
- p(q = 3) = 0
- p(q = 2) = 0.01
- p(q = 1) = 0.02
- p(q = 0) = 0.05
- p(q = -1) = 0.12
- p(q = -2) = 0.27
- p(q = -3) = 0.5

If a player has skill level 3
- p(q = 3) = 0.5
- p(q = 2) = 0.73
- p(q = 1) = 0.88
- p(q = 0) = 0.95
- p(q = -1) = 0.98
- p(q = -2) = 0.99
- p(q = -3) = 1