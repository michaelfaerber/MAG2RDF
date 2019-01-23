package textannotation;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;



// dependant on text annotation web service implementation v2.
//@XmlRootElement(name = "textannotation")
public class TextAnnotation implements Serializable {

	private String displayName; // e.g. "Barack Obama"; this is the label of the DBpedia entity
	private float weight; // e.g., 0.614
	private int entityID; // enumeration over all entities in the XML annotation result, i.e. 1,2,... Actually not needed so far
	// Nov 2015: besides EN, now also other languages:
	private String URL_EN;
	private String URL_ES;
	private String URL_FR;
	private String URL_DE;
	private String URL_ZH;
	private String lang = "en"; // fixed; otherwise, create Collection with <URL,lang>

	// each entity-annotation has a list of mentions in the text
	// contained in Mention: 'words'=surface form, 'start', 'end'
	private List<MentionInAnnotation> listOfMentionsInAnnotation;

	public TextAnnotation() {
	}
	
	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}


	public int getEntityID() {
		return entityID;
	}


	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}


	public String getURL_EN() {
		return URL_EN;
	}

	public void setURL_EN(String uRL_EN) {
		URL_EN = uRL_EN;
	}

	public String getURL_ES() {
		return URL_ES;
	}

	public void setURL_ES(String uRL_ES) {
		URL_ES = uRL_ES;
	}

	public String getURL_FR() {
		return URL_FR;
	}

	public void setURL_FR(String uRL_FR) {
		URL_FR = uRL_FR;
	}

	public String getURL_DE() {
		return URL_DE;
	}

	public void setURL_DE(String uRL_DE) {
		URL_DE = uRL_DE;
	}

	public String getURL_ZH() {
		return URL_ZH;
	}

	public void setURL_ZH(String uRL_ZH) {
		URL_ZH = uRL_ZH;
	}


	public String getLang() {
		return lang;
	}


	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<MentionInAnnotation> getListOfMentions() {
		return listOfMentionsInAnnotation;
	}

	public void setListOfMentions(List<MentionInAnnotation> listOfMentions) {
		this.listOfMentionsInAnnotation = listOfMentions;
	}
	
	@Override
    public String toString() {
       return "\n" + this.displayName + "; " + this.URL_EN + "; " + this.getListOfMentions();
    		   //"Annotation: " + this.displayName + ", " + this.getListOfMentions().toString() + ";" + "\n";
    		   //this.URL + ", " + this.weight + " "+ this.getListOfMentions().toString() + ";";
    }

}
