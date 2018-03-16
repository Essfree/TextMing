package feb_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.security.util.Length;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

public class Analysis {
	public void analyData(String analy,int k) throws IOException {
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
		
		for(int j=0;j<k;j++){
			int count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Computer")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][0] = count;
			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("database")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][1] = count;

			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("machinelearning")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][2] = count;
		}
		printData(cal);

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
//		按纯度算
		calByPurity(t);
//		按熵算
		calByEntropy(t);
//		算TP\TN\FN\FP
		calByMethods(cal,k);
		
		
		
		
	}

	private void calByMethods(int[][] cal,int k) {
		System.out.println("TP  TN  FN  FP");
		int TP[] = new int[k];
		int FP[] = new int[k];
		int FN[] = new int[k];
		int TN[] = new int[k];
		int all = 0;
		for (int i = 0; i < cal.length; i++) {
			int temp = 0;
			int max = 0;
			int m = 0;
			int n = 0;
			for (int j = 0; j < cal.length; j++) {
				if(cal[i][j]>temp){
					temp = cal[i][j];
					max = j;
				}
				m = m+cal[i][j];
			}
			for (int j = 0; j < cal.length; j++) {
				n += cal[j][max];
			}
			all = all+m;
//			System.out.println("m = " + m);
//			System.out.println("n = "+n);
//			System.out.println("max = "+max);
			TP[max] = temp;
//			System.out.println("tp = "+TP[max]);
			FP[max] = m - TP[max];
			FN[max] = n - TP[max];
		}
		for (int i = 0; i < cal.length; i++) {
//			System.out.println("tp["+i+"] = "+TP[i]);
			if(TP[i]==0){
				System.err.println("聚类出错");
				return;
			}
			TN[i] = all-TP[i]-FP[i]-FN[i];
			System.out.println(TP[i]+" "+FP[i]+" "+FN[i]+" "+TN[i]);
		}
//		召回率
		recall(TP,FN);
//		精确度
		precision(TP,FP);
//		f值
		fCal(TP,FP,FN);
//		roc auc
//		rocCal(TP,FP,FN,TN);
	}

	private void precision(int[] tP, int[] fP) {
		System.out.println("计算精确度");
		for (int i = 0; i < fP.length; i++) {
			double p = (double)tP[i]/(tP[i]+fP[i]);
			System.out.print(p+" ");
		}
		System.out.println();
		
	}

	private void rocCal(int[] tP, int[] fP, int[] fN,int[] tN) {
		double TPR[] = new double[tP.length];
		double FPR[] = new double[tP.length];
		for (int i = 0; i < TPR.length; i++) {
			TPR[i] = (double)(tP[i])/(tP[i]+fN[i]);
			FPR[i]=(double)fP[i]/(fP[i]+tN[i]);
		System.out.println("tpr = "+TPR[i]+" fpr i "+FPR[i]);
		}
		
	}

	private void fCal(int[] tP, int[] fP, int[] fN) {
		System.out.println("计算f值");
		double p[] = new double[tP.length];
		double r[] = new double[tP.length];
		double f[] = new double[tP.length];
		double beta = 1;
		for (int i = 0; i < f.length; i++) {
			p[i] = (double)(tP[i])/(tP[i]+fP[i]);
			r[i] = (double)(tP[i])/(tP[i]+fN[i]);
			f[i] = (double)((1+(Math.pow(beta, 2))*p[i]*r[i]))/(Math.pow(beta, 2)*p[i]+r[i]);
			System.out.print(i+" "+f[i]+"  ");
		}
		System.out.println();
	}

	private void recall(int[] tP, int[] fN) {
//		召回率
		System.out.println("计算召回率");
		for (int i = 0; i < fN.length; i++) {
			double recall  = (double)(tP[i])/(tP[i]+fN[i]);
			System.out.print(i+" "+recall+"  ");	
		}
		System.out.println();
		
	}

	private void calByEntropy(double[][] t) {
		System.out.println("按熵来算");
		double[] e = new double[t.length];        
		for(int i=0;i<t.length;i++){
			for (int j = 0; j < t.length; j++) {
				e[i] =e[i]+ (-(t[i][j]*((double)Math.log(t[i][j])/Math.log(2))));
			}
			System.out.println(e[i]);
		}
		System.out.println();
	}

	private void calByPurity(double[][] t) {
		System.out.println("按纯度来算");
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t.length; j++) {
				System.out.print(t[i][j]+" ");
			}
			System.out.println();
		}
	}

	private void printData(int[][] cal) {
		for (int i = 0; i < cal.length; i++) {
			for (int j = 0; j < cal.length; j++) {
				System.out.print(cal[i][j]+" ");
			}
			System.out.println();
		}
	}

	public void roc(String rocDir, int k) throws IOException {
		int len = calcu(rocDir);
		File filein = new File(rocDir);
		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
		BufferedReader br = new BufferedReader(in);
		String temp =br.readLine();
		String count[][] = new String[len][k+2];
		System.out.println(count.length);
		int i = 0;
		while(temp!=null){
			String[] te = temp.split(" ");
			if (te[0].contains("Computer")) {
				te[0] = "0";
			}else if(te[0].contains("carbon")){
				te[0] = "1";
			}else if(te[0].contains("History")){
				te[0] = "2";
			}	
			for (int j = 0; j < te.length; j++) {
				count[i][j] = te[j];
				System.out.print(count[i][j]+" ");
			}
			i++;
			System.out.println();
			temp = br.readLine();
		}
		for (int j = 0; j < count.length; j++) {
//			for (int j2 = 0; j2 < k; j2++) {
				if(count[j][0].equals(count[j][1])){
					saveAs(Double.parseDouble(count[j][Integer.parseInt(count[j][1])+2]),1,"E:\\MUC\\1.28\\Result\\Roc_"+count[j][1]+".txt");
				}else{
					saveAs(Double.parseDouble(count[j][Integer.parseInt(count[j][1])+2]),0,"E:\\MUC\\1.28\\Result\\Roc_"+count[j][1]+".txt");
				}
//			}
			System.out.println();
		}
	}

	private void saveAs(double d,int k, String dir) throws IOException {
		FileWriter resWriter = new FileWriter(dir,true);
		resWriter.append(d + " " + k +"\n");
		resWriter.flush();  
        resWriter.close();  
	}

	private int calcu(String rocDir) throws IOException {
		File filein = new File(rocDir);
		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
		BufferedReader br = new BufferedReader(in);
		String temp =br.readLine();
		int i = 0;
		while(temp!=null){
			i++;
			temp = br.readLine();
		}
		return i;
	}
}
