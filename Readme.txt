Assignment Prompt:

The book Computer Algorithms by Horowitz, Sahni and Rajasekaran states a string editing algorithm in section 5.6 to transform string X = x1 x2 ... xn into Y = y1 y2 ... ym, using edit operations of I (Insert), D (Delete) and C (Change).
Use the costs of 0.5, 0.4, 1.2 for I (Insert), D (Delete) and C (Change), respectively. In the algorithm, C(xi,yj) is applied with its assigned cost if xi is not equal to yj; if xi equals yj then the cost C(xi,yj) = 0.
Implement the algorithm to generate the cost matrix and decision sequence. Test your programs using the example from the slides used in class and provided input files.
Read in your test data from the files.
Show final cost(n,m) and the decision sequence for all tests; show input sequences, matrix and decision sequence only when the input sequences are of length <= 10.