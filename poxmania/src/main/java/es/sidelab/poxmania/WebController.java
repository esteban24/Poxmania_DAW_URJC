package es.sidelab.poxmania;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import es.sidelab.poxmania.ProductRepository;
import es.sidelab.poxmania.StorageCartRepository;
import es.sidelab.poxmania.StorageCartLineRepository;
import es.sidelab.poxmania.DataBaseController;

@Controller
public class WebController {
	
	private static final String FILES_FOLDER = "files";

	private List<String> imageTitles;
	
	@Autowired
	private ProductRepository productrepository;
	
	@Autowired
	private StorageCartRepository storageCartRepository;
	
	@Autowired
	private StorageCartLineRepository storageCartLineRepository;
	
	@Autowired
	private DataBaseController dataBaseContoller;
	
	//private StorageCart userStorageCart;
	
	
	@RequestMapping("/")
	public ModelAndView main(HttpSession sesion) {
		sesion.removeAttribute("user");
		sesion.removeAttribute("password");
		sesion.removeAttribute("admin");
		if (sesion.isNew()){
			//userStorageCart = new StorageCart();
			imageTitles = new ArrayList<String>();
			imageTitles.add("/image/1.jpg");
			imageTitles.add("/image/2.jpg");
			imageTitles.add("/image/3.jpg");
			imageTitles.add("/image/4.jpg");
		}
				
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("products",
				productrepository.findAll());

		return mv;
	}
	
	
	@RequestMapping("mainTemplate/{show}")
	public ModelAndView mainTemplateCategory(HttpSession session, @PathVariable Integer show){
		
		ModelAndView mv = new ModelAndView("mainTemplate");
		session.removeAttribute("user");
		session.removeAttribute("password");
		session.setAttribute("admin", false);
		
		switch(show){
		case 1: mv.addObject("products", productrepository.findByCategory(Constants.TELEVISION));
				break;
		case 2: mv.addObject("products", productrepository.findByCategory(Constants.INFORMATIC));
				break;
		case 3: mv.addObject("products", productrepository.findByCategory(Constants.VIDEOGAME));
				break;
		case 4: mv.addObject("products", productrepository.findByCategory(Constants.LITTLE_APPLIANCE));	
				break;
		default: mv.addObject("products", productrepository.findAll());
				 break;
		}
		return mv;
	}
	
	@RequestMapping("mainTemplate/search")
	public ModelAndView mainTemplateTelevision(HttpSession session, @RequestParam String name, @RequestParam Double prizeMin,
												@RequestParam Double prizeMax){
		
		ModelAndView mv = new ModelAndView("mainTemplate");
		if(name != null){
			mv.addObject("products", productrepository.findByName(name));
		}else{
			mv.addObject("products", productrepository.findByPrize(prizeMin, prizeMax));
		}
		return mv;
	
	}
	
	@RequestMapping(value = "/image/upload", method = RequestMethod.POST)
	public ModelAndView handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, @RequestParam("prize") String prize
			, @RequestParam("description") String description, @RequestParam("category") String category) {

		String fileName = imageTitles.size() + 1 + ".jpg";

		if (!file.isEmpty()) {
			try {

				File filesFolder = new File(FILES_FOLDER);
				if (!filesFolder.exists()) {
					filesFolder.mkdirs();
				}

				File uploadedFile = new File(filesFolder.getAbsolutePath(), fileName);
				file.transferTo(uploadedFile);

				if ((name.equals("")||(prize=="")||(description=="")||(category==""))){
					return new ModelAndView("addProduct").addObject("error",true);
				}else{
					try{
						double mydouble = Double.parseDouble(prize); 
						Product product = new Product(name,category,"/image/"+fileName,description,mydouble);
						imageTitles.add(product.getImage());
						productrepository.save(product);
						ModelAndView mv = new ModelAndView("addProduct").addObject("right",true);
						return mv;
					}catch(Exception e){
						return new ModelAndView("addProduct").addObject("error",true);
					}
				}
				

			} catch (Exception e) {
				return new ModelAndView("mainTemplate").addObject("fileName",
						fileName).addObject("error",
						e.getClass().getName() + ":" + e.getMessage());
			}
		} else {
			return new ModelAndView("addProduct").addObject("error",
					"The file is empty");
		}
	}
	
	@RequestMapping("image/{fileName}")
	public void handleFileDownload(@PathVariable String fileName,
			HttpServletResponse res) throws FileNotFoundException, IOException {

		File file = new File(FILES_FOLDER, fileName+".jpg");

		if (file.exists()) {
			res.setContentType("image/jpeg");
			res.setContentLength(new Long(file.length()).intValue());
			FileCopyUtils
					.copy(new FileInputStream(file), res.getOutputStream());
		} else {
			res.sendError(404, "File" + fileName + "(" + file.getAbsolutePath()
					+ ") does not exist");
		}
	}
	
	@RequestMapping("/addProduct")
	public ModelAndView add(HttpSession sesion){
		if (sesion.getAttribute("admin")!=null){
			if((boolean) sesion.getAttribute("admin")){
				ModelAndView mv = new ModelAndView("addProduct");
				return mv;
			}else{
				ModelAndView mv = new ModelAndView("notAdmin");
				return mv;
			}
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("/deleteProduct")
	public ModelAndView mainDelete(HttpSession sesion) {
		if (sesion.getAttribute("admin")!=null){
			if((boolean) sesion.getAttribute("admin")){
				ModelAndView mv = new ModelAndView("deleteProduct");
				return mv;
			}else{
				ModelAndView mv = new ModelAndView("notAdmin");
				return mv;
			}
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("deleteProduct/{show}")
	public ModelAndView delete(HttpSession sesion, @PathVariable Integer show){
		if((boolean) sesion.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("deleteProduct");
			
			switch(show){
			case 1: mv.addObject("products", productrepository.findByCategory(Constants.TELEVISION));
					break;
			case 2: mv.addObject("products", productrepository.findByCategory(Constants.INFORMATIC));
					break;
			case 3: mv.addObject("products", productrepository.findByCategory(Constants.VIDEOGAME));
					break;
			case 4: mv.addObject("products", productrepository.findByCategory(Constants.LITTLE_APPLIANCE));	
					break;
			default: mv.addObject("products", productrepository.findAll());
					 break;
			}
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("/deletedProduct")
	public ModelAndView deleted(HttpSession sesion, @RequestParam long idProduct) {	
		if (sesion.getAttribute("admin")!=null){	
			if((boolean) sesion.getAttribute("admin")){
				Product product = productrepository.findById(idProduct);
				productrepository.delete(idProduct);
				try{
					File filesFolder = new File(FILES_FOLDER);
					if (!filesFolder.exists()) {
						filesFolder.mkdirs();
					}
					String str = product.getImage().substring(6);
					File deleteFile = new File(filesFolder.getAbsolutePath()+"/", str);
					deleteFile.delete();
					imageTitles.remove(product.getName());
					
					System.out.println();
				}catch(Exception e){
					e.getMessage();
				}
				ModelAndView mv = new ModelAndView("deletedProduct").addObject("right",
						"The product has been deleted");
				return mv;
			}else{
				ModelAndView mv = new ModelAndView("notAdmin");
				return mv;
			}
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("confirmationStorage")
	public ModelAndView confirmationStorage(HttpSession sesion) {
		if((boolean) sesion.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("confirmationStorage").addObject("storageCartsFalse",
					storageCartRepository.findByProcessedStorageCart(false))
					.addObject("storageCartsTrue", storageCartRepository.findByProcessedStorageCart(true));
	
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("/storageConfirmated")
	public ModelAndView confirmated(HttpSession sesion, @RequestParam long idStorageCartFalse) {	
		if((boolean) sesion.getAttribute("admin")){
			storageCartRepository.setAlreadyExistingStorageCart(idStorageCartFalse, true);
			//storageCartRepository.delete(idStorageCart);
			ModelAndView mv = new ModelAndView("storageConfirmated").addObject("right",
					"The storage has been confirmated");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
		

	
	@RequestMapping("/storageCart")
	public ModelAndView showStorageCart(HttpSession sesion){
		if(sesion.getAttribute("carro")==null){
			ModelAndView mv = new ModelAndView("storageCart").addObject("no hay productos pedidos");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("storageCart").addObject("products", ((StorageCart) sesion.getAttribute("carro")).getStorageCartLine())
														  .addObject("prize", ((StorageCart) sesion.getAttribute("carro")).getTotalPrize());
			return mv;
		}
	}
	
	@RequestMapping("/storageCart/{delete}")
	public ModelAndView removeFromStorageCartLine(HttpSession session, @PathVariable String delete, @RequestParam long idProduct){
		StorageCart storageCart = (StorageCart) session.getAttribute("carro");
		if(!storageCart.equals(null)){
			storageCart.deleteItem((storageCart.searchById(idProduct, storageCart.getStorageCartLine())));
		}
		session.setAttribute("carro", storageCart);
		
		return new ModelAndView("storageCart").addObject("products", ((StorageCart) session.getAttribute("carro")).getStorageCartLine())
				  .addObject("prize", ((StorageCart) session.getAttribute("carro")).getTotalPrize());
	}
	
	@RequestMapping("/showProduct")
	public ModelAndView mostrar(@RequestParam long idProduct) {

		Product product = productrepository.findOne(idProduct);
				
		return new ModelAndView("showProduct").addObject("product", product);
	}
	
	@RequestMapping("/confirmationForm")
	public ModelAndView admin(HttpSession sesion, String user, String password){
		return new ModelAndView("confirmationForm").addObject("user", user)
													.addObject("password", password);
	}
	
	@RequestMapping("/adminTemplate")
	public ModelAndView confirm(HttpSession sesion, @RequestParam String user, @RequestParam String password){
		if ((user.equals(Constants.ADMIN))&&(password.equals(Constants.PASSWORD))){
			sesion.setAttribute("user",user);
			sesion.setAttribute("password",password);
			sesion.setAttribute("admin", true);
			return new ModelAndView("adminTemplate");
		}else{
			return new ModelAndView("confirmationForm").addObject("error",true);
		}
	}
	
	@RequestMapping("/adminBack")
	public ModelAndView back(HttpSession sesion){
		if ((sesion.getAttribute("user").equals(Constants.ADMIN))&&(sesion.getAttribute("password").equals(Constants.PASSWORD))){
			sesion.setAttribute("admin", true);
			return new ModelAndView("adminTemplate");
		}else{
			return new ModelAndView("confirmationForm").addObject("error",true);
		}
	}
	
	@RequestMapping("/modifyProduct")
	public ModelAndView modified(HttpSession sesion) {	
		if((boolean) sesion.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("modifyProduct");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("modifyProduct/{show}")
	public ModelAndView mainModified(HttpSession sesion, @PathVariable Integer show){
		if((boolean) sesion.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("modifyProduct");
			
			switch(show){
			case 1: mv.addObject("products", productrepository.findByCategory(Constants.TELEVISION));
					break;
			case 2: mv.addObject("products", productrepository.findByCategory(Constants.INFORMATIC));
					break;
			case 3: mv.addObject("products", productrepository.findByCategory(Constants.VIDEOGAME));
					break;
			case 4: mv.addObject("products", productrepository.findByCategory(Constants.LITTLE_APPLIANCE));	
					break;
			}
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("/modifiedProduct")
	public ModelAndView modified(HttpSession sesion, @RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, @RequestParam String prize
			,@RequestParam long id, @RequestParam String description, @RequestParam String category){
		if((boolean) sesion.getAttribute("admin")){
			Product myProduct = productrepository.findById(id);
			int i = Integer.parseUnsignedInt(String.valueOf(id));
			String fileName = imageTitles.get(i - 1).substring(7);
			if (!file.isEmpty()) {
				try {
					File filesFolder = new File(FILES_FOLDER);
					if (!filesFolder.exists()) {
						filesFolder.mkdirs();
					}
					File uploadedFile = new File(filesFolder.getAbsolutePath()+"/", fileName);
					file.transferTo(uploadedFile);
					imageTitles.add("/image/"+fileName);
					if ((name.equals("")||(prize=="")||(description=="")||(category==""))){
						return new ModelAndView("modifiedProduct").addObject("error",true);
					}else{
						try{
							imageTitles.add(fileName);
							double mydouble = Double.parseDouble(prize); 
							myProduct.setName(name);
							myProduct.setDescription(description);
							myProduct.setCategory(category);
							myProduct.setPrize(mydouble);
							productrepository.setAlreadyExistingProduct(id, myProduct.getName(), myProduct.getCategory(), "/image/"+fileName, myProduct.getDescription(), myProduct.getPrize());
							ModelAndView mv = new ModelAndView("modifiedProduct").addObject("right",true);
							return mv;
						}catch(Exception e){
							return new ModelAndView("modifiedProduct").addObject("error",true);
						}
					}				
				} catch (Exception e) {
					return new ModelAndView("mainTemplate").addObject("fileName",
							fileName).addObject("error",
							e.getClass().getName() + ":" + e.getMessage());
				}
			} else {
				return new ModelAndView("modifiedProduct").addObject("error",
						"The file is empty");
			}
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	@RequestMapping("/addToStorageCartConfirmation")
	public ModelAndView addToStorageCart(HttpSession sesion,@RequestParam long idProduct, @RequestParam int numElements){
		
		Product product = productrepository.findOne(idProduct);
		StorageCartLine newStCrt = new StorageCartLine(product, numElements);
		
		if(sesion.getAttribute("carro")!=null){
			StorageCart list = ((StorageCart) sesion.getAttribute("carro"));
			list.addItem(newStCrt);
			sesion.setAttribute("carro", list);
		}else{
			StorageCart list2 = new StorageCart();
			list2.addItem(newStCrt);
			sesion.setAttribute("carro", list2);			
		}				
		return new ModelAndView("addToStorageCartConfirmation");		
	}
	
	@RequestMapping("/buyConfirmation")
	public ModelAndView buyConfirmation(HttpSession sesion){
		
		
		ModelAndView mv = new ModelAndView("buyConfirmation").addObject("products",
				((StorageCart) sesion.getAttribute("carro")).getStorageCartLine());
		
		return mv;
	}
	
	@RequestMapping("/createStorageCart")
	public ModelAndView createStorageCart(HttpSession sesion, @RequestParam String name, @RequestParam String lastName){
		
		((StorageCart) sesion.getAttribute("carro")).setName(name);
		((StorageCart)sesion.getAttribute("carro")).setLastName(lastName);
		
		this.storageCartRepository.save(new StorageCart((StorageCart) sesion.getAttribute("carro")));
		
		//----------------------------------------------------------
		//Comprobación de que hemos guardado con éxito en la BBDD
		Iterable<StorageCart> storageCartList = this.storageCartRepository.findAll();
        System.out.println("Storage Cart found with findAll():");
        System.out.println("-------------------------------");
        for (StorageCart storageCart : storageCartList) {
            System.out.println(storageCart.toString());
        }
        System.out.println();
        //----------------------------------------------------------
		
		((StorageCart)sesion.getAttribute("carro")).getStorageCartLine().removeAll(((StorageCart)sesion.getAttribute("carro")).getStorageCartLine());
		((StorageCart)sesion.getAttribute("carro")).setTotalPrize(((StorageCart)sesion.getAttribute("carro")).calculatePrize(((StorageCart)sesion.getAttribute("carro")).getStorageCartLine()));
		
		return new ModelAndView("createStorageCart");
	}
	
}
