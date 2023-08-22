package com.project.mobilemcm.util

import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun sanitizeSearchQuery(query: String): String {
    if (query.isEmpty()) {
        return ""
    }
    val listSt = query.split(" ")
    var resultString = ""
    listSt.forEach {
        resultString += "%$it%"
    }
    return resultString
}

fun showPlusDialog(
    good: GoodWithStock,
    context: Context,
    update: () -> Unit,
    addList: (GoodWithStock, Double) -> Unit
) {
    val nameText = EditText(context)
    nameText.inputType = InputType.TYPE_CLASS_NUMBER
    MaterialAlertDialogBuilder(context)
        .setTitle("Enter value")
        .setView(nameText)
        .setMessage("Enter value for add many position")
        .setNegativeButton("No") { _, _ ->
            // Respond to negative button press
        }
        .setPositiveButton("Yes") { _, _ ->
            val count = nameText.text.toString().toDoubleOrNull()
            count?.let {
                addList(good, it)
            }
            update.invoke()
        }
        .show()
}

fun showAlert(context: Context, title: String, message: String) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton("No") { _, _ ->
            // Respond to negative button press
        }
        .setPositiveButton("Yes") { _, _ ->
        }
        .show()
}

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .override(100, 100).centerInside()
        .placeholder(R.drawable.loading_animation)
        .error(R.drawable.ic_broken_image)
        .transform(FitCenter())
        .into(this)
}

fun currencyFormat(currency: Double): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "ru"))
    currencyFormat.maximumFractionDigits = 2
    currencyFormat.currency = Currency.getInstance("RUB")
    return currencyFormat.format(currency)
}