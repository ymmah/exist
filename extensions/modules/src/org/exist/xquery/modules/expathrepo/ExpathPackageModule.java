/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2007-2010 The eXist Project
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
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * $Id: CompressionModule.java 11884 2010-07-01 10:32:31Z deliriumsky $
 */
package org.exist.xquery.modules.expathrepo;

import org.apache.log4j.Logger;
import org.exist.xquery.*;
import org.exist.xquery.XPathException;
import org.expath.pkg.repo.PackageException;
import org.expath.pkg.repo.Repository;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * XQuery Extension module for expath expathrepo
 *
 * @author James Fuller <jim.fuller@exist-db.org>
 * @author cutlass
 * @version 1.0
 */
public class ExpathPackageModule extends AbstractInternalModule {

    private final static Logger logger = Logger.getLogger(ExpathPackageModule.class);

    public final static String NAMESPACE_URI = "http://exist-db.org/xquery/repo";

    public final static String PREFIX = "repo";
    public final static String INCLUSION_DATE = "2010-07-27";
    public final static String RELEASED_IN_VERSION = "eXist-1.5";

    public static Repository _repo = null;

    private final static FunctionDef[] functions = {
        new FunctionDef(ListFunction.signature, ListFunction.class),
        new FunctionDef(InstallFunction.signature, InstallFunction.class),
        new FunctionDef(RemoveFunction.signature, RemoveFunction.class)
    };

    public ExpathPackageModule(Map<String, List<? extends Object>> parameters) throws XPathException {
        super(functions, parameters);
        _repo = getRepo();
    }

    @Override
    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }

    @Override
    public String getDefaultPrefix() {
        return PREFIX;
    }

    public String getDescription() {
        return "A module for working with expath repository manager";
    }

    public String getReleaseVersion() {
        return RELEASED_IN_VERSION;
    }


    private static synchronized Repository getRepo()
            throws XPathException
    {

        if ( _repo == null ) {
            try {
                String existHome = System.getProperty("exist.home");
                if (existHome != null){
                    new File( existHome + "/webapp/WEB-INF/expathrepo").mkdir();    
                    _repo = new Repository(new File( existHome + "/webapp/WEB-INF/expathrepo"));

                }else{
                    new File( System.getProperty("java.io.tmpdir") + "/expathrepo").mkdir();
                    _repo = new Repository(new File( System.getProperty("java.io.tmpdir") + "/expathrepo"));
                }

            }
            catch ( PackageException ex ) {
                throw new XPathException("Problem creating expath repository", ex);
            }
        }

        return _repo;
    }


    

}