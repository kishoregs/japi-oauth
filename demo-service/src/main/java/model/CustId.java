package model;

public class CustId{
	private String pid;

	public void setPid(String pid){
		this.pid = pid;
	}

	public String getPid(){
		return pid;
	}

	@Override
 	public String toString(){
		return 
			"CustId{" + 
			"pid = '" + pid + '\'' + 
			"}";
		}
}
