package com.example.demo.controllers;

import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.service.ConsentAndDataTransferService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;

public class ConsentAndDataTransferController {
    ConsentAndDataTransferService consentAndDataTransferService;
    HashMap<String, SseEmitter> map = new HashMap<>();
    /*
    {
  "notification": {
    "consentDetail": {
      "consentId": "15675f69-a202-49e4-b93c-c547da127080",
      "createdAt": "2023-04-14T09:31:15.84929155",
      "purpose": {
        "text": "Self Requested",
        "code": "PATRQT",
        "refUri": null
      },
      "patient": {
        "id": "pallavjain@sbx"
      },
      "consentManager": {
        "id": "sbx"
      },
      "hip": {
        "id": "team-29-hip-1",
        "name": null
      },
      "hiTypes": [
        "DiagnosticReport",
        "Prescription",
        "ImmunizationRecord",
        "DischargeSummary",
        "OPConsultation",
        "HealthDocumentRecord",
        "WellnessRecord"
      ],
      "permission": {
        "accessMode": "VIEW",
        "dateRange": {
          "from": "2016-04-14T09:31:15.813225045",
          "to": "2023-04-14T09:31:15.813239214"
        },
        "dataEraseAt": "2023-06-14T09:31:15.667",
        "frequency": {
          "unit": "HOUR",
          "value": 1,
          "repeats": 0
        }
      },
      "careContexts": [
        {
          "patientReference": "PUID-00001",
          "careContextReference": "visit-af63a734-9779-4d6b-9185-818ee54032fa"
        }
      ]
    },
    "status": "GRANTED",
    "signature": "hvf5tJndzjCMVDfITCZE1MAk8NU59Gh9bSQIuBhPnVx0KKwVZ7f86fc5gA3ZjUohfHuQEzkwks/OIVERHH04JvwrR4PGFLuT4kXrqA2QFzXagVK9QfclJC+6Gh0t9001Atj7j1xJbKBEpOfrDXJAZyubSgx9M7OYx1/BEUs3MST48AwTtEltrdLJTjTY/k/nZfmUUhu71/V18H8PzakzyWn25II8N+igk0bhLaFKp7BEkKjEDywCpupZ32dJB9BN4DScgvHs4kmRQGRLTbFVYKvTdv3UUMKEuAIC76fPWhlpV3VPO60utoPLAllfTpRgLMnZuKANsnOwVAVC3pkfmA==",
    "consentId": "15675f69-a202-49e4-b93c-c547da127080",
    "grantAcknowledgement": false
  },
  "requestId": "921dc90c-de8c-4bcd-928a-8816528e7798",
  "timestamp": "2023-04-14T09:31:15.883786689"
}
     */

    /*
    {
  "notification": {
    "consentDetail": {
      "consentId": "6916bfa1-8a39-454f-9c11-df2b38d884c0",
      "createdAt": "2023-04-14T09:31:15.852859285",
      "purpose": {
        "text": "Self Requested",
        "code": "PATRQT",
        "refUri": null
      },
      "patient": {
        "id": "pallavjain@sbx"
      },
      "consentManager": {
        "id": "sbx"
      },
      "hip": {
        "id": "team-29-hip-1",
        "name": null
      },
      "hiTypes": [
        "DiagnosticReport",
        "Prescription",
        "ImmunizationRecord",
        "DischargeSummary",
        "OPConsultation",
        "HealthDocumentRecord",
        "WellnessRecord"
      ],
      "permission": {
        "accessMode": "VIEW",
        "dateRange": {
          "from": "2016-04-14T09:31:15.813241554",
          "to": "2023-04-14T09:31:15.81324187"
        },
        "dataEraseAt": "2023-06-14T09:31:15.667",
        "frequency": {
          "unit": "HOUR",
          "value": 1,
          "repeats": 0
        }
      },
      "careContexts": [
        {
          "patientReference": "PUID-00001",
          "careContextReference": "visit-1c2b61e9-3b55-4328-aaff-45dc4872ab71"
        }
      ]
    },
    "status": "GRANTED",
    "signature": "lgsQWHXAWahnDmolauzom7n+xdyRFJK27fbvAj91IgD4rMHFw7iVqUiTCoZPFTyqmM/87VkiJwGofMP6Xb1WB4Ni1jByHjJaOPAbq+xFViNwu3KuvLTpTc5/9XNDcwLaB5mesJ8+PFPBpH/pFcCBhzqDF0j6aGhQA6xleX7cftyGTm+naP3r4ed2IKUNfqQP5TxuqNIJy8gVnoAcaX9Jmj4aVj8MgOFxSnopkeaNPcn8WiHddNDlLcCdnxKWLFiwB9f83eOIkVkK8l4cbVyqEaQQS0mbGTIwGD8oLWl6aEXhUzoJBoX9Af/mdZNMnZAZjrRpnbBeXku+tKLHGoQ3zA==",
    "consentId": "6916bfa1-8a39-454f-9c11-df2b38d884c0",
    "grantAcknowledgement": false
  },
  "requestId": "b0f69d3c-d8c5-486d-8416-421394803423",
  "timestamp": "2023-04-14T09:31:15.884702225"
}
     */

    /*
    {
  "notification": {
    "consentDetail": {
      "consentId": "995d14fc-0e8d-4e0c-a957-63825782cdc3",
      "createdAt": "2023-04-14T09:31:15.856374669",
      "purpose": {
        "text": "Self Requested",
        "code": "PATRQT",
        "refUri": null
      },
      "patient": {
        "id": "pallavjain@sbx"
      },
      "consentManager": {
        "id": "sbx"
      },
      "hip": {
        "id": "team-29-hip-1",
        "name": null
      },
      "hiTypes": [
        "DiagnosticReport",
        "Prescription",
        "ImmunizationRecord",
        "DischargeSummary",
        "OPConsultation",
        "HealthDocumentRecord",
        "WellnessRecord"
      ],
      "permission": {
        "accessMode": "VIEW",
        "dateRange": {
          "from": "2016-04-14T09:31:15.813242045",
          "to": "2023-04-14T09:31:15.813242254"
        },
        "dataEraseAt": "2023-06-14T09:31:15.667",
        "frequency": {
          "unit": "HOUR",
          "value": 1,
          "repeats": 0
        }
      },
      "careContexts": [
        {
          "patientReference": "PUID-00001",
          "careContextReference": "visit-234"
        }
      ]
    },
    "status": "GRANTED",
    "signature": "l9eKn4BlQqw0kw/wPZv1zu5S+gORPeFOi0dYMmjaIYj0naP6aAhzIERv+sciakj4AqKt0T7noshGPg5s8/uQlV1UjzF7Gv5+ldIZ+iupgvk8aS6ctmXKbjqPVibggxeQuJnRBqDy2USf6Ihf8SA7pGweCaPPHg/dCCsdlaeBk7AXoX+dH2XY9oKjoa0v69yto5wc5UbOGaDj74ZMONrJjzVrsQG2JH+pe80EAzQTRBO1eFWxkQkG77tLI4ImUkPDP6I8qu8b0fXZf9EjsFDEZOFOCTYN2S5a61cZJezbydjB8m41KgM974NF+Rqj8Wd9argFhMTOiV4dRe58iz97rA==",
    "consentId": "995d14fc-0e8d-4e0c-a957-63825782cdc3",
    "grantAcknowledgement": false
  },
  "requestId": "c3c0bc38-3838-40c9-a142-e375e1eca6e4",
  "timestamp": "2023-04-14T09:31:15.883567206"
}
     */

    // Consent flow diagram
        // STEP 1: /consent-request/init -> ABDM

    /*
        RequestBody will have :
        {
            patientId:      "102",
            doctorId:       "1",
            purpose:        "care management",
            code:           "CAREMGT",
            hiTypes:        [
                                OPConsultation
                            ]
            accessMode:     "view",
            dateTimeFrom:   ________
            dateTimeTo:     ________
            dateEraseAt:    ________
        }
     */

    @PostMapping("/consent-request/init")
    @CrossOrigin
    public SseEmitter consentRequestInit(@RequestBody String req) {
        JSONObject tempConsentObject = consentAndDataTransferService.getConsentObject(req);
        JSONObject requestObject = consentAndDataTransferService.getConsentInitObject(tempConsentObject);

        if (!consentAndDataTransferService.saveConsentRequest(requestObject)) throw new RuntimeException();

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("consent-request init"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        map.put(requestObject.getString("requestId"), sseEmitter);
        return sseEmitter;

    }

    // STEP 2: ABDM -> /consent-request/on-init
    @PostMapping("/v0.5/consent-requests/on-init")
    @CrossOrigin
    public void onConsentRequestInit(@RequestBody String responseBody) {
        String[] response = consentAndDataTransferService.prepareOnConsentRequestInitResponse(responseBody);
        SseEmitter sseEmitter = map.get(response[0]);
        if (response[1] == null) {

        }
        else {
            consentAndDataTransferService.updateConsentRequestId(response[0], response[1]);
        }
        try {
            sseEmitter.send(SseEmitter.event().name("consent-request-on-init").data(response[2]));
        }
        catch (Exception e) {
            System.out.println(e);
            sseEmitter.complete();
            map.remove(response[0]);
        }
    }

    // STEP 3: ABDM -> user "Consent required"


    // STEP 4: USER -> ABDM "Grant consent"

    // STEP 5: ABDM -> v0.5/consents/hiu/notify

    /*
        {
          "timestamp": "2023-04-15T10:07:28.46882752",
          "requestId": "cf39fde8-2990-4c08-aa69-d396c884f64d",
          "notification": {
            "consentRequestId": "78f175b5-8dc0-4069-9678-00a668b0a95f",
            "status": "GRANTED",
            "consentArtefacts": [
              {
                "id": "ac4c2823-46d5-4ebd-8276-f90fe4bfc49a"
              }
            ]
          }
        }
     */
    @PostMapping("/v0.5/consents/hiu/notify")
    public void hiuConsentNotify(@RequestBody JSONObject requestBody) {
        consentAndDataTransferService.updateConsentStatus(requestBody);
    }

    @PostMapping("/v0.5/consents/hip/notify")
    public void hipConsentNotify(@RequestBody JSONObject requestBody) {
        Consent consent = consentAndDataTransferService.createConsent(requestBody);

    }

        /*
            {
              "timestamp": "2023-04-14T13:26:43.135066",
              "requestId": "6d3c7361-ea0f-43ab-af3a-270aeb6bf458",
              "notification": {
                "consentRequestId": "82caedec-d1cc-484a-b433-8ae236be7bd5",
                "status": "GRANTED",
                "consentArtefacts": [
                  {
                    "id": "2d17d862-af29-4b01-9cfc-375a16284aa0"
                  }
                ]
              }
            }
         */
    // STEP 6: /v0.5/consents/hiu/on-notify -> ABDM
    // This is to notify Hospital/Clinic/Doctor that consent is granted
    // To be used only when consent is revoked or paused.

        /*
            {
                "requestId": "{{$guid}}",
                "timestamp": "{{$isoTimestamp}}",
                "acknowledgement": [
                    {
                        "status": "OK",
                        "consentId": "82caedec-d1cc-484a-b433-8ae236be7bd5"
                    }
                ],
                "resp": {
                    "requestId": "6d3c7361-ea0f-43ab-af3a-270aeb6bf458"
                }
            }
         */

    // This is to notify backend system that consent is granted. Take these tokens and start fetching data.
    // STEP 7: ABDM -> /v0.5/consents/hip/notify

        /*
            {
              "notification": {
                "consentDetail": {
                  "consentId": "2d17d862-af29-4b01-9cfc-375a16284aa0",
                  "createdAt": "2023-04-14T13:26:43.112588567",
                  "purpose": {
                    "text": "string",
                    "code": "CAREMGT",
                    "refUri": null
                  },
                  "patient": {
                    "id": "pallavjain@sbx"
                  },
                  "consentManager": {
                    "id": "sbx"
                  },
                  "hip": {
                    "id": "team-29-hip-1",
                    "name": "HIP 1"
                  },
                  "hiTypes": [
                    "OPConsultation"
                  ],
                  "permission": {
                    "accessMode": "VIEW",
                    "dateRange": {
                      "from": "2022-09-25T12:52:34.925",
                      "to": "2022-11-15T12:52:34.925"
                    },
                    "dataEraseAt": "2023-05-25T12:52:34.925",
                    "frequency": {
                      "unit": "HOUR",
                      "value": 1,
                      "repeats": 0
                    }
                  },
                  "careContexts": [
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-af63a734-9779-4d6b-9185-818ee54032fa"
                    },
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-1c2b61e9-3b55-4328-aaff-45dc4872ab71"
                    },
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-234"
                    }
                  ]
                },
                "status": "GRANTED",
                "signature": "kGzcdr7+Ypo/WQOgTzh0mlqvEUu+f+eVrQBm44rxucNz3P+WbUaScCPhYJ1V1cX8tQ5ab10zN87ODG2lr8UEMx1Y3nlvepjMhPJfTsgJ5/mhWQiCi7Zz7anfXzvGpH5TVnzhoKfshxvHJizGLgvriJ2guG/0PGjQ7dN/hxaWM83AMP1kW1ViVEhC3SAFuymBF4/Qxo9q6e44j94Ye1joVmbGAsRoFMS3mbj219R1I/tKV5XYKXhzmJ6exioAiURUjg2Xxh/oVh6R/q3I+EsRnJ84j6//JkfHDwu7qpobqnwmPIm1UL/QP5VCyMSp6uNCmtP+o7EmXEzlPUgkNJfOnw==",
                "consentId": "2d17d862-af29-4b01-9cfc-375a16284aa0",
                "grantAcknowledgement": false
              },
              "requestId": "7a4e6ba1-9d6f-4883-9ef6-c197b4c9d681",
              "timestamp": "2023-04-14T13:26:43.134952673"
            }
         */

    // STEP 8 : /consents/fetch -> ABDM

        /*
            {
                "requestId": "{{$guid}}",
                "timestamp": "{{$isoTimestamp}}",
                "consentId": "2d17d862-af29-4b01-9cfc-375a16284aa0"
            }
         */
    // STEP 9 : ABDM -> /consents/on-fetch

        /*
            {
              "requestId": "eb93068b-d785-4115-ab28-7c5dad55ae7c",
              "timestamp": "2023-04-14T13:57:54.661286915",
              "consent": {
                "status": "GRANTED",
                "consentDetail": {
                  "schemaVersion": "v0.5",
                  "consentId": "2d17d862-af29-4b01-9cfc-375a16284aa0",
                  "createdAt": "2023-04-14T13:26:43.112588567",
                  "patient": {
                    "id": "pallavjain@sbx"
                  },
                  "careContexts": [
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-af63a734-9779-4d6b-9185-818ee54032fa"
                    },
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-1c2b61e9-3b55-4328-aaff-45dc4872ab71"
                    },
                    {
                      "patientReference": "PUID-00001",
                      "careContextReference": "visit-234"
                    }
                  ],
                  "purpose": {
                    "text": "string",
                    "code": "CAREMGT",
                    "refUri": null
                  },
                  "hip": {
                    "id": "team-29-hip-1",
                    "name": "HIP 1"
                  },
                  "hiu": {
                    "id": "team-29-hiu-1",
                    "name": null
                  },
                  "consentManager": {
                    "id": "sbx"
                  },
                  "requester": {
                    "name": "Dr. Saurabh Tripathi",
                    "identifier": {
                      "value": "MH1001",
                      "type": "REGNO",
                      "system": "https://www.mciindia.org"
                    }
                  },
                  "hiTypes": [
                    "OPConsultation"
                  ],
                  "permission": {
                    "accessMode": "VIEW",
                    "dateRange": {
                      "from": "2022-09-25T12:52:34.925",
                      "to": "2022-11-15T12:52:34.925"
                    },
                    "dataEraseAt": "2023-05-25T12:52:34.925",
                    "frequency": {
                      "unit": "HOUR",
                      "value": 1,
                      "repeats": 0
                    }
                  }
                },
                "signature": "O3rVFfQbeIzz/wGFdulFgg/jCjsyddwFy2ZqKt20oY+6oLjwWdI3liteEwNtLVzcVyl5ah3mEmGLBHdZS7dWLpj33fZeENcBuXYYIcA9/8jdHt/D6lphpJOl8+KKB2r6AAL6TMEZ4LmyhXZ0Kyo86brkrHJMh92zI+4EhGPn/WpcVj5himl/N/FADGX9TUR1PPdtTFaqY5rmgyobEGV0NQgAUPCMJiVWxg+fx/cgGgWVwcR7cKMGkzJKXp4HOHOOqxza3GLj2RoRp848A3ugsBm5j+azlR5UeFpJW7v96pj0mxhs55ubr9Thqb74TUuxjRmDFfgF1I9U3YaeVaAv3w=="
              },
              "error": null,
              "resp": {
                "requestId": "4e1925ce-dae9-4839-9c83-48bb7f6da8f5"
              }
            }
         */
}
