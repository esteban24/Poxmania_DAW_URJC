package es.sidelab.poxmania;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Controller;

import es.sidelab.poxmania.Product;
import es.sidelab.poxmania.ProductRepository;

@Controller
public class DataBaseController implements CommandLineRunner {
	
	@Autowired
	private ProductRepository repository;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		//guardando un artículo de cada tipo de productos
		repository.save(new Product("Dell",Constants.INFORMATIC,"/image/0.jpg",
						"Ordenador Dell de última generación",125.33));
		repository.save(new Product("Minipimer Braun", Constants.LITTLE_APPLIANCE, "/image/1.jpg",
						"Minipimer Braun con la máxima potencia", 88.95));
		repository.save(new Product("LG", Constants.TELEVISION, "/image/2.jpg",
						"La mejor calidad de imagen con LG", 999.95));
		repository.save(new Product("PS4", Constants.VIDEOGAME, "/image/3.jpg", 
						"PS4 para vosotros jugadores", 399.95));
		
		//sacamos los productos
        Iterable<Product> products = repository.findAll();
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println();
        
        
		
	}

}
