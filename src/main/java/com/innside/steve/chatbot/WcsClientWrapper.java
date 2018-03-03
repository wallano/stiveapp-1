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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * 
 * Wrapper class of Watson Conversation Client
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class WcsClientWrapper {

    private static final String WCS_VERSION = ConversationService.VERSION_DATE_2017_02_03;

    private final Gson mGson = new GsonBuilder().create();
    private final ConversationService mWcsService;
    private final String mWorkspaceId;

    WcsClientWrapper(String userName, String password, String workspaceId) {
        mWorkspaceId = workspaceId;
        mWcsService = new ConversationService(WCS_VERSION);
        mWcsService.setUsernameAndPassword(userName, password);
    }

    /**
     * Send text to Watson Conversation
     * 
     * @param text
     * @param contextAsJSON
     * @return
     */
    @SuppressWarnings("unchecked")
    MessageResponse sendMessage(String text, String contextAsJSON) {
        final Map<String, Object> context = fromJSON(contextAsJSON, Map.class);
        return sendMessage(text, context);
    }

    MessageResponse sendMessage(String text, Map<String, Object> context) {

        final MessageRequest newMessage;

        if (context != null) {
            newMessage = new MessageRequest.Builder()
                    .inputText(text)
                    .context(context)
                    .build();
        } else {
            // When calling for the first time,the context is null.
            newMessage = new MessageRequest.Builder()
                    .inputText(text)
                    .build();
        }

        final MessageResponse response = mWcsService.message(this.mWorkspaceId, newMessage).execute();

        return response;
    }

    public String getJSONFromResponse(MessageResponse response) {
        return response.toString();
    }

    public String getContextJSONFromResponse(MessageResponse response) {
        final Map<String, Object> context = response.getContext();
        final String json = toJSON(context);
        return json;
    }

    private <T> T fromJSON(String json, Class<T> clazz) {
        return mGson.fromJson(json, clazz);
    }

    private <T> String toJSON(T clazz) {
        return mGson.toJson(clazz);
    }

}
