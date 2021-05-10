package Chip8Engine;

public class Joypad{
	private byte[] keyStatus;
	private boolean keyPressed = false;
	private byte keyValue;

	public Joypad(){
		keyStatus = new byte[ 16 ];
	}

	public void setKeyAt( int location ){
		keyStatus[ location ] = 1;
		keyValue = (byte)location;
		keyPressed = true;
	}

	public void display(){
		//for( int i=0; i<16; i++)
		//	System.out.println("Location: " + i + " -> " + keyStatus[i]);
	}

	public byte getKeyStatusAt( int location ){
		return keyStatus[ location ];
	}

	public void setKeyReleaseAt( int location ){
		keyStatus[ location ] = 0;
		keyPressed = false;
	}

	public void resetAnyKeyPressed(){
		keyPressed = false;
		
	}

	public void resetKeys(){
	for( int i=0; i<16;i++)
		keyStatus[ i ] = 0;
	}

	public boolean anyKeyPressed(){
		return keyPressed;
	}

	public byte getKeyValue(){
		return keyValue;
	}
}	