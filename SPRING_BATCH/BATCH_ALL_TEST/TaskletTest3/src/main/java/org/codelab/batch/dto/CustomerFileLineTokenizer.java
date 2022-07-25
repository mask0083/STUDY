package org.codelab.batch.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomerFileLineTokenizer implements LineTokenizer {
    private static final String DELIMITER = "|";
    private final FieldSetFactory fieldSetFactory;
    private final String[] names = new String[]{"firstName",
            "middleInitial",
            "lastName",
            "address",
            "city",
            "state",
            "zipCode"};
    /***
     * 구분자를 통해 여러 필드를 만든 후
     * 세 번째와 네 번째 필드를 묶어 단일 필드로 합친다.
     */
    @Override
    public FieldSet tokenize(String record) {
        String[] fields = record.split(DELIMITER);
        List<String> parsedFields = new ArrayList<>();
        for (int idx = 0; idx < fields.length; idx++) {
            if (idx == 4) {
                parsedFields.set(idx - 1, parsedFields.get(idx - 1) + " " + fields[idx]);
            } else {
                parsedFields.add(fields[idx]);
            }
        }
        return fieldSetFactory.create(parsedFields.toArray(new String[0]), names);
    }
}