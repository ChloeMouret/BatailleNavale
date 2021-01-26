

public class Key {
	private int height;
	private int width; 
	
	/**
	 * @param h
	 * @param w
	 * create key with height h and width w 
	 */
	public Key(int w, int h) {
		this.width = w;
		this.height = h;
	}
	
	
	/**
	 * @return height 
	 */
	public int getHeight() {
		return this.height;
	}
	
	/**
	 * @return width
	 */
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int hashCode() {
		return this.getWidth()*31+this.getHeight();
	}

	@Override
	public boolean equals(Object o) {
		if (! (o instanceof Key)){
			return false;
		}
		else {
			Key k = (Key)o;
			return (this.getHeight() == k.getHeight() && this.getWidth() == k.getWidth());
		}
	}
}
