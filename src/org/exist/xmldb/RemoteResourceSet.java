/*
 * eXist Open Source Native XML Database
 * Copyright (C) 2001-2015 The eXist Project
 * http://exist-db.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.exist.xmldb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.xml.transform.OutputKeys;

import com.evolvedbinary.j8fu.function.FunctionE;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.exist.storage.serializers.EXistOutputKeys;
import org.exist.util.VirtualTempFile;
import org.xmldb.api.base.ErrorCodes;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.base.XMLDBException;

public class RemoteResourceSet implements ResourceSet {

    private final RemoteCollection collection;
    private int handle = -1;
    private int hash = -1;
    private final List resources;
    private final Properties outputProperties;

    private static Logger LOG = LogManager.getLogger(RemoteResourceSet.class.getName());

    public RemoteResourceSet(final RemoteCollection col, final Properties properties, final Object[] resources, final int handle, final int hash) {
        this.handle = handle;
        this.hash = hash;
        this.resources = new ArrayList(Arrays.asList(resources));
        this.collection = col;
        this.outputProperties = properties;
    }

    @Override
    public void addResource(final Resource resource) {
        resources.add(resource);
    }

    @Override
    public void clear() throws XMLDBException {
        if (handle < 0) {
            return;
        }
        final List<Object> params = new ArrayList<>();
        params.add(handle);
        if (hash > -1)
            params.add(hash);
        try {
            collection.getClient().execute("releaseQueryResult", params);
        } catch (final XmlRpcException e) {
            LOG.error("Failed to release query result on server: " + e.getMessage(), e);
        }
        hash = -1;
        resources.clear();
        handle = -1;
    }

    @Override
    public ResourceIterator getIterator() throws XMLDBException {
        return new NewResourceIterator();
    }

    public ResourceIterator getIterator(final long start) throws XMLDBException {
        return new NewResourceIterator(start);
    }

    @Override
    public Resource getMembersAsResource() throws XMLDBException {
        final List<Object> params = new ArrayList<>();
        params.add(Integer.valueOf(handle));
        params.add(outputProperties);

        try {
            VirtualTempFile vtmpfile = null;
            try {
                vtmpfile = new VirtualTempFile();

                Map<?, ?> table = (Map<?, ?>) collection.getClient().execute("retrieveAllFirstChunk", params);

                long offset = ((Integer) table.get("offset")).intValue();
                byte[] data = (byte[]) table.get("data");
                final boolean isCompressed = "yes".equals(outputProperties.getProperty(EXistOutputKeys.COMPRESS_OUTPUT, "no"));
                // One for the local cached file
                Inflater dec = null;
                byte[] decResult = null;
                int decLength = 0;
                if (isCompressed) {
                    dec = new Inflater();
                    decResult = new byte[65536];
                    dec.setInput(data);
                    do {
                        decLength = dec.inflate(decResult);
                        vtmpfile.write(decResult, 0, decLength);
                    } while (decLength == decResult.length || !dec.needsInput());
                } else {
                    vtmpfile.write(data);
                }
                while (offset > 0) {
                    params.clear();
                    params.add(table.get("handle"));
                    params.add(Long.toString(offset));
                    table = (Map<?, ?>) collection.getClient().execute("getNextExtendedChunk", params);
                    offset = Long.parseLong((String) table.get("offset"));
                    data = (byte[]) table.get("data");
                    // One for the local cached file
                    if (isCompressed) {
                        dec.setInput(data);
                        do {
                            decLength = dec.inflate(decResult);
                            vtmpfile.write(decResult, 0, decLength);
                        } while (decLength == decResult.length || !dec.needsInput());
                    } else {
                        vtmpfile.write(data);
                    }
                }
                if (dec != null) {
                    dec.end();
                }

                final RemoteXMLResource res = new RemoteXMLResource(collection, handle, 0, XmldbURI.EMPTY_URI, Optional.empty());
                res.setContent(vtmpfile);
                res.setProperties(outputProperties);
                return res;
            } catch (final XmlRpcException xre) {
                final byte[] data = (byte[]) collection.getClient().execute("retrieveAll", params);
                String content;
                try {
                    content = new String(data, outputProperties.getProperty(OutputKeys.ENCODING, "UTF-8"));
                } catch (final UnsupportedEncodingException ue) {
                    LOG.warn(ue);
                    content = new String(data);
                }
                final RemoteXMLResource res = new RemoteXMLResource(collection, handle, 0,
                        XmldbURI.EMPTY_URI, Optional.empty());
                res.setContent(content);
                res.setProperties(outputProperties);
                return res;
            } catch (final IOException | DataFormatException ioe) {
                throw new XMLDBException(ErrorCodes.VENDOR_ERROR, ioe.getMessage(), ioe);
            } finally {
                if (vtmpfile != null) {
                    try {
                        vtmpfile.close();
                    } catch (final IOException ioe) {
                        //IgnoreIT(R)
                    }
                }
            }
        } catch (final XmlRpcException xre) {
            throw new XMLDBException(ErrorCodes.INVALID_RESOURCE, xre.getMessage(), xre);
        }
    }

    @Override
    public Resource getResource(final long pos) throws XMLDBException {
        if (pos >= resources.size()) {
            return null;
        }

        if(resources.get((int) pos) instanceof Resource) {
            return (Resource) resources.get((int) pos);
        } else {
            final Map<String, String> item = (Map<String, String>)resources.get((int)pos);

            switch(item.get("type")) {
                case "node()":
                case "document-node()":
                case "element()":
                case "attribute()":
                case "text()":
                case "processing-instruction()":
                case "comment()":
                case "namespace()":
                case "cdata-section()":
                    return getResourceNode((int)pos, item);

                case "xs:base64Binary":
                    return getResourceBinaryValue((int)pos, item, Base64::decodeBase64);

                case "xs:hexBinary":
                    return getResourceBinaryValue((int)pos, item, Hex::decodeHex);

                default:    // atomic value
                    return getResourceValue((int)pos, item);

            }
        }
    }

    private RemoteXMLResource getResourceNode(final int pos, final Map<String, String> nodeDetail) throws XMLDBException {
        final String doc = nodeDetail.get("docUri");
        final Optional<String> s_id =  Optional.ofNullable(nodeDetail.get("nodeId"));
        final XmldbURI docUri;
        try {
            docUri = XmldbURI.xmldbUriFor(doc);
        } catch (final URISyntaxException e) {
            throw new XMLDBException(ErrorCodes.INVALID_URI, e.getMessage(), e);
        }

        final RemoteCollection parent;
        if (docUri.startsWith(XmldbURI.DB)) {
            parent = RemoteCollection.instance(collection.getClient(), docUri.removeLastSegment());
        } else {
            //fake to provide a RemoteCollection for local files that have been transferred by xml-rpc
            parent = collection;
        }


        parent.setProperties(outputProperties);
        final RemoteXMLResource res = new RemoteXMLResource(parent, handle, pos, docUri, s_id);
        res.setProperties(outputProperties);
        return res;
    }

    private RemoteXMLResource getResourceValue(final int pos, final Map<String, String> valueDetail) throws XMLDBException {
        final RemoteXMLResource res = new RemoteXMLResource(collection, handle, pos, XmldbURI.create(Long.toString(pos)), Optional.empty());
        res.setContent(valueDetail.get("value"));
        res.setProperties(outputProperties);
        return res;
    }

    private <E extends Exception> RemoteBinaryResource getResourceBinaryValue(final int pos, final Map<String, String> valueDetail, final FunctionE<String, byte[], E> binaryDecoder) throws XMLDBException {
        final String type = valueDetail.get("type");

        final byte[] content;
        try {
            content = binaryDecoder.apply(valueDetail.get("value"));
        } catch(final Exception e) {
            throw new XMLDBException(ErrorCodes.UNKNOWN_ERROR, e);
        }

        final RemoteBinaryResource res = new RemoteBinaryResource(collection, XmldbURI.create(Integer.toString(pos)), type, content);
        res.setProperties(outputProperties);
        return res;
    }

    @Override
    public long getSize() throws XMLDBException {
        return resources == null ? 0 : (long) resources.size();
    }

    @Override
    public void removeResource(final long pos) throws XMLDBException {
        resources.remove(pos);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            clear();
        } finally {
            super.finalize();
        }
    }

    class NewResourceIterator implements ResourceIterator {
        long pos = 0;

        public NewResourceIterator() {
        }

        public NewResourceIterator(final long start) {
            pos = start;
        }

        @Override
        public boolean hasMoreResources() throws XMLDBException {
            return resources == null ? false : pos < resources.size();
        }

        @Override
        public Resource nextResource() throws XMLDBException {
            return getResource(pos++);
        }
    }
}

