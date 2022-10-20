package com.tommy.api.center.jbp;

import com.tommy.api.center.jbp.model.JbpSendDepositRequest;
import com.tommy.api.center.jbp.model.JbpSendJbpWithdrawResponse;
import com.tommy.api.center.jbp.model.JbpSendWithdrawRequest;
import com.tommy.api.center.jbp.tool.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/jbp-withdraw", produces = APPLICATION_JSON_VALUE) //配置返回值 application/json
@Api(tags = "jbp-withdraw-直接呼叫core-center")
public class JbpWithdrawCoreCenter {
    private static final String SECRET_KEY = "oixj2^42gsCaPf";
    private static final String WEB_URL = "https://pf2web.uenvsit.com/";
    private static final String COMPANY_ID = "17";


    private static final String CORE_CENTER_URL = "http://localhost:8888";


    @Autowired
    private RestTemplate restTemplate;


    @ApiOperation(value = "提現申請", response = JbpSendJbpWithdrawResponse.class)
    @PostMapping("/send")
    public ResponseEntity<JbpSendJbpWithdrawResponse> send(
        @ApiParam(value = "JbpSendWithdrawRequest", required = true)
        @RequestBody final JbpSendWithdrawRequest jbpSendDepositRequest) {
        final String path = "/jbp-withdraw/send";
        final String uri = String.format("%s%s", CORE_CENTER_URL, path);
        System.out.println("[LOG] uri = " + uri);
        // create headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // -----------------------------------------
        bindData(jbpSendDepositRequest);
        // -----------------------------------------
        // build the request
        final HttpEntity<JbpSendWithdrawRequest> request = new HttpEntity<>(jbpSendDepositRequest, headers);
        // send POST request
        return restTemplate.postForEntity(uri, request, JbpSendJbpWithdrawResponse.class);

    }


    private void bindData(final JbpSendWithdrawRequest jbpSendDepositRequest) {
        jbpSendDepositRequest.bankId = 1L;
        jbpSendDepositRequest.companyOrderNum = "U5" + MD5.md5Hex("UB" + System.currentTimeMillis() + "DA");
        jbpSendDepositRequest.key = "key";
    }

    private void testKey(final JbpSendDepositRequest jbpSendDepositRequest) {
        final StringBuilder encryptedWord = new StringBuilder();
        encryptedWord.append(MD5.md5Hex(SECRET_KEY))
            .append(COMPANY_ID).append(jbpSendDepositRequest.channelId).append(jbpSendDepositRequest.amount)
            .append(jbpSendDepositRequest.companyOrderNum).append(jbpSendDepositRequest.companyUser)
            .append(jbpSendDepositRequest.channelId).append(jbpSendDepositRequest.depositMode).append(0)
            .append(WEB_URL).append(jbpSendDepositRequest.memo).append(jbpSendDepositRequest.note).append(jbpSendDepositRequest.noteModel);
        final String depositEncryptSource = encryptedWord.toString();
        MD5.md5Hex(depositEncryptSource);
    }

}
