package march;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class ReadLda {

	public static void main(String[] args) throws IOException {
		String ldapath = "D:\\eclipseworkspace\\NLPLDAYL\\data\\LdaResults\\lda_100.theta";
		FileInputStream fi = new FileInputStream(new File(ldapath));
		BufferedReader br = new BufferedReader(new InputStreamReader(fi));
		String temp = br.readLine();
		double m[][] = new double[10][1];
		String[] e = temp.split(" ");
		for (int i = 0; i < m.length; i++) {
			double we = 0;
			int re = 0;
			for (int j = 0; j < e.length; j++) {
				double cn = Double.valueOf(e[j]);
				if(cn>we){
					we = cn;
					re = j;
				}
			}
		}
	}

}
