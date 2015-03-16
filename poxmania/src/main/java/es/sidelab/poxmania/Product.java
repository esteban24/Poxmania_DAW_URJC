package es.sidelab.poxmania;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String name;
	private String category;
	private String image;
	private String description;
	private double prize;
	
	public Product() {
	}

	public Product(String name, String category, String image, String description, double prize) {
		this.name = name;
		this.category = category;
		this.image = image;
		this.description = description;
		this.prize = prize;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrize() {
		return prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	@Override
	public String toString(){
		return "[ "+this.name+", "+this.category+", "+this.image+", "+this.description+", "
				   +String.valueOf(this.prize)+" ]";
		
	}
	
	

}
