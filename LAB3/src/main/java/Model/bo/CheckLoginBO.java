package Model.bo;

import Model.dao.CheckLoginDAO;

public class CheckLoginBO {
    private CheckLoginDAO loginDAO = new CheckLoginDAO();

    public boolean checkLogin(String username, String password) {
        return loginDAO.isValidUser(username, password);
    }
}
