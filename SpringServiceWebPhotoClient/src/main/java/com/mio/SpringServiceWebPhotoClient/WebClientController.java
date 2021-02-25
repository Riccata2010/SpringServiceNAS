package com.mio.SpringServiceWebPhotoClient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WebClientController {

	@GetMapping("/ss-nas-photo-client")
	public ModelAndView passParametersWithModelAndView() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
}
