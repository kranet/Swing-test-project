public class Position {
	private int y;
	private int x;
	
	public Position(int x, int y){
		this.y=y;
		this.x=x;		
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	public String toString(){
		return ""+x+", "+y;
	
	}
	@Override
	public int hashCode()
	{
		return x + y * 10000;
	}
	
	@Override
	
	public boolean equals(Object object){
		if (object == this){
			return true;
		}
		if (object == null){
			return false;
		}
		if (object.getClass() != getClass ()) {
			return false;
		}
		Position p = (Position) object;
		if (x == p.getX() && y == p.getY()){
			return true;
		}
		return false;
	}
}

