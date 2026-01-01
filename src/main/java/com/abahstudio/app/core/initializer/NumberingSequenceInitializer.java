package com.abahstudio.app.core.initializer;

import com.abahstudio.app.core.numbering.NumberingSequence;
import com.abahstudio.app.core.numbering.ResetType;
import com.abahstudio.app.core.numbering.NumberingSequenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class NumberingSequenceInitializer implements ApplicationRunner {

    private final NumberingSequenceRepository numberingSequenceRepository;

    @Value("${company.code.prefix}")
    private String codePrefix;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing numbering sequences...");

        createIfNotExists(
                "COMPANY_BY_SYSTEM",
                codePrefix,
                "{prefix}{seq}",
                ResetType.NONE,
                5,
                "Company code"
        );
        createIfNotExists(
                "INVOICE_BY_SYSTEM",
                "INV",
                "{prefix}{year}{month}{seq}",
                ResetType.MONTHLY,
                5,
                "Invoice system"
        );
        createIfNotExists(
                "TICKET_BY_SYSTEM",
                "T",
                "{prefix}{year}{month}{seq}",
                ResetType.MONTHLY,
                6,
                "Ticket Help System"
        );

        log.info("Numbering sequences initialized successfully");
    }

    private void createIfNotExists(
            String sequenceCode,
            String prefix,
            String pattern,
            ResetType resetType,
            Integer sequenceLength,
            String description
    ) {
        numberingSequenceRepository.findBySequenceCode(sequenceCode)
                .ifPresentOrElse(
                        existing -> log.debug("Sequence already exists: {}", sequenceCode),
                        () -> {
                            NumberingSequence sequence = new NumberingSequence();
                            sequence.setSequenceCode(sequenceCode);
                            sequence.setPrefix(prefix);
                            sequence.setPattern(pattern);
                            sequence.setResetType(resetType);
                            sequence.setSequenceLength(sequenceLength);
                            sequence.setDescription(description);
                            sequence.setLastNumber(0L);
                            sequence.setLastResetDate(LocalDate.now());
                            sequence.setCreatedAt(LocalDateTime.now());
                            sequence.setUpdatedAt(LocalDateTime.now());

                            numberingSequenceRepository.save(sequence);
                            log.info("Created sequence: {} - {}", sequenceCode, description);
                        }
                );
    }
}