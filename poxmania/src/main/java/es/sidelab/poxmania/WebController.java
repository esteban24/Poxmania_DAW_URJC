package es.sidelab.poxmania;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {
	
	@RequestMapping("/")
	public ModelAndView mainPage(HttpSession sesion){
		//TODO emartin: Crear el método de controlador para la página principal
		return null;
	}
}
