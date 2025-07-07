package eu.xap3y;

import eu.xap3y.enums.VisaResultType;

public record VisaResultRec(
    VisaResultType type,
    String message
) {
}
