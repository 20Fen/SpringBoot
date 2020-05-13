package com.example.demo.system.util;

import java.util.List;

public class Page1<T>{
	
	private Integer pageIndex;
	private Integer pageSize;
	private int total;
	private int totalPage;
	private java.util.List<T> content;
	private T contentT;

	public Integer getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage() {
		this.totalPage = (int)Math.ceil((double)this.total/this.pageSize);
	}
	public java.util.List<T> getContent() {
		return content;
	}
	public void setContent(java.util.List<T> content) {
		this.content = content;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public T getContentT() {
		return contentT;
	}

	public void setContentT(T contentT) {
		this.contentT = contentT;
	}

	public Page1(){
	}

	public Page1(int total, int totalPage, List<T> content) {
		this.total = total;
		this.totalPage = totalPage;
		this.content = content;
	}

}
