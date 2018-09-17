package march;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ujmp.core.Matrix;


public class Analysis {
	public static int total = 0;
	public void analyData(String analy,int k, String result) throws IOException {
		
		Map<String, Integer> newMap = new HashMap<String, Integer>();
		File filein = new File(analy);
		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
		BufferedReader br = new BufferedReader(in);
		String temp =br.readLine();
		StringBuffer sb = null;
		while(temp!=null){
			String[] te = temp.split("txt ");
			newMap.put(te[0]+"txt",Integer.parseInt(te[1]));
			temp = br.readLine();
		}
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(newMap.entrySet());
		int cal[][] = new int[k][k];
//		得到矩阵
		for(int j=0;j<k;j++){
			int count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Carbon")&&infoIds.get(i).getValue()==j){
//					count++;
					cal[j][0] += 1;
				}else if (infoIds.get(i).getKey().contains("Ceramic")&&infoIds.get(i).getValue()==j) {
					cal[j][1] += 1;
				}else if(infoIds.get(i).getKey().contains("metallic")&&infoIds.get(i).getValue()==j){
					cal[j][2] += 1;
				}else if(infoIds.get(i).getKey().contains("Organic Inorganic")&&infoIds.get(i).getValue()==j){
					cal[j][3] += 1;
				}else if(infoIds.get(i).getKey().contains("Polymer")&&infoIds.get(i).getValue()==j){
					cal[j][4] += 1;
				}else if(infoIds.get(i).getKey().contains("Semi-Metallic")&&infoIds.get(i).getValue()==j){
					cal[j][5] += 1;
				}
			}
		}

//		计算cal的总数
		
		
		File file  = new File(result);
		FileWriter resWriter = new FileWriter(result,true);  
//		printData(cal);
		
		for (int i = 0; i < cal.length; i++) {
			for (int j = 0; j < cal.length; j++) {
				resWriter.append(cal[i][j]+" ");
			}
			resWriter.append('\n');
		}
		
//		计算比例得到cal中每个值占总的概率（按行算）
		double[][] t = new double[cal.length][cal.length];
		for (int i = 0; i < cal.length; i++) {
			int m = 0;
			for (int j = 0; j < cal.length; j++) {
				m = m+cal[i][j];
			}
			
			for (int j = 0; j < cal.length; j++) {
				t[i][j] = (double)cal[i][j]/m;
//				t[i][j] = m;
			}
		}
		
//		计算比例得到cal中每个值占总的概率（按列算）
		double[][] col = new double[cal.length][cal.length];
		for (int j = 0; j < cal.length; j++) {
			int m = 0;
			for (int i = 0; i < cal.length; i++) {
				m = m+cal[i][j];
			}
			
			for (int i = 0; i < cal.length; i++) {
				col[i][j] = (double)cal[i][j]/m;
//				t[i][j] = m;
			}
		}
		
//		按单个纯度算，得到每个元素的纯度
//		calByPurity(t);
//		按熵算，得到每个簇的熵
//		calByEntropy(t);
		
		resWriter.append("按熵来算"+'\n');
		double[] e = new double[t.length];        
		for(int i=0;i<t.length;i++){
			for (int j = 0; j < t.length; j++) {
				if(t[i][j] == 0){
					e[i] = e[i];
				}else{
					e[i] =e[i]+ (-(t[i][j]*((double)Math.log(t[i][j])/Math.log(2))));
				}
			}
			resWriter.append(e[i]+" "+'\n');
		}
//		按纯度
//		int place[] = new int[k];
		double pur[] =  new double[t.length];
		double pre[] =  new double[col.length];
		for (int i = 0; i < t.length; i++) {
			double max = 0;
			int place = 0;
			for (int j = 0; j < t.length; j++) {
				if(max<t[i][j]){
					max = t[i][j];
					place = j;
				}

			}
			pur[i] = max;
			pre[i] = col[i][place];
			resWriter.append(max+" "+'\n');
		}
//		按精确度
//		double pre[] =  new double[col.length];
		for (int j = 0; j < col.length; j++) {
			resWriter.append(pre[j]+" "+'\n');
		}
		
////		用匈牙利算法(指派问题)得到总的纯度和总的熵
//		int hg[] = Hg(cal);
//		for (int i = 0; i < hg.length; i++) {
//			System.out.println(hg[i]);
//		}
////		for (int i = 0; i < hg.length; i++) {
////			System.out.println(cal[i][hg[i]]);
////		}
////		计算每行的纯度
//		resWriter.append("纯度分别为"+'\n');		
//		double pur[] = new double[k];
//		for (int i = 0; i < cal.length; i++) {
//			int count = 0;
//			for (int j = 0; j < cal.length; j++) {
//				count+=cal[i][j];
//			}
//			pur[i] = (double)cal[i][hg[i]]/count;
//			resWriter.append(pur[i]+" "+'\n');
//		}
////		resWriter.append('\n');
////		System.out.println();
////		计算每列的精确度
//		resWriter.append("精确度分别为："+'\n');
//		double pre[] = new double[k];
//		for (int j = 0; j < pre.length; j++) {
//			int count = 0;
//			for (int i = 0; i < pre.length; i++) {
//				count+=cal[i][j];
//			}
//			for (int i = 0; i < hg.length; i++) {
//				if(j == hg[i]){
//					pre[i] = (double)cal[i][hg[i]]/count;
//					resWriter.append(pre[i]+" "+'\n');
//					break;
//				}
//			}
//		}
//		resWriter.append('\n');
////		System.out.println();
//		计算总纯度和总精确度
		double purcnt = 0; double precnt = 0;
		
		for (int i = 0; i < pur.length; i++) {
			
			purcnt += pur[i]/k;
			precnt += pre[i]/k;
		}
		resWriter.append("总纯度为："+purcnt+'\n');
		resWriter.append("总精确度为："+precnt+'\n');
		
//		计算F值
		double f_measure = FMS(purcnt,precnt);
		resWriter.append("F值为："+f_measure+'\n');
		resWriter.append('\n');
		resWriter.flush();
		resWriter.close();
//		//////////////////////////
//		算TP\TN\FN\FP
//		calByMethods(cal,k);
		
		
		
		
	}

private double FMS(double r, double p) {
		double beta = 1;
		double f = 0;
		f = (double)((beta*beta+1)*p*r)/(beta*beta*p+r);
		return f;
	}

//	private void calByMethods(int[][] cal,int k) {
//		System.out.println("TP  TN  FN  FP");
//		int TP[] = new int[k];
//		int FP[] = new int[k];
//		int FN[] = new int[k];
//		int TN[] = new int[k];
//		int all = 0;
//		for (int i = 0; i < cal.length; i++) {
//			int temp = 0;
//			int max = 0;
//			int m = 0;
//			int n = 0;
//			for (int j = 0; j < cal.length; j++) {
//				if(cal[i][j]>temp){
//					temp = cal[i][j];
//					max = j;
//				}
//				m = m+cal[i][j];
//			}
//			for (int j = 0; j < cal.length; j++) {
//				n += cal[j][max];
//			}
//			all = all+m;
////			System.out.println("m = " + m);
////			System.out.println("n = "+n);
////			System.out.println("max = "+max);
//			TP[max] = temp;
////			System.out.println("tp = "+TP[max]);
//			FP[max] = m - TP[max];
//			FN[max] = n - TP[max];
//		}
//		for (int i = 0; i < cal.length; i++) {
////			System.out.println("tp["+i+"] = "+TP[i]);
//			if(TP[i]==0){
//				System.err.println("聚类出错");
//				return;
//			}
//			TN[i] = all-TP[i]-FP[i]-FN[i];
//			System.out.println(TP[i]+" "+FP[i]+" "+FN[i]+" "+TN[i]);
//		}
////		召回率
//		recall(TP,FN);
////		精确度
//		precision(TP,FP);
////		f值
//		fCal(TP,FP,FN);
////		roc auc
////		rocCal(TP,FP,FN,TN);
//	}

//	private void precision(int[] tP, int[] fP) {
//		System.out.println("计算精确度");
//		for (int i = 0; i < fP.length; i++) {
//			double p = (double)tP[i]/(tP[i]+fP[i]);
//			System.out.print(p+" ");
//		}
//		System.out.println();
//		
//	}

//	private void rocCal(int[] tP, int[] fP, int[] fN,int[] tN) {
//		double TPR[] = new double[tP.length];
//		double FPR[] = new double[tP.length];
//		for (int i = 0; i < TPR.length; i++) {
//			TPR[i] = (double)(tP[i])/(tP[i]+fN[i]);
//			FPR[i]=(double)fP[i]/(fP[i]+tN[i]);
//		System.out.println("tpr = "+TPR[i]+" fpr i "+FPR[i]);
//		}
//		
//	}

//	private void fCal(int[] tP, int[] fP, int[] fN) {
//		System.out.println("计算f值");
//		double p[] = new double[tP.length];
//		double r[] = new double[tP.length];
//		double f[] = new double[tP.length];
//		double beta = 1;
//		for (int i = 0; i < f.length; i++) {
//			p[i] = (double)(tP[i])/(tP[i]+fP[i]);
//			r[i] = (double)(tP[i])/(tP[i]+fN[i]);
//			f[i] = (double)((1+(Math.pow(beta, 2))*p[i]*r[i]))/(Math.pow(beta, 2)*p[i]+r[i]);
//			System.out.print(i+" "+f[i]+"  ");
//		}
//		System.out.println();
//	}
//
//	private void recall(int[] tP, int[] fN) {
////		召回率
//		System.out.println("计算召回率");
//		for (int i = 0; i < fN.length; i++) {
//			double recall  = (double)(tP[i])/(tP[i]+fN[i]);
//			System.out.print(i+" "+recall+"  ");	
//		}
//		System.out.println();
//		
//	}

	private int[] Hg(int[][] cal) {
//	     初始化了一个m.length*m[0].length的矩阵(4*4)
		Matrix mMatrix = Matrix.Factory.zeros(cal.length, cal[0].length);
//   矩阵赋值
		for (int i = 0; i < cal.length; i++) {
			for (int j = 0; j < cal[i].length; j++) {
				mMatrix.setAsInt(cal[i][j], i, j);
			}
		}
   
		Hungary h = new Hungary(mMatrix);
   
		h.findMaxMatch();
   
//		System.out.println(h.mapMatrix);
		int m[] =  ((h.mapIndices));
		int tto = 15;
		for (int i = 0; i < m.length; i++) {
			if(m[i]!=-1){
				tto = tto - m[i];
			}
		}
		for (int i = 0; i < m.length; i++) {
			if(m[i] == -1){
				m[i] = tto;
			}
		}
		return m;
//		System.out.println(Arrays.toString(h.mapIndices));
		
	}

//	private void calByEntropy(double[][] t) {
//		System.out.println("按熵来算");
//		double[] e = new double[t.length];        
//		for(int i=0;i<t.length;i++){
//			for (int j = 0; j < t.length; j++) {
//				if(t[i][j] == 0){
//					e[i] = e[i];
//				}else{
//					e[i] =e[i]+ (-(t[i][j]*((double)Math.log(t[i][j])/Math.log(2))));
//				}
//			}
//			System.out.println(e[i]);
//		}
//		System.out.println();
//	}

//	private void calByPurity(double[][] t) {
//		System.out.println("按纯度来算");
//		
//		double[] to = new double[t.length];
//		for (int i = 0; i < t.length; i++) {
//			double temp = 0;
//			for (int j = 0; j < t.length; j++) {
//				if(t[i][j] > temp){
//					temp = t[i][j];
//				}
//				System.out.print(t[i][j]+" ");
//			}
//			to[i] = temp;
//			System.out.println();
//		}
//		for (int i = 0; i < to.length; i++) {
//			System.out.print(to[i]+" ");
//		}
//	}

//	private void printData(int[][] cal) {
//		for (int i = 0; i < cal.length; i++) {
//			for (int j = 0; j < cal.length; j++) {
//				System.out.print(cal[i][j]+" ");
//			}
//			System.out.println();
//		}
//	}

//	public void roc(String rocDir, int k) throws IOException {
//		int len = calcu(rocDir);
//		File filein = new File(rocDir);
//		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
//		BufferedReader br = new BufferedReader(in);
//		String temp =br.readLine();
//		String count[][] = new String[len][k+2];
//		System.out.println(count.length);
//		int i = 0;
//		while(temp!=null){
//			String[] te = temp.split(" ");
//			if (te[0].contains("Computer")) {
//				te[0] = "0";
//			}else if(te[0].contains("carbon")){
//				te[0] = "1";
//			}else if(te[0].contains("History")){
//				te[0] = "2";
//			}	
//			for (int j = 0; j < te.length; j++) {
//				count[i][j] = te[j];
//				System.out.print(count[i][j]+" ");
//			}
//			i++;
//			System.out.println();
//			temp = br.readLine();
//		}
//		for (int j = 0; j < count.length; j++) {
////			for (int j2 = 0; j2 < k; j2++) {
//				if(count[j][0].equals(count[j][1])){
//					saveAs(Double.parseDouble(count[j][Integer.parseInt(count[j][1])+2]),1,"E:\\MUC\\1.28\\Result\\Roc_"+count[j][1]+".txt");
//				}else{
//					saveAs(Double.parseDouble(count[j][Integer.parseInt(count[j][1])+2]),0,"E:\\MUC\\1.28\\Result\\Roc_"+count[j][1]+".txt");
//				}
////			}
//			System.out.println();
//		}
//	}
//
//	private void saveAs(double d,int k, String dir) throws IOException {
//		FileWriter resWriter = new FileWriter(dir,true);
//		resWriter.append(d + " " + k +"\n");
//		resWriter.flush();  
//        resWriter.close();  
//	}
//
//	private int calcu(String rocDir) throws IOException {
//		File filein = new File(rocDir);
//		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
//		BufferedReader br = new BufferedReader(in);
//		String temp =br.readLine();
//		int i = 0;
//		while(temp!=null){
//			i++;
//			temp = br.readLine();
//		}
//		return i;
//	}
}
