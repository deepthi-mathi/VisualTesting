# Visual Testing POC

## Overview
This POC demonstrates a visual testing approach by comparing actual screenshots of an Android application against expected screenshots. It integrates with OpenAI's GPT-4 API for advanced image comparison and generates detailed error reports for any discrepancies.

## Constants Setup
The following constants are defined in the `Constants` class located at `VisualTesting/app/src/main/java/com/example/visualtesting/Constants.kt`:

const val API_KEY = "" - Please reachout to code owner for KEY

const val BASE_URL = "https://backbase-open-ai-chat-gpt4.openai.azure.com"

const val DEPLOYMENT_ID = "gpt-4o"

const val DEPLOYMENT_VERSION = "2024-02-01"


## Cloning the Project
1. Clone the project repository.
2. Navigate to the `VisualTesting/app` directory.

## Running the Tests
The instrumented tests are located at:
`VisualTesting/app/src/androidTest/java/com/example/visualtesting/tests/ExampleInstrumentedTest.kt`

To execute the tests:
- Open the project in Android Studio.
- Run the `ExampleInstrumentedTest` class.

## Test Results
The test results will be saved in the following locations:
- Actual Screenshots: `/storage/emulated/0/Documents/AIActualScreenShots/`
- AI Results: `/storage/emulated/0/Documents/AIResluts`

Expected screenshots should be placed in:
`VisualTesting/app/src/main/res/drawable`

## Image Comparison
The `assertCompareImages` method compares the expected and actual screenshots using the following prompt:

```text
Compare elements on two images for visual differences. Text on images, Layout and position of elements, colors of elements, texts and background should be the same. Ignore small noises, battery, system time display on top left, dark/light color differences and blur. First image named "Actual", second image "Expected". If you find any differences provide answer as numbered list and add to the end of the answer svg image to show differences on top of Expected image. Mark elements that disappear in red, new elements in green and elements that are on both images but changed in blue. In case of no differences answer "no differences".
```

The comparison process:
1. Connects to ChatGPT using a Volley client.
2. Converts images to base64 format.
3. Sends a JSON object request to the API.

## Example Tests
For POC purposes, three tests are provided in `ExampleInstrumentedTest` to observe failures.

## Error Reporting
The error report is generated in JSON format. Below is an example error report:

```json
[
  {
    "status": "Fail",
    "Test Name": "testImagesDoNotMatch",
    "error": "1. The text \"Question\" and \"Response\" in the \"Actual\" image do not appear in the \"Expected\" image.\n2. The \"Enter your query\" text box in the \"Actual\" image does not appear in the \"Expected\" image.\n3. The \"Expected\" image has a large bell icon with stars around it, which is not present in the \"Actual\" image.\n4. The \"Expected\" image contains a text box with the message \"Your personal information requires verification Please review and confirm your information by clicking on Proceed.\" which is not present in the \"Actual\" image.\n5. The \"Proceed\" button in the \"Expected\" image is not present in the \"Actual\" image.\n6. The \"Expected\" image has a background with a dark color and gradient design, whereas the \"Actual\" image has a plain gray background.\n\n\"svg\":\"<svg width=\\\"768\\\" height=\\\"1589\\\" xmlns=\\\"http://www.w3.org/2000/svg\\\">\\n <rect width=\\\"100%\\\" height=\\\"100%\\\" fill=\\\"#000\\\"/>\\n <rect x=\\\"20\\\" y=\\\"40\\\" width=\\\"728\\\" height=\\\"100\\\" fill=\\\"red\\\" fill-opacity=\\\"0.5\\\"/>\\n <rect x=\\\"20\\\" y=\\\"1320\\\" width=\\\"728\\\" height=\\\"100\\\" fill=\\\"red\\\" fill-opacity=\\\"0.5\\\"/>\\n <circle cx=\\\"384\\\" cy=\\\"300\\\" r=\\\"150\\\" fill=\\\"green\\\" fill-opacity=\\\"0.5\\\"/>\\n <rect x=\\\"20\\\" y=\\\"575\\\" width=\\\"728\\\" height=\\\"200\\\" fill=\\\"green\\\" fill-opacity=\\\"0.5\\\"/>\\n <rect x=\\\"20\\\" y=\\\"1420\\\" width=\\\"728\\\" height=\\\"100\\\" fill=\\\"green\\\" fill-opacity=\\\"0.5\\\"/>\\n</svg>\"\n"
  }
]
```

