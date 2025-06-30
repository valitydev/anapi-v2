package dev.vality.anapi.v2.security;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class AccessData {

    private final String operationId;
    private final String partyId;
    @Nullable
    private final List<String> shopIds;
    @Nullable
    private final String fileId;
    @Nullable
    private final String reportId;
    private final String realm;

}
