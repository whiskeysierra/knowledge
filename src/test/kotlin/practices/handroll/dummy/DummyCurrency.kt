package practices.handroll.dummy

//
data object DummyCurrency : Currency {
    override val code: String
        get() = throw UnsupportedOperationException("Dummy Currency should not be used.")

    override val symbol: String
        get() = throw UnsupportedOperationException("Dummy Currency should not be used.")

    override val displayName: String
        get() = throw UnsupportedOperationException("Dummy Currency should not be used.")

    override val defaultFractionDigits: Int
        get() = throw UnsupportedOperationException("Dummy Currency should not be used.")
}