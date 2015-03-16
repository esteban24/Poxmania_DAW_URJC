package es.sidelab.poxmania;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.sidelab.poxmania.ProductRepository;
import es.sidelab.poxmania.DataBaseController;

@Controller
public class WebController {
	
	@Autowired
	private ProductRepository productrepository;
	
	@Autowired
	private StorageCartRepository storageCartRepository;
	
	@Autowired
	private DataBaseController dataBaseContoller;
	
	private StorageCart userStorageCart;
	
	@RequestMapping("/")
	public ModelAndView main(HttpSession sesion) {
		//TODO emartin: metodo de ModelAndView
		
		if (sesion.isNew()){
			userStorageCart = new StorageCart();
		}
				
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("products",
				productrepository.findAll());

		return mv;
	}
	
	@RequestMapping("/storageCart")
	public ModelAndView showStorageCart(HttpSession sesion){
		
		ModelAndView mv = new ModelAndView("storageCart").addObject("products", userStorageCart.getProductsList())
														  .addObject("prize", userStorageCart.getTotalPrize());
		return mv;
	}
	
	@RequestMapping("/showProduct")
	public ModelAndView mostrar(@RequestParam long idProduct) {

		Product product = productrepository.findOne(idProduct);
				
		return new ModelAndView("showProduct").addObject("product", product);
	}
	
	@RequestMapping("/confirmationForm")
	public ModelAndView admin(String user, String password){
		
		return new ModelAndView("confirmationForm").addObject("user", user)
													.addObject("password", password);
	}
	
	@RequestMapping("/addToStorageCartConfirmation")
	public ModelAndView addToStorageCart(@RequestParam long idProduct){
		
		Product product = productrepository.findOne(idProduct);
		
		this.userStorageCart.addItem(product);
		
		return new ModelAndView("addToStorageCartConfirmation");
		
	}
}
