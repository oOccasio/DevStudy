package chuchu.miniproject.dto.worker.response;

import java.util.List;

public record ResponseGetWorkList(

        List<ResponseListGetWorkList> detail,

        Long sum
) {
}
