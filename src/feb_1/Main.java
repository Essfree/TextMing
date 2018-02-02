package feb_1;

import java.io.IOException;
import java.util.Map;


public class Main {
//分析计算roc曲线
	public static void main(String[] args) throws IOException {
		//文本分词		
//		SplitText st = new SplitText();
//		String dataDir = "E:\\MUC\\1.30\\OriginalSample";
//		st.split(dataDir);
//		String splitDir = "E:\\MUC\\1.28\\Sample";
//		Tfidf tI = new Tfidf();
//		Map<String, Map<String, Double>> tfidfMap = tI.CalTfIdf(splitDir);
		int classNum = 3;
		String desDir = "E:\\MUC\\1.28\\Result\\Result.txt";
		String RocDir = "E:\\MUC\\1.28\\Result\\Roc.txt";
//		new KmeansCal(tfidfMap,classNum,desDir);
		Analysis as = new Analysis();
//		as.analyData(desDir,classNum);
		as.roc(RocDir,classNum);
	}

}
