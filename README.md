# MAG2RDF

MAG2RDF contains the code for generating RDF files of the __[Microsoft Academic Knowledge Graph (MAKG)](http://ma-graph.org)__ .

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
