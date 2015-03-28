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
		repository.save(new Product("Dell",Constants.INFORMATIC,"/image/1.jpg",
						"Ordenador Dell de última generación",125.33));
		repository.save(new Product("Minipimer Braun", Constants.LITTLE_APPLIANCE, "/image/2.jpg",
						"Minipimer Braun con la máxima potencia", 88.95));
		repository.save(new Product("LG", Constants.TELEVISION, "/image/3.jpg",
						"La mejor calidad de imagen con LG", 999.95));
		repository.save(new Product("PS4", Constants.VIDEOGAME, "/image/4.jpg", 
						"PS4 para vosotros jugadores", 399.95));
		
		//sacamos los productos
        Iterable<Product> products = repository.findAll();
        System.out.println("Products found with findAll():");
        System.out.println("-------------------------------");
        for (Product product : products) {
            System.out.println(product);
        }
        System.out.println();
        
        /*
         * Comprobación de las búsquedas en la BBDD
         */
        
        //findByName()
        Iterable<Product> productsFindByName = repository.findByName("PS4");
        System.out.println("Products found with findByName(\"PS4\"):");
        System.out.println("-------------------------------");
        for (Product product : productsFindByName) {
            System.out.println(product);
        }
        System.out.println();
        
        //findByCategory
        Iterable<Product> productsFindByCategory = repository.findByCategory(Constants.LITTLE_APPLIANCE);
        System.out.println("Products found with findByCategory(\"Pequeño electrodoméstico\"):");
        System.out.println("-------------------------------");
        for (Product product : productsFindByCategory) {
            System.out.println(product);
        }
        System.out.println();
        
        //findByPrize()
        Iterable<Product> productsFindByPrize= repository.findByPrize(100.0, 500.0);
        System.out.println("Products found with findByPrize(100.0, 500.0):");
        System.out.println("-------------------------------");
        for (Product product : productsFindByPrize) {
            System.out.println(product);
        }
        System.out.println();
        
        //setAlreadyExistingProduct()
        repository.setAlreadyExistingProduct(3L, "LG2", Constants.INFORMATIC, "/image/777.jpg",
				"Modificación de LG", 300);
        //LG modificado
        Iterable<Product> productsNew = repository.findAll();
        System.out.println("LG modified:");
        System.out.println("-------------------------------");
        for (Product product : productsNew) {
            System.out.println(product);
        }
        System.out.println();
        
        //Volvemos a modificar LG a los valores originales
        repository.setAlreadyExistingProduct(3L, "LG", Constants.TELEVISION, "/image/3.jpg",
				"La mejor calidad de imagen con LG", 999.95);
        
        //LG original
        Iterable<Product> productsFinal = repository.findAll();
        System.out.println("LG original:");
        System.out.println("-------------------------------");
        for (Product product : productsFinal) {
            System.out.println(product);
        }
        System.out.println();
		
	}

}
