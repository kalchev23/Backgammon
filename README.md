# Backgammon

###### Made as a project for the Intelligent Systems course @ FMI, Sofia University, 2025.

## Short description
A console-based Java application which implements 2 AI algorithms: 
* Expectiminimax
* MonteCarloTreeSearch

## Board
&nbsp;`` --------------------------------------------------- ``\
``|12  11  10  9   8   7  |   | 6   5   4   3   2   1 |``\
``|X               O      |   | O                   X |``\
``|X               O      |   | O                   X |``\
``|X               O      |   | O                     |``\
``|X                      |   | O                     |``\
``|X                      |   | O                     |``\
&nbsp;`` ------------------------BAR------------------------- ``\
``|O                      |   | X                     |``\
``|O                      |   | X                     |``\
``|O               X      |   | X                     |``\
``|O               X      |   | X                   O |``\
``|O               X      |   | X                   O |``\
``|13  14  15  16  17  18 |   | 19  20  21  22  23  24|``\
&nbsp;`` --------------------------------------------------- ``\
``Out: Player 1 - 0, Player 2 - 0``

## Testing
Each AI bot is tested against open-source [**GNU Backgammon**](https://www.gnu.org/software/gnubg/) . Communication between them is made through sockets. The setup is quite simple:
1. Run Java app -> *select AI algorithm*
2. Run GNU Backgammon terminal-based (*gnubg-cli*)
3. Set player's socket (*set player 1 external ``<IP>:<PORT>``*)
4. Start new game
