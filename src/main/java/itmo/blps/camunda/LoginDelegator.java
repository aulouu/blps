package itmo.blps.camunda;

import itmo.blps.dto.auth.LoginUserDTO;
import itmo.blps.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginDelegator implements JavaDelegate {
    private final AuthService authService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        var username = (String) delegateExecution.getVariable("username");
        var password = (String) delegateExecution.getVariable("password");
        LoginUserDTO userDTO = new LoginUserDTO(username,password);
        try {
            var result = authService.login(userDTO, "");
            delegateExecution.setVariable("auth", true);
            delegateExecution.setVariable("token", result.getToken());
        } catch (Exception e){
            delegateExecution.setVariable("auth", false);
        }
    }
}
