package com.team03.cocktailrecipesapp.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.team03.cocktailrecipesapp.MainActivity
import com.team03.cocktailrecipesapp.R
import com.team03.cocktailrecipesapp.RegisterActivity
import com.team03.cocktailrecipesapp.userLoggedIn
import java.util.*
import kotlin.math.log


//import com.team03.cocktailrecipesapp.userLoggedIn

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    lateinit var username :EditText
    lateinit var password :EditText
    lateinit var login :Button
    lateinit var register :Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        username = findViewById<EditText>(R.id.etUsername)
        password = findViewById<EditText>(R.id.etPassword)
        login = findViewById<Button>(R.id.btnLogin)
        register = findViewById<Button>(R.id.btnRegister)
        val language : String = getLanguage()
        setLocale(language)

        val loading = findViewById<ProgressBar>(R.id.loading)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
               password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    fun getLanguage() : String
    {
        val shared_lang: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language: String? = shared_lang.getString("Language", "en")
        if (language != null) {
            return language
        }
        return "en"
    }

    fun setLocale(lang: String) {
        val myLocale = Locale(lang)
        val res: Resources = resources
        val dm: DisplayMetrics = res.getDisplayMetrics()
        val conf: Configuration = res.getConfiguration()
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
        baseContext.resources.updateConfiguration(conf, baseContext.resources.displayMetrics)
        invalidateOptionsMenu()
        updateFields()
    }

    fun updateFields()
    {
        username.hint = resources.getString(R.string.prompt_username)
        password.hint = resources.getString(R.string.prompt_password)
        login.text = resources.getString(R.string.action_sign_in)
        register.text = resources.getString(R.string.register_button)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        recreate()
    }

    fun registerOnClickFromLogin(view: View){
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience


        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        //TODO: sharedPreferences -> userLoggedIn = true;
        userLoggedIn = true;
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    fun getCreateState() : Boolean
    {
        val shared_lang: SharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return shared_lang.getBoolean("init_login_language", false)
    }

    fun saveCreateState(state: Boolean)
    {
        val editor: SharedPreferences.Editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putBoolean("init_login_language", state).apply()
    }

}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}