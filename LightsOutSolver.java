/****************************************************************************
  This program solves the lights out game by asking the user for
  the number of vertices and the connections between each vertex.

  Programmer: Michael Hefley
  Date(s): 5/1/19, 1/17/20
****************************************************************************/
import javax.swing.*;
public class LightsOutSolver{
  public static void main(String [] args){
    AugMatrix matrix1 = initializeAugMatrix();
    System.out.println(matrix1.toString() + "\n");

    AugMatrix finalMatrix = performRowReduction(matrix1);

    int count = 0;
    for (int pivotPos = 1; pivotPos <= finalMatrix.getSize(); pivotPos++){
      boolean pivot = checkPivot(pivotPos, finalMatrix);

      if (!(pivot))
        finalMatrix.setLimiter(pivotPos);
      else
        count++;
    }

    if (count == finalMatrix.getSize())
      finalMatrix.setLimiter("No restrictions found");

    System.out.println(finalMatrix.toString());
    requestStartAndFinish(finalMatrix);
    System.out.println(makeSolutions(finalMatrix));
  }

/****************************************************************************
  This method asks the user for the number of vertices and what other
  vertices are effected when a specific one is changed. This information is
  stored in the AugMatrix Abstract Data Type and is returned to be passed
  to the 'performRowReduction' method to find the solutions to the game.
****************************************************************************/
  static AugMatrix initializeAugMatrix(){
    String tempS = JOptionPane.showInputDialog("How many vertices does the " +
    "graph contain: ");

    int s = Integer.parseInt(tempS);
    AugMatrix matrix = new AugMatrix(s);

    for (int i = 1; i < matrix.getSize() + 1; i+=1){
      String changes1 = JOptionPane.showInputDialog("Type in the vertices that" +
        " change when vertex " + i + " changes state in the format " +
        " '1,4,6' : ");

      String [] changes2 = changes1.split(",");
      matrix.setPosMatrix(i, changes2);
    }
    return matrix;
  }

/****************************************************************************
  This method takes the matrix from the method 'initializeAugMatrix' and
  performs row reductions to find the possible solutions to the game.
****************************************************************************/
  static AugMatrix performRowReduction(AugMatrix matrix1){
    for(int count = 1; count <= matrix1.getSize(); count++){
      boolean pivot = makePivot(count, matrix1);
      System.out.println("Does column " + count + " have a pivot? " + pivot);
      pivot = checkPivot(count, matrix1);
      String change = "";

      if (pivot){

        for (int i = 1; i <= matrix1.getSize(); i++){
          if (i != count && (matrix1.getValMatrix(count, count)) ==
          (matrix1.getValMatrix(i, count))){

            change += i + ",";
          }
        }
      }

      if (!(change.equals(""))){
        String [] change2 = change.split(",");
        System.out.println("Rows reduced by row " + count + " : ");

        for (int k = 0; k < change2.length; k++){
          matrix1.rowReduce(Integer.parseInt(change2[k]), count);
          System.out.print(Integer.parseInt(change2[k]) + ", ");
        }
      }
      System.out.println("\n");
    }

    return matrix1;
  }

/****************************************************************************
  This method checks to see if there is pivot in a desired column. This
  determines whether or not to swap rows and performs that operation to
  get a pivot in that column. If no pivot is on the column, then we have a free
  variable. Returns boolean of whether or not it as able t o find a pivot for
  that column.
****************************************************************************/
 static boolean makePivot(int col, AugMatrix matrix1){
   int i = col;
   boolean pivot = true;

   if (matrix1.getValMatrix(col, col) == 0  & col != matrix1.getSize()){
     pivot = false;
     do{
       i+=1;
       if (matrix1.getValMatrix(i,col) == 1)
          pivot = true;
     } while (!(pivot) & i < matrix1.getSize());
   }

   if (i != col & pivot){
     pivot = false;
     matrix1.swapRows(col,i);
     System.out.println("Swapped rows : " + col + " and  " + i);
   }

   if (matrix1.getValMatrix(col, col) == 0 & col == matrix1.getSize())
     pivot = false;

   return pivot;
 }

/****************************************************************************
  This method analyzes the finalMatrix to print out all possible solutions
  that the user can easily perform without any further calculation.
****************************************************************************/
  static String makeSolutions(AugMatrix finalMatrix){

    finalMatrix.makeFinishVec();
    String result = "";
    int [] trivialSolution = new int[finalMatrix.getSize()];
    int numOfAltSols = 0;
    String freeVariables = "";

    for (int pivotPos = 1; pivotPos <= finalMatrix.getSize(); pivotPos+=1){
      boolean pivot = checkPivot(pivotPos, finalMatrix);

      if (finalMatrix.getValVector(pivotPos) == 1 & !(pivot))
        result = "No solutions to this game.";
      if (finalMatrix.getValVector(pivotPos) == 0 & !(pivot)){
        numOfAltSols +=1;
        freeVariables += pivotPos + ",";
      }
    }

    if (numOfAltSols >= 0 & result == ""){
      result += "\nA solution to the game is to press these vertices : ";

      for (int i = 0; i < finalMatrix.getSize(); i+=1){
        trivialSolution[i] = finalMatrix.getValVector(i+1);
        if (trivialSolution[i] == 1)
          result += (i+1) + " ";
      }

      if (numOfAltSols > 0){
        int [] otherSolutions = new int[finalMatrix.getSize() * numOfAltSols];
        int top = 0;
        int [] allCombos = createAllCombos(numOfAltSols, finalMatrix);
        String [] freeVar = freeVariables.split(",");

        for (int comboSol = 0; comboSol < numOfAltSols; comboSol+=1){
          for(int row = 0; row < finalMatrix.getSize(); row+=1){
            otherSolutions[top] = finalMatrix.getValMatrix(row + 1,
            Integer.parseInt(freeVar[comboSol]));

            if (row+1 == Integer.parseInt(freeVar[comboSol]))
              otherSolutions[top] = 1;
            top += 1;
          }
        }

        for (int totalNumSol = 0; totalNumSol < (int)(Math.pow(2, numOfAltSols) - 1);
        totalNumSol+=1){
          result += "\nAnother solution is to press these vertices : ";

          for (int iteration = 0; iteration < finalMatrix.getSize();
          iteration+=1){

            int totalVal = trivialSolution[iteration];
            int top2 = totalNumSol * numOfAltSols;

            for (int combo = 0; combo < numOfAltSols; combo+=1){
              totalVal += allCombos[top2] * otherSolutions[iteration + (finalMatrix.getSize()) * combo];
              top2 += 1;
            }
            totalVal = totalVal % 2;
            if (totalVal == 1)
              result += (iteration + 1) + " ";
          }
        }
      }
    }
    return result;
  }


/****************************************************************************
  This methods checks to see if there is a pivot in a certain position.
****************************************************************************/
  static boolean checkPivot(int column, AugMatrix finalMatrix){
    return (finalMatrix.getValMatrix(column, column) == 1);
  }

/****************************************************************************
  This method creates an array with all possible combinations of 1's and 0's.
****************************************************************************/
  static int[] createAllCombos(int numOfAltSols, AugMatrix finalMatrix){
    int [] allCombos = new int[numOfAltSols * (int)(Math.pow(2, numOfAltSols))];

    for (int i = 1; i < (numOfAltSols + 1); i+=1){
      int top = i - 1;
      boolean change = false;
      int value = 1;
      int counter = 0;

      for (int k = 0; k < (int)(Math.pow(2, numOfAltSols)); k+=1){

        if (counter >= (int)(Math.pow(2, numOfAltSols))/(int)Math.pow(2, i)){
          change = true;
          counter -= (int)(Math.pow(2, numOfAltSols))/(int)Math.pow(2, i);
        }

        if (change)
          value = (value + 1) % 2;

        allCombos[top + (numOfAltSols * k)] = value;
        counter += 1;
        change = false;
      }
    }
    return allCombos;
  }
/****************************************************************************
  This method initializes the state of of the vector that needs to be in the
  augmented part of the matrix. The abstract is calculated beforehand so
  the user can properly enter a start and finish position based on the vertices
  and their connections. A solvable soution may only be transformed into another
  solvable solution. An unsovable solution may only be transformed into another
  unsolvable solution.
****************************************************************************/
  static void requestStartAndFinish(AugMatrix finalMatrix){
    int s, f;
    for(int v = 1; v <= finalMatrix.getSize(); v++){
      s = Integer.parseInt(JOptionPane.showInputDialog("Enter the starting position of vertex " +
        v + ". '1' for on or '0' for off.\n" + finalMatrix.getLimiter() + " :"));

      f = Integer.parseInt(JOptionPane.showInputDialog("Enter the desired finishing position of vertex " +
        v + ". '1' for on or '0' for off.\n" + finalMatrix.getLimiter() + " :"));

      finalMatrix.setStateVertices(v, s+f);
    }
  }
}
