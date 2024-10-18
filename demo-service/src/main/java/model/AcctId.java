package model;

public class AcctId{
	private String number;
	private String code;

	public void setNumber(String number){
		this.number = number;
	}

	public String getNumber(){
		return number;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	@Override
 	public String toString(){
		return 
			"AcctId{" + 
			"number = '" + number + '\'' + 
			",code = '" + code + '\'' + 
			"}";
		}
}
