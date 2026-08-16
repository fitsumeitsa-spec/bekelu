package com.example.util

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    AMHARIC("am", "አማርኛ")
}

enum class CalendarSystem(val displayNameEn: String, val displayNameAm: String) {
    GREGORIAN("Gregorian", "ጎርጎሪያን"),
    ETHIOPIAN("Ethiopian 🇪🇹", "የኢትዮጵያ 🇪🇹")
}

object Localization {
    fun getGreeting(hour: Int, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (hour) {
                in 5..11 -> "እንደምን አደርሽ 🌸"
                in 12..17 -> "እንደምን ዋልሽ 🌸"
                else -> "እንደምን አመሸሽ 🌸"
            }
        } else {
            when (hour) {
                in 5..11 -> "Good morning 🌸"
                in 12..17 -> "Good afternoon 🌸"
                else -> "Good evening 🌸"
            }
        }
    }

    fun getDailySubtitle(isAmharic: Boolean): String {
        return if (isAmharic) {
            "ለራስሽ ጥቂት ጊዜ ስጪ 🌸"
        } else {
            "Take a moment for yourself today."
        }
    }

    fun getNextPeriodTitle(isAmharic: Boolean): String {
        return if (isAmharic) "የሚቀጥለው የወር አበባሽ" else "Your next period"
    }

    fun getCycleDayText(currentDay: Int, totalDays: Int, isAmharic: Boolean): String {
        return if (isAmharic) "የዑደት ቀን $currentDay ከ $totalDays" else "Cycle day $currentDay of $totalDays"
    }

    fun getCycleDayLabel(isAmharic: Boolean): String {
        return if (isAmharic) "የዑደት ቀን" else "Cycle Day"
    }

    fun getInDaysText(days: Int, isAmharic: Boolean): String {
        return if (isAmharic) {
            when {
                days == 0 -> "ዛሬ ይጀምራል"
                days == 1 -> "ነገ ይጠበቃል"
                days > 1 -> "በ $days ቀናት ውስጥ"
                else -> "ከ ${-days} ቀናት በፊት ዘግይቷል"
            }
        } else {
            when {
                days == 0 -> "Starts today"
                days == 1 -> "Tomorrow"
                days > 1 -> "$days days"
                else -> "${-days} days late"
            }
        }
    }

    fun getExpectedDateText(dateStr: String, isAmharic: Boolean): String {
        return if (isAmharic) "የሚጠበቀው ቀን: $dateStr" else "Expected $dateStr"
    }

    fun getCycleRegularityText(regularity: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (regularity) {
                "Regular" -> "ዑደትሽ መደበኛ ይመስላል ✨"
                "Slightly Irregular" -> "ዑደትሽ መጠነኛ ልዩነት አለው 🌸"
                else -> "የዑደትሽን ሂደት እየተማርን ነው 🌱"
            }
        } else {
            when (regularity) {
                "Regular" -> "Your cycle looks regular ✨"
                "Slightly Irregular" -> "Your cycle varies slightly 🌸"
                else -> "Learning your cycle patterns 🌱"
            }
        }
    }

    fun getHowAreYouFeeling(isAmharic: Boolean): String {
        return if (isAmharic) "ዛሬ ምን ይሰማሻል?" else "How are you feeling today?"
    }

    fun getTodayCheckIn(isAmharic: Boolean): String {
        return if (isAmharic) "የዛሬ የጤና ማስታወሻ" else "Today's check-in"
    }

    fun getLogPeriodButton(isAmharic: Boolean): String {
        return if (isAmharic) "የወር አበባ መዝግቢ" else "Log Period"
    }

    fun getPeriodLoggedBanner(isAmharic: Boolean): String {
        return if (isAmharic) "ተመዝግቧል 🌸" else "Period Logged 🌸"
    }

    fun getCheckedInCelebration(isAmharic: Boolean): String {
        return if (isAmharic) "የዛሬው ተመዝግቧል 🌸" else "You're all checked in 🌸"
    }

    // Moods
    fun getMoodLabel(moodKey: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (moodKey) {
                "Happy" -> "ደስተኛ"
                "Calm" -> "መረጋጋት"
                "Loved" -> "ፍቅር"
                "Neutral" -> "መደበኛ"
                "Sad" -> "ሀዘን"
                "Stressed" -> "ጭንቀት"
                "Tired" -> "ድካም"
                else -> moodKey
            }
        } else {
            moodKey
        }
    }

    // Symptoms
    fun getSymptomLabel(symptomKey: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (symptomKey) {
                "Cramps" -> "የሆድ ቁርጠት"
                "Headache" -> "ራስ ምታት"
                "Bloating" -> "የሆድ መነፋት"
                "Fatigue" -> "ድካም"
                "Acne" -> "ብጉር"
                "Breast tenderness" -> "የጡት ህመም"
                "Nausea" -> "ማቅለሽለሽ"
                "Back pain" -> "የወገብ ህመም"
                "Cravings" -> "የምግብ ፍላጎት"
                "Mood swings" -> "ስሜት መለዋወጥ"
                else -> symptomKey
            }
        } else {
            symptomKey
        }
    }

    // Flow
    fun getFlowLabel(flowKey: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (flowKey) {
                "None" -> "ምንም"
                "Light" -> "ቀላል"
                "Medium" -> "መካከለኛ"
                "Heavy" -> "ከባድ"
                else -> flowKey
            }
        } else {
            flowKey
        }
    }

    // Energy
    fun getEnergyLabel(energyKey: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (energyKey) {
                "Low" -> "ዝቅተኛ"
                "Normal" -> "መደበኛ"
                "High" -> "ከፍተኛ"
                else -> energyKey
            }
        } else {
            energyKey
        }
    }

    // Phases
    fun getPhaseName(phase: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (phase) {
                "Menstrual" -> "የወር አበባ ደረጃ 🩸"
                "Follicular" -> "የፎሊኩላር ደረጃ 🌱"
                "Ovulation" -> "የእንቁላል መውጫ ደረጃ ✨"
                "Luteal" -> "የሉቲያል ደረጃ 🌙"
                else -> phase
            }
        } else {
            when (phase) {
                "Menstrual" -> "Menstrual Phase 🩸"
                "Follicular" -> "Follicular Phase 🌱"
                "Ovulation" -> "Ovulation Phase ✨"
                "Luteal" -> "Luteal Phase 🌙"
                else -> phase
            }
        }
    }

    fun getPhaseDescription(phase: String, isAmharic: Boolean): String {
        return if (isAmharic) {
            when (phase) {
                "Menstrual" -> "እረፍት ውሰጂ፣ ብዙ ውሀ ጠጪ እና ሞቅ ያለ መጠጦች ተጠቀሚ 🌸"
                "Follicular" -> "ጉልበትሽ እና ስሜትሽ እየጨመረ የሚመጣበት መልካም ጊዜ ነው ✨"
                "Ovulation" -> "የመራባት ዕድል ከፍተኛ የሆነበት እና ሃይል የሚሰማሽ ወቅት ነው 🌷"
                "Luteal" -> "የሰውነት ሙቀትሽ ሊጨምር ይችላል፤ ለራስሽ እረፍት እና እንክብካቤ ስጪ 🌙"
                else -> ""
            }
        } else {
            when (phase) {
                "Menstrual" -> "Rest, stay hydrated with warm teas, and take gentle care of your body 🌸"
                "Follicular" -> "Your energy and creativity are rising! Great time for new goals ✨"
                "Ovulation" -> "Peak fertility and glowing energy window. You feel vibrant 🌷"
                "Luteal" -> "Progesterone is high; prioritize restful sleep and self-care 🌙"
                else -> ""
            }
        }
    }
}
