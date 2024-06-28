import android.app.ProgressDialog
import android.content.Context

object SpinnerLoader {
    private var progressDialog: ProgressDialog? = null

    fun show(context: Context, message: String = "Loading...") {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setMessage(message)
            progressDialog?.setCancelable(false)
            progressDialog?.show()
        }
    }

    fun hide() {
        progressDialog?.let {
            if (it.isShowing) {
                it.dismiss()
                progressDialog = null
            }
        }
    }
}
