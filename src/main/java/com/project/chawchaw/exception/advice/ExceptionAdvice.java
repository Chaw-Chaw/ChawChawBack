package com.project.chawchaw.exception.advice;

import com.project.chawchaw.config.response.DefaultResponseVo;
import com.project.chawchaw.config.response.ResponseMessage;
import com.project.chawchaw.exception.*;
import com.project.chawchaw.response.CommonResult;


import com.project.chawchaw.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    private final MessageSource messageSource;
//
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")),getMessage("unKnown.msg"));
//    }

    @ExceptionHandler(LoginFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity loginFailureException(){
       return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.LOGIN_FAIL,false),HttpStatus.OK);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity userAlreadyExistException(){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.DUPLICATE_USER,false),HttpStatus.OK);
    }
    @ExceptionHandler(FollowAlreadyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity followAlreadyExistException(){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.ALREADY_FOLLOW,false),HttpStatus.OK);
    }

    @ExceptionHandler(InvalidateProviderException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult invalidateProviderException() {
        return responseService.getFailResult(Integer.valueOf(getMessage("InvalidateProvider.code")),getMessage("InvalidateProvider.msg"));
    }


    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity userNotFoundException(HttpServletRequest request, UserNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.NOT_FOUND_USER,false),HttpStatus.OK);

    }
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult resourceNotFoundException(HttpServletRequest request, ResourceNotFoundException e){
        return responseService.getFailResult(Integer.valueOf(getMessage("resourceNotFound.code")),getMessage("resourceNotFound.msg"));

    }

    @ExceptionHandler(CountryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity countryNotFoundException(HttpServletRequest request, CountryNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.NOT_FOUND_COUNTRY,false),HttpStatus.OK);
    }
    @ExceptionHandler(LanguageNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity languageFoundException(HttpServletRequest request, LanguageNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.NOT_FOUND_LANGUAGE,false),HttpStatus.OK);
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity chatRoomNotFoundException(HttpServletRequest request, LanguageNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.CHATROOM_NOT_FOUND,false),HttpStatus.OK);
    }
    @ExceptionHandler(BlockAlreadyExistException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ResponseEntity  blockAlreadyExistException(HttpServletRequest request, LanguageNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.BLOCK_ALREADY_EXIST,false),HttpStatus.OK);
    }


    @ExceptionHandler(BlockNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity blockNotFoundException(HttpServletRequest request, FollwNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.BLOCK_NOT_FOUND,false),HttpStatus.OK);
    }

    @ExceptionHandler(FollwNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity followNotFoundException(HttpServletRequest request, FollwNotFoundException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.NOT_FOUND_FOLLOW,false),HttpStatus.OK);
    }
//    @ExceptionHandler(CUsernameSigninFailedException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected  CommonResult usernameSigninFailedException(HttpServletRequest request,CUsernameSigninFailedException e){
//        return responseService.getFailResult(Integer.valueOf(getMessage("usernameSigninFailed.code")),getMessage("usernameSigninFailed.msg"));
//    }
//    @ExceptionHandler(CResourceNotExistException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    protected  CommonResult resourceNotExistException(HttpServletRequest request,CResourceNotExistException e){
//        return responseService.getFailResult(Integer.valueOf(getMessage("resourceNotExist.code")),getMessage("resourceNotExist.msg"));
//    }
//    @ExceptionHandler(CNotOwnerException.class)
//    @ResponseStatus(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
//    protected  CommonResult notOwnerException(HttpServletRequest request,CNotOwnerException e){
//        return responseService.getFailResult(Integer.valueOf(getMessage("notOwner.code")),getMessage("notOwner.msg"));
//    }
//
    @ExceptionHandler(AuthenticationEntryPointException.class)
    protected ResponseEntity authenticationEntryPointException(HttpServletRequest request,AuthenticationEntryPointException e){

        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.ENTRYPOINT_EXCEPTION,false),HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity accessDeniedException(HttpServletRequest request,AccessDeniedException e){
        return new ResponseEntity(DefaultResponseVo.res(ResponseMessage.ACCESS_DENIED,false),HttpStatus.FORBIDDEN);
    }



//    @ExceptionHandler(COrderNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    protected CommonResult orderNotFound(HttpServletRequest request,AccessDeniedException e){
//        return responseService.getFailResult(Integer.valueOf(getMessage("orderNotFound.code")), getMessage("orderNotFound.msg"));
//
//    }
//    @ExceptionHandler(CNotEnoughStockException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected CommonResult notEnoughStock(){
//        return responseService.getFailResult(Integer.valueOf(getMessage("notEnoughStock.code")), getMessage("notEnoughStock.msg"));
//
//    }
//    @ExceptionHandler(CShoesAlreadyExistException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    protected CommonResult shoesAlreadyExist(){
//        return responseService.getFailResult(Integer.valueOf(getMessage("shoesAlreadyExist.code")), getMessage("shoesAlreadyExist.msg"));
//
//    }
    @ExceptionHandler(CommunicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult communicationException(HttpServletRequest request, CommunicationException e){
        return responseService.getFailResult(Integer.valueOf(getMessage("communicationError.code")), getMessage("communicationError.msg"));
    }
//    @ExceptionHandler(CUserNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    protected CommonResult messageNotFoundException(HttpServletRequest request,CUserNotFoundException e){
//        return responseService.getFailResult(Integer.valueOf(getMessage("userNotFound.code")),getMessage("userNotFound.msg"));
//
//    }




    private String getMessage(String code){
        return getMessage(code,null);
    }
    private String getMessage(String code, Object[] args){
        return messageSource.getMessage(code,args, LocaleContextHolder.getLocale());
    }



}