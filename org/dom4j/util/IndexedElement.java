/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.dom4j.util;

import org.dom4j.*;
import org.dom4j.tree.BackedList;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.LazyList;

import java.util.List;

/**
 * <p>
 * <code>IndexedElement</code> is an implementation of {@link Element}which
 * maintains an index of the attributes and elements it contains to optimise
 * lookups via name.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.10 $
 */
public class IndexedElement extends DefaultElement {

	/**
	 * Lazily constructed index for elements
	 */
	private DoubleNameMap<List<Element>> elementIndex;
	/**
	 * Lazily constructed index for attributes
	 */
	private DoubleNameMap<Attribute> attributeIndex;

	public IndexedElement(String name) {
		super(name);
	}

	public IndexedElement(QName qname) {
		super(qname);
	}

	public IndexedElement(QName qname, int attributeCount) {
		super(qname, attributeCount);
	}

	@Override
	public Attribute attribute(String name) {
		return attributeIndex().get(name);
	}

	@Override
	public Attribute attribute(QName qName) {
		return attributeIndex().get(qName);
	}

	@Override
	public Element element(String name) {
		return firstElement(elementIndex().get(name));
	}

	@Override
	public Element element(QName qName) {
		return firstElement(elementIndex().get(qName));
	}

	@Override
	public List<Element> elements(String name) {
		return asElementList(elementIndex().get(name));
	}

	@Override
	public List<Element> elements(QName qName) {
		return asElementList(elementIndex().get(qName));
	}

	protected static Element firstElement(List<Element> list) {
		if (list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	protected List<Element> asElementList(List<Element> list) {
		BackedList<Element> answer = createResultList();
		for (Element element : list) {
			answer.addLocal(element);
		}

		return answer;
	}

	// #### could we override the add(Element) remove(Element methods?

	@Override
	protected void addNode(Node node) {
		super.addNode(node);

		switch (node.getNodeTypeEnum()) {
			case ELEMENT_NODE:
				if (elementIndex != null) {
					addToElementIndex((Element) node);
				}
				break;
			case ATTRIBUTE_NODE:
				if (attributeIndex != null) {
					addToAttributeIndex((Attribute) node);
				}
				break;
		}
	}

	@Override
	protected boolean removeNode(Node node) {
		if (super.removeNode(node)) {
			switch (node.getNodeTypeEnum()) {
				case ELEMENT_NODE:
					if (elementIndex != null) {
						removeFromElementIndex((Element) node);
					}
					break;
				case ATTRIBUTE_NODE:
					if (attributeIndex != null) {
						removeFromAttributeIndex((Attribute) node);
					}
					break;
			}
			return true;
		}

		return false;
	}

	protected DoubleNameMap<Attribute> attributeIndex() {
		if (attributeIndex == null) {
			attributeIndex = new DoubleNameMap<Attribute>();
			for (Attribute attribute : attributeList()) {
				addToAttributeIndex(attribute);
			}

		}

		return attributeIndex;
	}

	protected DoubleNameMap<List<Element>> elementIndex() {
		if (elementIndex == null) {
			elementIndex = new DoubleNameMap<List<Element>>();
			for (Node node : contentList()) {
				Element element = NodeHelper.nodeAsElement(node);
				if (element != null) {
					addToElementIndex(element);
				}

			}
		}

		return elementIndex;
	}

	protected void addToElementIndex(Element element) {
		QName qName = element.getQName();
		List<Element> list = elementIndex.get(qName);
		if (list == null) {
			list = new LazyList<Element>();
			elementIndex.put(qName, list);
		}

		list.add(element);
	}

	protected void removeFromElementIndex(Element element) {
		QName qName = element.getQName();
		List<Element> list = elementIndex.get(qName);
		if (list != null) {
			list.remove(element);
			if (list.isEmpty()) {
				elementIndex.remove(qName);
			}

		}
	}

	protected void addToAttributeIndex(Attribute attribute) {
		QName qName = attribute.getQName();
		attributeIndex.put(qName, attribute);
	}

	protected void removeFromAttributeIndex(Attribute attribute) {
		QName qName = attribute.getQName();
		attributeIndex.remove(qName);
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
