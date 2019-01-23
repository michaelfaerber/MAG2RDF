package textannotation;

import java.io.Serializable;

public class MentionInAnnotation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8784154675105316498L;
	private String surfaceform; // == "words" in xml output
	private int start;
	private int end;
	
	public MentionInAnnotation(String surfaceform, int start, int end) {
		this.surfaceform = surfaceform;
		this.start = start;
		this.end = end;
	}
	
	public String getSurfaceform() {
		return surfaceform;
	}
	public void setSurfaceform(String surfaceform) {
		this.surfaceform = surfaceform;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	@Override
    public String toString() {
		return surfaceform + ": " + this.start + "-" + this.end;
				//+ ";";
	}
}
