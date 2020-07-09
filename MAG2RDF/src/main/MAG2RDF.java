package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import textannotation.FullTextAnnotationClientXML;
import textannotation.MentionInAnnotation;
import textannotation.TextAnnotation;

import utilities.NxUtil;
import utilities.UrlUtility;



//    This work is licensed under a Creative Commons Attribution Share-Alike 4.0 License.
public class MAG2RDF {
	/** Read the 15 MAG text files, such as Papers.txt and Authors.txt,
	 *  and transform the data into proper RDF.
	 *  
	 *  Input: 
	 *  (1) Path to directory with all 15 MAG text files as provided by Microsoft.
	 *  (2) Path to output directory.
	 * 
	 * @param args
	 * @throws IOException
	 */
	
	/** global parameters **/
	/** using x-LiSA for entity linking **/
	public static String xlisaTextannotationsServiceURI = "http://129.13.152.190:8080/text-annotation-with-offset-Nov14";
	
	static String kgPrefix = "http://ma-graph.org/";
	
	static String kgPropertyPrefix = kgPrefix + "property/";
	static String rdfTypeURI = "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	static String foafNameURI = "<http://xmlns.com/foaf/0.1/name>";
	static String foafHomepageURI = "<http://xmlns.com/foaf/0.1/homepage>";
	static String seeAlsoURI = "<http://www.w3.org/2000/01/rdf-schema#seeAlso>";
	static String sameAsURI = "<http://www.w3.org/2002/07/owl#sameAs>";
	static String createdURI = "<http://purl.org/dc/terms/created>";
	static String memberOfURI = "<http://www.w3.org/ns/org#memberOf>";
	static String partOfURI = "<http://purl.org/dc/terms/isPartOf>";
	static String startTimeURI = "<http://purl.org/NET/c4dm/timeline.owl#start>";
	static String endTimeURI = "<http://purl.org/NET/c4dm/timeline.owl#end>";
	static String issnURI = "<http://id.loc.gov/vocabulary/identifiers/issn>";
	static String publisherURI = "<http://purl.org/dc/terms/publisher>";
	static String doiURI = "<http://purl.org/spar/datacite/doi>";
	static String dctermsTitleURI = "<http://purl.org/dc/terms/title>";
	static String publicationDateURI = "<http://prismstandard.org/namespaces/1.2/basic/publicationDate>";
	static String dctermsCreatorURI = "<http://purl.org/dc/terms/creator>";
	static String dctermsLanguageURI = "<http://purl.org/dc/terms/language>";
	static String citoCitesURI = "<http://purl.org/spar/cito/cites>";
	
	
	// input: path to MAG txt files.
	// example run:
	// java -jar -Xmx50g MAG2RDF.jar /vol4/faerberm/data/mag/dumps/2018-07-19
	public static void main(String[] args) throws IOException {
		
		String bwuniclusterpath = "";
		if(args[0].equals("home")) {
			bwuniclusterpath = "C:/Users/FranzKrause/git/MAG2RDF/MAG2RDF" ; 
		}else {
			bwuniclusterpath = "/slow/users/mfa/makg/mag-2019-12-26/" ; 
		}

		// assuming: all input files exist.
		File conferenceSeries_ = new File(bwuniclusterpath + "/mag/" + "ConferenceSeries.txt");
		File affiliations_ = new File(bwuniclusterpath + "/mag/" + "Affiliations.txt");
		File authors_ = new File(bwuniclusterpath + "/mag/" + "Authors.txt");
		File conferenceInstances_ = new File(bwuniclusterpath + "/mag/" + "ConferenceInstances.txt");		
		File fieldOfStudyChildren_ = new File(bwuniclusterpath + "/advanced/" + "FieldOfStudyChildren.txt");
		File fieldOfStudyRelationship_ = new File(bwuniclusterpath + "/advanced/" + "RelatedFieldOfStudy.txt"); // this was renamed, since 2018-11-09, old file name: FieldOfStudyRelationship
		File fieldsOfStudy_ = new File(bwuniclusterpath + "/advanced/" + "FieldsOfStudy.txt");
		File journals_ = new File(bwuniclusterpath + "/mag/" + "Journals.txt");
		File paperAbstractsInvertedIndex_ = new File(bwuniclusterpath + "/nlp/" + "PaperAbstractsInvertedIndex.txt");
		File paperAuthorAffiliations_ = new File(bwuniclusterpath + "/mag/" + "PaperAuthorAffiliations.txt");
		File paperCitationContexts_ = new File(bwuniclusterpath + "/nlp/" + "PaperCitationContexts.txt");
		File paperFieldsOfStudy_ = new File(bwuniclusterpath + "/advanced/" + "PaperFieldsOfStudy.txt");
		File paperLanguages_ = new File(bwuniclusterpath + "/mag/" + "PaperLanguages.txt"); //FK language tags are now found in paperUrls_
		File paperReferences_ = new File(bwuniclusterpath + "/mag/" + "PaperReferences.txt");
		File papers_ = new File(bwuniclusterpath + "/mag/" + "Papers.txt");
		File paperUrls_ = new File(bwuniclusterpath + "/mag/" + "PaperUrls.txt");
		
		UrlUtility urlValidator = new UrlUtility();
		
		FullTextAnnotationClientXML fac = new FullTextAnnotationClientXML();
		fac.getAnnotationsByWholeDocAnnotation("test"); // test if text annotation tool is online.
					
		/** cache for xlisa textannotation service: (text to be annotated) -> retrieved entity **/
		HashMap<String,String> cacheTextAnnotations = new HashMap<String,String>();
		
		try {
			
//			if(DEBUG==false){
			/** Affiliations.txt **/
			System.out.println("### Start Affiliations.txt ###");
			StringBuilder affiliationsRDFOutput = new StringBuilder();
			BufferedReader inAffiliations = new BufferedReader(new FileReader(affiliations_));
			BufferedWriter outAffiliations = new BufferedWriter(new FileWriter(affiliations_ + ".nt"));
			String line = inAffiliations.readLine();
			
			while (line != null) {
				
				String[] affilliationLine = line.split("\t");
				// 1
				String affiliationEntity = "<" + kgPrefix + "entity/" + affilliationLine[0] + ">";
				// entity type
				affiliationsRDFOutput.append(affiliationEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/Affiliation>" + " .\n");
				
				// 2
				if(!affilliationLine[1].equals(""))
					affiliationsRDFOutput.append(affiliationEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(affilliationLine[1]) + " .\n");
				
				// 4
				if(!affilliationLine[3].equals(""))
					affiliationsRDFOutput.append(affiliationEntity + " " + foafNameURI + " " + returnXSDString(affilliationLine[3]) + " .\n");
				
				// 5 GRID; cf. curl -L -H 'Accept: application/rdf+xml' http://www.grid.ac/institutes/grid.446382.f
				if(!affilliationLine[4].equals("")) {
					String localGridID = affilliationLine[4];
					affiliationsRDFOutput.append(affiliationEntity + " " + returnOwnProperty("grid") + " " + "<http://www.grid.ac/institutes/" + localGridID + ">" + 
					" .\n");
//					/** Later we need a mapping of Grid ID to (MAG)Affiliation ID, so fill HashMap for that now (during iterating over Affiliations.txt): */
//					gridIDAffiliationEntityURIHashMap.put(localGridID, affiliationEntity);
				}
				
				// 6 homepage
				if(!affilliationLine[5].equals("")) {
					if(urlValidator.isValid((affilliationLine[5]))){
						affiliationsRDFOutput.append(affiliationEntity + " " + foafHomepageURI + " " + "<" + affilliationLine[5] + ">" + " .\n");
					}	
				}
				/** FYI: Not used 
				 *  if(urlValidator.isValid(affilliationLine[5])) { ..
				 *  and more, since http://www.ebrd.com//, https://web.archive.org/web/20120825125621/http://www.sumitomometals.co.jp/, etc.
				 *  were detected as invalid, but are actually valid/working. So no check here.
				 */
				// 7 wiki article
				if(!affilliationLine[6].equals("")) {
					// to Wikipedia
					affiliationsRDFOutput.append(affiliationEntity + " " + seeAlsoURI + " " + "<" + affilliationLine[6].replaceFirst("https://", "http://") + ">" + " .\n");
					// to DBpedia //FK https error resolved
					affiliationsRDFOutput.append(affiliationEntity + " " + sameAsURI + " " + "<" + (affilliationLine[6].replaceFirst("https://", "http://")).replaceFirst("http://en.wikipedia.org/wiki/", "http://dbpedia.org/resource/") + ">" + " .\n");
				}
				
				// 8 paper count
				if(!affilliationLine[7].equals(""))
					affiliationsRDFOutput.append(affiliationEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(affilliationLine[7]) + " .\n");
				
				// 9 citation count
				if(!affilliationLine[8].equals(""))
					affiliationsRDFOutput.append(affiliationEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(affilliationLine[8]) + " .\n");
				
				// 10 created date //FK 12 (old:10)
				if(!affilliationLine[11].equals(""))
					affiliationsRDFOutput.append(affiliationEntity + " " + createdURI + " " + returnXSDDate(affilliationLine[11]) + " .\n");

//				affiliationsRDFOutput.append("\n");
				outAffiliations.write(affiliationsRDFOutput.toString());
				outAffiliations.flush();
				affiliationsRDFOutput = new StringBuilder();
				line = inAffiliations.readLine();
			}
			inAffiliations.close();
			outAffiliations.flush();
			outAffiliations.close();
//			System.out.println(affiliationsRDFOutput.toString());
//			System.out.println("Size of hashmap: " + gridIDAffiliationEntityURIHashMap.size());
			System.out.println("### Finished Affiliations.txt ###");
			
			/** Authors.txt **/
			System.out.println("### Start Authors.txt ###");
			StringBuilder authorsRDFOutput = new StringBuilder();
			BufferedReader inAuthors = new BufferedReader(new FileReader(authors_));
			BufferedWriter outAuthors = new BufferedWriter(new FileWriter(authors_ + ".nt"));
			line = inAuthors.readLine();

			while (line != null) {

				String[] authorLine = line.split("\t");
				// 1
				String authorEntity = "<" + kgPrefix + "entity/" + authorLine[0] + ">";
				// entity type
				authorsRDFOutput.append(authorEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/Author>" + " .\n");
				
				// 2 rank
				if(!authorLine[1].equals(""))
					authorsRDFOutput.append(authorEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(authorLine[1]) + " .\n");
				
				// 4 name
				if(!authorLine[3].equals(""))
					authorsRDFOutput.append(authorEntity + " " + foafNameURI + " " + returnXSDString(authorLine[3]) + " .\n");
					//String chinese = NxUtil.escapeForMarkup(authorLine[3]);
					//FileUtils.writeStringToFile(new File(bwuniclusterpath + "test1.txt"), chinese + " .\n");
					//FileUtils.writeStringToFile(new File(bwuniclusterpath + "test2.txt"), StringEscapeUtils.unescapeHtml4(chinese) + " .\n");
				// 5 affiliation/member of -> Affiliation entity (use directly our entity, not grid.ac URL!)
				if(!authorLine[4].equals(""))
					authorsRDFOutput.append(authorEntity + " " + memberOfURI + " " + "<" + kgPrefix + "entity/" + authorLine[4] + ">" + " .\n");
				
				// 6 paper count
				authorsRDFOutput.append(authorEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(authorLine[5]) + " .\n");
				// 7 citation count
				authorsRDFOutput.append(authorEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(authorLine[6]) + " .\n");
				// 8 created date
				authorsRDFOutput.append(authorEntity + " " + createdURI + " " + returnXSDDate(authorLine[7]) + " .\n");
				
				outAuthors.write(authorsRDFOutput.toString());
				authorsRDFOutput = new StringBuilder();
				line = inAuthors.readLine();
//				authorsRDFOutput.append("\n");
			}
			inAuthors.close();
			outAuthors.flush();
			outAuthors.close();
//			System.out.println(authorsRDFOutput.toString());
			System.out.println("### Finished Authors.txt ###");
			
			/** ConferenceInstances.txt **/
			System.out.println("### Start ConferenceInstances.txt ###");
			StringBuilder conferenceInstancesRDFOutput = new StringBuilder();
			BufferedReader inConferenceInstances = new BufferedReader(new FileReader(conferenceInstances_));
			BufferedWriter outConferenceInstances = new BufferedWriter(new FileWriter(conferenceInstances_ + ".nt"));
			line = inConferenceInstances.readLine();
			
			while (line != null) {

				String[] conferenceInstanceLine = line.split("\t");
				// 1
				String conferenceInstanceEntity = "<" + kgPrefix + "entity/" + conferenceInstanceLine[0] + ">";
				// entity type
				conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/ConferenceInstance>" + " .\n");
				
				/** Changed in schema since at least 2018-11-09: no more rank column!) **/
//				// 2 rank
//				if(!conferenceInstanceLine[1].equals(""))
//					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(conferenceInstanceLine[1]) + " .\n");
				
				// 2 (old: 3) name
				if(!conferenceInstanceLine[2].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + foafNameURI + " " + returnXSDString(conferenceInstanceLine[2]) + " .\n");
				
				// 3 link to conference series entity, generate corresponding URI
				if(!conferenceInstanceLine[3].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + partOfURI + " " + "<" + kgPrefix + "entity/" + conferenceInstanceLine[3] + ">" + " .\n");
				
				// 5 (old: 6) location, link to DBpedia.
				if(!conferenceInstanceLine[4].equals("")) {
					/** USE X-LISA **/
					// e.g., in cache: http://dbpedia.org/resource/Karlsruhe
//					cacheTextAnnotations.entrySet().forEach(entry->{
//					    System.out.println(entry.getKey() + " " + entry.getValue());  
//					 }); 
					if(cacheTextAnnotations.containsKey(conferenceInstanceLine[4])) {
//						System.out.println("reuse");
						// if in cache, then reuse the annotation:
						conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + "<http://dbpedia.org/ontology/location>" + " " + "<" + cacheTextAnnotations.get(conferenceInstanceLine[4]) + ">" + " .\n");
					}
					else {
						String foundDBpediaEntity = null;
						List<TextAnnotation> dBPediaAnnotations = fac.getAnnotationsByWholeDocAnnotation(conferenceInstanceLine[4]);
//						System.out.println("NOW");
//						System.out.println(java.util.Arrays.toString(dBPediaAnnotations.toArray()));
						Iterator<TextAnnotation> iter = dBPediaAnnotations.iterator();
						boolean notFoundYet = true;
						while(iter.hasNext()) {
							TextAnnotation ta = iter.next();
							Iterator<MentionInAnnotation> iter2 = ta.getListOfMentions().iterator();
							while(iter2.hasNext()) {
								MentionInAnnotation mia = iter2.next();
								if(mia.getStart() == 0) {
									foundDBpediaEntity = ta.getURL_EN().replaceFirst("http://en.wikipedia.org/wiki/", "http://dbpedia.org/resource/");
									notFoundYet = false;
									break;
								}
							}
							if(notFoundYet=false) { // leave also second while loop.
								notFoundYet = true;
								break;
							}
						}
						if(foundDBpediaEntity != null) {
							conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + "<http://dbpedia.org/ontology/location>" + " " + "<" + foundDBpediaEntity + ">" + " .\n");
		//					System.out.println(foundDBpediaEntity + "\tfor\t" + conferenceInstanceLine[5]);
						}
						else {
							System.out.println("location not annotated with offset==0.");
						}
						// store in cache now: e.g., http://dbpedia.org/resource/Karlsruhe for Karlsruhe, Germany
						cacheTextAnnotations.put(conferenceInstanceLine[4], foundDBpediaEntity);
					}
				}
				
				//homepage
				/** same here: no more check with  if(urlValidator.isValid(conferenceInstanceLine[6])) {, since sometimes still valid URLs 
				 * **/
				if(!conferenceInstanceLine[5].equals(""))
					if(urlValidator.isValid((conferenceInstanceLine[5]))){
						conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + foafHomepageURI + " " + "<" + conferenceInstanceLine[5] + ">" + " .\n");
					}
				
				// start/end date
				if(!conferenceInstanceLine[6].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + startTimeURI + " " + returnXSDDate(conferenceInstanceLine[6]) + " .\n");
				if(!conferenceInstanceLine[7].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + endTimeURI + " " + returnXSDDate(conferenceInstanceLine[7]) + " .\n");
				
				// 9-12 (old: 10/11/12/13) abstract registration date....
				if(!conferenceInstanceLine[8].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("abstractRegistrationDate") +  " " + returnXSDDate(conferenceInstanceLine[8]) + " .\n");
				if(!conferenceInstanceLine[9].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("submissionDeadlineDate") +  " " + returnXSDDate(conferenceInstanceLine[9]) + " .\n");
				if(!conferenceInstanceLine[10].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("notificationDueDate") +  " " + returnXSDDate(conferenceInstanceLine[10]) + " .\n");
				if(!conferenceInstanceLine[11].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("finalVersionDueDate") +  " " + returnXSDDate(conferenceInstanceLine[11]) + " .\n");
				
				// 13 (old: 14) paper count
				if(!conferenceInstanceLine[12].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(conferenceInstanceLine[12]) + " .\n");
				
				// 14 (old: 15) citation count
				if(!conferenceInstanceLine[13].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(conferenceInstanceLine[13]) + " .\n");
				
				//FK 17
				if(!conferenceInstanceLine[16].equals(""))
					conferenceInstancesRDFOutput.append(conferenceInstanceEntity + " " + createdURI + " " + returnXSDDate(conferenceInstanceLine[16]) + " .\n");
				
				outConferenceInstances.write(conferenceInstancesRDFOutput.toString());
				conferenceInstancesRDFOutput = new StringBuilder();
				line = inConferenceInstances.readLine();
//				conferenceInstancesRDFOutput.append("\n");
			}
			inConferenceInstances.close();
			outConferenceInstances.flush();
			outConferenceInstances.close();
//			System.out.println("Conference instances:");
//			System.out.println(conferenceInstancesRDFOutput.toString());
			System.out.println("### Finished ConferenceInstances.txt ###");
			
			/** ConferenceSeries.txt **/
			System.out.println("### Start ConferenceSeries.txt ###");
			StringBuilder conferenceSeriesRDFOutput = new StringBuilder();
			BufferedReader inConferenceSeries = new BufferedReader(new FileReader(conferenceSeries_));
			BufferedWriter outConferenceSeries = new BufferedWriter(new FileWriter(conferenceSeries_ + ".nt"));
			line = inConferenceSeries.readLine();

			while (line != null) {

				String[] conferenceSeriesLine = line.split("\t");
				// 1
				String conferenceSeriesEntity = "<" + kgPrefix + "entity/" + conferenceSeriesLine[0] + ">";
				// entity type
				conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/ConferenceSeries>" + " .\n");
				
				// 2 rank
				if(!conferenceSeriesLine[1].equals(""))
					conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(conferenceSeriesLine[1]) + " .\n");
				// 4 name
				if(!conferenceSeriesLine[3].equals(""))
					conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + foafNameURI + " " + returnXSDString(conferenceSeriesLine[3]) + " .\n");
				
				// 5 paper count
				if(!conferenceSeriesLine[4].equals(""))
					conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(conferenceSeriesLine[4]) + " .\n");
				
				// 6 citation count
				if(!conferenceSeriesLine[5].equals(""))
					conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(conferenceSeriesLine[5]) + " .\n");
				
				// 7 created date
				if(!conferenceSeriesLine[6].equals(""))
					conferenceSeriesRDFOutput.append(conferenceSeriesEntity + " " + createdURI + " " + returnXSDDate(conferenceSeriesLine[6]) + " .\n");
				
				outConferenceSeries.write(conferenceSeriesRDFOutput.toString());
				conferenceSeriesRDFOutput = new StringBuilder();
				line = inConferenceSeries.readLine();
//				conferenceSeriesRDFOutput.append("\n");
			}
			inConferenceSeries.close();
			outConferenceSeries.flush();
			outConferenceSeries.close();
//			System.out.println("Conference series:");
//			System.out.println(conferenceSeriesRDFOutput.toString());
			System.out.println("### Finished ConferenceSeries.txt ###");
			
			
			/** FieldsOfStudy.txt **/
			System.out.println("### Start FieldsOfStudy.txt ###");
			StringBuilder fieldsOfStudyRDFOutput = new StringBuilder();
			BufferedReader inFieldsOfStudy = new BufferedReader(new FileReader(fieldsOfStudy_));
			BufferedWriter outFieldsOfStudy = new BufferedWriter(new FileWriter(fieldsOfStudy_ + ".nt"));
			line = inFieldsOfStudy.readLine();
			
			while (line != null) {

				String[] fieldOfStudyLine = line.split("\t");
				// 1
				String fieldOfStudyEntity = "<" + kgPrefix + "entity/" + fieldOfStudyLine[0] + ">";
				// entity type
				fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/FieldOfStudy>" + " .\n");
				// 2 rank
				if(!fieldOfStudyLine[1].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(fieldOfStudyLine[1]) + " .\n");
				// 4 name
				if(!fieldOfStudyLine[3].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + foafNameURI + " " + returnXSDString(fieldOfStudyLine[3]) + " .\n");
				// 5 main type/category, e.g., "biology.organism_classification"
				if(!fieldOfStudyLine[4].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + returnOwnProperty("category") + " " + returnXSDString(fieldOfStudyLine[4]) + " .\n");
				// 6 level
				if(!fieldOfStudyLine[5].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + returnOwnProperty("level") + " " + returnXSDInteger(fieldOfStudyLine[5]) + " .\n");
				// 7 paper count
				if(!fieldOfStudyLine[6].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(fieldOfStudyLine[6]) + " .\n");
				// 8 citation count
				if(!fieldOfStudyLine[7].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(fieldOfStudyLine[7]) + " .\n");
				// 9 created date
				if(!fieldOfStudyLine[8].equals(""))
					fieldsOfStudyRDFOutput.append(fieldOfStudyEntity + " " + createdURI + " " + returnXSDDate(fieldOfStudyLine[8]) + " .\n");
				
				outFieldsOfStudy.write(fieldsOfStudyRDFOutput.toString());
				fieldsOfStudyRDFOutput = new StringBuilder();
				line = inFieldsOfStudy.readLine();
//				fieldsOfStudyRDFOutput.append("\n");
			}
			inFieldsOfStudy.close();
			outFieldsOfStudy.flush();
			outFieldsOfStudy.close();
//			System.out.println("Fields of study:");
//			System.out.println(fieldsOfStudyRDFOutput.toString());
			System.out.println("### Finished FieldsOfStudy.txt ###");
			
			
			/** FieldsOfStudyChildren.txt **/
			System.out.println("### Start FieldsOfStudyChildren.txt ###");
			StringBuilder fieldsOfStudyChildrenRDFOutput = new StringBuilder();
			BufferedReader inFieldsOfStudyChildren = new BufferedReader(new FileReader(fieldOfStudyChildren_));
			BufferedWriter outFieldsOfStudyChildren = new BufferedWriter(new FileWriter(fieldOfStudyChildren_ + ".nt"));
			line = inFieldsOfStudyChildren.readLine();
			
			while (line != null) {
				
				String[] fieldOfStudyChildrenLine = line.split("\t");
				// 1
				String fieldEntity = "<" + kgPrefix + "entity/" + fieldOfStudyChildrenLine[0] + ">";
				String childEntity = "<" + kgPrefix + "entity/" + fieldOfStudyChildrenLine[1] + ">";
				// 2
				fieldsOfStudyChildrenRDFOutput.append(childEntity + " " + returnOwnProperty("hasParent") + " " + fieldEntity + " .\n");
				
				outFieldsOfStudyChildren.write(fieldsOfStudyChildrenRDFOutput.toString());
				fieldsOfStudyChildrenRDFOutput = new StringBuilder();
				line = inFieldsOfStudyChildren.readLine();
//				fieldsOfStudyChildrenRDFOutput.append("\n");
			}
			inFieldsOfStudyChildren.close();
			outFieldsOfStudyChildren.flush();
			outFieldsOfStudyChildren.close();
//			System.out.println("Fields of study children:");
//			System.out.println(fieldsOfStudyChildrenRDFOutput.toString());
			System.out.println("### Finished FieldsOfStudyChildren.txt ###");
			
			
			
			/** FieldOfStudyRelationship.txt **/
			System.out.println("### Start RelatedFieldOfStudy.txt ###");
			/** special table, contains in total 6 unique properties (from FieldOfStudy to FieldOfStudy) in the area of medicine.
			 * Use "Type 1" and "Type 2" to generate the property (name) **/
			StringBuilder fieldsOfStudyRelationshipRDFOutput = new StringBuilder();
			BufferedReader inFieldsOfStudyRelationship = new BufferedReader(new FileReader(fieldOfStudyRelationship_));
			BufferedWriter outFieldsOfStudyRelationship = new BufferedWriter(new FileWriter(fieldOfStudyRelationship_ + ".nt"));
			line = inFieldsOfStudyRelationship.readLine();
			

			while (line != null) {
				String[] fieldOfStudyRelationshipLine = line.split("\t");
				//FK entity2 -> fieldOfStudyRelationshipLine[2] (fieldOfStudyRelationshipLine[3])
				if(!fieldOfStudyRelationshipLine[0].equals("") && !fieldOfStudyRelationshipLine[2].equals("")) {
					// 1 entity one
					String entity1 = "<" + kgPrefix + "entity/" + fieldOfStudyRelationshipLine[0] + ">";
					// 4 entity two
					String entity2 = "<" + kgPrefix + "entity/" + fieldOfStudyRelationshipLine[2] + ">";
					// 
					String type1 = fieldOfStudyRelationshipLine[1]; //FK fieldOfStudyRelationshipLine[2] -> fieldOfStudyRelationshipLine[1]
					String type2 = fieldOfStudyRelationshipLine[3]; //FK fieldOfStudyRelationshipLine[5] -> fieldOfStudyRelationshipLine[3]
					
					if(type1.equals("disease") && type2.equals("symptom")) {
						String property = "diseaseHasSymptom";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("disease") && type2.equals("medical_treatment")) {
						String property = "diseaseHasMedicalTreatment";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("disease") && type2.equals("disease_cause")) {
						String property = "diseaseHasDiseaseCause";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("symptom") && type2.equals("disease")) {
						String property = "diseaseHasSymptom";
						// inverse relation (reuse property)
						fieldsOfStudyRelationshipRDFOutput.append(entity2 + " " + returnOwnProperty(property) + " " + entity1 + " .\n");
					}
					else if(type1.equals("disease_cause") && type2.equals("disease")) {
						String property = "diseaseHasDiseaseCause";
						// inverse relation (reuse property)
						fieldsOfStudyRelationshipRDFOutput.append(entity2 + " " + returnOwnProperty(property) + " " + entity1 + " .\n");
					}
					else if(type1.equals("medical_treatment") && type2.equals("disease")) {
						String property = "diseaseHasMedicalTreatment";
						// inverse relation (reuse property)
						fieldsOfStudyRelationshipRDFOutput.append(entity2 + " " + returnOwnProperty(property) + " " + entity1 + " .\n");
					}
					else if(type1.equals("disease_cause") && type2.equals("medical_treatment")) {
						String property = "medicalTreatmentForDiseaseCause";
						// inverse relation (reuse property)
						fieldsOfStudyRelationshipRDFOutput.append(entity2 + " " + returnOwnProperty(property) + " " + entity1 + " .\n");
					}
					else if(type1.equals("medical_treatment") && type2.equals("disease_cause")) {
						String property = "medicalTreatmentForDiseaseCause";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("medical_treatment") && type2.equals("symptom")) {
						String property = "medicalTreatmentForSymptom";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("symptom") && type2.equals("disease_cause")) {
						String property = "symptomHasDiseaseCause";
						fieldsOfStudyRelationshipRDFOutput.append(entity1 + " " + returnOwnProperty(property) + " " + entity2 + " .\n");
					}
					else if(type1.equals("disease_cause") && type2.equals("symptom")) {
						String property = "symptomHasDiseaseCause";
						// inverse relation (reuse property)
						fieldsOfStudyRelationshipRDFOutput.append(entity2 + " " + returnOwnProperty(property) + " " + entity1 + " .\n");
					}
					else {
						System.out.println("Relation not mapped to RDF: " + type1 + ", " + type2);
					}
					
					outFieldsOfStudyRelationship.write(fieldsOfStudyRelationshipRDFOutput.toString());
					fieldsOfStudyRelationshipRDFOutput = new StringBuilder();
					line = inFieldsOfStudyRelationship.readLine();
	//				fieldsOfStudyRelationshipRDFOutput.append("\n");
				}
			}
			inFieldsOfStudyRelationship.close();
			outFieldsOfStudyRelationship.flush();
			outFieldsOfStudyRelationship.close();
//			System.out.println("Fields of study relationship:");
//			System.out.println(fieldsOfStudyRelationshipRDFOutput.toString());
			System.out.println("### Finished RelatedFieldOfStudy.txt ###");
			
			
			
			/** Journals.txt **/
			System.out.println("### Start Journals.txt ###");
			StringBuilder journalsRDFOutput = new StringBuilder();
			BufferedReader inJournals = new BufferedReader(new FileReader(journals_));
			BufferedWriter outJournals = new BufferedWriter(new FileWriter(journals_ + ".nt"));
			line = inJournals.readLine();
			
			while (line != null) {

				String[] journalLine = line.split("\t");
				// 1
				String journalEntity = "<" + kgPrefix + "entity/" + journalLine[0] + ">";
				// entity type
				journalsRDFOutput.append(journalEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/Journal>" + " .\n");
				// 2 rank
				if(!journalLine[1].equals("")) //FK resolved error: previously journalLine[0].equals("")
					journalsRDFOutput.append(journalEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(journalLine[1]) + " .\n");
				// 4 name
				if(!journalLine[3].equals(""))
					journalsRDFOutput.append(journalEntity + " " + foafNameURI + " " + returnXSDString(journalLine[3]) + " .\n");
				// 5 ISSN
				if(!journalLine[4].equals(""))
					journalsRDFOutput.append(journalEntity + " " + issnURI + " " + returnXSDString(journalLine[4]) + " .\n");
				// 6 Publisher
				if(!journalLine[5].equals(""))
					journalsRDFOutput.append(journalEntity + " " + publisherURI + " " + returnXSDString(journalLine[5]) + " .\n");
				// 7 homepage
				if(!journalLine[6].equals("")) {
					journalsRDFOutput.append(journalEntity + " " + foafHomepageURI + " " + "<" + journalLine[6] + ">" + " .\n");
				}
				// 8 paper count
				if(!journalLine[7].equals(""))
					journalsRDFOutput.append(journalEntity + " " + returnOwnProperty("paperCount") + " " + returnXSDInteger(journalLine[7]) + " .\n");
				// 9 citation count
				if(!journalLine[8].equals(""))
					journalsRDFOutput.append(journalEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(journalLine[8]) + " .\n");
				// 10 created date
				if(!journalLine[9].equals(""))
					journalsRDFOutput.append(journalEntity + " " + createdURI + " " + returnXSDDate(journalLine[9]) + " .\n");
				
				outJournals.write(journalsRDFOutput.toString());
				journalsRDFOutput = new StringBuilder();
				line = inJournals.readLine();
//				journalsRDFOutput.append("\n");
			}
			inJournals.close();
			outJournals.flush();
			outJournals.close();
//			System.out.println("Journals:");
//			System.out.println(journalsRDFOutput.toString());
			System.out.println("### Finished Journals.txt ###");
			
			
			/** Papers.txt **/
			System.out.println("### Start Papers.txt ###");
			StringBuilder papersRDFOutput = new StringBuilder();
			BufferedReader inPapers = new BufferedReader(new FileReader(papers_));
			BufferedWriter outPapers = new BufferedWriter(new FileWriter(papers_ + ".nt"));
			line = inPapers.readLine();
			
			while (line != null) {

				String[] paperLine = line.split("\t");
				// 1
				String paperEntity = "<" + kgPrefix + "entity/" + paperLine[0] + ">";
				// entity type
				papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/Paper>" + " .\n");
				// 2 rank
				if(!paperLine[1].equals(""))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("rank") + " " + returnXSDInteger(paperLine[1]) + " .\n");
				// 3 DOI
				if(!paperLine[2].equals(""))
					papersRDFOutput.append(paperEntity + " " + doiURI +  returnXSDString(paperLine[2]) + " .\n");
				// 4 Doc type (can be [unknown (empty)], Journal, Patent, Conference, BookChapter, Book)
				if(!paperLine[3].equals("")) {
					if(paperLine[3].equals("Journal")) {
						papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + "http://purl.org/spar/fabio/JournalArticle" + ">" + " .\n");
					}
					else if (paperLine[3].equals("Patent")) {
						papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + "http://purl.org/spar/fabio/PatentDocument" + ">" + " .\n");
					}
					else if (paperLine[3].equals("Conference")) {
						papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + "http://purl.org/spar/fabio/ConferencePaper" + ">" + " .\n");
					}
					else if (paperLine[3].equals("BookChapter")) {
						papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + "http://purl.org/spar/fabio/BookChapter" + ">" + " .\n");
					}
					else if (paperLine[3].equals("Book")) {
						papersRDFOutput.append(paperEntity + " " + rdfTypeURI + " " + "<" + "http://purl.org/spar/fabio/Book" + ">" + " .\n");
					}
					else {
						System.out.println("others or not given: " + paperLine[3]);
					}
				}
				// 6 Original title as title
				if(!paperLine[5].equals(""))
					papersRDFOutput.append(paperEntity + " " + dctermsTitleURI + " " + returnXSDString(paperLine[5]) + " .\n");
				// 7 book title
				if(!paperLine[6].equals(""))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("bookTitle") + " " + returnXSDString(paperLine[6]) + " .\n");
				// 9 Date
				if(!paperLine[8].equals(""))
					papersRDFOutput.append(paperEntity + " " + publicationDateURI + " " + returnXSDDate(paperLine[8]) + " .\n");
				// 10 Publisher
				if(!paperLine[9].equals(""))
					papersRDFOutput.append(paperEntity + " " + publisherURI + " " + returnXSDString(paperLine[9]) + " .\n");
				// 11 Journal ID
				if(!paperLine[10].equals(""))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("appearsInJournal") + " " + "<" + kgPrefix + "entity/" + paperLine[10] + ">" + " .\n");
				// 12 Conference series ID
				if(!paperLine[11].equals(""))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("appearsInConferenceSeries") + " " + "<" + kgPrefix + "entity/" + paperLine[11] + ">" + " .\n");
				// 13 Conference instance ID
				if(!paperLine[12].equals(""))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("appearsInConferenceInstance") + " " + "<" + kgPrefix + "entity/" + paperLine[12] + ">" + " .\n");
				// 14 volume
				if(!paperLine[13].equals(""))
					papersRDFOutput.append(paperEntity + " " + "<http://prismstandard.org/namespaces/basic/2.0/volume>" + " " + returnXSDInteger(paperLine[13])  + " .\n");
				// 15 issue
				if(!paperLine[14].equals(""))
					papersRDFOutput.append(paperEntity + " " + "<http://prismstandard.org/namespaces/basic/2.0/issueIdentifier>" + " " + returnXSDString(paperLine[14])  + " .\n");
				/** be careful, noisy data: 
				 * <http://ma-graph.org/entity/2795711279> <http://prismstandard.org/namespaces/basic/2.0/startingPage> "-""^^<http://www.w3.org/2001/XMLSchema#integer> .
					<http://ma-graph.org/entity/2795711279> <http://prismstandard.org/namespaces/basic/2.0/endingPage> "-"^^<http://www.w3.org/2001/XMLSchema#integer> .
				 */
				// 16 first page
				if(!paperLine[15].equals("") && paperLine[15].matches("-?\\d+"))
					papersRDFOutput.append(paperEntity + " " + "<http://prismstandard.org/namespaces/basic/2.0/startingPage>" + " " + returnXSDInteger(paperLine[15]) + " .\n");
				// 17 last page
				if(!paperLine[16].equals("") && paperLine[16].matches("-?\\d+"))
					papersRDFOutput.append(paperEntity + " " + "<http://prismstandard.org/namespaces/basic/2.0/endingPage>" + " " + returnXSDInteger(paperLine[16]) + " .\n");
				// 18 Reference count
				if(!paperLine[17].equals("") && paperLine[17].matches("-?\\d+"))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("referenceCount") + " " + returnXSDInteger(paperLine[17]) + " .\n");
				// 19 citation count
				if(!paperLine[18].equals("") && paperLine[18].matches("-?\\d+"))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("citationCount") + " " + returnXSDInteger(paperLine[18]) + " .\n");
				// 20 estimated citation count
				if(!paperLine[19].equals("") && paperLine[19].matches("-?\\d+"))
					papersRDFOutput.append(paperEntity + " " + returnOwnProperty("estimatedCitationCount") + " " + returnXSDInteger(paperLine[19]) + " .\n");
				/** 21: OriginalVenue: left out (so far). **/
				// 22 (old: 21) created date //FK 23 (old: 22) created date
				if(!paperLine[22].equals(""))
					papersRDFOutput.append(paperEntity + " " + createdURI + " " + returnXSDDate(paperLine[22]) + " .\n");
				
				outPapers.write(papersRDFOutput.toString());
				papersRDFOutput = new StringBuilder();
				line = inPapers.readLine();
//				papersRDFOutput.append("\n");
			}
			inPapers.close();
			outPapers.flush();
			outPapers.close();
//			System.out.println("Papers:");
//			System.out.println(papersRDFOutput.toString());
			System.out.println("### Finished Papers.txt ###");
			
			
			/** PaperAbstractInvertedIndex.txt **/
			System.out.println("### Start PaperAbstractInvertedIndex.txt ###");
			StringBuilder paperAbstractInvertedIndexRDFOutput = new StringBuilder();
			BufferedReader inPaperAbstractInvertedIndex = new BufferedReader(new FileReader(paperAbstractsInvertedIndex_));
			BufferedWriter outPaperAbstractInvertedIndex = new BufferedWriter(new FileWriter(paperAbstractsInvertedIndex_ + ".nt"));
			line = inPaperAbstractInvertedIndex.readLine();	

			while (line != null) {

				String[] paperAbstractLine = line.split("\t");
				// 1
				String paperEntity = "<" + kgPrefix + "entity/" + paperAbstractLine[0] + ">";
				// 2: indexed abstract, tokenized
				/** processing of  paperLine[1] needed, since json object ( {"IndexLength":207,"InvertedIndex":{"In":[0,45],"the":[1,...) **/
				String currentAbstract = MAGJSONAbstractToText.getPaperAbstractFromJSON(paperAbstractLine[1]);
				
				
				if(currentAbstract != null && !currentAbstract.equals("")) {
					paperAbstractInvertedIndexRDFOutput.append(paperEntity + " " + "<http://purl.org/dc/terms/abstract>" + " " + returnXSDString(currentAbstract) + " .\n");
//					paperAbstractInvertedIndexRDFOutput.append("\n");
				}
				else {
					System.out.println("abstract missing.");
				}
				
				outPaperAbstractInvertedIndex.write(paperAbstractInvertedIndexRDFOutput.toString());
				paperAbstractInvertedIndexRDFOutput = new StringBuilder();
				line = inPaperAbstractInvertedIndex.readLine();
			}
			inPaperAbstractInvertedIndex.close();
			outPaperAbstractInvertedIndex.flush();
			outPaperAbstractInvertedIndex.close();
//			System.out.println("Abstracts:");
//			System.out.println(paperAbstractInvertedIndexRDFOutput.toString());
			System.out.println("### Finished PaperAbstractInvertedIndex.txt ###");
			
			
			/** PaperAuthorAffiliations.txt **/
			System.out.println("### Start PaperAuthorAffiliations.txt ###");
			StringBuilder paperAuthorAffiliationsRDFOutput = new StringBuilder();
			BufferedReader inPaperAuthorAffiliations = new BufferedReader(new FileReader(paperAuthorAffiliations_));
			BufferedWriter outPaperAuthorAffiliations = new BufferedWriter(new FileWriter(paperAuthorAffiliations_ + ".nt"));
			line = inPaperAuthorAffiliations.readLine();
			
			while (line != null) {
				
				String[] paperAuthorAffilLine = line.split("\t");
				if(!paperAuthorAffilLine[1].equals("")) {
					// 1 paper id
					String paperEntity = "<" + kgPrefix + "entity/" + paperAuthorAffilLine[0] + ">";
					// 2 author ID
					String authorEntity ="<" + kgPrefix + "entity/" + paperAuthorAffilLine[1] + ">";
					
					paperAuthorAffiliationsRDFOutput.append(paperEntity + " " + dctermsCreatorURI + " " + authorEntity + " .\n");
					// (3 affiliation already given in Authors.txt)
					// 4 Author sequence number: Only stored as n-ary relation (extended KG).
					
					outPaperAuthorAffiliations.write(paperAuthorAffiliationsRDFOutput.toString());
				}
				paperAuthorAffiliationsRDFOutput = new StringBuilder();
				line = inPaperAuthorAffiliations.readLine();
			}
			inPaperAuthorAffiliations.close();
			outPaperAuthorAffiliations.flush();
			outPaperAuthorAffiliations.close();
//			System.out.println("Paper author affiliations:");
//			System.out.println(paperAuthorAffiliationsRDFOutput.toString());
			System.out.println("### Finished PaperAuthorAffiliations.txt");
			
			
			/** PaperCitationContexts.txt **/
			System.out.println("### Start PaperCitationContexts.txt ###");
			StringBuilder paperCitationContextsRDFOutput = new StringBuilder();
			BufferedReader inPaperCitationContexts = new BufferedReader(new FileReader(paperCitationContexts_));
			BufferedWriter outPaperCitationContexts = new BufferedWriter(new FileWriter(paperCitationContexts_ + ".nt"));
			line = inPaperCitationContexts.readLine();
			
			while (line != null) {
				
				String[] paperCitationContexts = line.split("\t");
				
				if(!paperCitationContexts[2].equals("")) {
					// 1 paper
					String paperEntity = "<" + kgPrefix + "entity/" + paperCitationContexts[0] + ">";
					// 2 cited paper
					String citedPaperEntity = "<" + kgPrefix + "entity/" + paperCitationContexts[1] + ">";
					// create new entity and class Citation
					/** TODO: maybe change citationEntityURISuffix **/
					String citationEntityURISuffix = paperCitationContexts[0] + "-" + paperCitationContexts[1];
					String citationEntityURI = "<" + kgPrefix + "entity/" + citationEntityURISuffix + ">";
					
					paperCitationContextsRDFOutput.append(citationEntityURI + " " + rdfTypeURI + " " + "<" + kgPrefix + "class/Citation>" + " .\n");
					paperCitationContextsRDFOutput.append(citationEntityURI + " " + "<http://purl.org/spar/cito/hasCitingEntity>" + " " + paperEntity + " .\n");
					paperCitationContextsRDFOutput.append(citationEntityURI + " " + "<http://purl.org/spar/cito/hasCitedEntity>" + " " + citedPaperEntity + " .\n");
					
					// 3 citation context
					if(!paperCitationContexts[2].equals("")) {
						paperCitationContextsRDFOutput.append(citationEntityURI + " " + "<http://purl.org/spar/c4o/hasContext>" + " " + returnXSDString(paperCitationContexts[2]) +  " .\n");
					}
					
					outPaperCitationContexts.write(paperCitationContextsRDFOutput.toString());
				}
				paperCitationContextsRDFOutput = new StringBuilder();
				line = inPaperCitationContexts.readLine();
			}
			inPaperCitationContexts.close();
			outPaperCitationContexts.flush();
			outPaperCitationContexts.close();
//			System.out.println("Paper citation contexts:");
//			System.out.println(paperCitationContextsRDFOutput.toString());
			System.out.println("### Finished PaperCitationContexts.txt ###");
			
			
			/** PaperFieldsOfStudy.txt **/
			System.out.println("### Start PaperFieldsOfStudy.txt ###");
			StringBuilder paperFieldsOfStudyRDFOutput = new StringBuilder();
			BufferedReader inPaperFieldsOfStudy = new BufferedReader(new FileReader(paperFieldsOfStudy_));
			BufferedWriter outPaperFieldsOfStudy = new BufferedWriter(new FileWriter(paperFieldsOfStudy_ + ".nt"));
			line = inPaperFieldsOfStudy.readLine();
			
			while (line != null) {
				
				String[] paperFieldOfStudy = line.split("\t");
				if(!paperFieldOfStudy[1].equals("")) {
					// 1 paper
					String paperEntity = "<" + kgPrefix + "entity/" + paperFieldOfStudy[0] + ">";
					// 2 Field of study
					String fieldOfStudyEntity = "<" + kgPrefix + "entity/" + paperFieldOfStudy[1] + ">";
					paperFieldsOfStudyRDFOutput.append(paperEntity + " " + "<http://purl.org/spar/fabio/hasDiscipline>" + " " + fieldOfStudyEntity + " .\n");
					// 3 similarity: left out in this easy version, use only for n-ary relation (extended KG).
					
					outPaperFieldsOfStudy.write(paperFieldsOfStudyRDFOutput.toString());
				}
				paperFieldsOfStudyRDFOutput = new StringBuilder();
				line = inPaperFieldsOfStudy.readLine();
			}
			inPaperFieldsOfStudy.close();
			outPaperFieldsOfStudy.flush();
			outPaperFieldsOfStudy.close();
//			System.out.println("Paper fields of study:");
//			System.out.println(paperFieldsOfStudyRDFOutput.toString());
			System.out.println("### Finished PaperFieldsOfStudy.txt ###");
			
			
			/** PaperLanguages.txt **/
			System.out.println("### Start PaperLanguages.txt ###");
			StringBuilder paperLanguagesRDFOutput = new StringBuilder();
			BufferedReader inPaperLanguages = new BufferedReader(new FileReader(paperUrls_)); // FK language tag is now in paperUrls_
			BufferedWriter outPaperLanguages = new BufferedWriter(new FileWriter(paperLanguages_ + ".nt"));
			line = inPaperLanguages.readLine();
			
			while (line != null) {
				String[] paperLanguage = line.split("\t");
				if(paperLanguage.length > 3) { //FK check if language tag exists
					if(!paperLanguage[3].equals("")) { //FK was previously 1
						String paperEntity = "<" + kgPrefix + "entity/" + paperLanguage[0] + ">";
						// 2 language code for paper
						paperLanguagesRDFOutput.append(paperEntity + " " + dctermsLanguageURI + " " + returnXSDLang(paperLanguage[3]) + " .\n"); //FK was paperLanguage[1]
						
						outPaperLanguages.write(paperLanguagesRDFOutput.toString());
					}
				}
				
				paperLanguagesRDFOutput = new StringBuilder();
				line = inPaperLanguages.readLine();
			}
			inPaperLanguages.close();
			outPaperLanguages.flush();
			outPaperLanguages.close();
//			System.out.println("Paper languages:");
//			System.out.println(paperLanguagesRDFOutput.toString());
			System.out.println("### Finished PaperLanguages.txt ###");
			
			
			/** PaperRecommendations.txt left out **/
			
			/** PaperReferences.txt **/
			System.out.println("### Start PaperReferences.txt ###");
			StringBuilder paperReferencesRDFOutput = new StringBuilder();
			BufferedReader inPaperReferences = new BufferedReader(new FileReader(paperReferences_));
			BufferedWriter outPaperReferences = new BufferedWriter(new FileWriter(paperReferences_ + ".nt"));
			line = inPaperReferences.readLine();
			
			while (line != null) {
				
				String[] paperReference = line.split("\t");
				if(!paperReference[1].equals("")) {
					// 
					String paperEntity = "<" + kgPrefix + "entity/" + paperReference[0] + ">";
					// 2 paper reference ID (paper referenced)
					String referencedPaperEntity = "<" + kgPrefix + "entity/" + paperReference[1] + ">";
					// actually only :references, not :cites, but not given in ontology
					paperReferencesRDFOutput.append(paperEntity + " " + citoCitesURI + " " + referencedPaperEntity + " .\n");
					
					outPaperReferences.write(paperReferencesRDFOutput.toString());
				}
				paperReferencesRDFOutput = new StringBuilder();
				line = inPaperReferences.readLine();
			}
			inPaperReferences.close();
			outPaperReferences.flush();
			outPaperReferences.close();
//			System.out.println("Paper references:");
//			System.out.println(paperReferencesRDFOutput.toString());
			System.out.println("### Finished PaperReferences.txt ###");

			/** PaperUrls.txt **/
			System.out.println("### Start PaperUrls.txt ###");
			StringBuilder paperUrlsRDFOutput = new StringBuilder();
			BufferedReader inPaperUrls = new BufferedReader(new FileReader(paperUrls_));
			BufferedWriter outPaperUrls = new BufferedWriter(new FileWriter(paperUrls_ + ".nt"));
			line = inPaperUrls.readLine();
			
			while (line != null) {
				
				String[] paperUrl = line.split("\t");
				// 
				String paperEntity = "<" + kgPrefix + "entity/" + paperUrl[0] + ">";
				// 2 source type -- left out for plain version, only use for n-ary relations (extended KG)
				// 3 source URL
				/** no more else if(urlValidator.isValid(paperUrl[2])) {, since sometimes still valid **/
				if(!paperUrl[2].equals("")) {
						paperUrlsRDFOutput.append(paperEntity + " " + "<http://purl.org/spar/fabio/hasURL>" + " " + "<" + NxUtil.escapeForMarkup(paperUrl[2]) + ">" + " .\n");
						outPaperUrls.write(paperUrlsRDFOutput.toString());					
				}
				
				paperUrlsRDFOutput = new StringBuilder();
				line = inPaperUrls.readLine();
			}
			inPaperUrls.close();
			outPaperUrls.flush();
			outPaperUrls.close();
//			System.out.println("Paper URL:");
//			System.out.println(paperUrlsRDFOutput.toString());
			System.out.println("### Finished PaperUrls.txt ###");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("the end.");
	}
	
	public static String returnOwnProperty(String propName) {
		return "<" + kgPropertyPrefix + propName + ">";
	}
	
	// see https://jena.apache.org/documentation/notes/typed-literals.html
	public static String returnXSDInteger(String integer) {
		return "\"" + integer + "\"^^<http://www.w3.org/2001/XMLSchema#integer>";
	}
	
	// with escaping-correction from Nx parser, e.g., escape ' and "
	public static String returnXSDString(String str) {
		return "\"" + NxUtil.escapeForMarkup(str) + "\"^^<http://www.w3.org/2001/XMLSchema#string>";
	}
	
	public static String returnXSDDate(String date) {
		return "\"" + date + "\"^^<http://www.w3.org/2001/XMLSchema#date>";
	}
	
	public static String returnXSDLang(String lang) {
		return "\"" + lang + "\"^^<http://www.w3.org/2001/XMLSchema#language>";
	}
}
