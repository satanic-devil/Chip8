
package Chip8Engine;
import java.util.*;

public class CPU{
	
	private Memory memory;
	private Registers registers;
	private Joypad joypad;
	private Game game;
	private Display display;
	public boolean waitingForKey = false;
	private boolean debugModeOn = false;
	public boolean ready = false;
	public boolean graphicsUpdated = false;
	public int timeToWait = 60;

	public CPU( Memory memory, Registers registers,Game game, Joypad joypad){
		this.memory = memory;
		this.registers = registers;
		this.game = game;
		this.joypad = joypad;
		
	}

	
	public void loadGame(Display display){
		this.display = display;
		display.setJoypad( joypad);
		int opcode;
		byte[] codes;
		memory.loadGame( game.getGame(), game.getSize() );
		int i = 0;
		String op;
		Scanner kin = new Scanner(System.in);
		int speed = 1;
		int fps = 1000/60;
		while( true ){
			long start = System.currentTimeMillis( );
			opcode = memory.nextInstruction();
			execute( opcode );
			i = registers.getDelayTimer();
			if( i != 0)
				registers.setDelayTimer( (byte)(i-1));
			i = registers.getSoundTimer();
			if( i != 0 )
				registers.setSoundTimer( (byte)(i-1));
			long end = System.currentTimeMillis( );
			if( speed%10 == 0){
				if( graphicsUpdated){
					graphicsUpdated = false;
					display.repaint();
				}
				
				try{ Thread.sleep(60); }catch(Exception e){}
				speed = 0;
			}
			speed++;
			
		}
	}

	private void execute(int instruction){
		short opcode = (short)(instruction >> 12);
		debug("");
		short result;
		int x, kk, y, jKey, DT, I, temp;
		byte Vx, Vy;
		int[] tempArray = new int[ 15 ];
		switch( opcode ){
			
			case 0 : 	opcode = (short)(instruction & 0x00ff);
				switch( opcode ){
					case 0x00ee : 	debug("00EE : RET -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							memory.returnFromSubroutine();
							break;
					case 0x00e0 : 	debug("00E0 : CLS -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							display.clearScreen();
							break;
				} 
				break;

			case 1 : 	debug("1nnn : JUMP ADDR -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				memory.setProgramCounter( (short)(0x0fff & instruction) );
				break;
			case 2 : 	debug("2nnn : CALL SUBROUTINE --> " +  instruction + " | Hex : " + Integer.toHexString(instruction));
				memory.callSubroutine( (short)(0x0fff & instruction) );
				break;
			case 3 :	debug("3xkk : SE Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				kk = getKK( instruction );
				Vx = registers.getRegisterValueAt( x );
				skipIfEqual(Vx, kk);
				break;
			case 4 :	debug("4xkk : SNE Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				kk = getKK( instruction );
				Vx = registers.getRegisterValueAt( x );
				skipIfNotEqual(Vx, kk);
				break;
			case 5 :	debug("5xy0 : SE Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				y = ( instruction >> 4 ) & 0x00f;
				Vx = registers.getRegisterValueAt( x );
				Vy = registers.getRegisterValueAt( y );
				skipIfEqual(Vx, Vy);
				break;
			case 6  :	debug("6xkk : LD Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				kk = instruction & 0x00ff;
				x = getX( instruction ); 
				registers.setRegisterValueAt( x, (byte)kk);
				debug("V" + x +" : " + registers.getRegisterValueAt( x ) );
				break;
			case 7 :	debug("7xkk : ADD Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				kk = getKK( instruction );
				Vx = registers.getRegisterValueAt( x );
				registers.setRegisterValueAt( x, (byte)( Vx + kk) );
				debug("V" + x +" : " + registers.getRegisterValueAt( x ) );
				break;
			case 8 : 	opcode = (short)(instruction & 0x000f);
				debug("8xyk : ADD Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction) + " | Option : " + opcode);
				switch( opcode ){
					case 0 : 	debug("8xy0 : LD Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						registers.setRegisterValueAt( x, Vy);
						debug("y : " + y + " | x : " + x);
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 1 : 	debug("8xy1 : OR Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						registers.setRegisterValueAt( x, (byte)( Vx | Vy) );
						debug("x : " + x + " | y : " + y);
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 2 :	debug("8xy2 : AND Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						registers.setRegisterValueAt( x, (byte)( Vx & Vy) );
						debug("x : " + x + " | y : " + y);
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 3 :	debug("8xy3 : XOR Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						result = (short)( (Vx & 0xff) ^ (Vy & 0xff) );
						registers.setRegisterValueAt( x, (byte)result );
						debug("x : " + x + " | y : " + y);
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 4 :	debug("8xy4 : ADD Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						result = (short)( (Vx & 0xff) + (Vy & 0xff));
						if( result > 255) registers.setRegisterValueAt( 15, (byte)1);
						registers.setRegisterValueAt( x, (byte)result );
						debug("x : " + x + " | y : " + y);
						debug("VF : " + registers.getRegisterValueAt( 15));
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;

					case 5 :	debug("8xy5 : SUB Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						if( (Vx & 0xff) > (Vy & 0xff)) 
							registers.setRegisterValueAt( 15, (byte)1);
						else
							registers.setRegisterValueAt( 15, (byte)0);
	
						result = (short)( (Vx & 0xff) - (Vy & 0xff));
						
						registers.setRegisterValueAt( x, (byte)result );
						debug("x : " + x + " | y : " + y);
						debug("VF : " + registers.getRegisterValueAt( 15));
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 6 :	debug("8xy6 : SHR Vx {,Vy} -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						if( (Vx & 0x01) == 1) 
							registers.setRegisterValueAt( 15, (byte)1);
						else
							registers.setRegisterValueAt( 15, (byte)0);
						result = (short)((Vx & 0xff) >> 1);
						registers.setRegisterValueAt( x, (byte) result);
						debug("x : " + x + " | y : " + y);
						debug("VF : " + registers.getRegisterValueAt( 15));
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
					case 7 :	debug("8xy7 : SUBN Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						if( (Vy & 0xff) > (Vx & 0xff)) 
							registers.setRegisterValueAt( 15, (byte)1);
						else
							registers.setRegisterValueAt( 15, (byte)0);
	
						result = (short)( (Vy & 0xff) - (Vx & 0xff));
						
						registers.setRegisterValueAt( x, (byte)result );
						debug("x : " + x + " | y : " + y);
						debug("VF : " + registers.getRegisterValueAt( 15));
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;

					case 0xe :	debug("8xyE : SHL Vx {,Vy} -->" + instruction + " | Hex : " + Integer.toHexString(instruction));	
						x = getX( instruction );
						y = getY( instruction );
						Vy = registers.getRegisterValueAt( y );
						Vx = registers.getRegisterValueAt( x );
						result = (short)( ( (Vx & 0xff) >> 4) & 0x8);
						if( result == 8) 
							registers.setRegisterValueAt( 15, (byte)1);
						else
							registers.setRegisterValueAt( 15, (byte)0);
						result = (short)((Vx & 0xff) << 1);
						registers.setRegisterValueAt( x, (byte) result);
						debug("x : " + x + " | y : " + y);
						debug("VF : " + registers.getRegisterValueAt( 15));
						debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
						break;
				}
				break;
			case 9 :	debug("9xy0 : SNE Vx, Vy -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				y = getY( instruction );
				Vx = registers.getRegisterValueAt( x );
				Vy = registers.getRegisterValueAt( y );
				skipIfNotEqual( Vx, Vy);
				debug("V" + x +" : " + registers.getRegisterValueAt( x ) + " | V" + y + ":" + registers.getRegisterValueAt( y ));
				break;
			case 0xA :	debug("Annn : LD I, nnn -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				result = (short)(instruction & 0x0fff);
				registers.setIndexRegister( result );
				debug(" I : " + registers.getIndexRegister());
				break;
			case 0xB :	debug("Bnnn : JUMP V0, nnn -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				Vx = registers.getRegisterValueAt( 0 );
				debug("V0 : " + Vx);
				result = (short)(instruction & 0x0fff);
				result = (short)(result + ( Vx & 0xff));
				debug("Vx + nnn : " + result);
				memory.setProgramCounter( result );
				break;
			case 0xC :	debug("Cxkk : RND Vx, kk -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				kk = getKK( instruction );
				int rand = (int)Math.floor(Math.random()*255+1);
				x = getX( instruction );
				result = (short)( kk & rand);
				registers.setRegisterValueAt( x, (byte)result);
				debug("V" + x + " : " + registers.getRegisterValueAt( x ));
				break;
			case 0xD : debug("Dxyn : DRW Vx, Vy, n -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				x = getX( instruction );
				y = getY( instruction );
				Vx = registers.getRegisterValueAt( x );
				Vy = registers.getRegisterValueAt( y );
				temp = instruction & 0x000f;
					
				I = registers.getIndexRegister();
				for( int z=0; z<temp; z++){
					tempArray[ z ] = memory.getRAMValueAt( I + z) & 0xff;
					debug("Sprite At " + (I+z) +" : " + tempArray[z]);
				}
				debug("Draw At Vx : " + Vx + " | Vy : "+ Vy);
				graphicsUpdated = true;
				display.setScreenData(Vx, Vy, temp, tempArray);
				break;
			case 0xE: 	debug("ExXX :  -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				opcode = (short)(instruction & 0x00ff);
				switch( opcode ){
					case 0x9E :	debug("Ex9E : SKP Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							jKey = joypad.getKeyStatusAt( Vx );
							if( jKey == 1 ) memory.skipInstruction();
							debug("x : " + x +" | V" + x +" : " + Vx + " | Key At Vx Pressed : "  + (jKey == 1));
							break;
					case 0xA1:	debug("ExA1 : SKNP Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							jKey = joypad.getKeyStatusAt( Vx );
							if( jKey != 1 ) memory.skipInstruction();
							debug("x : " + x +" | V" + x +" : " + Vx + " | Key At Vx Pressed : " + (jKey == 1));
							break;
				}
				break;
			case 0xF :	debug("FxXX :  -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
				opcode = (short)(instruction & 0x00ff);
				switch( opcode ){
					case 0x07 :	debug("Fx07 : LD Vx, DT -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							DT = registers.getDelayTimer();	
							registers.setRegisterValueAt(x, (byte)DT);
							debug(" V" + x +" : " + registers.getRegisterValueAt( x ) +" | DT : " + DT);
							break;
					case 0x0A:	debug("Fx0A : LD Vx, K -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
								
							waitingForKey = true;

							
							
							
							x = getX( instruction );
							registers.setRegisterValueAt( x, joypad.getKeyValue()); 
							debug("Key : " + joypad.getKeyValue() + " | x " + x + " | V" + x + " : "  + registers.getRegisterValueAt( x ) );
							break;
					case 0x15:	debug("Fx15 : LD DT, Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							registers.setDelayTimer( Vx );
							debug(" V" + x + " : " + Vx + " | DT : " + registers.getDelayTimer() );
							break;
					case 0x18:	debug("Fx18 : LD ST, Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							registers.setSoundTimer( Vx );
							debug(" V" + x + " : " + Vx + " | DT : " + registers.getSoundTimer() );
							break;
					case 0x1E :	debug("Fx1E : ADD I, Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							I = registers.getIndexRegister();
							registers.setIndexRegister( (short)(I + Vx));
							debug(" V"+x+" : "+Vx+" | I :" + I +" | I : "+registers.getIndexRegister());
							break;
					case 0x29 :	debug("Fx29 : LD F, Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							I = memory.getDigitLocation( Vx );
							registers.setIndexRegister( (short)I );
							debug(" V" + x +" : "+ Vx +" | Location : " + memory.getDigitLocation( Vx ) + " | I : " + registers.getIndexRegister());
							break;
					case 0x33 :	debug("Fx33 : LD B, Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							Vx = registers.getRegisterValueAt( x );
							I = registers.getIndexRegister();
							int num = Vx;
							int  d = num % 10;
							num = num/10;
							memory.addToRAM( I+2, (byte)d);
							d = num % 10;
							num = num/10;
							memory.addToRAM( I+1, (byte)d);
							memory.addToRAM( I, (byte)num);
							debug("V"+x+" : "+Vx+" | memory[ "+I+"] : "+memory.getRAMValueAt( I ) + " | memory[ "+I+" + 1 ] : "+memory.getRAMValueAt( I+1 )+" | memory[ "+I+" + 2] : "+memory.getRAMValueAt( I+2 ) );			
							break;
					case 0x55:	debug("Fx55 : LD [I], Vx -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							I = registers.getIndexRegister();
							debug( "x : " + x + " | I : " + I );
							for( int index = 0; index<=x; index++){
								Vx = registers.getRegisterValueAt( index );
								memory.addToRAM( I+index,  Vx);
								debug("V"+x+" : "+ Vx + " | I+Index : " + (I+index) + " | Memory Value : " + memory.getRAMValueAt(index+I) );
							}
							break;
					case 0x65:	debug("Fx65 : LD Vx, [I] -->" + instruction + " | Hex : " + Integer.toHexString(instruction));
							x = getX( instruction );
							I = registers.getIndexRegister();
							debug( "x : " + x + " | I : " + I );
							for( int index = 0; index<=x; index++){
								Vx = memory.getRAMValueAt( I+index);
								registers.setRegisterValueAt( index, (byte)Vx);
								debug("V"+x+" : "+ Vx + " | I+Index : " + (I+index) + " | Memory Value : " + memory.getRAMValueAt(index+I) );
							}
							break;		
				}
				break; 
							
							
		}		

	}


	private int getY( int opcode ){
		return ( opcode >> 4) & 0x00f;
	}

	private int getX(int opcode){
		return ( opcode >> 8 ) & 0x0f;
	}

	private int getKK( int opcode ){
		return opcode & 0x00ff;
	}

	private void skipIfEqual(int val1, int val2){
		if( val1 == val2 ){
			debug("Value 1 == Value 2");
			memory.skipInstruction();
		}
	}

	
	private void skipIfNotEqual(int val1, int val2){
		if( val1 != val2 ){
			debug("Value 1 != Value 2");
			memory.skipInstruction();
		}
	}		
	private void debug(String msg){
		if( debugModeOn ){
			System.out.println( msg );
		}
	}
	private void saveRAMToFile(){
		
	}
}