package KeyWordExtracting;

/**
 * ChangeObject stores all the necessary information of the differences
 * @author Simon Kaeser, University of Zurich
 *
 */
public class ChangeObject {

	//defined weighting of changeobject
	protected double weighting;
	//Information from which field/class/method/package the keyword is extracted
	protected String extractedFrom;
	//actual change word
	protected String word;
	
	/**
	 * Creates new Changeobject and initializes variables.
	 * @param callhierarchyweighting
	 * @param exFrom
	 * @param word
	 */
	public ChangeObject(double callhierarchyweighting, String exFrom, String word){
	
		this.weighting = callhierarchyweighting;
		this.extractedFrom = exFrom;
		this.word = word;
	}
	/**Returns weighting of the keyword*/
	public double getWeighting() {
		return weighting;
	}
	/**Sets the weighting of the keyword*/
	public void setWeighting(int weighting) {
		this.weighting = weighting;
	}
	/**Returns String from where the keyword was extracted from*/
	public String getExtractedFrom() {
		return extractedFrom;
	}
	/**Sets the field/method/class/package from which the keyword was extracted*/
	public void setExtractedFrom(String extractedFrom) {
		this.extractedFrom = extractedFrom;
	}
	/**Returns the keyword*/
	public String getWord() {
		return word;
	}
	/**Sets the keyword of the ChangeObject*/
	public void setWord(String word) {
		this.word = word;
	}
	
	
	
}
