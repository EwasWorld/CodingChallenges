# 2021 - Qualifying Round - Problem 2 - Moons and Umbrellas 
## Important Info
- An art piece consists of a list of Js and Cs
- Every CJ has cost X and every JC has cost Y
  - Extension: costs can be negative (profitable)
- Some letters in the piece have not yet been decided, complete them with minimal cost


- Input: X Y <art piece - a string of Js, Cs, and ?s>
- Output: a minimal-cost string of Js and Cs (replace all ?s with a letter)


## Musings
- For ?s at the start of the piece
    - If both costs are positive
        - Duplicate the last item
    - Else if there's just one element to assign
        - If both costs are negative
            - Alternate
        - Else
            - Make the negative-cost pair or no pair
    - Else if both costs are negative
        - Alternate
    - Else if abs(negative cost) >= positive cost
        - Alternate making sure to end on a negative
    - Else
        - Make a single negative-cost pair or no pair
- For ?s in the middle of a piece
    - If letters before and after ?s are not identical
        - Make first ? the opposite of the first letter (will have to make at least this pair anyway, can fill the rest with identical letters to prevent further pairs)
    - If JC cost + CJ cost > 0
        - Alternate letters
    - Else
        - Duplicate letters
