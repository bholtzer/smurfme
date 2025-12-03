package com.bih.applicationsmurfforyou.data.api

sealed class UserContent {


    data class OutputImageInstruction(
        val type: String = "output_image"
    ) : UserContent()



    data class InputText(
        val type: String = "input_text",
        val text: String
    ) : UserContent()

    data class InputImage(
        val type: String = "input_image",
        val image_url: String
    ) : UserContent()
}