package me.brandonkey.bot;

public enum Logger {
	LOG,
	WARNING;
	
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	//private static final String ANSI_WHITE = "\u001B[37m";
	
	public void print(final String message)
	{
		System.out.println("[-------------------------------]");
		
		switch (this) {
			case LOG:
				System.out.println(ANSI_YELLOW + "[Log] " + message + ANSI_RESET);
				break;
			case WARNING:
				System.out.println(ANSI_RED + "[WARNING] " + message + ANSI_RESET);
				break;
			default:
				System.out.println(message);
				break;
		}
		
		System.out.println("[-------------------------------]");
		
	}
	
}
