/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.Closer;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utility class to load JSON values from various sources as {@link JsonNode}s.
 *
 * <p>This class uses a {@link JsonNodeReader} to parse JSON inputs.</p>
 *
 * @see JsonNodeReader
 */
public final class JsonLoader
{
    private static final Pattern INITIAL_SLASH = Pattern.compile("^/");
    /**
     * The reader
     */
    private static final JsonNodeReader READER = new JsonNodeReader();

    private JsonLoader()
    {
    }

    /**
     * Read a {@link JsonNode} from a resource path.
     *
     * <p>This method explicitly throws an {@link IOException} if the resource
     * does not exist, instead of letting a {@link NullPointerException} slip
     * through.</p>
     *
     * @param resource The path to the resource
     * @return the JSON document at the resource
     * @throws IOException if the resource does not exist or there was a
     * problem loading it, or if the JSON document is invalid
     */
    public static JsonNode fromResource(final String resource)
        throws IOException
    {
        final String realResource
            = INITIAL_SLASH.matcher(resource).replaceFirst("");
        final URL url = Resources.getResource(realResource);

        if (url == null)
            throw new IOException("resource " + resource + " not found");

        final Closer closer = Closer.create();
        final JsonNode ret;
        final InputStream in;

        try {
            in = closer.register(url.openStream());
            ret = READER.fromInputStream(in);
        } finally {
            closer.close();
        }

        return ret;
    }

    /**
     * Read a {@link JsonNode} from an URL.
     *
     * @param url The URL to fetch the JSON document from
     * @return The document at that URL
     * @throws IOException in case of network problems etc.
     */
    public static JsonNode fromURL(final URL url)
        throws IOException
    {
        return READER.fromInputStream(url.openStream());
    }

    /**
     * Read a {@link JsonNode} from a file on the local filesystem.
     *
     * @param path the path (relative or absolute) to the file
     * @return the document in the file
     * @throws IOException if this is not a file, if it cannot be read, etc.
     */
    public static JsonNode fromPath(final String path)
        throws IOException
    {
        final Closer closer = Closer.create();
        final JsonNode ret;
        final FileInputStream in;

        try {
            in = closer.register(new FileInputStream(path));
            ret = READER.fromInputStream(in);
        } finally {
            closer.close();
        }

        return ret;
    }

    /**
     * Same as {@link #fromPath(String)}, but this time the user supplies the
     * {@link File} object instead
     *
     * @param file the File object
     * @return The document
     * @throws IOException in many cases!
     */
    public static JsonNode fromFile(final File file)
        throws IOException
    {
        final Closer closer = Closer.create();
        final JsonNode ret;
        final FileInputStream in;

        try {
            in = closer.register(new FileInputStream(file));
            ret = READER.fromInputStream(in);
        } finally {
            closer.close();
        }

        return ret;
    }

    /**
     * Read a {@link JsonNode} from a user supplied {@link Reader}
     *
     * @param reader The reader
     * @return the document
     * @throws IOException if the reader has problems
     */
    public static JsonNode fromReader(final Reader reader)
        throws IOException
    {
        return READER.fromReader(reader);
    }

    /**
     * Read a {@link JsonNode} from a string input
     *
     * @param json the JSON as a string
     * @return the document
     * @throws IOException could not read from string
     */
    public static JsonNode fromString(final String json)
        throws IOException
    {
        return fromReader(new StringReader(json));
    }
}
