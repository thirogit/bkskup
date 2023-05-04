package com.bk.bkskup3.jsonstore.jsonflat;

import com.bk.bkskup3.utils.TextSearchUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 1/20/2015
 * Time: 11:17 PM
 */
public class JsonPath {

    private static final String ARRAY_SUFFIX = "*";
    private static final String PATH_SEPARATOR = ".";

    static class PathElementImpl implements JsonPathElement {
        private String mName;
        private boolean mArray;

        PathElementImpl(String name, boolean array) {
            this.mName = name;
            this.mArray = array;
        }

        @Override
        public String getName() {
            return mName;
        }

        @Override
        public boolean isArray() {
            return mArray;
        }

        PathElementImpl duplicate() {
            return new PathElementImpl(getName(), isArray());
        }
    }


    private LinkedList<PathElementImpl> mPath = new LinkedList<PathElementImpl>();

    private JsonPath() {

    }

    public List<JsonPathElement> elements() {
        return Lists.<JsonPathElement>newLinkedList(mPath);
    }

    private JsonPath duplicate() {
        JsonPath path = new JsonPath();
        for (PathElementImpl pathElement : this.mPath) {
            path.mPath.add(pathElement.duplicate());
        }

        return path;
    }

    public JsonPath popFront()
    {
        JsonPath path = new JsonPath();
        Preconditions.checkState(mPath.size() > 0);
        Iterator<PathElementImpl> elementIt = this.mPath.iterator();
        elementIt.next();
        while (elementIt.hasNext()) {
            PathElementImpl pathElement = elementIt.next();
            path.mPath.add(pathElement.duplicate());
        }

        return path;
    }

    public JsonPathElement front()
    {
        return mPath.peekFirst();
    }

    public boolean isEmpty()
    {
        return mPath.isEmpty();
    }

    public static JsonPath parse(String path) {
        JsonPath result;
        Splitter splitter = Splitter.on(PATH_SEPARATOR);
        Iterable<String> elements = splitter.split(path);

        Iterator<String> elementIt = elements.iterator();
        String root = elementIt.next();
        if (ARRAY_SUFFIX.equals(root)) {
            result = rootArray();
        } else {
            result = root();
        }

        while (elementIt.hasNext()) {
            String element = elementIt.next();
            if (element.endsWith(ARRAY_SUFFIX)) {
                result.mPath.add(new PathElementImpl(TextSearchUtils.substringBefore(element, ARRAY_SUFFIX), true));
            } else {
                result.mPath.add(new PathElementImpl(element, false));
            }
        }

        return result;
    }

    public static JsonPath rootArray() {
        JsonPath path = new JsonPath();
        path.mPath.add(new PathElementImpl("", true));
        return path;
    }

    public static JsonPath root() {
        JsonPath path = new JsonPath();
        path.mPath.add(new PathElementImpl("", false));
        return path;
    }

    public JsonPath next(String elementName) {
        JsonPath duplicate = duplicate();
        duplicate.mPath.add(new PathElementImpl(elementName, false));
        return duplicate;
    }

    public JsonPath nextArray(String elementName) {
        JsonPath duplicate = duplicate();
        duplicate.mPath.add(new PathElementImpl(elementName, true));
        return duplicate;
    }

    public String toString() {
        String result = "";
        for (Iterator<PathElementImpl> iterator = this.mPath.iterator(); iterator.hasNext(); ) {
            PathElementImpl pathElement = iterator.next();

            result += pathElement.getName();
            if (pathElement.isArray())
                result += ARRAY_SUFFIX;
            if (iterator.hasNext())
                result += PATH_SEPARATOR;
        }

        return result;
    }

}
