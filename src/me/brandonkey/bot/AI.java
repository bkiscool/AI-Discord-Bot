package me.brandonkey.bot;

import java.time.Duration;
import java.util.Arrays;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;

public class AI {
	
	private static OpenAiService service;

	public AI(final String token)
	{
		service = new OpenAiService(token, Duration.ofSeconds(30));
		
		Logger.LOG.print("OpenAI API is ready.");
		
	}
	
	/**
	 * Have the AI generate a response from a given prompt
	 * 
	 * @param prompt The thing to ask the AI
	 * @param username The name of the user, can be null
	 * @return The AI's response
	 */
	public static String askAi(final String prompt, final String username)
	{
		Logger.LOG.print(username + " prompted the AI with \"" + prompt + "\".");
		
		final ChatCompletionRequest request = ChatCompletionRequest.builder()
													.model("gpt-3.5-turbo")
													.messages(Arrays.asList(new ChatMessage("user", prompt, username)))
													.n(1)
													.build();
		
		final ChatMessage reply = service.createChatCompletion(request).getChoices().get(0).getMessage();
		final String response = reply.getContent();
		
		Logger.LOG.print("AI responded with \"" + response + "\".");
		
		return response;
		
	}
	
	/**
	 * Have the AI generate an image from a given prompt
	 * 
	 * @param prompt The thing to ask the AI
	 * @param username The name of the user, can be null
	 * @return The URL of the image
	 */
	public static String createImage(final String prompt, final String username)
	{
		try {
			Logger.LOG.print(username + " prompted the AI with \"" + prompt + "\".");
			
			CreateImageRequest imageRequest = new CreateImageRequest();
			imageRequest.setModel("dall-e-2");
			imageRequest.setPrompt(prompt);
			imageRequest.setSize("1024x1024");
			imageRequest.setQuality("standard");
			imageRequest.setN(1);
			imageRequest.setUser(username);
			
			ImageResult result = service.createImage(imageRequest);
			String resultUrl = result.getData().get(0).getUrl();
			
			Logger.LOG.print("AI responded with \"" + resultUrl + "\".");
			
			return resultUrl;
		} catch (OpenAiHttpException e)
		{
			Logger.WARNING.print("Could not generate image.");
			e.printStackTrace();
			return "https://cdn.discordapp.com/attachments/721951670132801600/1202142900969017415/image.png?ex=65cc61d2&is=65b9ecd2&hm=9c8f49d6f1ef74596b8c0d4b548c8a72e0b5fb096af7e9a3106360cc79f735ed&";
		}
		
	}
	
	public static void shutdown()
	{
		Logger.LOG.print("Shutting down the OpenAI API.");
		service.shutdownExecutor();
		Logger.LOG.print("API is shutdown.");
		
	}

}
