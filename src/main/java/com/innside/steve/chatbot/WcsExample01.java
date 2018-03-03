package com.innside.steve.chatbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.innside.steve.chatbot.WcsClient;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

public class WcsExample01 {

    // Edit here
    private static final String WATSON_CONVERSATION_USERNAME = "7325a607-cc55-4c91-b722-a4bfc36cffdc";
    private static final String WATSON_CONVERSATION_PASSWORD = "uOCtcqSmBzak";
    private static final String WATCON_CONVERSATION_WORKSPACE_ID = "e72b084a-17d0-4f58-9da0-76b5a86722b5";

    public static void main(String[] args)
    {

        String wcsClientId = "dummy_user_id";
        BufferedReader br = null;

        // Create client for Watson Conversation Username, password,
        // workspaceId can be confirmed on the workspace screen of Watson
        // Conversation's workspace
        WcsClient watson = new WcsClient(
                WATSON_CONVERSATION_USERNAME,
                WATSON_CONVERSATION_PASSWORD,
                WATCON_CONVERSATION_WORKSPACE_ID);

        // Perform initial access (call welcome node)
        // Call #startConversation for the first access to workspace
        MessageResponse wcsWelcomeRes = watson.startConversation(wcsClientId);
        br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("FROM WATSON:" + wcsWelcomeRes.getTextConcatenated(""));
        while(true){
        	
        	try {
				String input = br.readLine();
				MessageResponse wcsRes01 = watson.sendMessage(wcsClientId, input);
				System.out.println("FROM WATSON:" + wcsRes01.getTextConcatenated(""));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

 
    }
}
