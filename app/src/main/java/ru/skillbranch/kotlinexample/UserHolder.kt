package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                when {
                    map.containsKey("$email") ->  throw IllegalArgumentException("A user with this email already exists")
                    else -> map[user.login] = user
                }
            }
    }

    fun loginUser(login: String, password: String): String? {
        return map[login.trim().normalized()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        return User.makeUser(
            fullName,
            phone = rawPhone
        ).also { user ->
            require(!User.validatePhone(rawPhone)) { "Enter a valid phone number starting with a + and containing 11 digits" }
            when {
                map.containsKey("$rawPhone") -> throw IllegalArgumentException("A user with this phone already exists")
                else -> map[user.login] = user
            }
        }
    }

    fun requestAccessCode(login: String) {
        map[login.trim().normalized()].let { user: User? ->
            user?.setAccessCode()
        }
    }
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }
}

private fun String.normalized(): String {
    var result = this.trim().toLowerCase()
    if (this.startsWith("+")) {
        result = this.replace("""[^+\d]""".toRegex(), "")
    }
    return result
}

