/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.dom4j.tree;

import org.dom4j.*;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * <code>DefaultElement</code> is the default DOM4J default implementation of
 * an XML element.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.59 $
 */
public class DefaultElement extends AbstractElement {

	/**
	 * The <code>DocumentFactory</code> instance used by default
	 */
	private static final transient DocumentFactory DOCUMENT_FACTORY =
			DocumentFactory.getInstance();
	/**
	 * The <code>QName</code> for this element
	 */
	private QName qname;
	/**
	 * Stores the parent branch of this node which is either a Document if this
	 * element is the root element in a document, or another Element if it is a
	 * child of the root document, or null if it has not been added to a
	 * document yet.
	 */
	private Branch parentBranch;
	/**
	 * Stores null for no content or a List
	 * for multiple content nodes. The List will be lazily constructed when
	 * required.
	 */
	private List<Node> content = new LazyList<Node>();
	/**
	 * Lazily constructes list of attributes
	 */
	private final List<Attribute> attributes;

	public DefaultElement(String name) {
		this(DOCUMENT_FACTORY.createQName(name));
	}

	public DefaultElement(QName qname) {
		this(qname, 0);
	}

	public DefaultElement(QName qname, int attributeCount) {
		this.qname = qname;
		this.attributes = new LazyList<Attribute>();
	}

	public DefaultElement(String name, Namespace namespace) {
		this(DOCUMENT_FACTORY.createQName(name, namespace));
	}

	@Override
	public Element getParent() {
		Element result = null;

		if (parentBranch instanceof Element) {
			result = (Element) parentBranch;
		}

		return result;
	}

	@Override
	public void setParent(Element parent) {
		if (parentBranch instanceof Element || (parent != null)) {
			parentBranch = parent;
		}
	}

	@Override
	public Document getDocument() {
		if (parentBranch instanceof Document) {
			return (Document) parentBranch;
		} else if (parentBranch instanceof Element) {
			Element parent = (Element) parentBranch;

			return parent.getDocument();
		}

		return null;
	}

	@Override
	public void setDocument(Document document) {
		if (parentBranch instanceof Document || (document != null)) {
			parentBranch = document;
		}
	}

	@Override
	public boolean supportsParent() {
		return true;
	}

	public QName getQName() {
		return qname;
	}

	public void setQName(QName name) {
		this.qname = name;
	}

	@Override
	public String getStringValue() {
		if (contentList().size() == 0) {
			return "";
		}

		if (contentList().size() == 1) {
			return getContentAsStringValue(contentList().get(0));
		}

		StringBuilder builder = new StringBuilder();
		for (Node node : contentList()) {
			String string = getContentAsStringValue(node);
			if (string.length() > 0) {
				if (USE_STRINGVALUE_SEPARATOR) {
					if (builder.length() > 0) {
						builder.append(' ');
					}
				}
				builder.append(string);
			}
		}
		return builder.toString();
	}

	@Override
	public DefaultElement clone() {
		DefaultElement answer = (DefaultElement) super.clone();

		if (answer != this) {
			CloneHelper.setFinalLazyList(DefaultElement.class, answer, "attributes");
			CloneHelper.setFinalContent(DefaultElement.class, answer);
			answer.appendAttributes(this);
			answer.appendContent(this);
		}

		return answer;
	}

	@Override
	public Namespace getNamespaceForPrefix(String prefix) {
		if (prefix == null) {
			prefix = "";
		}

		if (prefix.equals(getNamespacePrefix())) {
			return getNamespace();
		} else if (prefix.equals("xml")) {
			return Namespace.XML_NAMESPACE;
		} else {
			List<Node> list = contentList();
			for (Node node : list) {
				Namespace namespace = NodeHelper.nodeAsNamespace(node);
				if (namespace != null && prefix.equals(namespace.getPrefix())) {
					return namespace;
				}
			}
		}

		Element parent = getParent();

		if (parent != null) {
			Namespace answer = parent.getNamespaceForPrefix(prefix);

			if (answer != null) {
				return answer;
			}
		}

		if ((prefix == null) || (prefix.length() <= 0)) {
			return Namespace.NO_NAMESPACE;
		}

		return null;
	}

	@Override
	public Namespace getNamespaceForURI(String uri) {
		if ((uri == null) || (uri.length() <= 0)) {
			return Namespace.NO_NAMESPACE;
		} else if (uri.equals(getNamespaceURI())) {
			return getNamespace();
		} else {
			List<Node> list = contentList();
			for (Node node : list) {
				Namespace namespace = NodeHelper.nodeAsNamespace(node);
				if (namespace != null && uri.equals(namespace.getURI())) {
					return namespace;
				}
			}

			Element parent = getParent();
			if (parent != null) {
				return parent.getNamespaceForURI(uri);
			}

			return null;
		}
	}

	@Override
	public List<Namespace> declaredNamespaces() {
		BackedList<Namespace> answer = createResultList();

		if (this.content != null) {
			for (Node node : this.content) {
				Namespace namespace = NodeHelper.nodeAsNamespace(node);
				if (namespace != null) {
					answer.addLocal(namespace);
				}
			}
		}

		return answer;
	}

	@Override
	public List<Namespace> additionalNamespaces() {
		BackedList<Namespace> answer = createResultList();
		for (Node node : contentList()) {
			Namespace namespace = NodeHelper.nodeAsNamespace(node);
			if (namespace != null && !namespace.equals(getNamespace())) {
				answer.addLocal(namespace);
			}
		}
		return answer;
	}

	@Override
	public List<Namespace> additionalNamespaces(String defaultNamespaceURI) {
		BackedList<Namespace> answer = createResultList();
		for (Node node : contentList()) {
			Namespace namespace = NodeHelper.nodeAsNamespace(node);
			if (namespace != null && !defaultNamespaceURI.equals(namespace.getURI())) {
				answer.addLocal(namespace);
			}
		}
		return answer;
	}

// Processing instruction API

	@Override
	public List<ProcessingInstruction> processingInstructions() {
		BackedList<ProcessingInstruction> answer = createResultList();
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null) {
				answer.addLocal(pi);
			}

		}
		return answer;
	}

	@Override
	public List<ProcessingInstruction> processingInstructions(String target) {
		BackedList<ProcessingInstruction> answer = createResultList();
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null && target.equals(pi.getName())) {
				answer.addLocal(pi);
			}
		}
		return answer;
	}

	@Override
	public ProcessingInstruction processingInstruction(String target) {
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null && target.equals(pi.getName())) {
				return pi;
			}
		}

		return null;
	}

	@Override
	public boolean removeProcessingInstruction(String target) {
		for (Iterator<? extends Node> iterator = this.content.iterator(); iterator.hasNext();) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(iterator.next());
			if (pi != null && target.equals(pi.getName())) {
				iterator.remove();
				return true;
			}
		}

		return false;
	}

	@Override
	public Element element(String name) {
		for (Node node : contentList()) {
			Element element = NodeHelper.nodeAsElement(node);
			if (element != null && name.equals(element.getName())) {
				return element;
			}
		}

		return null;
	}

	@Override
	public Element element(QName qName) {
		for (Node node : contentList()) {
			Element element = NodeHelper.nodeAsElement(node);
			if (element != null && qName.equals(element.getQName())) {
				return element;
			}
		}

		return null;
	}

	@Override
	public Element element(String name, Namespace namespace) {
		return element(getDocumentFactory().createQName(name, namespace));
	}

	public void setContent(List<Node> content) {
		contentRemoved();

		if (content instanceof ContentListFacade) {
			content = ((ContentListFacade<Node>) content).getBackingList();
		}

		List<Node> newContent = createContentList();

		if (content != null) {
			for (Node node : content) {
				Element parent = node.getParent();

				if ((parent != null) && (parent != this)) {
					node = (Node) node.clone();
				}

				newContent.add(node);
				childAdded(node);
			}
		}

		this.content = newContent;
	}

	public void clearContent() {
		assert this.content != null;
		contentRemoved();
		this.content.clear();
	}

	@Override
	public Node node(int index) {
		if (index < 0 || index >= contentList().size()) {
			return null;
		}
		return contentList().get(index);
	}

	@Override
	public int indexOf(Node node) {
		return contentList().indexOf(node);
	}

	@Override
	public int nodeCount() {
		return contentList().size();
	}

	@Override
	public Iterator<Node> nodeIterator() {
		return contentList().iterator();
	}

	@Override
	public List<Attribute> attributes() {
		return new ContentListFacade<Attribute>(this, attributeList());
	}

	public void setAttributes(List<Attribute> attributes) {
		if (attributes instanceof ContentListFacade) {
			attributes = ((ContentListFacade<Attribute>) attributes).getBackingList();
		}

		this.attributes.clear();
		if (attributes != null) {
			this.attributes.addAll(attributes);
		}
	}

	@Override
	public Iterator<Attribute> attributeIterator() {
		return attributeList().iterator();
	}

	@Override
	public Attribute attribute(int index) {
		if (index < 0 || index >= attributeList().size()) {
			return null;
		}
		return attributeList().get(index);
	}

	@Override
	public int attributeCount() {
		return attributeList().size();
	}

	@Override
	public Attribute attribute(String name) {
		for (Attribute attribute : attributeList()) {
			if (name.equals(attribute.getName())) {
				return attribute;
			}
		}

		return null;
	}

	@Override
	public Attribute attribute(QName qName) {
		for (Attribute attribute : attributeList()) {
			if (qName.equals(attribute.getQName())) {
				return attribute;
			}
		}

		return null;
	}

	@Override
	public Attribute attribute(String name, Namespace namespace) {
		return attribute(getDocumentFactory().createQName(name, namespace));
	}

	@Override
	public void add(Attribute attribute) {
		if (attribute.getParent() != null) {
			String message = "The Attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"";

			throw new IllegalAddException(this, attribute, message);
		}

		if (attribute.getValue() == null) {
			// try remove a previous attribute with the same
			// name since adding an attribute with a null value
			// is equivalent to removing it.
			Attribute oldAttribute = attribute(attribute.getQName());

			if (oldAttribute != null) {
				remove(oldAttribute);
			}

		} else {
			attributeList().add(attribute);
			childAdded(attribute);
		}

	}

	@Override
	public boolean remove(Attribute attribute) {
		boolean answer = attributeList().remove(attribute);

		if (!answer) {
			// we may have a copy of the attribute
			Attribute copy = attribute(attribute.getQName());

			if (copy != null) {
				answer = attributeList().remove(copy);
			}
		}

		if (answer) {
			childRemoved(attribute);
		}

		return answer;
	}

	@Override
	protected void addNewNode(Node node) {
		contentList().add(node);
		childAdded(node);
	}

	@Override
	protected boolean removeNode(Node node) {
		boolean answer = contentList().remove(node);
		if (answer) {
			childRemoved(node);
		}

		return answer;
	}

	protected List<Node> contentList() {
		assert this.content != null;
		return this.content;
	}

	protected void setAttributeList(List<Attribute> attributeList) {
		attributeList().clear();
		attributeList().addAll(attributeList);
	}

	@Override
	protected DocumentFactory getDocumentFactory() {
		DocumentFactory factory = qname.getDocumentFactory();
		return (factory != null) ? factory : DOCUMENT_FACTORY;
	}

	@Override
	protected List<Attribute> attributeList() {
		assert this.attributes != null;
		return attributes;
	}

	@Override
	@Deprecated
	protected List<Attribute> attributeList(int attributeCount) {
		//TODO
		assert this.attributes != null;
		return attributes;
	}
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 * 
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 * 
 * 5. Due credit should be given to the DOM4J Project - http://dom4j.sourceforge.net
 * 
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
