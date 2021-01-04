package com.cbec.internal.api.messager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    private String destination;
    private String title;
    private String message;

    public List<String> parseDestination() {
        if (StringUtils.isEmpty(destination)){
            return Collections.emptyList();
        }
        return Arrays.stream(destination.split(";")).collect(Collectors.toList());
    }
}
