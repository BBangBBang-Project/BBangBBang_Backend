package test.bbang.controller;


import com.google.cloud.dialogflow.v2.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.VoiceDto;
import test.bbang.service.DetectIntentTexts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/voice")
@Slf4j
public class VoiceController {


    @PostMapping
    public void sendToServer(@RequestBody VoiceDto message) throws IOException {


        System.out.println(message.getText());

        List<String> list = new ArrayList<>();
        list.add(message.getText());

        Map<String, QueryResult> results = DetectIntentTexts
                .detectIntentTexts("firstagent-ndlg", list, "1", "ko");

        for (String text : results.keySet()) {
            QueryResult queryResult = results.get(text);
//            System.out.println(queryResult);
            System.out.println("Query Text: " + queryResult.getQueryText());
            System.out.println("Detected Intent: " + queryResult.getIntent().getDisplayName());
            System.out.println("Fulfillment Text: " + queryResult.getFulfillmentText());
        }

    }

    @GetMapping
    public ResponseEntity<?> sendToApp(String text){
        return ResponseEntity.ok().body(text);
    }


}
