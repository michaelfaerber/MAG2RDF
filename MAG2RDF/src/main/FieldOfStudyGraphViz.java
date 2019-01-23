package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/** Visualize the scientific concepts ("fields of study") hierarchy given in the MAG
 * using
 * FieldOfStudyChildren.txt + FieldsOfStudy1+4_mfa.txt
 * 
 * After running this class via
 *  java -Xmx15g -jar FieldOfStudyGraphViz.jar 2
 * run in bash:
 *  dot -Tpng FieldsOfStudyChildrenDot.dot -o FieldsOfStudyChildrenDot.png
 * 
 * 
 * Typical level frequency:
 * Freq|Level
     19 0
    294 1
  82593 2
  92314 3
  37052 4
  17032 5
(given July 2018 MAG dump)

 * @author michael
 *
 */
public class FieldOfStudyGraphViz {
	
	public static HashMap<Long, String> mappingScientificFields;
	
	public static void main(String[] args) throws IOException {
		String mappingFilePath = "./FieldsOfStudy1+4+6_mfa.txt"; // now with level for filtering out low levels
		String hierarchyFilePath = "./FieldOfStudyChildren.txt";
		String outHierarchyFilePath = "./FieldsOfStudyChildrenDot.dot";
		int levelMax = Integer.valueOf(args[0]); // e.g., 1 (Caution: starting with 0.)
		/** first load the mappings from ID to scientific field, given by FieldsOfStudy1+4_mfa.txt **/
		mappingScientificFields = new HashMap<Long, String>();
		
		BufferedReader inMappings = new BufferedReader(new FileReader(new File(mappingFilePath)));
		String mappingLine = inMappings.readLine();
		while (mappingLine != null) {
			String[] fieldOfStudyLine = mappingLine.split("\t");
			Long field_id = Long.valueOf(fieldOfStudyLine[0]);
			String field_name = fieldOfStudyLine[1];
			int level = Integer.valueOf(fieldOfStudyLine[2]);
			if(level <= levelMax) {
				mappingScientificFields.put(field_id, field_name);
			}
			mappingLine = inMappings.readLine(); // very important
		}
		inMappings.close();
		System.out.println("---");
		
		/** then create paths for dot/graphviz **/
		BufferedReader inHierarchy = new BufferedReader(new FileReader(new File(hierarchyFilePath)));
		BufferedWriter out = new BufferedWriter(new FileWriter(outHierarchyFilePath));
		out.write(" digraph MAGFieldsOfStudyHierarchy {\n");
		String line2 = inHierarchy.readLine();
		while (line2 != null) {
			String[] fieldOfStudyHierarchyLine = line2.split("\t");
			Long field = Long.valueOf(fieldOfStudyHierarchyLine[0]);
			Long fieldChild = Long.valueOf(fieldOfStudyHierarchyLine[1]);
			/** only if in mapping hash function (filtered already) **/
			if(mappingScientificFields.containsKey(fieldChild) && mappingScientificFields.containsKey(field)) {
				out.write("\"" + mappingScientificFields.get(field).replaceAll("\"", "") + "\" -> \"" + mappingScientificFields.get(fieldChild).replaceAll("\"", "") + "\"" + ";\n");
			}
			line2 = inHierarchy.readLine(); // important
		}
		out.write("}");
		out.flush();
		out.close();
		inHierarchy.close();
	}
}
