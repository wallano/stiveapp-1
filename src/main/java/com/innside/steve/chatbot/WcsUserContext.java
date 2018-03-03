/*  
 *  Copyright (c) 2006-2017 Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package com.innside.steve.chatbot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User Context<br>
 * <p>
 * If you want to persist data, save both context from getMap() and attributes
 * from getLocalAttributes().
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class WcsUserContext implements Serializable {

    private static final long serialVersionUID = 860289915194669609L;

    private final Map<String, Object> mContext;

    private final Map<String, Object> mAttributes;

    public WcsUserContext(Map<String, Object> context) {
        mContext = context;
        mAttributes = new HashMap<String, Object>();
    }

    public WcsUserContext(Map<String, Object> context, Map<String, Object> attributes) {
        mContext = context;
        mAttributes = new HashMap<String, Object>();
    }

    public WcsUserContext put(String key, Object value) {
        mContext.put(key, value);
        return WcsUserContext.this;
    }

    public String getAsString(String key) {
        return (String) mContext.get(key);
    }

    public Boolean getAsBoolean(String key) {
        try {
            final Boolean booleanObj = (Boolean) mContext.get(key);
            return booleanObj;
        } catch (Throwable e) {
            return null;
        }
    }

    public Integer getAsInteger(String key) {
        try {
            final Object o = mContext.get(key);
            if (o == null) {
                return null;
            }
            else if (o instanceof Double) {
                Integer intValue = (int) ((double) o);
                return intValue;
            }
            else if (o instanceof Integer) {
                final Integer integerObj = (Integer) o;
                return integerObj;
            } else {
                return null;
            }

        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getAsDouble(String key) {
        try {
            final Object o = mContext.get(key);
            if (o == null) {
                return null;
            }
            else if (o instanceof Double) {
                Double doubleValue = (Double) o;
                return doubleValue;
            }
            else if (o instanceof Integer) {
                final Double doubleObj = (double) ((int) o);
                return doubleObj;
            } else {
                return null;
            }
        } catch (Throwable e) {
            return null;
        }
    }

    public Object get(String key) {
        return mContext.get(key);
    }

    public WcsUserContext remove(String key) {
        mContext.remove(key);
        return WcsUserContext.this;
    }

    public Map<String, Object> getMap() {
        return mContext;
    }

    public Map<String, Object> getLocalAttributes() {
        return mAttributes;
    }
}
