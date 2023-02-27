```
package com.galaxy.pltgateway.config;

import com.galaxy.pltgateway.validator.ReportSecurityValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authorizeRequests()
                .anyRequest()
                .permitAll();

    }

    @Bean
    public ReportSecurityValidator reportSecurityValidator() {
        return new ReportSecurityValidator();
    }

}
```


```
package com.galaxy.pltgateway.controller.basics.v1;

import com.galaxy.module.resp.SuccessResp;
import com.galaxy.pltgateway.controller.BaseController;
import com.galaxy.pltgateway.domain.basics.ReportManageDomainService;
import com.galaxy.pltgateway.enumeration.basics.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/v1/report")
@Tag(name = "報表", description = "報表API")
public class ReportManageController extends BaseController {
    private final ReportManageDomainService reportManageDomainService;

    public ReportManageController(HttpServletRequest request,
                                  ReportManageDomainService reportManageDomainService) {
        super(request);
        this.reportManageDomainService = reportManageDomainService;
    }

    @PreAuthorize("@reportSecurityValidator.hasAuthority(#reportType)")
    @Operation(summary = "報表製作,必須傳入報表類型(ReportType)")
    @Parameter(name = "reportType", description = "報表類型 ")
    @GetMapping
    public SuccessResp<Void> startingMakeReport(@RequestParam("reportType") ReportType reportType,
                                                @RequestParam Map<String, String> searchParam) {
        reportManageDomainService.startingWorkReport(searchParam);
        return new SuccessResp<>();
    }

    @PreAuthorize("@reportSecurityValidator.hasAuthority(#reportType)")
    @Operation(summary = "報表製作,必須傳入報表類型(ReportType)")
    @Parameter(name = "reportType", description = "報表類型")
    @Parameter(name = "id", description = "報表id")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("reportType") ReportType reportType,
                                                        @RequestParam("id") Long id) {
        return reportManageDomainService.makeDownloadFile(reportType, id);
    }

}

```


```
package com.galaxy.pltgateway.domain.basics.impl;

import com.galaxy.pltgateway.domain.basics.ReportManageDomainService;
import com.galaxy.pltgateway.enumeration.basics.ReportType;
import com.galaxy.pltgateway.feign.client.basics.BasicsFeignClient;
import com.galaxy.pltgateway.model.basics.vo.ReportDocumentVo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportManageDomainServiceImpl implements ReportManageDomainService {
    private final BasicsFeignClient basicsFeignClient;

    @Override
    public void startingWorkReport(Map<String, String> searchParam) {
        basicsFeignClient.createReportRecord(searchParam);
    }

    @Override
    public ResponseEntity<InputStreamResource> makeDownloadFile(ReportType reportType, Long id) {

        ReportDocumentVo docVo = basicsFeignClient.getDocument(reportType, id).getData();
        
        String filename = reportType.name() + "." + docVo.getFileSuffix();
        String mimeType = "application/" + docVo.getFileSuffix();
        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(docVo.getBytes()));
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(mimeType)).body(file);
    }
}

```

```
package com.galaxy.pltgateway.domain.basics;

import com.galaxy.pltgateway.enumeration.basics.ReportType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ReportManageDomainService {

    void startingWorkReport(Map<String, String> searchParam);

    ResponseEntity<InputStreamResource> makeDownloadFile(ReportType reportType, Long id);
}

```

```
package com.galaxy.pltgateway.enumeration.basics;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ReportType {
    // FIXME authority data
    PROXY("proxy:domain:edit"), // 代理列表
    PROXY_DOMAIN("proxy:proxy:editCommission") // 代理域名
    ;
    private final String authority; // 定義在 plt_account.vs_authority.authority

}

```


```
package com.galaxy.pltgateway.feign.client.basics;

import com.galaxy.module.resp.SuccessResp;
import com.galaxy.pltgateway.config.FeignSupportConfig;
import com.galaxy.pltgateway.enumeration.basics.ReportType;
import com.galaxy.pltgateway.feign.interceptor.InternalFeignInterceptor;
import com.galaxy.pltgateway.model.basics.dto.AvatarCreateDto;
import com.galaxy.pltgateway.model.basics.dto.AvatarQryDto;
import com.galaxy.pltgateway.model.basics.dto.AvatarUpdateDto;
import com.galaxy.pltgateway.model.basics.vo.AvatarVo;
import com.galaxy.pltgateway.model.basics.vo.PlatformCurrencyVo;
import com.galaxy.pltgateway.model.basics.vo.ReportDocumentVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;

@FeignClient(contextId = "plt-basics", name = "plt-basics", url = "${feign.client.plt-basics.url}", configuration = {InternalFeignInterceptor.class, FeignSupportConfig.class})
public interface BasicsFeignClient {

    @PostMapping(path = "/v1/file/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    SuccessResp<String> uploadImage(@RequestPart(value = "file") MultipartFile file, @RequestParam("imagePathType") Integer imagePathType);

    @PostMapping(path = "/v1/file/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    SuccessResp<String> uploadVideo(@RequestPart(value = "file") MultipartFile file, @RequestParam("videoPathType") Integer videoPathType);

    @PostMapping("/v1/config/avatar")
    SuccessResp<Void> insertAvatar(@RequestBody @Valid AvatarCreateDto dto);

    @PutMapping("/v1/config/avatar")
    SuccessResp<Void> updateAvatar(@RequestBody @Valid AvatarUpdateDto dto);

    @DeleteMapping("/v1/config/avatar/{id}")
    SuccessResp<Void> deleteAvatar(@Positive @NotNull @PathVariable("id") Long id);

    @GetMapping("/v1/config/avatars")
    SuccessResp<List<AvatarVo>> qryAvatarByFields(@SpringQueryMap @Valid AvatarQryDto dto);

    @GetMapping("/v1/config/avatar/{id}")
    SuccessResp<AvatarVo> qryAvatarById(@Positive @NotNull @PathVariable("id") Long id);

    @GetMapping("/v1/platform/currency")
    SuccessResp<List<PlatformCurrencyVo>> getPlatformCurrencyList();

    @PostMapping("/v1/report")
    SuccessResp<Void> createReportRecord(@RequestBody Map<String, String> dto);

    @GetMapping(value = "/v1/report/download", consumes = MediaType.APPLICATION_PROBLEM_JSON_VALUE)
    SuccessResp<ReportDocumentVo> getDocument(@RequestParam ReportType reportType, @NotNull @RequestParam Long id);

}

```


```
package com.galaxy.pltgateway.model.basics.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ReportDocumentVo {
    @NotNull
    private Long id;
    @NotNull
    private byte[] bytes;
    @NotNull
    private String fileSuffix;


}

```


```
package com.galaxy.pltgateway.validator;

import com.galaxy.pltgateway.enumeration.basics.ReportType;
import com.galaxy.pltgateway.security.details.AccountDetail;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Objects;


@Slf4j
@AllArgsConstructor
public class ReportSecurityValidator {

    public boolean hasAuthority(@NonNull ReportType reportType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) return false;

        Object principal = authentication.getPrincipal();
        if (Objects.isNull(principal)) return false;

        Collection<GrantedAuthority> authorities = ((AccountDetail) principal).getAuthorities();
        if (Objects.isNull(authorities)) return false;

        String checkAuthority = reportType.getAuthority();

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals(checkAuthority));

    }

}

```



