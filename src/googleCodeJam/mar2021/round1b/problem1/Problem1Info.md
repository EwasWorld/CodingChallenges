# 2021 - Round 1B - Problem 1 - Broken Clock
## Important Info
- There is a clock with 3 hands: hours, minutes, and seconds
- The clock hands are identical making it impossible to tell which is which
- There are no numbers on the clock, meaning it could be in any rotational position
- The clock is accurate to the nanosecond  


- Input:
    - A, B, and C: the angles of the hands relative to an arbitrary axis given in ticks in the clockwise direction
    - 1 tick = 1/12 * 10^-10 degrees
        - Hour hand moves 1 tick every nanosecond
        - Minute hand moves 12 tick every nanosecond
        - Second hand moves 720 tick every nanosecond
- Output: the time shown on the clock
    - Format: hours (0 to 11), minutes (0 to 59), seconds (0 to 59), and nanoseconds (0 to 10^9-1) as a space-separated list
    - midnight <= time on the clock < noon
- Test Sets
    1. Time is an integer number of seconds since midnight (no nanoseconds), and the clock can be read without rotating
    2. Time is an integer number of seconds since midnight (no nanoseconds)
    3. No constraints
