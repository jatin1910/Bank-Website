package services;

import dto.LoginRequestDto;
import dto.LoginResponseDto;
import models.AccountStatus;
import models.UserDetails;
import models.UserPassword;
import java.util.regex.Pattern;

public class LoginService {
    public static LoginResponseDto validationCredentials(LoginRequestDto loginRequestDto)
    {
        LoginResponseDto loginResponseDto=new LoginResponseDto();
        boolean uId = Pattern.matches("[0-9]{9}",loginRequestDto.getUserId());
        if(uId) {
            try {
                UserDetails userDetails = UserDetails.find.byId(Integer.parseInt(loginRequestDto.getUserId()));
                if (userDetails == null) {
                    loginResponseDto.setStatusCode(400);
                    loginResponseDto.setStatusMsg("Invalid User Id");
                    return loginResponseDto;
                }
                UserPassword userPassword = UserPassword.find.query().where()
                        .eq("userId.id", userDetails.getId())
                        .eq("passwrd", loginRequestDto.getPswrd()).findOne();

                if (userPassword == null) {
                    loginResponseDto.setStatusCode(404);
                    loginResponseDto.setStatusMsg("Wrong Password");
                    return loginResponseDto;
                }

                if (userDetails.getStatusId().getId().equals(AccountStatus.statusEnum.INACTIVE.getId())) {
                    loginResponseDto.setStatusCode(400);
                    loginResponseDto.setStatusMsg("Inactive Account.... Contact Admin ");
                    return loginResponseDto;
                }

                if (userDetails.getStatusId().getId().equals(AccountStatus.statusEnum.ACTIVE.getId())) {
                    loginResponseDto.setStatusCode(200);
                    loginResponseDto.setStatusMsg("Successful Login");
                    loginResponseDto.setStatusId(userDetails.getStatusId().getStatusName());
                    loginResponseDto.setRoleId(userDetails.getRoleId().getRoleName());
                }
            } catch (Exception exception) {
                System.out.println("Some Exception" + exception);
                loginResponseDto.setStatusCode(500);
                loginResponseDto.setStatusMsg("Internal Server Error");
                return loginResponseDto;
            }
        }
        else
        {
            loginResponseDto.setStatusCode(400);
            loginResponseDto.setStatusMsg("Invalid User Id");
        }

        return loginResponseDto;
    }
}
