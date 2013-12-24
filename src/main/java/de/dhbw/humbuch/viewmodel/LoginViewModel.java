package de.dhbw.humbuch.viewmodel;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Student;

public class LoginViewModel {

        public interface IsLoggedIn extends State<Boolean> {
        }

        public interface DoLogout extends ActionHandler {
        }

        public interface DoLogin extends ActionHandler {
        }

        @Inject
        private DAO<Student> daoStudent;

        @ProvidesState(IsLoggedIn.class)
        public final BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
                        Boolean.class);

        public LoginViewModel() {
                isLoggedIn.set(new Boolean(false));
        }

        @HandlesAction(DoLogin.class)
        public void doLogin(String username, String password) {
                // loginSuccessful.set(daoStudent.find(1).getFirstname());

                if ((username == null) || (password == null)) {
                        return;
                } else if (username.equals("admin") && password.equals("1234")) {
                        isLoggedIn.set(new Boolean(true));
                }
        }

        @HandlesAction(DoLogout.class)
        public void doLogout(Object obj) {
                isLoggedIn.set(new Boolean(false));
                System.out.println("LoginViewModel->doLogout()");
        }

}