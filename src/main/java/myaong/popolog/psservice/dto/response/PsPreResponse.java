package myaong.popolog.psservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PsPreResponse {
	private Long psId;
	private String title;
	private String position;
	private String timestamp;
}
