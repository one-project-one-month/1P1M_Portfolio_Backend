package com._p1m.portfolio.features.opomRegister.service.impl;

import com._p1m.portfolio.common.constant.Status;
import com._p1m.portfolio.config.exceptions.DuplicateEntityException;
import com._p1m.portfolio.config.exceptions.EntityNotFoundException;
import com._p1m.portfolio.config.response.dto.ApiResponse;
import com._p1m.portfolio.features.opomRegister.dto.request.UserRegisterRequest;
import com._p1m.portfolio.features.opomRegister.dto.response.UserRegisterResponse;
import com._p1m.portfolio.features.opomRegister.repository.OpomRegisterRespository;
import com._p1m.portfolio.features.opomRegister.service.OpomRegisterService;
import com._p1m.portfolio.data.models.OpomRegister;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpomRegisterServiceImpl implements OpomRegisterService {

    private final OpomRegisterRespository opomRegisterRespository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse registerUser(final UserRegisterRequest userRegisterRequest) {
        if(opomRegisterRespository.existsByEmail(userRegisterRequest.getEmail())){
            throw new DuplicateEntityException("Email is Already in Use.");
        }
        if(opomRegisterRespository.existsByPhone(userRegisterRequest.getPhone())){
            throw new DuplicateEntityException("Phone Number is Already in Use.");
        }

        OpomRegister opomRegister = new OpomRegister();
        opomRegister.setName(userRegisterRequest.getName());
        opomRegister.setEmail(userRegisterRequest.getEmail());
        opomRegister.setPhone(userRegisterRequest.getPhone());
        opomRegister.setGithub_url(userRegisterRequest.getGithub_username());
        opomRegister.setTelegram_username(userRegisterRequest.getTelegram_username());
        opomRegister.setRole(userRegisterRequest.getRole());

        opomRegisterRespository.save(opomRegister);

        UserRegisterResponse response = modelMapper.map(opomRegister , UserRegisterResponse.class);
        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(Map.of("Register User Info" , response))
                .message("Registered for OPOM Project Successfully!")
                .build();
    }

    @Override
    public ApiResponse updateOpomRegisterData(Long id, UserRegisterRequest updateRequest) {
        com._p1m.portfolio.data.models.OpomRegister opomRegister = this.opomRegisterRespository.findByIdAndStatus(id , Status.ACTIVE)
                .orElseThrow(()-> new EntityNotFoundException("User Data Not Found wiht Id :" + id));

        opomRegister.setName(updateRequest.getName());
        opomRegister.setEmail(updateRequest.getEmail());
        opomRegister.setPhone(updateRequest.getPhone());
        opomRegister.setGithub_url(updateRequest.getGithub_username());
        opomRegister.setTelegram_username(updateRequest.getTelegram_username());
        opomRegister.setRole(updateRequest.getRole());

        opomRegisterRespository.save(opomRegister);

        UserRegisterResponse response = modelMapper.map(opomRegister , UserRegisterResponse.class);
        return ApiResponse.builder()
                .success(1).code(HttpStatus.OK.value())
                .data(Map.of("Update Register User Info" , response))
                .message("Updated User Register successfully")
                .build();
    }
}
