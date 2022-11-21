package com.hkhs.hmms.haa.api.impl;

import javax.jws.WebService;

import com.hkhs.hmms.haa.api.MainWS;



@WebService(endpointInterface = "com.hkhs.hmms.service.MainService")
public class MainWSImpl implements MainWS {

    @Override
    public String ping(){
        return "Pong";
    }

}
