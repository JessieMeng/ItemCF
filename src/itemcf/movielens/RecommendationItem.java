package itemcf.movielens;

public class RecommendationItem implements Comparable<RecommendationItem>{
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

    public String toString(){
    	return  this.itemid+" @ "+this.itemscore;
    }
	@Override
	public int compareTo(RecommendationItem o) {
		if(this.itemscore > o.itemscore)
			return -1;
		else if(this.itemscore < o.itemscore)
			return 1;
		else 
			return 0;
	}
     
     
}
