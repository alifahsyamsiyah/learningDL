/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.dom4j.tree;

import org.dom4j.*;
import org.xml.sax.EntityResolver;

import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * <code>DefaultDocument</code> is the default DOM4J default implementation of
 * an XML document.
 * </p>
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan </a>
 * @version $Revision: 1.34 $
 */
public class DefaultDocument extends AbstractDocument {

	/**
	 * The name of the document
	 */
	private String name;
	/**
	 * The root element of this document
	 */
	private Element rootElement;
	/**
	 * Store the contents of the document as a lazily created <code>List</code>
	 */
	private final List<Node> content = new LazyList<Node>();
	/**
	 * The document type for this document
	 */
	private DocumentType docType;
	/**
	 * The document factory used by default
	 */
	private DocumentFactory documentFactory = DocumentFactory.getInstance();
	/**
	 * The resolver of URIs
	 */
	private transient EntityResolver entityResolver;

	public DefaultDocument() {
		this(null, null, null);
	}

	public DefaultDocument(String name) {
		this(name, null, null);
	}

	public DefaultDocument(Element rootElement) {
		this(null, rootElement, null);
	}

	public DefaultDocument(DocumentType docType) {
		this(null, null, docType);
	}

	public DefaultDocument(Element rootElement, DocumentType docType) {
		this(null, rootElement, docType);
	}

	public DefaultDocument(String name, Element rootElement, DocumentType docType) {
		this.name = name;
		this.setRootElement(rootElement);
		this.docType = docType;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public Element getRootElement() {
		return rootElement;
	}

	public DocumentType getDocType() {
		return docType;
	}

	public void setDocType(DocumentType docType) {
		this.docType = docType;
	}

	public Document addDocType(String docTypeName, String publicId, String systemId) {
		setDocType(getDocumentFactory().createDocType(docTypeName, publicId, systemId));

		return this;
	}

	@Override
	public String getXMLEncoding() {
		return encoding;
	}

	public EntityResolver getEntityResolver() {
		return entityResolver;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	@Override
	public DefaultDocument clone() {
		DefaultDocument document =  (DefaultDocument) super.clone();
		document.rootElement = null;
		CloneHelper.setFinalContent(DefaultDocument.class, document);
		document.appendContent(this);

		return document;
	}

	public List<ProcessingInstruction> processingInstructions() {
		List<ProcessingInstruction> answer = createResultList();
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null) {
				answer.add(pi);
			}
		}

		return answer;
	}

	public List<ProcessingInstruction> processingInstructions(String target) {
		List<ProcessingInstruction> answer = createResultList();
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null && target.equals(pi.getName())) {
				answer.add(pi);
			}
		}

		return answer;
	}

	public ProcessingInstruction processingInstruction(String target) {
		for (Node node : contentList()) {
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null && target.equals(pi.getName())) {
				return pi;
			}
		}

		return null;
	}

	public boolean removeProcessingInstruction(String target) {
		for (Iterator<Node> iter = contentList().iterator(); iter.hasNext();) {
			Node node = iter.next();
			ProcessingInstruction pi = NodeHelper.nodeAsProcessingInstruction(node);
			if (pi != null && target.equals(pi.getName())) {
				iter.remove();
				return true;
			}
		}

		return false;
	}

	public void setContent(List<Node> content) {
		rootElement = null;
		contentRemoved();

		if (content instanceof ContentListFacade) {
			content = ((ContentListFacade<Node>) content).getBackingList();
		}

		contentList().clear();
		if (content == null) {
			return;
		}

		List<Node> newContent = createContentList();
		for (Node node : content) {
			Document doc = node.getDocument();
			if ((doc != null) && (doc != this)) {
				node = (Node) node.clone();
			}

			Element element = NodeHelper.nodeAsElement(node);
			if (element != null) {
				if (rootElement == null) {
					rootElement = element;
				} else {
					throw new IllegalAddException("A document may only contain one root element: " + content);
				}
			}

			newContent.add(node);
			childAdded(node);
		}
		contentList().addAll(newContent);
	}

	public void clearContent() {
		contentRemoved();
		contentList().clear();
		rootElement = null;
	}

	public void setDocumentFactory(DocumentFactory documentFactory) {
		this.documentFactory = documentFactory;
	}

	protected List<Node> contentList() {
		assert this.content != null;
		return this.content;
	}

	protected void addNode(Node node) {
		if (node != null) {
			Document document = node.getDocument();

			if ((document != null) && (document != this)) {
				// XXX: could clone here
				String message = "The Node already has an existing document: " + document;
				throw new IllegalAddException(this, node, message);
			}

			contentList().add(node);
			childAdded(node);
		}
	}

	protected void addNode(int index, Node node) {
		if (node != null) {
			Document document = node.getDocument();

			if ((document != null) && (document != this)) {
				// XXX: could clone here
				String message = "The Node already has an existing document: " + document;
				throw new IllegalAddException(this, node, message);
			}

			contentList().add(index, node);
			childAdded(node);
		}
	}

	protected boolean removeNode(Node node) {
		if (node == rootElement) {
			rootElement = null;
		}

		if (contentList().remove(node)) {
			childRemoved(node);
			return true;
		}

		return false;
	}

	protected void rootElementAdded(Element element) {
		this.rootElement = element;
		element.setDocument(this);
	}

	@Override
	protected DocumentFactory getDocumentFactory() {
		return documentFactory;
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
