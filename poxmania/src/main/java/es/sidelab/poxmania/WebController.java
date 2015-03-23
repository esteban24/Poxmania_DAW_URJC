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
	
	@RequestMapping("/modifiedProduct")
	public ModelAndView modified(@RequestParam String image, @RequestParam String name, @RequestParam String prize
			,@RequestParam long id, @RequestParam String description, @RequestParam String category){
		if ((image.equals(""))||(name.equals("")||(prize=="")||(description=="")||(category==""))){
			return new ModelAndView("modifiedProduct").addObject("error",true);
		}else{
			try{
				double mydouble = Double.parseDouble(prize); 
				Product product = productrepository.findOne(id);
				product.setPrize(mydouble);
				product.setDescription(description);
				product.setName(name);
				product.setCategory(category);
				productrepository.save(product);
				return new ModelAndView("modifiedProduct").addObject("right",true);
			}catch(Exception e){
				return new ModelAndView("modifiedProduct").addObject("errorExecution",true);
			}
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
