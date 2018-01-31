package jan_28;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
				if(infoIds.get(i).getKey().contains("carbon")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][1] = count;

			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("History")&&infoIds.get(i).getValue()==j){
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
			TN[i] = all-TP[i]-FP[i]-FN[i];
			System.out.println(TP[i]+" "+FP[i]+" "+FN[i]+" "+TN[i]);
		}
//		召回率
		recall(TP,FN);
//		f值
		fCal(TP,FP,FN);
//		roc auc
		rocCal(TP,FP,FN,TN);
	}

	private void rocCal(int[] tP, int[] fP, int[] fN,int[] tN) {
		double TRP[] = new double[tP.length];
		double FPR[] = new double[tP.length];
		for (int i = 0; i < TRP.length; i++) {
			TRP[i] = (double)(tP[i])/(tP[i]+fN[i]);
			FPR[i]=(double)fP[i]/(fP[i]+tN[i]);
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
			f[i] = (double)((Math.pow(beta, 2)*p[i]*r[i]))/(Math.pow(beta, 2)*p[i]+r[i]);
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
}
