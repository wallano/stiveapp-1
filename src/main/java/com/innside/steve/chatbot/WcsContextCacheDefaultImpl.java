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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache user's context on memory
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class WcsContextCacheDefaultImpl implements WcsContextCache {

    private final Map<String, WcsUserContext> mUserContexts = new ConcurrentHashMap<String, WcsUserContext>();

    @Override
    public WcsUserContext get(String wcsClientId) {

        return mUserContexts.get(wcsClientId);
    }

    @Override
    public void put(String wcsClientId, WcsUserContext context) {
        mUserContexts.put(wcsClientId, context);

    }

    @Override
    public boolean hasId(String wcsClientId) {
        return mUserContexts.containsKey(wcsClientId);
    }

    @Override
    public void remove(String wcsClientId) {
        mUserContexts.remove(wcsClientId);

    }

}
