/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 *  $Id$
 */
package org.exist.xpath.value;

import org.exist.xpath.XPathException;

public abstract class AbstractSequence implements Sequence {

	public abstract int getItemType();

	public abstract SequenceIterator iterate();

	public abstract int getLength();
	
	public AtomicValue convertTo(int requiredType) throws XPathException {
		if(getLength() == 0)
			return AtomicValue.EMPTY_VALUE.convertTo(requiredType);
		Item first = iterate().nextItem();
		if(Type.subTypeOf(first.getType(), Type.ATOMIC))
			return ((AtomicValue)first).convertTo(requiredType);
		else
			return new StringValue(first.getStringValue()).convertTo(requiredType);
	}
	
	public String getStringValue() {
		if(getLength() == 0)
			return "";
		Item first = iterate().nextItem();
		return first.getStringValue();
	}
}
