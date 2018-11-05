package data;

public class Modification {
	
	
	public String id;
	public int type;
	public int x;
	public int y;
	
	public final static int DEPLACEMENT = 0;
	public final static int CREATION = 1;
	public final static int SUPPRESSION = 2;
	
	public Modification(String id, int type, int x, int y) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
	}
	
}
