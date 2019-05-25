# MAG2RDF

MAG2RDF contains the code for creating the __Microsoft Academic Knowledge Graph (MAKG)__.

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
* PaperReferences.txt
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

## Contact
In case of questions or requests, feel free to contact us at [Michael Färber](https://sites.google.com/view/michaelfaerber), michael.faerber@kit&#46;edu
