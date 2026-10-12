package com.example.saveapethub

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationTest {
    @Test fun validAmountIsAccepted() = assertTrue(Validation.isValidAmount("150"))
    @Test fun commaDecimalIsAccepted() = assertTrue(Validation.isValidAmount("99,50"))
    @Test fun zeroAmountIsRejected() = assertFalse(Validation.isValidAmount("0"))
    @Test fun negativeAmountIsRejected() = assertFalse(Validation.isValidAmount("-50"))
    @Test fun textAmountIsRejected() = assertFalse(Validation.isValidAmount("abc"))
    @Test fun hugeAmountIsRejected() = assertFalse(Validation.isValidAmount("2000000"))

    @Test fun matchingPasswordsAccepted() = assertTrue(Validation.isValidPassword("secret1", "secret1"))
    @Test fun shortPasswordRejected() = assertFalse(Validation.isValidPassword("abc", "abc"))
    @Test fun mismatchedPasswordsRejected() = assertFalse(Validation.isValidPassword("secret1", "secret2"))

    @Test fun validAgeAccepted() = assertTrue(Validation.isValidAge("5"))
    @Test fun negativeAgeRejected() = assertFalse(Validation.isValidAge("-3"))
    @Test fun textAgeRejected() = assertFalse(Validation.isValidAge("abc"))
    @Test fun ageTrimmed() = assertEquals(true, Validation.isValidAge(" 7 "))
}