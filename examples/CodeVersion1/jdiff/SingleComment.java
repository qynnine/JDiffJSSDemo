package jdiff;

import java.io.*;
import java.util.*;

/**
 * Represents a single comment element. Has an identifier and some text.
 * 
 * See the file LICENSE.txt for copyright details.
 * @author Matthew Doar, doar@pobox.com
 */
class SingleComment implements Comparable {

    /** The identifier for this comment. */
    public String id_ = null;

    /** The text of this comment. */
    public String text_ = null;

    /** If false, then this comment is inactive. */
    public boolean isUsed_ = true;

    public SingleComment(String id, String text) {
        id_ = id;
        text_ = text;
    }

    /** Compare two SingleComment objects using just the id. */
    public int compareTo(Object o) {
        return id_.compareTo(((SingleComment)o).id_);
    }
}
