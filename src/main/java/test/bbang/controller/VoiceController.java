package test.bbang.controller;


import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.protobuf.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.bbang.Dto.Voice.VoiceDto;
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
    public ResponseEntity<?> sendToServer(@RequestBody VoiceDto message) throws IOException {


        System.out.println(message.getText());

        List<String> list = new ArrayList<>();
        list.add(message.getText());

        Map<String, QueryResult> results = DetectIntentTexts
                .detectIntentTexts("firstagent-ndlg", list, "1", "ko");

        String sendMessage = "";
        for (String text : results.keySet()) {
            QueryResult queryResult = results.get(text);
            System.out.println("Fulfillment Text: " + queryResult.getFulfillmentText());


            if(queryResult.getIntent().getDisplayName().equals("purchase - check - chooseMenu")){
                Map<String, Value> parameters = queryResult.getParameters().getFieldsMap();

                String menu = "";
                String number = "";
                // 엔티티 이름을 키로 사용하여 값을 출력
                for (Map.Entry<String, Value> entry : parameters.entrySet()) {
//                    System.out.println("Entity Name: " + entry.getKey());
                    Value value = entry.getValue();

                    switch(entry.getKey()){
                        case "menu":
                            menu = value.getStringValue();
                            break;
                        case "number": // 개수가 없으면 0으로 나오고
                            if(value.getNumberValue()!=0.0) {
                                number = String.valueOf((int) value.getNumberValue());
                                break;
                            }
                        case "num":
                            if(!value.getStringValue().isEmpty()) {
                                number = value.getStringValue();
                                break;
                            }
                    }
                }

                if(number.equals("0") || number.isEmpty())
                    number = "1";

                if(queryResult.getFulfillmentText().equals("잘못 알아들었어요. 다시 말씀해주세요."))
                    sendMessage = queryResult.getFulfillmentText();
                else sendMessage = menu + " " + number + "개를 " + queryResult.getFulfillmentText();

                return ResponseEntity.ok().body(sendMessage);
            }

            return ResponseEntity.ok().body(queryResult.getFulfillmentText());
        }
        return ResponseEntity.badRequest().body("");
    }

    @GetMapping
    public ResponseEntity<?> sendToApp(String text){
        return ResponseEntity.ok().body(text);
    }


}
