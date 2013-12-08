package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;


public class MapperAmountAndBorrowedMaterial {	
	private int amount;
	private BorrowedMaterial borrowedMaterial;

	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public BorrowedMaterial getBorrowedMaterial() {
		return borrowedMaterial;
	}
	
	public void setBorrowedMaterial(BorrowedMaterial borrowedMaterial) {
		this.borrowedMaterial = borrowedMaterial;
	}
	
	public void increaseAmount(){
		this.amount++;
	}
}
