import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import com.example.appbardemo.R

class ProgressDialog(context: Context) : androidx.appcompat.app.AlertDialog(context) {
    private val progressBar: ProgressBar
    private val messageTextView: TextView

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
        progressBar = view.findViewById(R.id.progressBar)
        messageTextView = view.findViewById(R.id.messageText)

        setView(view)
        setCancelable(false)
    }

    fun setMessage(message: String) {
        messageTextView.text = message
    }
}