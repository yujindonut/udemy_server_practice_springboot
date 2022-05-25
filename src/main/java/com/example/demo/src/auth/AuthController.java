package com.example.demo.src.auth;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.auth.model.GetAutoLoginReq;
import com.example.demo.src.auth.model.GetAutoLoginRes;
import com.example.demo.src.auth.model.PostLoginReq;
import com.example.demo.src.auth.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

import static com.example.demo.utils.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;

    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService){
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    /* 로그인 */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            //이메일 검증
            if (postLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (postLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
//            이메일 정규식 검증
            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            PostLoginRes postLoginRes = authService.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

   @ResponseBody
    @PostMapping("/autoLogin")
    public BaseResponse<GetAutoLoginRes> autologin(@RequestBody GetAutoLoginReq getAutoLoginReq) throws BaseException{
        try{
//            if(jwtService.getJwt()==null){
//                return new BaseResponse<>(EMPTY_JWT);
//            }
//            else if(jwtService.checkJwt(jwtService.getJwt())==1){
//                return new BaseResponse<>(INVALID_JWT);
//
//            }
            //이메일 검증
            if (getAutoLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (getAutoLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
//            이메일 정규식 검증
            if(!isRegexEmail(getAutoLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            else{
                GetAutoLoginRes getAutoRes = authService.getAutoLogin(getAutoLoginReq);
                return new BaseResponse<>(getAutoRes);
            }

        }catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

}