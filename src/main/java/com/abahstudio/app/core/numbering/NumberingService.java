package com.abahstudio.app.core.numbering;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@Transactional
public class NumberingService {

    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM");
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("dd");

    @Autowired
    private NumberingSequenceRepository numberingSequenceRepository;

    /**
     * Generate nomor dengan sequence code tertentu
     */
    public String generateNumber(String sequenceCode) {
        return generateNumber(sequenceCode, null);
    }

    /**
     * Generate nomor dengan sequence code dan parameter tambahan
     */
    public String generateNumber(String sequenceCode, Map<String, Object> params) {
        // Lock record untuk menghindari race condition
        NumberingSequence sequence = numberingSequenceRepository
                .findBySequenceCodeWithLock(sequenceCode)
                .orElseThrow(() -> new RuntimeException("Sequence not found: " + sequenceCode));

        // Reset sequence jika diperlukan
        resetIfRequired(sequence);

        // Increment nomor
        sequence.setLastNumber(sequence.getLastNumber() + 1);

        numberingSequenceRepository.save(sequence);

        // Format nomor sesuai pattern
        return formatNumber(sequence, params);
    }

    /**
     * Reset sequence berdasarkan reset type
     */
    private void resetIfRequired(NumberingSequence sequence) {
        LocalDate now = LocalDate.now();

        if (sequence.getLastResetDate() == null) {
            sequence.setLastResetDate(now);
            return;
        }

        switch (sequence.getResetType()) {
            case DAILY:
                if (!sequence.getLastResetDate().isEqual(now)) {
                    sequence.setLastNumber(0L);
                    sequence.setLastResetDate(now);
                }
                break;
            case MONTHLY:
                YearMonth currentMonth = YearMonth.from(now);
                YearMonth lastMonth = YearMonth.from(sequence.getLastResetDate());
                if (!currentMonth.equals(lastMonth)) {
                    sequence.setLastNumber(0L);
                    sequence.setLastResetDate(now);
                }
                break;
            case YEARLY:
                Year currentYear = Year.from(now);
                Year lastYear = Year.from(sequence.getLastResetDate());
                if (!currentYear.equals(lastYear)) {
                    sequence.setLastNumber(0L);
                    sequence.setLastResetDate(now);
                }
                break;
            case NONE:
                // Tidak direset
                break;
        }
    }

    /**
     * Format nomor sesuai pattern
     */
    private String formatNumber(NumberingSequence sequence, Map<String, Object> params) {
        String pattern = sequence.getPattern();
        LocalDate now = LocalDate.now();

        // Replace placeholder dengan nilai
        if (pattern.contains("{prefix}") && sequence.getPrefix() != null) {
            pattern = pattern.replace("{prefix}", sequence.getPrefix());
        }

        if (pattern.contains("{year}")) {
            pattern = pattern.replace("{year}", now.format(YEAR_FORMATTER));
        }

        if (pattern.contains("{month}")) {
            pattern = pattern.replace("{month}", now.format(MONTH_FORMATTER));
        }

        if (pattern.contains("{day}")) {
            pattern = pattern.replace("{day}", now.format(DAY_FORMATTER));
        }

        if (pattern.contains("{seq}")) {
            String seqFormat = "%0" + sequence.getSequenceLength() + "d";
            String sequenceNumber = String.format(seqFormat, sequence.getLastNumber());
            pattern = pattern.replace("{seq}", sequenceNumber);
        }

        // Replace custom parameters jika ada
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                if (pattern.contains(placeholder)) {
                    pattern = pattern.replace(placeholder, entry.getValue().toString());
                }
            }
        }

        return pattern;
    }


    @Transactional
    public NumberingSequence createDynamicSequence(
            String sequenceCode,
            String prefix,
            String pattern,
            ResetType resetType,
            String description) {

        // Cek apakah sudah ada
        if (numberingSequenceRepository.findBySequenceCode(sequenceCode).isPresent()) {
            throw new IllegalArgumentException("Sequence code already exists: " + sequenceCode);
        }

        NumberingSequence sequence = new NumberingSequence();
        sequence.setSequenceCode(sequenceCode);
        sequence.setPrefix(prefix);
        sequence.setPattern(pattern);
        sequence.setResetType(resetType);
        sequence.setSequenceLength(5); // default
        sequence.setDescription(description);
        sequence.setLastNumber(0L);

        return numberingSequenceRepository.save(sequence);
    }
}
