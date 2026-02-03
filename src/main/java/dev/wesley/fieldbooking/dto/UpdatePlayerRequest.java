package dev.wesley.fieldbooking.dto;

import dev.wesley.fieldbooking.model.Enums.Position;
import dev.wesley.fieldbooking.model.Enums.SkillLevel;

import java.math.BigDecimal;

public record UpdatePlayerRequest(
        SkillLevel skillLevel,
        Position preferredPosition,
        BigDecimal rating,
        Integer goals,
        Integer assists,
        Integer math,
        Integer matchesPlayed
) {}


