package com.github.kr328.clipboard

import android.app.Activity
import android.os.Bundle
import android.os.Parcel
import android.os.ServiceManager
import android.widget.TextView
import com.github.kr328.clipboard.shared.Constants
import com.github.kr328.clipboard.shared.IClipboardWhitelist
import java.io.IOException
import kotlin.concurrent.thread

class MainActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = TextView(this)

        setContentView(text)

        thread {
            try {
                val clipboard = ServiceManager.getService("clipboard")

                val data = Parcel.obtain()
                val reply = Parcel.obtain()

                val binder = try {
                    clipboard.transact(Constants.TRANSACT_CODE_GET_SERVICE, data, reply, 0)

                    if ( reply.dataSize() == 0 )
                        throw IOException("clipboard whitelist service not found")

                    reply.readStrongBinder()
                } finally {
                    data.recycle()
                    reply.recycle()
                }

                val service = IClipboardWhitelist.Stub.asInterface(binder)

                text.text = "loaded service ${service.version()}"
            } catch (e: Exception) {
                text.text = e.stackTraceToString()
            }
        }
    }
}