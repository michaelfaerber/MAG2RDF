# MAG2RDF

MAG2RDF contains the code for generating the __[Microsoft Academic Knowledge Graph (MAKG)](http://ma-graph.org)__ in RDF.

For more information, see [The Microsoft Academic Knowledge Graph: A Linked Data Source with 8 Billion Triples of Scholarly Data](http://dbis.informatik.uni-freiburg.de/content/team/faerber/papers/MAKG_ISWC2019.pdf).

The main class is [MAG2RDF](MAG2RDF/src/main/MAG2RDF.java).


## Input
The generation of the MAKG RDF data requires to have the data dump files of the Microsoft Academic Graph. Specifically, the following files are needed:

* Affiliations.txt
* Authors.txt
* ConferenceInstances.txt
* ConferenceSeries.txt
* FieldOfStudyChildren.txt
* RelatedFieldOfStudy.txt
* FieldsOFStudy.txt
* Journals.txt
* PaperAbstractsInvertedIndex.txt
* PaperAuthorAffiliations.txt
* PaperCitationContexts.txt
* PaperFieldsOfStudy.txt
* PaperLanguages.txt
* PaperReferences.txtA. MAG Dump-Files:

	1. Affiliations.txt.nt
		i.   Wiki articles were expected to be https. A conversion of wiki to dbpedia was not achieved. FIXED
		ii.  Created date is now in column 12 (previously 10)
		
	2. ConferenceInstances.txt.nt
		i.   Created date is now in column 17 (previously 15)
		
	3. FieldOfStudyRelationship.txt.nt
		i.   Entity two is now in column 3 (previously 4)
		ii.  Type one is now in column 2 (previously 3)
		iii. Type two is now in column 4 (previously 6)
		
	4. Journals.txt.nt
		i.   Created date is now in column 23 (previously 22)
		
	5. PaperLanguages.txt.nt
		i.   File doesn't exist anymore. If a paper has a tagged language, the language can be found in column 4 of PaperUrls.txt.
		
B: MAG2RDF Code:

	1. textannotation/TullTextAnnotationClientXML
		i.   Line 113: Changed 'new ByteArrayInputStream(xmlstring.getBytes())' to 'new ByteArrayInputStream(xmlstring.getBytes(Charsets.UTF_8))'
		ii.  Line 263: Changed '.type(MediaType.APPLICATION_XML)' to '.type(MediaType.APPLICATION_XML + "; charset=UTF-8")'
* Papers.txt
* PaperUrls.txt

To obtain these files, please follow the instructions at https://docs.microsoft.com/en-us/academic-services/graph/get-started-setup-provisioning.

## Processing
Compile [MAG2RDF.java](MAG2RDF/src/main/MAG2RDF.java), create the corresponding jar file and run
```
java MAG2RDF.jar
```

## Output
For each input file, the program creates a corresponding output file in the RDF format:

* Affiliations.txt.nt
* Authors.txt.nt
* ConferenceInstances.txt.nt
* ConferenceSeries.txt.nt
* FieldOfStudyChildren.txt.nt
* RelatedFieldOfStudy.txt.nt
* FieldsOFStudy.txt.nt
* Journals.txt.nt
* PaperAbstractsInvertedIndex.txt.nt
* PaperAuthorAffiliations.txt.nt
* PaperCitationContexts.txt.nt
* PaperFieldsOfStudy.txt.nt
* PaperLanguages.txt.nt
* PaperReferences.txt.nt
* Papers.txt.nt
* PaperUrls.txt.nt

## Contact & More Information
More information can be found in my ISWC'19 paper [The Microsoft Academic Knowledge Graph: A Linked Data Source with 8 Billion Triples of Scholarly Data](http://dbis.informatik.uni-freiburg.de/content/team/faerber/papers/MAKG_ISWC2019.pdf).

Feel free to reach out to me in case of questions or comments:

[Michael Färber](https://sites.google.com/view/michaelfaerber), michael.faerber@kit.edu

## How to Cite
Please cite my work (described in [this paper](http://dbis.informatik.uni-freiburg.de/content/team/faerber/papers/MAKG_ISWC2019.pdf)) as follows:
```
@inproceedings{DBLP:conf/semweb/Farber19,
  author    = {Michael F{\"{a}}rber},
  title     = "{The Microsoft Academic Knowledge Graph: {A} Linked Data Source with
               8 Billion Triples of Scholarly Data}",
  booktitle = "{Proceedings of the 18th International Semantic Web Conference}",
  series    = "{ISWC'19}",
  location  = "{Auckland, New Zealand}",
  pages     = {113--129},
  year      = {2019},
  url       = {https://doi.org/10.1007/978-3-030-30796-7\_8},
  doi       = {10.1007/978-3-030-30796-7\_8}
}

```

## Last Major Updates
* 2020-07-09
* 2019-07-15

## Changes for Version 2020-07-09

A. MAG Dump-Files:

	1. Affiliations.txt.nt
		i.   Wiki articles were expected to be https. A conversion of wiki to dbpedia was not achieved. FIXED
		ii.  Created date is now in column 12 (previously 10)
		
	2. ConferenceInstances.txt.nt
		i.   Created date is now in column 17 (previously 15)
		
	3. FieldOfStudyRelationship.txt.nt
		i.   Entity two is now in column 3 (previously 4)
		ii.  Type one is now in column 2 (previously 3)
		iii. Type two is now in column 4 (previously 6)
		
	4. Journals.txt.nt
		i.   Created date is now in column 23 (previously 22)
		
	5. PaperLanguages.txt.nt
		i.   File doesn't exist anymore. If a paper has a tagged language, the language can be found in column 4 of PaperUrls.txt.
		
B: MAG2RDF Code:

	1. textannotation/TullTextAnnotationClientXML
		i.   Line 113: Changed 'new ByteArrayInputStream(xmlstring.getBytes())' to 'new ByteArrayInputStream(xmlstring.getBytes(Charsets.UTF_8))'
		ii.  Line 263: Changed '.type(MediaType.APPLICATION_XML)' to '.type(MediaType.APPLICATION_XML + "; charset=UTF-8")'
