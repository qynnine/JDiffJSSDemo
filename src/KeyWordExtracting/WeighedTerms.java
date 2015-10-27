package KeyWordExtracting;


public class WeighedTerms {

	String term;
	double weight;
	
	
	public WeighedTerms(String NTerm, double NWeight) {
		
		term = NTerm;
		weight= NWeight;
	}
	
	
	public String getTerm() {
		return term;
	}
	
	
	public double getWeight() {
		return weight;
	}
	
	
	public void setTerm(String term) {
		this.term = term;
	}	
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
