package KeyWordExtracting;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import jdiff.HTMLReportGenerator;
import callGraph.JCallGraph;

/**
 * KeyWordExtractor consists of a list of changes (including changeobject).
 * Stores all Changes from added, removed or changed differences.
 * 
 * @author Simon Kaeser, University of Zurich
 * 
 */
public class KeyWordExtractor {
	// stores all Changes of removed, added or changed changes
	public Hashtable<Integer, Change> ht;
	public int changeNr;

	/**
	 * Creates new KeywordExtractor and initializes new hashtable ht.
	 */
	public KeyWordExtractor() {
		ht = new Hashtable<Integer, Change>();
		changeNr = 0;
	}

	/**
	 * adds a change to the hashtable.
	 * 
	 * @param change
	 */
	public void addChange(Change change) {
		ht.put(changeNr++, change);
	}

	/**
	 * returns Vector with changeObjects at position key in hashtable.
	 */
	public Change getChange(int key) {
		Change tempChange = ht.get(key);
		return tempChange;
	}

	/**
	 * prints all elements in hashtable - for testing purposes
	 */
	public void print() {
		Enumeration<Integer> keys = ht.keys();
		while (keys.hasMoreElements()) {
			int i = keys.nextElement();
			System.out.println("Change " + i + ": " + ht.get(i).getFullName());
			for (int j = 0; j < ht.get(i).differenceVector.size(); j++) {
				System.out.print(ht.get(i).getDifferenceVector().get(j)
						.getWord()
						+ "("
						+ ht.get(i).getDifferenceVector().get(j).getWeighting()
						+ ") ");
			}
			System.out.println("");
		}
	}

	/**
	 * Sorts elements in hashtable according to their weighting
	 */
	public void sort() {
		Enumeration<Integer> keys = ht.keys();
		while (keys.hasMoreElements()) {
			int i = keys.nextElement();
			Vector<ChangeObject> differenceVectorOld = ht.get(i)
					.getDifferenceVector();
			Vector<ChangeObject> tempDifferenceVector = new Vector<ChangeObject>();
			while (!differenceVectorOld.isEmpty()) {
				double tempHighestWeighting = -1;
				int tempHighestWeightingPosition = -1;
				// go through elements and select the one with the highest
				// weighting
				for (int k = 0; k < differenceVectorOld.size(); k++) {
					if (differenceVectorOld.get(k).getWeighting() > tempHighestWeighting) {
						tempHighestWeighting = differenceVectorOld.get(k)
								.getWeighting();
						tempHighestWeightingPosition = k;
					}
				}
				// add element with current highest weighting to the
				// temporaryDifferenceVector and remove it from the old one
				tempDifferenceVector.addElement(differenceVectorOld
						.get(tempHighestWeightingPosition));
				differenceVectorOld
						.removeElementAt(tempHighestWeightingPosition);
			}
			// set tempVector as the new sorted differenceVector
			ht.get(i).differenceVector = tempDifferenceVector;
		}
	}

	/**
	 * Adds Call Hierarchy to extracted Keywords/Changes
	 */
	public void addCallHierarchy(int versionNr) {
		JCallGraph callGraph;
		if (versionNr == 1) {
			callGraph = HTMLReportGenerator.callGraphV1;
		} else {
			callGraph = new JCallGraph(Settings.JarVersion2Path);
		}
		for (int i = 0; i < ht.size(); i++) {
			// if checks if it is a changed class, if yes call hierarchy will
			// not be added!
			if (ht.get(i).getExtractionType()!=null && !ht.get(i).getExtractionType()
					.equalsIgnoreCase("Changed Class")
					&& !ht.get(i).getExtractionType()
							.equalsIgnoreCase("Changed Constructor")) {
				String changeName = ht.get(i).getFullName();
				if (callGraph.isCalledByGraphMap.containsKey(changeName)) {
					for (int j = 0; j < callGraph.isCalledByGraphMap.get(
							changeName).size(); j++) {
						String tempWord = callGraph.isCalledByGraphMap.get(
								changeName).get(j);
						ht.get(i).add(Settings.callHierarchyWeighting,
								tempWord, tempWord);
					}
				}
				if (callGraph.superClasses.containsKey(changeName)) {
					String superClassName = callGraph.superClasses.get(
							changeName).toString();
					ht.get(i).add(Settings.callHierarchyWeighting,
							superClassName, superClassName);
				}
			}
		}
	}

	/**
	 * Checks renamings by comparing KeyWordExtractorAdded and
	 * KeyWordExtractorRemoved. If two elements of the two Extractors have the
	 * same call hierarchy, they are compared by LeventhsteinDistanceAlgorithm.
	 * If the similarity is below a certain value it is marked as renaming and
	 * deleted from the two extractors.
	 * 
	 * @param extractorRemoved
	 * @author Simon Kaeser, University of Zurich
	 */
	public void checkRenamings(KeyWordExtractor extractorRemoved) {
		Hashtable<String, Vector<String>> isCalledByGraphMap = HTMLReportGenerator.callGraphV1.isCalledByGraphMap;
		Hashtable<String, Vector<String>> callGraphMap = HTMLReportGenerator.callGraphV1.callGraphMap;
		JCallGraph callGraphV2 = new JCallGraph(Settings.JarVersion2Path);
		Vector<Integer> elementsToRemoveAdded = new Vector<Integer>();
		Vector<Integer> elementsToRemoveRemoved = new Vector<Integer>();

		int htSize = ht.size();
		int extractorRemovedHtSize = extractorRemoved.ht.size();
		for (int i = 0; i < htSize; i++) {
			for (int j = 0; j < extractorRemovedHtSize; j++) {

				if (callGraphV2.isCalledByGraphMap.containsKey(ht.get(i)
						.getFullName())
						&& isCalledByGraphMap.containsKey(extractorRemoved.ht
								.get(j).getFullName())) {
					if (callGraphV2.isCalledByGraphMap.get(
							ht.get(i).getFullName()).equals(
							isCalledByGraphMap.get(extractorRemoved.ht.get(j)
									.getFullName()))) {

						if (checkLevenshtein(ht.get(i).getFullName(),
								extractorRemoved.ht.get(j).getFullName(),
								Settings.levenshteinDistanceLimit)) {
							System.out.println("Found Renaming! from "
									+ extractorRemoved.ht.get(j).getFullName()
									+ " to " + ht.get(i).getFullName());
							elementsToRemoveAdded.add(i);
							elementsToRemoveRemoved.add(j);
						}
					}
					if (callGraphV2.isCalledByGraphMap.get(
							ht.get(i).getFullName()).containsAll(
							isCalledByGraphMap.get(extractorRemoved.ht.get(j)
									.getFullName()))
							&& isCalledByGraphMap.get(
									extractorRemoved.ht.get(j).getFullName())
									.containsAll(
											callGraphV2.isCalledByGraphMap
													.get(ht.get(i)
															.getFullName()))) {
						if (checkLevenshtein(ht.get(i).getFullName(),
								extractorRemoved.ht.get(j).getFullName(),
								Settings.levenshteinDistanceLimit)) {
							System.out.println("Found Renaming! from "
									+ extractorRemoved.ht.get(j).getFullName()
									+ " to " + ht.get(i).getFullName());
							elementsToRemoveAdded.add(i);
							elementsToRemoveRemoved.add(j);
						}
					}
				} else {
					if (checkLevenshtein(ht.get(i).getFullName(),
							extractorRemoved.ht.get(j).getFullName(),
							Settings.levenshteinDistanceLimitNoCallHierarchy)) {
						System.out.println("Found Renaming! from "
								+ extractorRemoved.ht.get(j).getFullName()
								+ " to " + ht.get(i).getFullName());
						elementsToRemoveAdded.add(i);
						elementsToRemoveRemoved.add(j);
					}
				}
			}
		}
		// remove elements which are marked as renamings from
		// extractorAdded-Hashtable
		for (int t = elementsToRemoveAdded.size() - 1; t >= 0; t--) {
			ht.remove(elementsToRemoveAdded.get(t));
		}
		// remove elements which are marked as renmaings from
		// extractorRemoved-Hashtable
		for (int t = elementsToRemoveRemoved.size() - 1; t >= 0; t--) {
			extractorRemoved.ht.remove(elementsToRemoveRemoved.get(t));
		}

	}

	/**
	 * Calls getLevenshteinDistance and checks if distance is smaller than
	 * predefined limit. If distance is smaller return true, else false.
	 * 
	 * @param added
	 * @param removed
	 * @param levensteinDistance
	 * @return
	 * @author Simon Kaeser, University of Zurich
	 */
	private boolean checkLevenshtein(String added, String removed,
			int levensteinDistance) {
		if (getLevenshteinDistance(added, removed) < levensteinDistance) {
			return true;
		} else
			return false;
	}

	/**
	 * LevenshteinDistanceAlgorithm copied from
	 * http://www.merriampark.com/ldjava.htm MICHAEL GILLELAND 1736 Hague Avenue
	 * St. Paul, MN 55104 651-644-8837 (home) megilleland@yahoo.com
	 * 
	 * @param s
	 * @param t
	 * @return
	 */
	private static int getLevenshteinDistance(String s, String t) {
		if (s == null || t == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		int n = s.length(); // length of s
		int m = t.length(); // length of t

		if (n == 0) {
			return m;
		} else if (m == 0) {
			return n;
		}

		int p[] = new int[n + 1]; // 'previous' cost array, horizontally
		int d[] = new int[n + 1]; // cost array, horizontally
		int _d[]; // placeholder to assist in swapping p and d

		// indexes into strings s and t
		int i; // iterates through s
		int j; // iterates through t

		char t_j; // jth character of t

		int cost; // cost

		for (i = 0; i <= n; i++) {
			p[i] = i;
		}

		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;

			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
						+ cost);
			}

			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}

		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];
	}

	/**
	 * Removes standard words related to codingstyle predefined in
	 * KeyWordExtractor.Settings.wordsToFilterOut[] like has/is/set/get and so
	 * on.
	 * 
	 * @author Simon Kaeser, University of Zurich
	 */
	public void removeStdWords() {
		Enumeration<Integer> keys = ht.keys();
		Vector<Integer> elementsToRemove = new Vector<Integer>();
		while (keys.hasMoreElements()) {
			int i = keys.nextElement();
			for (int j = 0; j < ht.get(i).getDifferenceVector().size(); j++) {
				String tempWord = ht.get(i).getDifferenceVector().get(j)
						.getWord().trim();
				//check if elements are in vector and note found matchings to vector "elementsToRemove"
				for (int k = 0; k < Settings.wordsToFilterOut.length; k++) {
					if (tempWord.equalsIgnoreCase(Settings.wordsToFilterOut[k])) {
						elementsToRemove.add(j);
					}
				}
			}
			//remove elements from vector
			for (int t = elementsToRemove.size() - 1; t >= 0; t--) {
				ht.get(i).getDifferenceVector()
						.removeElementAt(elementsToRemove.get(t));
			}
			elementsToRemove.clear();

		}
	}
}
