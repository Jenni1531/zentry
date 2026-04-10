package zentry.back.api.global;

import zentry.back.api.ai.dtos.*;
import zentry.back.api.ai.models.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public final class mappers {
    private mappers() {}

//#region IA
public static iaModelsResponse toResponse(IaModels entity) {
    if (entity == null) return null;
    return iaModelsResponse.builder()
            .id(entity.getId())
            .nombre(entity.getNombre())
            .build();
}


}
