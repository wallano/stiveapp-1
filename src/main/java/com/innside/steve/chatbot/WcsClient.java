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

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * Client for Watson Conversation
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class WcsClient
{

	private final WcsClientWrapper mWcsWrapper;
	private final WcsClientUserContextManager mCtxMgr;

	// Whether or not to display the welcome node's response in chat
	private boolean mWelcomeNodeResponseEnabled = false;
	private String mResponseTextSeparator = "\n";

	/**
	 * 
	 * @param userName
	 *            userName of Watson conversation workspace
	 * @param password
	 *            password of Watson conversation workspace
	 * @param workspaceId
	 */
	public WcsClient(String userName, String password, String workspaceId) {
		mWcsWrapper = new WcsClientWrapper(userName, password, workspaceId);
		mCtxMgr = new WcsClientUserContextManager();
		setLibLoggingEnabled(false);
	}

	public WcsClient(String userName, String password, String workspaceId, WcsContextCache contextCacheImpl) {
		mWcsWrapper = new WcsClientWrapper(userName, password, workspaceId);
		mCtxMgr = new WcsClientUserContextManager(contextCacheImpl);
		setLibLoggingEnabled(false);
	}

	public synchronized boolean isConversationStarted(String wcsClientId) {
		return mCtxMgr.hasUser(wcsClientId);
	}

	/**
	 * Start conversation
	 * 
	 * @param wcsClientId
	 * @return
	 */
	public synchronized MessageResponse startConversation(String wcsClientId) {
		// At the initial invocation, a context cache for each user is created
		// in the #sendText method
		return sendMessage(wcsClientId, "");
	}

	/**
	 * Clear user's conversation with Watson. Clear specified user's
	 * conversation history (= forget context).
	 * 
	 * @param user
	 */
	public synchronized void clearConversation(String wcsClientId) {
		mCtxMgr.clearConversation(wcsClientId);
	}

	/**
	 * Send text to watson
	 * 
	 * @param wcsClientId
	 * @param text
	 * @return Raw messageResponse from Watson including <br>
	 *         Map<String, Object> context<br>
	 *         List<Entity> entities<br>
	 *         List<Intent> intents<br>
	 *         Map<String, Object> output<br>
	 *         Map<String, Object> input<br>
	 */
	public synchronized MessageResponse sendMessage(String wcsClientId, String text) {

		if (mCtxMgr.hasUser(wcsClientId)) {

			// get current user context
			final Map<String, Object> context = getUserContext(wcsClientId).getMap();

			// send text with current user context
			final MessageResponse watsonMessageResponse = mWcsWrapper.sendMessage(text, context);

			// Update current user context when response returns.
			mCtxMgr.updateUserContext(wcsClientId, watsonMessageResponse);

			return watsonMessageResponse;

		} else {
			// - if this call is the first

			// At the initial invocation, a context cache for each user will be
			// created in the #sendText method

			final MessageResponse firstResponse = mWcsWrapper.sendMessage("", (Map<String, Object>) null);

			final Map<String, Object> context = firstResponse.getContext();

			final WcsUserContext userContext = new WcsUserContext(context);

			// set first context to user context
			mCtxMgr.populateToUserContext(wcsClientId, userContext);

			return firstResponse;
		}
	}

	/**
	 * Set whether to include the response text returned by the welcome node in
	 * the result
	 * 
	 * @see #sendMessageForText
	 * @param enabled
	 */
	public void setWelcomeNodeResponseEnabled(boolean enabled) {
		mWelcomeNodeResponseEnabled = enabled;
	}

	/**
	 * Separator with multiple node outputs
	 * 
	 * @see #sendMessageForText
	 * @param responseTextNewLine
	 */
	public void setResponseTextSeparator(String responseTextNewLine) {
		mResponseTextSeparator = responseTextNewLine;
	}

	/**
	 * 
	 * Send text to Watson and receive it as text <br>
	 * <p>
	 * High level method to get Watson's result as text. Whether or not to
	 * include the response of the Welcome node (the first node) at the initial
	 * access is specified by {@link #setWelcomeNodeResponseEnabled}. <br>
	 * The separator of OutputText when passing through multiple nodes is
	 * specified by {@link #setResponseTextSeparator}<br>
	 * If you want to do more raw-level operations, use {@link #sendMessage}
	 * instead of this method<br>
	 * 
	 * @param wcsClientId
	 * 
	 * @param userInputText
	 * @see #setWelcomeNodeResponseEnabled(boolean)
	 * @see #setResponseTextSeparator(String)
	 * @see #sendMessage(String, String)
	 * @return
	 */
	public String sendMessageForText(String wcsClientId, String userInputText) {

		final StringBuilder watsonOutputText = new StringBuilder();

		if (isConversationStarted(wcsClientId) == false) {
			// - if Watson and this slack user's dialogue has not started yet

			// Start a dialogue with Watson with this slack user (first call)
			// welcome node is executed for the first time
			MessageResponse wcsWelcomeRes = startConversation(wcsClientId);

			if (mWelcomeNodeResponseEnabled) {
				// If want to display the welcome node response in chat
				watsonOutputText.append(join(mResponseTextSeparator, wcsWelcomeRes.getText())).append(mResponseTextSeparator);
			}
		}

		// slack Send the user's input text to Watson and receive the response
		MessageResponse wcsRes = sendMessage(wcsClientId, userInputText);

		// Since the text is of type List <String>, join
		watsonOutputText.append(join(mResponseTextSeparator, wcsRes.getText()));

		return watsonOutputText.toString();
	}

	private WcsUserContext getUserContext(String wcsClientId) {
		return mCtxMgr.getUserContext(wcsClientId);
	}

	/**
	 * Put data into context
	 * 
	 * @param user
	 * @param key
	 * @param value
	 */
	public void put(String wcsClientId, String key, Object value) {
		final WcsUserContext context = getUserContext(wcsClientId);
		context.put(key, value);
	}

	/**
	 * Get String data from context
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	public String getAsString(String wcsClientId, String key) {
		final WcsUserContext context = getUserContext(wcsClientId);
		return context.getAsString(key);
	}

	/**
	 * Get Boolean data from context
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	public Boolean getAsBoolean(String wcsClientId, String key) {
		final WcsUserContext context = getUserContext(wcsClientId);
		return context.getAsBoolean(key);
	}

	/**
	 * Get Integer data from context
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	public Integer getAsInteger(String wcsClientId, String key) {
		final WcsUserContext context = getUserContext(wcsClientId);
		return context.getAsInteger(key);
	}

	/**
	 * Get Double data from context
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	public Double getAsDouble(String wcsClientId, String key) {
		final WcsUserContext context = getUserContext(wcsClientId);
		return context.getAsDouble(key);
	}

	/**
	 * Get complex data(nested JSON) from context
	 * 
	 * @param user
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAsMap(String wcsClientId, String key) {
		final WcsUserContext context = getUserContext(wcsClientId);
		try {
			return (Map<String, Object>) context.get(key);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * Enable base library logging
	 * 
	 * @param enabled
	 */
	public void setLibLoggingEnabled(boolean enabled) {
		final Logger libLogger = Logger.getLogger(OkHttpClient.class.getName());
		libLogger.setUseParentHandlers(enabled);
	}

	private String join(String separator, List<String> list) {
		final StringBuilder sb = new StringBuilder();
		for (String str : list) {
			if (str.length() > 0) {
				sb.append(str);
				sb.append(separator);
			}
		}
		return sb.toString();

	}

	public WcsClientWrapper getDialogManager() {
		return mWcsWrapper;
	}

}
