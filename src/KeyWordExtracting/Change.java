package KeyWordExtracting;

import java.util.Vector;
import jdiff.APIComparator;

/**
 * Creates new Change Object to insert differences between program versions.
 * 
 * @author Simon Kaeser, University of Zurich
 * 
 */
public class Change {
	// Vector containing all ChangeObjects (splitted words)
	public Vector<ChangeObject> differenceVector = new Vector<ChangeObject>();
	// full name of the change(with packagename etc.)
	private String fullName;
	// name of the field/method/class
	private String name;

	/**
	 * Return name of field, method, class or package
	 * 
	 * @return name of changeobject
	 * @author Simon Kaeser, University of Zurich
	 */
	public String getName() {
		return name;
	}

	// contains information if its a removeed/added field, class, method, etc.
	private String extractionType;

	// name of the Class
	private String className;

	/**
	 * Returns ClassName
	 * 
	 * @return classname
	 * @author Simon Kaeser, University of Zurich
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns information what kind of chagne it is (Removed Field, Added
	 * Field, etc.)
	 * 
	 * @return extractionType
	 * @author Simon Kaeser, University of Zurich
	 */
	public String getExtractionType() {
		return extractionType;
	}

	Vector<String> tempVector2 = new Vector<String>();

	/**
	 * Creates new Change
	 * 
	 * @param changeName
	 */
	public Change(String changeName) {
		this.fullName = changeName;
		if (APIComparator.classContent.containsKey(changeName)
				|| APIComparator.packageContent.containsKey(changeName)) {
			Vector<String> tempVector3 = new Vector<String>();

			if (APIComparator.classContent.containsKey(changeName)) {
				tempVector3 = APIComparator.classContent.get(changeName);
			} else if (APIComparator.packageContent.containsKey(changeName)) {
				tempVector3 = APIComparator.packageContent.get(changeName);
			}
			for (int i = 0; i < tempVector3.size(); i++) {
				tempVector2.addAll(splitWords(tempVector3.get(i)));
				// System.out.println(splitWords(tempVector3.get(i).toString()));
			}
		}

	}

	/**
	 * Creates new Change
	 * 
	 * @param changeName
	 * @param extractedFrom
	 */
	public Change(String changeName, String extractedFrom) {
		this.fullName = changeName;
		this.extractionType = extractedFrom;
		if (APIComparator.classContent.containsKey(changeName)
				|| APIComparator.packageContent.containsKey(changeName)) {
			Vector<String> tempVector3 = new Vector<String>();
			if (APIComparator.classContent.containsKey(changeName)) {
				tempVector3 = APIComparator.classContent.get(changeName);
			} else if (APIComparator.packageContent.containsKey(changeName)) {
				tempVector3 = APIComparator.packageContent.get(changeName);
			}
			for (int i = 0; i < tempVector3.size(); i++) {
				tempVector2.addAll(splitWords(tempVector3.get(i)));
			}
		}

	}

	/**
	 * Creates new Change
	 * 
	 * @param changeName
	 * @param extractedFrom
	 * @param className
	 */
	public Change(String changeName, String extractedFrom, String className) {
		this.className = className;
		this.fullName = changeName;
		this.extractionType = extractedFrom;
		if (APIComparator.classContent.containsKey(changeName)
				|| APIComparator.packageContent.containsKey(changeName)) {
			Vector<String> tempVector3 = new Vector<String>();
			if (APIComparator.classContent.containsKey(changeName)) {
				tempVector3 = APIComparator.classContent.get(changeName);
			} else if (APIComparator.packageContent.containsKey(changeName)) {
				tempVector3 = APIComparator.packageContent.get(changeName);
			}
			for (int i = 0; i < tempVector3.size(); i++) {
				tempVector2.addAll(splitWords(tempVector3.get(i)));
			}
		}

	}

	/** Returns full Name of Change */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Adds new Keywords to the Change
	 * 
	 * @param weighting
	 * @param changeFrom
	 * @param word
	 * @author Simon Kaeser, University of Zurich
	 */
	public void add(double callhierarchyweighting, String changeFrom,
			String word) {
		Vector<String> tempVector;
		tempVector = splitWords(word);

		// if (!tempVector2.isEmpty()) {
		// tempVector.addAll(tempVector2);
		// tempVector2.clear();
		// }

		for (int i = 0; i < tempVector.size(); i++) {
			Boolean exists = false;
			if (Settings.noDuplicates) {
				for (int j = 0; j < differenceVector.size(); j++) {
					// checks if keyword is already included
					if (differenceVector.get(j).getWord()
							.equals(tempVector.get(i))) {
						exists = true;
					}
				}
			}
			// if keyword isn't included yet create a new Changeobject and add
			// it to differencevector
			if (Settings.noDuplicates) {
				if (!exists || differenceVector.isEmpty()) {
					ChangeObject changeObject = new ChangeObject(callhierarchyweighting,
							changeFrom, tempVector.get(i));
					differenceVector.add(changeObject);
				}
			} else {
				ChangeObject changeObject = new ChangeObject(callhierarchyweighting,
						changeFrom, tempVector.get(i));
				differenceVector.add(changeObject);
			}
		}
	if (!tempVector2.isEmpty()) {
			for (int i = 0; i < tempVector2.size(); i++) {
				Boolean exists = false;
				if (Settings.noDuplicates) {
					for (int j = 0; j < differenceVector.size(); j++) {
						// checks if keyword is already included
						if (differenceVector.get(j).getWord()
								.equals(tempVector2.get(i))) {
							exists = true;
						}
					}
				}
				// if keyword isn't included yet create a new Changeobject and
				// add
				// it to differencevector
				if (Settings.noDuplicates) {
					if (!exists || differenceVector.isEmpty()) {
						ChangeObject changeObject = new ChangeObject(
								Settings.subElementWeighting, changeFrom,
								tempVector2.get(i));
						differenceVector.add(changeObject);
					}
				} else {
					ChangeObject changeObject = new ChangeObject(
							Settings.subElementWeighting, changeFrom,
							tempVector2.get(i));
					differenceVector.add(changeObject);
				}
			}
		}
	}

	/**
	 * Adds new Keywords to the Change from the documentation of a method
	 * 
	 * @param docweighting
	 * @param changeFrom
	 * @param word
	 * @author Simon Kaeser, University of Zurich
	 */
	public void addDoc(double docweighting, String changeFrom,
			String word) {
		Vector<String> tempVector;
		tempVector = splitDoc(word);
		for (int i = 0; i < tempVector.size(); i++) {
			
			ChangeObject changeObject = new ChangeObject(
				docweighting, changeFrom, tempVector.get(i));
			differenceVector.add(changeObject);
		}
	}

	/** Returns Vector with all ChangeObjects */
	public Vector<ChangeObject> getDifferenceVector() {
		return differenceVector;
	}

	/**
	 * Splits a given String to sub-Words, separated by CamelCase.
	 * 
	 * @param str
	 *            String to be splitted by CamelCase
	 * @return Vector with separated words
	 */
	private Vector<String> splitWords(String str) {
		Vector<String> vector = new Vector<String>();
		String tempStr = new String();
		Boolean tempCharBeforeIsUpperCase = false;
		Boolean charAfterIsUpperCase = false;
		Boolean charAfterIsLetter = false;
		for (int i = 0; i < str.length(); i++) {
			Character tempChar = str.charAt(i);
			if (i > 0) {
				if (i + 1 < str.length()) {
					Character charAfter = str.charAt(i + 1);
					charAfterIsUpperCase = charAfter.isUpperCase(charAfter);
					charAfterIsLetter = charAfter.isLetter(charAfter);
				}
				Character charBefore = str.charAt(i - 1);
				tempCharBeforeIsUpperCase = charBefore.isUpperCase(charBefore);
			}

			if (!tempChar.isLetter(tempChar)) {
				if (tempStr.length() > 1) {
					if (Settings.stemmingOn) {
						Settings.stemmerEN.setCurrent(tempStr);
						Settings.stemmerEN.stem();
						vector.add(Settings.stemmerEN.getCurrent());
					} else {
						vector.add(tempStr);
					}
					tempStr = "";
				}
			} else if ((tempChar.isLowerCase(tempChar) || tempStr.length() < 2 || (tempCharBeforeIsUpperCase && (charAfterIsUpperCase || !charAfterIsLetter)))) {
				if (tempStr.length() == 0) {
					tempChar = tempChar.toLowerCase(tempChar);
					tempStr = tempChar.toString();
				} else {
					tempChar = tempChar.toLowerCase(tempChar);
					tempStr = tempStr.concat(tempChar.toString());
				}
			} else {
				if (Settings.stemmingOn) {
					Settings.stemmerEN.setCurrent(tempStr);
					Settings.stemmerEN.stem();
					vector.add(Settings.stemmerEN.getCurrent());
				} else {
					vector.add(tempStr);
				}
				tempStr = "";
				tempChar = tempChar.toLowerCase(tempChar);
				tempStr = tempChar.toString();
			}
		}
		if (!tempStr.equalsIgnoreCase("")) {
			if (Settings.stemmingOn) {
				Settings.stemmerEN.setCurrent(tempStr);
				Settings.stemmerEN.stem();
				vector.add(Settings.stemmerEN.getCurrent());
			} else {
				vector.add(tempStr);
			}
		}
		return vector;
		
		// The following code was used to get the elements names (without split and without package name) for Exp 5-A
		/*String term =str;
		if (str.contains(".")){
			term = str.substring(str.lastIndexOf(".")+1);
		}else 
			term =str;
		 
		term = term.replaceAll("<", " ");
		term = term.replaceAll(">", " ");
		vector.add(term);
		System.out.println("############### TERM #################");
		System.out.println("########TERM :"+term);
		return vector;*/
	}

	/** Set name of a change */
	public void setName(String name) {
		this.name = name;

	}
	
	public Vector<String> splitDoc(String str) {
		Vector<String> terms = new Vector<String>();
		String tempStr;
		tempStr = str.trim();
		//This part is including a lot of problems because the signs > and < are used not only used in tags, so we keep tags
		/*//delete all tags
		while (tempStr.indexOf("<")>=0){
			int start = tempStr.indexOf("<");
			int end = tempStr.indexOf(">");
			//manage cases where the sign is the first/last character in the string
			if(end>=0 && end<start)
				tempStr = tempStr.replaceFirst(">", " ");
			else if(start ==0){
				if(end==-1)
					tempStr = tempStr.substring(1);// delete the symbol
				else if (end==tempStr.length()-1)
					tempStr= ""; //we do not consider the documentation in this case
				else
					tempStr = tempStr.substring(end+1);
			}
			else if(end==tempStr.length()-1){
				tempStr= tempStr.substring(0, start-1);
			}
			else
				tempStr= tempStr.substring(0, start-1)+tempStr.substring(end+1);
		}*/
		
		
		//remove special characters
		tempStr = tempStr.replaceAll("[^a-zA-Z0-9]+"," ");
		
		while(tempStr.length()>1){
			
			int spaceIndex = tempStr.indexOf(" ");
			String word;
			if(spaceIndex>0){
				word =tempStr.substring(0, spaceIndex);
				tempStr= tempStr.substring(spaceIndex).trim();
			}
			else{
				word = tempStr;
				tempStr="";
			}
			if(word!=null && word.trim().length()>1 )
				terms.add(word);
		}
		return terms;
	}
}