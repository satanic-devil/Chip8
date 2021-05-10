/*
* CHIP 8 Emulator implemented in Java
* Author: satanic-devil
*
*
*
*/
import Chip8Engine.*;
class Chip8{

	private Memory memory;
	private Registers registers;
	private Game game;
	private CPU cpu;
	private Display display;
	private Joypad joypad;
	public boolean ready;

	public static void main( String args[] ){
		if( args.length == 1 )
			new Chip8( args[0] );
		else {
			System.out.println("Usage : java Chip8 gameName");
			
		}
	}

	Chip8(String fileName){
		game = new Game( fileName );
		memory = new Memory();
		registers = new Registers();
		joypad = new Joypad();
		cpu = new CPU( memory, registers, game, joypad);
		display = new Display(registers, cpu);
		cpu.loadGame(display);
		
	}
}
