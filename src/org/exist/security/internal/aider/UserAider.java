/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2010 The eXist Project
 *  http://exist-db.org
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *  
 *  $Id$
 */
package org.exist.security.internal.aider;

import java.util.Set;

import org.exist.security.Group;
import org.exist.security.Realm;
import org.exist.security.User;
import org.exist.xmldb.XmldbURI;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class UserAider implements User {

	String name;
	
	public UserAider(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see java.security.Principal#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#addGroup(java.lang.String)
	 */
	@Override
	public void addGroup(String name) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#addGroup(org.exist.security.Group)
	 */
	@Override
	public void addGroup(Group group) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#remGroup(java.lang.String)
	 */
	@Override
	public void remGroup(String group) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#setGroups(java.lang.String[])
	 */
	@Override
	public void setGroups(String[] groups) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getGroups()
	 */
	@Override
	public String[] getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#hasDbaRole()
	 */
	@Override
	public boolean hasDbaRole() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getUID()
	 */
	@Override
	public int getUID() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getPrimaryGroup()
	 */
	@Override
	public String getPrimaryGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#hasGroup(java.lang.String)
	 */
	@Override
	public boolean hasGroup(String group) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#setPassword(java.lang.String)
	 */
	@Override
	public void setPassword(String passwd) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#setHome(org.exist.xmldb.XmldbURI)
	 */
	@Override
	public void setHome(XmldbURI homeCollection) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getHome()
	 */
	@Override
	public XmldbURI getHome() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#authenticate(java.lang.Object)
	 */
	@Override
	public boolean authenticate(Object credentials) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#isAuthenticated()
	 */
	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getRealm()
	 */
	@Override
	public Realm getRealm() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#setUID(int)
	 */
	@Override
	public void setUID(int uid) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getPassword()
	 */
	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getDigestPassword()
	 */
	@Override
	public String getDigestPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object value) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.exist.security.User#getAttributeNames()
	 */
	@Override
	public Set<String> getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

}