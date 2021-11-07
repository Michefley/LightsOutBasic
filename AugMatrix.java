/****************************************************************************
  This class sets up an nxn matrix which will be used to find out
  which vertices are adjacent to one another.
****************************************************************************/
import java.util.Stack;
import java.lang.String;

public class AugMatrix{
  private int [][] matrix;
  private int size;
  private int [] startVec;
  private int [] finishVec;
  private String [] abstractVec;
  private final String [] lettersS = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
    "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
    "y", "z"};
  private final char [] lettersC = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
    'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
    'y', 'z'};
  private String limiter;


/****************************************************************************
  This method is the Constructor for the ADT. Has a parameter the sets the
  size of the of both the matrix and the vector. Then a matrix and vector
  of all zeroes is created.
****************************************************************************/
  public AugMatrix(int s){
    size = s;
    matrix = new int[size][size];
    startVec = new int[size];
    finishVec = new int[size];
    abstractVec = new String[size];
    limiter = "The vertice(s) ";

    for (int q = 0; q < size; q++)
      abstractVec[q] = lettersS[q];
  }

/****************************************************************************
  This is a Mutator method for row operations that subtracts a row from
  another. Takes 2 parameters of type int which tell us which rows we are
  subtracting.The rowChange parameter is the index plus one of the row we
  wish to change. The rowAdd  parameter is the index plus one of the row we
  are subtracting from rowChange. Values are always mod2 because the lights
  can only be on (1) or off (0).
****************************************************************************/
  public void rowReduce(int rowChange, int rowAdd){
    for (int i = 0; i < size; i+=1){
      matrix[rowChange-1][i] = (matrix[rowChange-1][i] + matrix[rowAdd-1][i])%2;
    }
    abstractVec[rowChange-1] = abstractVec[rowChange-1] + "+" +
                               abstractVec[rowAdd-1];
  }

/****************************************************************************
  This is a Mutator method that changes the matrix to tell us which vertices
  each vertex changes. The parameter row describes the vertex thus we must
  subtract 1 to get the index corresponding to that row. The parameter
  changes contains an array with Strings that can be converted to integers
  which tell us which vertices the vertex changes. Again subtracting 1 for
  index equivalence in the matrix. Sets the values to 1 meaning it changes.
****************************************************************************/
  public void setPosMatrix(int col, String [] changes){
      for (int e = 0; e < changes.length; e+=1){
        int changes2 = Integer.parseInt(changes[e]);
        matrix[changes2-1][col-1] = 1;
      }
  }

/****************************************************************************
  This is a Mutator method that changes the vector to tell us if a vertex
  starts in the 'on' state or 'off' state (0 for off and 1 for on). The
  vertex parameter tells us the which vertex we are refering to. The state
  parameter tells us if the vertex starts "on" or "off".
****************************************************************************/
  public void setStateVertices(int vertex, int value){
    startVec[vertex-1] = value%2;
  }

/****************************************************************************
  This is a Mutator method that initializes the limiter that tells the user
  the possible starting a finishing positions. Takes the vector and runs it
  through the abstract vector to find out the finishing vector that produces our
  answers. Uses PostfixEvaluator to help reduce the abstract if needed.
****************************************************************************/
  public void setLimiter(int column){
    for (int a = 0; a < size; a++)
      abstractVec[a] = expressionSimplifyer(abstractVec[a]);

    int count = 0;
    String str = abstractVec[column-1];

    if (str.contains("+")){
      str = str.replaceAll("\\+", "");
    }
    if (str.contains("-")){
      str = str.replaceAll("\\-", "");
    }
    if (str.contains("*")){
      str = str.replaceAll("\\*", "");
    }

    for(int d = 0; d < size; d++){
      if(str.contains(lettersS[d])){
        count++;
        limiter += Integer.toString(d+1) + ", ";
      }
    }

    if (count >= 2){
      limiter += "must all be off or and even amount of them may be on";
    }
    else {
      limiter += "must be off";
    }
  }

/****************************************************************************
  If there are no limitations, this method is called instead of the previous one.
****************************************************************************/
  public void setLimiter(String expression){
    limiter = expression;
  }

/****************************************************************************
  This is a Mutator method that makes the finishVec that is just the startVec
  ran through the abstractVec as if the matrix was augmented by the startVec.
****************************************************************************/
  public void makeFinishVec(){
    String newString = "";

    for (int a = 0; a < size; a++){
      newString = abstractVec[a];
      for(int c = 0; c < size; c++){
        if(newString.contains(lettersS[c])){
          newString = newString.replaceAll(lettersS[c],
          Integer.toString(startVec[c]));
        }
      }
      newString = (infixToPostfix(newString));
      finishVec[a] = (evaluatePostfix(newString))%2;
    }
  }
/****************************************************************************
  This is a Mutator method for row operations that swaps an entire row
  within the matrix and corresponding vector row with another row of the
  matrix and the corresponding vector row. Parameters row1 and row2 are
  the row numbers we are swaping thus we must subtract 1 from both
  to get to the corresponding index.
****************************************************************************/
  public void swapRows(int row1, int row2){
    int tempValue;
    for (int j = 0; j < size; j+=1){
      tempValue = matrix[row1-1][j];
      matrix[row1-1][j] = matrix[row2-1][j];
      matrix[row2-1][j] = tempValue;
    }

    String tempValue2 = abstractVec[row1-1];
    abstractVec[row1-1] = abstractVec[row2-1];
    abstractVec[row2-1] = tempValue2;
  }

/****************************************************************************
  This is an Accessor method to get the size of the matrix and vector.
****************************************************************************/
  public int getSize(){
    return size;
  }

/****************************************************************************
  This is an Accessor method that returns the value at the passed row and
  column positions. Again subtracting 1 to get to corresponding index
  positions.
****************************************************************************/
  public int getValMatrix(int row, int col){
    return matrix[row-1][col-1];
  }

/****************************************************************************
  This is an Accessor method that returns the value at the passed row.
****************************************************************************/
  public int getValVector(int row){
    return finishVec[row-1];
  }

/****************************************************************************
  This is an Accessor method that returns the String that tells the user
  the possible starting and finishing positions.
****************************************************************************/
  public String getLimiter(){
    return limiter;
  }

/****************************************************************************
  This method prints out the Augmented Matrix.
****************************************************************************/
  public String toString(){
    String result = "";

    for (int r = 0; r < size; r+=1){
      for (int c = 0; c < size; c+=1){
        result += " " + matrix[r][c];
      }
      result += " | " + abstractVec[r] + "\n";
    }
    return result;
  }

/****************************************************************************
  Combines like terms and reduces down the coefficients to the proper base value.
****************************************************************************/
  public String expressionSimplifyer(String expression){
    for (int w = 0; w < size; w++){
      int index = 0;
      int count = 0;
      if (expression.contains(lettersS[w])){
        index = expression.indexOf(lettersS[w]) + 1;
        count ++;

        if (expression.indexOf(lettersS[w], index) != -1){

          while (expression.indexOf(lettersS[w], index) < expression.length() &
            expression.indexOf(lettersS[w], index) >= 0){
              index = expression.indexOf(lettersS[w], index) + 1;
              if (index == 0)
                index = size;

              else
                count ++;
          }
          if (count%2 == 0){
            expression = expression.replaceAll(lettersS[w], "");
            expression = removeExcess(expression);
          }
          else {
            expression = expression.replaceAll(lettersS[w], "");
            expression = removeExcess(expression);
            expression = expression + "+" + lettersS[w];
          }
        }
      }
    }
    return expression;
  }
/****************************************************************************
  Works together with expressionSimplifyer to eliminate all unnecessary
  plus signs from the expression.
****************************************************************************/
  public String removeExcess(String str){
    String [] newStr = str.split("");
    str = "";
    Stack<String> stack = new Stack<String>();
    int count = 0;

    for (int i = 0; i < newStr.length; i++){
      if (!newStr[i].equals("+")){
        stack.push(newStr[i]);
        count ++;
      }
    }

    for (int j = 0; j < count; j++){
      if (j == count - 1)
        str += stack.pop();
      else
        str += stack.pop() + "+";
    }
    return str;
  }

/****************************************************************************
  These methods change infix expressions into postfix expressions and also
  evaluate the postfix expressions.
  Author: GeeksforGeeks
****************************************************************************/

  private static int Prec(char ch){
    switch(ch){
      case '+':
      case '-':
        return 1;

      case '*':
      case '/':
        return 2;

      case '^':
        return 3;
    }
    return -1;
  }

  public static String infixToPostfix(String expr){
    String result = "";
    Stack<Character> stack = new Stack<>();

    for (int i = 0; i < expr.length(); ++i ){
      char c = expr.charAt(i);
      if (Character.isDigit(c))
        result += c;

      else if (c == '(')
        stack.push(c);

      else if (c == ')'){
        while(!(stack.isEmpty()) && stack.peek() != '('){
          result += stack.pop();
        }

        if (!(stack.isEmpty()) && stack.peek() != '(')
          result += stack.pop();
        else
          stack.pop();
      }

      else{
        while (!(stack.isEmpty()) && Prec(c) <= Prec(stack.peek())){
          if (stack.peek() == '(')
            return "Invalid Expression";
          result += stack.pop();
        }

        stack.push(c);
      }
    }

    while (!(stack.isEmpty())){
      if (stack.peek() == '(')
        return "Invalid Expression";
      result += stack.pop();
    }
    return result;
  }

  public static int evaluatePostfix(String exp)
    {
        //create a stack
        Stack<Integer> stack=new Stack<>();

        // Scan all characters one by one
        for(int i=0;i<exp.length();i++)
        {
            char c=exp.charAt(i);

            // If the scanned character is an operand (number here),
            // push it to the stack.
            if(Character.isDigit(c))
            stack.push(c - '0');

            //  If the scanned character is an operator, pop two
            // elements from stack apply the operator
            else
            {
                int val1 = stack.pop();
                int val2 = stack.pop();

                switch(c)
                {
                    case '+':
                    stack.push(val2+val1);
                    break;

                    case '-':
                    stack.push(val2- val1);
                    break;

                    case '/':
                    stack.push(val2/val1);
                    break;

                    case '*':
                    stack.push(val2*val1);
                    break;
              }
            }
        }
        return stack.pop();
    }
}
