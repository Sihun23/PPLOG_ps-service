package myaong.popolog.psservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PsEditResponse {
    private Long psId;
    private String originalContent;
    private String highlightedContent;
}
