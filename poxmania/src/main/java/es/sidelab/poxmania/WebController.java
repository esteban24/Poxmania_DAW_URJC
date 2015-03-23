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
	
	@RequestMapping("/adminTemplate")
	public ModelAndView confirm(@RequestParam String user, @RequestParam String password){
		if ((user.equals(Constants.ADMIN))&&(password.equals(Constants.PASSWORD))){
			return new ModelAndView("adminTemplate").addObject("user",user)
														.addObject("password",password);
		}else{
			return new ModelAndView("confirmationForm").addObject("error",true);
			/*return new ModelAndView("/mainTemplate").addObject("products",
					productrepository.findAll());*/
		}
	}
	
	@RequestMapping("/modifyProduct")
	public ModelAndView modified(HttpSession sesion) {						
		ModelAndView mv = new ModelAndView("modifyProduct").addObject("products",
				productrepository.findAll());
		return mv;
	}
	
	@RequestMapping("/modifyProduct")
	public ModelAndView modified(@RequestParam String image, @RequestParam String name, @RequestParam double prize
			,@RequestParam long idProduct, @RequestParam String description, @RequestParam String category){
		if ((image.equals(""))||(name.equals("")||(prize==0)||(description=="")||(category==""))){
			return new ModelAndView("modifyPruduct").addObject("error",true);
		}else{
			Product product = productrepository.findOne(idProduct);
			product.setPrize(prize);
			product.setDescription(description);
			product.setName(name);
			product.setCategory(category);
			//productrepository.delete(product);
			//Product newproduct = new Product(name,Constants.INFORMATIC,image,
					//"Ordenador Dell de última generación",prize);
			//newproduct.setId(idOld);
			productrepository.save(product);
			return new ModelAndView("modifyPruduct").addObject("ha ido bien");
		}
	}
	
	@RequestMapping("/addToStorageCartConfirmation")
	public ModelAndView addToStorageCart(@RequestParam long idProduct){
		
		Product product = productrepository.findOne(idProduct);
		
		this.userStorageCart.addItem(product);
		
		return new ModelAndView("addToStorageCartConfirmation");
		
	}
	
	@RequestMapping("/buyConfirmation")
	public ModelAndView buyConfirmation(HttpSession sesion){
		
		
		ModelAndView mv = new ModelAndView("buyConfirmation").addObject("products",
				this.userStorageCart.getProductsList());
		
		return mv;
	}
	
	@RequestMapping("/createStorageCart")
	public ModelAndView createStorageCart(@RequestParam String name, @RequestParam String lastName){
		
		this.userStorageCart.setName(name);
		this.userStorageCart.setLastName(lastName);
		
		this.storageCartRepository.save(this.userStorageCart);
		
		this.userStorageCart.getProductsList().removeAll(this.userStorageCart.getProductsList());
		this.userStorageCart.setTotalPrize(this.userStorageCart.calculatePrize());
		
		return new ModelAndView("createStorageCart");
	}
}
