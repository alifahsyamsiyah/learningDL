/////////////////////////////////////////////////////////////////////////////
// $Id$  
package org.dom4j;

/**
 * @author Jirsák Filip
 * @version $Revision$
 */
public enum NodeType {

	/**
	 * Matches Element nodes
	 */
	ANY_NODE((short) 0, "Node"),
	/**
	 * Matches Element nodes
	 */
	ELEMENT_NODE((short) 1, "Element"),
	/**
	 * Matches elements nodes
	 */
	ATTRIBUTE_NODE((short) 2, "Attribute"),
	/**
	 * Matches elements nodes
	 */
	TEXT_NODE((short) 3, "Text"),
	/**
	 * Matches elements nodes
	 */
	CDATA_SECTION_NODE((short) 4, "CDATA"),
	/**
	 * Matches elements nodes
	 */
	ENTITY_REFERENCE_NODE((short) 5, "Entity"),
	/**
	 * Matches elements nodes
	 */
	ENTITY_NODE((short) 6, "Entity"),
	/**
	 * Matches ProcessingInstruction
	 */
	PROCESSING_INSTRUCTION_NODE((short) 7, "ProcessingInstruction"),
	/**
	 * Matches Comments nodes
	 */
	COMMENT_NODE((short) 8, "Comment"),
	/**
	 * Matches Document nodes
	 */
	DOCUMENT_NODE((short) 9, "Document"),
	/**
	 * Matches DocumentType nodes
	 */
	DOCUMENT_TYPE_NODE((short) 10, "DocumentType"),
	DOCUMENT_FRAGMENT_NODE((short) 11, "DocumentFragment"),
	NOTATION_NODE((short) 12, "Notation"),
	/**
	 * Matchs a Namespace Node - NOTE this differs from DOM
	 */
	NAMESPACE_NODE((short) 13, "Namespace"),
	/**
	 * Does not match any valid node
	 */
	UNKNOWN_NODE((short) 14, "Unknown"),;

	private final short code;
	private final String name;
	private static final NodeType[] byCode = new NodeType[values().length];

	static {
		for (NodeType nodeType : values()) {
			byCode[nodeType.code] = nodeType;
		}
	}

	private NodeType(final short code, final String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * Return W3C DOM compliant node type code.
	 */
	public short getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public static NodeType byCode(short code) {
		if (code < 0 || code >= byCode.length) {
			return null;
		}
		NodeType nodeType = byCode[code];
		assert nodeType != null;
		return nodeType;
	}
}
