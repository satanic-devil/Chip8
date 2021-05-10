package Chip8Engine;
import java.io.*;

public class Game{
	private boolean hasErrorOccured = false;
	private String errorMessage = "";
	private String fileName;
	private byte[] game;

	private long size;

	public Game( String fileName){
		this.fileName = fileName;
		loadGame();
	}

	private void loadGame(){
		File file = new File( fileName );
		size = file.length();
		int n = 0;
		try{
			FileInputStream is = new FileInputStream( file );	
			game = new byte[ (int)size ];
			is.read( game );
			is.close();
		}catch( Exception e){
			hasErrorOccured = true;
			errorMessage = e.getMessage() + "\n" + e;
		}
	}
	
	public long getSize(){
		return size;
	}		

	public boolean error(){
		return hasErrorOccured;
	}

	public String getErrorMsg(){
		return errorMessage;
	}	

	public byte[] getGame(){
		return game;
	}
}