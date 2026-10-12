package com.example.saveapethub

object Validation {
    fun isValidAmount(text: String): Boolean {
        val value = text.replace(',', '.').toDoubleOrNull() ?: return false
        return value > 0 && value <= 1_000_000
    }

    fun isValidPassword(password: String, confirm: String): Boolean =
        password.length >= 6 && password == confirm

    fun isValidAge(text: String): Boolean {
        val age = text.trim().toIntOrNull() ?: return false
        return age in 0..60
    }
}