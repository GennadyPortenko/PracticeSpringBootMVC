package gpk.practice.spring.bootmvc.controller;

import gpk.practice.spring.bootmvc.model.User;
import gpk.practice.spring.bootmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }

    @RequestMapping(value="/registered", method = RequestMethod.POST)
    public ModelAndView register(User user, ModelMap modelMap) {
        try {
            User userFoundByLogin = userService.findByLogin(user.getLogin());
            User userFoundByEmail = userService.findByEmail(user.getEmail());
            if ( userFoundByLogin != null ) {
                modelMap.put("registered_f", false);
                modelMap.put("error_msg", "Пользователь с таким логином уже существует");
            } else if (userFoundByEmail != null ) {
                modelMap.put("registered_f", false);
                modelMap.put("error_msg", "Пользователь с таким email уже существует");
            } else {
                modelMap.put("registered_f", true);
                userService.saveUser(user);
            }
        } catch (Exception /* NonUniqueResultException */ e) {
            modelMap.put("registered_f", false);
            modelMap.put("error_msg", "Ошибка регистрации. Попробуйте другую комбинацию логина и пароля");
            return new ModelAndView("registered");
        }
        return new ModelAndView("registered");
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public ModelAndView logIn() {
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value="/login", method = RequestMethod.POST)
    public ModelAndView checkIfLoggedIn(User user, ModelMap modelMap, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ModelAndView modelAndView = new ModelAndView();
        try {
          User userFoundByLogin = userService.findByLogin(user.getLogin());
          User userFoundByEmail = userService.findByEmail(user.getLogin());

          User userFound = null;
          if (userFoundByEmail != null) {
              userFound = userFoundByEmail;
          } else if (userFoundByLogin != null) {
              userFound = userFoundByLogin;
          } else {
              modelMap.put("logged_f", false);
              // modelMap.put("error_msg", "Пользователя с такими логином/паролем не существует");
              modelMap.put("error_msg", "Ошибка авторизации");
              modelAndView.setViewName("login-error");
          }
          if ( userFound.getPassword().equals(user.getPassword()) ) {
              modelMap.put("logged_f", true);
              session.setAttribute("userLogin", userFound.getLogin());
              modelAndView.setViewName("redirect:/");
          } else {
              modelMap.put("logged_f", false);
              modelMap.put("error_msg", "Неверный пароль");
              modelAndView.setViewName("login-error");
          }
        } catch (Exception /* NonUniqueResultException */ e) {
            modelMap.put("logged_f", false);
            modelMap.put("error_msg", "Ошибка авторизации");
            modelAndView.setViewName("login-error");
            return modelAndView;
        }
        return modelAndView;
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public ModelAndView logOut(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.invalidate();
        return new ModelAndView("redirect:/");
    }

}