/////////////////////////////////////////////////////////////////////////////
// $Id$  
package org.dom4j;

import org.dom4j.tree.BackedList;

import java.util.EnumSet;

/**
 * @author Jirsák Filip
 * @version $Revision$
 */
public final class NodeHelper {

	private static final EnumSet<NodeType> BRANCH_TYPE = EnumSet.of(NodeType.ELEMENT_NODE, NodeType.DOCUMENT_NODE);

	private NodeHelper() {
	}

	public static Attribute nodeAsAttribute(Node node) {
		if (node.getNodeTypeEnum() == NodeType.ATTRIBUTE_NODE) {
			assert node instanceof Attribute;
			return (Attribute) node;
		}
		return null;
	}

	public static Branch nodeAsBranch(Node node) {
		if (BRANCH_TYPE.contains(node.getNodeType())) {
			assert node instanceof Branch;
			return (Branch) node;
		}
		return null;
	}

	public static Document nodeAsDocument(Node node) {
		if (node.getNodeTypeEnum() == NodeType.DOCUMENT_NODE) {
			assert node instanceof Document;
			return (Document) node;
		}
		return null;
	}

	/**
	 * If node type is element, returns node typed as <code>Element</code>, or <code>null</code> otherwise.
	 * @param node
	 * @return Element node or <code>null</code>
	 */
	public static Element nodeAsElement(Node node) {
		if (node.getNodeTypeEnum() == NodeType.ELEMENT_NODE) {
			assert node instanceof Element;
			return (Element) node;
		}
		return null;
	}

	public static Namespace nodeAsNamespace(Node node) {
		if (node.getNodeTypeEnum() == NodeType.NAMESPACE_NODE) {
			assert node instanceof Namespace;
			return (Namespace) node;
		}
		return null;
	}

	public static ProcessingInstruction nodeAsProcessingInstruction(Node node) {
		if (node.getNodeTypeEnum() == NodeType.PROCESSING_INSTRUCTION_NODE) {
			assert node instanceof ProcessingInstruction;
			return (ProcessingInstruction) node;
		}
		return null;
	}

	public static BackedList<Element> appendElementLocal(Node node, BackedList<Element> list) {
		Element element = nodeAsElement(node);
		if (element != null) {
			list.addLocal(element);
		}

		return list;
	}

	public static BackedList<Element> appendElementNamedLocal(Node node, BackedList<Element> list, String name) {
		Element element = nodeAsElement(node);
		if (element != null && name.equals(element.getName())) {
			list.addLocal(element);
		}

		return list;
	}

	public static BackedList<Element> appendElementQNamedLocal(Node node, BackedList<Element> list, QName qname) {
		Element element = nodeAsElement(node);
		if (element != null && qname.equals(element.getQName())) {
			list.addLocal(element);
		}

		return list;
	}

	public static String getAttributeValue(Attribute attribute, String defaultValue) {
		return attribute == null ? defaultValue : attribute.getValue();
	}
}
