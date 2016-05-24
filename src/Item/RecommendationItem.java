package Item;

public class RecommendationItem implements Comparable{
     private int itemid ;
     private double itemscore;
     public RecommendationItem(int itemid,double itemscore){
    	 this.itemid = itemid;
    	 this.itemscore = itemscore;
     }
	public int getItemid() {
		return itemid;
	}
	public void setItemid(int itemid) {
		this.itemid = itemid;
	}
	public double getItemscore() {
		return itemscore;
	}
	public void setItemscore(double itemscore) {
		this.itemscore = itemscore;
	}

	@Override
	public int compareTo(Object o) {
		RecommendationItem r = (RecommendationItem)o;
		if(this.itemscore > r.itemscore)
			return -1;
		else if(this.itemscore < r.itemscore)
			return 1;
		else 
			return 0;
	}
    
    public String toString(){
    	return  this.itemid+" @ "+this.itemscore;
    }
     
     
}
