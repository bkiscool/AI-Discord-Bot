package me.brandonkey.bot;

import java.time.Duration;
import java.util.Arrays;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

public class AI {
	
	private static OpenAiService service;

	public AI(final String token)
	{
		service = new OpenAiService(token, Duration.ofSeconds(30));
		
		System.out.println("OpenAI API ready.");
	}
	
	/**
	 * Have the AI generate a response from a given prompt
	 * 
	 * @param prompt The thing to ask the AI
	 * @return The AI's response
	 */
	public static String prompt(final String prompt, String username)
	{
		System.out.println("[" + username + "] " + prompt);
		
		final ChatCompletionRequest request = ChatCompletionRequest.builder()
													.model("gpt-3.5-turbo")
													.messages(Arrays.asList(new ChatMessage("user", prompt, username)))
													.n(1)
													.build();
		
		final ChatMessage reply = service.createChatCompletion(request).getChoices().get(0).getMessage();
		final String response = reply.getContent();
		
		System.out.println("[-] " + response);
		
		return response;
		
	}
	
	public static void shutdown()
	{
		System.out.println("Shutting down the API.");
		
		service.shutdownExecutor();
		
	}

}
