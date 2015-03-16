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
	private ProductRepository repository;
	
	@Autowired
	private DataBaseController dataBaseContoller;
	
	@RequestMapping("/")
	public ModelAndView main(HttpSession sesion) {
		//TODO emartin: metodo de ModelAndView
				
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("products",
				repository.findAll());

		return mv;
	}
	
	@RequestMapping("/showProduct")
	public ModelAndView mostrar(@RequestParam long idProduct) {

		Product product = repository.findOne(idProduct);

		return new ModelAndView("showProduct").addObject("product", product);
	}
	
	@RequestMapping("/adminTemplate")
	public ModelAndView newAdmin(HttpSession sesion) {

		ModelAndView mv1 = new ModelAndView("adminTemplate");
		
		return mv1;
	}
}
