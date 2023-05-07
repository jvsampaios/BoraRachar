package com.example.borarachar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    lateinit var qtdPessoas: EditText
    lateinit var qtdDinheiro: EditText
    lateinit var total: TextView
    var tts : TextToSpeech? = null
    lateinit var ttsButton: FloatingActionButton
    lateinit var shareButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        qtdPessoas = findViewById(R.id.pessoas)
        qtdDinheiro = findViewById(R.id.valorTotal)
        total = findViewById(R.id.valorResultado)
        ttsButton = findViewById(R.id.falar)
        shareButton = findViewById(R.id.compartilhar)

        qtdPessoas.addTextChangedListener(textWatcher)
        qtdDinheiro.addTextChangedListener(textWatcher)

        ttsButton.isEnabled = false;
        tts = TextToSpeech(this, this)
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val df = DecimalFormat("#,##0.00")
            df.roundingMode = RoundingMode.CEILING
            if(qtdPessoas.text.toString() != "" && qtdDinheiro.text.toString() != ""){
                val pessoas : Int = qtdPessoas.text.toString().toInt()
                val dinheiro : Float? = qtdDinheiro.text.toString().toFloatOrNull()


                if (dinheiro != null) {
                    val resultado : String? = df.format((dinheiro.div(pessoas)))
                    total.text = resultado
                }
            }else{
                total.text = df.format(0)
            }
            ttsButton.setOnClickListener { speakOut() }
            shareButton.setOnClickListener{ share() }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("pt", "BR")
            val result = tts?.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","Linguagem não suportada!")
            } else {
                ttsButton.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    private fun share() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "O valor a ser pago é: R$" + total.text.toString() + " por pessoa")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun speakOut() {
        tts?.setSpeechRate(0.8f)
        tts?.speak("O valor da conta deu", TextToSpeech.QUEUE_ADD, null, null)
        tts?.speak(total.text.toString(), TextToSpeech.QUEUE_FLUSH, null,"")
        tts?.speak("reais por pessoa", TextToSpeech.QUEUE_ADD, null, null)
    }

    public override fun onDestroy() {
        // Shutdown TTS
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}