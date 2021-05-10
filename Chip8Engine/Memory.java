package Chip8Engine;

public class Memory{
	private final int RAM_SIZE = 4096, STACK_SIZE = 16;
	private byte stackPointer;
	private byte[] RAM;
	
	private short programCounter;
	private short[] stack;
	private byte[] instruction;

	/* THE BASIC SPRITES ARRAY */
	public static final int[] BASIC_SPRITES = {
		/* 0 */
		0b11110000,
		0b10010000,
		0b10010000,
		0b10010000,	
		0b11110000,
		/* 1 */
		0b00100000,
		0b01100000,
		0b00100000,
		0b00100000,
		0b01110000,
		/* 2 */
		0b11110000,
		0b00010000,
		0b11110000,
		0b10000000,
		0b11110000,
		/* 3 */
		0b11110000,
		0b00010000,
		0b11110000,
		0b00010000,
		0b11110000,
		/* 4 */
		0b10010000,
		0b10010000,
		0b11110000,
		0b00010000,
		0b00010000,
		/* 5 */
		0b11110000,
		0b10000000,
		0b11110000,
		0b00010000,
		0b11110000,
		/* 6 */
		0b11110000,
		0b10000000,
		0b11110000,
		0b10010000,
		0b11110000,
		/* 7 */
		0b11110000,
		0b00010000,
		0b00100000,
		0b01000000,
		0b01000000,
		/* 8 */
		0b11110000,
		0b10010000,
		0b11110000,
		0b10010000,
		0b11110000,
		/* 9 */
		0b11110000,
		0b10010000,
		0b11110000,
		0b00010000,
		0b11110000,
		/* A */
		0b11110000,
		0b10010000,
		0b11110000,
		0b10010000,
		0b10010000,
		/* B */
		0b11100000,
		0b10010000,
		0b11100000,
		0b10010000,
		0b11100000,
		/* C */
		0b11110000,
		0b10000000,
		0b10000000,
		0b10000000,
		0b11110000,
		/* D */
		0b11100000,
		0b10010000,
		0b10010000,
		0b10010000,
		0b11100000,
		/* E */
		0b11110000,
		0b10000000,
		0b11110000,
		0b10000000,
		0b11110000,
		/* F */
		0b11110000,
		0b10000000,
		0b11110000,
		0b10000000,
		0b10000000,
	};
	/* END OF BASIC SPRITE */

	public Memory(){
		RAM= new byte[ RAM_SIZE ];
		
		stack = new short[ STACK_SIZE ];
		instruction = new byte[ 2 ];
		stackPointer = 0;
		programCounter = 0x200;
		addBasicSprites();
	}	

	private void addBasicSprites(){
		int basicSpritesSize = BASIC_SPRITES.length;

		for( int i = 0; i<basicSpritesSize; i++)
			RAM[ i ] = (byte)BASIC_SPRITES[i];
	}	

	
	public void skipInstruction(){
		programCounter += 2;	
	}

	public void addToRAM(int loc, byte val){
		RAM[ loc ] = val;
	}

	public byte getRAMValueAt(int loc){
		return RAM[ loc ];
	}
	public int nextInstruction(){
		short higherByte = (short) (RAM[ programCounter++ ] & 0xff);
		short lowerByte = (short) (RAM[ programCounter++ ] & 0xff);
		int opcode = ( higherByte << 8) | lowerByte;
		return opcode;
	}

	public void setProgramCounter( short newAddress){
		programCounter = newAddress;
		
	}

	public void callSubroutine( short subAddress){
		push( programCounter );
		setProgramCounter( subAddress );
	}

	public void returnFromSubroutine(){
		programCounter = pop();
	}

	public void push(short value){
		stack[ stackPointer++ ] = value;
	}

	public short pop(){
		return stack[  --stackPointer ];
		
	}

	public byte[] getRAMData(){
		return RAM;
	}

	public void loadGame(byte[] game, long size){
		size += 0x200;
		for( int i = 0x200, j = 0; i < size; i++,j++){
			RAM[ i ] =game[ j ];
		}
		
	}

	public int getDigitLocation(int val){
		return val*5;
	}
}