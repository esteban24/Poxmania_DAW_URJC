package es.sidelab.poxmania;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.sidelab.poxmania.ProductRepository;

@Controller
public class WebController {
	
	@Autowired
	private ProductRepository repository;
	
	@RequestMapping("/")
	public ModelAndView main(HttpSession sesion) {
		//TODO emartin: metodo de ModelAndView
		
		Product p = new Product("lg", "television", "C:\\Users\\Jorge\\Documents\\BROMA\\Desktop.tv.jpg", "blabla", 123);

		repository.save(p);
		
		ModelAndView mv = new ModelAndView("mainTemplate").addObject("productos",
				repository.findAll());

		return mv;
	}
}
