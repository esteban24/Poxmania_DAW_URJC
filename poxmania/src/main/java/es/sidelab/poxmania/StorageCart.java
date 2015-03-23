package es.sidelab.poxmania;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class StorageCart implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6309613457936170782L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	private String lastName;
	private ArrayList<Product> productsList;
	private double totalPrize;
	
	public StorageCart(){
		this.productsList = new ArrayList<Product>();
		this.totalPrize = calculatePrize();
		this.name = null;
		this.lastName = null;
	}
	
	public StorageCart(ArrayList<Product> productsList, String name, String lastName){
		this.productsList = productsList;
		this.totalPrize = calculatePrize();
		this.name = name;
		this.lastName = lastName;
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
	
	public void setTotalPrize(double totalPrize){
		this.totalPrize = totalPrize;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
