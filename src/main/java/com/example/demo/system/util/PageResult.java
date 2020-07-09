package com.example.demo.system.util;

import java.io.Serializable;

public class PageResult<T> implements Serializable {

	private T pageData;
	private int pageNum;
	private int pageSize;
	private long total;
	private int pages;

	public PageResult() {
	}

	public PageResult(T pageData, int pageNum, int pageSize, long total, int pages) {
		this.pageData = pageData;
		this.pageNum = pageNum;
		this.pageSize = pageSize;
		this.total = total;
		this.pages = pages;
	}

	public PageResult(T pageData, long total) {
		this.pageData = pageData;
		this.total = total;
	}

	public T getPageData() {
		return this.pageData;
	}

	public void setPageData(T pageData) {
		this.pageData = pageData;
	}

	public int getPageNum() {
		return this.pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public long getTotal() {
		return this.total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getPages() {
		return this.pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

}
