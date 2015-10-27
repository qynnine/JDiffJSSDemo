package jdiff;

import java.util.ArrayList;
import java.util.List;

import KeyWordExtracting.Settings;

/**
 * Class to represent a class, analogous to ClassDoc in the Javadoc doclet API.
 * 
 * The method used for Collection comparison (compareTo) must make its
 * comparison based upon everything that is known about this class.
 * 
 * See the file LICENSE.txt for copyright details.
 * 
 * @author Matthew Doar, mdoar@pobox.com
 */
class ClassAPI implements Comparable {

	/** Name of the class, not fully qualified. */
	public String name_;

	/** Set if this class is an interface. */
	public boolean isInterface_;

	/** Set if this class is abstract. */
	boolean isAbstract_ = false;

	/** Modifiers for this class. */
	public Modifiers modifiers_;

	/** Name of the parent class, or null if there is no parent. */
	public String extends_; // Can only extend zero or one class or interface

	/** Interfaces implemented by this class. */
	public List implements_; // String[]

	/** Constructors in this class. */
	public List ctors_; // ConstructorAPI[]

	/** Methods in this class. */
	public List methods_; // MethodAPI[]

	/** SK - Returns all Methods of Class */
	public List getMethods_() {
		return methods_;
	}

	/** Fields in this class. */
	public List fields_; // FieldAPI[]

	/** The doc block, default is null. */
	public String doc_ = null;

	/** Constructor. */
	public ClassAPI(String name, String parent, boolean isInterface,
			boolean isAbstract, Modifiers modifiers) {
		name_ = name;
		extends_ = parent;
		isInterface_ = isInterface;
		isAbstract_ = isAbstract;
		modifiers_ = modifiers;

		implements_ = new ArrayList(); // String[]
		ctors_ = new ArrayList(); // ConstructorAPI[]
		methods_ = new ArrayList(); // MethodAPI[]
		fields_ = new ArrayList(); // FieldAPI[]
	}

	/** Compare two ClassAPI objects by all the known information. */
	public int compareTo(Object o) {
		ClassAPI oClassAPI = (ClassAPI) o;
		int comp = name_.compareTo(oClassAPI.name_);
		if (comp != 0)
			return comp;
		//SK - enable possibility to enable/disable differencing of class signatures
		if(Settings.enableClassSignatureDifferencing){
			if (isInterface_ != oClassAPI.isInterface_)
				return -1;
			if (isAbstract_ != oClassAPI.isAbstract_)
				return -1;
			comp = modifiers_.compareTo(oClassAPI.modifiers_);
			if (comp != 0)
				return comp;
			if (APIComparator.docChanged(doc_, oClassAPI.doc_))
				return -1;
		}
		return 0;
	}

	/**
	 * Tests two methods for equality using just the class name, used by
	 * indexOf().
	 */
	public boolean equals(Object o) {
		if (name_.compareTo(((ClassAPI) o).name_) == 0)
			return true;
		return false;
	}

	/** SK - Returns Name of Class */
	public String getName_() {
		return name_;
	}

}
