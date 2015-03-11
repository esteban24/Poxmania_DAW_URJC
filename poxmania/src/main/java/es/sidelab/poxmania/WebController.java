package es.sidelab.poxmania;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WebController {
	
	@RequestMapping("/")
	public ModelAndView tablon(HttpSession sesion) {
		//TODO emartin: metodo de ModelAndView
/*
		ModelAndView mv = new ModelAndView("index").addObject("productos",
				repository.findAll());

		if (sesion.isNew()) {
			mv.addObject("saludo", "Bienvenido!!");
		}

		return mv;*/
		
		return null;
	}
}
