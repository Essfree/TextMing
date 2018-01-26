package jan_25;

import java.io.IOException;
import java.util.Map;


public class Main {

	public static void main(String[] args) throws IOException {
		//文本分词		
//		SplitText st = new SplitText();
//		String dataDir = "E:\\MUC\\1.25\\OriginalSample";
//		st.split(dataDir);
		String splitDir = "E:\\MUC\\1.25\\Sample";
		Tfidf tI = new Tfidf();
		Map<String, Map<String, Double>> tfidfMap = tI.CalTfIdf(splitDir);
		int classNum = 3;
		new KmeansCal(tfidfMap,classNum);
		String analy = "E:\\MUC\\1.25\\Result\\Result.txt";
		Analysis as = new Analysis();
		as.analyData(analy);
	}

}
