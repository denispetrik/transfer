package ru.yandex.money.transfer.transfer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.money.transfer.core.command.Response;
import ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferCommand;
import ru.yandex.money.transfer.transfer.command.prepare.PrepareTransferRequest;
import ru.yandex.money.transfer.transfer.command.prepare.TransferContract;
import ru.yandex.money.transfer.transfer.command.process.ProcessTransferCommand;
import ru.yandex.money.transfer.transfer.command.process.ProcessTransferRequest;
import ru.yandex.money.transfer.transfer.command.process.TransferResult;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author petrique
 */
@RestController
@RequestMapping(
        path = "/api/v1/transfers",
        produces = APPLICATION_JSON_VALUE
)
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    private final PrepareTransferCommand prepareTransferCommand;
    private final ProcessTransferCommand processTransferCommand;

    public TransferController(
            PrepareTransferCommand prepareTransferCommand,
            ProcessTransferCommand processTransferCommand
    ) {
        this.prepareTransferCommand = prepareTransferCommand;
        this.processTransferCommand = processTransferCommand;
    }

    @PostMapping(path = "/", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<TransferContract>> prepare(
            @Valid @RequestBody PrepareTransferRequest request
    ) {
        log.info("preparing transfer operation: request={}", request);
        return prepareTransferCommand.execute(request);
    }

    @PutMapping(path = "/{transferId}", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Response<TransferResult>> process(
            @PathVariable("transferId") String transferId
    ) {
        ProcessTransferRequest request = new ProcessTransferRequest(transferId);
        log.info("processing transfer operation: request={}", request);
        return processTransferCommand.execute(request);
    }
}
