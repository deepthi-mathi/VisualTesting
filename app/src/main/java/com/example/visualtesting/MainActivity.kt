package com.example.visualtesting


import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.VolleyLog.TAG
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.visualtesting.Constants.API_KEY
import com.example.visualtesting.Constants.BASE_URL
import com.example.visualtesting.Constants.DEPLOYMENT_ID
import com.example.visualtesting.Constants.DEPLOYMENT_VERSION
import com.example.visualtesting.JsonObjectCreation.createJsonForChatGpt
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {

    // creating variables on below line.
    lateinit var responseTV: TextView
    lateinit var questionTV: TextView
    lateinit var queryEdt: TextInputEditText

    private val apiKey = API_KEY // AZURE_API_KEY
    private val endpoint = BASE_URL
    private val deploymentId = DEPLOYMENT_ID
    private val version = DEPLOYMENT_VERSION
    val imageConversions = ImageConversions()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //appContext = applicationContext
        // initializing variables on below line.
        responseTV = findViewById(R.id.idTVResponse)
        questionTV = findViewById(R.id.idTVQuestion)
        queryEdt = findViewById(R.id.idEdtQuery)

        // adding editor action listener for edit text on below line.
        queryEdt.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // setting response tv on below line.
                responseTV.text = "Please wait.."
                // validating text
                if (queryEdt.text.toString().length > 0) {
                    // calling get response to get the response.
                    getResponse(queryEdt.text.toString())
                } else {
                    Toast.makeText(this, "Please enter your query..", Toast.LENGTH_SHORT).show()
                }
                return@OnEditorActionListener true
            }
            false
        })

    }
    /*companion object {
        lateinit var appContext: Context
            private set
    }*/

    private fun getResponse(query: String) {
        // setting text on for question on below line.
        questionTV.text = "Comparing images......"
        queryEdt.setText("")
        // creating a queue for request queue.
        val queue: RequestQueue = Volley.newRequestQueue(applicationContext)


        val image64ExpectedBase64 =
            imageConversions.getBase64FromDrawable(R.drawable.expected, this.baseContext)
        val image64ActualBase64 =
            imageConversions.getBase64FromDrawable(R.drawable.expected, this.baseContext)
        /* val image64ActualBase64 =
             imageConversions.getBase64FromFilePath("/storage/emulated/0/Android/data/com.example.visualtesting/files/Pictures/TestResult_20241007_115837.png")
 */
        // creating a json object on below line.
        val jsonObject = createJsonForChatGpt(image64ActualBase64, image64ExpectedBase64)
        println("jsonObject========$jsonObject\n")
        // Construct the full API URL
        val fullurl =
            "$endpoint/openai/deployments/$deploymentId/chat/completions?api-version=$version"

        // on below line making json object request.
        val postRequest: JsonObjectRequest =
            // on below line making json object request.
            object : JsonObjectRequest(Method.POST, fullurl, jsonObject,
                Response.Listener { response ->
                    // on below line getting response message and setting it to text view.
                    val responseMsg: String =
                        response.getJSONArray("choices").getJSONObject(0).getJSONObject("message")
                            .getString("content")
                    responseTV.text = responseMsg
                },
                // adding on error listener
                Response.ErrorListener { error ->
                    Log.e("TAGAPI", "Error is : " + error.message + "\n" + error)
                    Log.d(TAG, "onErrorResponse: ERROR IS : >> " + error.stackTrace)
                    Log.d(TAG, "onErrorResponse: ERROR IS : >> " + error.localizedMessage)
                    Log.d(TAG, "onErrorResponse: ERROR IS : >> " + error)
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    // adding headers on below line.
                    params["Content-Type"] = "application/json"
                    params["api-key"] = apiKey
                    return params
                }
            }

        // on below line adding retry policy for our request.
        postRequest.setRetryPolicy(object : RetryPolicy {
            override fun getCurrentTimeout(): Int {
                return 50000
            }

            override fun getCurrentRetryCount(): Int {
                return 50000
            }

            @Throws(VolleyError::class)
            override fun retry(error: VolleyError) {
            }
        })
        // on below line adding our request to queue.
        queue.add(postRequest)
    }


}

