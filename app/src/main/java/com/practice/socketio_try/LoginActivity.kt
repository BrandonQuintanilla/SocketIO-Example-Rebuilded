package com.practice.socketio_try

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }*/


    private var mUsernameView: EditText? = null
    private var mUsername: String? = null

    //    private var mSocket: Socket? = null
    lateinit var mSocket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val app = application as ChatApplication
        mSocket = app.getSocket()

        // Set up the login form.
        mUsernameView = findViewById<View>(R.id.username_input) as EditText
        mUsernameView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        val signInButton = findViewById<View>(R.id.sign_in_button) as Button
        signInButton.setOnClickListener { attemptLogin() }
        mSocket.on("login", onLogin)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.off("login", onLogin)
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        mUsernameView!!.error = null

        // Store values at the time of the login attempt.
        val username = mUsernameView!!.text.toString().trim { it <= ' ' }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            mUsernameView!!.error = getString(R.string.error_field_required)
            mUsernameView!!.requestFocus()
            return
        }
        mUsername = username

        // perform the user login attempt.
        Log.i("TAG_RESULT", "emit: mUsername: " + username);
        mSocket.emit("add user", username)
    }

    private val onLogin = Emitter.Listener { args ->
        val data = args[0] as JSONObject
        val numUsers: Int
        numUsers = try {
            data.getInt("numUsers")
        } catch (e: JSONException) {
            return@Listener
        }

        Log.i("TAG_RESULT", "call: mUsername: " + mUsername);
        Log.i("TAG_RESULT", "call: numUsers: " + numUsers);

        val intent = Intent()
        intent.putExtra("username", mUsername)
        intent.putExtra("numUsers", numUsers)
        setResult(RESULT_OK, intent)
        finish()
    }
}