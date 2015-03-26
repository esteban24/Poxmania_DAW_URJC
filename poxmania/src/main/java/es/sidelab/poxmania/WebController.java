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
	
	private StorageCart userStorageCart;
	
	
	@RequestMapping("/")
	public ModelAndView main(HttpSession sesion) {
		
		if (sesion.isNew()){
			userStorageCart = new StorageCart();
			imageTitles = new ArrayList<String>();
			imageTitles.add("/images/0.jpg");
			imageTitles.add("/images/1.jpg");
			imageTitles.add("/images/2.jpg");
			imageTitles.add("/images/3.jpg");
			imageTitles.add("/images/4.jpg");
		}
				
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("products",
				productrepository.findAll());

		return mv;
	}
	
	@RequestMapping(value = "/image/upload", method = RequestMethod.POST)
	public ModelAndView handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("file") MultipartFile file, @RequestParam("prize") String prize
			, @RequestParam("description") String description, @RequestParam("category") String category) {

		String fileName = imageTitles.size() + ".jpg";

		if (!file.isEmpty()) {
			try {

				File filesFolder = new File(FILES_FOLDER);
				if (!filesFolder.exists()) {
					filesFolder.mkdirs();
				}

				File uploadedFile = new File(filesFolder.getAbsolutePath(), fileName);
				file.transferTo(uploadedFile);

				imageTitles.add(name);
				if ((name.equals("")||(prize=="")||(description=="")||(category==""))){
					return new ModelAndView("addProduct").addObject("error",true);
				}else{
					try{
						double mydouble = Double.parseDouble(prize); 
						Product product = new Product(name,category,"image/"+fileName,description,mydouble);
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
	
	@RequestMapping("/image/{fileName}")
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
		ModelAndView mv = new ModelAndView("addProduct");
		return mv;
	}
	
	/*@RequestMapping("/addedProduct")
	public ModelAndView adds(@RequestParam String image, @RequestParam String name, @RequestParam String prize
			, @RequestParam String description, @RequestParam String category){
		if ((image.equals(""))||(name.equals("")||(prize=="")||(description=="")||(category==""))){
			return new ModelAndView("addedProduct").addObject("error",true);
		}else{
			try{
				double mydouble = Double.parseDouble(prize); 
				Product product = new Product(name,category,image,description,mydouble);
				productrepository.save(product);
				ModelAndView mv = new ModelAndView("addedProduct").addObject("right",true);
				return mv;
			}catch(Exception e){
				return new ModelAndView("addedProduct").addObject("error",true);
			}
		}
	}*/
	
	@RequestMapping("/storageCart")
	public ModelAndView showStorageCart(HttpSession sesion){
		
		ModelAndView mv = new ModelAndView("storageCart").addObject("products", this.userStorageCart.getStorageCartLine())
														  .addObject("prize", this.userStorageCart.getTotalPrize());
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
	public ModelAndView addToStorageCart(@RequestParam long idProduct, @RequestParam int numElements){
		
		Product product = productrepository.findOne(idProduct);
		
		StorageCartLine newStCrt = new StorageCartLine(product, numElements);
		
		this.userStorageCart.addItem(newStCrt);
				
		return new ModelAndView("addToStorageCartConfirmation");
		
	}
	
	@RequestMapping("/buyConfirmation")
	public ModelAndView buyConfirmation(HttpSession sesion){
		
		
		ModelAndView mv = new ModelAndView("buyConfirmation").addObject("products",
				this.userStorageCart.getStorageCartLine());
		
		return mv;
	}
	
	@RequestMapping("/createStorageCart")
	public ModelAndView createStorageCart(@RequestParam String name, @RequestParam String lastName){
		
		this.userStorageCart.setName(name);
		this.userStorageCart.setLastName(lastName);
		
		this.storageCartRepository.save(new StorageCart(this.userStorageCart));
		
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
		
		this.userStorageCart.getStorageCartLine().removeAll(this.userStorageCart.getStorageCartLine());
		this.userStorageCart.setTotalPrize(this.userStorageCart.calculatePrize(this.userStorageCart.getStorageCartLine()));
		
		return new ModelAndView("createStorageCart");
	}
	
}
