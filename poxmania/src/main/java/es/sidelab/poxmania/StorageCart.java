package es.sidelab.poxmania;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;


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
	private double totalPrize = 0.0;
	@OneToMany(mappedBy="product")
	private List<StorageCartLine> storageCartLine;
	private boolean processed = false;
	
	public StorageCart(){
		this.storageCartLine = new ArrayList<StorageCartLine>();
		this.totalPrize = 0.0;
		this.name = null;
		this.lastName = null;
		this.processed = false;
	}
	
	/**
	 * 
	 * @param name
	 * @param lastName
	 * @param storageCartLine
	 * @param totalPrize
	 */
	public StorageCart(String name, String lastName, List<StorageCartLine> storageCartLine, double totalPrize){
		this.storageCartLine = storageCartLine;
		this.totalPrize = totalPrize;
		this.name = name;
		this.lastName = lastName;
		this.processed = false;
	}
	
	/**
	 * 
	 * @param storageCart
	 */
	public StorageCart(StorageCart storageCart){
		this.lastName = storageCart.getLastName();
		this.name = storageCart.getName();
		this.processed = storageCart.getProcessed();
		this.storageCartLine = storageCart.getStorageCartLine();
		this.totalPrize = storageCart.getTotalPrize();
	}
	
	/**
	 * Returns a StorageCartLine object found by the id passed as parameter
	 * @param id
	 * @param storageCartList
	 * @return
	 */
	public StorageCartLine searchById(long id, List<StorageCartLine> storageCartList){
		StorageCartLine returned = null;
		if(storageCartList !=null){
			for(StorageCartLine aux : storageCartList){
				if(aux.getProduct().getId() == id){
					returned = aux;
					break;
				}
			}
		}
		return returned;
	}
	
	
	/**
	 * Add a new product from a StorageCart passed as parameter
	 * @param storageCartLine
	 */
	private void addProductFromStorageCart(StorageCartLine storageCartLine){
		int index = 0;
		for(StorageCartLine aux : this.getStorageCartLine()){
			if(aux.getProduct().getId() == storageCartLine.getProduct().getId()){
				break;
			}else{
				index++;
			}
		}
		
		this.getStorageCartLine().get(index).setCuantity(this.getStorageCartLine().get(index).getCuantity() + 
														storageCartLine.getCuantity());
	}
	
	/**
	 * Add an StorageCartLine object to the StorageCartLine list of the StorageCart object passed
	 * @param storageCartLine
	 */
	public void addItem(StorageCartLine storageCartLine){
		
		StorageCartLine searched = this.searchById(storageCartLine.getProduct().getId(), this.getStorageCartLine());
		if(searched != null){
			this.addProductFromStorageCart(storageCartLine);
		}else{
			this.getStorageCartLine().add(storageCartLine);
		}
		this.totalPrize = calculatePrize(this.getStorageCartLine());
		
	}
	
	/**
	 * Delete the StorageCartLine object passed as parameter from the StorageCart object passed 
	 * @param storageCartLine
	 */
	public void deleteItem(StorageCartLine storageCartLine){
		this.storageCartLine.remove(storageCartLine);
		this.setTotalPrize(calculatePrize(this.getStorageCartLine()));
	}
	
	/**
	 * Calculate the prize from StorageCartLine list passed as parameter
	 * @param storageCartLine
	 * @return
	 */
	public double calculatePrize(List<StorageCartLine> storageCartLine){
		double prize = 0.0;
		if(!storageCartLine.isEmpty()){
			for(StorageCartLine pAux : storageCartLine){
				prize = prize + (pAux.getPrize());
			}
		}
		
		return prize;
	}
	

	@OneToMany(mappedBy="product")
	public List<StorageCartLine> getStorageCartLine() {
		return storageCartLine;
	}
    
	public void setStorageCartLine(List<StorageCartLine> storageCartLine) {
		this.storageCartLine = storageCartLine;
	}
	
	public boolean getProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
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
	
	@Override
	public String toString(){
		return "[ "+this.getName()+", "+this.getLastName()+", "+String.valueOf(this.getTotalPrize())+", procesado: "+ this.getProcessed()+ " ]";
		
	}
	
}
