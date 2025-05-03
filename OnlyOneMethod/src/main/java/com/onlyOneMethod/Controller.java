package com.onlyOneMethod;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;


@RestController
@RequestMapping("/file")
@Tag(name = "File Controller", description = "Обработка Excel файлов")
public class Controller {

    @PostMapping ("/process")
    @Operation(summary = "Вывод числа из exel таблицы", description = "выводит N-ое число из столбца exel таблицы")
    public Serializable returnValue(@RequestParam String filePath, @RequestParam int n) {
        // Read file
        OperationsWithExel operationsWithExel = new OperationsWithExel();
        int [] numbersArr = operationsWithExel.readNumbersFromExcel(filePath);

        // Sort numbers
        ArrayOperations arrayOperations = new ArrayOperations();
        numbersArr = arrayOperations.asyncQuickSort(numbersArr);

        //return number
        return arrayOperations.selectNValueArray(numbersArr, n);
        }
    }

