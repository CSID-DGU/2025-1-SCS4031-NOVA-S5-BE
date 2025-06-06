package nova.backend.domain.cafe.dto.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import nova.backend.domain.cafe.dto.common.CafeBasicInfoDTO;
import nova.backend.domain.cafe.dto.common.CafeOpenHourDTO;
import nova.backend.domain.cafe.dto.common.CafeOperatingInfoDTO;
import nova.backend.domain.cafe.dto.common.StampBookDesignBasicDTO;
import nova.backend.domain.cafe.entity.Cafe;
import nova.backend.domain.cafe.entity.StampBookDesign;

import java.util.List;

public record CafeDesignOverviewDTO(
        @JsonUnwrapped
        CafeBasicInfoDTO basicInfo,
        @JsonUnwrapped
        StampBookDesignBasicDTO designInfo,
        List<CafeOpenHourDTO> openHours
) {
    public static CafeDesignOverviewDTO fromEntity(Cafe cafe, StampBookDesign design) {
        return new CafeDesignOverviewDTO(
                CafeBasicInfoDTO.from(cafe),
                StampBookDesignBasicDTO.from(design),
                CafeOperatingInfoDTO.openHoursFrom(cafe)
        );
    }
}
