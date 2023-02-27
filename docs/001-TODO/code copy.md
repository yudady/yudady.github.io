```
package com.galaxy.controller.v1;

import com.galaxy.domain.ReportDomainService;
import com.galaxy.enumeration.ReportType;
import com.galaxy.model.dto.ReportUpdateDto;
import com.galaxy.model.vo.ReportDocumentVo;
import com.galaxy.module.controller.PltBaseController;
import com.galaxy.module.resp.SuccessResp;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/v1/report")
public class ReportManageController extends PltBaseController {

    private final ReportDomainService reportDomainService;

    public ReportManageController(HttpServletRequest request, ReportDomainService reportDomainService) {
        super(request);
        this.reportDomainService = reportDomainService;
    }

    @PostMapping
    public SuccessResp<Void> createReportRecord(@RequestBody Map<String, Object> searchParam) {
        reportDomainService.create(searchParam, getAccountId(), getAccount());
        return new SuccessResp<>();
    }


    @PutMapping
    public SuccessResp<Void> makeReportDocument(@RequestBody @Valid ReportUpdateDto dto) {
        reportDomainService.makeReportDocument(dto, getAccountId(), getAccount());
        return new SuccessResp<>();
    }

    @GetMapping("/download")
    @SneakyThrows
    public SuccessResp<ReportDocumentVo> getDocument(@RequestParam Long id, @RequestParam ReportType reportType, HttpServletResponse response) {
        return new SuccessResp<>(reportDomainService.downloadDocument(id, reportType));

    }

}

```



```
package com.galaxy.converter;

import com.galaxy.model.vo.ReportDownloadRecordVo;
import com.galaxy.model.vo.ReportQryVo;
import com.galaxy.storage.rdbms.entity.ReportDownloadRecordEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportConverter {

    ReportConverter INSTANCES = Mappers.getMapper(ReportConverter.class);


    ReportQryVo toQryVo(ReportDownloadRecordEntity entity);

    ReportDownloadRecordVo toVo(ReportDownloadRecordEntity entity);

}

```

```

```



```
package com.galaxy.domain;

import com.galaxy.enumeration.ReportType;
import com.galaxy.model.dto.ReportUpdateDto;
import com.galaxy.model.vo.ReportDocumentCombineVo;
import com.galaxy.model.vo.ReportDocumentVo;

import java.util.Map;

public interface ReportDomainService {

    void create(Map<String, Object> searchParam, Long accountId, String account);

    void makeReportDocument(ReportUpdateDto dto, Long accountId, String account);

    void updateStatusFail(Long id);

    void makeCombineDocument(ReportDocumentCombineVo payload);

    ReportDocumentVo downloadDocument(Long id, ReportType reportType);
}

```


```
package com.galaxy.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportDocumentType {
    CSV("/doc/report/csv", ".csv"),
    PDF("/doc/report/pdf", ".pdf");

    private final String path;
    private final String fileSuffix;

}

```



```
package com.galaxy.enumeration;

public enum ReportExportType {
    CSV
}

```

```
package com.galaxy.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportStatus {
    RUNNING(0, "處理中"),
    SUCCESS(1, "成功"),
    FAIL(2, "失敗");
    private final Integer code;
    private final String desc;
    
}

```



```
package com.galaxy.enumeration;

import lombok.Getter;

@Getter
public enum ReportType {
    PROXY("PROXY", "代理列表"),
    PROXY_DOMAIN("PROXY_DOMAIN", "代理域名")
    ;

    private final String type;
    private final String desc;

    ReportType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}

```


```
delete ReportTypeEnum.java
ReportCreateDto.java
```


```


```




```
package com.galaxy.model.vo;

import com.galaxy.enumeration.ReportType;
import lombok.Data;

import java.util.Map;

@Data
public class ReportCreateVo {
    private String routingKey;
    private String source;
    private ReportType reportType;
    private String searchParamHash;
    private Map<String, String> searchParam;
    private String errorMessage;
    private Long creatorId;
    private String creator;
}

```




```
package com.galaxy.model.vo;

import com.galaxy.enumeration.ReportDocumentType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReportDocumentCombineVo {
    @NotNull
    private Long id;
    @NotNull
    private ReportDocumentType reportDocumentType;

}

```


```
package com.galaxy.model.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReportDocumentVo {
    @NotNull
    private Long id;
    @NotNull
    private byte[] bytes;
    @NotNull
    private String fileSuffix;


}

```



````
package com.galaxy.model.vo;

import com.galaxy.enumeration.ReportStatus;
import com.galaxy.enumeration.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ReportDownloadRecordVo {

    private Long id;
    private String source; // 报表来源,中文說明
    private ReportType reportType; // 報表名稱
    private String filePath; // 檔案路徑
    private ReportStatus status; // RUNNING:處理中 SUCCESS:成功 FAIL:失敗
    private String searchParamHash; // md5(searchParam)

    private Map<String, Object> searchParam; // 查詢條件
    private String errorMessage; // 錯誤訊息
}


```



```
package com.galaxy.model.vo;

import com.galaxy.enumeration.ReportType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@Builder
public class ReportQryVo {
    @NotNull
    private Long id;
    @NotNull
    private ReportType reportType;
    @NotNull
    private Map<String, Object> searchParam;

}

```

```
package com.galaxy.mq.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportConfig {
    // TODO
    // FIXME mq sync to normal

    public static final Integer REPORT_CACHE_MINUTE = 5; // cache 5分鐘
    /**
     * 報表 ROUTING KEY 規則
     */
    public final static String REPORT_TOPIC_ROUTING_KEY_RULE = "plt.basic.report.topic.%s.q";
    /**
     * 報表使用TopicExchange
     */
    public final static String REPORT_TOPIC_EX = "plt.basic.report.topic.ex";
    public final static String MONITOR_RUN_FAILURE_Q = "plt.basic.report.topic.q";
    public final static String MONITOR_RUN_FAILURE_DATA_KEY = "plt.basic.report.topic.#";
    private final static Integer DELAY_QUEUE_DELAY_TIME = REPORT_CACHE_MINUTE * 60 * 1000;
    private final static String DELAY_QUEUE_CHANNEL_Q = "plt.basic.report.delay.channel.q"; // 延遲隊列
    public final static String DELAY_QUEUE_EX = "plt.basic.report.delay.ex";
    public final static String DELAY_QUEUE_Q = "plt.basic.report.delay.q";
    public final static String DEAD_QUEUE_EX = "plt.basic.report.dead.ex";
    public final static String DEAD_QUEUE_Q = "plt.basic.report.dead.q";
    public final static String COMBINE_FILE_EX = "plt.basic.report.combine.file.ex";
    public final static String COMBINE_FILE_Q = "plt.basic.report.combine.file.q";

    @Bean("reportTopicExchange")
    public TopicExchange reportTopicExchange(@Qualifier("normalAdmin") RabbitAdmin admin) {
        TopicExchange topicExchange = new TopicExchange(REPORT_TOPIC_EX);
        topicExchange.setAdminsThatShouldDeclare(admin);
        return topicExchange;
    }

    @Bean("reportCommonQueue")
    public Queue reportCommonQueue(@Qualifier("normalAdmin") RabbitAdmin admin) {
        Queue q = QueueBuilder.durable(MONITOR_RUN_FAILURE_Q).quorum().build();
        q.setAdminsThatShouldDeclare(admin);
        return q;
    }

    @Bean("reportCommonQueueBinding")
    public Binding reportCommonQueueBinding(@Qualifier("normalAdmin") RabbitAdmin admin,
                                            @Qualifier("reportTopicExchange") TopicExchange topicExchange,
                                            @Qualifier("reportCommonQueue") Queue q) {

        Binding b = BindingBuilder.bind(q).to(topicExchange).with(MONITOR_RUN_FAILURE_DATA_KEY);
        b.setAdminsThatShouldDeclare(admin);
        return b;
    }

    @Bean("reportDelayExchange")
    public DirectExchange reportDelayExchange(@Qualifier("normalAdmin") RabbitAdmin admin) {
        DirectExchange dex = new DirectExchange(DELAY_QUEUE_EX);
        dex.setAdminsThatShouldDeclare(admin);
        return dex;
    }

    @Bean("reportDelayQueue")
    public Queue reportDelayQueue(@Qualifier("normalAdmin") RabbitAdmin admin) {
        Queue q = QueueBuilder.durable(DELAY_QUEUE_Q)
                .quorum()
                .deadLetterExchange(DEAD_QUEUE_EX)
                .deadLetterRoutingKey(DEAD_QUEUE_Q)
                .ttl(DELAY_QUEUE_DELAY_TIME)
                .build();
        q.setAdminsThatShouldDeclare(admin);
        return q;
    }

    @Bean("delayQueueBinding")
    public Binding delayQueueBinding(@Qualifier("normalAdmin") RabbitAdmin admin,
                                     @Qualifier("reportDelayExchange") DirectExchange dex,
                                     @Qualifier("reportDelayQueue") Queue q) {
        Binding b = BindingBuilder.bind(q).to(dex).with(DELAY_QUEUE_CHANNEL_Q);
        b.setAdminsThatShouldDeclare(admin);
        return b;
    }


    @Bean("reportDeadExchange")
    public DirectExchange reportDeadExchange(@Qualifier("normalAdmin") RabbitAdmin admin) {
        DirectExchange dex = new DirectExchange(DEAD_QUEUE_EX);
        dex.setAdminsThatShouldDeclare(admin);
        return dex;
    }

    @Bean("reportDeadQueue")
    public Queue reportDeadQueue(@Qualifier("normalAdmin") RabbitAdmin admin) {
        Queue q = QueueBuilder.durable(DEAD_QUEUE_Q)
                .quorum()
                .build();
        q.setAdminsThatShouldDeclare(admin);
        return q;
    }

    @Bean("reportDeadQueueBinding")
    public Binding reportDeadQueueBinding(@Qualifier("normalAdmin") RabbitAdmin admin,
                                          @Qualifier("reportDeadExchange") DirectExchange dex,
                                          @Qualifier("reportDeadQueue") Queue q) {
        Binding b = BindingBuilder.bind(q).to(dex).with(DEAD_QUEUE_Q);
        b.setAdminsThatShouldDeclare(admin);
        return b;
    }


    @Bean("combineFileEx")
    public FanoutExchange combineFileEx(@Qualifier("normalAdmin") RabbitAdmin admin) {
        FanoutExchange ex = new FanoutExchange(COMBINE_FILE_EX);
        ex.setAdminsThatShouldDeclare(admin);
        return ex;
    }

    @Bean("combineFileQ")
    public Queue combineFileQ(@Qualifier("normalAdmin") RabbitAdmin admin) {
        Queue q = QueueBuilder.durable(COMBINE_FILE_Q).quorum().build();
        q.setAdminsThatShouldDeclare(admin);
        return q;
    }

    @Bean
    public Binding combineFileQBinding(@Qualifier("normalAdmin") RabbitAdmin admin,
                                       @Qualifier("combineFileEx") FanoutExchange ex,
                                       @Qualifier("combineFileQ") Queue q) {
        Binding b = BindingBuilder.bind(q).to(ex);
        b.setAdminsThatShouldDeclare(admin);
        return b;
    }
}

```

```
package com.galaxy.mq.rabbit;

import com.galaxy.domain.ReportDomainService;
import com.galaxy.model.vo.ReportDocumentCombineVo;
import com.galaxy.model.vo.ReportQryVo;
import com.galaxy.module.Sender;
import com.galaxy.module.rabbit.listener.RabbitListenerRequeueable;
import com.galaxy.module.rabbit.model.RabbitSendWith;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import static com.galaxy.mq.rabbit.config.ReportConfig.COMBINE_FILE_EX;
import static com.galaxy.mq.rabbit.config.ReportConfig.COMBINE_FILE_Q;
import static com.galaxy.mq.rabbit.config.ReportConfig.DEAD_QUEUE_EX;
import static com.galaxy.mq.rabbit.config.ReportConfig.DEAD_QUEUE_Q;
import static com.galaxy.mq.rabbit.config.ReportConfig.DELAY_QUEUE_Q;
import static com.galaxy.mq.rabbit.config.ReportConfig.MONITOR_RUN_FAILURE_Q;
import static com.galaxy.mq.rabbit.config.ReportConfig.REPORT_TOPIC_EX;

@Slf4j
@Validated
@Component
public class ReportListener extends com.galaxy.module.rabbit.listener.RabbitListener {

    private final Sender<RabbitSendWith> rabbitSender;
    private final ReportDomainService reportDomainService;


    public ReportListener(@Qualifier("syncRabbitSender") Sender<RabbitSendWith> rabbitSender,
                          ReportDomainService reportDomainService) {
        this.rabbitSender = rabbitSender;
        this.reportDomainService = reportDomainService;
    }

    @SneakyThrows
    @RabbitListener(queues = MONITOR_RUN_FAILURE_Q, containerFactory = "normalContainerFactory", concurrency = "1")
    public void registerReportFailListener(@Payload Message<ReportQryVo> messages, Channel channel) {
        processMessageOrRequeue(messages, channel,
                new RabbitListenerRequeueable.RequeueInfo(REPORT_TOPIC_EX, MONITOR_RUN_FAILURE_Q,
                        MONITOR_RUN_FAILURE_Q, rabbitSender),
                () -> {
                    ReportQryVo qryVo = messages.getPayload();
                    rabbitSender.send(new RabbitSendWith("", DELAY_QUEUE_Q), qryVo);
                    log.info("send message to delay queue");
                });
    }

    @SneakyThrows
    @RabbitListener(queues = DEAD_QUEUE_Q, containerFactory = "normalContainerFactory", concurrency = "1")
    public void handleFailedRecord(@Payload Message<ReportQryVo> messages, Channel channel) {
        processMessageOrRequeue(messages, channel,
                new RabbitListenerRequeueable.RequeueInfo(DEAD_QUEUE_EX, DEAD_QUEUE_Q, DEAD_QUEUE_Q, rabbitSender),
                () -> {
                    ReportQryVo payload = messages.getPayload();
                    reportDomainService.updateStatusFail(payload.getId());
                    log.info("update message status to fail : id = " + payload.getId());
                });
    }


    @SneakyThrows
    @RabbitListener(queues = COMBINE_FILE_Q, containerFactory = "normalContainerFactory", concurrency = "1")
    public void handleCombineDocument(@Payload Message<ReportDocumentCombineVo> messages, Channel channel) {
        processMessageOrRequeue(messages, channel,
                new RabbitListenerRequeueable.RequeueInfo(COMBINE_FILE_EX, COMBINE_FILE_Q, COMBINE_FILE_Q, rabbitSender),
                () -> reportDomainService.makeCombineDocument(messages.getPayload()));
    }

}
```


```
package com.galaxy.service.impl;

import com.galaxy.enumeration.ReportDocumentType;
import com.galaxy.module.exception.BusinessException;
import com.galaxy.service.ReportDocumentService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.galaxy.module.error.Error.BAD_REQUEST_ERROR;


@Service
public class CSVDocumentServiceImpl implements ReportDocumentService {
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_TIME;

    public boolean accept(ReportDocumentType reportDocumentType) {
        return ReportDocumentType.CSV == reportDocumentType;
    }

    @Override
    public byte[] makeDocument(List<String> titles, List<List<?>> rows) throws BusinessException {
        if (rows.isEmpty()) throw new BusinessException(BAD_REQUEST_ERROR, "查無資料");

        if (titles.size() != rows.get(0).size()) throw new BusinessException(BAD_REQUEST_ERROR, "表頭與資料無法匹配");
        
        String reportTitle = titles.stream().map(String::valueOf).collect(Collectors.joining(","));
        String reportContent = rows.stream()
                .map(this::toRowString)
                .collect(Collectors.joining(System.lineSeparator()));

        return String.format("%s%s%s", reportTitle, System.lineSeparator(), reportContent).getBytes();
    }

    @Override
    public byte[] combineDocument(@NonNull List<byte[]> documents) {
        if (documents.isEmpty()) throw new BusinessException(BAD_REQUEST_ERROR, "google storage 查無資料");
        if (documents.size() == 1) return documents.get(0);


        String firstDocument = new String(documents.get(0));
        String nextAllRemoveTitleDocuments = documents.stream()
                .skip(1)
                .map(this::removeCsvTitles)
                .collect(Collectors.joining(System.lineSeparator()));

        String csv = firstDocument + System.lineSeparator() + nextAllRemoveTitleDocuments;
        return csv.getBytes(StandardCharsets.UTF_8);
    }

    private String removeCsvTitles(byte[] rowBytes) {
        String[] rows = new String(rowBytes).split(System.lineSeparator());
        return Arrays.stream(rows)
                .skip(1)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private String toRowString(List<?> row) {
        return row.stream()
                .map(this::toCellString)
                .collect(Collectors.joining(","));
    }

    private String toCellString(Object cell) {
        if (cell == null) return "";

        switch (cell.getClass().getName()) {
            case "java.time.OffsetDateTime":
                return formatter.format((OffsetDateTime) cell);
            default:
                return cell.toString();
        }
    }
}

```


```
package com.galaxy.service.impl;

import com.galaxy.enumeration.ImagePath;
import com.galaxy.enumeration.ReportDocumentType;
import com.galaxy.enumeration.VideoPath;
import com.galaxy.model.vo.UploadFileVo;
import com.galaxy.service.FileManageService;
import com.galaxy.utils.GcpUtil;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsFileManageServiceImpl implements FileManageService {

    private final Storage storage;

    @SneakyThrows
    @Override
    public void createBlob(@NonNull String filePath, @NonNull MultipartFile multipartFile) {
        createBlob(filePath, multipartFile.getBytes());
    }

    @Override
    public void createBlob(@NonNull String path, @NonNull byte[] content) {
        String bucketName = GcpUtil.getBucketName();
        BlobId blobId = BlobId.of(bucketName, path);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, content);
    }

    @Override
    public String getDocumentPrefixPath(@NonNull ReportDocumentType reportDocumentType) {
        return String.format("/%s%s/", GcpUtil.getPrefixFolder(), reportDocumentType.getPath());
    }

    @SneakyThrows
    @Override
    public String getImagePath(@NonNull UploadFileVo updateFileVo, @NonNull Integer imagePathType) {
        String md5Hex = DigestUtils.md5Hex(updateFileVo.getFile().getInputStream());
        ImagePath imagePath = ImagePath.valueOf(imagePathType);
        return GcpUtil.getPrefixFolder() + imagePath.getPath() + getFolderMd5Path(updateFileVo.getPltCode()) + generateFileName(md5Hex, updateFileVo.getFileSuffix());
    }

    @SneakyThrows
    @Override
    public String getVideoPath(@NonNull UploadFileVo updateFileVo, @NonNull Integer videoPathType) {
        String md5Hex = DigestUtils.md5Hex(updateFileVo.getFile().getInputStream());
        VideoPath videoPath = VideoPath.valueOf(videoPathType);
        return GcpUtil.getPrefixFolder() + videoPath.getPath() + getFolderMd5Path(updateFileVo.getPltCode()) + generateFileName(md5Hex, updateFileVo.getFileSuffix());
    }

    @Override
    public byte[] getDocument(String path) {
        String bucketName = GcpUtil.getBucketName();
        BlobId blobId = BlobId.of(bucketName, path);
        return storage.readAllBytes(blobId);

    }

    @Override
    public List<String> getDirectoryOfFileNames(String directory) {
        String bucketName = GcpUtil.getBucketName();
        Page<Blob> buckets = storage.list(bucketName, Storage.BlobListOption.currentDirectory(),
                Storage.BlobListOption.prefix(directory));
        Iterator<Blob> blobIterator = buckets.iterateAll().iterator();
        List<String> fileNames = new ArrayList<>();

        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            fileNames.add(blob.getName());
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    @Override
    public int deleteDirectory(String directory) {
        String bucketName = GcpUtil.getBucketName();
        Page<Blob> buckets = storage.list(bucketName, Storage.BlobListOption.currentDirectory(),
                Storage.BlobListOption.prefix(directory));
        Iterator<Blob> blobIterator = buckets.iterateAll().iterator();
        List<BlobId> blobIds = new ArrayList<>();
        while (blobIterator.hasNext()) {
            Blob blob = blobIterator.next();
            BlobId blobId = blob.getBlobId();
            blobIds.add(blobId);
        }

        return storage.delete(blobIds).size();
    }

    private String getFolderMd5Path(String pltCode) {
        return DigestUtils.md5Hex(GcpUtil.getBucketName() + pltCode).substring(0, 10) + "/";
    }

    private String generateFileName(String md5Hex, String fileSuffix) {
        return md5Hex.substring(0, 16) + "." + fileSuffix;
    }

}

```

```
package com.galaxy.service.impl;

import com.galaxy.enumeration.ReportDocumentType;
import com.galaxy.module.exception.BusinessException;
import com.galaxy.service.ReportDocumentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PDFDocumentServiceImpl implements ReportDocumentService {
    @Override
    public boolean accept(ReportDocumentType reportDocumentType) {
        return ReportDocumentType.PDF == reportDocumentType;
    }

    @Override
    public byte[] makeDocument(List<String> titles, List<List<?>> rows) throws BusinessException {
        throw new UnsupportedOperationException("目前不之支援產生pdf");
    }

    @Override
    public byte[] combineDocument(List<byte[]> documents) {
        throw new UnsupportedOperationException("目前不之支援產生pdf");
    }

}

```


```
package com.galaxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.galaxy.converter.ReportConverter;
import com.galaxy.enumeration.ReportStatus;
import com.galaxy.enumeration.ReportType;
import com.galaxy.model.vo.ReportDownloadRecordVo;
import com.galaxy.model.vo.ReportQryVo;
import com.galaxy.module.Sender;
import com.galaxy.module.rabbit.model.RabbitSendWith;
import com.galaxy.service.ReportDownloadRecordService;
import com.galaxy.storage.rdbms.entity.ReportDownloadRecordEntity;
import com.galaxy.storage.rdbms.mapper.ReportDownloadRecordMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.galaxy.mq.rabbit.config.ReportConfig.REPORT_TOPIC_EX;
import static com.galaxy.mq.rabbit.config.ReportConfig.REPORT_TOPIC_ROUTING_KEY_RULE;


@Service
public class ReportDownloadRecordServiceImpl implements ReportDownloadRecordService {
    private final ReportDownloadRecordMapper reportDownloadRecordMapper;

    private final Sender<RabbitSendWith> rabbitSender;

    public ReportDownloadRecordServiceImpl(ReportDownloadRecordMapper reportDownloadRecordMapper,
                                           @Qualifier("normalRabbitSender") Sender<RabbitSendWith> rabbitSender) {
        this.reportDownloadRecordMapper = reportDownloadRecordMapper;
        this.rabbitSender = rabbitSender;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createAndSendQry(ReportType reportType, String searchParamHash, Map<String, Object> searchParam, String account, Long accountId) {
        ReportDownloadRecordEntity entity = ReportDownloadRecordEntity.builder()
                .source(reportType.getDesc())
                .reportType(reportType)
                .status(ReportStatus.RUNNING)
                .searchParamHash(searchParamHash)
                .searchParam(searchParam)
                .creator(account)
                .creatorId(accountId)
                .build();
        reportDownloadRecordMapper.insertRecord(entity);

        String routingKey = String.format(REPORT_TOPIC_ROUTING_KEY_RULE, reportType.getType().toLowerCase());
        ReportQryVo qryVo = ReportConverter.INSTANCES.toQryVo(entity);
        rabbitSender.send(new RabbitSendWith(REPORT_TOPIC_EX, routingKey), qryVo);

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatusToFail(@NonNull Long id, @NonNull String errorMessage) {
        ReportDownloadRecordEntity vo = ReportDownloadRecordEntity.builder()
                .id(id)
                .status(ReportStatus.FAIL)
                .errorMessage(errorMessage)
                .build();
        reportDownloadRecordMapper.updateById(vo);
    }

    @Override
    public boolean isRunningStatus(@NonNull Long id) {
        LambdaQueryWrapper<ReportDownloadRecordEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .eq(ReportDownloadRecordEntity::getId, id)
                .eq(ReportDownloadRecordEntity::getStatus, ReportStatus.RUNNING);
        return reportDownloadRecordMapper.exists(queryWrapper);
    }

    @Override
    public boolean duplicateQry(String searchParamHash, Long accountId, Duration time) {
        OffsetDateTime queryTime = OffsetDateTime.now().minus(time);
        LambdaQueryWrapper<ReportDownloadRecordEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .eq(ReportDownloadRecordEntity::getCreatorId, accountId)
                .eq(ReportDownloadRecordEntity::getSearchParamHash, searchParamHash)
                .in(ReportDownloadRecordEntity::getStatus, List.of(ReportStatus.SUCCESS, ReportStatus.RUNNING))
                .gt(ReportDownloadRecordEntity::getCreateTime, queryTime);
        return reportDownloadRecordMapper.exists(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusFail(Long id) {
        reportDownloadRecordMapper.updateStatusFail(id, "make report timeout");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, String filePath, String errorMessage) {
        ReportStatus status = Objects.isNull(errorMessage) ? ReportStatus.SUCCESS : ReportStatus.FAIL;
        reportDownloadRecordMapper.updateRecord(id, filePath, status, errorMessage);
    }

    @Override
    public ReportDownloadRecordVo qryRecord(Long id, ReportType reportType) {
        LambdaQueryWrapper<ReportDownloadRecordEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .eq(ReportDownloadRecordEntity::getId, id)
                .eq(ReportDownloadRecordEntity::getReportType, reportType)
                .eq(ReportDownloadRecordEntity::getStatus, ReportStatus.SUCCESS);

        return ReportConverter.INSTANCES.toVo(reportDownloadRecordMapper.selectOne(queryWrapper));
    }

}

```


```
package com.galaxy.service;

import com.galaxy.enumeration.ReportDocumentType;
import com.galaxy.model.vo.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileManageService {

    void createBlob(String filePath, MultipartFile multipartFile);

    void createBlob(String path, byte[] content);

    String getDocumentPrefixPath(ReportDocumentType reportDocumentType);

    String getImagePath(UploadFileVo updateFileVo, Integer imagePathType);

    String getVideoPath(UploadFileVo updateFileVo, Integer videoPathType);

    byte[] getDocument(String filePath);

    List<String> getDirectoryOfFileNames(String directory);

    int deleteDirectory(String directory);
}

```

```
package com.galaxy.service;

import com.galaxy.enumeration.ReportDocumentType;
import com.galaxy.module.exception.BusinessException;

import java.util.List;

public interface ReportDocumentService {
    boolean accept(ReportDocumentType reportDocumentType);

    byte[] makeDocument(List<String> titles, List<List<?>> rows) throws BusinessException;

    byte[] combineDocument(List<byte[]> documents);
}

```


```


```


```
package com.galaxy.storage.rdbms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.galaxy.enumeration.ReportStatus;
import com.galaxy.enumeration.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "report_download_record", autoResultMap = true)
public class ReportDownloadRecordEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String source; // 报表来源,中文說明
    private ReportType reportType; // 報表名稱
    private String filePath; // 檔案路徑
    private ReportStatus status; // RUNNING:處理中 SUCCESS:成功 FAIL:失敗
    private String searchParamHash; // md5(searchParam)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> searchParam; // 查詢條件
    private String errorMessage; // 錯誤訊息
    private Long creatorId;
    private String creator;
    private OffsetDateTime createTime;

}

```

```
package com.galaxy.storage.rdbms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.galaxy.enumeration.ReportStatus;
import com.galaxy.storage.rdbms.entity.ReportDownloadRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ReportDownloadRecordMapper extends BaseMapper<ReportDownloadRecordEntity> {

    int insertRecord(ReportDownloadRecordEntity gameMerchantEntity);

    void updateStatusFail(@Param("id") Long id, @Param("errorMessage") String errorMessage);

    void updateRecord(@Param("id") Long id, @Param("filePath") String filePath, @Param("status") ReportStatus status, @Param("errorMessage") String errorMessage);
}

```

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.galaxy.storage.rdbms.mapper.ReportDownloadRecordMapper">

    <insert id="insertRecord" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO report_download_record (source, report_type, status, search_param_hash, search_param,
                                            error_message, creator_id, creator)
        VALUES (#{source}, #{reportType}, #{status}, #{searchParamHash},
                #{searchParam, typeHandler=com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler}::jsonb,
                #{errorMessage}, #{creatorId}, #{creator})
    </insert>

    <update id="updateStatusFail">
        update report_download_record
        set status        = 'FAIL',
            error_message = #{errorMessage}
        where id = #{id}
          and status = 'RUNNING'
    </update>

    <update id="updateRecord">
        update report_download_record
        set status        = #{status},
            file_path     = #{filePath},
            error_message = #{errorMessage}
        where id = #{id}
          and status = 'RUNNING'
    </update>
</mapper>
```



```
BEGIN;
DROP TABLE IF EXISTS plt_basics.vs_report_download_record;
CREATE TABLE IF NOT EXISTS plt_basics.vs_report_download_record
(
    id                INT4 GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    source            VARCHAR(64) NOT NULL,
    report_type       VARCHAR(64) NOT NULL,
    file_path         VARCHAR(256),
    status            VARCHAR(8)  NOT NULL     DEFAULT 'RUNNING',
    search_param_hash VARCHAR(32) NOT NULL,
    search_param      jsonb       NOT NULL     DEFAULT '{}',
    error_message     VARCHAR(256),
    creator_id        INT         NOT NULL,
    creator           VARCHAR(32) NOT NULL,
    create_time       TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- index by creator_id search_param_hash status
CREATE INDEX index_c_s ON plt_basics.vs_report_download_record USING btree (creator_id, search_param_hash);

COMMENT
    ON TABLE plt_basics.vs_report_download_record IS '報表下載紀錄';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.id IS 'ID';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.source IS '报表来源說明';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.report_type IS '報表名稱';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.file_path IS '檔案路徑';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.status IS '報表執行狀態:RUNNING:處理中,SUCCESS:成功,FAIL:失敗';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.search_param_hash IS 'md5(searchParam)';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.search_param IS '查詢條件:json';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.error_message IS '錯誤訊息';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.creator_id IS '創建者id';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.creator IS '創建者帳號';
COMMENT
    ON COLUMN plt_basics.vs_report_download_record.create_time IS '創建時間';

COMMIT;
```

