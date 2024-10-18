package model;

public class Response{
	private CustId custId;
	private AcctId acctId;

	public void setCustId(CustId custId){
		this.custId = custId;
	}

	public CustId getCustId(){
		return custId;
	}

	public void setAcctId(AcctId acctId){
		this.acctId = acctId;
	}

	public AcctId getAcctId(){
		return acctId;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"custId = '" + custId + '\'' + 
			",acctId = '" + acctId + '\'' + 
			"}";
		}
}
