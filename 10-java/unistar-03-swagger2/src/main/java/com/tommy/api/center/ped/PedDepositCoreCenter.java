package com.tommy.api.center.ped;

import com.tommy.api.center.ped.model.PedSendDepositRequest;
import com.tommy.api.center.ped.model.PedSendDepositResponse;
import com.tommy.api.center.ped.tool.PBKDF2Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
@RequestMapping(value = "/ped", produces = APPLICATION_JSON_VALUE) //配置返回值 application/json
@Api(tags = "PED充值-直接呼叫core-center")
public class PedDepositCoreCenter {
    private static final String SECRET_KEY = "123qwe";
    private static final String WEB_URL = "https://pf2web.uenvsit.com/";
    private static final String COMPANY_ID = "15";


    private static final String CORE_CENTER_URL = "http://localhost:8888";
    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String getDepositEncryptSource(final String secretKey, final String companyId, final String webUrl, final PedSendDepositRequest request) {
        final StringBuilder encryptedWord = new StringBuilder();
        encryptedWord.append(md5Hex(secretKey))
            .append(companyId).append(request.bankId).append(request.amount)
            .append(request.companyOrderNum).append(request.companyUser)
            .append(request.bankId).append(request.depositMode).append(0)
            .append(webUrl).append(request.memo).append(request.note).append(request.noteModel);
        return encryptedWord.toString();
    }

    private static String md5Hex(final String text) {
        return hash(text, "MD5");
    }

    private static String hash(final String text, final String algorithm) {
        try {
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            final byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return hex(digest);
        } catch (final NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }

    private static String hex(final byte[] bytes) {
        final char[] chars = new char[bytes.length << 1];
        int index = 0;
        for (final byte b : bytes) {  // two characters form the hex value.
            chars[index++] = HEX_CHARS[(b >> 4) & 0xF];
            chars[index++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation("新增")
    @PostMapping("/add")
    public PedSendDepositResponse addUser(@RequestBody final PedSendDepositRequest jbpPedSendDepositRequest) {

        final String uri = String.format("%s%s", CORE_CENTER_URL, "/ped-deposit/send");
        System.out.println("[LOG] uri = " + uri);
        // create headers
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // -----------------------------------------
        final PedSendDepositRequest afterBindData = bindData(jbpPedSendDepositRequest);
        // -----------------------------------------
        // build the request
        final HttpEntity<PedSendDepositRequest> request = new HttpEntity<>(afterBindData, headers);
        // send POST request
        final ResponseEntity<PedSendDepositResponse> response = restTemplate.postForEntity(uri, request, PedSendDepositResponse.class);

        return response.getBody();
    }

    private PedSendDepositRequest bindData(final PedSendDepositRequest sendDepositRequest) {
        sendDepositRequest.bankId = 1L;
        sendDepositRequest.companyOrderNum = "U5" + md5Hex("UB" + System.currentTimeMillis() + "DA");
        final String depositEncryptSource = getDepositEncryptSource(SECRET_KEY, COMPANY_ID, WEB_URL, sendDepositRequest);
        System.out.println("[LOG] depositEncryptSource = " + depositEncryptSource);
        final String encrypt = PBKDF2Utils.encrypt(depositEncryptSource);
        System.out.println("[LOG] encrypt = " + encrypt);
        sendDepositRequest.key = encrypt;
        return sendDepositRequest;
    }


}
