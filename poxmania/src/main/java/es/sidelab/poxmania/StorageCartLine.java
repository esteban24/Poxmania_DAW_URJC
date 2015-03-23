package es.sidelab.poxmania;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import es.sidelab.poxmania.Product;

@Entity
public class StorageCartLine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7985565044371831627L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private Product product;
	private int cuantity;
	
	public StorageCartLine(){
		this.product = null;
		this.cuantity = 0;
	}
	
	public StorageCartLine(Product product, int cuantity){
		this.product = product;
		this.cuantity = cuantity;
	}
	
	public double getPrize(){
		return (this.cuantity * this.getProduct().getPrize());
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="product")
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getCuantity() {
		return cuantity;
	}
	public void setCuantity(int cuantity) {
		this.cuantity = cuantity;
	}
	
}
