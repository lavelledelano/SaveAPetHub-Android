package com.example.saveapethub

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object Supabase {
    val client = createSupabaseClient(
        supabaseUrl = "https://xvwcgwlinkzafqrhojmg.supabase.co",
        supabaseKey = "sb_publishable_ooVAcJ_XoBDUouDsS6pLBQ_ZWm13Vfi"
    ) {
        install(Postgrest)
        install(Auth) {
            scheme = "saph"
            host = "login"
        }
    }
}

