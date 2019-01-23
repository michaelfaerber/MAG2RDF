package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

/** used as additional, stand-alone jar.
 * Input is the  PaperAbstractsInvertedIndex.txt, output is the changed PaperAbstractsInvertedIndex with the normal text as second column. **/
public class MAGTransformAbstractTxtOnly {
	
	public static void main(String[] args) throws IOException {
		BufferedReader inPaperAbstractsIndexFile = new BufferedReader(new FileReader(args[0]));
		BufferedWriter outPaperAbstractsFile = new BufferedWriter(new FileWriter(args[1]));
		String line = inPaperAbstractsIndexFile.readLine();
		while (line != null) {
			String[] abstractsLine = line.split("\t");
			String paperID = abstractsLine[0];
			String invertedAbstractJSON = abstractsLine[1];
			outPaperAbstractsFile.append(paperID + "\t" + StringEscapeUtils.escapeJava(MAGJSONAbstractToText.getPaperAbstractFromJSON(invertedAbstractJSON)) + "\n");
			line = inPaperAbstractsIndexFile.readLine();
		}
		inPaperAbstractsIndexFile.close();
		outPaperAbstractsFile.flush();
		outPaperAbstractsFile.close();
	}
	

}

