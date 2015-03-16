package es.sidelab.poxmania;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class StorageCart {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private ArrayList<Product> productsList;
	private double totalPrize;
	
	public StorageCart(){
		this.productsList = new ArrayList<Product>();
	}
	
	public StorageCart(ArrayList<Product> productsList){
		this.productsList = productsList;
	}
	
	public void addItem(Product product){
		this.productsList.add(product);
		this.totalPrize = calculatePrize();
		
	}
	
	public void deleteItem(Product product){
		this.productsList.remove(product);
		this.totalPrize = calculatePrize();
	}
	
	public double calculatePrize(){
		double prize = 0.0;
		for(Product pAux : this.productsList){
			prize += pAux.getPrize();
		}
		
		return prize;
	}

	public ArrayList<Product> getProductsList() {
		return productsList;
	}

	public void setProductsList(ArrayList<Product> productsList) {
		this.productsList = productsList;
	}

	public double getTotalPrize() {
		return this.totalPrize;
	}
	
	public long getId() {
		return id;
	}
	
}
