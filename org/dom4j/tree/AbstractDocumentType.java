/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */
package org.dom4j.tree;

import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.NodeType;
import org.dom4j.Visitor;
import org.dom4j.dtd.InternalDeclaration;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * <code>AbstractDocumentType</code> is an abstract base class for tree
 * implementors to use for implementation inheritence.
 * </p>
 *
 * @author <a href="mailto:james.strachan@metastuff.com">James Strachan </a>
 * @version $Revision: 1.17 $
 */
public abstract class AbstractDocumentType extends AbstractNode implements
		DocumentType {

	public AbstractDocumentType() {
	}

	@Override
	public NodeType getNodeTypeEnum() {
		return NodeType.DOCUMENT_TYPE_NODE;
	}

	@Override
	public String getName() {
		return getElementName();
	}

	@Override
	public void setName(String name) {
		setElementName(name);
	}

	public String getPath(Element context) {
		// not available in XPath
		return "";
	}

	public String getUniquePath(Element context) {
		// not available in XPath
		return "";
	}

	/**
	 * Returns the text format of the declarations if applicable, or the empty
	 * String
	 *
	 * @return DOCUMENT ME!
	 */
	@Override
	public String getText() {
		List<InternalDeclaration> list = getInternalDeclarations();

		if ((list != null) && (list.size() > 0)) {
			StringBuilder builder = new StringBuilder();
			for (InternalDeclaration decl : list) {
				builder.append(decl.toString());
				builder.append('\n');
			}
			if (builder.length() > 0) {
				builder.setLength(builder.length() - 1);
			}
			return builder.toString();
		}

		return "";
	}

	@Override
	protected void toString(StringBuilder builder) {
		super.toString(builder);
		builder.append(" [DocumentType: ");
		this.asXML(builder);
		builder.append(']');
	}

	public String asXML() {
		StringBuilder builder = new StringBuilder();
		this.asXML(builder);
		return builder.toString();
	}

	protected void asXML(StringBuilder builder) {
		builder.append("<!DOCTYPE ");
		builder.append(getElementName());

		boolean hasPublicID = false;
		String publicID = getPublicID();

		if ((publicID != null) && (publicID.length() > 0)) {
			builder.append(" PUBLIC ");
			builder.append('"');
			builder.append(publicID);
			builder.append('"');
			hasPublicID = true;
		}

		String systemID = getSystemID();

		if ((systemID != null) && (systemID.length() > 0)) {
			if (!hasPublicID) {
				builder.append(" SYSTEM");
			}

			builder.append(" \"");
			builder.append(systemID);
			builder.append('"');
		}

		builder.append('>');
	}

	@Override
	public void write(Writer writer) throws IOException {
		writer.write("<!DOCTYPE ");
		writer.write(getElementName());

		boolean hasPublicID = false;
		String publicID = getPublicID();

		if ((publicID != null) && (publicID.length() > 0)) {
			writer.write(" PUBLIC \"");
			writer.write(publicID);
			writer.write('\"');
			hasPublicID = true;
		}

		String systemID = getSystemID();

		if ((systemID != null) && (systemID.length() > 0)) {
			if (!hasPublicID) {
				writer.write(" SYSTEM");
			}

			writer.write(" \"");
			writer.write(systemID);
			writer.write('\"');
		}

		List<InternalDeclaration> list = getInternalDeclarations();

		if ((list != null) && (list.size() > 0)) {
			writer.write(" [");

			for (Iterator<InternalDeclaration> iter = list.iterator(); iter.hasNext();) {
				InternalDeclaration decl = iter.next();
				writer.write("\n  ");
				writer.write(decl.toString());
			}

			writer.write("\n]");
		}

		writer.write('>');
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
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
