package com.abahstudio.app.core.numbering;

import com.abahstudio.app.core.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "numbering_sequence")
@Getter
@Setter
public class NumberingSequence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence_code", unique = true, nullable = false, length = 50)
    private String sequenceCode;

    @Column(name = "prefix", length = 10)
    private String prefix;

    @Column(name = "pattern", nullable = false, length = 100)
    private String pattern; // Contoh: {prefix}{year}{month}{seq:05d}

    @Column(name = "last_number", nullable = false)
    private Long lastNumber = 0L;

    @Column(name = "last_reset_date")
    private LocalDate lastResetDate;

    @Column(name = "reset_type", length = 20)
    @Enumerated(EnumType.STRING)
    private ResetType resetType = ResetType.NONE; // YEARLY, MONTHLY, DAILY, NONE

    @Column(name = "seq_length", nullable = false)
    private Integer sequenceLength = 5;

    private String description;


}

