package me.brandonkey.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.FileUpload;

public class DiscordBot extends ListenerAdapter {
	
	private static JDA jda;
	private static Guild GUILD;

	public DiscordBot(final String token, final String guildId) {
		Logger.LOG.print("Starting Discord bot.");
		try {
			jda = JDABuilder.createDefault(token)
							.addEventListeners(this)
							.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
							.build().awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		final SlashCommandData askCommand = Commands.slash("ask", "Prompt the AI.")
													.addOption(OptionType.STRING, "prompt", "The prompt to give to the AI.", true);
		final SlashCommandData shutdownCommand = Commands.slash("shutdown", "Shutdown the bot.");
		final SlashCommandData createImageCommand = Commands.slash("createimage", "Prompt the AI to create an image.")
															.addOption(OptionType.STRING, "prompt", "The prompt to give to the AI.", true);
		
		GUILD = jda.getGuildById(guildId);
		
		jda.retrieveCommands().queue(commands -> commands.stream().forEach(command -> command.delete().queue()));
		jda.updateCommands().queue();
		jda.updateCommands().addCommands(askCommand, shutdownCommand, createImageCommand).queue();
		
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{
		
		switch (event.getName())
		{
			case "ask":
				Logger.LOG.print("Discord user ran /ask");
				event.deferReply().queue();
				
				final String askPrompt = event.getOption("prompt", OptionMapping::getAsString);
				
				new Runnable()
				{
					@Override
					public void run()
					{
						final String response = AI.askAi(askPrompt, event.getUser().getName());
						final String discordResponse = "> " + askPrompt + "\n\n" + response;
						
						if (discordResponse.length() > 2000)
						{
							final FileUpload file = FileUpload.fromData(discordResponse.getBytes(), "response.txt");
							
							event.getHook().sendFiles(file).queue();
							
							
						} else
						{
							event.getHook().sendMessage(discordResponse).queue();
						}
						
					}
					
				}.run();
				
				break;
			case "shutdown":
				Logger.LOG.print("Discord user ran /shutdown");
				shutdown();
				AI.shutdown();
				System.exit(0);
				
				break;
			case "createimage":
				Logger.LOG.print("Discord user ran /createimage");
				event.deferReply().queue();
				
				final String createImagePrompt = event.getOption("prompt", OptionMapping::getAsString);
				
				new Runnable()
				{
					@Override
					public void run()
					{
						final String responseUrl = AI.createImage(createImagePrompt, event.getUser().getName());
						final String discordResponse = "> " + createImagePrompt;
						
						event.getHook().sendMessage(discordResponse).queue();
						sendMessage(responseUrl, event.getMessageChannel()).queue();
						
					}
					
				}.run();
				
				break;
			default:
				break;
				
		}
		
	}
	
	@Override
	public void onReady(ReadyEvent event)
	{
		Logger.LOG.print("Discord bot is ready.");
		
	}
	
	/*
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (event.getAuthor().isBot()) return;
		
		final String message = event.getMessage().getContentDisplay();
		
		if (message.startsWi)
		
	}
	*/
	
	private static MessageCreateAction sendMessage(final String message, final MessageChannel channel)
	{
		Logger.LOG.print("Sending message: " + message);
		return channel.sendMessage(message);
	}
	
	public static void shutdown()
	{
		Logger.LOG.print("Shutting down the Discord bot.");
		sendMessage("Shutting down..", GUILD.getDefaultChannel().asTextChannel()).complete();
		jda.shutdown();
		Logger.LOG.print("Discord bot finished shutting down.");
		
	}

}
