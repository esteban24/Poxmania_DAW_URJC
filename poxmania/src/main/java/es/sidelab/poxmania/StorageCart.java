package es.sidelab.poxmania;

import java.io.Serializable;
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
	private double totalPrize;
	@OneToMany(mappedBy="product")
	private List<StorageCartLine> storageCartLine;
	private boolean processed = false;
	
	public StorageCart(){
		this.storageCartLine = null;
		this.totalPrize = 0.0;
		this.name = null;
		this.lastName = null;
		this.processed = false;
	}
	
	public StorageCart(String name, String lastName, List<StorageCartLine> storageCartLine){
		this.storageCartLine = storageCartLine;
		this.totalPrize = calculatePrize();
		this.name = name;
		this.lastName = lastName;
		this.processed = false;
	}
	
	
	public StorageCartLine searchById(long id, List<StorageCartLine> storageCartList){
		StorageCartLine returned = null;
		for(StorageCartLine aux : storageCartList){
			if(aux.getId() == id){
				returned = aux;
				break;
			}
		}
		return returned;
	}
	
	public void addProductFromStorageCart(StorageCartLine storageCartLine){
		int index = 0;
		for(StorageCartLine aux : this.getStorageCartLine()){
			if(aux.getId() == storageCartLine.getId()){
				break;
			}else{
				index++;
			}
		}
		
		this.getStorageCartLine().get(index).setCuantity(this.getStorageCartLine().get(index).getCuantity() + 1);
	}
	
	public void addItem(StorageCartLine storageCartLine){
		
		StorageCartLine searched = this.searchById(storageCartLine.getId(), this.getStorageCartLine());
		if(searched != null){
			this.addProductFromStorageCart(storageCartLine);
		}else{
			this.getStorageCartLine().add(storageCartLine);
		}
		this.totalPrize = calculatePrize();
		
	}
	
	public void deleteItem(StorageCartLine storageCartLine){
		this.storageCartLine.remove(storageCartLine);
		this.totalPrize = calculatePrize();
	}
	
	public double calculatePrize(){
		double prize = 0.0;
		if(!this.getStorageCartLine().isEmpty()){
			for(StorageCartLine pAux : this.storageCartLine){
				prize += pAux.getPrize();
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
	
}
