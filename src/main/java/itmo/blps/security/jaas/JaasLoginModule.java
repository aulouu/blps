package itmo.blps.security.jaas;

import itmo.blps.model.User;
import itmo.blps.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

public class JaasLoginModule implements LoginModule {
    private PasswordEncoder passwordEncoder;
    private String login;
    private Subject subject;
    private boolean loginSucceeded = false;
    private CallbackHandler callbackHandler;
    private UserRepository userRepository;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.userRepository = (UserRepository) options.get("userRepository");
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public boolean login() {
        var nameCallback = new NameCallback("login: ");
        var passwordCallback = new PasswordCallback("password: ", false);
        try {
            callbackHandler.handle(new Callback[]{nameCallback, passwordCallback});
        } catch (IOException | UnsupportedCallbackException e) {
            throw new RuntimeException(e);
        }
        login = nameCallback.getName();
        String password = new String(passwordCallback.getPassword());
        Optional<User> user = userRepository.findByUsername(login);
        loginSucceeded = user.map(u -> passwordEncoder.matches(password, u.getPassword())).orElse(false);
        return loginSucceeded;
    }

    @Override
    public boolean commit() {
        if (!loginSucceeded) return false;
        if (login == null) throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "login is null");
        Principal principal = (UserPrincipal) () -> login;
        subject.getPrincipals().add(principal);
        return true;
    }

    @Override
    public boolean abort() {
        return false;
    }

    @Override
    public boolean logout() {
        subject.getPrincipals().removeIf(principal -> principal instanceof UserPrincipal);
        loginSucceeded = false;
        return true;
    }
}
