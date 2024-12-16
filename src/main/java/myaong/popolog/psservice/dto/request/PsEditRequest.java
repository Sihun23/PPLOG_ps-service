package myaong.popolog.psservice.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PsEditRequest {
    private Long psId;
    private Long memberId;
    private String originalContent;
}
