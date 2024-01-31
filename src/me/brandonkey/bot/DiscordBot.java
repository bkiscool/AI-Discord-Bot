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
	private static Guild test_guild;

	public DiscordBot(final String token) {
		try {
			jda = JDABuilder.createDefault(token)
							.addEventListeners(this)
							.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
							.build().awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		final SlashCommandData promptCommand = Commands.slash("prompt", "Prompt the AI.").addOption(OptionType.STRING, "prompt", "The prompt to give to the AI.", true);
		final SlashCommandData shutdownCommand = Commands.slash("shutdown", "Shutdown the bot.");
		
		test_guild = jda.getGuildById("721951670132801596");
		
		test_guild.updateCommands().queue();
		test_guild.updateCommands().addCommands(promptCommand, shutdownCommand).queue();
	}
	
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{
		
		switch (event.getName())
		{
			case "prompt":
				event.deferReply().queue();
				
				final String prompt = event.getOption("prompt", OptionMapping::getAsString);
				
				new Runnable()
				{
					@Override
					public void run()
					{
						final String response = AI.prompt(prompt, event.getUser().getName());
						final String discordResponse = "> " + prompt + "\n\n" + response;
						
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
				shutdown();
				AI.shutdown();
				System.exit(0);
				
				break;
			default:
				break;
				
		}
		
	}
	
	@Override
	public void onReady(ReadyEvent event)
	{
		System.out.println("Discord bot ready.");
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
		System.out.println("[-] " + message);
		
		return channel.sendMessage(message);
	}
	
	public static void shutdown()
	{
		System.out.println("Shutting down the Discord bot.");
		sendMessage("Shutting down..", test_guild.getDefaultChannel().asTextChannel()).complete();
		jda.shutdown();
		
	}

}
