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
	


	private List<String> imageTitles;
	
	@Autowired
	private ProductRepository productrepository;
	
	@Autowired
	private StorageCartRepository storageCartRepository;
	
	@Autowired
	private StorageCartLineRepository storageCartLineRepository;
	
	@Autowired
	private DataBaseController dataBaseContoller;
		
	/**
	 * The main ModelAndView method that shows the "mainTemplate.html" template, deletes the HttpSession attributes 
	 * "user" "password" and "admin" and, if the session is new, initializes the images list and returns the view with 
	 * "products" object passed to the view that contains all the products from the database.
	 * @param session
	 * @return
	 */
	@RequestMapping("/")
	public ModelAndView main(HttpSession session) {
		session.removeAttribute("user");
		session.removeAttribute("password");
		session.removeAttribute("admin");
		if (session.isNew()){
			imageTitles = new ArrayList<String>();
			imageTitles.add("/image/1.jpg");
			imageTitles.add("/image/2.jpg");
			imageTitles.add("/image/3.jpg");
			imageTitles.add("/image/4.jpg");
			imageTitles.add("/image/5.jpg");
			imageTitles.add("/image/6.jpg");

		}
				
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("products",
				productrepository.findAll());

		return mv;
	}
	
	/**
	 * The mainTemplateCategory ModelAndView method shows the "mainTemplate/{show}.html" 
	 * template, deletes the HttpSession attributes "user" "password" and "admin" and returns 
	 * the view with "products" object passed to the view that contains all the products from the database from the 
	 * selected option "show". 1=TELEVISION, 2=INFORMATIC, 3=VIDEOGAME, 4=LITTLE_APPLIANCE and all the products 
	 * by default.
	 * @param session
	 * @param show
	 * @return
	 */
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
	
	/**
	 * The mainTemplateSearch ModelAndView method shows the "mainTemplate/search.html" 
	 * template, deletes the HttpSession attributes "user" "password" and "admin" and returns 
	 * the view with "products" object passed to the view that contains all the products from the database from the 
	 * selected option "name" or "prizeMin" and "prizeMax". If "name" is passed as parameter, the method search by name
	 * and, if "prizeMin" and "prizeMax" is passed as parameter, the method search by prize.
	 * @param session
	 * @param name
	 * @param prizeMin
	 * @param prizeMax
	 * @return
	 */
	@RequestMapping("mainTemplate/search")
	public ModelAndView mainTemplateSearch(HttpSession session, String name, Double prizeMin, Double prizeMax){
		
		session.removeAttribute("user");
		session.removeAttribute("password");
		session.setAttribute("admin", false);
		
		ModelAndView mv = new ModelAndView("mainTemplate");
		if(name != "" && name != null){
			mv.addObject("products", productrepository.findByName(name));
		}else if(prizeMin != null && prizeMax != null){
			mv.addObject("products", productrepository.findByPrize(prizeMin, prizeMax));
		}else{
			mv.addObject("products", productrepository.findAll());
		}
		return mv;	
	}
	
	/**
	 * The newProduct ModelAndView method allows the application to move an image passed as parameter and place it
	 * in the "files" folder in the application in order to be able to show it properly in the template and stores a new product
	 * in the database.
	 * @param name
	 * @param file
	 * @param prize
	 * @param description
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/image/upload", method = RequestMethod.POST)
	public ModelAndView newProduct(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, @RequestParam("prize") String prize
			, @RequestParam("description") String description, @RequestParam("category") String category) {

		String fileName = imageTitles.size() + 1 + ".jpg";

		if (!file.isEmpty()) {
			try {

				File filesFolder = new File(Constants.FILES_FOLDER_IMAGES);
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
	
	/**
	 * The imageManage ModelAndView method allows the application to place the image 
	 * passed as parameter in the templates properly to be able to use it.
	 * @param fileName
	 * @param res
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@RequestMapping("image/{fileName}")
	public void imageManage(@PathVariable String fileName,
			HttpServletResponse res) throws FileNotFoundException, IOException {

		File file = new File(Constants.FILES_FOLDER_IMAGES, fileName+".jpg");

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
	
	/**
	 * The scriptManage ModelAndView method allows the application to place the javascript file 
	 * passed as parameter in the templates properly to be able to use it.
	 * @param fileName
	 * @param res
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@RequestMapping("scripts/{fileName}")
	public void scriptManage(@PathVariable String fileName,
			HttpServletResponse res) throws FileNotFoundException, IOException {

		File file = new File(Constants.FILES_FOLDER_SCRIPTS, fileName+ ".js");

		if (file.exists()) {
			res.setContentType("text/javascript");
			res.setContentLength(new Long(file.length()).intValue());
			FileCopyUtils
					.copy(new FileInputStream(file), res.getOutputStream());
		} else {
			res.sendError(404, "File" + fileName + "(" + file.getAbsolutePath()
					+ ") does not exist");
		}
	}
	
	/**
	 * The styleManage ModelAndView method allows the application to place the css file 
	 * passed as parameter in the templates properly to be able to use it. 
	 * @param fileName
	 * @param res
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@RequestMapping("styles/{fileName}")
	public void styleManage(@PathVariable String fileName,
			HttpServletResponse res) throws FileNotFoundException, IOException {

		File file = new File(Constants.FILES_FOLDER_STYLES, fileName+ ".css");
		
		if (file.exists()) {
			res.setContentType("text/css");
			res.setContentLength(new Long(file.length()).intValue());
			FileCopyUtils
					.copy(new FileInputStream(file), res.getOutputStream());
		} else {
			res.sendError(404, "File" + fileName + "(" + file.getAbsolutePath()
					+ ") does not exist");
		}
	}
	
	
	/**
	 * The add ModelAndView method allows the user to go to the add product option.
	 * @param session
	 * @return
	 */
	@RequestMapping("/addProduct")
	public ModelAndView add(HttpSession session){
		if (session.getAttribute("admin")!=null){
			if((boolean) session.getAttribute("admin")){
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
	
	/**
	 * The mainDelete ModelAndView method allows the user to go to the delete product option.
	 * @param session
	 * @return
	 */
	@RequestMapping("/deleteProduct")
	public ModelAndView mainDelete(HttpSession session) {
		if (session.getAttribute("admin")!=null){
			if((boolean) session.getAttribute("admin")){
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
	
	/**
	 * The delete ModelAndView method allows the user to delete the selected product found by category.
	 * @param session
	 * @param show
	 * @return
	 */
	@RequestMapping("deleteProduct/{show}")
	public ModelAndView delete(HttpSession session, @PathVariable Integer show){
		if((boolean) session.getAttribute("admin")){
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
	
	@RequestMapping("deleteProduct/search")
	public ModelAndView mainDeleteSearch(HttpSession session, String name, Double prizeMin, Double prizeMax){
		
		ModelAndView mv = new ModelAndView("deleteProduct");
		if(name != "" && name != null){
			mv.addObject("products", productrepository.findByName(name));
		}else if(prizeMin != null && prizeMax != null){
			mv.addObject("products", productrepository.findByPrize(prizeMin, prizeMax));
		}else{
			mv.addObject("products", productrepository.findAll());
		}
		return mv;	
	}
	
	/**
	 * The deleted ModelAndView method shows a message about the delete process and, if possible, deletes the image
	 * associated to the product that has been removed.
	 * @param session
	 * @param idProduct
	 * @return
	 */
	@RequestMapping("/deletedProduct")
	public ModelAndView deleted(HttpSession session, Long idProduct) {	
		if (session.getAttribute("admin")!=null){	
			if((boolean) session.getAttribute("admin")){
				Product product = productrepository.findById(idProduct);
				productrepository.delete(idProduct);
				try{
					File filesFolder = new File(Constants.FILES_FOLDER_IMAGES);
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
	
	/**
	 * The confirmationStorage ModelAndView method shows the storage cart list processed and pending of processing
	 * @param session
	 * @return
	 */
	@RequestMapping("confirmationStorage")
	public ModelAndView confirmationStorage(HttpSession session) {
		if((boolean) session.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("confirmationStorage").addObject("storageCartsFalse",
					storageCartRepository.findByProcessedStorageCart(false))
					.addObject("storageCartsTrue", storageCartRepository.findByProcessedStorageCart(true));
	
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	/**
	 * The confirmed ModelAndView method set a not processed storage cart to processed storage cart and stores it in the DDBB.
	 * @param session
	 * @param idStorageCartFalse
	 * @return
	 */
	@RequestMapping("/storageConfirmated")
	public ModelAndView confirmed(HttpSession session, @RequestParam long idStorageCartFalse) {	
		if((boolean) session.getAttribute("admin")){
			storageCartRepository.setAlreadyExistingStorageCart(idStorageCartFalse, true);
			ModelAndView mv = new ModelAndView("storageConfirmated").addObject("right",
					"The storage has been confirmated");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	/**
	 * The showStorageCart ModelAndView method shows the storage cart line list from a storage cart not formed yet.
	 * @param session
	 * @return
	 */
	@RequestMapping("/storageCart")
	public ModelAndView showStorageCart(HttpSession session){
		if(session.getAttribute("storageCart")==null){
			ModelAndView mv = new ModelAndView("storageCart").addObject("no hay productos pedidos");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("storageCart").addObject("products", ((StorageCart) session.getAttribute("storageCart")).getStorageCartLine())
														  .addObject("prize", ((StorageCart) session.getAttribute("storageCart")).getTotalPrize());
			return mv;
		}
	}
	
	/**
	 * The removeFromStorageCartLine ModelAndView method delete a product from a storage cart line list
	 * @param session
	 * @param delete
	 * @param idProduct
	 * @return
	 */
	@RequestMapping("/storageCart/{delete}")
	public ModelAndView removeFromStorageCartLine(HttpSession session, @PathVariable String delete, @RequestParam long idProduct){
		StorageCart storageCart = (StorageCart) session.getAttribute("storageCart");
		if(!storageCart.equals(null)){
			storageCart.deleteItem((storageCart.searchById(idProduct, storageCart.getStorageCartLine())));
		}
		session.setAttribute("storageCart", storageCart);
		
		return new ModelAndView("storageCart").addObject("products", ((StorageCart) session.getAttribute("storageCart")).getStorageCartLine())
				  .addObject("prize", ((StorageCart) session.getAttribute("storageCart")).getTotalPrize());
	}
	
	/**
	 * The show ModelAndView method shows the product selected template with its parameters and the option to store the number
	 * of products selected in the storage cart
	 * @param idProduct
	 * @return
	 */
	@RequestMapping("/showProduct")
	public ModelAndView show(Long idProduct) {
		
		ModelAndView mv = new ModelAndView("showProduct");
		if(idProduct!=null){
			Product product = productrepository.findOne(idProduct);
			mv.addObject("product", product);
		}
				
		return mv;
	}
	
	/**
	 * The confirm ModelAndView method allows the user to identify himself as administrator and redirect to the administration
	 * template.
	 * @param session
	 * @param user
	 * @param password
	 * @return
	 */
	@RequestMapping("/confirmationForm")
	public ModelAndView confirm(HttpSession session, String user, String password){
		return new ModelAndView("confirmationForm").addObject("user", user)
													.addObject("password", password);
	}
	
	/**
	 * The admin ModelAndView method allows the administrator to navigate between the different administration options.
	 * @param session
	 * @param user
	 * @param password
	 * @return
	 */
	@RequestMapping("/adminTemplate")
	public ModelAndView admin(HttpSession session, @RequestParam String user, @RequestParam String password){
		if ((user.equals(Constants.ADMIN))&&(password.equals(Constants.PASSWORD))){
			session.setAttribute("user",user);
			session.setAttribute("password",password);
			session.setAttribute("admin", true);
			return new ModelAndView("adminTemplate");
		}else{
			return new ModelAndView("confirmationForm").addObject("error",true);
		}
	}
	
	/**
	 * The back ModelAndView method allows the administrator to go back to the adminTemplate.html
	 * @param session
	 * @return
	 */
	@RequestMapping("/adminBack")
	public ModelAndView back(HttpSession session){
		if ((session.getAttribute("user").equals(Constants.ADMIN))&&(session.getAttribute("password").equals(Constants.PASSWORD))){
			session.setAttribute("admin", true);
			return new ModelAndView("adminTemplate");
		}else{
			return new ModelAndView("confirmationForm").addObject("error",true);
		}
	}
	
	/**
	 * The modified ModelAndView method allows the administrator to navigate to "modifyProduct.html" template.
	 * @param session
	 * @return
	 */
	@RequestMapping("/modifyProduct")
	public ModelAndView modified(HttpSession session) {	
		if((boolean) session.getAttribute("admin")){
			ModelAndView mv = new ModelAndView("modifyProduct");
			return mv;
		}else{
			ModelAndView mv = new ModelAndView("notAdmin");
			return mv;
		}
	}
	
	/**
	 * The mainModified method allows to the administrator to navigate between the different product categories 
	 * in order to modify any product from the selected category.
	 * @param session
	 * @param show
	 * @return
	 */
	@RequestMapping("modifyProduct/{show}")
	public ModelAndView mainModified(HttpSession session, @PathVariable Integer show){
		if((boolean) session.getAttribute("admin")){
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
	
	@RequestMapping("modifyProduct/search")
	public ModelAndView mainModifySearch(HttpSession session, String name, Double prizeMin, Double prizeMax){
		
		ModelAndView mv = new ModelAndView("modifyProduct");
		if(name != "" && name != null){
			mv.addObject("products", productrepository.findByName(name));
		}else if(prizeMin != null && prizeMax != null){
			mv.addObject("products", productrepository.findByPrize(prizeMin, prizeMax));
		}else{
			mv.addObject("products", productrepository.findAll());
		}
		return mv;	
	}
	
	/**
	 * The modified method modify a product and stores it in the database
	 * @param session
	 * @param name
	 * @param file
	 * @param prize
	 * @param id
	 * @param description
	 * @param category
	 * @return
	 */
	@RequestMapping("/modifiedProduct")
	public ModelAndView modified(HttpSession session, @RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, @RequestParam String prize
			,@RequestParam long id, @RequestParam String description, @RequestParam String category){
		if((boolean) session.getAttribute("admin")){
			Product myProduct = productrepository.findById(id);
			int i = Integer.parseUnsignedInt(String.valueOf(id));
			String fileName = imageTitles.get(i - 1).substring(7);
			if (!file.isEmpty()) {
				try {
					File filesFolder = new File(Constants.FILES_FOLDER_IMAGES);
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
	
	/**
	 * The addToStorageCart method add to the storage cart line the element and the number of elements passed as parameter
	 * @param session
	 * @param idProduct
	 * @param numElements
	 * @return
	 */
	@RequestMapping("/addToStorageCartConfirmation")
	public ModelAndView addToStorageCart(HttpSession session,@RequestParam long idProduct, @RequestParam int numElements){
		
		Product product = productrepository.findOne(idProduct);
		StorageCartLine newStCrt = new StorageCartLine(product, numElements);
		
		if(session.getAttribute("storageCart")!=null){
			StorageCart list = ((StorageCart) session.getAttribute("storageCart"));
			list.addItem(newStCrt);
			session.setAttribute("storageCart", list);
		}else{
			StorageCart list2 = new StorageCart();
			list2.addItem(newStCrt);
			session.setAttribute("storageCart", list2);			
		}				
		return new ModelAndView("showProduct").addObject("added", true);		
	}
	
	/**
	 * The buyConfirmation method shows the list of products in the storage cart and the form that the user has to fill up with
	 * his name and last name to continue with the buy and order the confirmation.
	 * @param session
	 * @return
	 */
	@RequestMapping("/buyConfirmation")
	public ModelAndView buyConfirmation(HttpSession session){
		
		
		ModelAndView mv = new ModelAndView("buyConfirmation").addObject("products",
				((StorageCart) session.getAttribute("storageCart")).getStorageCartLine());
		
		return mv;
	}
	
	/**
	 * The createStorageCart method shows the user that the order has been completed successfully and deletes the storage cart
	 * line in order to let the user order more products in a new storage cart.
	 * @param session
	 * @param name
	 * @param lastName
	 * @return
	 */
	@RequestMapping("/createStorageCart")
	public ModelAndView createStorageCart(HttpSession session, @RequestParam String name, @RequestParam String lastName){
		
		((StorageCart) session.getAttribute("storageCart")).setName(name);
		((StorageCart)session.getAttribute("storageCart")).setLastName(lastName);
		
		this.storageCartRepository.save(new StorageCart((StorageCart) session.getAttribute("storageCart")));
		
		((StorageCart)session.getAttribute("storageCart")).getStorageCartLine().removeAll(((StorageCart)session.getAttribute("storageCart")).getStorageCartLine());
		((StorageCart)session.getAttribute("storageCart")).setTotalPrize(((StorageCart)session.getAttribute("storageCart")).calculatePrize(((StorageCart)session.getAttribute("storageCart")).getStorageCartLine()));
		
		return new ModelAndView("createStorageCart");
	}
	
}
