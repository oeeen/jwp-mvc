package slipp.controller;

import nextstep.mvc.tobe.JsonView;
import nextstep.mvc.tobe.ModelAndView;
import nextstep.utils.JsonUtils;
import nextstep.web.annotation.Controller;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slipp.domain.User;
import slipp.support.db.DataBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
public class UserApiController {
    private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public ModelAndView signUp(HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = JsonUtils.toObject(getBody(request), User.class);
            logger.debug("signUp User: {} ", user);
            DataBase.addUser(user);
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.addHeader("Location", "/api/users?userId=" + user.getUserId());
        } catch (IOException e) {
            logger.debug("Sign Up Fail Exception: {}", ExceptionUtils.getStackTrace(e));
        }

        return new ModelAndView();
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ModelAndView showUser(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        User user = DataBase.findUserById(userId);

        return new ModelAndView(new JsonView()).addObject("User", user);
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.PUT)
    public ModelAndView updateUser(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userId = request.getParameter("userId");
            User user = JsonUtils.toObject(getBody(request), User.class);
            logger.debug("Update User: {} ", user);
            DataBase.findUserById(userId).update(user);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            logger.debug("Sign Up Fail Exception: {}", ExceptionUtils.getStackTrace(e));
        }

        return new ModelAndView();
    }

    private String getBody(HttpServletRequest request) throws IOException {
        InputStream inputStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        int contentLength = request.getContentLength();
        char[] body = new char[contentLength];

        bufferedReader.read(body, 0, contentLength);

        return String.copyValueOf(body);
    }
}
