package march;

import java.util.Arrays;
import org.ujmp.core.Matrix;
import org.ujmp.core.calculation.Calculation.Ret;

/**
 * The Hungary method solving allocating problem.
 *
 */
public class Hungary {

    Matrix graph;

    int n, m;

    //int minMatchValue;

    Matrix mapMatrix;

    int[] mapIndices;

    public static final int MAX_ITE_NUM = 1000;

    /**
     * plus:相加
     * @param 矩阵 pGraph
     */
    public Hungary(Matrix pGraph) {
        graph = pGraph.plus(Ret.NEW, false, 0);
//        n是指行数，m是指列数
        n = (int) pGraph.getRowCount();
        m = (int) pGraph.getColumnCount();
        if (n != m) {
            graphSqureChange();
        }
    }
    
    /**
     * 
     */
    private void graphSqureChange() {
        if (n < m) {
//        	垂直方向添加一个矩阵
            graph = graph.appendVertically(Ret.LINK,Matrix.Factory.zeros(m - n, m));
        } else {
//        	水平方向添加一个矩阵
            graph = graph.appendHorizontally(Ret.LINK,Matrix.Factory.zeros(n, n - m));
        }
        n = (int) graph.getRowCount();
        m = n;
    }
    
    /**
     * 
     */
    public void findMaxMatch() {
//        取矩阵每行最大的数
        Matrix rowMaxValue = graph.max(Ret.NEW, 1);
        Matrix minGraph = Matrix.Factory.zeros(n,m);
//        System.out.println(graph);
        for (int i = 0; i < graph.getRowCount(); i++) {
			for (int j = 0; j < graph.getColumnCount(); j++) {
				minGraph.setAsInt(-1*graph.getAsInt(i,j),i,j);
			}
		}
//        System.out.println(minGraph);
//        tC-->空矩阵
        Matrix tC = Matrix.Factory.emptyMatrix();
//        最大值减每行得到的新矩阵
        for (int i = 0; i < n; i++) {
            tC = tC.appendVertically(Ret.LINK, minGraph.selectRows(Ret.LINK, i).plus(rowMaxValue.getAsInt(i, 0)));
        }
//        取矩阵每列最小值
        Matrix columnMinValue = tC.min(Ret.NEW, 0);
        
//        for (int i = 0; i < tC.getRowCount(); i++) {
//			for (int j = 0; j < tC.getColumnCount(); j++) {
//				minGraph.setAsInt(-1*tC.getAsInt(i,j),i,j);
//			}
//		}
//        System.out.println(tC);
//        System.out.println(minGraph);
//        空矩阵
        Matrix _tC = Matrix.Factory.emptyMatrix();
        
//        对于矩阵tC，最大值减去列每列得到的新矩阵
        for (int i = 0; i < m; i++) {
            _tC = _tC.appendHorizontally(Ret.LINK,tC.selectColumns(Ret.LINK, i).minus(columnMinValue.getAsInt(0, i)));
        }

        Matrix tMapMatrix = constructMapAndUpdate(_tC)[0];
        int tCount = 0;
        while (!isOptimal(tMapMatrix) && tCount++ < MAX_ITE_NUM) {
            Matrix[] tMatrix = constructMapAndUpdate(_tC);
            tMapMatrix = tMatrix[0];
            _tC = tMatrix[1];
        }

        mapMatrix = tMapMatrix;
        mapIndices = new int[n];
        Arrays.fill(mapIndices, -1);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if(mapMatrix.getAsInt(i, j) == 1) {
                    mapIndices[i] = j;
                    break;
                }
            }
        }
    }

    private Matrix[] constructMapAndUpdate(Matrix c) {
//        新建空矩阵tMap
    	Matrix tMap = Matrix.Factory.zeros(n, m);
//        新建矩阵（原矩阵+0）
        Matrix updateC = c.plus(Ret.NEW, false, 0);
//        得到一个0位置的二维数组
        int[][] rowZeroIndices = getRowZeroIndices(c);
//        确定行顺序：1，2，0，3
        int[] indexSequence = findMinToMaxRowZeroCountIndexSequence(rowZeroIndices);
        boolean[] rowComputed = new boolean[n];
        boolean[] columnComputed = new boolean[m];
        for (int i = 0; i < n; i++) {
            int currentRow = indexSequence[i];
            for (int j = 0; j < rowZeroIndices[currentRow].length; j++) {
                if (!columnComputed[rowZeroIndices[currentRow][j]]) {
                    tMap.setAsInt(1, currentRow, rowZeroIndices[currentRow][j]);
                    columnComputed[rowZeroIndices[currentRow][j]] = true;
                    // 1) Flag for having bracket.
                    rowComputed[currentRow] = true;
                    break;
                }
            }
        }
        //System.out.println("C(1)\r\n" + tMap);

        if (isOptimal(tMap)) {
            return new Matrix[] { tMap, updateC };
        }
        // C' --> C''
        boolean[] rowFlag = new boolean[n];
        // 1)
        for (int i = 0; i < n; i++) {
            rowFlag[i] = !rowComputed[i];
        }
        //System.out.println("C(1): " + Arrays.toString(rowFlag));

        boolean[] columnFlag = new boolean[m];

        boolean[] _rowFlag = new boolean[n];
        boolean[] _columnFlag = new boolean[m];

        while (!Arrays.equals(_rowFlag, rowFlag) || !Arrays.equals(_columnFlag, columnFlag)) {
            _rowFlag = rowFlag;
            _columnFlag = columnFlag;

            // 2) Flag column indices for all the zero elements in those
            // bracket-flaged row.
            for (int i = 0; i < n; i++) {
                // flaged row
                if (rowFlag[i]) {
                    for (int j = 0; j < rowZeroIndices[i].length; j++) {
                        columnFlag[rowZeroIndices[i][j]] = true;
                    }
                }
            }
            //System.out.println("C(1)" + Arrays.toString(columnFlag));

            // 3) Flag row indices for those bracket-flaged elements in flaged
            // columns.
            for (int i = 0; i < m; i++) {
                if (columnFlag[i]) {
                    for (int j = 0; j < n; j++) {
                        if (tMap.getAsInt(j, i) == 1) {
                            rowFlag[j] = true;
                            break;
                        }
                    }
                }
            }
        }

        // 5) Find minimum element in those locations uncovered by lines.
        int tMinValue = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            // skip row Lines
            if (!rowFlag[i]) {
                continue;
            }

            for (int j = 0; j < m; j++) {
                if (!columnFlag[j]) {
                    if (c.getAsInt(i, j) < tMinValue) {
                        tMinValue = c.getAsInt(i, j);
                    }
                }
            }
        }

        // 6) Minus the minimum value for those flaged rows.
        for (int i = 0; i < n; i++) {
            if (rowFlag[i]) {
                for (int j = 0; j < m; j++) {
                    updateC.setAsInt(updateC.getAsInt(i, j) - tMinValue, i, j);
                }
            }
        }
        // 6) Plus the minimum value for those flaged columns.
        for (int i = 0; i < m; i++) {
            if (columnFlag[i]) {
                for (int j = 0; j < n; j++) {
                    updateC.setAsInt(updateC.getAsInt(j, i) + tMinValue, j, i);
                }
            }
        }

        return new Matrix[] { tMap, updateC };
    }

    private int[] findMinToMaxRowZeroCountIndexSequence(int[][] rowZeroIndices) {
        int[] tSequence = new int[n];
        int tIndex = 0;
        boolean[] rowComputed = new boolean[n];
        while (tIndex < n) {
            int minZeroCountIndex = 0;
            int minZeroCount = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (rowComputed[i]) {
                    continue;
                }

                if (rowZeroIndices[i].length < minZeroCount) {
                    minZeroCount = rowZeroIndices[i].length;
                    minZeroCountIndex = i;
                }

            }
            tSequence[tIndex++] = minZeroCountIndex;
            rowComputed[minZeroCountIndex] = true;
        }
        return tSequence;
    }

    private int[][] getRowZeroIndices(Matrix c) {

        int[][] tRowZeroIndices = new int[n][];
        int[] tRowZeroCounts = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (c.getAsInt(i, j) == 0) {
                    tRowZeroCounts[i]++;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            tRowZeroIndices[i] = new int[tRowZeroCounts[i]];
            tRowZeroCounts[i] = 0;
            for (int j = 0; j < m; j++) {
                if (c.getAsInt(i, j) == 0) {
                    tRowZeroIndices[i][tRowZeroCounts[i]++] = j;
                }
            }
        }

        return tRowZeroIndices;
    }

    /**
     * Judge if the map matrix is optimal.
     * 
     * @param mapC
     * @return
     */
    private boolean isOptimal(Matrix mapC) {
        return mapC.sum(Ret.NEW, Matrix.ALL, false).getAsInt(0, 0) == n;
    }

    public int[] getMapIndices() {
        return mapIndices;
    }
    
    
    /**
     * 测试主函数
    **/
//	public static void main(String[] args) {
//    	int[][] m = null;
//        
//        
//
//    	m = new int[][]{
//                {129,256,41,271,1659,28},
//            	{42,35,18,16,9,656},
//            	{873,73,575,136,322,47},
//            	{73,51,1240,403,27,22},
//            	{1650,589,91,208,78,254},
//            	{471,21,1044,118,20,32}, 
//        };
//    	
//    	
////    	     初始化了一个m.length*m[0].length的矩阵(4*4)
//        Matrix mMatrix = Matrix.Factory.zeros(m.length, m[0].length);
////        矩阵赋值
//        for (int i = 0; i < m.length; i++) {
//            for (int j = 0; j < m[i].length; j++) {
//                mMatrix.setAsInt(m[i][j], i, j);
//            }
//        }
//        
//        Hungary h = new Hungary(mMatrix);
//        
//        h.findMaxMatch();
//        
//        System.out.println(h.mapMatrix);
//        System.out.println(Arrays.toString(h.mapIndices));
//    }
}