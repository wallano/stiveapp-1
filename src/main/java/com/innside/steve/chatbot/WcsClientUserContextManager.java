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

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * Class managing context for each user
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class WcsClientUserContextManager {

    private final WcsContextCache mContextCacheImpl;

    WcsClientUserContextManager() {
        mContextCacheImpl = new WcsContextCacheDefaultImpl();
    }

    WcsClientUserContextManager(WcsContextCache contextCacheImpl) {
        mContextCacheImpl = contextCacheImpl;
    }

    boolean hasUser(String wcsClientId) {

        return mContextCacheImpl.hasId(wcsClientId);
    }

    void clearConversation(String wcsClientId) {
        if (hasUser(wcsClientId)) {

            mContextCacheImpl.remove(wcsClientId);
        }
    }

    WcsClientUserContextManager updateUserContext(String user, MessageResponse response) {
        populateToUserContext(user, new WcsUserContext(response.getContext()));
        return WcsClientUserContextManager.this;
    }

    WcsClientUserContextManager populateToUserContext(String wcsClientId, WcsUserContext ctx) {

        if (mContextCacheImpl.hasId(wcsClientId)) {
            // - user context already cached

            final WcsUserContext crrCtx = mContextCacheImpl.get(wcsClientId);

            // populate context to current cached context
            crrCtx.getMap().putAll(ctx.getMap());
            crrCtx.getLocalAttributes().putAll(ctx.getLocalAttributes());

        } else {
            // - still not exists
            mContextCacheImpl.put(wcsClientId, ctx);
        }

        return WcsClientUserContextManager.this;
    }

    WcsUserContext getUserContext(String wcsClientId) {

        if (hasUser(wcsClientId)) {

            return mContextCacheImpl.get(wcsClientId);

        } else {
            return null;
        }
    }

}
