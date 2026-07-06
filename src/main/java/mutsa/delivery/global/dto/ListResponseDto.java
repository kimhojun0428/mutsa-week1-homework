package mutsa.delivery.global.dto;

import java.util.List;

public record ListResponseDto<T> (
    int count,
    List<T> data
){
    public static <T> ListResponseDto<T> of(List<T> data){
        return new ListResponseDto<>(data.size(), data);
    }
}
