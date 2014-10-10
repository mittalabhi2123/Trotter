package com.trotter.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommunicationDTO<T> implements Serializable{

	private boolean errFlag;
	private StringBuffer message = new StringBuffer();
	private List<T> dataLst = new ArrayList<T>();
	
	public boolean isErrFlag() {
		return errFlag;
	}
	public void setErrFlag(boolean errFlag) {
		this.errFlag = errFlag;
	}
	public String[] getMessage() {
		return message.toString().split("~");
	}
	public void setMessage(String message) {
		this.message.append(message + "~");
	}
	public List<T> getDataLstList() {
		return dataLst;
	}
	public void setData(T data) {
		this.dataLst.add(data);
	}
	public void setDataLst(List<T> dataLstList) {
		this.dataLst.addAll(dataLstList);
	}
	
}
